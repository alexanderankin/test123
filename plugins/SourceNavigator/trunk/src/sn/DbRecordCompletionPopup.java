/**
 * 
 */
package sn;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.CompletionPopup;
import org.gjt.sp.jedit.textarea.JEditTextArea;

@SuppressWarnings("serial")
class DbRecordCompletionPopup extends CompletionPopup {
	private View view;
	private String text;
	private Vector<DbRecord> records;
	private TagCandidates candidates;
	private String currentText;
	
	public DbRecordCompletionPopup(View view, String text, Vector<DbRecord> records)
	{
		super(view, getLocation(view.getTextArea(), text));
		this.view = view;
		this.text = text;
		this.records = records;
		candidates = new TagCandidates(text, records);
		candidates.updateText(text);
	}
	
	// Position the popup below the caret line, aligned with the text being completed
	static private Point getLocation(JEditTextArea textArea, String text) {
		Point location = textArea.offsetToXY(textArea.getCaretPosition() - text.length());
		location.y += textArea.getPainter().getFontMetrics().getHeight();
		SwingUtilities.convertPointToScreen(location, textArea.getPainter());
		return location;
	}

	private class TagCandidates implements Candidates {
		private final ListCellRenderer renderer;
		private Vector<String> completions;
		private String text;
		private boolean firstTime;
		
		public TagCandidates(String text, Vector<DbRecord> records) {
			renderer = new DefaultListCellRenderer();
			completions = new Vector<String>();
			firstTime = true;
		}
		private void updateText(String newText) {
			currentText = newText;
			candidates.reset(currentText, records);
			if (candidates.getSize() == 1) {
				candidates.complete(0);
				dispose();
			} else
				DbRecordCompletionPopup.this.reset(candidates, true);
		}
		public void reset(String newText, Vector<DbRecord> records) {
			text = newText;
			completions.clear();
			for (DbRecord record: records) {
				String name = record.getName();
				if (name.startsWith(text) && ! completions.contains(name))
					completions.add(name);
			}
			// First time only, automatically apply the longest common
			// prefix of all available completions.
			if (firstTime) {
				firstTime = false;
				String common = MiscUtilities.getLongestPrefix(completions, false);
				if (! common.equals(text)) {
					String insertion = common.substring(text.length());
					SourceNavigatorPlugin.getEditorInterface().insertAtCaret(view, insertion);
					updateText(common);
					return;
				}
			}
			Collections.sort(completions);
		}
		public void complete(int index) {
			String selected = completions.get(index);
			String insertion = selected.substring(text.length());
			SourceNavigatorPlugin.getEditorInterface().insertAtCaret(view, insertion);
		}
		public Component getCellRenderer(JList list, int index,
				boolean isSelected, boolean cellHasFocus) {
			return renderer.getListCellRendererComponent(list,
				completions.get(index), index, isSelected, cellHasFocus);
		}
		public String getDescription(int index) {
			return "";
		}
		public int getSize() {
			return completions.size();
		}
		public boolean isValid() {
			return (! completions.isEmpty());
		}
	}
	
	protected void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			view.getTextArea().backspace();
			e.consume();
			if (currentText.length() > text.length())
				candidates.updateText(currentText.substring(0, currentText.length() - 1));
			else
				dispose();
		}
	}

	protected void keyTyped(KeyEvent e)
	{
		char ch = e.getKeyChar();
		if (ch != '\b' && ch != '\t')
		{
			view.getTextArea().userInput(ch);
			e.consume();
			candidates.updateText(currentText + ch);
		}
	}
}