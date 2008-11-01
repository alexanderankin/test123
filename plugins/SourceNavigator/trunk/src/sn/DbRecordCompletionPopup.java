/**
 * 
 */
package sn;

import java.awt.Component;
import java.awt.Point;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.CompletionPopup;
import org.gjt.sp.jedit.textarea.JEditTextArea;

@SuppressWarnings("serial")
class DbRecordCompletionPopup extends CompletionPopup {
	private View view;
	private String text;
	private Vector<Candidate> candidates;
	
	public DbRecordCompletionPopup(View view, String text, Vector<DbRecord> records) {
		super(view, getLocation(view.getTextArea(), text));
		this.view = view;
		this.text = text;
		candidates = new Vector<Candidate>();
		for (DbRecord record: records)
			candidates.add(new Candidate(record));
		reset(new TagCandidates(), true);
	}
	
	// Position the popup below the caret line, aligned with the text being completed
	static private Point getLocation(JEditTextArea textArea, String text) {
		Point location = textArea.offsetToXY(textArea.getCaretPosition() - text.length());
		location.y += textArea.getPainter().getFontMetrics().getHeight();
		SwingUtilities.convertPointToScreen(location, textArea.getPainter());
		return location;
	}

	private class Candidate {
		private DbRecord record;
		public Candidate(DbRecord record) {
			this.record = record;
		}
		public String toString() {
			return record.getName();
		}
		public String getDescription() {
			return record.getSourceLink().toString();
		}
	}
	
	private class TagCandidates implements Candidates {
		private final ListCellRenderer renderer;
		public TagCandidates() {
			renderer = new DefaultListCellRenderer();
		}
		public void complete(int index) {
			String selected = candidates.get(index).toString();
			String insertion = selected.substring(text.length());
			SourceNavigatorPlugin.getEditorInterface().insertAtCaret(view, insertion);
		}
		public Component getCellRenderer(JList list, int index,
				boolean isSelected, boolean cellHasFocus) {
			return renderer.getListCellRendererComponent(list,
				candidates.get(index), index, isSelected, cellHasFocus);
		}
		public String getDescription(int index) {
			return candidates.get(index).getDescription();
		}
		public int getSize() {
			return candidates.size();
		}
		public boolean isValid() {
			return (! candidates.isEmpty());
		}
	}
}