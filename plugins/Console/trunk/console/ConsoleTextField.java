/*
 * ConsoleTextField.java - History text field with completion
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002, 2003 Slava Pestov
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

package console;

//{{{ Imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Arrays;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.*;
//}}}

public class ConsoleTextField extends HistoryTextField
{
	//{{{ ConsoleTextField constructor
	public ConsoleTextField(View view, Console console,
		ConsoleToolBar toolBar)
	{
		super(null);
		this.view = view;
		this.console = console;
		this.toolBar = toolBar;
	} //}}}

	//{{{ getFocusTraversalKeysEnabled() method
	public boolean getFocusTraversalKeysEnabled()
	{
		return false;
	} //}}}

	//{{{ processKeyEvent() method
	protected void processKeyEvent(KeyEvent evt)
	{
		if(evt.getID() == KeyEvent.KEY_PRESSED)
		{
			if(evt.getKeyCode() == KeyEvent.VK_TAB)
			{
				complete();
				return;
			}
		}

		super.processKeyEvent(evt);
	} //}}}

	//{{{ Private members
	private View view;
	private Console console;
	private ConsoleToolBar toolBar;

	//{{{ complete() method
	private void complete()
	{
		Shell shell;
		if(toolBar != null)
			shell = toolBar.getShell();
		else
			shell = console.getShell();

		Shell.CompletionInfo info = shell.getCompletions(console,
			getText().substring(0,getCaretPosition()));

		if(info == null || info.completions.length == 0)
			getToolkit().beep();
		else if(info.completions.length == 1)
		{
			select(info.offset, getCaretPosition());
			replaceSelection(info.completions[0]);
		}
		else //if(info.completions.length > 1)
		{
			// Find a partial completion
			String longestCommonStart = MiscUtilities
				.getLongestPrefix(info.completions,
				ProcessRunner.getProcessRunner()
				.isCaseSensitive());

			if(longestCommonStart.length() != 0)
			{
				if(getCaretPosition() - info.offset
					!= longestCommonStart.length())
				{
					select(info.offset,getCaretPosition());
					replaceSelection(longestCommonStart);
					return;
				}
			}

			Console consoleInstance;

			if(console == null)
			{
				view.getDockableWindowManager().addDockableWindow("console");
				consoleInstance = (Console)view.getDockableWindowManager()
					.getDockable("console");
				/* consoleInstance.getTextField().setText(getText());
				consoleInstance.getTextField().setCaretPosition(getCaretPosition());
				setText(null); */
			}
			else
				consoleInstance = console;

			consoleInstance.print(console.getInfoColor(),
				jEdit.getProperty(
				"console.completions"));

			Arrays.sort(info.completions,new MiscUtilities
				.StringICaseCompare());

			for(int i = 0; i < info.completions.length; i++)
			{
				consoleInstance.print(null,info.completions[i]);
			}

			consoleInstance.print(console.getInfoColor(),
				jEdit.getProperty(
				"console.completions-end"));
		}
	} //}}}

	//}}}
}
