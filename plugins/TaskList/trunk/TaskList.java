/*
 * TaskList.java - TaskList plugin
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
 */


// TODO: remove unused packages
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;

public class TaskList extends JPanel
	implements DockableWindow, EBComponent
{

	public TaskList(View view)
	{
		super(new BorderLayout());

		this.view = view;

		table = new TaskListTable();

		add(BorderLayout.CENTER, new JScrollPane(table));
	}

	class TaskListTable extends JTable
	{
		private boolean init = false;

		TaskListTable()
		{
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			addMouseListener(new MouseHandler());
			taskListModel = new TaskListModel(view);
			setModel(taskListModel);
			setRowHeight(18);
			setShowVerticalLines(jEdit.getBooleanProperty("tasklist.table.vertical-lines"));
			setShowHorizontalLines(jEdit.getBooleanProperty("tasklist.table.horizontal-lines"));
			setIntercellSpacing(new Dimension(0,1));

			if(getTableHeader() != null)
			{
				getTableHeader().setReorderingAllowed(false);
			}
			init = true;
			resizeTable();
		}

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
		}

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
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			Point p = e.getPoint();
			final int rowNum = table.rowAtPoint(p);
			if(e.getClickCount() == 1 &&
				(e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			{
				e.consume();
				table.setRowSelectionInterval(rowNum, rowNum);
				showPopup(view, rowNum, p);
			}
			else if(e.getClickCount() > 1)
			{
				if(rowNum == -1)
					TaskListPlugin.parseBuffer(view.getBuffer());
				else
					showTaskText(rowNum);
			}
		}

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
		}

		private void showTaskText(final int row)
		{
			//get EditPane of buffer clicked, goto selection
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
							textArea.setCaretPosition(textArea.getLineStartOffset(task.getLine()) + task.getStartOffset());
							textArea.scrollToCaret(true);
							textArea.grabFocus();

							break;
						}
					}
				}
			});

		}
	}

	public String getName()
	{
		return TaskListPlugin.NAME;
	}

	public Component getComponent()
	{
		return this;
	}

	public void handleMessage(EBMessage message)
	{
		if(message instanceof PropertiesChanged)
		{
			table.setShowVerticalLines(
				jEdit.getBooleanProperty("tasklist.table.vertical-lines"));
			table.setShowHorizontalLines(
				jEdit.getBooleanProperty("tasklist.table.horizontal-lines"));
		}
		// QUESTION: what other messages need to be handled here?
	}

	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
		EditBus.addToBus(taskListModel);

		// register table model to be notified when task are added/removed
		TaskListPlugin.addTaskListener(taskListModel);
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
		EditBus.removeFromBus(taskListModel);

		// table model doesn't need to be notified when task are added/removed
		TaskListPlugin.removeTaskListener(taskListModel);
	}

	private View view;
	TaskListModel taskListModel;
	JTable table;
}

