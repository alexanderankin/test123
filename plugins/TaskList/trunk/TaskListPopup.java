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
 *
 * $Id$
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.*;
import javax.swing.text.Element;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.textarea.JEditTextArea;

// imports for jEdit 4.0
import org.gjt.sp.jedit.buffer.LineElement;

import org.gjt.sp.util.Log;

/**
 * A popup menu for the TaskList plugin
 *
 * @author John Gellene (jgellene@nyc.rr.com)
 */
public class TaskListPopup extends JPopupMenu
{
	private View view;
	private TaskList list;
	private int taskNum;
	private ActionListener listener;
	private BoundedMenu changeMenu;
	private BoundedMenu deleteMenu;

	/**
	 * Constructor
	 *
	 * @param View view the view in which the popup menu will appear
	 * @param TaskList list the TaskList object represented in the
	 * window in which the popup menu will appear
	 * @param int TaskNum the zero-based index of the selected table row
	 * that will be the subject of the popup
	 */
	public TaskListPopup(View view, TaskList list, int taskNum)
	{
		super(jEdit.getProperty("tasklist.popup.heading"));
		setLightWeightPopupEnabled(true);
		this.view = view;
		this.list = list;
		this.taskNum = taskNum;
		this.listener = new ActionHandler();

		changeMenu =
			new BoundedMenu(list, jEdit.getProperty("tasklist.popup.change-menu"));

		int item = 0;
		String name = jEdit.getProperty("tasklist.tasktype." + String.valueOf(item) + ".name");
		while(name != null)
		{
			changeMenu.add(createMenuItem(name));
			item++;
			name = jEdit.getProperty("tasklist.tasktype." + String.valueOf(item)
				+ ".name");
		}
		add(changeMenu);

		deleteMenu = new BoundedMenu(list, "Delete task");
		deleteMenu.add(createMenuItem("Delete task tag", "%Dtag"));
		deleteMenu.add(createMenuItem("Delete entire task", "%Dtask"));
		add(deleteMenu);
	}

	/**
	 * An extension of the JMenu class that relocates the object's child popup menu
	 * as necessary so that it does not appear to right or below the bounds of
	 * another component.
	 * <p>
	 * In the TaskList implementation, the bounding component is the TaskList panel
	 * containing the table display of taks items.
	 *
	 * @author John Gellene (jgellene@nyc.rr.com)
	 */
	public class BoundedMenu extends JMenu
	{
		/**
		 * The component that defines the bounds of the child popup menu.
		 */
		private Component bounds;

		/**
		 * Constructs a BoundedMenu object.
		 *
		 * @param bounds the Component forming the bounds for the object's
		 * child popup menu.
		 * @param title the text to be displayed on the parent menu item
		 */
		public BoundedMenu(Component bounds, String title) {
			super(title);
			this.bounds = bounds;
		}

		/**
		 * Overrides the implementation in JMenu to relocate the child
		 * popup menu so it does not appear outside the right-hand
		 * or lower borders of the menu's bounding component.
		 *
		 * @param visible determines whether the child popup menu is to
		 * made visible.
		 */
		public void setPopupMenuVisible(boolean visible) {
			boolean oldValue = isPopupMenuVisible();
			if(visible != oldValue) {
				if((visible == true) && isShowing()) {
					Point p = setLocation();
					getPopupMenu().show(this, p.x, p.y);
				}
				else {
					getPopupMenu().setVisible(false);
				}
			}
		}

		/**
		 * Determines the location of the child popup menu.
		 *
		 * @return a Point representing the upper left-hand corner
		 * of the child popup menu, expressed relative to the parent
		 * menu of this BoundedMenu object.
		 */
		private Point setLocation() {
			Component parent = getParent();
			Dimension dParent = parent.getPreferredSize();
			/* NOTE: default location of child popup menu */
			Point pPopup = new Point(dParent.width - 1 , -1);
	        SwingUtilities.convertPointToScreen(pPopup, parent);
        	SwingUtilities.convertPointFromScreen(pPopup, list);
        	Dimension dList = list.getSize();
			Dimension dPopup = getPopupMenu().getPreferredSize();
			Point pThis = this.getLocation();
        	if (pPopup.x + dPopup.width > dList.width)
            	pPopup.x -= (dPopup.width + dParent.width);
        	if (pPopup.y + pThis.y + dPopup.height > dList.height)
            	pPopup.y -= (dPopup.height - dParent.height + pThis.y);
	        SwingUtilities.convertPointToScreen(pPopup, list);
        	SwingUtilities.convertPointFromScreen(pPopup, parent);
			return pPopup;
		}
	}


	/**
	 * Creates a menu item for the popup menu
	 *
	 * @param name Represents the menu item entry's text
	 * @param cmd Represents the action command associated
	 * with the menu item
	 *
	 * @return a JMenuItem representing the new menu item
	 */
	private JMenuItem createMenuItem(String name, String cmd) {
		JMenuItem mi = new JMenuItem(name);
		mi.setActionCommand(cmd != null ? cmd : name);
		mi.addActionListener(listener);
		return mi;
	}

	/**
	 * Creates a menu item for the popup menu containing an
	 * action command with the same name as the menu item
	 *
	 * @param name Represents the menu item entry's text
	 *
	 * @return a JMenuItem representing the new menu item
	 */
	private JMenuItem createMenuItem(String name) {
		return createMenuItem(name, null);
	}

	/**
	 * Causes substitution of the comment tag for the selected task item;
	 * displays a message if a parsing error occurs;
	 * reparses buffer regardless of success.
	 */
	class ActionHandler implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			final View v = view;
			final Task task = (Task)(list.taskListModel).elementAt(taskNum);
			final Buffer buffer = task.getBuffer();
			final LineElement line = new LineElement(buffer, task.getLineNumber());
			String cmd = evt.getActionCommand();
			if(cmd.equals("%Dtask"))
			{
				// NOTE: routine for deleting entire task
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if(line != null)
						{
							boolean replace = false;
							int searchStart = line.getStartOffset();
							int searchEnd  = line.getEndOffset();
							Token token = buffer.markTokens(task.getLineNumber()).getFirstToken();
							Segment testSegment = new Segment();
							while(token.id != Token.END)
							{
								if(token.id == Token.COMMENT1 || token.id == Token.COMMENT2)
								{
									int startTask = searchStart + token.length;
//									Log.log(Log.DEBUG, TaskListPopup.class,
//										"Delete task: getting text at offset "
//										+ String.valueOf(searchStart)
//										+ " to "
//										+ String.valueOf(searchEnd));
									buffer.getText(searchStart, searchEnd - searchStart, testSegment);
//									Log.log(Log.DEBUG, TaskListPopup.class,
//										"segment is: " + testSegment.toString());
									int taskLength = task.getText().length();
									String testString = new String(testSegment.array,
										testSegment.offset + token.length - taskLength, taskLength);
//									Log.log(Log.DEBUG, TaskListPopup.class, "comparing \""
//										+ testString + "\" to \"" + task.getText() + "\"");
									if(task.getText().equals(testString))
									{
										SearchAndReplace.setSearchString(testSegment.toString().trim());
										SearchAndReplace.setReplaceString("");
										replace = SearchAndReplace.replace(v, buffer,
											searchStart, searchEnd);
										break;
									}
								}
								searchStart += token.length;
								token = token.next;
							}
						}
					}
				});
			}
			else
			{
				if(cmd.equals("%Dtag"))
					cmd = "";
				else cmd = cmd + ":";
				final String newTaskTag = cmd;

				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						final String taskText = task.getText();
						final String oldTaskTag = taskText.substring(0, taskText.indexOf(':') + 1);
						boolean replace = false;
						if(oldTaskTag.equals(newTaskTag))
							replace = true;
						else if(line != null)
						{
							int tokenStart = line.getStartOffset();
							Token token = buffer.markTokens(task.getLineNumber()).getFirstToken();
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
			}
			view = null;
		}
	}

}



