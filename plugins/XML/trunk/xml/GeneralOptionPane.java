/*
 * GeneralOptionPane.java - XML general options panel
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
import java.util.Hashtable;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class GeneralOptionPane extends AbstractOptionPane
{
	public GeneralOptionPane()
	{
		super("xml.general");
	}

	// protected members
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

		addComponent(showAttributes = new JCheckBox(jEdit.getProperty(
			"options.xml.general.show-attributes")));
		showAttributes.setSelected(jEdit.getBooleanProperty("xml.show-attributes"));

		addComponent(validate = new JCheckBox(jEdit.getProperty(
			"options.xml.general.validate")));
		validate.setSelected(jEdit.getBooleanProperty("xml.validate"));

		addComponent(complete = new JCheckBox(jEdit.getProperty(
			"options.xml.general.complete")));
		complete.setSelected(jEdit.getBooleanProperty("xml.complete"));
		complete.addActionListener(new ActionHandler());

		try
		{
			delayValue = Integer.parseInt(jEdit.getProperty("xml.complete-delay"));
		}
		catch(NumberFormatException nf)
		{
			delayValue = 500;
		}

		addComponent(jEdit.getProperty("options.xml.general.complete-delay"),
			completeDelay = new JSlider(0,1500,delayValue));
		for(int i = 0; i <= 1500; i += 250)
		{
			labelTable.put(new Integer(i),new JLabel(
				String.valueOf((double)i / 1000.0)));
		}
		completeDelay.setLabelTable(labelTable);
		completeDelay.setPaintLabels(true);
		completeDelay.setMajorTickSpacing(250);
		completeDelay.setPaintTicks(true);

		completeDelay.setEnabled(complete.isSelected());

		JLabel label = new JLabel(jEdit.getProperty("options.xml.general.modes"));
		label.setBorder(new EmptyBorder(0,0,6,0));

		addComponent(label);

		Mode[] modeList = jEdit.getModes();
		JCheckBoxList.Entry[] listModel = new JCheckBoxList.Entry[modeList.length];
		for(int i = 0; i < modeList.length; i++)
		{
			listModel[i] = new JCheckBoxList.Entry(
				modeList[i].getBooleanProperty("xml.parse"),
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
		jEdit.setBooleanProperty("buffer.xml.buffer-change-parse",
			bufferChangeParse.isSelected());
		jEdit.setBooleanProperty("buffer.xml.keystroke-parse",
			keystrokeParse.isSelected());
		jEdit.setProperty("xml.auto-parse-delay",String.valueOf(
			autoParseDelay.getValue()));
		jEdit.setBooleanProperty("xml.show-attributes",showAttributes.isSelected());
		jEdit.setBooleanProperty("xml.validate",validate.isSelected());
		jEdit.setBooleanProperty("xml.complete",complete.isSelected());
		jEdit.setProperty("xml.complete-delay",String.valueOf(
			completeDelay.getValue()));

		JCheckBoxList.Entry[] listModel = modes.getValues();
		for(int i = 0; i < listModel.length; i++)
		{
			JCheckBoxList.Entry entry = listModel[i];

			// we unset the property, instead of setting it to false,
			// to avoid cluttering the properties with dozens of
			// .xml.parse entries for modes we're not interested in
			String propName = "mode." + entry.getValue() + ".xml.parse";

			if(!entry.isChecked())
				jEdit.unsetProperty(propName);
			else
				jEdit.setBooleanProperty(propName,true);
		}
	}

	// private members
	private JCheckBox bufferChangeParse;
	private JCheckBox keystrokeParse;
	private JSlider autoParseDelay;
	private JCheckBox showAttributes;
	private JCheckBox validate;
	private JCheckBox complete;
	private JSlider completeDelay;
	private JCheckBoxList modes;

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			autoParseDelay.setEnabled(keystrokeParse.isSelected());
			completeDelay.setEnabled(complete.isSelected());
			if(keystrokeParse.isSelected())
				bufferChangeParse.setSelected(true);
		}
	}
}
