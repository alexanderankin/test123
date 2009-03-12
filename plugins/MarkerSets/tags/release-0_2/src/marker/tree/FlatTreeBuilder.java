/**
 * 
 */
package marker.tree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import marker.tree.SourceLinkTree.SourceLinkParentNode;

public class FlatTreeBuilder implements MarkerTreeBuilder
{
	public String toString()
	{
		return "Flat";
	}
	public boolean rebuildOnChange()
	{
		return false;
	}
	public void buildSubTree(SourceLinkParentNode parent,
		Vector<DefaultMutableTreeNode> markerNodes)
	{
		for (DefaultMutableTreeNode markerNode: markerNodes)
			parent.add(markerNode);
	}
}