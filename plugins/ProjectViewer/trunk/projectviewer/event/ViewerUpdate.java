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

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.View;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;

/**
 *  A project viewer update message sent on the Edit Bus.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      PV 3.0.0
 */
public final class ViewerUpdate extends EBMessage
{

    public static enum Type
    {
        /** Notifies that a node has been selected. */
        NODE_SELECTED,

        /** Notifies the changing of the active project. */
        PROJECT_LOADED,

        /** Notifies that a group has been activated, or selected. */
        GROUP_ACTIVATED,
    }

    private final boolean isViewer;
    private final VPTNode node;
    private final VPTGroup oldParent;
    private final Type type;

    /**
     *  Construct a new message with the given type and no associated
     *  viewer. {@link #getViewer()} might still return a viewer, if
     *  the dockable for the view is activated.
     */
    public ViewerUpdate(View v,
                        VPTNode n,
                        Type type)
    {
        super(v);
        this.node = n;
        this.type = type;
        this.oldParent = null;
        this.isViewer = false;
    }

    /**
     *  Construct a new message with the given type and an associated
     *  viewer.
     */
    public ViewerUpdate(ProjectViewer v,
                        VPTNode n,
                        Type type)
    {
        super(v);
        this.node = n;
        this.type = type;
        this.oldParent = null;
        this.isViewer = true;
    }

    /** @return The event type of this message. */
    public Type getType()
    {
        return type;
    }

    /**
     *  Returns the {@link ProjectViewer}. This may be null if the
     *  dockable hasn't been activated yet.
     */
    public ProjectViewer getViewer()
    {
        if (isViewer) {
            return (ProjectViewer) getSource();
        } else {
            return ProjectViewer.getViewer((View)getSource());
        }
    }

    /**
     *  Returns the view where the event occurred.
     */
    public View getView()
    {
        if (isViewer) {
            return ((ProjectViewer)getSource()).getView();
        } else {
            return (View) getSource();
        }
    }

    /**
     *  Returns the node affected by the event being notified. The
     *  node can be null, a project or a group.
     */
    public VPTNode getNode()
    {
        return node;
    }

    /**
     *  When firing a NODE_MOVED message, returns the old parent of the
     *  affected node (which can be retrieved by getSource()).
     */
    public VPTGroup getOldParent()
    {
        return oldParent;
    }

}

