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
		addComponent(autoParse = new JCheckBox(jEdit.getProperty(
			"options.xml.general.auto-parse")));
		autoParse.setSelected(jEdit.getBooleanProperty("buffer.xml.auto-parse"));
		autoParse.addActionListener(new ActionHandler());

		int delayValue;
		try
		{
			delayValue = Integer.parseInt(jEdit.getProperty("xml.delay"));
		}
		catch(NumberFormatException nf)
		{
			delayValue = 1500;
		}

		addComponent(jEdit.getProperty("options.xml.general.delay"),
			delay = new JSlider(500,3000,delayValue));
		Hashtable labelTable = new Hashtable();
		for(int i = 500; i <= 3000; i += 500)
		{
			labelTable.put(new Integer(i),new JLabel(
				String.valueOf((double)i / 500.0)));
		}
		delay.setLabelTable(labelTable);

		addComponent(showAttributes = new JCheckBox(jEdit.getProperty(
			"options.xml.general.show-attributes")));
		showAttributes.setSelected(jEdit.getBooleanProperty("xml.show-attributes"));

		addComponent(validate = new JCheckBox(jEdit.getProperty(
			"options.xml.general.validate")));
		validate.setSelected(jEdit.getBooleanProperty("buffer.xml.validate"));

		addComponent(new JLabel(jEdit.getProperty(
			"options.xml.general.modes")));

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
		cons.weightx = cons.weighty = 1.0f;

		gridBag.setConstraints(scroller,cons);
		add(scroller);
	}

	protected void _save()
	{
		jEdit.setBooleanProperty("buffer.xml.auto-parse",autoParse.isSelected());
		jEdit.setProperty("xml.delay",String.valueOf(delay.getValue()));
		jEdit.setBooleanProperty("xml.show-attributes",showAttributes.isSelected());
		jEdit.setBooleanProperty("buffer.xml.validate",validate.isSelected());

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
	private JCheckBox autoParse;
	private JSlider delay;
	private JCheckBox showAttributes;
	private JCheckBox validate;
	private JCheckBoxList modes;

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			delay.setEnabled(autoParse.isSelected());
		}
	}
}
