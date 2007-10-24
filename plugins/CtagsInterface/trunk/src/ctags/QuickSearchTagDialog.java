package ctags;

import java.awt.BorderLayout;
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
	
	public QuickSearchTagDialog(View view) {
		super(view, "Search tag", false);
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
		pack();
		setVisible(true);
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
				String name = rs.getString(TagDB.TAGS_NAME);
				String kind = rs.getString(TagDB.attr2col("kind"));
				if (kind == null)
					kind = "";
				else
					kind = " (" + kind + ")";
				model.addElement(name + kind);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
