/*
 * TaskListModel.java - TaskList plugin
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


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

public class TaskListModel extends AbstractTableModel
	implements TaskListPlugin.TaskListener, EBComponent
{

	private static final String[] colNames = {
		"",
		"Line #",
		"Description",
		"Buffer",
	};

	// buffer display format constants
	private static final int NAME_DIR = 0;	// filename (dir)
	private static final int FULL_PATH = 1;	// dir/name
	private static final int NAME_ONLY = 2; // filename


	/**
	*
	*/
	public TaskListModel(View view)
	{
		this.view = view;

		tasks = new Vector();
		buffers = new Vector();

		this.bufferDisplay = getBufferDisplay();

		try
		{
			EditPane[] editPanes = view.getEditPanes();
			for(int i = 0; i < editPanes.length; i++)
			{
				_addBuffer(editPanes[i].getBuffer());
			}

			if(editPanes.length > 1)
				fireTableStructureChanged();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//this.viewBuffers = getViewBuffers();

		// TODO: need to display task for currect set of edit panes when first
		// created
	}


	/**
	*
	*/
	public Task elementAt(int row)
	{
		return (Task)tasks.elementAt(row);
	}


	/**
	* Adds the buffer to the set of buffers whose tasks should be returned by
	* the table model.  If the buffer is already in the set, return - otherwise
	* request the tasks for this buffer from TaskListPlugin.  If the set of
	* tasks returned is null, the buffer hasn't been parsed - it will be parsed
	* and we will find out about the task when 'taskAdded' events are fired.
	*/
	private void _addBuffer(Buffer buffer)
	{
		//Log.log(Log.DEBUG, TaskListModel.class,
		//	"TaskListModel.addBuffer(" + buffer.getPath() + ")"); //##

		// add the buffer to the buffers vector
		if(buffers.indexOf(buffer) == -1)
		{
			//Log.log(Log.DEBUG, TaskListModel.class,
			//	"buffer not in buffers, adding...");//##

			buffers.addElement(buffer);
		}
		else
		{
			Log.log(Log.ERROR, TaskListModel.class,
				"buffer already in buffers");//##
		}

		// ask for tasks - if they have not been parsed,
		// they will be asynchronously parsed - otherwise
		// they will be returned
		Hashtable taskMap = TaskListPlugin.requestTasksForBuffer(buffer);

		//Log.log(Log.DEBUG, TaskListModel.class,
		//		"fetching tasks for buffer...");//##

		if(taskMap != null)
		{
			//Log.log(Log.DEBUG, TaskListModel.class,
			//	"...taskMap not null, adding " +
			//	taskMap.size() + " tasks");//##

			Enumeration _keys = taskMap.keys();
			while(_keys.hasMoreElements())
			{
				Object key = _keys.nextElement();
				Task task = (Task)taskMap.get(key);
				addTask(task);
			}
		}
	}


	/**
	*
	*/
	private void addTask(Task task)
	{
		//Log.log(Log.DEBUG, TaskListModel.class,
		//	"TaskListModel.addTask(" + task.toString() + ")");//##

		tasks.addElement(task);

		// check whether task is appended or inserted
		int index = tasks.indexOf(task);
		//Log.log(Log.DEBUG, TaskListModel.class,
		//		"index=" + index + ",tasks.size()=" + tasks.size());//##

		fireTableRowsInserted(tasks.size() - 1, tasks.size() -1);
	}


	/**
	* Remove the buffer from the current set, and remove all the tasks that
	* 'belong' to the buffer.
	*/
	private void _removeBuffer(Buffer buffer)
	{
		//Log.log(Log.DEBUG, TaskListModel.class,
		//	"TaskListModel.removeBuffer(" + buffer.getPath() + ")");//##

		int index = buffers.indexOf(buffer);
		if(index > -1)
		{
			//Log.log(Log.DEBUG, TaskListModel.class,
			//	"buffer to be removed {" + buffer.getPath() + "} found");//##

			for(int i = tasks.size() - 1; i >= 0; i--)
			{
				if(((Task)tasks.elementAt(i)).getBuffer() == buffer)
				{
					removeTask(i);
				}
			}

			buffers.removeElementAt(index);

		}
		else
		{
			Log.log(Log.ERROR, TaskListModel.class,
				"buffer to be removed {" + buffer.getPath()
				+ "} not in set?");//##
		}
	}


	// internal method to remove a task
	private void removeTask(Task task)
	{
		int taskNum = tasks.indexOf(task);
		if(taskNum != -1)
		{
			removeTask(taskNum);
		}
	}


	private void removeTask(int index)
	{
		tasks.removeElementAt(index);
		fireTableRowsDeleted(index, index);
	}


	private int bufferDisplay;
	private int viewBuffers;
	private View view;
	private Vector tasks;
	private Vector buffers;

	// ~ start of TaskListener interface implementation

	public void taskAdded(Task task)
	{
		if(buffers.indexOf(task.getBuffer()) > -1)
		{
			if(tasks.indexOf(task) == -1)
			{
				addTask(task);
			}
		}
	}


	public void taskRemoved(Task task)
	{
		removeTask(task);
	}
	// ~ end of TaskListener interface implementation


	public void handleMessage(EBMessage message)
	{
		if(message instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)message;
			View view = epu.getEditPane().getView();
			if(view == this.view)
			{
				// used to determine whether to fireTableStructureChanged
				int prevBuffCount = buffers.size();

				if(epu.getWhat() == EditPaneUpdate.DESTROYED)
				{
					Buffer buffer = epu.getEditPane().getBuffer();

					for(int i = 0; i < buffers.size(); i++)
					{
						if(((Buffer)buffers.elementAt(i)) != buffer)
							_removeBuffer(buffer);
					}
				}
				else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
				{
					EditPane[] editPanes = view.getEditPanes();

					// if the buffer is alreay in the current set,
					// no need to add it, just remove the one no longer
					// displayed
					Buffer buffer = epu.getEditPane().getBuffer();
					if(buffers.indexOf(buffer) == -1)
						_addBuffer(buffer);

					// if there is a buffer displayed which is no longer
					// in one of the currect set of EditPanes, remove it
					for(int i = 0; i < buffers.size(); i++)
					{
						buffer = (Buffer)buffers.elementAt(i);
						boolean foundBuffer = false;

						// look through the current set of editPanes for
						// the current buffer
						for(int j = 0; j < editPanes.length && foundBuffer == false; j++)
						{
							if(buffer == editPanes[j].getBuffer())
							{
								foundBuffer = true;
							}
						}

						if(foundBuffer == false)
							_removeBuffer(buffer);
					}
				}// end if what == BUFFER_CHANGED

				// if going from multiple buffers to one buffer, don't
				// display the buffer path
				if((prevBuffCount != 0 && buffers.size() != 0) &&
					(prevBuffCount == 1 || buffers.size() == 1) &&
					(prevBuffCount != buffers.size()))
				{
					fireTableStructureChanged();
				}

			}// end if view == this.view
		}
		else if(message instanceof PropertiesChanged)
		{
			propertiesChanged();
		}
	}

	public String getColumnName(int col)
	{
		return colNames[col];
	}

	public int getRowCount()
	{
		return tasks.size();
	}

	public int getColumnCount()
	{
		// only display buffer name if showing more than one buffer
		return (buffers.size() <= 1 ? 3 : 4);
	}

	public Object getValueAt(int row, int col)
	{
		Task task = (Task)tasks.elementAt(row);
		switch(col)
		{
			case 0:
				return task.getIcon();
			case 1:
				return new Integer(task.getLine() + 1);
			case 2:
				return task.getText();
			case 3:
				switch(bufferDisplay)
				{
					case NAME_DIR:
						return task.getBuffer().toString();
					case NAME_ONLY:
						return task.getBuffer().getName();
					default:
						return task.getBuffer().getPath();
				}
			default:
				return null;
		}
	}

	public Class getColumnClass(int col)
	{
		switch(col)
		{
			case 0:
				return Icon.class;
			case 1:
				return Integer.class;
			default:
				return String.class;
		}
	}

	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	public void setValueAt(Object value, int row, int col)
	{
		return;
	}

	/**
	* Returns the current buffer display style
	* (default to 'name (dir)')
	*/
	private int getBufferDisplay()
	{
		String _bufferDisplay = jEdit.getProperty(
			"tasklist.buffer.display");

		//Log.log(Log.DEBUG, TaskListModel.class,
		//	"_bufferDisplay=" + _bufferDisplay);//##

		if(_bufferDisplay == "" || _bufferDisplay == null)
		{
			return NAME_DIR;
		}
		else if(_bufferDisplay.equals(jEdit.getProperty(
				"options.tasklist.general.buffer.display.nameonly")))
		{
			return NAME_ONLY;
		}
		else if(_bufferDisplay.equals(jEdit.getProperty(
				"options.tasklist.general.buffer.display.fullpath")))
		{
			return FULL_PATH;
		}
		else
		{
			return NAME_DIR;
		}
	}


	private void propertiesChanged()
	{
		int _bufferDisplay = getBufferDisplay();
		if(bufferDisplay != _bufferDisplay)
		{
			// QUESTION: is fire table data changed needed?
			bufferDisplay = _bufferDisplay;
		}
	}
}
