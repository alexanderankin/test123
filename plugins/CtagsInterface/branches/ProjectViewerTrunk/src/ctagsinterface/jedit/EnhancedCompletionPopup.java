package ctagsinterface.jedit;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.CompletionPopup;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

@SuppressWarnings("serial")
public class EnhancedCompletionPopup extends CompletionPopup
{
	private View view;
	private EnhancedCompletion complete;

	public EnhancedCompletionPopup(View view, int caret,
		EnhancedCompletion complete, boolean active)
	{
		super(view, getLocation(view.getTextArea(), caret, complete));

		this.view = view;
		this.complete = complete;

		reset(new Candidates(), active);
	}

	/// Inactive popup
	public EnhancedCompletionPopup(View view, int caret,
		EnhancedCompletion complete)
	{
		this(view, caret, complete, false);
	}

	protected void keyPressed(KeyEvent evt)
	{
		// These code should be reduced to make this popup behave
		// like a built-in popup. But these are here to keep
		// compatibility with the old implementation before
		// rafctoring out of CompletionPopup.
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
			 if (!canHandleBackspace())
				 dispose();
			 break;
		case KeyEvent.VK_DELETE:
			dispose();
			break;
		default:
			break;
		}
	}

	protected boolean canHandleBackspace()
	{
		return false;
	}
	
	public void keyTyped(KeyEvent evt)
	{
		char ch = evt.getKeyChar();
		if ((ch == '\b') && (! canHandleBackspace()))
		{
			evt.consume();
			return;
		}

		keyTyped(ch);

		evt.consume();
	}

	private static Point getLocation(JEditTextArea textArea,
		int caret, EnhancedCompletion complete)
	{
		Point location = textArea.offsetToXY(caret -
			complete.getTokenLength());
		TextAreaPainter painter = textArea.getPainter();
		location.y += painter.getFontMetrics().getHeight();
		SwingUtilities.convertPointToScreen(location, painter);
		return location;
	}

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
	}

	private void keyTyped(char ch)
	{
		// If no completion is selected, do not pass the key to
		// handleKeystroke() method. This avoids interfering
		// between a bit intermittent user typing and automatic
		// completion (which is not selected initially).
		int selected = getSelectedIndex();
		if (selected == -1)
		{
			view.getTextArea().userInput(ch);
			updateCompletion(false);
		}
		else if (complete.handleKeystroke(selected, ch))
			updateCompletion(true);
		else
			dispose();
	}

	private void updateCompletion(boolean active)
	{
		EnhancedCompletion newComplete = complete;
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();
		int caret = textArea.getCaretPosition();
		if (! newComplete.updateInPlace(editPane, caret))
			newComplete = complete.complete(editPane, caret);
		if ((newComplete == null) || (newComplete.size() == 0))
			dispose();
		else
		{
			complete = newComplete;
			setLocation(getLocation(textArea, caret, complete));
			reset(new Candidates(), active);
		}
	}
}
