package sn;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gjt.sp.jedit.View;

import sn.DbAccess.RecordHandler;

import com.sleepycat.db.DatabaseEntry;

@SuppressWarnings("serial")
public class RefByList extends JPanel {

	private View view;
	private JTextField text;
	private JList list;
	private DefaultListModel model;

	public RefByList(View view) {
		super(new BorderLayout());
		this.view = view;
		model = new DefaultListModel();
		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					SourceElement refBy = (SourceElement) list.getSelectedValue();
					if (refBy != null)
						refBy.jumpTo(RefByList.this.view);
				}
			}
		});
		add(new JScrollPane(list), BorderLayout.CENTER);
		JPanel p = new JPanel(new BorderLayout());
		JLabel l = new JLabel("Find:");
		p.add(l, BorderLayout.WEST);
		text = new JTextField(40);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					find(text.getText());
				else
					super.keyReleased(e);
			}
		});
		p.add(text, BorderLayout.CENTER);
		add(p, BorderLayout.NORTH);
	}
	
	private DatabaseEntry identifierToKey(String identifier) {
        int index = identifier.lastIndexOf("::");
        byte[] bytes;
        if (index >= 0) {
        	String namespace = identifier.substring(0, index);
        	String name = identifier.substring(index + 2);
        	bytes = new byte[index + 1 + name.length() + 1];
        	for (int i = 0; i < namespace.length(); i++)
        		bytes[i] = (byte) namespace.charAt(i);
        	bytes[index] = 1;
        	for (int i = 0; i < name.length(); i++)
        		bytes[index + 1 + i] = (byte) name.charAt(i);
        } else {
        	bytes = new byte[3 + identifier.length()];
        	bytes[0] = '#';
        	bytes[1] = 1;
        	for (int i = 0; i < identifier.length(); i++)
        		bytes[2 + i] = (byte) identifier.charAt(i);
        }
    	bytes[bytes.length - 1] = 1;
        return new DatabaseEntry(bytes);
	}
	
	private class RefByRecordHandler implements RecordHandler {
		private String dir;
		private String identifier;
		public RefByRecordHandler(String dir, String identifier) {
			this.dir = dir;
			this.identifier = identifier;
		}
		@Override
		public boolean handle(DatabaseEntry key, DatabaseEntry data) {
			String [] strings = keyToStrings(key);
			if (! getIdentifier(strings).equals(identifier))
				return false;
			model.addElement(recordToSourceElement(strings, dir));
			return true;
		}
		private String [] keyToStrings(DatabaseEntry key) {
			byte [] bytes = key.getData();
			String [] strings = new String[9];
			int start = 0;
			int index = 0;
			for (int i = 0; i < bytes.length && index < 9; i++) {
				if (bytes[i] <= 1) {
					strings[index++] = new String(bytes, start, i - start);
					start = i + 1;
				}
			}
			if (index < 9)
				strings[index] = new String(bytes, start, bytes.length - start - 1);
			return strings;
		}
		private String getIdentifier(String [] strings) {
			if (strings[0].equals("#"))
				return strings[1];
			return strings[0] + "::" + strings[1];
		}
		private SourceElement recordToSourceElement(String [] strings, String dir) {
			return new SourceElement(strings[3], strings[4], strings[5], strings[8],
				Integer.valueOf(strings[7]), dir);
		}
		
	}
	private void find(String identifier) {
		model.clear();
		DbAccess db = new DbAccess("by");
		DatabaseEntry key = identifierToKey(identifier);
		DatabaseEntry data = new DatabaseEntry();
		db.lookup(key, data, new RefByRecordHandler(db.getDir(), identifier));
	}
}
