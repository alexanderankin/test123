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
import java.util.regex.Pattern;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.Gutter;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
//}}}
import org.gjt.sp.util.StandardUtilities;

public class ErrorListPlugin extends EBPlugin
{
	static final String FILENAME_FILTER = "error-list.filenameFilter";
	static final String IS_INCLUSION_FILTER = "error-list.isInclusionFilter";
	static final String SHOW_ICONS_IN_GUTTER = "error-list.showIconsInGutter";

	//{{{ start() method
	public void start()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				initEditPane(panes[i]);
				addErrorOverviewIfErrors(panes[i]);
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
				uninitEditPane(panes[i]);
				removeErrorOverview(panes[i]);
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
				addErrorOverviewIfErrors(panes[i]);
			view = view.getNext();
		}
	} //}}}

	//{{{ addErrorOverview() method
	public static void addErrorOverview(EditPane editPane)
	{
		ErrorOverview overview = getErrorOverview(editPane);
		if(overview != null)
			overview.repaint();
		else
		{
			overview = new ErrorOverview(editPane);
			JEditTextArea textArea = editPane.getTextArea();
			textArea.addLeftOfScrollBar(overview);
			textArea.putClientProperty(ErrorOverview.class,overview);
			textArea.revalidate();
		}
	} //}}}

	//{{{ removeErrorOverview() method
	public static void removeErrorOverview(EditPane editPane)
	{
		ErrorOverview overview = getErrorOverview(editPane);
		if(overview != null)
		{
			JEditTextArea textArea = editPane.getTextArea();
			textArea.removeLeftOfScrollBar(overview);
			textArea.revalidate();
			textArea.putClientProperty(ErrorOverview.class,null);
		}
	} //}}}

	//{{{ getErrorOverview() method
	public static ErrorOverview getErrorOverview(EditPane editPane)
	{
		return (ErrorOverview)editPane.getTextArea()
			.getClientProperty(ErrorOverview.class);
	} //}}}

	//{{{ getErrorColor() method
	static Color getErrorColor(int type)
	{
		return (type == ErrorSource.WARNING ? warningColor : errorColor);
	} //}}}

	//{{{ getFilenameFilter() method
	static Pattern getFilenameFilter()
	{
		return filter;
	} //}}}

	//{{{ isInclusionFilter() method
	static boolean isInclusionFilter()
	{
		return isInclusionFilter;
	} //}}}

	//{{{ showIconsInGutter() method
	static boolean showIconsInGutter()
	{
		return jEdit.getBooleanProperty(SHOW_ICONS_IN_GUTTER, false);
	} //}}}

	//{{{ Private members
	private static boolean showOnError;
	private static boolean showErrorOverview;
	private static Color warningColor;
	private static Color errorColor;
	private static Pattern filter;
	private static boolean isInclusionFilter;
	
	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		showOnError = jEdit.getBooleanProperty("error-list.showOnError");
		showErrorOverview = jEdit.getBooleanProperty("error-list.showErrorOverview");
		warningColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.warningColor"));
		errorColor = GUIUtilities.parseColor(jEdit.getProperty(
			"error-list.errorColor"));
		String globFilter = jEdit.getProperty(FILENAME_FILTER);
		if (globFilter != null && globFilter.length() > 0)
			filter = Pattern.compile(StandardUtilities.globToRE(globFilter));
		else
			filter = null;
		isInclusionFilter = jEdit.getBooleanProperty(IS_INCLUSION_FILTER, false);
		
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				uninitEditPane(panes[i]);
				initEditPane(panes[i]);
				addErrorOverviewIfErrors(panes[i]);
			}
			view = view.getNext();
		}
	} //}}}

	//{{{ initEditPane() method
	private void initEditPane(EditPane editPane)
	{
		JEditTextArea textArea = editPane.getTextArea();
		TextAreaExtension ext;
		if (showIconsInGutter())
		{
			ext = new ErrorGutterIcon(editPane);
			Gutter gutter = textArea.getGutter();
			gutter.addExtension(ext);
			gutter.putClientProperty("ErrorHighlight", ext);
		}
		else
		{
			ext = new ErrorHighlight(editPane);
			textArea.getPainter().addExtension(ext);
			textArea.putClientProperty("ErrorHighlight", ext);
		}
	} //}}}

	//{{{ uninitTextArea() method
	private void uninitEditPane(EditPane editPane)
	{
		JEditTextArea textArea = editPane.getTextArea();
		TextAreaExtension ext = (TextAreaExtension)
			textArea.getClientProperty("ErrorHighlight");
		if (ext != null)
		{
			textArea.getPainter().removeExtension(ext);
			textArea.putClientProperty("ErrorHighlight",null);
		}
		Gutter gutter = textArea.getGutter();
		ext = (TextAreaExtension)gutter.getClientProperty("ErrorHighlight");
		if (ext != null)
		{
			gutter.removeExtension(ext);
			gutter.putClientProperty("ErrorHighlight",null);
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
					pane.getTextArea().getPainter().repaint();
					addErrorOverviewIfErrors(pane);
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
					addErrorOverviewIfErrors(pane);
				}
			}

			view = view.getNext();
		}
	} //}}}

	//{{{ handleEditPaneMessage() method
	private void handleEditPaneMessage(EditPaneUpdate message)
	{
		EditPane editPane = message.getEditPane();
		Object what = message.getWhat();

		if(what == EditPaneUpdate.CREATED)
		{
			initEditPane(editPane);
			addErrorOverviewIfErrors(editPane);
		}
		else if(what == EditPaneUpdate.DESTROYED)
		{
			uninitEditPane(editPane);
			removeErrorOverview(editPane);
		}
		else if(what == EditPaneUpdate.BUFFER_CHANGED)
		{
			addErrorOverviewIfErrors(editPane);
		}
	} //}}}

	//{{{ addErrorOverviewIfErrors() method
	private void addErrorOverviewIfErrors(EditPane editPane)
	{
		Buffer buffer = editPane.getBuffer();

		if(showErrorOverview)
		{
			ErrorSource[] errorSources = ErrorSource.getErrorSources();
			for(int i = 0; i < errorSources.length; i++)
			{
				ErrorSource source = errorSources[i];
				if(source.getFileErrors(buffer.getSymlinkPath()) != null)
				{
					addErrorOverview(editPane);
					return;
				}
			}
		}

		// if we got here, no sources have errors or user disabled
		// error overview
		removeErrorOverview(editPane);
	} //}}}

	//}}}
}
