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

//{{{ Import statements
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
//}}}

public class SideKickActions
{
	//{{{ keyComplete() method
	public static void keyComplete(View view)
	{
		if(timer != null)
			timer.stop();

		complete(view,true);
	} //}}}

	//{{{ keyCompleteWithDelay() method
	public static void keyCompleteWithDelay(final View view)
	{
		if(!completion)
			return;

		if(timer != null)
			timer.stop();

		final JEditTextArea textArea = view.getTextArea();

		caretWhenCompleteKeyPressed = textArea.getCaretPosition();

		if(timer == null)
		{
			timer = new Timer(0,new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					if(caretWhenCompleteKeyPressed == textArea.getCaretPosition())
						complete(view,false);
				}
			});

			timer.setInitialDelay(delay);
			timer.setRepeats(false);
		}

		timer.start();
	} //}}}

	//{{{ complete() method
	public static void complete(View view, boolean insertLongestPrefix)
	{
		EditPane editPane = view.getEditPane();
		Buffer buffer = editPane.getBuffer();
		SideKickParser parser = SideKickPlugin
			.getParserForBuffer(buffer);

		SideKickParsedData data = SideKickParsedData
			.getParsedData(view);

		if(!buffer.isEditable()
			|| data == null || parser == null
			|| !parser.supportsCompletion())
		{
			System.err.println("data is " + data);
			return;
		}

		JEditTextArea textArea = editPane.getTextArea();
		int caret = textArea.getCaretPosition();

		SideKickCompletion complete = parser.complete(editPane,caret);
		if(insertLongestPrefix)
		{
			if(complete == null || complete.size() == 0)
			{
				// nothing to do
				return;
			}
			else if(complete.size() == 1)
			{
				complete.insert(0);
				return;
			}
			else
			{
				String longestPrefix = complete.getLongestPrefix();
				if(longestPrefix != null
					&& longestPrefix.length() != 0)
				{
					textArea.setSelectedText(longestPrefix);
				}
			}
		}

		new SideKickCompletionPopup(view,parser,caret,complete);
	} //}}}

	//{{{ selectAsset() method
	public static void selectAsset(View view)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		if(data == null)
		{
			view.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = view.getTextArea();

		TreePath path = data.getTreePathForPosition(textArea.getCaretPosition());
		if(path == null)
		{
			view.getToolkit().beep();
			return;
		}
		Asset asset = (Asset)((DefaultMutableTreeNode)path
			.getLastPathComponent()).getUserObject();
		textArea.setCaretPosition(asset.end.getOffset());
		textArea.addToSelection(
			new Selection.Range(
				asset.start.getOffset(),
				asset.end.getOffset()));
	} //}}}

	//{{{ narrowToAsset() method
	public static void narrowToAsset(View view)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		if(data == null)
		{
			view.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = view.getTextArea();

		TreePath path = data.getTreePathForPosition(textArea.getCaretPosition());
		if(path == null)
		{
			view.getToolkit().beep();
			return;
		}
		Asset asset = (Asset)((DefaultMutableTreeNode)path
			.getLastPathComponent()).getUserObject();
		textArea.getDisplayManager().narrow(
			textArea.getLineOfOffset(asset.start.getOffset()),
			textArea.getLineOfOffset(asset.end.getOffset()));
	} //}}}

	//{{{ goToPrevAsset() method
	public static void goToPrevAsset(View view)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		if(data == null)
		{
			view.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = view.getTextArea();

		int caret = textArea.getCaretPosition();
		TreePath path = data.getTreePathForPosition(caret);
		if(path == null)
		{
			view.getToolkit().beep();
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			path.getLastPathComponent();

		// see if caret is at the end of a child of the current asset
		for(int i = 0; i < node.getChildCount(); i++)
		{
			Asset asset = (Asset)((DefaultMutableTreeNode)node.getChildAt(i))
				.getUserObject();
			if(caret == asset.end.getOffset())
			{
				textArea.setCaretPosition(asset.start.getOffset());
				return;
			}
		}

		Asset asset = ((Asset)node.getUserObject());
		if(caret == asset.start.getOffset())
		{
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
				node.getParent();
			for(int i = 0; i < parent.getChildCount(); i++)
			{
				if(node == parent.getChildAt(i))
				{
					if(i == 0)
					{
						if(parent.getUserObject() instanceof Asset)
						{
							textArea.setCaretPosition(
								((Asset)parent.getUserObject())
								.start.getOffset());
						}
					}
					else
					{
						Asset prevAsset = (Asset)((DefaultMutableTreeNode)
							parent.getChildAt(i - 1)).getUserObject();
						textArea.setCaretPosition(prevAsset.end.getOffset());
					}
					return;
				}
			}
		}
		else
			textArea.setCaretPosition(asset.start.getOffset());
	} //}}}

	//{{{ goToNextAsset() method
	public static void goToNextAsset(View view)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		if(data == null)
		{
			view.getToolkit().beep();
			return;
		}

		JEditTextArea textArea = view.getTextArea();

		int caret = textArea.getCaretPosition();
		TreePath path = data.getTreePathForPosition(caret);
		if(path == null)
		{
			view.getToolkit().beep();
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			path.getLastPathComponent();

		// see if caret is at the end of a child of the current asset
		for(int i = 0; i < node.getChildCount(); i++)
		{
			Asset asset = (Asset)((DefaultMutableTreeNode)node.getChildAt(i))
				.getUserObject();
			if(caret == asset.end.getOffset())
			{
				if(i != node.getChildCount() - 1)
				{
					Asset nextAsset = (Asset)((DefaultMutableTreeNode)
						node.getChildAt(i + 1)).getUserObject();
					textArea.setCaretPosition(nextAsset.start.getOffset());
					return;
				}
				else
					break;
			}
		}

		textArea.setCaretPosition(((Asset)node.getUserObject()).end.getOffset());
	} //}}}

	//{{{ propertiesChanged() method
	public static void propertiesChanged()
	{
		completion = jEdit.getBooleanProperty("sidekick.complete");
		delay = jEdit.getIntegerProperty("sidekick.complete-delay",500);
		if(timer != null)
			timer.setInitialDelay(delay);
	} //}}}

	//{{{ Private members
	private static boolean completion;
	private static int delay;
	private static int caretWhenCompleteKeyPressed;
	private static Timer timer;
	//}}}

	//}}}
}
