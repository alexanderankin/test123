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
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Hashtable;
import org.gjt.sp.jedit.gui.JCheckBoxList;
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
		addComponent(bufferChangeParse = new JCheckBox(jEdit.getProperty(
			"options.xml.general.buffer-change-parse")));
		bufferChangeParse.setSelected(jEdit.getBooleanProperty(
			"buffer.xml.buffer-change-parse"));
		bufferChangeParse.addActionListener(new ActionHandler());

		addComponent(keystrokeParse = new JCheckBox(jEdit.getProperty(
			"options.xml.general.keystroke-parse")));
		keystrokeParse.setSelected(jEdit.getBooleanProperty(
			"buffer.xml.keystroke-parse"));
		keystrokeParse.addActionListener(new ActionHandler());

		int delayValue;
		try
		{
			delayValue = Integer.parseInt(jEdit.getProperty("xml.auto-parse-delay"));
		}
		catch(NumberFormatException nf)
		{
			delayValue = 1500;
		}

		addComponent(jEdit.getProperty("options.xml.general.auto-parse-delay"),
			autoParseDelay = new JSlider(500,3000,delayValue));
		Hashtable labelTable = new Hashtable();
		for(int i = 500; i <= 3000; i += 500)
		{
			labelTable.put(new Integer(i),new JLabel(
				String.valueOf((double)i / 1000.0)));
		}
		autoParseDelay.setLabelTable(labelTable);
		autoParseDelay.setPaintLabels(true);
		autoParseDelay.setMajorTickSpacing(500);
		autoParseDelay.setPaintTicks(true);

		autoParseDelay.setEnabled(keystrokeParse.isSelected());

		addComponent(validate = new JCheckBox(jEdit.getProperty(
			"options.xml.general.validate")));
		validate.setSelected(jEdit.getBooleanProperty("xml.validate"));

		addComponent(networkOK = new JCheckBox(jEdit.getProperty(
			"options.xml.general.network-ok")));
		networkOK.setSelected(jEdit.getBooleanProperty("xml.network-ok"));

		addComponent(tagHighlight = new JCheckBox(jEdit.getProperty(
			"options.xml.general.tag-highlight-enabled")));
		tagHighlight.setSelected(jEdit.getBooleanProperty(
			"xml.tag-highlight"));
		tagHighlight.addActionListener(new ActionHandler());

		addComponent(jEdit.getProperty("options.xml.general.tag-highlight-color"),
			tagHighlightColor = createColorButton(
			"xml.tag-highlight-color"));
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

		
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setBooleanProperty("buffer.xml.buffer-change-parse",
			bufferChangeParse.isSelected());
		jEdit.setBooleanProperty("buffer.xml.keystroke-parse",
			keystrokeParse.isSelected());
		jEdit.setProperty("xml.auto-parse-delay",String.valueOf(
			autoParseDelay.getValue()));
		jEdit.setIntegerProperty("xml.show-attributes",
			showAttributes.getSelectedIndex());
		jEdit.setBooleanProperty("xml.validate",validate.isSelected());
		jEdit.setBooleanProperty("xml.network-ok",networkOK.isSelected());
		jEdit.setBooleanProperty("xml.tag-highlight",
			tagHighlight.isSelected());
		jEdit.setProperty("xml.tag-highlight-color",
			GUIUtilities.getColorHexString(
			tagHighlightColor.getBackground()));
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JCheckBox bufferChangeParse;
	private JCheckBox keystrokeParse;
	private JSlider autoParseDelay;
	private JCheckBox validate;
	private JCheckBox networkOK;
	private JCheckBox tagHighlight;
	private JButton tagHighlightColor;
	private JComboBox showAttributes;
	//}}}

	//{{{ createColorButton() method
	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ActionHandler());
		b.setRequestFocusEnabled(false);
		return b;
	} //}}}

	//}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == keystrokeParse)
			{
				autoParseDelay.setEnabled(keystrokeParse.isSelected());
				if(keystrokeParse.isSelected())
					bufferChangeParse.setSelected(true);
			}
			else if(evt.getSource() == tagHighlight)
				tagHighlightColor.setEnabled(tagHighlight.isSelected());
			else if(evt.getSource() == tagHighlightColor)
			{
				Color c = JColorChooser.showDialog(
					GeneralOptionPane.this,
					jEdit.getProperty("colorChooser.title"),
					tagHighlightColor.getBackground());
				if(c != null)
					tagHighlightColor.setBackground(c);
			}
		}
	} //}}}
}
