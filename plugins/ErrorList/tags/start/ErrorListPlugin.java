/*
 * ErrorListPlugin.java - Error list plugin
 * Copyright (C) 1999 Slava Pestov
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

import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

public class ErrorListPlugin extends EBPlugin
{
	public void start()
	{
		jEdit.addAction(new error_list());

		DefaultErrorSource test = new DefaultErrorSource("Test");
		test.addError(ErrorSource.ERROR,"/tmp/bob",10,0,0,"testing error");
		test.addError(ErrorSource.WARNING,"/tmp/bob",11,0,0,"testing warning");
		test.addError(ErrorSource.ERROR,"/tmp/bob",12,0,0,"testing error");
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,test);
		EditBus.addToBus(test);
	}

	public void createMenuItems(View view, Vector menus, Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenuItem(view,"error-list"));
	}

	public void handleMessage(EBMessage message)
	{
		if(message instanceof ErrorSourceUpdate)
			handleErrorSourceMessage((ErrorSourceUpdate)message);
		else if(message instanceof ViewUpdate)
			handleViewMessage((ViewUpdate)message);
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

	// private members
	private static Hashtable errorLists;

	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();
		if(what == ErrorSourceUpdate.ERROR_ADDED
			|| what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			ErrorSource.Error error = message.getError();
			Buffer buffer = error.getBuffer();
			if(buffer == null)
				return;

			int lineNumber = error.getLineNumber();

			View view = jEdit.getFirstView();
			while(view != null)
			{
				if(view.getBuffer() == buffer)
					view.getTextArea().getPainter()
						.invalidateLine(lineNumber);
				view = view.getNext();
			}
		}
		else if(what == ErrorSourceUpdate.ERRORS_CLEARED)
		{
			View view = jEdit.getFirstView();
			while(view != null)
			{
				view.getTextArea().repaint();
				view = view.getNext();
			}
		}
	}

	private void handleViewMessage(ViewUpdate message)
	{
		View view = message.getView();

		if(message.getWhat() == ViewUpdate.CREATED)
		{
			ErrorHighlight highlight = new ErrorHighlight();
			view.getTextArea().getPainter().addCustomHighlight(highlight);
		}
	}
}
