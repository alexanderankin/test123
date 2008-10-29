package sn;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.gjt.sp.jedit.View;


@SuppressWarnings("serial")
public class DbDockable extends JPanel {

	public class DbTableModel extends AbstractTableModel {
		
		DbDescriptor desc;
		Vector<DbRecord> elements;
		
		public DbTableModel(DbDescriptor desc) {
			this.desc = desc;
			elements = new Vector<DbRecord>();
		}
		public void clear() {
			elements.clear();
		}
		@Override
		public int getColumnCount() {
			return desc.getColumnCount();
		}
		@Override
		public int getRowCount() {
			return elements.size();
		}
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= getRowCount() ||
				columnIndex < 0 || columnIndex >= getColumnCount())
				return null;
			return elements.get(rowIndex).getColumn(columnIndex);
		}
		public SourceLink getSourceLink(int selectedRow) {
			return elements.get(selectedRow).getSourceLink();
		}
		public void setElements(Vector<DbRecord> elements) {
			this.elements = elements;
		}
		@Override
		public String getColumnName(int column) {
			return desc.getColumn(column);
		}
	}
	
	private View view;
	private DbTableModel model;
	private JTable table;
	private JTextField text;
	private DbDescriptor dbDescriptor;
	
	public DbDockable(View view, String db)
	{
		super(new BorderLayout());
		this.view = view;
		dbDescriptor = SourceNavigatorPlugin.getDbDescriptor(db);
		System.err.println("Desc:" + dbDescriptor.name + " label:" + dbDescriptor.label);
		model = new DbTableModel(dbDescriptor);
		table = new JTable();
		table.setModel(model);
		table.setAutoCreateRowSorter(true);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//table.setAutoResizeColumns(true);
		//table.setAutoResizeWithHeaders(true);
		table.setRowSelectionAllowed(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					int sel = table.getSelectedRow();
					SourceLink link = (SourceLink)
						model.getSourceLink(table.convertRowIndexToModel(sel));
					if (link != null)
						link.jumpTo(DbDockable.this.view);
				}
			}
		});
		add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel p = new JPanel(new BorderLayout());
		JLabel l = new JLabel("Find:");
		p.add(l, BorderLayout.WEST);
		final JCheckBox prefix = new JCheckBox("Prefix");
		p.add(prefix, BorderLayout.EAST);
		text = new JTextField(40);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					find(text.getText(), prefix.isSelected());
			}
		});
		p.add(text, BorderLayout.CENTER);
		add(p, BorderLayout.NORTH);
	}
	
	private void find(String text, boolean prefixKey) {
		model.clear();
		Vector<DbRecord> records = DbAccess.lookup(dbDescriptor, text, prefixKey);
		model.setElements(records);
		model.fireTableDataChanged();
	}
}
