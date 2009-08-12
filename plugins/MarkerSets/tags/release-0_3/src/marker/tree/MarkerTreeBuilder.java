/**
 * 
 */
package marker.tree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import marker.tree.SourceLinkTree.SourceLinkParentNode;

public interface MarkerTreeBuilder
{
	boolean rebuildOnChange();
	void buildSubTree(SourceLinkParentNode parent,
		Vector<DefaultMutableTreeNode> markerNodes);
}