/*
 * ConsolePlugin.java - Console plugin
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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

import gnu.regexp.REException;
import java.io.*;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

public class ConsolePlugin extends EBPlugin
{
	public static final Shell SYSTEM_SHELL = new SystemShell();
	public static final Shell BEAN_SHELL = new BeanShell();

	public void start()
	{
		// register shells
		Shell.registerShell(SYSTEM_SHELL);
		Shell.registerShell(BEAN_SHELL);

		// register dockable window
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,"console");

		// load script with useful runCommandInConsole() method
		BufferedReader in = new BufferedReader(
			new InputStreamReader(
			getClass().getResourceAsStream(
			"/console/console.bsh")));

		// have to write org.gjt.sp.jedit.BeanShell otherwise javac
		// will think we want console.BeanShell class 
		org.gjt.sp.jedit.BeanShell.runScript(null,"console.bsh",
			in,false,false);
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("console-menu"));
	}

	public void createOptionPanes(OptionsDialog dialog)
	{
		dialog.addOptionPane(new ConsoleOptionPane());
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof CreateDockableWindow)
		{
			CreateDockableWindow cmsg = (CreateDockableWindow)msg;
			if(cmsg.getDockableWindowName().equals("console"))
				cmsg.setDockableWindow(new Console(cmsg.getView()));
		}
		else if(msg instanceof ViewUpdate)
		{
			ViewUpdate vmsg = (ViewUpdate)msg;
			View view = vmsg.getView();
			if(vmsg.getWhat() == ViewUpdate.CREATED)
			{
				if(jEdit.getBooleanProperty("console.toolbar.enabled"))
				{
					view.addToolBar(new ConsoleToolBar(view));
				}
			}
		}
		else if(msg instanceof PropertiesChanged)
		{
			// lazily load the matchers the next time they are
			// needed
			errorMatchers = null;
		}
	}

	// package-private members
	static synchronized int parseLine(String text, String directory,
		DefaultErrorSource errorSource)
	{
		if(errorMatchers == null)
			loadMatchers();

		for(int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];
			int type = m.match(text,directory,errorSource);
			if(type != -1)
				return type;
		}

		return -1;
	}

	static void loadMatchers()
	{
		Vector vector = new Vector();
		int i = 0;
		String match;
		while((match = jEdit.getProperty("console.error." + i + ".match")) != null)
		{
			String name = jEdit.getProperty("console.error." + i + ".name");
			String filename = jEdit.getProperty("console.error." + i + ".filename");
			String line = jEdit.getProperty("console.error." + i + ".line");
			String message = jEdit.getProperty("console.error." + i + ".message");

			try
			{
				ErrorMatcher matcher = new ErrorMatcher(name,match,
					filename,line,message);
				vector.addElement(matcher);
			}
			catch(REException re)
			{
				Log.log(Log.ERROR,ConsolePlugin.class,
					"Invalid regexp: " + match);
				Log.log(Log.ERROR,ConsolePlugin.class,re);
			}

			i++;
		}

		errorMatchers = new ErrorMatcher[vector.size()];
		vector.copyInto(errorMatchers);
	}

	// private members
	private static ErrorMatcher[] errorMatchers;
}
