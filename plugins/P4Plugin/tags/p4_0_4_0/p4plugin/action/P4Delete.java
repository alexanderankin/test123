/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2010 Marcelo Vanzin
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
package p4plugin.action;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import p4plugin.Perforce;

/**
 * Calls "p4 delete" on the selected file and removes it from the
 * project tree.
 *
 * @author     Marcelo Vanzin
 * @version    $Id$
 * @since      P4P 0.4.0
 */
public class P4Delete extends P4FileAction
{

    public P4Delete(boolean defaultCL)
    {
        super("delete", defaultCL);
    }


    protected void postProcess(Perforce p4)
    {
        String path = getPath();
        VPTNode node = null;
        super.postProcess(p4);
        if (path == null) {
            node = viewer.getSelectedNode();
        } else {
            VPTProject proj = ProjectViewer.getActiveProject(viewer.getView());
            if (proj != null) {
                node = proj.getChildNode(path);
            }
        }

        if (node != null) {
            ProjectViewer.removeNodeFromParent(node);
        }
    }

}

