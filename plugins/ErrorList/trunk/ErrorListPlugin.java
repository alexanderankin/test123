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

import java.awt.Color;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class ErrorListPlugin extends EBPlugin
{
	public void start()
	{
		jEdit.addAction(new error_list());
		propertiesChanged();
	}

	public void createMenuItems(View view, Vector menus, Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenuItem(view,"error-list"));
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
		else if(message instanceof PropertiesChanged)
			propertiesChanged();
	}

	// package-private members
	static boolean isErrorListShowing(View view)
	{
		if(errorLists == null)
			return false;
		else
			return (errorLists.get(view) != null);
	}

	static ErrorList getErrorList(View view)
	{
		if(errorLists == null)
			errorLists = new Hashtable();

		ErrorList list = (ErrorList)errorLists.get(view);
		if(list != null)
		{
			list.toFront();
			list.requestFocus();
			return list;
		}

		list = new ErrorList(view);
		errorLists.put(view,list);
		return list;
	}

	static void closeErrorList(View view)
	{
		errorLists.remove(view);
	}

	static Color getErrorColor(int type)
	{
		return (type == ErrorSource.WARNING ? warningColor : errorColor);
	}

	// private members
	private static Hashtable errorLists;
	private static boolean showOnError;
	private static boolean showOnStartup;
	private static Color warningColor;
	private static Color errorColor;

	private static void propertiesChanged()
	{
		showOnError = "yes".equals(jEdit.getProperty("error-list.showOnError"));
		warningColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.warningColor"));
		errorColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.errorColor"));
	}

	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();
		if(what == ErrorSourceUpdate.ERROR_ADDED
			|| what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			if((errorLists == null || errorLists.size() == 0)
				&& showOnError)
			{
				View view = jEdit.getFirstView();
				if(view == null)
					showOnStartup = true;
				else
					getErrorList(jEdit.getFirstView());
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
					JEditTextArea[] textAreas = view.getTextAreas();
					for(int i = 0; i < textAreas.length; i++)
					{
						textAreas[i].getPainter()
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
				JEditTextArea[] textAreas = view.getTextAreas();
				for(int i = 0; i < textAreas.length; i++)
				{
					textAreas[i].getPainter().repaint();
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
				getErrorList(message.getView());
			}
		}
		else if(message.getWhat() == ViewUpdate.TEXTAREA_CREATED)
		{
			ErrorHighlight highlight = new ErrorHighlight();
			message.getTextArea().getPainter().addCustomHighlight(highlight);
		}
	}
}
