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
import java.awt.event.*;
import java.awt.*;
import java.util.Hashtable;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{
	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane()
	{
		super("xml.general");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		addComponent(validate = new JCheckBox(jEdit.getProperty(
			"options.xml.general.validate")));
		validate.setSelected(jEdit.getBooleanProperty("buffer.xml.validate"));

		String[] networkValues = {
			jEdit.getProperty("options.xml.general.network-off"),
			jEdit.getProperty("options.xml.general.network-cache"),
			jEdit.getProperty("options.xml.general.network-always")
		};
		addComponent(jEdit.getProperty("options.xml.general.network-mode"),
			network = new JComboBox(networkValues));
		if(jEdit.getBooleanProperty("xml.network"))
		{
			if(jEdit.getBooleanProperty("xml.cache"))
				network.setSelectedIndex(1);
			else
				network.setSelectedIndex(2);
		}
		else
			network.setSelectedIndex(0);

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
		jEdit.setBooleanProperty("buffer.xml.validate",validate.isSelected());
		jEdit.setBooleanProperty("xml.cache",network.getSelectedIndex() == 1);
		jEdit.setBooleanProperty("xml.network",network.getSelectedIndex() >= 1);
		jEdit.setIntegerProperty("xml.show-attributes",
			showAttributes.getSelectedIndex());
		jEdit.setBooleanProperty("xml.close-complete",
			closeComplete.isSelected());
		jEdit.setBooleanProperty("xml.close-complete-open",
			closeCompleteOpen.isSelected());
		jEdit.setBooleanProperty("xml.standalone-extra-space",
			standaloneExtraSpace.isSelected());
	} //}}}

	//{{{ Private members
	private JCheckBox validate;
	private JComboBox network;
	private JComboBox showAttributes;
	private JCheckBox closeCompleteOpen;
	private JCheckBox closeComplete;
	private JCheckBox standaloneExtraSpace;
	//}}}
}
