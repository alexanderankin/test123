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
		EditPane editPane = view.getEditPane();
		final JEditTextArea textArea = view.getTextArea();

		textArea.userInput(ch);

		if(!completion)
			return;

		Buffer buffer = textArea.getBuffer();

		SideKickParser parser = SideKickPlugin.getParserForBuffer(buffer);
		if(parser == null)
			return;

		String completionTriggers = parser.getDelayCompletionTriggers();
		if(completionTriggers.indexOf(ch) == -1)
			return;

		SideKickParsedData data = SideKickParsedData.getParsedData(editPane);
		if(data == null)
			return;

		// XXX
		if(/* XmlPlugin.isDelegated(textArea) || */ !buffer.isEditable())
			return;

		if(timer != null)
			timer.stop();

		final int caret = textArea.getCaretPosition();

		timer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if(caret == textArea.getCaretPosition())
					complete(view);
			}
		});

		timer.setInitialDelay(delay);
		timer.setRepeats(false);
		timer.start();
	} //}}}

	//{{{ complete() method
	public static void complete(View view)
	{
		EditPane editPane = view.getEditPane();
		Buffer buffer = editPane.getBuffer();
		JEditTextArea textArea = editPane.getTextArea();
		SideKickParsedData data = SideKickParsedData.getParsedData(editPane);
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

	//{{{ propertiesChanged() method
	public static void propertiesChanged()
	{
		completion = jEdit.getBooleanProperty("sidekick.complete");
		delay = jEdit.getIntegerProperty("sidekick.complete-delay",500);
	} //}}}

	//{{{ Private members
	private static boolean completion;
	private static int delay;
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
