package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Hashtable;
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
		tags.setCellRenderer(new DefaultListCellRenderer() {
			@SuppressWarnings("unchecked")
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
				Hashtable<String, String> tag = (Hashtable<String, String>)
					tagModel.getElementAt(index);
				l.setText(getHtmlText(tag, index));
				return l;
			}
			private String getHtmlText(Hashtable<String, String> tag, int index) {
				StringBuffer s = new StringBuffer("<html>");
				s.append(index + 1);
				s.append(": <b>");
				s.append(tag.get(TagDB.NAME_COL));
				s.append("</b>  ");
				s.append(tag.get(TagDB.FILE_COL));
				s.append(tag.containsKey("LINE") ? ":" + tag.get("LINE") : "");
				s.append("<br>");
				s.append(tag.get(TagDB.PROJECT_COL));
				s.append("<br><u>");
				s.append(tag.get(TagDB.PATTERN_COL));
				s.append("</u>");
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
