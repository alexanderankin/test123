/*
* GeneralOptionPane.java
* Copyright (c) 2006 Jakub Roztocil <j.roztocil@gmail.com>
*
* $Id$
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
*
*/

package contextmenu;

//{{{ Imports
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.jEdit;
//}}}



public class GeneralOptionPane extends AbstractOptionPane {

	private JCheckBox showInMenuBar, showInContextMenu;

	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane() {
        super("contextmenu-general");
    } //}}}

	//{{{ _init()
	protected void _init() {
		showInMenuBar = new JCheckBox(jEdit.getProperty("contextmenu.in-menubar.label"),
										jEdit.getBooleanProperty("contextmenu.in-menubar"));

		add(showInMenuBar);

		showInContextMenu = new JCheckBox(jEdit.getProperty("contextmenu.in-popup.label"),
											jEdit.getBooleanProperty("contextmenu.in-popup"));
		add(showInContextMenu);

	} //}}}

	//{{{ _save()
	protected void _save() {
		jEdit.setBooleanProperty("contextmenu.in-menubar", showInMenuBar.isSelected());
		jEdit.setBooleanProperty("contextmenu.in-popup", showInContextMenu.isSelected());
	} //}}}

}


/* :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false: */
