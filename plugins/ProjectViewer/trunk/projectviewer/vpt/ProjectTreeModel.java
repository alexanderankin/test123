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
package projectviewer.vpt;

import javax.swing.tree.DefaultTreeModel;

/**
 * A tree model that defines a few methods used internally by
 * ProjectViewer.
 *
 * @author     Marcelo Vanzin
 * @version    $Id$
 * @since      PV 3.0.0
 */
public abstract class ProjectTreeModel extends DefaultTreeModel
{

    protected ProjectTreeModel(VPTNode root)
    {
        super(root, true);
    }


    /**
     * Returns the name of the tree, to be shown in the tabbed
     * pane GUI. The name can also be a key for a jEdit property.
     */
    protected abstract String getName();

    /**
     * Returns whether this model shows files in a nin-hierarchical
     * ("flat") manner. "Flat" trees receive a few different
     * notifications than non-flat ones: basically, they don't get
     * notifications for changes in directories - rather, they are
     * notified of a change in the project's structure instead.
     */
    protected boolean isFlat()
    {
        return false;
    }


    /**
     * Called when a project's file is opened in jEdit.
     */
    protected void fileOpened(VPTNode file)
    {

    }

    /**
     * Called when a project's file is opened in jEdit.
     */
    protected void fileClosed(VPTNode file)
    {

    }


    /**
     * Called when a project had been closed.
     */
    protected void projectClosed(VPTProject p)
    {

    }

}

