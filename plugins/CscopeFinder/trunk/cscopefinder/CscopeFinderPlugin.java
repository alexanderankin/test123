/*
 * TagsPlugin.java
 * Copyright (c) 2001, 2002 Kenrick Drew (kdrew@earthlink.net)
 * Copyright (c) 2003, 2004 Ollie Rutherfurd (oliver@jedit.org)
 *
 * This file is part of the Tags plugin.
 *
 * TagsPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * TagsPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA.
 *
 * $Id: TagsPlugin.java,v 1.10 2004/11/07 15:52:36 orutherfurd Exp $
 */
/*
 * This file originates from the Tags Plugin version 2.0.1
 * whose copyright and licensing is seen above.
 * The original file was modified to become the derived work you see here
 * in accordance with Section 2 of the Terms and Conditions of the GPL v2.
 *
 * The derived work is called the CscopeFinder Plugin and is
 * Copyright 2006 Dean Hall.
 *
 * 2006/08/09
 */

package cscopefinder;

//{{{ imports
import java.util.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.Container;
import java.awt.Component;
import java.awt.Point;
import javax.swing.*;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferChanging;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.search.CurrentBufferSet;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.search.SearchFileSet;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}

public class CscopeFinderPlugin extends EBPlugin
{

	//{{{ declarations
	private static HashMap tagStacks;
	private MouseHandler mouseHandler;
	private static Point mousePointer = null;
	//}}}

	//{{{ EBPlugin methods

	//{{{ start() method
	public void start()
	{
		mouseHandler = new MouseHandler();
		installMouseListener();
	} //}}}

	//{{{ stop()
	public void stop()
	{
		uninstallMouseListener();
		mouseHandler = null;
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage ebmsg)
	{
		if (ebmsg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate) ebmsg;
			EditPane editPane = ((EditPaneUpdate)ebmsg).getEditPane();

			if (epu.getWhat() == EditPaneUpdate.CREATED)
			{
				setMouseMotionListener(editPane.getView());
				setMouseMotionListener(editPane.getTextArea());
			}
			epu = null;
			editPane = null;
		}
		if(ebmsg instanceof BufferUpdate)
		{
			// when a buffer is closed, we want
			// to release all reference to it
			// in the TagStackModels which contain
			// references to it
			BufferUpdate bu = (BufferUpdate)ebmsg;
			if(bu.getWhat() == BufferUpdate.CLOSED)
			{
				HashMap models = getTagStackModels();
				View[] views = jEdit.getViews();
				for(int i=0; i < views.length; i++)
				{
					TargetStackModel model = (TargetStackModel)models.get(views[i]);
					if(model != null)
					{
						model.releaseBuffer(bu.getBuffer());
					}
				}
			}
		}
		else if(ebmsg instanceof PropertiesChanged)
		{
			propertiesChanged();
		}
	} //}}}

	//}}}

	//{{{ Mouse methods

	//{{{ installMouseListener() method
	protected void installMouseListener()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			setMouseMotionListener(view);
			EditPane[] editPanes = view.getEditPanes();
			for(int i=0; i < editPanes.length; i++)
				setMouseMotionListener(editPanes[i].getTextArea());
			view = view.getNext();
		}
	} //}}}

	//{{{ uninstallMouseListener() method
	public void uninstallMouseListener()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			removeMouseMotionListener(view);
			EditPane[] editPanes = view.getEditPanes();
			for(int i=0; i < editPanes.length; i++)
				removeMouseMotionListener(editPanes[i].getTextArea());
			view = view.getNext();
		}
	} //}}}

	//{{{ setMouseMotionListener() method
	protected void setMouseMotionListener(Container container)
	{
		Component c;
		for (int i = 0; i < container.getComponentCount(); i++)
		{
			c = container.getComponent(i);
			c.addMouseMotionListener(mouseHandler);
			if (c instanceof Container)
				setMouseMotionListener((Container) c);
		}
		c = null;
	} //}}}

	//{{{ removeMouseMotionListener() method
	protected void removeMouseMotionListener(Container container)
	{
		Component c;
		for(int i=0; i < container.getComponentCount(); i++)
		{
			c = container.getComponent(i);
			c.removeMouseMotionListener(mouseHandler);
			if(c instanceof Container)
				removeMouseMotionListener((Container)c);
		}
	} //}}}

	//{{{ getMousePointer() method
	public static Point getMousePointer()
	{
		return mousePointer;
	} //}}}

	//{{{ MouseHandler class
	static class MouseHandler implements MouseMotionListener
	{
		//{{{ mouseDragged() method
		public void mouseDragged(MouseEvent e)
		{
			mouseMoved(e);
		} //}}}

		//{{{ mouseMoved() method
		public void mouseMoved(MouseEvent e)
		{
			Point p = e.getPoint();
			SwingUtilities.convertPointToScreen(p, e.getComponent());
			mousePointer = p;
		} //}}}
	} //}}}

	//}}}

	//{{{ TargetStack methods

	//{{{ pushPosition() method
	public static void pushPosition(View view)
	{
		// For Navigator plugin's benefit:
		EditBus.send(new BufferChanging(view.getEditPane(), null));
		TargetStackModel model = getTagStack(view);
		try
		{
			TargetStackPosition pos = model.peek();
			String path = view.getBuffer().getPath();
			// ignore case in case closed and reopened 
			// w/different case on windows.
			if(path.equalsIgnoreCase(pos.getPath()))
			{
				if(view.getTextArea().getCaretLine()+1 == pos.getLineNumber())
				{
					Log.log(Log.DEBUG, CscopeFinderPlugin.class,
						"Not pushing duplicate position onto the stack.");
					return;
				}
			}
		}
		catch(EmptyStackException e)
		{
		}
		model.push(new TargetStackPosition(view));
	} //}}}

	//{{{ popPosition() method
	public static void popPosition(View view)
	{
		TargetStackModel model = getTagStack(view);
		try
		{
			TargetStackPosition pos = null;
			pos = model.pop();
			/*
			* Below is a bit of hackery to make the Tag Stack
			* behave as if the current position is on the top
			* of the stack.	 So, unless the top of the stack
			* is open at the current position, we go to that
			* position instead of popping it from the stack.
			*/
			String path = view.getBuffer().getPath();
			if(path.equalsIgnoreCase(pos.getPath()))
			{
				if(view.getTextArea().getCaretLine()+1 == pos.getLineNumber())
				{
					try
					{
						pos = model.peek();
					}
					catch(EmptyStackException ese)
					{
						// stack is empty
						Log.log(Log.DEBUG, CscopeFinderPlugin.class,
							"Couldn't do stack popping trickery");	// ##
					}
				}
				else
				{
					// cursor is not at current position
					// so put position back on the stack
					model.push(pos);
				}
			}
			else
			{
				// current buffer is not position's buffer, so
				// put the position back on the stack.
				model.push(pos);
			}
			// move to position
			pos.goTo(view);
		}
		catch(EmptyStackException e)
		{
			Toolkit.getDefaultToolkit().beep();
		}
	} //}}}

	//{{{ clearStack() method
	public static void clearStack(View view)
	{
		TargetStackModel model = getTagStack(view);
		model.removeAllElements();
	} //}}}

	//{{{ getTagStack() method
	public static TargetStackModel getTagStack(View view)
	{
		HashMap models = getTagStackModels();
		TargetStackModel model = (TargetStackModel)models.get(view);
		if(model == null)
		{
			model = new TargetStackModel();
			models.put(view,model);
		}
		return model;
	} //}}}

	//{{{ getTagStackModels() method
	private static HashMap getTagStackModels()
	{
		if(tagStacks == null)
			tagStacks = new HashMap();
		return tagStacks;
	} //}}}

	//}}}

	//{{{ Tag following methods

	//{{{ getTagNameAtCursor() method
	/**
	 * Returns the tag name under the cursor, respecting
	 * 'Tags extend through member access operator',
	 * or null if nothing can be selected.
	 */
	public static String getTagNameAtCursor(JEditTextArea textArea)
	{
		int caretLine = textArea.getCaretLine();
		String lineText = textArea.getLineText(caretLine);
		int lineIdx = textArea.getCaretPosition() - // offsets from beg of file
									textArea.getLineStartOffset(caretLine);

		int lineLength = lineText.length();
		if(lineLength == 0 || lineIdx == lineLength)
			return null;

		String tagName = null;
		char ch = lineText.charAt(lineIdx);
		if(Character.isLetter(ch) || ch == '_')
		{
			// Search forward from cursor for '.'
			boolean found = false;
			int i;
			for(i = lineIdx; i < lineLength && !found; i++)
			{
				ch = lineText.charAt(i);
				if(Character.isLetter(ch))
					continue;
				else if(ch == '.')
					found = true;
				else
					break;
			}

			if (found &&			// of form ClassName.method()OrFieldName
				jEdit.getBooleanProperty(
                            "cscopefinder.target-extends-through-dot",
                            false))
			{
				// Get class name
				int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
				int end = TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
				String className = lineText.substring(start, end);

				// Get method or field name
				String methodOrFieldName = null;
				if(i != lineLength)
				{
					start = TextUtilities.findWordStart(lineText, i + 2, "_");
					end = TextUtilities.findWordEnd(lineText, i + 2, "_");
					methodOrFieldName = lineText.substring(start, end);
				}

				tagName = className +
							((methodOrFieldName != null) ? "." : "") +
							((methodOrFieldName != null) ? methodOrFieldName : "");

				//Macros.message(view_, "Letter:	\"" + tagName);
			}
			else				 // of form method()OrFieldName or ClassName
			{
				// method or field name
				int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
				int end		= TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
				tagName = lineText.substring(start, end);
			}
		}
		else
			return null;

		return tagName;
	} //}}}

	//{{{ goToTagLine() method
	public static void goToTagLine(View view, TargetLine tagLine, 
									boolean newView, String tagName)
	{
		Log.log(Log.DEBUG, CscopeFinderPlugin.class, 
			"goToTag: " + tagLine + ", tagName: " + tagName); // ##
		JEditTextArea textArea = null;
		Buffer buffer = null;
		CscopeFinderPlugin.pushPosition(view);	// push current position onto the stack

		if(newView)
			view = jEdit.newView(view, view.getBuffer());

		textArea = view.getTextArea();

		buffer = jEdit.openFile(view, tagLine.getDefinitionFileName());
		if(buffer == null)
		{
			view.getStatus().setMessage("Unable to open: " + tagLine.getDefinitionFileName());
			return;
		}

		//final String searchString = tagLine.getDefinitionSearchString();
		final String searchString = tagLine.getSearchString();
		Log.log(Log.DEBUG, CscopeFinderPlugin.class, "tagLine.getSearchString(): " 
							+ searchString);
		Log.log(Log.DEBUG, CscopeFinderPlugin.class, "tagLine.getDefinitionLineNumber(): " 
							+ tagLine.getDefinitionLineNumber()); // ##
		final View v = view;	// for VFSManager inner class
		if(tagLine.getDefinitionLineNumber() <= 0)
		{
			// This is how jEdit.gotoMarker() and its use by jEdit.openFile()
			// does it.	 However b/c this code is under a GUI callback I
			// thought we are already in the swing GUI thread.	Guess not.
			// When in Rome...
			VFSManager.runInAWTThread(new Runnable()
			{
				public void run()
				{
					// set the caret pos to the beginning for searching...
					v.getTextArea().setCaretPosition(0);

					// get current search values/parameters
					SearchAndReplace.save();
					SearchFileSet oldFileset = SearchAndReplace.getSearchFileSet();
					boolean oldRegexp = SearchAndReplace.getRegexp();
					boolean oldReverse = SearchAndReplace.getReverseSearch();
					boolean oldIgnoreCase = SearchAndReplace.getIgnoreCase();
					boolean oldBeanShellReplace = SearchAndReplace.getBeanShellReplace();
					boolean oldAutoWrapAround = SearchAndReplace.getAutoWrapAround();
					String oldSearchString = SearchAndReplace.getSearchString();

					// set current search values/parameters
					SearchAndReplace.setSearchFileSet(new CurrentBufferSet());
					SearchAndReplace.setRegexp(true);
					SearchAndReplace.setReverseSearch(false);
					SearchAndReplace.setIgnoreCase(false);
					SearchAndReplace.setBeanShellReplace(false);
					SearchAndReplace.setAutoWrapAround(true);

					Log.log(Log.DEBUG, CscopeFinderPlugin.class, "unescaped searchString: " + searchString);	// ##
					//final String escapedSearchString = "^" + SearchAndReplace.escapeRegexp(searchString, false) + "$";
					final String escapedSearchString = 
						StandardUtilities.charsToEscapes(searchString, "\r\t\\()[]{}$^*+?|.");
					Log.log(Log.DEBUG, CscopeFinderPlugin.class, "escaped searchString: " + escapedSearchString);	// ##
					SearchAndReplace.setSearchString(escapedSearchString);
					SearchAndReplace.find(v);

					// Be nice and restore search values/parameters
					SearchAndReplace.load();
					if(oldFileset != null)
						SearchAndReplace.setSearchFileSet(oldFileset);
					SearchAndReplace.setRegexp(oldRegexp);
					SearchAndReplace.setReverseSearch(oldReverse);
					SearchAndReplace.setIgnoreCase(oldIgnoreCase);
					SearchAndReplace.setBeanShellReplace(oldBeanShellReplace);
					SearchAndReplace.setAutoWrapAround(oldAutoWrapAround);
					if(oldSearchString != null)
						SearchAndReplace.setSearchString(oldSearchString);

					v.getTextArea().removeFromSelection(
							 v.getTextArea().getCaretPosition());
					CscopeFinderPlugin.pushPosition(v);
				}
			});

		}
		else
		{
			final int defLine = tagLine.getDefinitionLineNumber();
			VFSManager.runInAWTThread(new Runnable()
			{
				public void run()
				{
					// minus 1 b/c line numbers start at 0
					v.getTextArea().setCaretPosition(
							v.getTextArea().getLineStartOffset(defLine - 1));
					CscopeFinderPlugin.pushPosition(v);
				}
			});
		}

		view.getStatus().setMessage("Found: " + tagName);
		HistoryModel taggingHistoryModel =
						HistoryModel.getModel(
                                        "cscopefinder.enter-target.history");
		taggingHistoryModel.addItem(tagName);
	} //}}}

	//}}}

	///{{{ property methods

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
	} //}}}

	//}}}

}
