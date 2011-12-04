/*
 * PHPParserPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2011 Matthieu Casanova
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

import common.gui.itemfinder.ItemFinderWindow;
import gatchan.phpparser.methodlist.PHPFunctionList;
import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.project.itemfinder.PHPItemFinder;
import gatchan.phpparser.sidekick.PHPSideKickParser;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import net.sourceforge.phpdt.internal.compiler.ast.Expression;
import net.sourceforge.phpdt.internal.compiler.ast.PHPDocument;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

/**
 * The PHP Parser plugin.
 *
 * @author Matthieu Casanova
 */
public class PHPParserPlugin extends EditPlugin
{
	private ProjectManager projectManager;

	private static ItemFinderWindow<PHPItem> itemFinderWindow;
	private static PHPItemFinder itemFinder;
	public static PHPFunctionList phpFunctionList;

	@Override
	public void start()
	{
		projectManager = ProjectManager.getInstance();
		itemFinder = new PHPItemFinder();
		itemFinderWindow = new ItemFinderWindow<PHPItem>(itemFinder);
		phpFunctionList = new PHPFunctionList();
		phpFunctionList.reload();
	}

	@Override
	public void stop()
	{
		phpFunctionList = null;
		projectManager.dispose();
		projectManager = null;
		itemFinderWindow.dispose();
		itemFinderWindow = null;
		itemFinder = null;
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
		findItem(view, PHPItemFinder.CLASS_MODE, PHPItemFinder.PROJECT_SCOPE);
	}

	/**
	 * show the dialog to find a class.
	 *
	 * @param view the jEdit's view
	 */
	public static void findInterface(View view)
	{
		findItem(view, PHPItemFinder.INTERFACE_MODE, PHPItemFinder.PROJECT_SCOPE);
	}

	/**
	 * show the dialog to find a class.
	 *
	 * @param view the jEdit's view
	 */
	public static void findClassOrInterface(View view)
	{
		findItem(view, PHPItemFinder.CLASS_MODE ^ PHPItemFinder.INTERFACE_MODE, PHPItemFinder.PROJECT_SCOPE);
	}

	/**
	 * show the dialog to find a method.
	 *
	 * @param view the jEdit's view
	 */
	public static void findMethod(View view)
	{
		findItem(view, PHPItemFinder.METHOD_MODE, PHPItemFinder.PROJECT_SCOPE);
	}

	/**
	 * Find any item in the current file.
	 *
	 * @param view the jEdit's view
	 */
	public static void findInFile(View view)
	{
		findItem(view, PHPItemFinder.ALL_MODE, PHPItemFinder.FILE_SCOPE);
	}

	/**
	 * Open the find item frame for the view in the given mode
	 *
	 * @param view  the view
	 * @param mode  one of the following  {@link PHPItemFinder#ALL_MODE}, {@link PHPItemFinder#CLASS_MODE} or {@link
	 *              PHPItemFinder#METHOD_MODE}
	 * @param scope the scope : {@link PHPItemFinder#FILE_SCOPE} or {@link PHPItemFinder#PROJECT_SCOPE}
	 */
	private static void findItem(View view, int mode, int scope)
	{
		itemFinder.init(view, mode, scope);
		itemFinderWindow.setLocationRelativeTo(jEdit.getActiveView());
		itemFinderWindow.setVisible(true);
	}

	public static void debugAtCaret(TextArea textArea, Buffer buffer)
	{
		PHPDocument phpDocument = (PHPDocument) buffer.getProperty(PHPSideKickParser.PHPDOCUMENT_PROPERTY);
		if (phpDocument == null)
		{
			return;
		}
		int caretPosition = textArea.getCaretPosition();
		int caretLine = textArea.getCaretLine();
		int lineStartOffset = textArea.getLineStartOffset(caretLine);
		int caretColumn = caretPosition - lineStartOffset;
		AstNode statement = phpDocument.getNodeAt(caretLine + 1, caretColumn);
		if (statement == null) return;
		Log.log(Log.DEBUG, PHPParserPlugin.class, "----------------------------------------------------");
		Log.log(Log.DEBUG, PHPParserPlugin.class, statement);
		AstNode node = statement.subNodeAt(caretLine + 1, caretColumn);
		while (node != null)
		{
			if (node instanceof Expression)
			{
				Expression expression = (Expression) node;
				Log.log(Log.DEBUG, PHPParserPlugin.class, expression + " | " + expression.getType());
			}
			else
			{
				Log.log(Log.DEBUG, PHPParserPlugin.class, node);
			}
			node = node.subNodeAt(caretLine + 1, caretColumn);
		}
		Log.log(Log.DEBUG, PHPParserPlugin.class, "----------------------------------------------------");
	}

	public static void reloadFunctionList()
	{
		phpFunctionList.reload();
	}
}
