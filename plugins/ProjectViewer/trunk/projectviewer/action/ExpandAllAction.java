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
package projectviewer.action;

//{{{ Imports
import java.awt.event.ActionEvent;

import javax.swing.Icon;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
//}}}

/**
 *	Expands all nodes of the current tree.
 *
 *	@author		Marcelo Vanzin (based on code from old ProjectViewer class)
 *	@version	$Id$
 */
public class ExpandAllAction extends Action {

	//{{{ +ExpandAllAction() : <init>
	public ExpandAllAction() {
		super("projectviewer_wrapper_expand_all");
	} //}}}

	//{{{ +getText() : String
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.expand_all");
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		JTree tree = viewer.getCurrentTree();
		expand(new TreePath(tree.getModel().getRoot()), tree);
	} //}}}

	//{{{ +expand(TreePath, JTree) : void
	/** Expand the given sub tree. */
	public void expand(TreePath path, JTree tree) {
		TreeModel model = tree.getModel();
		Object node = path.getLastPathComponent();
		if(model.isLeaf(node))
			return;
		tree.expandPath(path);

		int count = model.getChildCount(node);
		for(int i = 0; i < count; i++) {
			expand(path.pathByAddingChild(model.getChild(node, i)), tree);
		}
	}//}}}

}

