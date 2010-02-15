package codebook.gui;
// imports {{{
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.gui.CompletionPopup;

import java.util.ArrayList;
import java.awt.Point;
import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
// }}} imports
/**
 * @author Damien Radtke
 * class Popup
 * An extension of the completion popup class
 * TODO: Add a completion filter; as the user types something, narrow down the number of completions
 */
public class Popup extends CompletionPopup {
	protected JEditTextArea textArea;
	public Popup(JEditTextArea textArea, ArrayList<String[]> complete) {
		super(textArea.getView(), Popup.getLocation(textArea));
		this.textArea = textArea;
		this.reset(new Candidates(complete), true);
	}
	
	public static Point getLocation(JEditTextArea textArea) {
		Point location = textArea.offsetToXY(textArea.getCaretPosition());
		location.y += textArea.getPainter().getFontMetrics().getHeight();
		SwingUtilities.convertPointToScreen(location,
			textArea.getPainter());	
		return location;
	}
	
	
	private class Candidates implements CompletionPopup.Candidates {
		private String[][] list; // A list of arrays of length 2: {completion, description}
		public Candidates(ArrayList<String[]> _list) {
			list = new String[_list.size()][2];
			for (int i=0; i<list.length; i++) {
				list[i] = _list.get(i);
			}
		}
		public void complete(int index) {
			codebook.CodeBookPlugin.complete(textArea, list[index][0]);
		}
		public Component getCellRenderer(JList jlist, int index, boolean isSelected, boolean cellHasFocus) {
			/*
			JLabel text = new JLabel(list[index][0]+"   ");
			if (isSelected) {
			text.setForeground(Color.BLUE);
			//text.setBackground(jlist.getSelectionBackground());
			} else {
			text.setForeground(Color.BLACK);
			text.setBackground(Color.BLACK);
			}
			return text;
			*/
			return new DefaultListCellRenderer().getListCellRendererComponent(
				jlist, list[index][0]+"   ", index, isSelected, cellHasFocus);
		}
		public String getDescription(int index) {
			return list[index][1];
		}
		public int getSize() {
			return list.length;
		}
		public boolean isValid() { return true; }
	}
}
