/**
 * 
 */
package marker.tree;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import marker.FileMarker;
import marker.tree.SourceLinkTree.SourceLinkParentNode;

public class FolderTreeBuilder implements MarkerTreeBuilder
{
	public String toString()
	{
		return "Folder";
	}
	public boolean rebuildOnChange()
	{
		return true;
	}
	@SuppressWarnings("unchecked")
	public void buildSubTree(SourceLinkParentNode parent,
		Vector<DefaultMutableTreeNode> markerNodes)
	{
		// First break all paths to parts
		HashMap<String, Object> paths = new HashMap<String, Object>();
		HashMap<String, Object> current;
		for (DefaultMutableTreeNode markerNode: markerNodes)
		{
			FileMarker marker = (FileMarker) markerNode.getUserObject();
			current = paths;
			String path = marker.file;
			String [] parts = path.split("[/\\\\]");
			for (int i = 0; i < parts.length - 1; i++)
			{
				HashMap<String, Object> child =
					(HashMap<String, Object>) current.get(parts[i]);
				if (child == null)
				{
					child = new HashMap<String, Object>();
					current.put(parts[i], child);
				}
				current = child;
			}
			String filePart = parts[parts.length - 1];
			Vector<DefaultMutableTreeNode> markers =
				(Vector<DefaultMutableTreeNode>) current.get(filePart);
			if (markers == null)
			{
				markers = new Vector<DefaultMutableTreeNode>();
				current.put(filePart, markers);
			}
			markers.add(markerNode);
		}
		// Now, consolidate matching parts
		consolidateTree("", parent, paths);
	}
	
	@SuppressWarnings("unchecked")
	private void consolidateTree(String parent,
		DefaultMutableTreeNode parentNode, HashMap<String, Object> paths)
	{
		Vector<String> roots = new Vector<String>(paths.keySet());
		Collections.sort(roots);
		for (String r: roots)
		{
			HashMap<String, Object> current = paths;
			String childPath = (parent.length() > 0) ?
				(parent + File.separator + r) : r;
			Object child = current.get(r);
			if (child instanceof HashMap)
			{
				current = (HashMap<String, Object>) child;
				if (current.keySet().size() == 1)
					consolidateTree(childPath, parentNode, current);
				else
				{
					DefaultMutableTreeNode childNode =
						new DefaultMutableTreeNode(childPath);
					parentNode.add(childNode);
					consolidateTree("", childNode, current);
				}
			}
			else
			{
				DefaultMutableTreeNode fileNode =
					new DefaultMutableTreeNode(childPath);
				parentNode.add(fileNode);
				Vector<DefaultMutableTreeNode> markers =
					(Vector<DefaultMutableTreeNode>) child;
				for (DefaultMutableTreeNode marker: markers)
					fileNode.add(marker);
			}
		}
	}
}