/*
 * SideKickOptionPane.java - SideKick options panel
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package sidekick;

//{{{ Imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Hashtable;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
//}}}

public class SideKickOptionPane extends AbstractOptionPane
{
	//{{{ GeneralOptionPane constructor
	public SideKickOptionPane()
	{
		super("sidekick");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		addComponent(bufferChangeParse = new JCheckBox(jEdit.getProperty(
			"options.sidekick.buffer-change-parse")));
		bufferChangeParse.setSelected(jEdit.getBooleanProperty(
			"buffer.sidekick.buffer-change-parse"));
		bufferChangeParse.addActionListener(new ActionHandler());

		addComponent(keystrokeParse = new JCheckBox(jEdit.getProperty(
			"options.sidekick.keystroke-parse")));
		keystrokeParse.setSelected(jEdit.getBooleanProperty(
			"buffer.sidekick.keystroke-parse"));
		keystrokeParse.addActionListener(new ActionHandler());

		int delayValue;
		try
		{
			delayValue = Integer.parseInt(jEdit.getProperty("sidekick.auto-parse-delay"));
		}
		catch(NumberFormatException nf)
		{
			delayValue = 1500;
		}

		addComponent(jEdit.getProperty("options.sidekick.auto-parse-delay"),
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

		addComponent(treeFollowsCaret = new JCheckBox(jEdit.getProperty(
			"options.sidekick.tree-follows-caret")));
		treeFollowsCaret.setSelected(jEdit.getBooleanProperty(
			"sidekick-tree.follows-caret"));
		treeFollowsCaret.addActionListener(new ActionHandler());
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setBooleanProperty("buffer.sidekick.buffer-change-parse",
			bufferChangeParse.isSelected());
		jEdit.setBooleanProperty("buffer.sidekick.keystroke-parse",
			keystrokeParse.isSelected());
		jEdit.setProperty("sidekick.auto-parse-delay",String.valueOf(
			autoParseDelay.getValue()));
		jEdit.setBooleanProperty("sidekick-tree.follows-caret",
			treeFollowsCaret.isSelected());
	} //}}}

	//{{{ Private members
	private JCheckBox bufferChangeParse;
	private JCheckBox keystrokeParse;
	private JSlider autoParseDelay;
	private JCheckBox treeFollowsCaret;
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
		}
	} //}}}
}
