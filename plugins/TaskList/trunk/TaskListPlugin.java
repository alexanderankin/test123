// TODO: line 1

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
	DONE: write documentation
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
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.util.Log;

/**
 * The main plugin class for the TaskList plugin,
 * conforming to the jEdit Plugin API.
 */
public class TaskListPlugin extends EBPlugin
{
	public static final String NAME = "tasklist";
	public static final int PARSE_DELAY = 1000;

	private static Color highlightColor = Color.blue;
	private static int parseDelay = 1000;
	private static boolean highlightTasks = false;
	private static Hashtable highlights = new Hashtable();

	/**
	 * Creates options panes for jEdit's "Global options" window
	 *
	 * @param od The OptionsDialog in which the oprions panes will be included.
	 */
	public void createOptionPanes(OptionsDialog od)
	{
		OptionGroup optionGroup = new OptionGroup(NAME);

		optionGroup.addOptionPane(new TaskListGeneralOptionPane());
		optionGroup.addOptionPane(new TaskListTaskTypesOptionPane());

		od.addOptionGroup(optionGroup);
	}

	/**
	 * Adds menu items to jEdit's "Plugins" menu
	 *
	 * @param menuItems the collection of menu items being assembled by
	 * the application
	 */
	public void createMenuItems(Vector menuItems)
	{
//		Log.log(Log.DEBUG, TaskListPlugin.class, "createMenuItems() called.");
		menuItems.addElement(GUIUtilities.loadMenu("tasklist.menu"));
	}

	/**
	 * Initialization routine called after the plugin is first loaded
	 */
	public void start()
	{
//		Log.log(Log.DEBUG, TaskListPlugin.class, "start() called.");
		propertiesChanged();
	}

	/**
	 * Returns the user-specified color used for highlighting task items
	 * in the current buffer.
	 *
	 * @return a Color representing the highlighting color.
	 */
	public static Color getHighlightColor()
	{
		return highlightColor;
	}

	/**
	 * Handles selected messages received by the TaskListPlugin object
	 * from jEdit's EditBus messaging facility.
	 * <p>
	 * The methods handles the following messages:
	 * <ul><li>CreateDockableWindow (deprecated in jEdit 4.0)</li>
	 * <li>BufferUpdate (to trigger reparsing and updating of the task display)</li>
	 * <li>EditPaneUpdate (also to trigger task list updates)</li>
	 * <li>PropertiesChanged (to update disaply following changes in user options)</li></ul>
	 *
	 * @param message a EBMessage object received from jEdit's EditBus.
	 */
	public void handleMessage(EBMessage message)
	{

		//  NOTE: don't need to parse buffer when they are loaded, just when
		// they are displayed
		if(message instanceof BufferUpdate)
		{
			BufferUpdate bu = (BufferUpdate)message;
			if(bu.getWhat() == BufferUpdate.MODE_CHANGED ||
				bu.getWhat() == BufferUpdate.LOADED ||
				bu.getWhat() == BufferUpdate.SAVED)
			{
				final Buffer buffer = bu.getBuffer();
				// only re-parse if buffer is loaded and buffer map contains
				// as non-null ref (null indicates no parse has been done)
				if(buffer.isLoaded() && (bufferMap.get(buffer) != null))
				{
					TaskListPlugin.extractTasks(buffer);
				}
			}
		}
		else if(message instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)message;
			if(epu.getWhat() == EditPaneUpdate.CREATED)
			{
				EditPane editPane = epu.getEditPane();
				JEditTextArea textArea = editPane.getTextArea();
				TaskHighlight highlight = new TaskHighlight(textArea);
				highlights.put(editPane, highlight);
				textArea.getPainter().addExtension(highlight);
			}
			else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
			{
				final Buffer buffer = epu.getEditPane().getBuffer();
				TaskListPlugin.clearTasks(buffer);
				TaskListPlugin.extractTasks(buffer);
			}
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				highlights.remove(epu.getEditPane());
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
	 * Adds a TaskType object to the list maintained by the plugin object.
	 *
	 * @param taskType the TaskType object to be added
	 */
	public static void addTaskType(TaskType taskType)
	{
		taskTypes.addElement(taskType);
	}


	/**
	 * loads TaskType objects from data maintained in the Properties object
	 * currently maintained by the application.
	 */
	private static void loadTaskTypes()
	{
		int i = 0;
		String pattern;
		while((pattern = jEdit.getProperty("tasklist.tasktype." +
			i + ".pattern")) != null)
		{
			String name = jEdit.getProperty(
				"tasklist.tasktype." + i + ".name");
			String iconPath = jEdit.getProperty(
				"tasklist.tasktype."+ i + ".iconpath");
			String sample = jEdit.getProperty(
				"tasklist.tasktype." + i + ".sample");
			boolean ignoreCase = jEdit.getBooleanProperty(
				"tasklist.tasktype." + i + ".ignorecase");

			taskTypes.addElement(
				new TaskType(name, pattern, sample, ignoreCase, iconPath));

			i++;
		}

//		Log.log(Log.DEBUG, TaskListPlugin.class,
//			"starting class list plugin");//##
	}

	/**
	 * Clears existing task patterns and reloads default settings
	 */
	public static void resetPatterns(View view)
	{
		if(JOptionPane.YES_OPTION == GUIUtilities.confirm(view, "tasklist.reset-query",
			null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
		{
			reloadPatterns();
			GUIUtilities.message(view, "tasklist.reset-complete", null);
			TaskListPlugin.extractTasks(view.getBuffer());
		}
	}

	/**
	 * Implements reloading of default task patterns
	 */
	private static void reloadPatterns()
	{
		TaskListPlugin.clearTaskTypes();
		jEdit.setProperty("tasklist.tasktype.0.name", "DEBUG");
		jEdit.setProperty("tasklist.tasktype.0.iconpath", "Debug.gif");
		jEdit.setProperty("tasklist.tasktype.0.ignorecase", "false");
		jEdit.setProperty("tasklist.tasktype.0.pattern", "(?:\\s*)(DEBUG):(?:\\s+)(.+)$");
		jEdit.setProperty("tasklist.tasktype.0.sample", "DEBUG: [comment text]");

		jEdit.setProperty("tasklist.tasktype.1.name", "DONE");
		jEdit.setProperty("tasklist.tasktype.1.iconpath", "Done.gif");
		jEdit.setProperty("tasklist.tasktype.1.ignorecase", "false");
		jEdit.setProperty("tasklist.tasktype.1.pattern", "(?:\\s*)(DONE):(?:\\s+)(.+)$");
		jEdit.setProperty("tasklist.tasktype.1.sample", "DONE: [comment text]");

		jEdit.setProperty("tasklist.tasktype.2.name", "IDEA");
		jEdit.setProperty("tasklist.tasktype.2.iconpath", "Intranet.gif");
		jEdit.setProperty("tasklist.tasktype.2.ignorecase", "false");
		jEdit.setProperty("tasklist.tasktype.2.pattern", "(?:\\s*)(IDEA):(?:\\s+)(.+)$");
		jEdit.setProperty("tasklist.tasktype.2.sample", "IDEA: [comment text]");

		jEdit.setProperty("tasklist.tasktype.3.name", "NOTE");
		jEdit.setProperty("tasklist.tasktype.3.iconpath", "Document.gif");
		jEdit.setProperty("tasklist.tasktype.3.ignorecase", "false");
		jEdit.setProperty("tasklist.tasktype.3.pattern", "(?:\\s*)(NOTE):(?:\\s+)(.+)$");
		jEdit.setProperty("tasklist.tasktype.3.sample", "NOTE: [comment text]");

		jEdit.setProperty("tasklist.tasktype.4.name", "QUESTION");
		jEdit.setProperty("tasklist.tasktype.4.iconpath", "Question.gif");
		jEdit.setProperty("tasklist.tasktype.4.ignorecase", "false");
		jEdit.setProperty("tasklist.tasktype.4.pattern", "(?:\\s*)(QUESTION):(?:\\s+)(.+)$");
		jEdit.setProperty("tasklist.tasktype.4.sample", "QUESTION: [comment text]");

		jEdit.setProperty("tasklist.tasktype.5.name", "TODO");
		jEdit.setProperty("tasklist.tasktype.5.iconpath", "Exclamation.gif");
		jEdit.setProperty("tasklist.tasktype.5.ignorecase", "false");
		jEdit.setProperty("tasklist.tasktype.5.pattern", "(?:\\s*)(TODO):(?:\\s+)(.+)$");
		jEdit.setProperty("tasklist.tasktype.5.sample", "TODO: [comment text]");
		pruneTaskListProperties(6);
		loadTaskTypes();
	}

	static void pruneTaskListProperties(int start)
	{
		for(int i = start;
			jEdit.getProperty("tasklist.tasktype." + i + ".pattern") != null;
			i++)
		{
			jEdit.unsetProperty("tasklist.tasktype." + i + ".name");
			jEdit.unsetProperty("tasklist.tasktype." + i + ".iconpath");
			jEdit.unsetProperty("tasklist.tasktype." + i + ".ignorecase");
			jEdit.unsetProperty("tasklist.tasktype." + i + ".pattern");
			jEdit.unsetProperty("tasklist.tasktype." + i + ".sample");
		}
	}



	/**
	 * Causes an update of application data, typically after a change
	 * in the plugin's user settings.
	 */
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


		Enumeration elements = highlights.elements();
		boolean highlightEnabled = jEdit.getBooleanProperty("tasklist.highlight.tasks");
		while(elements.hasMoreElements())
		{
			TaskHighlight highlight = (TaskHighlight)elements.nextElement();
			if(highlight != null)
				highlight.setEnabled(highlightEnabled);
		}

		fireTasksUpdated();
	}

	/**
	 * Removes all TaskType data from the collection maintained by the plugin.
	 */
	public static void clearTaskTypes()
	{
		taskTypes.removeAllElements();
	}

	/**
	 * A collection of pending requests to parse buffers, maintained to
	 * prevent unnecessary duplication.
	 */
	private static Hashtable parseRequests = new Hashtable();

	/**
	 * A collection of TaskType objects representing the form of comments that
	 * will be parsed from a buffer and store as Task objects
	 */
	private static Vector taskTypes = new Vector();

	/**
	 * A collection of collections: each member represents a collection of
	 * Task objects associated with a particular buffer. The plugin uses this
	 * object to keep track of Task objects.
	 */
	private static Hashtable bufferMap = new Hashtable();

	/**
	 * A collection of TaskListener objects that will be notified when
	 * Task objects are added or removed from the collection maintained
	 * by the plugin.
	 */
	private static Vector listeners = new Vector();


	/**
	* Returns the current set of tasks for the buffer requested, if there is a
	* set.  If there is no set, the buffer is parsed.
	* <p>
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
	 * Directs the parsing of a buffer for task data if no request for parsing
	 * that buffer is currently pending.
	 * <p>
	 * This method is more effcient than parseBuffer() because it prevents
	 * duplicate parse requests.
	 *
	 * @param buffer the Buffer to be parsed for task data.
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
	 * Parses a Buffer and extracts task item data to be stored in the plugin's
	 * collection.
	 *
	 * @param buffer the Buffer to be parsed
	 */
	public synchronized static void parseBuffer(Buffer buffer)
	{
		// NOTE: parseBuffer() method
		// DEBUG: starting method
//		Log.log(Log.DEBUG, TaskListPlugin.class,
//			"TaskListPlugin.parseBuffer('" + buffer.getName() + "');");//##

		TaskListPlugin.clearTasks(buffer);

		int firstLine = 0;
		int lastLine = buffer.getLineCount();
		for(int lineNum = firstLine; lineNum < lastLine; lineNum++)
		{
			int lineStart = buffer.getLineStartOffset(lineNum);
			int lineLen = buffer.getLineLength(lineNum);

			Token token = buffer.markTokens(lineNum).getFirstToken();
			int tokenStart = lineStart;

			while(token.id != Token.END)
			{
				if(token.id == Token.COMMENT1 || token.id == Token.COMMENT2)
				{
//					Log.log(Log.DEBUG,TaskListPlugin.class,
//						"Comment token found on line " + String.valueOf(lineNum)
//						+ " length = " + String.valueOf(token.length));
					try
					{
//						Log.log(Log.DEBUG,TaskListPlugin.class,"Comment token on line "
//							+ String.valueOf(lineNum));
						String text = buffer.getText(tokenStart, token.length);
//						Log.log(Log.DEBUG,TaskListPlugin.class,"Parsing: " + text);
						// NOTE: might want to have task types in an array
						for(int i = 0; i < taskTypes.size(); i++)
						{
							TaskType taskType = (TaskType)taskTypes.elementAt(i);
							Task task = taskType.extractTask(buffer, text, lineNum, tokenStart - lineStart);
							if(task != null)
							{
//								Log.log(Log.DEBUG,TaskListPlugin.class,
//									"Parsed task found at line " + String.valueOf(lineNum));
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
//							ex.toString());
							ex);
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

//		Log.log(Log.DEBUG, TaskListPlugin.class,
//			"TaskListPlugin.parseBuffer(...) DONE");//##

		// remove 'buffer' from parse queue
		parseRequests.remove(buffer);

		fireTasksUpdated();

	}


	/**
	* Add a Task to the collection maintained by the plugin.
	*
	* @param task the Task to be added.
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

		Integer _line = new Integer(task.getLineIndex());
		if(taskMap.get(_line) != null)
		{
			Log.log(Log.ERROR, TaskListPlugin.class,
				"Already a task on line " + task.getLineIndex()
				+ "of buffer: " + buffer.getPath());//##
		}

		//@@tasks.addElement(task);
		taskMap.put(_line, task);
		fireTaskAdded(task);
	}


	/**
	* Remove all tasks relating to a given Buffer from the collection
	* of Task objects maintained by the plugin.
	*
	* @param buffer the Buffer whose tasks are to be removed.
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
	/**
	 * An interface defining actions to be taken upon the addition (or removal)
	 * of a Task object to (or from) the collection maintained by the plugin.
	 */
	public interface TaskListener
	{
		public void taskAdded(Task task);
		public void taskRemoved(Task task);
		public void tasksUpdated();
	}


	/**
	 * Adds a TaskListener to the collection maintined by the plugin.
	 *
	 * @param listener the TaskListener to be added
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
	 * Removes a TaskListener from the collection maintined by the plugin.
	 *
	 * @param listener the TaskListener to be removed
	 */
	public static boolean removeTaskListener(TaskListener listener)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.removeTaskListener()");//##

		return listeners.removeElement(listener);
	}


	/**
	 * Calls the taskAdded() method of each of the TaskListener objects
	 * in the collection maintained by the plugin.
	 *
	 * @param task the Task being added to the collection of Task objects
	 * maintained by the plugin.
	 */
	private static void fireTaskAdded(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.fireTaskAdded(" + task.toString() + ")");//##

		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).taskAdded(task);
	}


	/**
	 * Calls the taskRemoved() method of each of the TaskListener objects
	 * in the collection maintained by the plugin.
	 *
	 * @param task the Task being removed from the collection of Task objects
	 * maintained by the plugin.
	 */
	private static void fireTaskRemoved(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.fireTaskRemoved(" + task.toString() + ")");//##

		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).taskRemoved(task);
	}

	private static void fireTasksUpdated()
	{
		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).tasksUpdated();
	}

}
