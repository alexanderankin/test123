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
	implements DockableWindow
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
			setShowVerticalLines(false);
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
			if(e.getClickCount() < 2)
				return;
			final int rowNum = table.rowAtPoint(e.getPoint());
			if(rowNum == -1)
				return;

			//get EditPane of buffer clicked, goto selection
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Task task = (Task)taskListModel.elementAt(rowNum);
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


	/**
	* Re-sizes the columns in the table - called when cols are
	* added or removed.
	*/
//	private void resizeTable()
//	{
//		TableColumnModel columnModel = table.getColumnModel();
//
//		columnModel.getColumn(0).setMinWidth(20);
//		columnModel.getColumn(0).setPreferredWidth(20);
//		columnModel.getColumn(0).setMaxWidth(20);
//		columnModel.getColumn(0).setResizable(false);
//		columnModel.getColumn(1).setMinWidth(50);
//		columnModel.getColumn(1).setMaxWidth(80);
//		columnModel.getColumn(1).sizeWidthToFit();
//		columnModel.getColumn(1).setResizable(false);
//		columnModel.getColumn(2).setMinWidth(200);
//		columnModel.getColumn(2).setPreferredWidth(1000);
//		if(columnModel.getColumnCount() == 4)
//		{
//			columnModel.getColumn(3).setMinWidth(100);
//			columnModel.getColumn(3).setPreferredWidth(500);
//		}
//	}

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
		// QUESTION: what messages need to be handled here?
	}

	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(taskListModel);

		// register table model to be notified when task are added/removed
		TaskListPlugin.addTaskListener(taskListModel);
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(taskListModel);

		// table model doesn't need to be notified when task are added/removed
		TaskListPlugin.removeTaskListener(taskListModel);
	}

	private View view;
	private TaskListModel taskListModel;
	private JTable table;
}

