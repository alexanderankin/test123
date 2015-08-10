/*
 * CompletionOptionPane.java - XML completion options panel
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

public class CompletionOptionPane extends AbstractOptionPane
{
	public CompletionOptionPane()
	{
		super("xml.complete");
	}

	// protected members
	protected void _init()
	{
		addComponent(complete = new JCheckBox(jEdit.getProperty(
			"options.xml.complete.complete")));
		complete.setSelected(jEdit.getBooleanProperty("xml.complete"));
		complete.addActionListener(new ActionHandler());

		int delayValue;
		try
		{
			delayValue = Integer.parseInt(jEdit.getProperty("xml.complete-delay"));
		}
		catch(NumberFormatException nf)
		{
			delayValue = 500;
		}

		addComponent(jEdit.getProperty("options.xml.complete.complete-delay"),
			completeDelay = new JSlider(0,1500,delayValue));

		Hashtable labelTable = new Hashtable();
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

		addComponent(closeComplete = new JCheckBox(jEdit.getProperty(
			"options.xml.complete.close-complete")));
		closeComplete.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete"));
		closeComplete.addActionListener(new ActionHandler());

		addComponent(closeCompleteOpen = new JCheckBox(jEdit.getProperty(
			"options.xml.complete.close-complete-open")));
		closeCompleteOpen.setSelected(jEdit.getBooleanProperty(
			"xml.close-complete-open"));
		closeCompleteOpen.addActionListener(new ActionHandler());
	}

	protected void _save()
	{
		jEdit.setBooleanProperty("xml.complete",complete.isSelected());
		jEdit.setProperty("xml.complete-delay",String.valueOf(
			completeDelay.getValue()));
		jEdit.setBooleanProperty("xml.close-complete",
			closeComplete.isSelected());
		jEdit.setBooleanProperty("xml.close-complete-open",
			closeCompleteOpen.isSelected());
	}

	// private members
	private JCheckBox complete;
	private JSlider completeDelay;
	private JCheckBox closeCompleteOpen;
	private JCheckBox closeComplete;

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			completeDelay.setEnabled(complete.isSelected());
		}
	}
}
