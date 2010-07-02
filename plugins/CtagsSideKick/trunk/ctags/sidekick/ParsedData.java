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

package ctags.sidekick;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.FoldHandler;

import sidekick.IAsset;
import sidekick.SideKickParsedData;
import ctags.sidekick.filters.ITreeFilter;
import ctags.sidekick.mappers.ITreeMapper;
import ctags.sidekick.mappers.KindTreeMapper;
import ctags.sidekick.options.GeneralOptionPane;
import ctags.sidekick.renderers.IIconProvider;
import ctags.sidekick.renderers.ITextProvider;
import ctags.sidekick.renderers.NameAndSignatureTextProvider;
import ctags.sidekick.sorters.ITreeSorter;


public class ParsedData extends SideKickParsedData
{
	static FoldHandler foldHandler = new ctags.sidekick.CtagsFoldHandler();
	ITreeMapper mapper = null;
	ITreeSorter sorter = null;
	ITreeFilter filter = null;
	ITextProvider textProvider = null;
	IIconProvider iconProvider = null;
	CtagsSideKickTreeNode tree = new CtagsSideKickTreeNode();
	
	public ParsedData(Buffer buffer, String lang)
	{
		super(buffer.getName());
		String mode = buffer.getMode().getName();
		mapper = (ITreeMapper) MapperManager.getInstance().getProcessorForMode(mode);
		mapper.setLang(lang);
		sorter = (ITreeSorter) SorterManager.getInstance().getProcessorForMode(mode);
		filter = (ITreeFilter) FilterManager.getInstance().getProcessorForMode(mode);
		textProvider = (ITextProvider) TextProviderManager.getInstance().getProcessorForMode(mode);
		if (textProvider == null)
			textProvider = new NameAndSignatureTextProvider();
		if (jEdit.getBooleanProperty(GeneralOptionPane.SHOW_ICONS, false))
			iconProvider = (IIconProvider)
				IconProviderManager.getInstance().getProcessorForMode(mode);
	}
	
	void add(Tag tag)
	{
		if (filter != null && filter.pass(tag) == false)
			return;
		if (textProvider != null)
			tag.setTextProvider(textProvider);
		if (iconProvider != null)
			tag.setIcon(iconProvider.getIcon(tag));
		if (mapper == null)
		{
			tree.putChild(tag, false);
			return;
		}
		boolean deferCollisions = (mapper.getCollisionHandler() != null);
		Vector<Object> path = mapper.getPath(tag);
		path.add(tag);
		CtagsSideKickTreeNode node = tree; 
		for (int i = 0; i < path.size(); i++)
			node = node.putChild(path.get(i), deferCollisions);
	}
	public void done()
	{
		if (sorter != null)
			tree.sort(sorter);
		if (mapper instanceof KindTreeMapper)
			tree.addChildCounts();
		tree.addToTree(root, mapper.getCollisionHandler());
	}
	private static boolean assetContains(IAsset asset, int offset)
	{
		return offset >= asset.getStart().getOffset()
		    && offset < asset.getEnd().getOffset();
	}
	protected TreeNode getNodeAt(TreeNode parent, int offset)
	{
		for (int i = 0; i < parent.getChildCount(); i++)
		{
			TreeNode node = parent.getChildAt(i);
			// First check node's children recursively (DFS)
			TreeNode ret = getNodeAt(node, offset);
			if (ret != null)
				return ret;
			// If not in the children - check node
			IAsset asset = getAsset(node);
			if ((asset != null) && assetContains(asset, offset))
				return node;
		}
		IAsset asset = getAsset(parent);
		if ((asset != null) && assetContains(asset, offset))
			return parent;
		return null;
	}

	protected FoldHandler getFoldHandler() {
		return foldHandler;
	}
}
