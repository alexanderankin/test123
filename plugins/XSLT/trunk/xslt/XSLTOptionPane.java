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
		
		addComponent(jEdit.getProperty("options.xslt.factory.label"), xsltFactory);
		
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


}
