/*
 * SideKickActions.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

package sidekick;

import javax.swing.tree.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;

public class SideKickActions
{
	//{{{ selectAsset() method
	public static void selectAsset(EditPane editPane, int caret)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(editPane);
		if(data == null)
		{
			editPane.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();

		TreePath path = data.getTreePathForPosition(caret);
		if(path == null)
		{
			editPane.getToolkit().beep();
			return;
		}
		Asset asset = (Asset)((DefaultMutableTreeNode)path
			.getLastPathComponent()).getUserObject();
		if(asset.end != null)
		{
			textArea.setCaretPosition(asset.end.getOffset());
			textArea.addToSelection(
				new Selection.Range(
					asset.start.getOffset(),
					asset.end.getOffset()));
		}
		else
			textArea.setCaretPosition(asset.start.getOffset());
	} //}}}

	//{{{ goToPrevAsset() method
	public static void goToPrevAsset(EditPane editPane)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(editPane);
		if(data == null)
		{
			editPane.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();

		TreePath path = data.getTreePathForPosition(textArea.getCaretPosition());
		if(path == null)
		{
			editPane.getToolkit().beep();
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			path.getLastPathComponent();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
			node.getParent();

		Asset destination = null;

		for(int i = 0; i < parent.getChildCount(); i++)
		{
			if(parent.getChildAt(i) == node)
			{
				if(i == 0)
					destination = (Asset)parent.getUserObject();
				else
				{
					destination = (Asset)((DefaultMutableTreeNode)
						parent.getChildAt(i - 1))
						.getUserObject();
				}
				break;
			}
		}

		if(destination == null)
			editPane.getToolkit().beep();
		else
		{
			textArea.setCaretPosition(destination.start.getOffset());
		}
	} //}}}

	//{{{ goToNextAsset() method
	public static void goToNextAsset(EditPane editPane)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(editPane);
		if(data == null)
		{
			editPane.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();

		TreePath path = data.getTreePathForPosition(textArea.getCaretPosition());
		if(path == null)
		{
			editPane.getToolkit().beep();
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			path.getLastPathComponent();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
			node.getParent();

		Asset destination = null;

		for(int i = 0; i < parent.getChildCount(); i++)
		{
			if(parent.getChildAt(i) == node)
			{
				if(i == parent.getChildCount() - 1)
					destination = (Asset)parent.getUserObject();
				else
				{
					destination = (Asset)((DefaultMutableTreeNode)
						parent.getChildAt(i + 1))
						.getUserObject();
				}
				break;
			}
		}

		if(destination == null)
			editPane.getToolkit().beep();
		else
		{
			if(destination.end != null)
				textArea.setCaretPosition(destination.end.getOffset());
			else
				textArea.setCaretPosition(destination.start.getOffset());
		}
	} //}}}
}
