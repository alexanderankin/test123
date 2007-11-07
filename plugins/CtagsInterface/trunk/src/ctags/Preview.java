package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.textarea.TextArea;

@SuppressWarnings("serial")
public class Preview extends JPanel implements DefaultFocusComponent {

	View view;
	JList tags;
	DefaultListModel tagModel;
	TextArea text;
	boolean first = true;
	
	Preview(View view) {
		super(new BorderLayout());
		this.view = view;
		tagModel = new DefaultListModel();
		tags = new JList(tagModel);
		JScrollPane listPane = new JScrollPane(tags);
		tags.setCellRenderer(new TagListCellRenderer());
		text = new TextArea();
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPane, text);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(150);
		add(split, BorderLayout.CENTER);
		view.getTextArea().addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				String name = CtagsInterfacePlugin.getDestinationTag(Preview.this.view);
				Vector<Tag> tags = CtagsInterfacePlugin.queryTag(name);
				tagModel.clear();
				for (int i = 0; i < tags.size(); i++)
					tagModel.addElement(tags.get(i));
				if (tags.isEmpty())
					return;
				final Tag t = tags.get(0);
				String file = t.getFile();
				int line = t.getLine();
				if (line > -1) {
					text.setText(getContents(file));
					text.scrollTo(line, 0, true);
				}
			}
		});
	}
	public void focusOnDefaultComponent() {
		tags.requestFocus();
	}
	static public String getContents(String path) {
		StringBuffer contents = new StringBuffer();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (input!= null)
					input.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return contents.toString();
	}
	
	private final class TagListCellRenderer extends DefaultListCellRenderer {
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
			Tag tag = (Tag) tagModel.getElementAt(index);
			String name = tag.getName();
			String signature = tag.getExtension("signature");
			if (signature != null && signature.length() > 0)
				l.setText(name + signature);
			else
				l.setText(name);
			ImageIcon icon = tag.getIcon();
			if (icon != null)
				l.setIcon(icon);
			return l;
		}
	}

}
