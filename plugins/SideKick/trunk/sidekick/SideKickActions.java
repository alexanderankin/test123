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
	//{{{ completeKeyTyped() method
	public static void completeKeyTyped(final View view, char ch)
	{
		final JEditTextArea textArea = view.getTextArea();

		textArea.userInput(ch);

		if(!completion)
			return;

		Buffer buffer = textArea.getBuffer();

		SideKickParser parser = SideKickPlugin.getParserForBuffer(buffer);
		if(parser == null || !parser.supportsCompletion())
			return;

		// XXX
		if(/* XmlPlugin.isDelegated(textArea) || */ !buffer.isEditable())
			return;

		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		if(data == null)
			return;

		if(timer != null)
			timer.stop();

		String delayCompletionTriggers = parser.getDelayCompletionTriggers();
		if(delayCompletionTriggers != null && delayCompletionTriggers.indexOf(ch) != -1)
		{
			caretWhenCompleteKeyPressed = textArea.getCaretPosition();

			if(timer == null)
			{
				timer = new Timer(0,new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						if(caretWhenCompleteKeyPressed == textArea.getCaretPosition())
							complete(view);
					}
				});

				timer.setInitialDelay(delay);
				timer.setRepeats(false);
			}

			timer.start();

			return;
		}

		String instantCompletionTriggers = parser.getInstantCompletionTriggers();
		if(instantCompletionTriggers != null && instantCompletionTriggers.indexOf(ch) != -1)
				complete(view);
	} //}}}

	//{{{ complete() method
	public static void complete(View view)
	{
		Buffer buffer = view.getBuffer();
		JEditTextArea textArea = view.getTextArea();
		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		SideKickParser parser = SideKickPlugin.getParserForBuffer(buffer);

		// XXX
		if(/* XmlPlugin.isDelegated(textArea) || */ !buffer.isEditable()
			|| data == null || parser == null
			|| !parser.supportsCompletion())
		{
			view.getToolkit().beep();
			return;
		}

		int caret = textArea.getCaretPosition();

		new SideKickCompletionPopup(view,parser,caret);
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
		textArea.getFoldVisibilityManager().narrow(
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

	//{{{ Inner classes
	static class CompleteAction extends EditAction
	{
		private char ch;

		CompleteAction(char ch)
		{
			super("-xml-complete-key-" + ch);
			this.ch = ch;
		}

		public boolean noRecord()
		{
			return true;
		}

		public void invoke(View view)
		{
			Macros.Recorder recorder = view.getMacroRecorder();
			if(recorder != null)
				recorder.record(1,ch);
			completeKeyTyped(view,ch);
		}

		public String getCode()
		{
			return null;
		}
	} //}}}

	//}}}
}
