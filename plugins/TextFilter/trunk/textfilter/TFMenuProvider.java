/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package textfilter;

//{{{ Imports
import java.util.Arrays;
import java.util.Comparator;

import java.awt.Component;

import javax.swing.JMenu;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
import org.gjt.sp.jedit.menu.EnhancedMenuItem;
//}}}

/**
 *	Dynamic menu implementation for jEdit 4.2. This shows a menu with all the
 *	action in jEdit's plugin menu, allowing the user to change the active
 *	project without having to go to the PV dockable. Since jEdit doesn't allow
 *	dynamic sub-menus, this has to take care of the static entries also.
 *
 *	<p>Implementation note: you need to update this class when changing the
 *	number of static entries in the menu.</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public final class TFMenuProvider implements DynamicMenuProvider {

	//{{{ +update(JMenu) : void
	public void update(JMenu menu) {
		if (menu.getItemCount() == 0) {
			Component[] others = GUIUtilities.loadMenu("textfilter.menu")
									.getMenuComponents();
			for (int i = 0; i < others.length; i++) {
				menu.add(others[i]);
			}
		} else {
			while (menu.getItemCount() != 3)
				menu.remove(3);
		}

		ActionManager am = ActionManager.getInstance();
		if (am.getActionSet().getActionCount() > 0) {
			menu.addSeparator();
			EditAction[] actions = am.getActionSet().getActions();
			Arrays.sort(actions, new EditActionComparator());

			for (int i = 0; i < actions.length; i++) {
				EnhancedMenuItem mi = new EnhancedMenuItem(actions[i].getLabel(),
											actions[i].getName(),
											jEdit.getActionContext());
				menu.add(mi);
			}
		}
	} //}}}

	//{{{ +updateEveryTime() : boolean
	/** We don't want to update every time; returns false. */
	public boolean updateEveryTime() {
		return false;
	} //}}}

}

