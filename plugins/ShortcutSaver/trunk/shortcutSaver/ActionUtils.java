/*
 *  ActionUtils.java - ShortcutSaver plugin
 *  Copyright (C) 2003 Carmine Lucarelli
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

package shortcutSaver;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 *  A utility class to handle action retrieval based on buffer properties
 *
 *@author    <A HREF="mailto:carmine.lucarelli@lombard.ca">Carmine Lucarelli</A>
 */
public class ActionUtils
{
	public static final String PROP_PREFIX = "ShortcutSaver.ActionList.";

	/**
	 *  Get a list of all actions associated with the given buffer.  Actions are
	 *  listed, by name, in properties.  These are based on:
	 *  <ul>
	 *  <li> The buffer's edit mode</li>
	 *  <li> The file's extension</li>
	 *  </ul>
	 *  
	 */
	public static ArrayList resolveActionForBuffer(Buffer buffer, int actionNumber)
	{
		ArrayList actions = new ArrayList();
		String work = buffer.getMode().getName();
		String actList = jEdit.getProperty(PROP_PREFIX + actionNumber + "." + work);
		ActionUtils.addActions(actList, actions);
		work = MiscUtilities.getFileExtension(MiscUtilities.getFileName(buffer.getPath()));
		actList = jEdit.getProperty(PROP_PREFIX + work);
		ActionUtils.addActions(actList, actions);
		return actions;
	}

	/**
	 *  Split the given string into tokens and retrieve the EditAction
	 *  for each one.
	 */
	private static void addActions(String actList, ArrayList actions)
	{
		if(actList == null)
			return;
		
		StringTokenizer st = new StringTokenizer(actList);
		while(st.hasMoreTokens())
		{
			String name = st.nextToken();
			EditAction action = jEdit.getAction(name);
			if(action != null)
				actions.add(action);
			else
				Log.log(Log.DEBUG, ActionUtils.class, "Unknown action name: " + name);
		}
	}
}

