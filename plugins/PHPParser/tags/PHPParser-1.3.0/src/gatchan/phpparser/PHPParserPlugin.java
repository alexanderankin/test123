/*
 * PHPParserPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.phpparser;

import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.project.itemfinder.FrameFindItem;
import gatchan.phpparser.sidekick.PHPSideKickParser;
import org.gjt.sp.jedit.*;

/**
 * The PHP Parser plugin.
 *
 * @author Matthieu Casanova
 */
public class PHPParserPlugin extends EditPlugin
{
	private ProjectManager projectManager;

	private static FrameFindItem findItemWindow;

	@Override
	public void start()
	{
		projectManager = ProjectManager.getInstance();
		findItemWindow = new FrameFindItem();
	}

	@Override
	public void stop()
	{
		projectManager.dispose();
		projectManager = null;
		findItemWindow.dispose();
		findItemWindow = null;
		Buffer[] buffers = jEdit.getBuffers();
		for (Buffer buffer : buffers)
		{
			buffer.unsetProperty(PHPSideKickParser.PHPDOCUMENT_PROPERTY);
		}

	}

	/**
	 * show the dialog to find a class.
	 *
	 * @param view the jEdit's view
	 */
	public static void findClass(View view)
	{
		findItem(view, FrameFindItem.CLASS_MODE, FrameFindItem.PROJECT_SCOPE);
	}

	/**
	 * show the dialog to find a class.
	 *
	 * @param view the jEdit's view
	 */
	public static void findInterface(View view)
	{
		findItem(view, FrameFindItem.INTERFACE_MODE, FrameFindItem.PROJECT_SCOPE);
	}

	/**
	 * show the dialog to find a class.
	 *
	 * @param view the jEdit's view
	 */
	public static void findClassOrInterface(View view)
	{
		findItem(view, FrameFindItem.CLASS_MODE ^ FrameFindItem.INTERFACE_MODE, FrameFindItem.PROJECT_SCOPE);
	}

	/**
	 * show the dialog to find a method.
	 *
	 * @param view the jEdit's view
	 */
	public static void findMethod(View view)
	{
		findItem(view, FrameFindItem.METHOD_MODE, FrameFindItem.PROJECT_SCOPE);
	}

	/**
	 * Find any item in the current file.
	 *
	 * @param view the jEdit's view
	 */
	public static void findInFile(View view)
	{
		findItem(view, FrameFindItem.ALL_MODE, FrameFindItem.FILE_SCOPE);
	}

	/**
	 * Open the find item frame for the view in the given mode
	 *
	 * @param view  the view
	 * @param mode  one of the following  {@link FrameFindItem#ALL_MODE}, {@link FrameFindItem#CLASS_MODE} or {@link
	 *              FrameFindItem#METHOD_MODE}
	 * @param scope the scope : {@link FrameFindItem#FILE_SCOPE} or {@link FrameFindItem#PROJECT_SCOPE}
	 */
	private static void findItem(View view, int mode, int scope)
	{
		findItemWindow.init(view, mode, scope);
		findItemWindow.setLocationRelativeTo(jEdit.getActiveView());
		findItemWindow.setVisible(true);
	}
}
