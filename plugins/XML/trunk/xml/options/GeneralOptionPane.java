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
import javax.swing.*;
import org.gjt.sp.jedit.*;

import xml.Resolver;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{
	//{{{ Private members

	private JCheckBox local;
	private JCheckBox ask;
	private JCheckBox cache;
	
	private JCheckBox popupEditorComplete;
	private JCheckBox validate;
	private JComboBox showAttributes;
	private JCheckBox closeCompleteOpen;
	private JCheckBox closeComplete;
	private JCheckBox standaloneExtraSpace;
	//}}}	
	
	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane()
	{
		super("xml.general");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		
		addComponent(popupEditorComplete = new JCheckBox(jEdit.getProperty(
			"options.xml.general.tageditor.popupOnCompletion")));
		popupEditorComplete.setSelected(jEdit.getBooleanProperty("xml.tageditor.popupOnComplete", true));
		
		addComponent(validate = new JCheckBox(jEdit.getProperty(
			"options.xml.general.validate")));
		validate.setSelected(jEdit.getBooleanProperty("buffer.xml.validate"));
		
		String prefix = Resolver.NETWORK_PROPS;
		local = new JCheckBox (jEdit.getProperty("options." + Resolver.LOCAL));
		local.setSelected(Resolver.isLocal());
		addComponent(local);
		
		cache = new JCheckBox (jEdit.getProperty("options." + Resolver.CACHE));
		cache.setSelected(Resolver.isUsingCache());
		addComponent(cache);
		
		ask = new JCheckBox (jEdit.getProperty("options." + Resolver.ASK));
		ask.setSelected(Resolver.isAskBeforeDownloading());
		addComponent(ask);
		
		String[] showAttributeValues = {
			jEdit.getProperty("options.xml.general.show-attributes.none"),
			jEdit.getProperty("options.xml.general.show-attributes.id-only"),
			jEdit.getProperty("options.xml.general.show-attributes.all")
		};

		addComponent(jEdit.getProperty("options.xml.general.show-attributes"),
			showAttributes = new JComboBox(showAttributeValues));
		showAttributes.setSelectedIndex(jEdit.getIntegerProperty(
			"xml.show-attributes",0));

		addComponent(closeComplete = new JCheckBox(jEdit.getProperty(
			"options.xml.general.close-complete")));
		closeComplete.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete"));

		addComponent(closeCompleteOpen = new JCheckBox(jEdit.getProperty(
			"options.xml.general.close-complete-open")));
		closeCompleteOpen.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete-open"));

		addComponent(standaloneExtraSpace = new JCheckBox(jEdit.getProperty(
			"options.xml.general.standalone-extra-space")));
		standaloneExtraSpace.setSelected(jEdit.getBooleanProperty(
			"xml.standalone-extra-space"));
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setBooleanProperty("xml.tageditor.popupOnComplete", popupEditorComplete.isSelected());
		
		jEdit.setBooleanProperty("buffer.xml.validate",validate.isSelected());
		Resolver.setLocal(local.isSelected());
		Resolver.setAskBeforeDownloading(ask.isSelected());
		Resolver.setUsingCache(cache.isSelected());

		jEdit.setIntegerProperty("xml.show-attributes",
			showAttributes.getSelectedIndex());
		jEdit.setBooleanProperty("xml.close-complete",
			closeComplete.isSelected());
		jEdit.setBooleanProperty("xml.close-complete-open",
			closeCompleteOpen.isSelected());
		jEdit.setBooleanProperty("xml.standalone-extra-space",
			standaloneExtraSpace.isSelected());
	} //}}}


}
