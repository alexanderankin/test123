/*
 * TagsPlugin.java
 * Copyright (c) 2001, 2002 Kenrick Drew (kdrew@earthlink.net)
 * Copyright (c) 2003, 2004 Ollie Rutherfurd (oliver@jedit.org)
 * Copyright (c) 2007 Shlomy Reinstein (shlomy@users.sourceforge.net)
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
 * $Id$
 */

package tags;

//{{{ imports
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.Container;
import java.awt.Component;
import java.awt.Point;
import javax.swing.*;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PositionChanging;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.search.CurrentBufferSet;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.search.SearchFileSet;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.StandardUtilities;
import org.gjt.sp.util.Log;
//}}}

public class TagsPlugin extends EBPlugin
{

	//{{{ declarations
	public static final String ACTION_SET_NAME =
		"Plugin: Tags - Collision resolvers";
	public static final String OPTION_HAS_DYNAMIC_ACTIONS =
		"options.tags.hasDynamicActions";
	public static final String OPTION_NUM_DYNAMIC_ACTIONS =
		"options.tags.actions.size";
	private static TagFileManager tagFileManager = null;
	private static HashMap<View, TagStackModel> tagStacks;
	private MouseHandler mouseHandler;
	private static Point mousePointer = null;
	private static ActionSet actions;
	private static AttributeValueCollisionResolver resolver = null;
	//}}}

	//{{{ EBPlugin methods

	//{{{ start() method
	public void start()
	{
		mouseHandler = new MouseHandler();
		installMouseListener();
		actions = new ActionSet(ACTION_SET_NAME);
		reloadActions();
		jEdit.addActionSet(actions);
	} //}}}

	//{{{ stop()
	public void stop()
	{
		uninstallMouseListener();
		mouseHandler = null;
	} //}}}

	//{{{ getAllActions()
	static public AttributeValueCollisionResolver[] getAllActions() {
		EditAction [] editActions = actions.getActions();
		AttributeValueCollisionResolver [] resolvers =
			new AttributeValueCollisionResolver[editActions.length];
		for (int i = 0; i < editActions.length; i++)
			resolvers[i] =
				((CollisionResolverAction)editActions[i]).getResolver();
		return resolvers;
	} //}}}
	
	//{{{ loadActions()
	static public void reloadActions() {
		actions.removeAllActions();
		int n = jEdit.getIntegerProperty(OPTION_NUM_DYNAMIC_ACTIONS, 0);
		for (int i = 0; i < n; i++) {
			AttributeValueCollisionResolver resolver =
				new AttributeValueCollisionResolver(i);
			actions.addAction(new CollisionResolverAction(resolver));
		}
		actions.initKeyBindings();
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
				HashMap<View, TagStackModel> models = getTagStackModels();
				View[] views = jEdit.getViews();
				for(int i=0; i < views.length; i++)
				{
					TagStackModel model = models.get(views[i]);
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

	//{{{ CollisionResolverAction class
	static class CollisionResolverAction extends EditAction {
		AttributeValueCollisionResolver resolver;
		public CollisionResolverAction(AttributeValueCollisionResolver resolver) {
			super(resolver.getName());
			this.resolver = resolver;
			String name = resolver.getName();
			jEdit.setTemporaryProperty(name + ".label", name);
		}
		public AttributeValueCollisionResolver getResolver() {
			return resolver;
		}
		@Override
		public void invoke(View view) {
			TagsPlugin.resolver = resolver;
			followTag(view, false);
			TagsPlugin.resolver = null;
		}
	} //}}}
	
	//}}}

	//{{{ TagStack methods

	//{{{ pushPosition() method
	public static void pushPosition(View view)
	{
		// PositionChanging
		EditBus.send(new PositionChanging(view.getTextArea()));
		TagStackModel model = getTagStack(view);
		try
		{
			StackPosition pos = model.peek();
			String path = view.getBuffer().getPath();
			// ignore case in case closed and reopened 
			// w/different case on windows.
			if(path.equalsIgnoreCase(pos.getPath()))
			{
				if(view.getTextArea().getCaretLine()+1 == pos.getLineNumber())
				{
					Log.log(Log.DEBUG, TagsPlugin.class,
						"Not pushing duplicate position onto the stack.");
					return;
				}
			}
		}
		catch(EmptyStackException e)
		{
		}
		model.push(new StackPosition(view));
	} //}}}

	//{{{ popPosition() method
	public static void popPosition(View view)
	{
		TagStackModel model = getTagStack(view);
		try
		{
			StackPosition pos = null;
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
						Log.log(Log.DEBUG, TagsPlugin.class,
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
		TagStackModel model = getTagStack(view);
		model.removeAllElements();
	} //}}}

	//{{{ getTagStack() method
	public static TagStackModel getTagStack(View view)
	{
		HashMap<View, TagStackModel> models = getTagStackModels();
		TagStackModel model = models.get(view);
		if(model == null)
		{
			model = new TagStackModel();
			models.put(view,model);
		}
		return model;
	} //}}}

	//{{{ getTagStackModels() method
	private static HashMap<View, TagStackModel> getTagStackModels()
	{
		if(tagStacks == null)
			tagStacks = new HashMap<View, TagStackModel>();
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

			if(found &&			// of form ClassName.method()OrFieldName
				jEdit.getBooleanProperty("options.tags.tag-extends-through-dot",false))
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

	//{{{ enterAndFollowTag() method
	public static void enterAndFollowTag(View view)
	{
		TagPromptDialog dialog = new TagPromptDialog(view);
		if(dialog.isOK())
			followTag(view, dialog.getNewView(), dialog.getTag(), false);
		else
			view.getTextArea().requestFocus();
		dialog = null;
	} //}}}

	//{{{ followTag() method
	public static void followTag(View view, boolean newView)
	{
		String tag = view.getTextArea().getSelectedText();
		if(tag == null)
			tag = getTagNameAtCursor(view.getTextArea());
		followTag(view, newView, tag, true);
	} //}}}

	//{{{ followTag() method
	public static void followTag(View view, boolean newView, 
			String function, boolean usePopup)
	{
		if(function == null)
		{
			Log.log(Log.ERROR, TagsPlugin.class, "no 'function' passed to followTag");
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		String path = view.getBuffer().getPath();
		Vector tagIndexFiles = getTagFileManager().getTagIndexFiles(view,path);

		followTag(view, newView, function, getSearchAllTagFiles(), 
						usePopup, tagIndexFiles);
	} //}}}

	//{{{ followTag() method
	public static void followTag(View view, boolean newView, String function,
									boolean searchAll, boolean usePopup, 
									Vector tagIndexFiles)
	{
		String bufferPath = view.getBuffer().getPath();
		Vector<TagLine> allTagLines = new Vector<TagLine>();
		for(int i=0; i < tagIndexFiles.size(); i++)
		{
			String tagIndexFile = (String)tagIndexFiles.elementAt(i);
			Log.log(Log.DEBUG, TagsPlugin.class, "getting reader for " + tagIndexFile); // XX
			try
			{
				TagFileReader reader = getTagFileManager().getReader(view, tagIndexFile, bufferPath);
				if(reader == null)
				{
					Log.log(Log.DEBUG, TagsPlugin.class, "No reader found for '" + tagIndexFile + "'");
					continue;
				}
				long start, end = 0;
				start = System.currentTimeMillis();
				Vector<TagLine> tagLines = reader.findTagLines(function);
				end = System.currentTimeMillis();
				Log.log(Log.DEBUG, TagsPlugin.class, "Searching '" + reader.getPath() + 
								"' took " + (end - start) * .001 + " seconds.");
				if(tagLines.size() > 0)
				{
					Log.log(Log.DEBUG, TagsPlugin.class, 
									"adding from '" + reader.getPath() + "':" + tagLines);	// ##
					allTagLines.addAll(tagLines);
					if(searchAll == false)
						break;
				}
			}
			catch(IOException ioe)
			{
				Log.log(Log.ERROR, TagsPlugin.class, "Error reading tag index file: " + tagIndexFile);
				Log.log(Log.ERROR, TagsPlugin.class, ioe);
			}
		}

		if (allTagLines.size() > 1 && resolver != null)
			allTagLines = resolver.resolve(allTagLines);
		
		if(allTagLines.size() == 0)
		{
			Object args[] = { function };
			GUIUtilities.error(view, "func-not-found", args);
		}
		else if(allTagLines.size() > 1)
		{
			// XXX this should probably be done by the rendering component
			for(int i=0; i < allTagLines.size(); i++)
				allTagLines.elementAt(i).setIndex(i+1);

			String dockableName = "tags-taglist";
			DockableWindowManager dwm = view.getDockableWindowManager();
			JComponent dockable = null;
			if (dwm.isDockableWindowDocked(dockableName))
				dwm.showDockableWindow(dockableName);
			dockable = dwm.getDockable(dockableName);
			if (dockable != null)
			{
				if (! dwm.isDockableWindowDocked(dockableName))
					dwm.showDockableWindow(dockableName);
				((ChooseTagListDockable)dockable).setTagLines(allTagLines);
			}
			else if(usePopup)
				new ChooseTagListPopup(view, allTagLines, newView);
			else
				new ChooseTagListDialog(view, allTagLines, newView);
		}
		else
		{
			TagLine tagLine = allTagLines.elementAt(0);
			goToTagLine(view, tagLine, newView, tagLine.getTag());
		}
	} //}}}

	//{{{ goToTagLine() method
	public static void goToTagLine(View view, TagLine tagLine, 
									boolean newView, String tagName)
	{
		Log.log(Log.DEBUG, TagsPlugin.class, 
			"goToTag: " + tagLine + ", tagName: " + tagName); // ##
		Buffer buffer = null;

		TagsPlugin.pushPosition(view);	// push current position onto the stack

		if(newView)
			view = jEdit.newView(view, view.getBuffer());

		buffer = jEdit.openFile(view, tagLine.getDefinitionFileName());
		if(buffer == null)
		{
			view.getStatus().setMessage("Unable to open: " + tagLine.getDefinitionFileName());
			return;
		}

		//final String searchString = tagLine.getDefinitionSearchString();
		final String searchString = tagLine.getSearchString();
		Log.log(Log.DEBUG, TagsPlugin.class, "tagLine.getSearchString(): " 
							+ searchString);
		Log.log(Log.DEBUG, TagsPlugin.class, "tagLine.getDefinitionLineNumber(): " 
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

					Log.log(Log.DEBUG, TagsPlugin.class, "unescaped searchString: " + searchString);	// ##
					//final String escapedSearchString = "^" + SearchAndReplace.escapeRegexp(searchString, false) + "$";
					final String escapedSearchString = 
						StandardUtilities.charsToEscapes(searchString, "\r\t\\()[]{}$^*+?|.");
					Log.log(Log.DEBUG, TagsPlugin.class, "escaped searchString: " + escapedSearchString);	// ##
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
					TagsPlugin.pushPosition(v);
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
					TagsPlugin.pushPosition(v);
				}
			});
		}

		view.getStatus().setMessage("Found: " + tagName);
		HistoryModel taggingHistoryModel =
						HistoryModel.getModel("tags.enter-tag.history");
		taggingHistoryModel.addItem(tagName);
	} //}}}

	//{{{ getTagFileManager() method
	public static TagFileManager getTagFileManager()
	{
		if(tagFileManager == null)
			tagFileManager = new TagFileManager();
		return tagFileManager;
	} //}}}

	//}}}

	///{{{ property methods

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		getTagFileManager().setSize(
			jEdit.getIntegerProperty("options.tags.cache-size",
				TagFileManager.CACHE_SIZE));
		getTagFileManager().reload();
	} //}}}

	//{{{ getCurrentBufferTagFilename() method
	public static String getCurrentBufferTagFilename()
	{
		return jEdit.getProperty("options.tags.current-buffer-file-name","tags");
	} //}}}

	//{{{ getPlaceDialogsUnderCursor() method
	public static boolean getPlaceDialogsUnderCursor()
	{
		return jEdit.getBooleanProperty("options.tags.open-dialogs-under-cursor", false);
	} //}}}

	//{{{ getSearchAllTagFiles() method
	public static boolean getSearchAllTagFiles()
	{
		return jEdit.getBooleanProperty("options.tags.tag-search-all-files", false);
	} //}}}

	//{{{ setSearchAllTagFiles() method
	public static void setSearchAllTagFiles(boolean value)
	{
		jEdit.setBooleanProperty("options.tags.tag-search-all-files", value);
	} //}}}

	//{{{ getUseLineNumbers() method
	public static boolean getUseLineNumbers()
	{
		return jEdit.getBooleanProperty("options.tags.tag-use-line-numbers", false);
	} //}}}

	//{{{ setUseLineNumbers() method
	public static void setUseLineNumbers(boolean value)
	{
		jEdit.setBooleanProperty("options.tags.tag-use-line-numbers", value);
	} //}}}
	//}}}

}

// :collapseFolds=1:noTabs=false:lineSeparator=\r\n:tabSize=4:indentSize=4:deepIndent=false:folding=explicit:
