/*
 * ConsoleDockablePluginPart.java - Manages console dockables
 * Copyright (C) 2000 Slava Pestov
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class ConsoleDockablePluginPart extends EBPlugin
{
	public void start()
	{
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
	}

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
			console.setShell(evt.getActionCommand());
		}
	}
}
