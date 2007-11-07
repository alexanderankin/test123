package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.textarea.TextArea;

@SuppressWarnings("serial")
public class Preview extends JPanel implements DefaultFocusComponent,
	CaretListener, ListSelectionListener {

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
		tags.setCellRenderer(new TagListCellRenderer());
		tags.setVisibleRowCount(4);
		tags.addListSelectionListener(this);
		text = new TextArea();
		text.setMinimumSize(new Dimension(150, 50));
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			new JScrollPane(tags), text);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(100);
		add(split, BorderLayout.CENTER);
		view.getTextArea().addCaretListener(this);
	}

	public void caretUpdate(CaretEvent e) {
		String name = CtagsInterfacePlugin.getDestinationTag(Preview.this.view);
		if (name == null)
			return;
		Vector<Tag> tags = CtagsInterfacePlugin.queryTag(name);
		tagModel.clear();
		for (int i = 0; i < tags.size(); i++)
			tagModel.addElement(tags.get(i));
		if (! tags.isEmpty())
			this.tags.setSelectedIndex(0);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		int index = tags.getSelectedIndex();
		if (index < 0)
			return;
		Tag t = (Tag) tagModel.getElementAt(index);
		String file = t.getFile();
		int line = t.getLine();
		if (line > -1) {
			text.getBuffer().setReadOnly(false);
			text.setText(getContents(file));
			text.scrollTo(line, 0, true);
			text.setCaretPosition(text.getLineStartOffset(line - 1));
			text.getBuffer().setReadOnly(true);
		}
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
		//private Font tagListFont = new Font("Monospaced", Font.PLAIN, 12);
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
			Tag tag = (Tag) tagModel.getElementAt(index);
			//l.setFont(tagListFont );
			l.setText(getText(tag));
			ImageIcon icon = tag.getIcon();
			if (icon != null)
				l.setIcon(icon);
			return l;
		}
		String getText(Tag tag) {
			StringBuffer s = new StringBuffer();
			s.append(tag.getName());
			String signature = tag.getExtension("signature");
			if (signature != null && signature.length() > 0)
				s.append(signature);
			s.append("   ");
			s.append(tag.getFile());
			int line = tag.getLine();
			if (line > -1)
				s.append(":" + line);
			return s.toString();
		}
	}

}
