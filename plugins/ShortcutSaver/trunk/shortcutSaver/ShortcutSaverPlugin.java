/*
 *  ShortcutSaverPlugin.java - ShortcutSaver plugin
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.util.Log;

import common.gui.PopupList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;


/**
 *  A plugin for mapping multiple actions to a single action
 *
 *@author    <A HREF="mailto:carmine.lucarelli@lombard.ca">Carmine Lucarelli</A>
 */
public class ShortcutSaverPlugin extends EditPlugin
{
	/**
	 *  Run the action associated with the current buffer.  Actions are associated with
	 *  buffer edit modes and file name extensions.  If there is more than one, show a
	 *  choose action dialog.
	 */
	public static void callActionForBuffer(final View view, Buffer buffer, int actionNumber)
	{
		ArrayList actions = ActionUtils.resolveActionForBuffer(buffer, actionNumber);

		if(actions.size() == 0)
		{
			String[] args = { String.valueOf(actionNumber), buffer.getMode().getName() };
			GUIUtilities.error(view, "shortcutSaver.error.noaction", args);
			return;
		}

		if(actions.size() > 1)
		{
			ArrayList listItems = new ArrayList();
			for(int i = 0; i < actions.size(); i++)
				listItems.add(new ShortcutSaverListItem(((EditAction)actions.get(i))));
			final PopupList pop = new PopupList();
			pop.setItems(listItems);
			pop.setRequestTextAreaFocusOnCancel(true);
			pop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					EditAction action = (EditAction)pop.getSelectedActualItem();
					action.invoke(view);
				}
			});
			pop.show(view);
		}
		else
			((EditAction)actions.get(0)).invoke(view);
	}
}

