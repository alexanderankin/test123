/*
 * GeneralOptionPane.java - XML general options panel
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
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
		validate.setSelected(jEdit.getBooleanProperty("xml.validate"));

		tagHighlight = new JCheckBox(jEdit.getProperty(
			"options.xml.general.tag-highlight-enabled"));
		tagHighlight.setSelected(jEdit.getBooleanProperty(
			"xml.tag-highlight"));
		tagHighlight.addActionListener(new ActionHandler());

		tagHighlightColor = new ColorWellButton(
			jEdit.getColorProperty("xml.tag-highlight-color"));

		addComponent(tagHighlight,tagHighlightColor,
			GridBagConstraints.VERTICAL);

		tagHighlightColor.setEnabled(tagHighlight.isSelected());

		String[] values = {
			jEdit.getProperty("options.xml.general.show-attributes.none"),
			jEdit.getProperty("options.xml.general.show-attributes.id-only"),
			jEdit.getProperty("options.xml.general.show-attributes.all")
		};

		addComponent(jEdit.getProperty("options.xml.general.show-attributes"),
			showAttributes = new JComboBox(values));
		showAttributes.setSelectedIndex(jEdit.getIntegerProperty(
			"xml.show-attributes",0));

		addComponent(closeComplete = new JCheckBox(jEdit.getProperty(
			"options.xml.general.close-complete")));
		closeComplete.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete"));
		closeComplete.addActionListener(new ActionHandler());

		addComponent(closeCompleteOpen = new JCheckBox(jEdit.getProperty(
			"options.xml.general.close-complete-open")));
		closeCompleteOpen.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete-open"));
		closeCompleteOpen.addActionListener(new ActionHandler());
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setIntegerProperty("xml.show-attributes",
			showAttributes.getSelectedIndex());
		jEdit.setBooleanProperty("xml.validate",validate.isSelected());
		jEdit.setBooleanProperty("xml.tag-highlight",
			tagHighlight.isSelected());
		jEdit.setProperty("xml.tag-highlight-color",
			GUIUtilities.getColorHexString(
			tagHighlightColor.getSelectedColor()));
		jEdit.setBooleanProperty("xml.close-complete",
			closeComplete.isSelected());
		jEdit.setBooleanProperty("xml.close-complete-open",
			closeCompleteOpen.isSelected());
	} //}}}

	//{{{ Private members
	private JCheckBox bufferChangeParse;
	private JCheckBox keystrokeParse;
	private JSlider autoParseDelay;
	private JCheckBox validate;
	private JCheckBox tagHighlight;
	private ColorWellButton tagHighlightColor;
	private JComboBox showAttributes;
	private JCheckBox closeCompleteOpen;
	private JCheckBox closeComplete;
	//}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == keystrokeParse)
			{
				autoParseDelay.setEnabled(keystrokeParse.isSelected());
				if(keystrokeParse.isSelected())
					bufferChangeParse.setSelected(true);
			}
			else if(source == tagHighlight)
				tagHighlightColor.setEnabled(tagHighlight.isSelected());
		}
	} //}}}
}
