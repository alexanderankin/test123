/*
 * TextToolsPlugin.java - Plugin for a number of text related functions
 * Copyright (C) 1999 mike dillon, Slava Pestov
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

import java.util.Vector;
import javax.swing.JMenu;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

public class TextToolsPlugin extends EditPlugin
{
	public void start()
	{
		jEdit.addAction(new remove_trailing_ws());
		jEdit.addAction(new sort_lines());
		jEdit.addAction(new revsort_lines());
		jEdit.addAction(new reverse());
		jEdit.addAction(new rot13());
		jEdit.addAction(new format());
		jEdit.addAction(new insert_date());
		jEdit.addAction(new transpose_chars());
		jEdit.addAction(new transpose_words());
		jEdit.addAction(new transpose_lines());
	}

	public void stop()
	{
	}

	public void createMenuItems(View view, Vector menus, Vector menuItems)
	{
		JMenu myMenu = GUIUtilities.loadMenu(view, "text-tools");

		menus.addElement(myMenu);
	}
}
