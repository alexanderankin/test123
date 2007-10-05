/*
 * XInsertActions.java
 * Written (c) 2005 by Martin Raspe (raspe@biblhertz.it), using
 * ClipperActions.java
 * ( Copyright (c) 2001 John Gellene, Copyright (c) 2001, 2002 Andre Kaplan )
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

import javax.swing.JTree;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

/**
 * XInsertActions: provides actions that can be bound to keyboard shortcuts
 *
 * @author     Martin Raspe
 * @created    March 5, 2005
 * @modified   $Date: 2005/05/11 12:27:51 $ by $Author: hertzhaft $
 * @version    $Revision: 1.1 $
 */
public class XInsertActions {

    public static void goToXInsert(View view) {
	// toggle focus of XInsert tree (making it visible if necessary)
        DockableWindowManager wm = view.getDockableWindowManager();
        if (!wm.isDockableWindowVisible(XInsertPlugin.NAME)) 
		wm.showDockableWindow(XInsertPlugin.NAME);
        XTree xtree = (XTree) wm.getDockableWindow(XInsertPlugin.NAME);
        if (xtree == null) return; // should never happen
	JTree tree = xtree.getTree();
	if (tree.hasFocus()) { // return to TextArea if tree is already focused
		view.getTextArea().requestFocus();
		return;
		};
	xtree.requestFocus();
	tree.requestFocus();
        }

    public static void insertSelected(View view) {
        // insert selected item from XInsert tree
	DockableWindowManager wm = view.getDockableWindowManager();
        //if (!wm.isDockableWindowVisible(XInsertPlugin.NAME)) return;
        XTree xtree = (XTree) wm.getDockableWindow(XInsertPlugin.NAME);
        if (xtree != null) xtree.treeAction();
        }
}

