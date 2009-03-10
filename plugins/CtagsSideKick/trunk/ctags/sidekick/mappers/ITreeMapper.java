/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ctags.sidekick.mappers;
import java.util.Vector;

import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;


public interface ITreeMapper extends IObjectProcessor {
	
	Vector<Object> getPath(Tag tag);
	void setLang(String lang);
	CollisionHandler getCollisionHandler();
	
	// The collision handler is invoked when all tags have been added
	// to the tree; its purpose is to re-map children of identically-named
	// nodes (collisions) located under the same parent. For example,
	// a C++ source file may include both a "forward declaration" and the
	// actual declaration of a class. Ctags will generate tags for both,
	// and both may get the same "named" tree path and be added under the
	// same parent. The nodes for the class members should be added under
	// the actual class declaration, not under the forward declaration,
	// but the mapper cannot provide this information. The tree builder
	// might first put the class member nodes under the class declaration
	// tag, or maybe split the member nodes between the two declaration
	// nodes. When all tags have been added to the tree, the collision
	// handler is invoked with the two class declaration nodes in the
	// vector parameter, and it should move all member nodes to the actual
	// declaration node.
	public interface CollisionHandler {
		void remapChildrenOf(Vector<CtagsSideKickTreeNode> parents);
	}
	
}

