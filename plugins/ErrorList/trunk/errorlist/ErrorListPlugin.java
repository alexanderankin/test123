/*
 * ErrorListPlugin.java - Error list plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2003 Slava Pestov
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

package errorlist;

//{{{ Imports
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
//}}}

public class ErrorListPlugin extends EBPlugin
{
	//{{{ start() method
	public void start()
	{
		if(showOnStartup && jEdit.getActiveView() != null)
		{
			showOnStartup = false;
			showErrorList(jEdit.getActiveView());
		}

		View[] views = jEdit.getViews();
		for(int i = 0; i < views.length; i++)
		{
			EditPane[] panes = views[i].getEditPanes();
			for(int j = 0; j < panes.length; j++)
			{
				initTextArea(panes[j].getTextArea());
			}
		}
	} //}}}

	//{{{ stop() method
	public void stop()
	{
		View[] views = jEdit.getViews();
		for(int i = 0; i < views.length; i++)
		{
			EditPane[] panes = views[i].getEditPanes();
			for(int j = 0; j < panes.length; j++)
			{
				uninitTextArea(panes[j].getTextArea());
			}
		}
	} //}}}

	//{{{ createOptionPanes() method
	public void createOptionPanes(OptionsDialog dialog)
	{
		dialog.addOptionPane(new ErrorListOptionPane());
	} //}}}

	//{{{ handleMessage() method
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
	} //}}}

	//{{{ addErrorOverview() method
	public static void addErrorOverview(JEditTextArea textArea)
	{
		ErrorOverview overview = new ErrorOverview(textArea);
		textArea.addLeftOfScrollBar(overview);
		textArea.putClientProperty(ErrorOverview.class,overview);
	} //}}}

	//{{{ removeErrorOverview() method
	public static void removeErrorOverview(JEditTextArea textArea)
	{
		ErrorOverview overview = (ErrorOverview)textArea.getPainter()
			.getClientProperty(ErrorOverview.class);
		if(overview != null)
		{
			textArea.removeLeftOfScrollBar(overview);
			textArea.getPainter().putClientProperty(ErrorOverview.class,
				null);
		}
	} //}}}

	//{{{ getErrorColor() method
	static Color getErrorColor(int type)
	{
		return (type == ErrorSource.WARNING ? warningColor : errorColor);
	} //}}}

	//{{{ Private members
	private static boolean showOnError;
	private static boolean showOnStartup;
	private static Color warningColor;
	private static Color errorColor;

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		showOnError = jEdit.getBooleanProperty("error-list.showOnError");
		warningColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.warningColor"));
		errorColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.errorColor"));
	} //}}}

	//{{{ initTextArea() method
	private void initTextArea(JEditTextArea textArea)
	{
		ErrorHighlight highlight = new ErrorHighlight(textArea);
		textArea.getPainter().addExtension(highlight);
		textArea.getPainter().putClientProperty(ErrorHighlight.class,
			highlight);
	} //}}}

	//{{{ uninitTextArea() method
	private void uninitTextArea(JEditTextArea textArea)
	{
		ErrorHighlight highlight = (ErrorHighlight)textArea.getPainter()
			.getClientProperty(ErrorHighlight.class);
		if(highlight != null)
		{
			textArea.getPainter().removeExtension(highlight);
			textArea.getPainter().putClientProperty(ErrorHighlight.class,
				null);
		}
	} //}}}

	//{{{ showErrorList() method
	private void showErrorList(View view)
	{
		DockableWindowManager dockableWindowManager = view.getDockableWindowManager();
		dockableWindowManager.addDockableWindow("error-list");
	} //}}}

	//{{{ handleErrorSourceMessage() method
	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();
		if(what == ErrorSourceUpdate.ERROR_ADDED
			|| what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			ErrorSource.Error error = message.getError();
			Buffer buffer = error.getBuffer();
			if(buffer != null)
			{
				int lineNumber = error.getLineNumber();

				View view = jEdit.getFirstView();
				while(view != null)
				{
					if(view.getBuffer() == buffer)
					{
						EditPane[] editPanes = view.getEditPanes();
						for(int i = 0; i < editPanes.length; i++)
							editPanes[i].getTextArea().invalidateLine(lineNumber);
					}

					view = view.getNext();
				}
			}

			if(showOnError)
			{
				if(jEdit.getActiveView() == null)
					showOnStartup = true;
				else
					showErrorList(jEdit.getActiveView());
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
	} //}}}

	//{{{ handleViewMessage() method
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
	} //}}}

	//{{{ handleEditPaneMessage() method
	private void handleEditPaneMessage(EditPaneUpdate message)
	{
		JEditTextArea textArea = message.getEditPane().getTextArea();

		if(message.getWhat() == EditPaneUpdate.CREATED)
			initTextArea(textArea);
		else if(message.getWhat() == EditPaneUpdate.DESTROYED)
			uninitTextArea(textArea);
	} //}}}

	//}}}
}
