/*
 *  $Id$
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.tree;

import javax.swing.tree.TreeModel;
import projectviewer.*;

/** A <code>TreeModel</code> that has the capability to be a branch in a
 * much larger tree.
 */
public interface Branch extends TreeModel {

	/** Set the parent of the branch.
	 *
	 * @param  owner  The new branchOwner value
	 */
	public void setBranchOwner(BranchOwner owner);

}

