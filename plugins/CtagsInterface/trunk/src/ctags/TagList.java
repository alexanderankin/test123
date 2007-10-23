package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;

import db.TagDB;

@SuppressWarnings("serial")
public class TagList extends JPanel implements DefaultFocusComponent {

	View view;
	JList tags;
	DefaultListModel tagModel;
	
	TagList(View view) {
		super(new BorderLayout());
		this.view = view;
		tagModel = new DefaultListModel();
		tags = new JList(tagModel);
		add(new JScrollPane(tags), BorderLayout.CENTER);
		tags.setCellRenderer(new TagListCellRenderer());
		tags.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				jumpTo(tags.getSelectedIndex());
			}
		});
		tags.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				ke.consume();
				char c = ke.getKeyChar();
				if (c == ' ')
					jumpTo(tags.getSelectedIndex());
				else if (c >= '1' && c <= '9')
					jumpTo(c - '1');
			}
		});
		setTags(null);
	}
	
	@SuppressWarnings("unchecked")
	protected void jumpTo(int selectedIndex) {
		Hashtable<String, String> tag = (Hashtable<String, String>)
			tagModel.getElementAt(selectedIndex);
		String file = tag.get(TagDB.FILES_NAME);
		String lineStr = tag.get(TagDB.TAGS_LINE);
		if (lineStr != null) {
			int line = Integer.valueOf(lineStr);
			CtagsInterfacePlugin.jumpTo(view, file, line);
		}
	}

	public void setTags(Vector<Hashtable<String, String>> tags) {
		tagModel.removeAllElements();
		if (tags == null)
			return;
		for (int i = 0; i < tags.size(); i++)
			tagModel.addElement(tags.get(i));
	}
	
	public void focusOnDefaultComponent() {
		tags.requestFocus();
	}

	private final class TagListCellRenderer extends DefaultListCellRenderer {
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
			Hashtable<String, String> tag = (Hashtable<String, String>)
				tagModel.getElementAt(index);
			l.setText(getHtmlText(tag, index));
			l.setFont(new Font("Monospaced", Font.PLAIN, 12));
			return l;
		}

		private String getHtmlText(Hashtable<String, String> tag, int index) {
			StringBuffer s = new StringBuffer("<html>");
			s.append(index + 1);
			s.append(": <b>");
			s.append(tag.get(TagDB.TAGS_NAME));
			s.append("</b>  ");
			String project = tag.get(TagDB.PROJECT_COL);
			if (project != null && project.length() > 0) {
				s.append("(<i>");
				s.append(project);
				s.append("</i>)  ");
			}
			s.append(tag.get(TagDB.FILES_NAME));
			s.append(tag.containsKey(TagDB.TAGS_LINE) ? ":" +
				tag.get(TagDB.TAGS_LINE) : "");
			s.append("<br>Pattern: ");
			s.append(tag.get(TagDB.TAGS_PATTERN));
			s.append("<br>");
			TreeSet<String> keys = new TreeSet<String>(tag.keySet());
			keys.remove(TagDB.TAGS_NAME);
			keys.remove(TagDB.TAGS_FILE_ID);
			keys.remove(TagDB.PROJECT_COL);
			keys.remove(TagDB.TAGS_LINE);
			keys.remove(TagDB.TAGS_PATTERN);
			Iterator<String> it = keys.iterator();
			boolean first = true;
			while (it.hasNext()) {
				if (! first)
					s.append("  ");
				first = false;
				String key = (String) it.next();
				s.append(TagDB.col2attr(key));
				s.append(": ");
				s.append(tag.get(key));
			}
			return s.toString();
		}
	}

}
