/*
 * TaskListPlugin.java - TaskList plugin
 * Copyright (C) 2001,2002 Oliver Rutherfurd
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

package tasklist;

/*{{{ TODOS...
	TODO: need a text area change listener for re-parsing
	TODO: ensure task highlights are repainted when buffer reloaded, etc...
	DONE: are there portions of the code which are not thread safe?
	FUTURE-TODO: allow for displaying all buffers or only current ones
}}}*/

//{{{ imports
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
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.util.Log;

import tasklist.options.TaskListGeneralOptionPane;
import tasklist.options.TaskListModesOptionPane;
import tasklist.options.TaskListTaskTypesOptionPane;
//}}}

/**
 * The main plugin class for the TaskList plugin,
 * conforming to the jEdit Plugin API.
 */
public class TaskListPlugin extends EBPlugin
{
	public static final String NAME = "tasklist";

	private static Color highlightColor = Color.blue;
	private static boolean highlightTasks = false;
	private static Hashtable highlights = new Hashtable();
	private static boolean allowSingleClickSelection = false;

	//{{{ createOptionPanes() method
	/**
	 * Creates options panes for jEdit's "Global options" window
	 * @param od The OptionsDialog in which the oprions panes will be included.
	 */
	public void createOptionPanes(OptionsDialog od)
	{
		OptionGroup optionGroup = new OptionGroup(NAME);

		optionGroup.addOptionPane(new TaskListGeneralOptionPane());
		optionGroup.addOptionPane(new TaskListTaskTypesOptionPane());
		optionGroup.addOptionPane(new TaskListModesOptionPane());

		od.addOptionGroup(optionGroup);
	}//}}}

	//{{{ createMenuItems() method
	/**
	 * Adds menu items to jEdit's "Plugins" menu
	 * @param menuItems the collection of menu items being assembled by
	 * the application
	 */
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("tasklist.menu"));
	}//}}}

	//{{{ start() method
	/**
	 * Initialization routine called after the plugin is first loaded
	 */
	public void start()
	{
		propertiesChanged();
	}//}}}

	//{{{ getHighlightColor() method
	/**
	 * Returns the user-specified color used for highlighting task items
	 * in the current buffer.
	 * @return a Color representing the highlighting color.
	 */
	public static Color getHighlightColor()
	{
		return highlightColor;
	}//}}}

	//{{{ getAllowSingleClickSelection() method
	/**
	* Returns whether single-clicks will display a task.
	*/
	public static boolean getAllowSingleClickSelection()
	{
		return allowSingleClickSelection;
	}//}}}

	//{{{ handleMessage() method
	/**
	 * Handles selected messages received by the TaskListPlugin object
	 * from jEdit's EditBus messaging facility.
	 * <p>
	 * The methods handles the following messages:
	 * <ul><li>CreateDockableWindow (deprecated in jEdit 4.0)</li>
	 * <li>BufferUpdate (to trigger reparsing and updating of the task display)</li>
	 * <li>EditPaneUpdate (also to trigger task list updates)</li>
	 * <li>PropertiesChanged (to update disaply following changes in user options)</li>
	 * </ul>
	 * @param message a EBMessage object received from jEdit's EditBus.
	 */
	public void handleMessage(EBMessage message)
	{

		//  NOTE: don't need to parse buffer when they are loaded, 
		// just when they are displayed
		if(message instanceof BufferUpdate)
		{
			BufferUpdate bu = (BufferUpdate)message;
			if(bu.getWhat() == BufferUpdate.PROPERTIES_CHANGED ||
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
			else if(bu.getWhat() == BufferUpdate.CLOSED)
			{
				bufferMap.remove(bu.getBuffer());
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

			propertiesChanged();

			Buffer[] buffers = jEdit.getBuffers();
			for(int i = 0; i < buffers.length; i++)
			{
				if(bufferMap.get(buffers[i]) != null)
					extractTasks(buffers[i]);
			}
		}
	}//}}}

	//{{{ addTaskType() method
	/**
	 * Adds a TaskType object to the list maintained by the plugin object.
	 * @param taskType the TaskType object to be added
	 */
	public static void addTaskType(TaskType taskType)
	{
		taskTypes.addElement(taskType);
	}//}}}

	//{{{ loadTaskTypes() method
	/**
	 * loads TaskType objects from data maintained in the Properties object
	 * currently maintained by the application.
	 */
	private static void loadTaskTypes()
	{
		int i = 0;
		String pattern;
		while((pattern = jEdit.getProperty("tasklist.tasktype." +
			i + ".pattern")) != null && !pattern.equals(""))
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
	}//}}}

	//{{{ resetPatterns() method
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
	}//}}}

	//{{{ reloadPatterns() method
	/**
	 * Implements reloading of default task patterns
	 */
	private static void reloadPatterns()
	{
		TaskListPlugin.clearTaskTypes();

		jEdit.setProperty("tasklist.tasktype.0.name","DEBUG");
		jEdit.setProperty("tasklist.tasktype.0.iconpath","stock_preferences-16.png");
		jEdit.setProperty("tasklist.tasktype.0.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.0.pattern","\\s(DEBUG):\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.0.sample"," DEBUG: [comment text]");

		jEdit.setProperty("tasklist.tasktype.1.name","DONE");
		jEdit.setProperty("tasklist.tasktype.1.iconpath","stock_spellcheck-16.png");
		jEdit.setProperty("tasklist.tasktype.1.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.1.pattern","\\s(DONE):\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.1.sample"," DONE: [comment text]");

		jEdit.setProperty("tasklist.tasktype.2.name","FIXME");
		jEdit.setProperty("tasklist.tasktype.2.iconpath","stock_broken_image-16.png");
		jEdit.setProperty("tasklist.tasktype.2.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.2.pattern","\\s(FIXME):\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.2.sample"," FIXME: [comment text]");

		jEdit.setProperty("tasklist.tasktype.3.name","IDEA");
		jEdit.setProperty("tasklist.tasktype.3.iconpath","stock_about-16.png");
		jEdit.setProperty("tasklist.tasktype.3.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.3.pattern","\\s(IDEA):\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.3.sample"," IDEA: [comment text]");

		jEdit.setProperty("tasklist.tasktype.4.name","NOTE");
		jEdit.setProperty("tasklist.tasktype.4.iconpath","stock_attach-16.png");
		jEdit.setProperty("tasklist.tasktype.4.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.4.pattern","\\s(NOTE):\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.4.sample"," NOTE: [comment text]");

		jEdit.setProperty("tasklist.tasktype.5.name","QUESTION");
		jEdit.setProperty("tasklist.tasktype.5.iconpath","stock_help-16.png");
		jEdit.setProperty("tasklist.tasktype.5.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.5.pattern","\\s(QUESTION):\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.5.sample"," QUESTION: [comment text]");

		jEdit.setProperty("tasklist.tasktype.6.name","TODO");
		jEdit.setProperty("tasklist.tasktype.6.iconpath","stock_jump-to-16.png");
		jEdit.setProperty("tasklist.tasktype.6.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.6.pattern","\\s(TODO):\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.6.sample"," TODO: [comment text]");

		jEdit.setProperty("tasklist.tasktype.7.name","XXX");
		jEdit.setProperty("tasklist.tasktype.7.iconpath","stock_right-16.png");
		jEdit.setProperty("tasklist.tasktype.7.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.7.pattern","\\s(XXX)\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.7.sample"," XXX [comment text]");

		jEdit.setProperty("tasklist.tasktype.8.iconpath","stock_help-16.png");
		jEdit.setProperty("tasklist.tasktype.8.ignorecase","false");
		jEdit.setProperty("tasklist.tasktype.8.name","???");
		jEdit.setProperty("tasklist.tasktype.8.pattern","\\s([?]{3})\\s+(.+)$");
		jEdit.setProperty("tasklist.tasktype.8.sample"," ??? [commented text]");

		pruneTaskListProperties(9);
		loadTaskTypes();
	}//}}}

	//{{{ pruneTaskListProperties() method
	/**
	* Removes all task patterns >= `start`.
	* @param start the first task pattern to remove, all after will
	* also be removed.
	*/
	public static void pruneTaskListProperties(int start)
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
	}//}}}

	//{{{ propertiesChanged() method
	/**
	 * Causes an update of application data, typically after a change
	 * in the plugin's user settings.
	 */
	private static void propertiesChanged()
	{
		TaskListPlugin.clearTaskTypes();
		TaskListPlugin.loadTaskTypes();
		TaskListPlugin.loadParseModes();

		highlightColor = GUIUtilities.parseColor(jEdit.getProperty(
			"tasklist.highlight.color"));

		allowSingleClickSelection = jEdit.getBooleanProperty(
			"tasklist.single-click-selection",false);

		Enumeration elements = highlights.elements();
		boolean highlightEnabled = jEdit.getBooleanProperty("tasklist.highlight.tasks");
		while(elements.hasMoreElements())
		{
			TaskHighlight highlight = (TaskHighlight)elements.nextElement();
			if(highlight != null)
				highlight.setEnabled(highlightEnabled);
		}

		fireTasksUpdated();
	}//}}}

	//{{{ loadParseModes() method
	/**
	* Load which modes are to be parsed and which are not to be parsed.
	*/
	public static void loadParseModes()
	{
		parseModes.clear();

		Mode[] modes = jEdit.getModes();
		for(int i = 0; i < modes.length; i++)
		{
			Boolean parse = new Boolean(
				jEdit.getBooleanProperty(
					"options.tasklist.parse." + modes[i].getName(), true));

			parseModes.put(modes[i].getName(),parse);
		}
	}//}}}

	//{{{ clearTaskTypes() method
	/**
	 * Removes all TaskType data from the collection maintained by the plugin.
	 */
	public static void clearTaskTypes()
	{
		taskTypes.removeAllElements();
	}//}}}

	//{{{ private static members
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
	 * A collection to track which buffer modes to parse
	 * tasks from and which to skip.
	 */
	private static Hashtable parseModes = new Hashtable();
	//}}}

	//{{{ requestTasksForBuffer() method
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

	}//}}}

	//{{{ extractTasks() method
	/**
	 * Directs the parsing of a buffer for task data if no request for parsing
	 * that buffer is currently pending.
	 * <p>
	 * This method is more effcient than parseBuffer() because it prevents
	 * duplicate parse requests.
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
	}//}}}

	//{{{ parseBuffer() method
	/**
	 * Parses a Buffer and extracts task item data to be stored in the plugin's
	 * collection.
	 * @param buffer the Buffer to be parsed
	 */
	public synchronized static void parseBuffer(Buffer buffer)
	{
		// Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.parseBuffer('" + buffer.getName() + "');");//##

		TaskListPlugin.clearTasks(buffer);

		// if this file's mode is not to be parsed, skip it
		String mode = buffer.getMode().getName();
		if(!((Boolean)parseModes.get(mode)).booleanValue()){

			// fill with empty Hashtable of tasks
			bufferMap.put(buffer, new Hashtable());

			// remove 'buffer' from parse queue
			parseRequests.remove(buffer);

			fireTasksUpdated();
			return;
		}

		int firstLine = 0;
		int lastLine = buffer.getLineCount();
		for(int lineNum = firstLine; lineNum < lastLine; lineNum++)
		{
			int lineStart = buffer.getLineStartOffset(lineNum);
			int lineLen = buffer.getLineLength(lineNum);

            DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
			buffer.markTokens(lineNum,tokenHandler);
			Token token = tokenHandler.getTokens();
			int tokenStart = lineStart;
			int lastToken = token.id;
			int chunkStart = -1;
			int chunkLength = 0;
			int type = -1;

			while(token.id != Token.END)
			{
				// it looks like in 4.1 Tokens are not longer the whole
				// string for the type of token, as WHITESPACE and TAB are 
				// now returned between words, so we need to piece together 
				// the text for the comment
				if(token.id == Token.COMMENT1 || token.id == Token.COMMENT2)
				{
					type = token.id;
					chunkStart = tokenStart;
					chunkLength = token.length;
					while(token.next.id == type 
							|| token.next.id == Token.WHITESPACE 
							|| token.next.id == Token.TAB)
					{
						token = token.next;
						chunkLength += token.length;
					}
					String text = buffer.getText(chunkStart, chunkLength);

					// NOTE: might want to have task types in an array
					for(int i = 0; i < taskTypes.size(); i++)
					{
						TaskType taskType = (TaskType)taskTypes.elementAt(i);
						Task task = taskType.extractTask(buffer, text, lineNum, chunkStart - lineStart);
						if(task != null)
						{
							TaskListPlugin.addTask(task);
							break;
						}
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

		// Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.parseBuffer(...) DONE");//##

		// remove 'buffer' from parse queue
		parseRequests.remove(buffer);

		fireTasksUpdated();
	}//}}}

	//{{{ addTask() method
	/**
	* Add a Task to the collection maintained by the plugin.
	* @param task the Task to be added.
	*/
	private static void addTask(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.addTask(" + task.toString() + ")");//##

		Buffer buffer = task.getBuffer();
		Hashtable taskMap = (Hashtable)bufferMap.get(buffer);

		if(taskMap == null)
		{
			bufferMap.put(buffer, taskMap);
		}

		Integer _line = new Integer(task.getLineIndex());
		if(taskMap.get(_line) != null)
		{
			Log.log(Log.ERROR, TaskListPlugin.class,
				"Already a task on line " + task.getLineIndex()
				+ "of buffer: " + buffer.getPath());//##
		}

		taskMap.put(_line, task);
		fireTaskAdded(task);
	}//}}}

	//{{{ clearTasks() method
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

		Hashtable taskMap = (Hashtable)bufferMap.get(buffer);

		if(taskMap == null)
		{
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
	}//}}}

	//{{{ TaskListener interface
	/**
	 * An interface defining actions to be taken upon the addition (or removal)
	 * of a Task object to (or from) the collection maintained by the plugin.
	 */
	public interface TaskListener
	{
		// QUESTION: what about 'batch' operations (clearing all the tasks for
		//	a specific buffer, finished parsing buffer, etc)
		public void taskAdded(Task task);
		public void taskRemoved(Task task);
		public void tasksUpdated();
	}//}}}

	//{{{ addTaskListener() method
	/**
	 * Adds a TaskListener to the collection maintined by the plugin.
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
	}//}}}

	//{{{ removeTaskListener() method
	/**
	 * Removes a TaskListener from the collection maintined by the plugin.
	 * @param listener the TaskListener to be removed
	 */
	public static boolean removeTaskListener(TaskListener listener)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.removeTaskListener()");//##

		return listeners.removeElement(listener);
	}//}}}

	//{{{ fireTaskAdded() method
	/**
	 * Calls the taskAdded() method of each of the TaskListener objects
	 * in the collection maintained by the plugin.
	 * @param task the Task being added to the collection of Task objects
	 * maintained by the plugin.
	 */
	private static void fireTaskAdded(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.fireTaskAdded(" + task.toString() + ")");//##

		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).taskAdded(task);
	}//}}}

	//{{{ fireTaskRemoved() method
	/**
	 * Calls the taskRemoved() method of each of the TaskListener objects
	 * in the collection maintained by the plugin.
	 * @param task the Task being removed from the collection of Task objects
	 * maintained by the plugin.
	 */
	private static void fireTaskRemoved(Task task)
	{
		//Log.log(Log.DEBUG, TaskListPlugin.class,
		//	"TaskListPlugin.fireTaskRemoved(" + task.toString() + ")");//##

		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).taskRemoved(task);
	}//}}}

	//{{{ fireTasksUpdated() method
	private static void fireTasksUpdated()
	{
		for(int i = 0; i < listeners.size(); i++)
			((TaskListener)listeners.elementAt(i)).tasksUpdated();
	}//}}}

}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
