package sidekick.perl;

import javax.swing.tree.*;
import javax.swing.text.Position;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import sidekick.*;


/**
 * PerlParsedData:
 * extends SidekickParsedData because we need a special getTreeForPosition method 
 *
 * @author     Martin Raspe
 * @created    April 11, 2005
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */

public class PerlParsedData extends SideKickParsedData {
	//{{{ constructor
/**	 * Constructs a new PerlParsedData object
	 *
	 * @param name See sidekick.SidekickParsedData.
	 */
	public PerlParsedData(String name) {
		super(name);
	} //}}}
	
	//{{{ getTreePathForPosition() method
/**	 * gets the tree path for a text position
	 * largely copied from SidekickParsedData
	 * @param dot See sidekick.SidekickParsedData.
	 */
	public TreePath getTreePathForPosition(int dot)
	{
		if(root.getChildCount() == 0)
			return null;

		ArrayList _path = new ArrayList();
		for(int i = root.getChildCount() - 1; i >= 0; i--)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				root.getChildAt(i);
			if(getTreePathForPosition(node,dot,_path))
			{
				_path.add(node);
				break;
			}
		}

		if(_path.size() == 0)
		{
			// nothing found
			return null;
		}
		else
		{
			Object[] path = new Object[_path.size() + 1];
			path[0] = root;
			int len = _path.size();
			for(int i = 0; i < len; i++)
				path[i + 1] = _path.get(len - i - 1);

			TreePath treePath = new TreePath(path);
			return treePath;
		}
	} //}}}

	//{{{ getTreePathForPosition() method
	private boolean getTreePathForPosition(TreeNode node, int dot, List path)
	{
		int childCount = node.getChildCount();
		Object userObject = ((DefaultMutableTreeNode)node).getUserObject();
		if(!(userObject instanceof Asset))
			return false;

		Asset asset = (Asset)userObject;

		// return true if any of our children contain the caret
		for(int i = childCount - 1; i >= 0; i--)
		{
			TreeNode _node = node.getChildAt(i);
			if(getTreePathForPosition(_node,dot,path))
			{
				path.add(_node);
				return true;
			}
		}
		// otherwise return true if we contain the caret
		return (dot >= asset.start.getOffset() && dot < asset.end.getOffset());
	} //}}}
	

}
