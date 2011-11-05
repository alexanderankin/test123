/*
 *  MacroManagerPlugin.java
 *  Copyright (C) 2002 Carmine Lucarelli
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
package macroManager;

import macroManager.popup.*;

import org.gjt.sp.jedit.*;


/**
 *  Description of the Class
 */
public class MacroManagerPlugin extends EditPlugin
{
	/**
	 *  Description of the Method
	 *
	 *@param  view Description of the Parameter
	 */
	public static void showPopup(final View view)
	{
		FileSelectionListener listener = new MacroSelectionListener(view);
		FindFileWindow ffw = new FindFileWindow();
		ffw.addFileSelectionListener(listener);
		ffw.showWindow();
	}


	/**
	 *  Listens for import selection from popup import window.
	 */
	private static class MacroSelectionListener implements FileSelectionListener
	{
		private View mView;

		/**
		 *  Constructor for the MacroSelectionListener object
		 */
		public MacroSelectionListener(View view)
		{
			mView = view;
		}


		public void fileSelected(EditAction editAction)
		{
			editAction.invoke(mView);
		}
	}

}

