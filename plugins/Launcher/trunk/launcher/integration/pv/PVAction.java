/*
Copyright (c) 2010, François Rey, Dale Anson (some code copied from SVNPlugin)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package launcher.integration.pv;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import launcher.LauncherPlugin;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import projectviewer.vpt.VPTNode;

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as the menu for a pull-out menu containing the applicable launcher actions.
 */
public class PVAction extends projectviewer.action.Action {

	private final JMenu menu = new JMenu(LauncherPlugin.NAME);

    public PVAction() {
    }

    // this won't be displayed in the PV context menu
    public String getText() {
        return "Launcher";
    }

    // returns the menu
    public JComponent getMenuItem() {
        return menu;
    }

    // called by ProjectViewer to let us know the currently selected node in
    // the PV tree.
    public void prepareForNode( VPTNode node ) {
    	Log.log(Log.DEBUG, this, "preparing for " + node.getNodePath());
		List<VPTNode> nodes = PVHelper.getSelectedNodes(viewer);
		if (nodes.size() > 0) {
	    	menu.removeAll();
			LauncherPlugin plugin = (LauncherPlugin)jEdit.getPlugin(LauncherPlugin.class.getName());
			File[] files = PVHelper.resolveToFileArray(nodes, false);
			JMenuItem[] items = plugin.getMenuItemsFor(files);
			LauncherUtils.addItemsToMenu(menu, items);
		}
    }

	public void actionPerformed( ActionEvent ae ) {
        // does nothing, this is the top of a pull out menu so has no specific
        // action other than to display the pull out.
    }


}
