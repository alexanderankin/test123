/*
 * ConsolePlugin.java - Console plugin
 * Copyright (C) 1999, 2000 Slava Pestov
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

public class ConsolePlugin extends EBPlugin
{
	public static final Shell SHELL = new DefaultShell();

	public void start()
	{
		// initialize console shell
		errorSource = new DefaultErrorSource("console");
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
		EditBus.addToBus(errorSource);
		EditBus.addToNamedList(Shell.SHELLS_LIST,SHELL);

		// initialize console GUI
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,"console");

		jEdit.addAction(new OpenAction());
		jEdit.addAction(new SelectShellAction());
	}

	public void createMenuItems(View view, Vector menus, Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenuItem("console"));
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
	static void addError(int type, String file, int line, String message)
	{
		errorSource.addError(type,file,line,0,0,message);
	}

	static void clearErrors()
	{
		errorSource.clear();
	}

	static int parseLine(String text)
	{
		if(errorMatchers == null)
			loadMatchers();

		for(int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];
			int result = m.match(text);
			if(result != -1)
				return result;
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
	private static DefaultErrorSource errorSource;
	private static ErrorMatcher[] errorMatchers;

	class OpenAction extends EditAction
	{
		OpenAction()
		{
			super("console");
		}

		public void actionPerformed(ActionEvent evt)
		{
			// If action command is null, just toggle visibility,
			// otherwise if an action command is set (eg if run from
			// a macro) we run that command
			View view = getView(evt);
			DockableWindowManager wm = view.getDockableWindowManager();

			String actionCommand = evt.getActionCommand();
			if(actionCommand != null)
			{
				wm.addDockableWindow("console");
				Console console = (Console)wm.getDockableWindow("console");
				console.run(actionCommand);
			}
			else
			{
				wm.toggleDockableWindow("console");
			}
		}

		public boolean isToggle()
		{
			return true;
		}

		public boolean isSelected(Component comp)
		{
			return getView(comp).getDockableWindowManager()
				.isDockableWindowVisible("console");
		}
	}

	class SelectShellAction extends EditAction
	{
		SelectShellAction()
		{
			super("console-shell");
		}

		public void actionPerformed(ActionEvent evt)
		{
			View view = getView(evt);
			DockableWindowManager wm = view.getDockableWindowManager();
			wm.addDockableWindow("console");
			Console console = (Console)wm.getDockableWindow("console");
			Object[] shells = EditBus.getNamedList(Shell.SHELLS_LIST);
			for(int i = 0; i < shells.length; i++)
			{
				Shell shell = (Shell)shells[i];
				if(shell.getName().equals(evt.getActionCommand()))
				{
					console.setShell(shell);
					break;
				}
			}
		}
	}
}
