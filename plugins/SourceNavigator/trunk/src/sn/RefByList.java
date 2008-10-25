package sn;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.gjt.sp.jedit.View;

import sn.DbAccess.RecordHandler;

import com.sleepycat.db.DatabaseEntry;

@SuppressWarnings("serial")
public class RefByList extends JPanel {

	private View view;
	private JTextField text;
	private JTable table;
	private SourceElementTableModel model;
	
	private class SourceElementTableModel extends AbstractTableModel {
		private final String [] Columns = { "Type", "Name", "Kind", "Location" };
		private Vector<SourceElement> elements;

		public SourceElementTableModel() {
			elements = new Vector<SourceElement>();
		}
		public SourceElement getElement(int rowIndex) {
			if (rowIndex < 0 || rowIndex >= getRowCount())
				return null;
			return elements.get(rowIndex);
		}
		public void clear() {
			elements.clear();
		}
		public void addElement(SourceElement element) {
			elements.add(element);
		}
		public int getColumnCount() {
			return Columns.length;
		}
		public String getColumnName(int columnIndex) {
			return Columns[columnIndex];
		}
		public int getRowCount() {
			return elements.size();
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= elements.size() ||
				columnIndex < 0 || columnIndex >= getColumnCount())
			{
				return null;
			}
			SourceElement element = elements.get(rowIndex);
			switch (columnIndex) {
			case 0: return element.namespace;
			case 1: return element.name;
			case 2: return element.kind;
			case 3: return element.getLocation();
			}
			return null;
		}
	}
	public RefByList(View view) {
		super(new BorderLayout());
		this.view = view;
		model = new SourceElementTableModel();
		table = new JTable(model);
		table.setRowSelectionAllowed(true);
		//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					SourceElement refBy = (SourceElement)
						model.getElement(table.getSelectedRow());
					if (refBy != null)
						refBy.jumpTo(RefByList.this.view);
				}
			}
		});
		add(new JScrollPane(table), BorderLayout.CENTER);
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
		model.fireTableDataChanged();
	}
}
