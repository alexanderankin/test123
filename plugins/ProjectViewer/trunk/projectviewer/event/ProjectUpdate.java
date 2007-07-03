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

import java.util.List;
import org.gjt.sp.jedit.EBMessage;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;

/**
 *  A project update message sent on the Edit Bus.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      PV 3.0.0
 */
public final class ProjectUpdate extends EBMessage
{

    public static enum Type
    {
        /** Update for when files have been added to a project. */
        FILES_CHANGED,

        /** Update for when the project's properties have changed. */
        PROPERTIES_CHANGED;
    }

    private final Type type;
    private final List<VPTFile> added;
    private final List<VPTFile> removed;

    /** Construct a new message with type FILES_CHANGED. */
    public ProjectUpdate(VPTProject p,
                         List<VPTFile> added,
                         List<VPTFile> removed)
    {
        super(p);
        this.added = added;
        this.removed = removed;
        this.type = Type.FILES_CHANGED;
    }

    /** Construct a new message with type PROPERTIES_CHANGED. */
    public ProjectUpdate(VPTProject p) {
        super(p);
        this.type = Type.PROPERTIES_CHANGED;
        this.added = null;
        this.removed = null;
    }

    /** @return The affected project. */
    public VPTProject getProject()
    {
        return (VPTProject) getSource();
    }

    /** @return The event type of this message. */
    public Type getType()
    {
        return type;
    }

    /** @return The list of added files (may be null). */
    public List<VPTFile> getAddedFiles()
    {
        return added;
    }

    /** @return The list of removed files (may be null). */
    public List<VPTFile> getRemovedFiles()
    {
        return removed;
    }

}

