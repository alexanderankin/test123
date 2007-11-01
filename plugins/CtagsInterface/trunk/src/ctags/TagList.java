package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
	static String [] extensionOrder = new String [] {
		"class", "access" 
	};
	
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
		Tag tag = (Tag) tagModel.getElementAt(selectedIndex);
		String file = tag.getFile();
		int line = tag.getLine();
		if (line >= 0)
			CtagsInterfacePlugin.jumpTo(view, file, line);
	}

	public void setTags(Vector<Tag> tags) {
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
			Tag tag = (Tag) tagModel.getElementAt(index);
			l.setText(getHtmlText(tag, index));
			l.setFont(new Font("Monospaced", Font.PLAIN, 12));
			ImageIcon icon = tag.getIcon();
			if (icon != null)
				l.setIcon(icon);
			return l;
		}

		private String getHtmlText(Tag tag, int index) {
			StringBuffer s = new StringBuffer("<html>");
			s.append(index + 1);
			s.append(": <b>");
			s.append(tag.getName());
			s.append("</b>  ");
			String originType = tag.getAttachment("O_TYPE");
			if (originType != null &&
				originType.equals(TagDB.PROJECT_ORIGIN))
			{
				String project = tag.getAttachment("O_NAME");
				if (project != null && project.length() > 0) {
					s.append("(<i>");
					s.append(project);
					s.append("</i>)  ");
				}
			}
			s.append(tag.getFile());
			s.append((tag.getLine() >= 0) ? ":" + tag.getLine() : "");
			s.append("<br>");
			s.append(depattern(tag.getPattern()));
			s.append("<br>");
			Vector<String> extOrder = new Vector<String>();
			for (int i = 0; i < extensionOrder.length; i++)
				if (tag.getExtension(extensionOrder[i]) != null)
					extOrder.add(extensionOrder[i]);
			TreeSet<String> extensions =
				new TreeSet<String>(tag.getExtensions());
			Iterator<String> it = extensions.iterator();
			while (it.hasNext()) {
				String extension = (String) it.next();
				if (extension.equals("line") || extOrder.contains(extension))
					continue;
				extOrder.add(extension);
			}
			boolean first = true;
			for (int i = 0; i < extOrder.size(); i++) {
				if (! first)
					s.append(",  ");
				first = false;
				String extension = extOrder.get(i);
				s.append(extOrder.get(i));
				s.append(": ");
				s.append(tag.getExtension(extension));
			}
			return s.toString();
		}

		private Object depattern(String pattern) {
			if (pattern.startsWith("/^"))
				pattern = pattern.substring(2);
			if (pattern.endsWith("$/"))
				pattern = pattern.substring(0, pattern.length() - 2);
			return pattern;
		}
	}

}
