/*
 * TaskListModesOptionPane.java - TaskList plugin
 * Copyright (C) 2002 Oliver Rutherfurd
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
 *
 * $Id$
 */

package tasklist.options;

//{{{ imports
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.JCheckBoxList;
import tasklist.*;
//}}}

public class TaskListModesOptionPane extends AbstractOptionPane
{
	//{{{ constructor
	public TaskListModesOptionPane()
	{
		super("tasklist.modes");
	}//}}}

	//{{{ _init()
	protected void _init()
	{
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		addComponent(new JLabel(jEdit.getProperty(
			"options.tasklist.modes.parse.label")));
		addComponent(Box.createVerticalStrut(6));

		Mode[] modes = jEdit.getModes();
		JCheckBoxList.Entry[] entries = new JCheckBoxList.Entry[modes.length];

		for(int i=0; i < modes.length; i++)
		{
			String mode = modes[i].getName();
			boolean parse = jEdit.getBooleanProperty(
				"options.tasklist.parse." + mode,true);
			entries[i] = new JCheckBoxList.Entry(parse,mode);
		}

		this.modes = new JCheckBoxList(entries);
		addComponent(new JScrollPane(this.modes));

	}//}}}

	//{{{ save()
	public void _save()
	{
		JCheckBoxList.Entry[] entries = this.modes.getValues();
		for(int i=0; i < entries.length; i++)
		{
			String mode = entries[i].getValue().toString();
			jEdit.setBooleanProperty(
				"options.tasklist.parse." + mode,
				entries[i].isChecked());
		}
	}//}}}

	private JCheckBoxList modes;
}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
