/*
 * TaskListPlugin.java - TaskList plugin
 * Copyright (C) 2001 Oliver Rutherfurd
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


/*

	TODO: validation in option general option pane
	TODO: validation in TaskTypeDialog (option pane 2)
	TODO: need a text area change listener for re-parsing
	TODO: toggle TextAreaHighlight depending on whether it is selected
	TODO: write documentation
	DONE: check into cvs
	TODO: ensure task highlights are repainted when buffer reloaded, etc...

	QUESTION: what about removing RE stuff in options, should users see?
		- thoughts: yes, it's a programmer's editor - leave the power for the
		user, but provide defaults which are easy to substitute (as done now)
	DONE: are there portions of the code which are not thread safe?

	FUTURE-TODO: allow for displaying all buffers or only current ones
	FUTURE-TODO: add sorting

*/

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.CreateDockableWindow;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.util.Log;

public class TaskListPlugin extends EBPlugin
{
	public static final String NAME = "tasklist";
	public static final int PARSE_DELAY = 1000;

	private static Color highlightColor = Color.blue;
	private static int parseDelay = 1000;
	private static boolean highlightTasks = false;

	/**
	*
	*/
	public void createOptionPanes(OptionsDialog od)
	{
		OptionGroup optionGroup = new OptionGroup(
			jEdit.getProperty("tasklist.label"));

		optionGroup.addOptionPane(new TaskListGeneralOptionPane());
		optionGroup.addOptionPane(new TaskListTaskTypesOptionPane());

		od.addOptionGroup(optionGroup);
	}

	public void createMenuItems(Vector menuItems)
	{
		Log.log(Log.DEBUG, TaskListPlugin.class, "createMenuItems() called.");
		menuItems.addElement(GUIUtilities.loadMenu("tasklist.menu"));
	}

	/**
	*
	*/
	public void start()
	{
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, NAME);
		Log.log(Log.DEBUG, TaskListPlugin.class, "start() called.");
		propertiesChanged();
	}


	public static Color getHighlightColor()
	{
		return highlightColor;
	}

	public void handleMessage(EBMessage message)
	{
		if(message instanceof CreateDockableWindow)
		{
			CreateDockableWindow cmsg = (CreateDockableWindow)message;
			if(cmsg.getDockableWindowName().equals(NAME))
			{

				// QUESTION: would it make sense to keep a hash of
				//	views and TaskLists so we can re-use them here?

				Log.log(Log.NOTICE, TaskListPlugin.class,
					"Creating TaskList window");//##

				TaskList taskList = new TaskList(cmsg.getView());
				cmsg.setDockableWindow(taskList);
			}
		}

		//  NOTE: don't need to parse buffer when they are loaded, just when
		// they are displayed
		else if(message instanceof BufferUpdate)
		{
			BufferUpdate bu = (BufferUpdate)message;
			// if the mode has changed, only re-parse if the
			if(bu.getWhat() == BufferUpdate.MODE_CHANGED)
			{
				final Buffer buffer = bu.getBuffer();
				// only re-parse if buffer is loaded and buffer map contains
				// as non-null ref (null indicates no parse has been done)
				if(buffer.isLoaded() && (bufferMap.get(buffer) != null))
				{
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							TaskListPlugin.parseBuffer(buffer);
						}
					});
				}
			}
			else if(bu.getWhat() == BufferUpdate.LOADED ||
				bu.getWhat() == BufferUpdate.SAVING)
			{
				final Buffer buffer = bu.getBuffer();
				// if the bufferMap contains a null ref, no parse request
				// has been received, so don't reparse
				if(bufferMap.get(buffer) != null)
				{
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							TaskListPlugin.parseBuffer(buffer);
						}
					});
				}
			}
		}
		else if(message instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)message;
			if(epu.getWhat() == EditPaneUpdate.CREATED)
			{
				// TODO: only add if property highlightTasks is true
				// TODO: need to be able to remove highlights
				TaskHighlight highlight = new TaskHighlight();
				epu.getEditPane().getTextArea().getPainter()
					.addCustomHighlight(highlight);
			}
		}
		else if(message instanceof PropertiesChanged)
		{

			Buffer[] buffers = jEdit.getBuffers();
			for(int i = 0; i < buffers.length; i++)
			{
				if(bufferMap.get(buffers[i]) != null)
					extractTasks(buffers[i]);
			}

			// TODO: reparse all buffers
			propertiesChanged();
		}
	}


	/**
	*
	*/
	public static void addTaskType(TaskType taskType)
	{
		taskTypes.addElement(taskType);
	}


	/**
	*
	*/
	private static void loadTaskTypes()
	{
		int i = 0;
		String pattern;
		while((pattern = jEdit.getProperty("tasklist.tasktype." +
			i + ".pattern")) != null)
		{
			String iconPath = jEdit.getProperty(
				"tasklist.tasktype."+ i + ".iconpath");
			String sample = jEdit.getProperty(
				"tasklist.tasktype." + i + ".sample");
			boolean ignoreCase = jEdit.getBooleanProperty(
				"tasklist.tasktype." + i + ".ignorecase");

			taskTypes.addElement(
				new TaskType(pattern, sample, ignoreCase, iconPath));

			i++;
		}

		Log.log(Log.DEBUG, TaskListPlugin.class,
			"starting class list plugin");//##
	}

	private static void propertiesChanged()
	{
		TaskListPlugin.clearTaskTypes();
		TaskListPlugin.loadTaskTypes();

		highlightColor = GUIUtilities.parseColor(jEdit.getProperty(
			"tasklist.highlight.color"));

		try
		{
			parseDelay = Integer.parseInt(
				jEdit.getProperty("tasklist.parsedelay"));
		}
		catch(NumberFormatException nfe)
		{
		}

		if(parseDelay <= 0)
			parseDelay = PARSE_DELAY;
	}

	public static void clearTaskTypes()
	{
		taskTypes.removeAllElements();
	}

	private static Hashtable parseRequests = new Hashtable();

	private static Vector taskTypes = new Vector();
	private static Hashtable bufferMap = new Hashtable();
	private static Vector listeners = new Vector();


	/**
	* Returns the current set of tasks for the buffer requested, if there is a
	* set.  If there is no set, the buffer is parsed.
	* NOTE: This method will not cause a re-parse of a buffer.
	*/
	public synchronized static Hashtable requestTasksForBuffer(final Buffer buffer)
	{
		if(buffer.isLoaded() == false)
			return null;

		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.requestTasksForBuffer("
		//	+ buffer.toString() + ")");//##

		Hashtable taskMap = (Hashtable)bufferMap.get(buffer);

		// taskMap should only be null if buffer has never been parsed
		if(taskMap == null)
		{
			extractTasks(buffer);
		}

		return taskMap;

	}


	/**
	*
	*/
	public synchronized static void extractTasks(final Buffer buffer)
	{

		// NOTE: remove this if this method becomes private
		if(buffer.isLoaded() == false)
			return;
		// if buffer is already in the queue, return
		if(parseRequests.get(buffer) != null)
			return;

		parseRequests.put(buffer, buffer);
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				TaskListPlugin.parseBuffer(buffer);
			}
		});
	}


	/**
	*
	*/
	public synchronized static void parseBuffer(Buffer buffer)
	{
		// DEBUG: starting method
		Log.log(Log.DEBUG, TaskListPlugin.class,
			"TaskListPlugin.parseBuffer('" + buffer.getName() + "');");//##

		TaskListPlugin.clearTasks(buffer);

		Element map = buffer.getDefaultRootElement();
		int firstLine = 0;
		int lastLine = map.getElementCount();
		for(int lineNum = firstLine; lineNum < lastLine; lineNum++)
		{
			Element line = map.getElement(lineNum);
			int lineStart = line.getStartOffset();
			int lineEnd = line.getEndOffset();
			int lineLen = lineEnd - lineStart - 1;

			Token token = buffer.markTokens(lineNum).getFirstToken();
			int tokenStart = lineStart;

			while(token.id != Token.END)
			{
				if(token.id == Token.COMMENT1 || token.id == Token.COMMENT2)
				{
					try
					{
						String text = buffer.getText(tokenStart, token.length);

						// NOTE: might want to have task types in an array
						for(int i = 0; i < taskTypes.size(); i++)
						{
							TaskType taskType = (TaskType)taskTypes.elementAt(i);
							Task task = taskType.extractTask(buffer, text, lineNum, tokenStart - lineStart);
							if(task != null)
							{
								TaskListPlugin.addTask(task);
								break;
							}
						}
					}
					/*catch(BadLocationException bl)
					{
						Log.log(Log.ERROR, TaskListPlugin.class,
							bl.toString());
					}*/
					catch(Exception ex)
					{
						Log.log(Log.ERROR, TaskListPlugin.class,
							ex.toString());
					}

				}
				tokenStart += token.length;
				token = token.next;
			}
		}

		// after a buffer has been parsed, bufferMap should contain
		// and empty set of tasks, if there are not, not a null set
		// (a null set is used to indicate the buffer has never been parsed)
		if(bufferMap.get(buffer) == null)
			bufferMap.put(buffer, new Hashtable());

		Log.log(Log.DEBUG, TaskListPlugin.class,
			"TaskListPlugin.parseBuffer(...) DONE");//##

		// remove 'buffer' from parse queue
		parseRequests.remove(buffer);

	}


	/**
	* Add a task
	*/
	private static void addTask(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.addTask(" + task.toString() + ")");//##

		Buffer buffer = task.getBuffer();
		//@@Vector tasks = (Vector)bufferMap.get(buffer);
		Hashtable taskMap = (Hashtable)bufferMap.get(buffer);

		//@@if(tasks == null)
		if(taskMap == null)
		{
			//@@tasks = new Vector();
			bufferMap.put(buffer, taskMap);
		}

		Integer _line = new Integer(task.getLine());
		if(taskMap.get(_line) != null)
		{
			Log.log(Log.ERROR, TaskListPlugin.class,
				"ALREADY A TASK ON LINE " + task.getLine());//##
		}

		//@@tasks.addElement(task);
		taskMap.put(_line, task);
		fireTaskAdded(task);
	}


	/**
	* Remove all tasks from the buffer.
	*/
	private static void clearTasks(Buffer buffer)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.clearTasks(" + buffer.toString() + ")");//##

		//@@Vector tasks = (Vector)bufferMap.get(buffer);
		Hashtable taskMap = (Hashtable)bufferMap.get(buffer);

		//@@if(tasks == null)
		if(taskMap == null)
		{
			//@@tasks = new Vector();
			bufferMap.put(buffer, new Hashtable());
			return;
		}

		Enumeration _keys = taskMap.keys();
		while(_keys.hasMoreElements())
		{
			Object key = _keys.nextElement();
			Task _task = (Task)taskMap.remove(key);

			//Log.log(Log.DEBUG, TaskListPlugin.class,
			//	"removing task (key=" + key + "): " + _task.toString());//##

			fireTaskRemoved(_task);
		}
	}


	// QUESTION: what about 'batch' operations (clearing all the tasks for
	//	a specific buffer, finished parsing buffer, etc)
	public interface TaskListener
	{
		public void taskAdded(Task task);
		public void taskRemoved(Task task);
	}


	/**
	*
	*/
	public static void addTaskListener(TaskListener listener)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.addTaskListener()");//##

		if(listeners.indexOf(listener) == -1)
		{
			//Log.log(Log.DEBUG, TaskListPlugin.class,
			//	"adding TaskListener: " + listener.toString());//##
			listeners.addElement(listener);
		}
	}


	/**
	*
	*/
	public static boolean removeTaskListener(TaskListener listener)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.removeTaskListener()");//##

		return listeners.removeElement(listener);
	}


	/**
	*
	*/
	private static void fireTaskAdded(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.fireTaskAdded(" + task.toString() + ")");//##

		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).taskAdded(task);
	}


	/**
	*
	*/
	private static void fireTaskRemoved(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.fireTaskRemoved(" + task.toString() + ")");//##

		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).taskRemoved(task);
	}

}
