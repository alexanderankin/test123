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
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
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
	private JTextArea verif;
	
	private static final String[] KNOWN_FACTORIES = 
	{
		"org.apache.xalan.processor.TransformerFactoryImpl", /* Xalan 2.7.1 */
		"com.icl.saxon.TransformerFactoryImpl",              /* Saxon 6.5.5 */
		"net.sf.saxon.TransformerFactoryImpl"                /* Saxon 9.2 */
	};
	
	//}}}	
	
	//{{{ XSLTOptionPane constructor
	public XSLTOptionPane()
	{
		super("xslt");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		String prefix = "xslt.factory.";
		factoryLabels = new HashMap<String,String>();
		Vector<String> factories = new Vector<String>(KNOWN_FACTORIES.length); 
		for (int i=0; i<KNOWN_FACTORIES.length; ++i) {
			String l = jEdit.getProperty(prefix + KNOWN_FACTORIES[i], KNOWN_FACTORIES[i]);
			factories.add(l);
			factoryLabels.put(l,KNOWN_FACTORIES[i]);
		}
		
		String currentFactory = jEdit.getProperty(XSLTUtilities.XSLT_FACTORY_PROP, KNOWN_FACTORIES[0]);
		String currentFactoryLabel = jEdit.getProperty(prefix+currentFactory,currentFactory);
		if(!factories.contains(currentFactoryLabel)){
			factories.add(currentFactoryLabel);
			factoryLabels.put(currentFactoryLabel,currentFactory);
		}
		
		xsltFactory = new JComboBox(new DefaultComboBoxModel(factories));
		xsltFactory.setEditable(true);
		xsltFactory.setName("factory");
		xsltFactory.setToolTipText("options.xslt.factory.tooltip");
		xsltFactory.setSelectedIndex(factories.indexOf(currentFactoryLabel));
		VerifyFactoryListener verifier = new VerifyFactoryListener();
		xsltFactory.addActionListener(verifier);
		((JTextComponent)xsltFactory.getEditor().getEditorComponent()).getDocument().addDocumentListener(verifier);
		addComponent(jEdit.getProperty("options.xslt.factory.label"), xsltFactory);
	
		verif = new JTextArea(5,80);
		verif.setName("factory-errors");
		verif.setEditable(false);
		verif.setBorder(BorderFactory.createEmptyBorder(20,0,20,20));
		verif.setBackground(xsltFactory.getParent().getBackground());
		verif.setForeground(java.awt.Color.RED);
		//verif.setVisible(false);
		addComponent(verif);
		verifier.verify((String)xsltFactory.getSelectedItem());
		
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
		String factoryLabel = (String)xsltFactory.getSelectedItem();
		if(factoryLabel!=null){
			if(factoryLabels.containsKey(factoryLabel)){
				factory = factoryLabels.get(factoryLabel);
			}else{
				factory = factoryLabel;
			}
			Log.log(Log.DEBUG,this,"setting factory to : "+factory);
			jEdit.setProperty(XSLTUtilities.XSLT_FACTORY_PROP,factory);
		}
		jEdit.setBooleanProperty(XSLTPlugin.COMPILE_ON_SAVE_PROP,compileOnSave.isSelected());
	} //}}}

	
	//{{{ VerifyFactoryListener class
	private class VerifyFactoryListener implements DocumentListener, ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			verify((String)xsltFactory.getSelectedItem());
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
			if(factoryLabels.containsKey(factoryClass))
			{
				factoryClass = factoryLabels.get(factoryClass);
			}
			
			try
			{
				Class.forName(factoryClass).asSubclass(SAXTransformerFactory.class);
				verif.setVisible(false);
			}
			catch(ClassNotFoundException e)
			{
				String jars = new File(jEdit.getSettingsDirectory(),"jars").getPath();
				verif.setText(jEdit.getProperty("options.xslt.factory.error-not-found",new Object[]{jars}));
				verif.setVisible(true);
			}
			catch(ClassCastException e)
			{
				verif.setText(jEdit.getProperty("options.xslt.factory.error-class-cast"));
				verif.setVisible(true);
			}
		}
	}
	//}}}
}
