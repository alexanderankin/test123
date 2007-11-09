/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.event;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.VFSPathSelected;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;

/**
 *  An update that notifies that a node in the tree has been selected.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      PV 3.0.0
 */
public final class NodeSelectionUpdate extends VFSPathSelected
{

    private final ProjectViewer viewer;
    private final VPTNode node;

    /**
     *  Construct a new selection update message for the given viewer
     *  and node.
     */
    public NodeSelectionUpdate(ProjectViewer v,
                               VPTNode n)
    {
        super(v.getView(), n.getNodePath(), n.isDirectory());
        this.viewer = v;
        this.node = n;
    }

    /**
     *  Returns the {@link ProjectViewer} where the event was generated.
     */
    public ProjectViewer getViewer()
    {
        return viewer;
    }

    /**
     *  Returns the node that has been selected.
     */
    public VPTNode getNode()
    {
        return node;
    }

}

