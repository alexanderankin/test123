/*
 * TagHighlightOptionPane.java - Tag highlight options panel
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class TagHighlightOptionPane extends AbstractOptionPane
{
	public TagHighlightOptionPane()
	{
		super("xml.tag-highlight");
	}

	// protected members
	protected void _init()
	{
		addComponent(tagHighlight = new JCheckBox(jEdit.getProperty(
			"options.xml.tag-highlight.enabled")));
		tagHighlight.setSelected(jEdit.getBooleanProperty(
			"xml.tag-highlight"));
		tagHighlight.addActionListener(new ActionHandler());

		addComponent(jEdit.getProperty("options.xml.tag-highlight.color"),
			tagHighlightColor = createColorButton(
			"xml.tag-highlight-color"));

		JLabel label = new JLabel(jEdit.getProperty("options.xml.tag-highlight.modes"));
		label.setBorder(new EmptyBorder(0,0,6,0));

		addComponent(label);

		Mode[] modeList = jEdit.getModes();
		JCheckBoxList.Entry[] listModel = new JCheckBoxList.Entry[modeList.length];
		for(int i = 0; i < modeList.length; i++)
		{
			listModel[i] = new JCheckBoxList.Entry(
				modeList[i].getBooleanProperty("xml.tag-highlight"),
				modeList[i].getName()
			);
		}

		modes = new JCheckBoxList(listModel);

		JScrollPane scroller = new JScrollPane(modes);
		scroller.setPreferredSize(new Dimension(150,150));

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = y++;
		cons.gridheight = cons.REMAINDER;
		cons.gridwidth = cons.REMAINDER;
		cons.fill = GridBagConstraints.VERTICAL;
		cons.anchor = GridBagConstraints.WEST;
		cons.weightx = cons.weighty = 1.0f;

		gridBag.setConstraints(scroller,cons);
		add(scroller);
	}

	protected void _save()
	{
		jEdit.setBooleanProperty("xml.tag-highlight",
			tagHighlight.isSelected());

		jEdit.setProperty("xml.tag-highlight-color",
			GUIUtilities.getColorHexString(
			tagHighlightColor.getBackground()));
		JCheckBoxList.Entry[] listModel = modes.getValues();
		for(int i = 0; i < listModel.length; i++)
		{
			JCheckBoxList.Entry entry = listModel[i];

			// we unset the property, instead of setting it to false,
			// to avoid cluttering the properties with dozens of
			// .xml.parse entries for modes we're not interested in
			String propName = "mode." + entry.getValue() + ".xml.tag-highlight";

			if(!entry.isChecked())
				jEdit.unsetProperty(propName);
			else
				jEdit.setBooleanProperty(propName,true);
		}
	}

	// private members
	private JCheckBox tagHighlight;
	private JButton tagHighlightColor;
	private JCheckBoxList modes;

	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ActionHandler());
		b.setRequestFocusEnabled(false);
		return b;
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == tagHighlight)
			{
				tagHighlightColor.setEnabled(tagHighlight.isSelected());
				modes.setEnabled(tagHighlight.isSelected());
			}
			else
			{
				Color c = JColorChooser.showDialog(
					TagHighlightOptionPane.this,
					jEdit.getProperty("colorChooser.title"),
					tagHighlightColor.getBackground());
				if(c != null)
					tagHighlightColor.setBackground(c);
			}
		}
	}
}
