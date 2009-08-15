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

package tasklist;

//{{{ imports
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
//}}}

/**
 * A TaskListModel uses a Buffer as its underlying data model.  This list model
 * will only show tasks for its buffer, that is, there is a one to one relationship
 * between a TaskListModel and a Buffer.
 * danson, 13 Aug 2009, I changed the way this model works.  It used to get registered
 * with TaskListPlugin as a task listener, then would respond to calls to "taskAdded",
 * "taskRemoved", and "taskUpdated".  Now I have it calling TaskListPlugin to fetch
 * the tasks for this model's buffer and ignores tasks for all other buffers.
 */
public class TaskListModel extends AbstractTableModel implements EBComponent {

	//{{{ private static members
	private static final String[] colNames = {
	            "",
	            jEdit.getProperty( "tasklist.tableheader.lineno", "Line #" ),
	            jEdit.getProperty( "tasklist.tableheader.description", "Description" )
	        };

	//{{{ private members
	private Buffer buffer;
	private final List<Task> tasks;
	private	int sortCol;
	private boolean sortAscending;
	//}}}


	//{{{ constructor
	/**
	 * Constructs a TaskListModel object
	 *
	 * @param view the View with which the model is associated
	 */
	public TaskListModel( Buffer buffer ) {
		this.buffer = buffer;

		tasks = new ArrayList<Task>();

		// NOTE:  default sort column is column 1 (line number)
		try {
			this.sortCol = Integer.parseInt( jEdit.getProperty( "tasklist.table.sort-column", "1" ) );
		}
		catch ( NumberFormatException e ) {
			this.sortCol = 1;
		}
		this.sortAscending = jEdit.getBooleanProperty( "tasklist.table.sort-ascending" );

		//TaskListPlugin.extractTasks( buffer );
		loadTasks();

	} //}}}

	//{{{ setSortCol(int sortCol) method
	public void setSortCol( int sortCol ) {
		this.sortCol = sortCol;
	} //}}}

	//{{{ setSortAscending(boolean sortAscending) method
	public void setSortAscending( boolean sortAscending ) {
		this.sortAscending = sortAscending;
	} //}}}

	//{{{ getSortCol() method
	public int getSortCol() {
		return sortCol;
	} //}}}

	//{{{ getSortAscending() method
	public boolean getSortAscending() {
		return sortAscending;
	} //}}}

	public void setBuffer( Buffer buffer ) {
		//if (buffer != null && !buffer.equals(this.buffer)) {
		this.buffer = buffer;
		tasks.clear();
		TaskListPlugin.extractTasks( buffer );
		//}
	}

	public Buffer getBuffer() {
		return buffer;
	}

	//{{{ elementAt(int row) method
	/**
	* Returns Task at `row`
	* @param row the row index of task
	*/
	public Task elementAt( int row ) {
		return tasks.get( row );
	} //}}}


	//{{{ taskAdded(Task task) method
	public void taskAdded( Task task ) {
		if ( task == null ) {
			return ;
		}
		if ( buffer.equals( task.getBuffer() ) ) {
			tasks.add( task );
			fireTableRowsInserted( tasks.size() - 1, tasks.size() - 1 );
		}
	} //}}}

	//{{{ taskRemoved(Task task) method
	public void taskRemoved( Task task ) {
		if ( task == null ) {
			return ;
		}
		if ( buffer.equals( task.getBuffer() ) ) {
			int index = tasks.indexOf( task );
			if ( index >= 0 ) {
				tasks.remove( index );
				fireTableRowsDeleted( index, index );
			}
		}
	} //}}}

	//{{{ handleMessage(EBMessage message) method
	public void handleMessage( EBMessage message ) {
		if ( message instanceof BufferUpdate ) {
			BufferUpdate bu = ( BufferUpdate ) message;
			if ( buffer.equals( bu.getBuffer() ) && BufferUpdate.SAVED.equals( bu.getWhat() ) ) {
				// on Buffer save, reparse and reload the tasks for the Buffer.
				loadTasks();
			}
		}
	} //}}}

	private void loadTasks() {
		int size = tasks.size() - 1;
		tasks.clear();
		fireTableRowsDeleted( 0, size );
		TaskListPlugin.parseBuffer( buffer );
		HashMap<Integer, Task> tasks = TaskListPlugin.requestTasksForBuffer( buffer );
		if ( tasks != null && tasks.size() > 0 ) {
			for ( Iterator it = tasks.values().iterator(); it.hasNext(); ) {
				Task task = ( Task ) it.next();
				taskAdded( task );
			}
		}
	}

	//{{{ getColumnName(int c) method
	public String getColumnName( int col ) {
		return colNames[ col ];
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount() {
		return tasks.size();
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount() {
		return 3;
	} //}}}

	//{{{ getValueAt(int r, int c) method
	public Object getValueAt( int row, int col ) {
		Task task = tasks.get( row );
		switch ( col ) {
			case 0:
				return task.getIcon();
			case 1:
				return Integer.valueOf( task.getLineNumber() + 1 );
			case 2:
				return task.getText();
			default:
				return null;
		}
	} //}}}

	//{{{ getColumnClass(int col) method
	public Class getColumnClass( int col ) {
		switch ( col ) {
			case 0:
				return Icon.class;
			case 1:
				return Integer.class;
			default:
				return String.class;
		}
	} //}}}

	//{{{ isCellEditable(int row, int col) method
	public boolean isCellEditable( int row, int col ) {
		return false;
	} //}}}

	//{{{ setValueAt(Object value, int r, int col) method
	public void setValueAt( Object value, int row, int col ) {} //}}}

	//{{{ sort() method
	public void sort() {
		sort( sortCol, sortAscending );
	} //}}}

	//{{{ sort(int col, boolean sortAscending) method
	public void sort( int sortCol, boolean sortAscending ) {
		// DEBUG: get sort parameters
		//Log.log(Log.DEBUG, TaskListModel.class, "sorting TaskList items: "
		//	+ "sortCol = " + String.valueOf(sortCol)
		//	+ ", SortAscending = " + String.valueOf(sortAscending));
		Collections.sort( tasks, new ColumnSorter( sortCol, sortAscending ) );
		fireTableDataChanged();
	} //}}}

	//{{{ ColumnSorter class
	/**
	 * A class to perform comparisons on task data; available sortings are
	 * by tasks type
	 * and line number and by line number only
	 *
	 * @author John Gellene (jgellene@nyc.rr.com)
	 */
	class ColumnSorter implements Comparator<Task> {
		private final int LINENUMBER = 0;
		private final int TASKTAG = 1;
		private final int BUFFER = 2;

		private int sortType;
		private final boolean ascending;

		//{{{ constructor
		public ColumnSorter( int col, boolean ascending ) {
			if ( col == 1 )
				this.sortType = LINENUMBER;
			else if ( col == 0 || col == 2 )
				this.sortType = TASKTAG;
			else
				this.sortType = BUFFER;
			this.ascending = ascending;
		} //}}}

		//{{{ compare() method
		public int compare( Task task1, Task task2 ) {
			int result = 0;

			if ( sortType == TASKTAG ) {
				// sort based on identifiers
				String id1 = task1.getIdentifier();
				String id2 = task2.getIdentifier();
				result = id1.compareTo( id2 );
			}
			else if ( sortType == BUFFER ) {
				String b1 = task1.getBuffer().toString();
				String b2 = task2.getBuffer().toString();
				result = b1.compareTo( b2 );
			}

			if ( result == 0 ) {
				result = task1.getLineIndex() - task2.getLineIndex();
			}

			if ( !ascending ) {
				result = -result;
			}

			return result;
		} //}}}

	} //}}}
}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4: