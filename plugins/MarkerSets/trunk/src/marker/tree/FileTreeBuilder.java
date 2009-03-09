/**
 * 
 */
package marker.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import marker.FileMarker;
import marker.tree.SourceLinkTree.SourceLinkParentNode;

public class FileTreeBuilder implements MarkerTreeBuilder
{
	public String toString()
	{
		return "File";
	}
	public boolean rebuildOnChange()
	{
		return true;
	}
	public void buildSubTree(SourceLinkParentNode parent,
		Vector<DefaultMutableTreeNode> markerNodes)
	{
		// First, group the marker nodes by file
		HashMap<String, Vector<DefaultMutableTreeNode>> fileNodes =
			new HashMap<String, Vector<DefaultMutableTreeNode>>();
		for (DefaultMutableTreeNode markerNode: markerNodes)
		{
			FileMarker marker = (FileMarker) markerNode.getUserObject();
			String file = marker.file;
			Vector<DefaultMutableTreeNode> markers = fileNodes.get(file);
			if (markers == null)
			{
				markers = new Vector<DefaultMutableTreeNode>();
				fileNodes.put(file, markers);
			}
			markers.add(markerNode);
		}
		// Now, build the sub tree
		Vector<String> paths = new Vector<String>(fileNodes.keySet());
		Collections.sort(paths);
		for (String path: paths)
		{
			DefaultMutableTreeNode pathNode = new DefaultMutableTreeNode(path);
			parent.add(pathNode);
			for (DefaultMutableTreeNode markerNode: fileNodes.get(path))
				pathNode.add(markerNode);
		}
	}
}