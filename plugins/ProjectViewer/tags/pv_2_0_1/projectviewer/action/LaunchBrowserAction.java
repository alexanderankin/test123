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

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Opens the selected file in the configured web-browser. Files that are under
 *	the project root are treated differently: the project's URL root is used
 *	to build the URL. For other files, or if the projects URL is not set, the
 *	absolute path to the file is given to the browser.
 *
 *	@author		Marcelo Vanzin (based on code from Matthew Payne)
 *	@version	$Id$
 */
public class LaunchBrowserAction extends Action {

	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.launch_browser");
	} //}}}

	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		VPTFile file = (VPTFile) viewer.getSelectedNode();
		VPTProject p = VPTNode.findProjectFor(file);

		String sURL;
		if (p.getURL() != null && file.getNodePath().startsWith(p.getRootPath())) {
			sURL = p.getURL() + file.getNodePath().substring(p.getRootPath().length() + 1);
		} else {
			sURL = file.getNodePath();
		}

		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec(new String[] { ProjectViewerConfig.getInstance().getBrowserPath(), sURL });
		} catch(java.io.IOException ioe) {
			JOptionPane.showMessageDialog(viewer,
				jEdit.getProperty("projectviewer.launcher.io_error", new Object[] { ioe.getMessage() }),
				jEdit.getProperty("projectviewer.error"),
				JOptionPane.ERROR_MESSAGE);
		}
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		cmItem.setVisible(node != null && node.isFile());
	} //}}}

}

