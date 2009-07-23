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
import org.gjt.sp.jedit.EditBus;

import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;

/**
 *  An update message describing some change in the data kept by
 *  the plugin.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      PV 3.0.0
 */
public final class StructureUpdate extends EBMessage
{

    public static enum Type
    {
        /** Notifies the creation of a project. */
        PROJECT_ADDED,

        /** Notifies the removal of a project. */
        PROJECT_REMOVED,

        /** Notifies the addition of a group. */
        GROUP_ADDED,

        /** Notifies the removal of a group. */
        GROUP_REMOVED,

        /** Notifies that a project or group has been moved to another group. */
        NODE_MOVED;
    }

    public static void send(VPTNode node, Type type)
    {
        EditBus.send(new StructureUpdate(node, type));
    }

    public static void send(VPTNode node, VPTGroup parent)
    {
        EditBus.send(new StructureUpdate(node, parent));
    }

    private final Type type;
    private final VPTGroup parent;

    /** Constructs a new update with the given type. */
    private StructureUpdate(VPTNode node, Type type)
    {
        super(node);
        assert (type != Type.NODE_MOVED) : "Use the other constructor.";
        this.type = type;
        this.parent = null;
    }

    /** Constructs a new NODE_MOVED update message. */
    private StructureUpdate(VPTNode node, VPTGroup parent)
    {
        super(node);
        this.type = Type.NODE_MOVED;
        this.parent = parent;
    }

    /** @return The event type of this message. */
    public Type getType()
    {
        return type;
    }

    /** @return The affected node. */
    public VPTNode getNode()
    {
        return (VPTNode) getSource();
    }

    /** @return The old parent, if type is NODE_MOVED. */
    public VPTGroup getOldParent()
    {
        return parent;
    }

}

