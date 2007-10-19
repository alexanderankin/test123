package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

	private static final String LINE_COL = "LINE";
	View view;
	JList tags;
	DefaultListModel tagModel;
	
	TagList(View view) {
		super(new BorderLayout());
		this.view = view;
		tagModel = new DefaultListModel();
		tags = new JList(tagModel);
		add(new JScrollPane(tags), BorderLayout.CENTER);
		tags.setCellRenderer(new DefaultListCellRenderer() {
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
				s.append(tag.get(TagDB.NAME_COL));
				s.append("</b>  ");
				String project = tag.get(TagDB.PROJECT_COL);
				if (project != null && project.length() > 0) {
					s.append("(<i>");
					s.append(project);
					s.append("</i>)  ");
				}
				s.append(tag.get(TagDB.FILE_COL));
				s.append(tag.containsKey(LINE_COL) ? ":" + tag.get(LINE_COL) : "");
				s.append("<br>Pattern: ");
				s.append(tag.get(TagDB.PATTERN_COL));
				s.append("<br>");
				TreeSet<String> keys = new TreeSet<String>(tag.keySet());
				keys.remove(TagDB.NAME_COL);
				keys.remove(TagDB.FILE_COL);
				keys.remove(TagDB.PROJECT_COL);
				keys.remove(LINE_COL);
				keys.remove(TagDB.PATTERN_COL);
				Iterator<String> it = keys.iterator();
				boolean first = true;
				while (it.hasNext()) {
					if (! first)
						s.append("  ");
					first = false;
					String key = (String) it.next();
					s.append(key);
					s.append(": ");
					s.append(tag.get(key));
				}
				return s.toString();
			}
		});
		setTags(null);
	}
	
	public void setTags(Vector<Hashtable<String, String>> tags) {
		tagModel.removeAllElements();
		if (tags == null)
			return;
		for (int i = 0; i < tags.size(); i++)
			tagModel.addElement(tags.get(i));
	}
	
	public void focusOnDefaultComponent() {
	}

}
