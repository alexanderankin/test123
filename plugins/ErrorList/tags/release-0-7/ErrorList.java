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

		getContentPane().add(BorderLayout.CENTER,createListScroller());

		EditBus.addToBus(this);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowHandler());

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
	private DefaultListModel errorModel;
	private JList errorList;

	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();

		if(what == ErrorSourceUpdate.ERROR_ADDED)
		{
			errorModel.addElement(message.getError());
		}
		else if(what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			errorModel.removeElement(message.getError());
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
		errorModel = new DefaultListModel();

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
					errorModel.addElement(errors[j]);
				}
			}
		}

		errorList = new JList(errorModel);
		errorList.setCellRenderer(new ErrorCellRenderer());
		errorList.addListSelectionListener(new ListHandler());

		JScrollPane scroller = new JScrollPane(errorList);
		scroller.setPreferredSize(new Dimension(640,300));

		return scroller;
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

	class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			if(evt.getValueIsAdjusting())
				return;

			ErrorSource.Error error = (ErrorSource.Error)
				errorList.getSelectedValue();

			Buffer buffer = error.getBuffer();
			if(buffer == null)
			{
				buffer = jEdit.openFile(view,null,
					error.getFilePath(),false,false);
			}
			view.setBuffer(buffer);
			view.toFront();
			view.requestFocus();

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
	}

	class WindowHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent evt)
		{
			close();
		}
	}
}
