/*
 * GeneralOptionPane.java - XML general options panel
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.options;

//{{{ Imports
import java.util.Arrays;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import xml.Resolver;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{
	//{{{ Private members

	private JComboBox network;

	private JCheckBox cache;
	
	private JCheckBox popupEditorComplete;
	private JCheckBox validate;
	private JCheckBox enableSchemaMapping;
	private JComboBox showAttributes;
	private JCheckBox closeCompleteOpen;
	private JCheckBox closeComplete;
	private JCheckBox standaloneExtraSpace;
	
	private JCheckBox xinclude;
	private JCheckBox xincludeBaseURI;
	
	static String[] comboLabels;
	
	//}}}	
	
	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane()
	{
		super("xml.general");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		
		addSeparator("options.xml.general.validate-separator");
		
		addComponent(validate = new JCheckBox(jEdit.getProperty(
			"options.xml.general.validate")));
		validate.setSelected(jEdit.getBooleanProperty("buffer.xml.validate"));
		validate.setName("validate");
		
		String prefix = "options." + Resolver.NETWORK_PROPS + ".";
		String[] comboLabels = new String[Resolver.MODES.length];
		for (int i=0; i<comboLabels.length; ++i) {
			comboLabels[i] = jEdit.getProperty(prefix + Resolver.MODES[i]);
		}
		
		network = new JComboBox(comboLabels);
		network.setSelectedIndex(Arrays.asList(Resolver.MODES).indexOf(Resolver.getNetworkMode()));
		network.setName("network");
		
		addComponent(jEdit.getProperty("options.xml.general.network-mode"), network);
		
		cache = new JCheckBox (jEdit.getProperty("options." + Resolver.CACHE));
		cache.setSelected(Resolver.isUsingCache());
		cache.setName("cache");
		addComponent(cache);
		
		addComponent(enableSchemaMapping = new JCheckBox(jEdit.getProperty(
			"options.xml.general.enable-schema-mapping")));
		enableSchemaMapping.setSelected(jEdit.getBooleanProperty(xml.SchemaMappingManager.ENABLE_SCHEMA_MAPPING_PROP));
		enableSchemaMapping.setName("enable-schema-mapping");
		enableSchemaMapping.setToolTipText(jEdit.getProperty(
				"options.xml.general.enable-schema-mapping.tooltip" ));
		
		String[] showAttributeValues = {
			jEdit.getProperty("options.xml.general.show-attributes.none"),
			jEdit.getProperty("options.xml.general.show-attributes.id-only"),
			jEdit.getProperty("options.xml.general.show-attributes.all")
		};

		addSeparator("options.xml.general.tree-separator");

		addComponent(jEdit.getProperty("options.xml.general.show-attributes"),
			showAttributes = new JComboBox(showAttributeValues));
		showAttributes.setSelectedIndex(jEdit.getIntegerProperty(
			"xml.show-attributes",0));
		showAttributes.setName("showAttributes");

		addSeparator("options.xml.general.tags-separator");

		closeComplete = new JCheckBox(jEdit.getProperty(
			"options.xml.general.close-complete"));
		closeComplete.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete"));
		closeComplete.setToolTipText(jEdit.getProperty(
				"options.xml.general.close-complete.tooltip" ));
		closeComplete.setName("closeComplete");
		addComponent(closeComplete);

		closeCompleteOpen = new JCheckBox(jEdit.getProperty(
			"options.xml.general.close-complete-open"));
		closeCompleteOpen.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete-open"));
		closeCompleteOpen.setName("closeCompleteOpen");
		addComponent(closeCompleteOpen);
		
		addComponent(standaloneExtraSpace = new JCheckBox(jEdit.getProperty(
			"options.xml.general.standalone-extra-space")));
		standaloneExtraSpace.setSelected(jEdit.getBooleanProperty(
			"xml.standalone-extra-space"));
		standaloneExtraSpace.setName("standaloneExtraSpace");

		addComponent(popupEditorComplete = new JCheckBox(jEdit.getProperty(
			"options.xml.general.tageditor.popupOnCompletion")));
		popupEditorComplete.setSelected(jEdit.getBooleanProperty("xml.tageditor.popupOnComplete", true));
		popupEditorComplete.setName("popupEditorComplete");
		
		addSeparator("options.xml.general.xinclude-separator");
		
		addComponent(xinclude = new JCheckBox(jEdit.getProperty(
			"options.xml.general.xinclude")));
		xinclude.setSelected(jEdit.getBooleanProperty(
			"buffer.xml.xinclude"));
		xinclude.setName("xinclude");
		//force sensible default
		xinclude.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(xinclude.isSelected())xincludeBaseURI.setSelected(true);
			}
		});
		addComponent(xincludeBaseURI = new JCheckBox(jEdit.getProperty(
			"options.xml.general.xinclude-xmlbase")));
		xincludeBaseURI.setSelected(jEdit.getBooleanProperty(
			"buffer.xml.xinclude.fixup-base-uris"));
		xincludeBaseURI.setToolTipText(jEdit.getProperty(
			"options.xml.general.xinclude-xmlbase.tooltip"));
		xincludeBaseURI.setName("xincludeBaseURI");
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setBooleanProperty("xml.tageditor.popupOnComplete", popupEditorComplete.isSelected());
		
		jEdit.setBooleanProperty("buffer.xml.validate",validate.isSelected());
		Resolver.setNetworkMode(Resolver.MODES[network.getSelectedIndex()]);
		Resolver.setUsingCache(cache.isSelected());
		jEdit.setBooleanProperty(xml.SchemaMappingManager.ENABLE_SCHEMA_MAPPING_PROP,
			enableSchemaMapping.isSelected());
		jEdit.setIntegerProperty("xml.show-attributes",
			showAttributes.getSelectedIndex());
		jEdit.setBooleanProperty("xml.close-complete",
			closeComplete.isSelected());
		jEdit.setBooleanProperty("xml.close-complete-open",
			closeCompleteOpen.isSelected());
		/* If we want XML close completion, we need to also enable the
		   SideKick option to close immediately when possible.  */
		if (closeComplete.isSelected()) {
			jEdit.setBooleanProperty("sidekick.complete-instant.toggle", true);
		}
		jEdit.setBooleanProperty("xml.standalone-extra-space",
			standaloneExtraSpace.isSelected());
		jEdit.setBooleanProperty("buffer.xml.xinclude",xinclude.isSelected());
		jEdit.setBooleanProperty("buffer.xml.xinclude.fixup-base-uris",xincludeBaseURI.isSelected());
	} //}}}


}
