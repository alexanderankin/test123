/*
 *  LauncherOptionPane.java - Panel in jEdit's Global Options dialog
 *  Copyright (C) 2007 Carmine Lucarelli
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package launcher.browser;

import javax.swing.JComboBox;

import launcher.Launcher;
import launcher.LauncherTypeOptionPane;

public class BrowserLauncherTypeOptionPane extends LauncherTypeOptionPane
{
	JComboBox browserChoice;

	public BrowserLauncherTypeOptionPane()
	{
		super(BrowserLauncherType.INSTANCE.getPropertyPrefix());
	}


	public void _init()
	{
        browserChoice =
        	addDefaultLauncherComboBox(BrowserLauncherType.INSTANCE);		
	}


	/**
	 *  Called when the options dialog's `OK' button is pressed. This should save
	 *  any properties saved in this option pane.
	 *
	 * @since
	 */
	public void _save()
	{
		BrowserLauncherType.INSTANCE.setDefaultLauncher(
					(Launcher)browserChoice.getSelectedItem());
	}

}


