/*
 * ErrorList.java - Error list window
 * Copyright (C) 1999, 2000 Slava Pestov
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.*;

public class ErrorList extends JFrame implements EBComponent
{
	public static final ImageIcon ERROR_ICON = new ImageIcon(
		ErrorList.class.getResource("TrafficRed.gif"));
	public static final ImageIcon WARNING_ICON = new ImageIcon(
		ErrorList.class.getResource("TrafficYellow.gif"));

	public ErrorList(View view)
	{
		super(jEdit.getProperty("error-list.title"));

		this.view = view;

		getContentPane().add(BorderLayout.NORTH,status = new JLabel());
		getContentPane().add(BorderLayout.CENTER,createListScroller());
		updateStatus();

		EditBus.addToBus(this);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowHandler());

		setIconImage(GUIUtilities.getPluginIcon());

		pack();
		GUIUtilities.loadGeometry(this,"error-list");
		show();
	}

	public void handleMessage(EBMessage message)
	{
		if(message instanceof ErrorSourceUpdate)
			handleErrorSourceMessage((ErrorSourceUpdate)message);
		else if(message instanceof ViewUpdate)
			handleViewMessage((ViewUpdate)message);
	}

	public void close()
	{
		EditBus.removeFromBus(this);
		ErrorListPlugin.closeErrorList(view);
		GUIUtilities.saveGeometry(this,"error-list");
		dispose();
	}

	// private members
	private View view;
	private JLabel status;
	private DefaultListModel errorModel;
	private JList errorList;

	private void updateStatus()
	{
		int warningCount = 0;
		int errorCount = 0;
		for(int i = 0; i < errorModel.getSize(); i++)
		{
			ErrorSource.Error error = (ErrorSource.Error)
				errorModel.getElementAt(i);
			if(error.getErrorType() == ErrorSource.ERROR)
				errorCount++;
			else
				warningCount++;
		}

		Integer[] args = { new Integer(errorCount),
			new Integer(warningCount) };
		status.setText(jEdit.getProperty("error-list.status",args));
	}

	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();

		if(what == ErrorSourceUpdate.ERROR_ADDED)
		{
			errorModel.addElement(message.getError());
			updateStatus();
		}
		else if(what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			errorModel.removeElement(message.getError());
			updateStatus();
		}
		else if(what == ErrorSourceUpdate.ERRORS_CLEARED)
		{
			ErrorSource source = message.getErrorSource();
			for(int i = errorModel.getSize() - 1; i >= 0; i--)
			{
				if(((ErrorSource.Error)errorModel.getElementAt(i))
					.getErrorSource() == source)
					errorModel.removeElementAt(i);
			}

			updateStatus();
		}
	}

	private void handleViewMessage(ViewUpdate message)
	{
		if(message.getWhat() == ViewUpdate.CLOSED)
		{
			if(message.getView() == view)
				view = null;
		}
	}

	private JScrollPane createListScroller()
	{
		Vector errorVector = new Vector();

		Object[] sources = EditBus.getNamedList(ErrorSource
			.ERROR_SOURCES_LIST);
		if(sources != null)
		{
			for(int i = 0; i < sources.length; i++)
			{
				ErrorSource source = (ErrorSource)sources[i];
				ErrorSource.Error[] errors = source.getAllErrors();
				if(errors == null)
					continue;
				for(int j = 0; j < errors.length; j++)
				{
					errorVector.addElement(errors[j]);
				}
			}
		}

		MiscUtilities.quicksort(errorVector,new ErrorCompare());
		errorModel = new DefaultListModel();
		for(int i = 0; i < errorVector.size(); i++)
		{
			errorModel.addElement(errorVector.elementAt(i));
		}

		errorList = new JList(errorModel);
		errorList.setCellRenderer(new ErrorCellRenderer());
		errorList.addMouseListener(new MouseHandler());

		JScrollPane scroller = new JScrollPane(errorList);
		scroller.setPreferredSize(new Dimension(640,300));

		return scroller;
	}

	class ErrorCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			ErrorSource.Error err1 = (ErrorSource.Error)obj1;
			ErrorSource.Error err2 = (ErrorSource.Error)obj2;

			String path1 = err1.getFilePath();
			String path2 = err2.getFilePath();
			int comp = path1.compareTo(path2);
			if(comp != 0)
				return comp;

			int line1 = err1.getLineNumber();
			int line2 = err2.getLineNumber();
			return line1 - line2;
		}
	}

	class ErrorCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(
			JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus)
		{
			ErrorSource.Error error = (ErrorSource.Error)value;

			// XXX: we depend on DefaultLCR internals here
			super.getListCellRendererComponent(list,error,
				index,isSelected,cellHasFocus);

			setIcon(error.getErrorType() == ErrorSource.WARNING ?
				WARNING_ICON : ERROR_ICON);

			return this;
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			int index = errorList.locationToIndex(evt.getPoint());
			if(index == -1)
				return;

			final ErrorSource.Error error = (ErrorSource.Error)
				errorModel.getElementAt(index);

			final Buffer buffer;
			if(error.getBuffer() != null)
				buffer = error.getBuffer();
			else
			{
				buffer = jEdit.openFile(view,null,
					error.getFilePath(),false,false);
				if(buffer == null)
					return;
			}

			view.toFront();
			view.requestFocus();

			VFSManager.runInAWTThread(new Runnable()
			{
				public void run()
				{
					view.setBuffer(buffer);

					int start = error.getStartOffset();
					int end = error.getEndOffset();

					int lineNo = error.getLineNumber();
					Element line = buffer.getDefaultRootElement()
						.getElement(lineNo);
					if(line != null)
					{
						start += line.getStartOffset();
						if(end == 0)
							end = line.getEndOffset() - 1;
						else
							end += line.getStartOffset();
					}

					view.getTextArea().select(start,end);
				}
			});
		}
	}

	class WindowHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent evt)
		{
			close();
		}
	}
}
