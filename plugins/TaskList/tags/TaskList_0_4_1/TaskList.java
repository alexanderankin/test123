/*
 * TaskList.java - TaskList plugin
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
 */

//{{{ imports
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
//}}}

/**
 * A dockable component contaning a scrollable table; the table contains
 * data on task items found by parsing one or more buffers.
 *
 * @author Oliver Rutherfurd
 */
public class TaskList extends JPanel implements EBComponent
{

	//{{{ constructor
	/**
	 * Constructor
	 *
	 * @param view The view in which the TaskList component will appear
	 */
	public TaskList(View view)
	{
		super(new BorderLayout());

		this.view = view;
		this.taskListModel = new TaskListModel(view);

		table = new TaskListTable();

		add(BorderLayout.CENTER, new JScrollPane(table));
	}//}}}

	//{{{ TaskListTable class
	/**
	 * The table containing data on task items
	 */
	class TaskListTable extends JTable
	{
		private boolean init = false;

		//{{{ constructor
		TaskListTable()
		{
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setCellSelectionEnabled(false);
			setRowSelectionAllowed(true);
			// NOTE:  a single cell renderer that does not indicate cell focus
			setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					Component c = super.getTableCellRendererComponent(table, value,
						isSelected, false, row, column);
					int horizAlignment = SwingConstants.LEFT;
					if(column == 0)
						horizAlignment = SwingConstants.CENTER;
					else if(column == 1)
						horizAlignment = SwingConstants.RIGHT;
					((JLabel)c).setHorizontalAlignment(horizAlignment);
					return c;
				}
			});
			setDefaultRenderer(Image.class, null);
			setDefaultRenderer(Number.class, null);

			setModel(TaskList.this.taskListModel);
			//setRowHeight(18);
			setShowVerticalLines(jEdit.getBooleanProperty("tasklist.table.vertical-lines"));
			setShowHorizontalLines(jEdit.getBooleanProperty("tasklist.table.horizontal-lines"));
			//setIntercellSpacing(new Dimension(0,1));
			MouseHandler handler = new MouseHandler();
			addMouseListener(handler);

			// TODO: Fix height of header using Windows L&F
			if(getTableHeader() != null)
			{
				getTableHeader().setReorderingAllowed(false);
				getTableHeader().setResizingAllowed(true);
				getTableHeader().addMouseListener(handler);
				Dimension dim = getTableHeader().getPreferredSize();
				dim.height = getRowHeight();
				getTableHeader().setPreferredSize(dim);
			}
			init = true;
			resizeTable();
			sort();
		}//}}}

		//{{{ getTaskListModel() method
		public TaskListModel getTaskListModel()
		{
			return (TaskListModel)getModel();
		}//}}}

		//{{{ tableChanged() method
		/**
		 * Calls resizeTable() when the number of table columns change
		 *
		 * @param e The TableModelEvent represeting the change in the table's state
		 */
		public void tableChanged(TableModelEvent e)
		{
			super.tableChanged(e);

			if(e.getFirstRow() == e.HEADER_ROW && init)
			{
				try
				{
					resizeTable();
				}
				catch(Exception ex)
				{
					Log.log(Log.ERROR, TaskList.class, ex);
				}
			}
		}//}}}

		//{{{ resizeTable() method
		/**
		 * Re-sizes the columns in the table - called when cols are
		 * added or removed.
		 */
		void resizeTable()
		{
			TableColumnModel columnModel = getColumnModel();

			columnModel.getColumn(0).setMinWidth(20);
			columnModel.getColumn(0).setPreferredWidth(20);
			columnModel.getColumn(0).setMaxWidth(20);
			columnModel.getColumn(0).setResizable(false);
			columnModel.getColumn(1).setMinWidth(50);
			columnModel.getColumn(1).setMaxWidth(80);
			columnModel.getColumn(1).sizeWidthToFit();
			columnModel.getColumn(1).setResizable(false);
			columnModel.getColumn(2).setMinWidth(200);
			columnModel.getColumn(2).setPreferredWidth(1000);
		if(columnModel.getColumnCount() == 4)
			{
				columnModel.getColumn(3).setMinWidth(100);
				columnModel.getColumn(3).setPreferredWidth(500);
			}
			getTableHeader().resizeAndRepaint();
		}//}}}

		//{{{ sort() method
		private void sort()
		{
			getTaskListModel().sort();
		}//}}}

		//{{{ sort(int col, boolean ascending) method
		private void sort(int col, boolean ascending)
		{
			getTaskListModel().sort(col, ascending);
		}//}}}

	}//}}}

	//{{{ MouseHandler class
	/**
	 * Responds to mouse clicks in the table or its header row
	 */
	class MouseHandler extends MouseAdapter
	{
		//{{{ mouseClicked() method
		/**
		 * Calls handling routine based on number, type and location
		 * of mouse clicks
		 * @param e The MouseEvent being handled
		 */
		public void mouseClicked(MouseEvent e)
		{
			Buffer buffer = view.getBuffer();
			if(buffer.isDirty() && e.getClickCount() == 1)
			{
				TaskListPlugin.extractTasks(view.getBuffer());
			}
			Point p = e.getPoint();
			final int rowNum = table.rowAtPoint(p);
			if(e.getClickCount() == 1 &&
				(e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			{
				e.consume();
				showPopup(view, rowNum, p);
			}
			else if(e.getClickCount() > 1)
			{
				if(e.getComponent() == table.getTableHeader())
				{
					TaskListModel model = table.getTaskListModel();
					int sortCol = table.columnAtPoint(p);
					switch(sortCol)
					{
						case 0:
							sortCol = 2;
						case 1:
						case 2:
						{
							if(model.getSortCol() == sortCol)
								model.setSortAscending(!model.getSortAscending());
							else
								model.setSortCol(sortCol);
							break;
						}
						default:
						{
							return;
						}
					}
					model.sort();
				}
				else if(rowNum > -1)
				{
					table.setRowSelectionInterval(rowNum, rowNum);
					showTaskText(rowNum);
				}
			}
			else if(e.getClickCount() == 1)
			{
				if(e.getComponent() == table.getTableHeader())
					return;

				if(TaskListPlugin.getAllowSingleClickSelection())
				{
					table.setRowSelectionInterval(rowNum,rowNum);
					showTaskText(rowNum);
				}
			}
		}//}}}

		//{{{ showPopup(View view, int row, Point p) method
		/**
		 * Causes a popup context menu to be shown
		 * @param view he View in which the TaskList component appears
		 * @param row The table row clicked by the mouse
		 * @param p The Point within the TaskList's table object clicked by the mouse
		 */
		private void showPopup(final View view, final int row, Point p)
		{
			TaskListPopup popup = new TaskListPopup(view, TaskList.this, row);
			// NOTE: keep within screen limits; use task list panel, not table
			SwingUtilities.convertPointToScreen(p, table);
			SwingUtilities.convertPointFromScreen(p, TaskList.this);
			Dimension dt = TaskList.this.getSize();
			Dimension dp = popup.getPreferredSize();
			if (p.x + dp.width > dt.width)
				p.x = dt.width - dp.width;
			if (p.y + dp.height > dt.height)
				p.y = dt.height - dp.height;
			popup.show(TaskList.this, p.x+1, p.y+1);
		}//}}}

		//{{{ showTaskText(int row) method
		/**
		 * Locates and displays buffer text corresponding to the selected row of the TaskList's table component
		 *
		 * @param row The selected row of the TaskList table
		 */
		private void showTaskText(final int row)
		{
			// NOTE: get EditPane of buffer clicked, goto selection
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Task task = (Task)taskListModel.elementAt(row);
					EditPane[] editPanes = view.getEditPanes();
					Buffer buffer = task.getBuffer();
					for(int i = 0; i < editPanes.length; i++)
					{
						if(editPanes[i].getBuffer() == buffer)
						{
							JEditTextArea textArea = editPanes[i].getTextArea();
							textArea.setCaretPosition(textArea.getLineStartOffset(task.getLineNumber()) + task.getStartOffset());
							textArea.scrollToCaret(true);
							textArea.grabFocus();

							break;
						}
					}
				}
			});
		}//}}}

	}//}}}

	//{{{ getName() method
	/**
	 * Property accessor required by jEdit Plugin API
	 * @return The plugin's name property
	 */
	public String getName()
	{
		return TaskListPlugin.NAME;
	}//}}}

	//{{{ getComponent() method
	/**
	 * Property accessor required by jEdit Plugin API
	 * @return A reference to the TaskList object
	 */
	public Component getComponent()
	{
		return this;
	}//}}}

	//{{{ handleMessage(EBMessage msg) method
	/**
	 * Message handling routine required by the jEdit Plugin API
	 *
	 * @param message The EBMessage received from the EditBus
	 */
	public void handleMessage(EBMessage message)
	{
		if(message instanceof PropertiesChanged)
		{
			table.setShowVerticalLines(
				jEdit.getBooleanProperty("tasklist.table.vertical-lines"));
			table.setShowHorizontalLines(
				jEdit.getBooleanProperty("tasklist.table.horizontal-lines"));
		}
	}//}}}

	//{{{ addNotify() method
	/**
	 * Adds the TaskList and its table's data model to the EditBus
	 * to listen for messages; registers the data model to be notified when
	 * tasks are added or removed.
	 */
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
		EditBus.addToBus(taskListModel);
		TaskListPlugin.addTaskListener(taskListModel);
	}//}}}

	//{{{ removeNotify() method
	/**
	 * Removes the TaskList and its table's data model from the EditBus;
	 * removes the data model form the list of components that listen for
	 * the addition or removal of tasks items
	 */
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
		EditBus.removeFromBus(taskListModel);

		// table model doesn't need to be notified when task are added/removed
		TaskListPlugin.removeTaskListener(taskListModel);
	}//}}}

	//{{{ members
	/**
	 * The view in which the TaskList component appears
	 */
	private View view;
	/**
	 * The table display task items; given package access
	 * to allow for calls by a TaskListPopup object.
	 */
	TaskListTable table;
	/**
	 * The data model for the TaskList's table; given package access
	 * to allow for calls by a TaskListPopup object.
	 */
	TaskListModel taskListModel;
	//}}}
}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
