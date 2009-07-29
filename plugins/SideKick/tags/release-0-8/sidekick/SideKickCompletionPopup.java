/*
 * SideKickCompletionPopup.java - Completer popup
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright 2000, 2005 Slava Pestov
 *		   2005 Robert McKinnon
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package sidekick;

//{{{ Imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.CompletionPopup;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;
//}}}

public class SideKickCompletionPopup extends CompletionPopup
{
	//{{{ Instance variables
	private View view;
	private SideKickParser parser;
	private SideKickCompletion complete;
	//}}}

	//{{{ SideKickCompletionPopup constructor
	public SideKickCompletionPopup(View view, SideKickParser parser,
		int caret, SideKickCompletion complete, boolean active)
	{
		super(view, getLocation(view.getTextArea(), caret, complete));

		this.view = view;
		this.parser = parser;
		this.complete = complete;

		reset(new Candidates(), active);
	}

	/// This constructor makes an inactive popup as in SideKick 0.7.5.
	public SideKickCompletionPopup(View view, SideKickParser parser,
		int caret, SideKickCompletion complete)
	{
		this(view, parser, caret, complete, false);
	} //}}}

	//{{{ keyPressed() method
	protected void keyPressed(KeyEvent evt)
	{
		// These code should be reduced to make this popup behave
		// like a builtin popup. But these are here to keep
		// compatibility with the old implementation before
		// ractoring out of CompletionPopup.
		switch(evt.getKeyCode())
		{
		case KeyEvent.VK_ENTER:
			keyTyped('\n');
			evt.consume();
			break;
		case KeyEvent.VK_TAB:
			keyTyped('\t');
			evt.consume();
			break;
		case KeyEvent.VK_SPACE:
			evt.consume();
			break;
		case KeyEvent.VK_BACK_SPACE:
			 if(!parser.canHandleBackspace())
			 {
				 dispose();
			 }
			 break;
		case KeyEvent.VK_DELETE:
			dispose();
			break;
		default:
			break;
		}
	} //}}}

	//{{{ keyTyped() method
	public void keyTyped(KeyEvent evt)
	{
		char ch = evt.getKeyChar();
		if(ch == '\b' && !parser.canHandleBackspace())
		{
			evt.consume();
			return;
		}

		keyTyped(ch);

		evt.consume();
	} //}}}

	//{{{ Private members

	//{{{ getLocation() method
	private static Point getLocation(JEditTextArea textArea, int caret,
		SideKickCompletion complete)
	{
		Point location = textArea.offsetToXY(caret - complete.getTokenLength());
		location.y += textArea.getPainter().getFontMetrics().getHeight();
		SwingUtilities.convertPointToScreen(location,
			textArea.getPainter());
		return location;
	} //}}}

	//{{{ Candidates class
	private class Candidates implements CompletionPopup.Candidates
	{
		private final ListCellRenderer renderer;

		public Candidates()
		{
			renderer = complete.getRenderer();
		}

		public int getSize()
		{
			return complete.size();
		}

		public boolean isValid()
		{
			return complete != null && complete.size() > 0;
		}

		public void complete(int index)
		{
			complete.insert(index);
		}
	
		public Component getCellRenderer(JList list, int index,
			boolean isSelected, boolean cellHasFocus)
		{
			return renderer.getListCellRendererComponent(list,
				complete.get(index), index,
				isSelected, cellHasFocus);
		}

		public String getDescription(int index)
		{
			return complete.getCompletionDescription(index);
		}
	} //}}}

	//{{{ keyTyped() method
	private void keyTyped(char ch)
	{
		// If no completion is selected, do not pass the key to
		// handleKeystroke() method. This avoids interfering
		// between a bit intermittent user typing and automatic
		// completion (which is not selected initially).
		int selected = getSelectedIndex();
		if(selected == -1)
		{
			view.getTextArea().userInput(ch);
			updateCompletion(false);
		}
		else if(complete.handleKeystroke(selected, ch))
		{
			updateCompletion(true);
		}
		else {
			dispose();
		}
	} //}}}

	//{{{ updateCompletion() method
	private void updateCompletion(boolean active)
	{
		SideKickCompletion newComplete = complete;
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();
		int caret = textArea.getCaretPosition();
		if(!newComplete.updateInPlace(editPane, caret))
		{
			newComplete = parser.complete(editPane, caret);
		}
		if(newComplete == null || newComplete.size() == 0)
		{
			dispose();
		}
		else
		{
			complete = newComplete;
			setLocation(getLocation(textArea, caret, complete));
			reset(new Candidates(), active);
		}
	} //}}}

	//}}}
}
