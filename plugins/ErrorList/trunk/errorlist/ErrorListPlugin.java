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
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				initTextArea(textArea);
				addErrorOverviewIfErrors(textArea);
			}
			view = view.getNext();
		}

		propertiesChanged();
	} //}}}

	//{{{ stop() method
	public void stop()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				uninitTextArea(textArea);
				removeErrorOverview(textArea);
			}
			view = view.getNext();
		}
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage message)
	{
		if(message instanceof ErrorSourceUpdate)
			handleErrorSourceMessage((ErrorSourceUpdate)message);
		else if(message instanceof EditPaneUpdate)
			handleEditPaneMessage((EditPaneUpdate)message);
		else if(message instanceof PropertiesChanged)
			propertiesChanged();
	} //}}}

	//{{{ showErrorOverviewIfNecessary() method
	public void showErrorOverviewIfNecessary()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				addErrorOverviewIfErrors(panes[i].getTextArea());
			}
			view = view.getNext();
		}
	} //}}}

	//{{{ addErrorOverview() method
	public static void addErrorOverview(JEditTextArea textArea)
	{
		ErrorOverview overview = getErrorOverview(textArea);
		if(overview != null)
			overview.repaint();
		else
		{
			overview = new ErrorOverview(textArea);
			textArea.addLeftOfScrollBar(overview);
			textArea.putClientProperty(ErrorOverview.class,overview);
			textArea.revalidate();
		}
	} //}}}

	//{{{ removeErrorOverview() method
	public static void removeErrorOverview(JEditTextArea textArea)
	{
		ErrorOverview overview = getErrorOverview(textArea);
		if(overview != null)
		{
			textArea.removeLeftOfScrollBar(overview);
			textArea.revalidate();
			textArea.putClientProperty(ErrorOverview.class,null);
		}
	} //}}}

	//{{{ getErrorOverview() method
	public static ErrorOverview getErrorOverview(JEditTextArea textArea)
	{
		return (ErrorOverview)textArea.getClientProperty(
			ErrorOverview.class);
	} //}}}

	//{{{ getErrorColor() method
	static Color getErrorColor(int type)
	{
		return (type == ErrorSource.WARNING ? warningColor : errorColor);
	} //}}}

	//{{{ Private members
	private static boolean showOnError;
	private static boolean showErrorOverview;
	private static Color warningColor;
	private static Color errorColor;

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		showOnError = jEdit.getBooleanProperty("error-list.showOnError");
		showErrorOverview = jEdit.getBooleanProperty("error-list.showErrorOverview");
		warningColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.warningColor"));
		errorColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.errorColor"));

		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				addErrorOverviewIfErrors(textArea);
			}
			view = view.getNext();
		}
	} //}}}

	//{{{ initTextArea() method
	private void initTextArea(JEditTextArea textArea)
	{
		ErrorHighlight highlight = new ErrorHighlight(textArea);
		textArea.getPainter().addExtension(highlight);
		textArea.putClientProperty(ErrorHighlight.class,highlight);
	} //}}}

	//{{{ uninitTextArea() method
	private void uninitTextArea(JEditTextArea textArea)
	{
		ErrorHighlight highlight = (ErrorHighlight)textArea.getPainter()
			.getClientProperty(ErrorHighlight.class);
		if(highlight != null)
		{
			textArea.getPainter().removeExtension(highlight);
			textArea.putClientProperty(ErrorHighlight.class,null);
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
		if(what == ErrorSourceUpdate.ERROR_ADDED)
		{
			ErrorSource.Error error = message.getError();
			Buffer buffer = error.getBuffer();
			if(buffer != null)
				invalidateLineInAllViews(buffer,error.getLineNumber());

			if(showOnError)
			{
				if(jEdit.getActiveView() != null)
					showErrorList(jEdit.getActiveView());
			}
		}
		else if(what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			ErrorSource.Error error = message.getError();
			Buffer buffer = error.getBuffer();
			if(buffer != null)
			{
				invalidateLineInAllViews(buffer,error.getLineNumber());
			}

			if(showOnError)
			{
				if(jEdit.getActiveView() != null)
					showErrorList(jEdit.getActiveView());
			}
		}
		else if(what == ErrorSourceUpdate.ERRORS_CLEARED
			|| what == ErrorSourceUpdate.ERROR_SOURCE_ADDED
			|| what == ErrorSourceUpdate.ERROR_SOURCE_REMOVED)
		{
			View view = jEdit.getFirstView();
			while(view != null)
			{
				EditPane[] editPanes = view.getEditPanes();
				for(int i = 0; i < editPanes.length; i++)
				{
					EditPane pane = editPanes[i];
					pane.getTextArea().getPainter()
						.repaint();
					addErrorOverviewIfErrors(pane.getTextArea());
				}
				view = view.getNext();
			}

			if(what == ErrorSourceUpdate.ERROR_SOURCE_ADDED)
			{
				if(showOnError)
				{
					if(jEdit.getActiveView() != null)
						showErrorList(jEdit.getActiveView());
				}
			}
		}
	} //}}}

	//{{{ invalidateLineInAllViews() method
	private void invalidateLineInAllViews(Buffer buffer, int line)
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] editPanes = view.getEditPanes();
			for(int i = 0; i < editPanes.length; i++)
			{
				EditPane pane = editPanes[i];
				if(pane.getBuffer() == buffer)
				{
					pane.getTextArea().invalidateLine(line);
					addErrorOverviewIfErrors(pane.getTextArea());
				}
			}

			view = view.getNext();
		}
	} //}}}

	//{{{ handleEditPaneMessage() method
	private void handleEditPaneMessage(EditPaneUpdate message)
	{
		JEditTextArea textArea = message.getEditPane().getTextArea();
		Object what = message.getWhat();

		if(what == EditPaneUpdate.CREATED)
		{
			initTextArea(textArea);
			addErrorOverviewIfErrors(textArea);
		}
		else if(what == EditPaneUpdate.DESTROYED)
		{
			uninitTextArea(textArea);
			removeErrorOverview(textArea);
		}
		else if(what == EditPaneUpdate.BUFFER_CHANGED)
		{
			addErrorOverviewIfErrors(textArea);
		}
	} //}}}

	//{{{ addErrorOverviewIfErrors() method
	private void addErrorOverviewIfErrors(JEditTextArea textArea)
	{
		Buffer buffer = textArea.getBuffer();

		if(showErrorOverview)
		{
			ErrorSource[] errorSources = ErrorSource.getErrorSources();
			for(int i = 0; i < errorSources.length; i++)
			{
				ErrorSource source = errorSources[i];
				if(source.getFileErrors(buffer.getPath()) != null)
				{
					addErrorOverview(textArea);
					return;
				}
			}
		}

		// if we got here, no sources have errors or user disabled
		// error overview
		removeErrorOverview(textArea);
	} //}}}

	//}}}
}
