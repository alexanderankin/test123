/*
 * TaskListPopup.java - provides popup actions for TaskList plugin
 * Copyright (c) 2001 John Gellene
 * jgellene@nyc.rr.com
 * http://community.jedit.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.Element;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;

/**
 * A popup menu for the TaskList plugin
 *
 * @author   John Gellene
 */
public class TaskListPopup extends JPopupMenu
{
	private View view;
	private TaskList list;
	private int taskNum;

	public TaskListPopup(View view, TaskList list, int taskNum)
	{
		super(jEdit.getProperty("tasklist.popup.heading"));
		setLightWeightPopupEnabled(true);
		this.view = view;
		this.list = list;
		this.taskNum = taskNum;

		int item = 0;
		String name = jEdit.getProperty("tasklist.tasktype." + String.valueOf(item) + ".name");
		while(name != null)
		{
			add(createMenuItem(name));
			item++;
			name = jEdit.getProperty("tasklist.tasktype." + String.valueOf(item)
				+ ".name");
		}
	}


	private JMenuItem createMenuItem(String name) {
		JMenuItem mi = new JMenuItem(name);
		mi.setActionCommand(name);
		mi.addActionListener(new ActionHandler());
		return mi;
	}


	class ActionHandler implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			final String newTaskTag = evt.getActionCommand();
			final View v = view;
			final Task task = (Task)(list.taskListModel).elementAt(taskNum);

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					final String taskText = task.getText();
					final String oldTaskTag = taskText.substring(0, taskText.indexOf(':'));
					final Buffer buffer = task.getBuffer();
					final Element map = buffer.getDefaultRootElement();
					final Element line = map.getElement(task.getLine());
					boolean replace = false;
					if(line != null)
					{
						int tokenStart = line.getStartOffset();
						Token token = buffer.markTokens(task.getLine()).getFirstToken();
						while(token.id != Token.END)
						{
							if(token.id == Token.COMMENT1 || token.id == Token.COMMENT2)
							{
								SearchAndReplace.setSearchString(oldTaskTag);
								SearchAndReplace.setReplaceString(newTaskTag);
								replace =
									SearchAndReplace.replace(v, buffer, tokenStart,
										line.getEndOffset());
								break;
							}
							tokenStart += token.length;
							token = token.next;
						}
					}
					if(!replace)
						JOptionPane.showMessageDialog(v,
							jEdit.getProperty("tasklist.popup.parse-error"),
							jEdit.getProperty("tasklist.title"),
							JOptionPane.ERROR_MESSAGE);
					TaskListPlugin.parseBuffer(buffer);
				}
			});
			view = null;
		}
	}

}



