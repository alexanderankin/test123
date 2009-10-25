/*
 * SideKickParsedData.java
 *
 * Copyright (C) 2003, 2004 Slava Pestov
 * portions Copyright (C) 2009 Eric Le Lay
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package sidekick.html;

// Imports
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;


import org.gjt.sp.jedit.Buffer;

import sidekick.util.SideKickAsset;
import sidekick.util.SideKickElement;

import xml.XmlParsedData;
import xml.completion.CompletionInfo;
import sidekick.html.parser.html.HtmlDocument;

/**
 * Stores a buffer structure tree.
 * 
 * Plugins can extend this class to persist plugin-specific information. For
 * example, the XML plugin stores code completion-related structures using a
 * subclass.
 * 
 * danson: modified for HtmlSideKick.
 */
public class HtmlSideKickParsedData extends XmlParsedData
{
	/**
	 * @param fileName
	 *                The file name being parsed, used as the root of the
	 *                tree.
	 */
	public HtmlSideKickParsedData(String fileName, Buffer buffer)
	{
		super(fileName, true);
		CompletionInfo completionInfo = CompletionInfo.getCompletionInfoForBuffer(buffer);
		setCompletionInfo("", completionInfo);
	}

    /*
	public IAsset getAssetAtOffset(int pos)
	{
		IAsset asset = super.getAssetAtOffset(pos);
		System.out.println(asset.getName() + ": " + asset.getStart());
		return asset;
	}
    */
    
    //{{{ getXPathForPosition() method
    @Override
	public String getXPathForPosition(int pos)
	{
		TreePath path = getTreePathForPosition(pos);
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode)path.getLastPathComponent();
		TreeNode[]steps = tn.getPath();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)steps[0];
		String xpath = "";
		if(steps.length == 1)
		{
			//there is only the node with the file name
			xpath = null;
		}
		else
		{
			parent = (DefaultMutableTreeNode)steps[1];
			

			SideKickElement curTag = ((SideKickAsset)parent.getUserObject()).getElement();
			String name;
			
			if(curTag instanceof HtmlDocument.TagBlock)
			{
				name = ((HtmlDocument.TagBlock)curTag).startTag.tagName;
			}
			else
			{
				name = ((HtmlDocument.Tag)curTag).tagName;
			}
			
			xpath = "/" + name;
			
			for(int i=2;i<steps.length;i++)
			{
				DefaultMutableTreeNode cur=(DefaultMutableTreeNode)steps[i];

				curTag = ((SideKickAsset)cur.getUserObject()).getElement();
				
				if(curTag instanceof HtmlDocument.TagBlock)
				{
					name = ((HtmlDocument.TagBlock)curTag).startTag.tagName;
				}
				else if(curTag instanceof HtmlDocument.Tag)
				{
					name = ((HtmlDocument.Tag)curTag).tagName;
				}
				else
				{
					//won't include this step in the XPath
					continue;
				}
				
				int jCur = parent.getIndex(cur);
				int cntChild = 0;
				for(int j=0;j<=jCur;j++)
				{
					DefaultMutableTreeNode aChild = (DefaultMutableTreeNode)parent.getChildAt(j);
					SideKickElement aTag = ((SideKickAsset)aChild.getUserObject()).getElement();
					String aName;
					
					if(aTag instanceof HtmlDocument.TagBlock)
					{
						aName = ((HtmlDocument.TagBlock)aTag).startTag.tagName;
					}
					else if(aTag instanceof HtmlDocument.Tag)
					{
						aName = ((HtmlDocument.Tag)aTag).tagName;
					}
					else
					{
						aName = null;
					}
					
					if(name.equals(aName))
					{
						cntChild++;
					}
				}
				
				xpath += "/"+name+"["+cntChild+"]";
				
				parent = cur;
			}
		}
		return xpath;
	}
	//}}}

}
