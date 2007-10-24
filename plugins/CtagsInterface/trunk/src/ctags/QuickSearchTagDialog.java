package ctags;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gjt.sp.jedit.View;

import db.TagDB;

@SuppressWarnings("serial")
public class QuickSearchTagDialog extends JDialog {

	JTextField name;
	JList tags;
	DefaultListModel model;
	View view;
	
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
		pack();
		setVisible(true);
	}

	protected void jumpToSelected() {
		Tag t = (Tag) tags.getSelectedValue();
		CtagsInterfacePlugin.jumpTo(view, t.file, t.line);
	}

	protected void setFilter() {
		model.removeAllElements();
		TagDB db = CtagsInterfacePlugin.getDB();
		String query = "SELECT * FROM " + TagDB.TAGS_TABLE + "," +
			TagDB.FILES_TABLE +
			" WHERE " + TagDB.TAGS_FILE_ID + "=" + TagDB.FILES_ID +
			" AND " + TagDB.TAGS_NAME + " LIKE " +
				db.quote("%" + name.getText() + "%");
		try {
			ResultSet rs = CtagsInterfacePlugin.getDB().query(query);
			while (rs.next()) {
				Tag t = new Tag(rs);
				if (t.isValid())
					model.addElement(t);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static private class Tag {
		String file;
		int line;
		String desc;
		public Tag(ResultSet rs) {
			StringBuffer text = new StringBuffer();
			try {
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
