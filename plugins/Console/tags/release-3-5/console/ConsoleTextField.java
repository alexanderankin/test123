/*
 * ConsoleTextField.java - History text field with completion
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Slava Pestov
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
	public ConsoleTextField(View view)
	{
		super(null);
		this.view = view;
	} //}}}

	//{{{ getShell() method
	public Shell getShell()
	{
		return shell;
	} //}}}

	//{{{ setShell() method
	public void setShell(Shell shell)
	{
		this.shell = shell;
		setModel("console." + shell.getName());
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
	private Shell shell;

	//{{{ complete() method
	private void complete()
	{
		Console console = (Console)view.getDockableWindowManager()
			.getDockable("console");

		Shell.CompletionInfo info = shell.getCompletions(
			view,(console == null ? System.getProperty("user.dir")
			: SystemShell.getConsoleState(console).currentDirectory),
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
			String longestCommonStart = findLongestCommonStart(info.completions);
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

			if(console == null)
			{
				view.getDockableWindowManager().addDockableWindow("console");
				console = (Console)view.getDockableWindowManager()
					.getDockable("console");
				console.getTextField().setText(getText());
				console.getTextField().setCaretPosition(getCaretPosition());
				setText(null);
			}

			console.print(console.getInfoColor(),
				jEdit.getProperty(
				"console.completions"));

			Arrays.sort(info.completions,new MiscUtilities
				.StringICaseCompare());

			for(int i = 0; i < info.completions.length; i++)
			{
				console.print(null,info.completions[i]);
			}

			console.print(console.getInfoColor(),
				jEdit.getProperty(
				"console.completions-end"));
		}
	} //}}}

	//{{{ findLongestCommonStart() method
	/**
	 * Returns the longest substring starting at the beginning of the string
	 * of the strings in the given array. The comparison of strings is done
	 * in a way that respects the OS case sensitivity.
	 */
	private static String findLongestCommonStart(String [] strings)
	{
		if (strings.length == 0)
			return "";
		if (strings.length == 1)
			return strings[0];

		boolean isOSCaseSensitive = ProcessRunner.getProcessRunner().isCaseSensitive();
		String longestCommonStart = strings[0];
		int longestCommonStartLength = longestCommonStart.length();
		for(int i = 0; i < strings.length; i++){
			int commonStartLength = 0;
			int strLength = strings[i].length();
			while((commonStartLength < strLength) && (commonStartLength < longestCommonStartLength))
			{
				char c1 = strings[i].charAt(commonStartLength);
				char c2 = longestCommonStart.charAt(commonStartLength);

				if(!isOSCaseSensitive)
				{
					c1 = Character.toLowerCase(c1);
					c2 = Character.toLowerCase(c2);
				}

				if(c1 != c2)
					break;
				commonStartLength++;
			}
			longestCommonStart = longestCommonStart.substring(0, commonStartLength);
			longestCommonStartLength = commonStartLength;
		}

		return longestCommonStart;
	} //}}}

	//}}}
}
