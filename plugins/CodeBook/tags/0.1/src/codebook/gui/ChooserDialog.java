package codebook.gui;
// imports {{{
import org.gjt.sp.jedit.textarea.JEditTextArea;
import common.gui.ListPanel;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JDialog;
// }}} imports
/**
 * @author Damien Radtke
 * class ChooseDialog
 * A class to help narrow down ambiguous classes and names
 * Provides a dialog window with a list and remembers the selection
 */
public class ChooserDialog extends JDialog {
	private String[] options;
	private ListPanel list;
	private String chosen;
	public ChooserDialog(JEditTextArea textArea, String[] options) {
		super(textArea.getView(), "Narrow down your selection", true);
		this.options = options;
		list = new ListPanel("Options", options);
		
		list.setReorderable(false);
		list.setSelectedIndex(0);
		add(list);
		Listener l = new Listener();
		list.addKeyListener(l);
		addKeyListener(l);
		setSize(300, 100);
		Point p = Popup.getLocation(textArea);
		setLocation(p.x, p.y);
		setVisible(true);
	}
	
	public String getChosen() {
		return chosen;
	}
	
	private class Listener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			if (code == KeyEvent.VK_ENTER) {
				chosen = options[list.getLastSelectedIndex()];
				dispose();
			} else if (code == KeyEvent.VK_ESCAPE) {
				chosen = null;
				dispose();
			}
		}
	}
}
