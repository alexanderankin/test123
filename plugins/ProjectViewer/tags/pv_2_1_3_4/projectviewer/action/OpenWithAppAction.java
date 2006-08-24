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

import javax.swing.JMenuItem;

import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTNode;
import projectviewer.config.AppLauncher;
//}}}

/**
 *	Opens a file with a registered app, or ask for an application in case none
 *	is configured for the file type.
 *
 *	@author		Marcelo Vanzin
 *	@see		projectviewer.config.AppLauncher
 *	@version	$Id$
 */
public class OpenWithAppAction extends Action {

	private static final AppLauncher appList = AppLauncher.getInstance();

	//{{{ +OpenWithAppAction() : <init>
	public OpenWithAppAction() {
		super("projectviewer_wrapper_openwith");
	} //}}}

	//{{{ +getText() : String
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.launcher.open_with_none");
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		VPTNode node = (VPTNode) viewer.getSelectedNode();
		appList.launchApp(node.getNodePath(), viewer);
	} //}}}

	//{{{ +prepareForNode(VPTNode) : void
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (node != null && node.canOpen()) {
			cmItem.setVisible(true);
			// "Beautify" the "Open with..." entry
			String app = appList.getAppName(node.getNodePath());
			if (app != null) {
				int idx = app.lastIndexOf("/");
				if (idx != -1) {
					app = app.substring(idx + 1, app.length());
				}
				((JMenuItem)cmItem).setText(jEdit.getProperty("projectviewer.launcher.open_with", new Object[] { app } ));
			} else {
				((JMenuItem)cmItem).setText(jEdit.getProperty("projectviewer.launcher.open_with_none"));
			}
		} else {
			cmItem.setVisible(false);
		}
	} //}}}

}

