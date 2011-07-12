/*
 * SideKickActions.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2005 Slava Pestov
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
import java.lang.ref.WeakReference;
import javax.swing.tree.*;
import javax.swing.Timer;
import java.awt.event.*;

import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;

//}}}

// {{{ SideKickActions class
public class SideKickActions
{
	//{{{ Private members
	private static boolean completeDelay;
	private static boolean completeInstant;
	private static boolean autoCompletePopupGetFocus;
	private static int delay;
	private static WeakReference<JEditTextArea> delayedCompletionTarget;
	private static int caretWhenCompleteKeyPressed;
	private static Timer timer;
	private static SideKickCompletionPopup popup;
	//}}}
	//{{{ keyComplete() method
	public static void keyComplete(View view)
	{
		if(timer != null)
			timer.stop();

		if(!completeInstant)
			return;

		complete(view,COMPLETE_INSTANT_KEY);
	} //}}}

	//{{{ keyCompleteWithDelay() method
	public static void keyCompleteWithDelay(View view)
	{
		if(!completeDelay)
			return;

		if(timer != null)
			timer.stop();

		JEditTextArea textArea = view.getTextArea();
		if (delayedCompletionTarget == null || delayedCompletionTarget.get() != textArea)
		{
			delayedCompletionTarget = new WeakReference<JEditTextArea>(textArea);
		}
		caretWhenCompleteKeyPressed = textArea.getCaretPosition();

		if(timer == null)
		{
			timer = new Timer(0,new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					JEditTextArea textArea = delayedCompletionTarget.get();
					if(textArea != null
						&& caretWhenCompleteKeyPressed == textArea.getCaretPosition())
					{
						complete(textArea.getView(),COMPLETE_DELAY_KEY);
					}
				}
			});

			timer.setInitialDelay(delay);
			timer.setRepeats(false);
		}

		timer.start();
	} //}}}

	//{{{ complete() method
	public static final int COMPLETE_COMMAND = 0;
	public static final int COMPLETE_DELAY_KEY = 1;
	public static final int COMPLETE_INSTANT_KEY = 2;
	public static String acceptChars;
	public static String insertChars;

	public static void complete(View view, int mode)
	{
		EditPane editPane = view.getEditPane();
		Buffer buffer = editPane.getBuffer();
		JEditTextArea textArea = editPane.getTextArea();

		SideKickParser parser = SideKickPlugin
			.getParserForBuffer(buffer);
		SideKickParsedData data = SideKickParsedData
			.getParsedData(view);

		SideKickCompletion complete = null;

		if(buffer.isEditable()
			&& data != null && parser != null
			&& parser.supportsCompletion())
		{
			complete = parser.complete(editPane,
				textArea.getCaretPosition());
		}

		if(complete == null || complete.size() == 0)
		{
			if(mode == COMPLETE_INSTANT_KEY
				|| mode == COMPLETE_DELAY_KEY)
			{
				// don't bother user with beep if eg
				// they press < in XML mode
				return;
			}
			else
			{
				view.getToolkit().beep();
				return;
			}
		}
		else if(complete.size() == 1)
		{
			// if user invokes complete explicitly, insert the
			// completion immediately.
			//
			// if the user eg enters </ in XML mode, there will
			// only be one completion and / is an instant complete
			// key, so we insert it
			if(mode == COMPLETE_COMMAND
				|| mode == COMPLETE_INSTANT_KEY)
			{
				complete.insert(0);
				return;
			}
		}

		// show the popup if
		// - complete has one element and user invoked with delay key
		// - or complete has multiple elements
		// and popup is not already shown because of explicit invocation
		// of the complete action during the trigger delay
		if(popup != null)
			return;

		boolean active = (mode == COMPLETE_COMMAND)
			|| autoCompletePopupGetFocus;
		popup = parser.getCompletionPopup(view,
			textArea.getCaretPosition(), complete, active);
		popup.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				popup = null;
			}
		});
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

		IAsset asset = data.getAssetAtOffset(
			textArea.getCaretPosition());

		if(asset == null || asset.getEnd() == null)
		{
			view.getToolkit().beep();
			return;
		}

		int pos = asset.getEnd().getOffset();
		if (pos > textArea.getBuffer().getLength())
		{
		    view.getToolkit().beep();
		    return;
		}
			
		textArea.setCaretPosition(pos);
		textArea.addToSelection(
			new Selection.Range(
				asset.getStart().getOffset(),
				pos));
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
		IAsset asset = (IAsset)((DefaultMutableTreeNode)path
			.getLastPathComponent()).getUserObject();

		if(asset == null || asset.getEnd() == null)
		{
			view.getToolkit().beep();
			return;
		}

		textArea.getDisplayManager().narrow(
			textArea.getLineOfOffset(asset.getStart().getOffset()),
			textArea.getLineOfOffset(asset.getStart().getOffset()));
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
			Object userObject = ((DefaultMutableTreeNode)node.getChildAt(i)).getUserObject();
			if (userObject instanceof IAsset)
			{
				IAsset asset = (IAsset)userObject;
				if(asset.getEnd() != null && caret == asset.getEnd().getOffset())
				{
					textArea.setCaretPosition(asset.getStart().getOffset());
					return;
				}
			}
		}

		IAsset asset = ((IAsset)node.getUserObject());
		if(caret == asset.getStart().getOffset())
		{
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
				node.getParent();
			if (parent != null) 
			{				
				for(int i = 0; i < parent.getChildCount(); i++)
				{
					if(node == parent.getChildAt(i))
					{
						if(i == 0)
						{
							if(parent.getUserObject() instanceof IAsset)
							{
								textArea.setCaretPosition(
									((IAsset)parent.getUserObject())
									.getStart().getOffset());
							}
						}
						else
						{
							Object child = ((DefaultMutableTreeNode)parent.getChildAt(i - 1)).getUserObject();
							if (child instanceof IAsset) {
								IAsset prevAsset = (IAsset)child;
								if(prevAsset.getEnd() != null)
									textArea.setCaretPosition(prevAsset.getEnd().getOffset());
							}
						}
						return;
					}
				}
			}
		}
		else
			textArea.setCaretPosition(asset.getStart().getOffset());
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
			Object userObject = ((DefaultMutableTreeNode)node.getChildAt(i)).getUserObject();
			if (userObject instanceof IAsset)
			{
				IAsset asset = (IAsset)userObject;
				if(caret == asset.getEnd().getOffset())
				{
					if(i != node.getChildCount() - 1)
					{
						IAsset nextAsset = (IAsset)((DefaultMutableTreeNode)
							node.getChildAt(i + 1)).getUserObject();
						int offset = nextAsset.getStart().getOffset() >= textArea.getBufferLength() ? 
							textArea.getBufferLength() - 1 :
							nextAsset.getStart().getOffset();
						textArea.setCaretPosition(offset);
						return;
					}
					else
						break;
				}
			}
		}

		int offset = ((IAsset)node.getUserObject()).getEnd().getOffset();
		offset = offset >= textArea.getBufferLength() ? textArea.getBufferLength() - 1 : offset;
		textArea.setCaretPosition(offset);
	} //}}}

	//{{{ propertiesChanged() method
	public static void propertiesChanged()
	{
		completeDelay = jEdit.getBooleanProperty("sidekick.complete-delay.toggle");
		completeInstant = jEdit.getBooleanProperty("sidekick.complete-instant.toggle");
		autoCompletePopupGetFocus = jEdit.getBooleanProperty("sidekick.auto-complete-popup-get-focus");
		acceptChars = MiscUtilities.escapesToChars(jEdit.getProperty("sidekick.complete-popup.accept-characters"));
		insertChars = MiscUtilities.escapesToChars(jEdit.getProperty("sidekick.complete-popup.insert-characters"));
		delay = jEdit.getIntegerProperty("sidekick.complete-delay",500);
		if(timer != null)
			timer.setInitialDelay(delay);
	} //}}}

	// {{{ SideKickAction class
	abstract public static class SideKickAction extends EditAction 
	{
		protected String parserName;
		protected SideKickAction(String actionName, String parserName) 
		{
			super(actionName, new Object[] {parserName} );
			this.parserName = parserName;
		}
		
	}// }}}

	// {{{ ToggleParser class
	/** An action which will always activate the SideKick parser,
	 *  alternately selecting the default parser, and then the
	 *  selected one, allowing you to toggle between say, Outline
	 *  and Java parsers, XML and HTML, or Python and Jython parsers. 
	 */
	public static class ToggleParser extends SideKickAction 
	{
		public String getLabel() 
		{
			return parserName + " (Toggle)";
		}
		public ToggleParser(String parserName) 
		{
			super("sidekick.parser." + parserName + "-toggle", parserName);
			this.parserName =parserName;
		}
		public String getCode() {
			return "new sidekick.SideKickActions.ToggleAction(\"" + parserName + "\").invoke(view)";
		}
		public void invoke(View view)
		{
			
			
		}
	} //}}}

} // }}}
