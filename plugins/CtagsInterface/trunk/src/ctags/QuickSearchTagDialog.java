package ctags;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import options.ProjectsOptionPane;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

import db.TagDB;

@SuppressWarnings("serial")
public class QuickSearchTagDialog extends JDialog {

	JTextField name;
	JList tags;
	DefaultListModel model;
	View view;
	Vector<QuickSearchTag> tagNames;
	/** This window will contains the scroll with the items. */
	private final JWindow window = new JWindow(this);
	
	public QuickSearchTagDialog(View view) {
		super(view, "Search tag", false);
		this.view = view;
		JPanel p = new JPanel();
		p.add(new JLabel("Type part of the tag name:"));
		name = new JTextField(30);
		p.add(name);
		add(p, BorderLayout.NORTH);
		model = new DefaultListModel();
		tags = new JList(model);
		tags.setBorder(BorderFactory.createEtchedBorder());
		tags.setCellRenderer(new TagListCellRenderer());
		window.setContentPane(new JScrollPane(tags));
		name.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setFilter();
			}
			public void insertUpdate(DocumentEvent e) {
				setFilter();
			}
			public void removeUpdate(DocumentEvent e) {
				setFilter();
			}
		});
		name.addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e) {
					if (handledByList(e)) {
						tags.dispatchEvent(e);
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						jumpToSelected();
					}
				}
			}
		);
		tags.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				jumpToSelected();
			}
		});
		tags.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				name.dispatchEvent(e);
			}
				    
			public void keyPressed(KeyEvent e) {
				if (!handledByList(e)) {
					name.dispatchEvent(e);
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					jumpToSelected();
				}
			}
		});
		TagDB db = CtagsInterfacePlugin.getDB();
		ResultSet rs;
		String query = "SELECT " + TagDB.TAGS_TABLE + ".*," +
			TagDB.FILES_NAME +
			" FROM " + TagDB.TAGS_TABLE + "," + TagDB.FILES_TABLE +
			" WHERE " + TagDB.TAGS_FILE_ID + "=" + TagDB.FILES_ID;
		if (ProjectsOptionPane.getSearchActiveProjectOnly()) {
			String project = CtagsInterfacePlugin.getProjectWatcher().getActiveProject(view);
			query = query + " AND EXISTS (SELECT " + TagDB.MAP_FILE_ID +
				" FROM " + TagDB.MAP_TABLE + "," + TagDB.ORIGINS_TABLE +
				" WHERE " + TagDB.MAP_TABLE + "." + TagDB.MAP_ORIGIN_ID +
					"=" + TagDB.ORIGINS_TABLE + "." + TagDB.ORIGINS_ID +
				" AND " + TagDB.ORIGINS_TABLE + "." + TagDB.ORIGINS_NAME +
					"=" + db.quote(project) +
				" AND " + TagDB.ORIGINS_TABLE + "." + TagDB.ORIGINS_TYPE +
					"=" + db.quote(TagDB.PROJECT_ORIGIN) +
				")";
		}
		try {
			tagNames = new Vector<QuickSearchTag>();
			rs = db.query(query);
			while (rs.next())
				tagNames.add(new QuickSearchTag(rs));
			rs.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		pack();
		setLocationRelativeTo(view);
		setVisible(true);
	}

	protected void jumpToSelected() {
		QuickSearchTag t = (QuickSearchTag) tags.getSelectedValue();
		CtagsInterfacePlugin.jumpTo(view, t.file, t.line);
		dispose();
	}

	protected void setFilter() {
		model.removeAllElements();
		String substr = name.getText();
		for (int i = 0; i < tagNames.size(); i++) {
			QuickSearchTag t = tagNames.get(i);
			if (t.name.contains(substr))
				model.addElement(t);
		}
		if (model.isEmpty())
		{
			window.setVisible(false);
		}
		else
		{
			tags.setVisibleRowCount(Math.min(10, model.size()));
			window.pack();
			window.setVisible(true);
		}
	}
	
	private static boolean handledByList(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_DOWN ||
		e.getKeyCode() == KeyEvent.VK_UP ||
		e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
		e.getKeyCode() == KeyEvent.VK_PAGE_UP;
	}
	
	public void setVisible(boolean b) {
		Rectangle bounds = getBounds();
		window.setLocation(bounds.x, bounds.y + bounds.height);
		GUIUtilities.requestFocus(this, name);
		window.setVisible(false);
		super.setVisible(b);
	}
	
	static private class QuickSearchTag {
		String file;
		int line;
		String name;
		String desc;
		String kind;
		public QuickSearchTag(ResultSet rs) {
			StringBuffer text = new StringBuffer();
			try {
				name = rs.getString(TagDB.TAGS_NAME);
				text.append(rs.getString(TagDB.TAGS_NAME));
				kind = rs.getString(TagDB.extension2column("kind"));
				if (kind != null)
					text.append(" (" + kind + ")");
				file = rs.getString(TagDB.FILES_NAME);
				String lineStr = rs.getString(TagDB.extension2column("line"));
				if (lineStr != null)
					line = Integer.valueOf(lineStr);
				else
					line = -1;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			desc = text.toString();
		}
		public boolean isValid() {
			return (desc.length() > 0 && file != null && line >= 0);
		}
		public String toString() {
			return desc;
		}
		public ImageIcon getIcon() {
			return KindIconProvider.getIcon(kind);
		}
	}

	public class TagListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel l = (JLabel) super.getListCellRendererComponent(
				list, value, index, isSelected, cellHasFocus);
			if (value instanceof QuickSearchTag) {
				ImageIcon icon = ((QuickSearchTag)value).getIcon();
				if (icon != null)
					l.setIcon(icon);
			}
			return l;
		}

	}

}
