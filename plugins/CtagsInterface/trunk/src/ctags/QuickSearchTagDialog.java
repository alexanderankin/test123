package ctags;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import options.ProjectsOptionPane;

import org.gjt.sp.jedit.View;

import db.TagDB;

@SuppressWarnings("serial")
public class QuickSearchTagDialog extends JDialog {

	JTextField name;
	JList tags;
	DefaultListModel model;
	View view;
	Vector<Tag> tagNames;
	
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
		tags.setVisibleRowCount(10);
		add(new JScrollPane(tags), BorderLayout.CENTER);
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
		tags.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				jumpToSelected();
			}
		});
		tags.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
			tagNames = new Vector<Tag>();
			rs = db.query(query);
			while (rs.next())
				tagNames.add(new Tag(rs));
			rs.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		pack();
		setVisible(true);
	}

	protected void jumpToSelected() {
		Tag t = (Tag) tags.getSelectedValue();
		CtagsInterfacePlugin.jumpTo(view, t.file, t.line);
		dispose();
	}

	protected void setFilter() {
		model.removeAllElements();
		String substr = name.getText();
		for (int i = 0; i < tagNames.size(); i++) {
			Tag t = tagNames.get(i);
			if (t.name.contains(substr))
				model.addElement(t);
		}
	}
	
	static private class Tag {
		String file;
		int line;
		String name;
		String desc;
		public Tag(ResultSet rs) {
			StringBuffer text = new StringBuffer();
			try {
				name = rs.getString(TagDB.TAGS_NAME);
				text.append(rs.getString(TagDB.TAGS_NAME));
				String kind = rs.getString(TagDB.attr2col("kind"));
				if (kind != null)
					text.append(" (" + kind + ")");
				file = rs.getString(TagDB.FILES_NAME);
				String lineStr = rs.getString(TagDB.attr2col("line"));
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
	}
}
