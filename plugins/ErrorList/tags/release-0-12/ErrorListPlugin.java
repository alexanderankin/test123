/*
 * ErrorListPlugin.java - Error list plugin
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

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;

public class ErrorListPlugin extends EBPlugin
{
	public static final String NAME = "error-list";

	public void start()
	{
		jEdit.addAction(new OpenAction());
		jEdit.addAction(new NextErrorAction());
		jEdit.addAction(new PreviousErrorAction());
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,NAME);
		propertiesChanged();
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("error-list-menu"));
	}

	public void createOptionPanes(OptionsDialog dialog)
	{
		dialog.addOptionPane(new ErrorListOptionPane());
	}

	public void handleMessage(EBMessage message)
	{
		if(message instanceof ErrorSourceUpdate)
			handleErrorSourceMessage((ErrorSourceUpdate)message);
		else if(message instanceof ViewUpdate)
			handleViewMessage((ViewUpdate)message);
		else if(message instanceof EditPaneUpdate)
			handleEditPaneMessage((EditPaneUpdate)message);
		else if(message instanceof PropertiesChanged)
			propertiesChanged();
		else if(message instanceof CreateDockableWindow)
			handleCreateDockableMessage((CreateDockableWindow)message);
	}

	static Color getErrorColor(int type)
	{
		return (type == ErrorSource.WARNING ? warningColor : errorColor);
	}

	// private members
	private static boolean showOnError;
	private static boolean showOnStartup;
	private static Color warningColor;
	private static Color errorColor;

	private void propertiesChanged()
	{
		showOnError = jEdit.getBooleanProperty("error-list.showOnError");
		warningColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.warningColor"));
		errorColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.errorColor"));
	}

	private void showErrorList(View view)
	{
		DockableWindowManager dockableWindowManager = view.getDockableWindowManager();
		if(!dockableWindowManager.isDockableWindowVisible(NAME))
			dockableWindowManager.addDockableWindow(NAME);
	}

	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();
		if(what == ErrorSourceUpdate.ERROR_ADDED
			|| what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			if(showOnError)
			{
				View view = jEdit.getFirstView();
				if(view == null)
					showOnStartup = true;
				else
					showErrorList(view);
			}

			ErrorSource.Error error = message.getError();
			Buffer buffer = error.getBuffer();
			if(buffer == null)
				return;

			int lineNumber = error.getLineNumber();

			View view = jEdit.getFirstView();
			while(view != null)
			{
				if(view.getBuffer() == buffer)
				{
					EditPane[] editPanes = view.getEditPanes();
					for(int i = 0; i < editPanes.length; i++)
					{
						editPanes[i].getTextArea().getPainter()
							.invalidateLine(lineNumber);
					}
				}

				view = view.getNext();
			}
		}
		else if(what == ErrorSourceUpdate.ERRORS_CLEARED)
		{
			View view = jEdit.getFirstView();
			while(view != null)
			{
				EditPane[] editPanes = view.getEditPanes();
				for(int i = 0; i < editPanes.length; i++)
				{
					editPanes[i].getTextArea().getPainter()
						.repaint();
				}
				view = view.getNext();
			}
		}
	}

	private void handleViewMessage(ViewUpdate message)
	{
		if(message.getWhat() == ViewUpdate.CREATED)
		{
			if(showOnStartup)
			{
				showOnStartup = false;
				showErrorList(message.getView());
			}
		}
	}

	private void handleEditPaneMessage(EditPaneUpdate message)
	{
		if(message.getWhat() == EditPaneUpdate.CREATED)
		{
			ErrorHighlight highlight = new ErrorHighlight();
			message.getEditPane().getTextArea().getPainter()
				.addCustomHighlight(highlight);
		}
	}

	private void handleCreateDockableMessage(CreateDockableWindow message)
	{
		if(message.getDockableWindowName().equals(NAME))
			message.setDockableWindow(new ErrorList(message.getView()));
	}

	public class OpenAction extends EditAction
	{
		public OpenAction()
		{
			super("error-list");
		}

		public void actionPerformed(ActionEvent evt)
		{
			getView(evt).getDockableWindowManager().toggleDockableWindow(NAME);
		}
	
		public boolean isToggle()
		{
			return true;
		}
	
		public boolean isSelected(Component comp)
		{
			return getView(comp).getDockableWindowManager()
				.isDockableWindowVisible(NAME);
		}
	}

	public class NextErrorAction extends EditAction
	{
		public NextErrorAction()
		{
			super("error-list-next-error");
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			DockableWindowManager wm = getView(evt).getDockableWindowManager();
			wm.addDockableWindow(NAME);
			DockableWindow win = wm.getDockableWindow(NAME);
			((ErrorList)win).nextError();
		}
	}

	public class PreviousErrorAction extends EditAction
	{
		public PreviousErrorAction()
		{
			super("error-list-previous-error");
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			DockableWindowManager wm = getView(evt).getDockableWindowManager();
			wm.addDockableWindow(NAME);
			DockableWindow win = wm.getDockableWindow(NAME);
			((ErrorList)win).previousError();
		}
	}
}
