/*
* Code2HTMLPlugin.java
* Copyright (c) 2000 Andre Kaplan
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

// import org.gjt.sp.jedit.EBComponent;
// import org.gjt.sp.jedit.EditBus;
// import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.gui.OptionsDialog;

import org.gjt.sp.util.Log;

/**
 * Code2HTML Mode plugin
 * 
 * @author  Andre Kaplan
 */
public class Code2HTMLPlugin
	extends EditPlugin // EBPlugin
{
	public Code2HTMLPlugin() {
		super();
	}

	public void start() {
		jEdit.addAction(new code2html_current_buffer());
		jEdit.addAction(new code2html_current_selection());
	}

	public void stop() {}

	public void createMenuItems(View view, Vector menus, Vector menuItems) {
		JMenu menu = GUIUtilities.loadMenu(view, "code2html");
		menus.addElement(menu);
	}

    public void createOptionPanes(OptionsDialog dialog) {
        dialog.addOptionPane(new Code2HTMLOptionPane());
    }

	// public void handleMessage(EBMessage message) {}
}

