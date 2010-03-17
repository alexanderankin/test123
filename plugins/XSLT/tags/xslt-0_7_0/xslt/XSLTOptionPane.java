/*
 * XSLTOptionPane.java - XSLT general options panel
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Eric Le Lay
 *
 * The XSLT plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 2.0, for example used by the Xalan package."
 */

package xslt;

//{{{ Imports
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import javax.xml.transform.sax.SAXTransformerFactory;

import java.io.File;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
//}}}

public class XSLTOptionPane extends AbstractOptionPane
{
	//{{{ Private members

	private JCheckBox compileOnSave;
	private JComboBox xsltFactory;
	private Map<String,String> factoryLabels;
	private JComboBox xpathAdapter;
	
	//}}}	
	
	//{{{ XSLTOptionPane constructor
	public XSLTOptionPane()
	{
		super("xslt");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		// factory
		factoryLabels = new HashMap<String,String>();
		List<String> factories = PropertyUtil.getEnumeratedProperty("xslt.factory.class"); 
		List<String> factoryLabelList = PropertyUtil.getEnumeratedProperty("xslt.factory.label"); 
		for (int i=0; i<factories.size() && i<factoryLabelList.size(); ++i) {
			factoryLabels.put(factoryLabelList.get(i),factories.get(i));
		}
		
		String currentFactory = jEdit.getProperty(XSLTUtilities.XSLT_FACTORY_PROP, factories.get(0));
		String currentFactoryLabel;
		if(factories.contains(currentFactory)){
			currentFactoryLabel = factoryLabelList.get(factories.indexOf(currentFactory)); 
		}else{
			currentFactoryLabel = currentFactory;
		}
		
		if(!factoryLabelList.contains(currentFactoryLabel)){
			factoryLabelList.add(currentFactoryLabel);
			factoryLabels.put(currentFactoryLabel,currentFactory);
		}
		
		xsltFactory = new JComboBox(factoryLabelList.toArray(new String[factoryLabelList.size()]));
		xsltFactory.setEditable(true);
		xsltFactory.setName("factory");
		xsltFactory.setToolTipText(jEdit.getProperty("options.xslt.factory.tooltip"));
		xsltFactory.setSelectedIndex(factoryLabelList.indexOf(currentFactoryLabel));
		addComponent(jEdit.getProperty("options.xslt.factory.label"), xsltFactory);
		
		VerifyFactoryListener verifier = new VerifyFactoryListener(factoryLabels
			,SAXTransformerFactory.class
			,"xslt.factory"
			,xsltFactory);
		
		xsltFactory.addActionListener(verifier);
		((JTextComponent)xsltFactory.getEditor().getEditorComponent()).getDocument().addDocumentListener(verifier);
	
		// message area
		addComponent(verifier);
		verifier.verify((String)xsltFactory.getSelectedItem());
		
		// XPath engines
		String currentAdapter = jEdit.getProperty(XPathTool.XPATH_ADAPTER_PROP);
		List<String> adaptersList = PropertyUtil.getEnumeratedProperty("xpath.adapter.class");
		List<String> adapterLabelsList = PropertyUtil.getEnumeratedProperty("xpath.adapter.label");
		xpathAdapter = new JComboBox(adapterLabelsList.toArray(new String[]{}));
		xpathAdapter.setEditable(false);
		xpathAdapter.setName("xpath-adapter");
		xpathAdapter.setToolTipText(jEdit.getProperty("options.xpath.adapter.tooltip"));
		xpathAdapter.setSelectedIndex(adaptersList.indexOf(currentAdapter));
		addComponent(jEdit.getProperty("options.xpath.adapter.label"), xpathAdapter);
		
		// compile on save
		compileOnSave = new JCheckBox(jEdit.getProperty("options.xslt.compile-on-save.label"));
		compileOnSave.setName("compile-on-save");
		compileOnSave.setToolTipText("options.xslt.compile-on-save.tooltip");
		compileOnSave.setSelected(jEdit.getBooleanProperty(XSLTPlugin.COMPILE_ON_SAVE_PROP));
		
		addComponent(compileOnSave);
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		String factory = null;
		String factoryLabel = ((String)xsltFactory.getSelectedItem()).trim();
		if(factoryLabel!=null){
			if(factoryLabels.containsKey(factoryLabel)){
				factory = factoryLabels.get(factoryLabel);
			}else{
				factory = factoryLabel;
			}
			Log.log(Log.DEBUG,this,"setting factory to : "+factory);
			jEdit.setProperty(XSLTUtilities.XSLT_FACTORY_PROP,factory);
		}

		String adapterClass = jEdit.getProperty("xpath.adapter.class."+xpathAdapter.getSelectedIndex());
		Log.log(Log.DEBUG,this,"setting XPathAdapter to : "+adapterClass);
		jEdit.setProperty(XPathTool.XPATH_ADAPTER_PROP,adapterClass);
		
		jEdit.setBooleanProperty(XSLTPlugin.COMPILE_ON_SAVE_PROP,compileOnSave.isSelected());
	} //}}}

	
	//{{{ VerifyFactoryListener class
	private class VerifyFactoryListener extends JTextArea implements DocumentListener, ActionListener
	{
		private Map<String,String>labelToClass;
		private Class wantedClass;
		private String prefix;
		private JComboBox target;
		
		VerifyFactoryListener(Map<String,String> labelToClass, Class wantedClass, String prefix, JComboBox target)
		{
			super();
			this.labelToClass = labelToClass;
			this.wantedClass = wantedClass;
			this.prefix = prefix;
			this.target = target;
			
			setEditable(false);
			setBorder(BorderFactory.createEmptyBorder(20,0,20,20));
			setBackground(target.getParent().getBackground());
			setForeground(java.awt.Color.RED);
			setVisible(false);
			setName(prefix+"-errors");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			verify((String)target.getSelectedItem());
		}
		
		public void changedUpdate(DocumentEvent e){}
		
		public void insertUpdate(DocumentEvent e){
			try
			{
				verify(e.getDocument().getText(0,e.getDocument().getLength()));
			}
			catch(BadLocationException ble)
			{
				Log.log(Log.ERROR,this,ble);
			}
		}
		public void removeUpdate(DocumentEvent e){
			try
			{
				verify(e.getDocument().getText(0,e.getDocument().getLength()));
			}
			catch(BadLocationException ble)
			{
				Log.log(Log.ERROR,this,ble);
			}
		}
		
		private void verify(String factoryClass)
		{
			if(labelToClass.containsKey(factoryClass))
			{
				factoryClass = labelToClass.get(factoryClass);
			}
			
			try
			{
				Class.forName(factoryClass).asSubclass(wantedClass);
				setVisible(false);
			}
			catch(ClassNotFoundException e)
			{
				String jars = new File(jEdit.getSettingsDirectory(),"jars").getPath();
				setText(jEdit.getProperty("options."+prefix+".error-not-found",new Object[]{jars}));
				setVisible(true);
			}
			catch(ClassCastException e)
			{
				setText(jEdit.getProperty("options."+prefix+".error-class-cast"));
				setVisible(true);
			}
		}
	}
	//}}}
}
