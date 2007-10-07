/**
 * 
 */
package ctags.sidekick.renderers;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import common.gui.FileTextField;

import ctags.sidekick.AbstractObjectEditor;

@SuppressWarnings("serial")
public class AttributeIconEditor extends AbstractObjectEditor {

	/**
	 * 
	 */
	private IconTableModel items;
	private JTextField name;
	private JTable values;
	private JTextField value;
	private FileTextField icon;
	private FileTextField unspecified;
	private FileTextField missing;
	private static final int FileFieldColumns = 20;
	
	public AttributeIconEditor(AttributeIconProvider provider) {
		super(provider);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		JPanel p = new JPanel();
		add(p, c);
		p.add(new JLabel("Attribute:"));
		name = new JTextField(15);
		p.add(name, c);

		c.gridx = 0;
		c.gridy++;
		add(new JLabel("Values with associated icons:"), c);
		items = new IconTableModel();
		items.add("Dummy", "");
		values = new JTable(items);
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 5;
		add(new JScrollPane(values), c);
		c.gridx += c.gridwidth;
		c.gridwidth = 1;
		JPanel buttons = new JPanel(new GridLayout(0, 1));
		add(buttons, c);
		JButton add = new JButton("Add");
		buttons.add(add);
		JButton edit = new JButton("Edit");
		buttons.add(edit);
		JButton del = new JButton("Remove");
		buttons.add(del);

		c.gridx = 0;
		c.gridy += c.gridheight;
		c.gridheight = 1;
		c.gridwidth = 2;
		p = new JPanel();
		add(p, c);
		p.add(new JLabel("Icon for unspecified value:"));
		unspecified = new FileTextField(true);
		unspecified.getTextField().setColumns(FileFieldColumns);
		p.add(unspecified, c);

		c.gridy++;
		p = new JPanel();
		add(p, c);
		p.add(new JLabel("Icon if attribute is missing:"), c);
		missing = new FileTextField(true);
		missing.getTextField().setColumns(FileFieldColumns);
		p.add(missing, c);
		
		c.gridy++;
		p = new JPanel();
		add(p, c);
		p.add(new JLabel("Value:"));
		value = new JTextField(15);
		p.add(value);
		p.add(new JLabel("Icon:"), c);
		icon = new FileTextField(true);
		icon.getTextField().setColumns(FileFieldColumns);
		p.add(icon);
		JButton update = new JButton("Update");
		p.add(update);
		
		values.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		values.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = values.getSelectedRow();
				if (index < 0)
				{
					value.setEnabled(false);
					icon.setEnabled(false);
					return;
				}
				value.setEnabled(true);
				icon.setEnabled(true);
				value.setText((String) items.getValueAt(index, 1));
				icon.getTextField().setText((String) items.getValueAt(index, 2));
			}
		});
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				items.add("<value>", "<icon>");
			}
		});
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = values.getSelectedRow();
				if (index >= 0)
					items.remove(index);
			}
		});
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int index = values.getSelectedRow();
				String file = icon.getTextField().getText();
				items.set(index, value.getText(), file);
			}
		});
	}
	
	@Override
	public void save() {
		StringBuffer buf = new StringBuffer(name.getText());
		buf.append(AttributeIconProvider.SECTION_SEPARATOR);
		for (int i = 0; i < items.getRowCount(); i++)
		{
			if (i > 0)
				buf.append(AttributeIconProvider.VALUE_SEPARATOR);
			buf.append(items.getValueAt(i, 1).toString());
			buf.append(AttributeIconProvider.VALUE_SEPARATOR);
			buf.append(items.getValueAt(i, 2).toString());
		}
		buf.append(AttributeIconProvider.SECTION_SEPARATOR);
		buf.append(unspecified.getTextField().getText());
		buf.append(AttributeIconProvider.SECTION_SEPARATOR);
		buf.append(missing.getTextField().getText());
		processor.setParams(buf.toString());
	}
	
	public static class IconTableModel extends AbstractTableModel {

		public static class IconTableRow {
			IconTableRow(String v, String f) {
				icon = new ImageIcon(f);
				value = v;
				file = f;
			}
			ImageIcon icon;
			String value;
			String file;
		}
		Vector<IconTableRow> rows = new Vector<IconTableRow>();
		
		public void add(String value, String file) {
			rows.add(new IconTableRow(value, file));
			this.fireTableDataChanged();
		}
		public void remove(int index) {
			rows.remove(index);
			this.fireTableDataChanged();
		}
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0: return "Icon";
			case 1: return "Value";
			case 2: return "Icon file";
			}
			return null;
		}
		@Override
		public Class<?> getColumnClass(int col) {
			if (col == 0)
				return ImageIcon.class;
			return super.getColumnClass(col);
		}
		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return rows.size();
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return rows.get(row).icon;
			case 1:
				return rows.get(row).value;
			case 2:
				return rows.get(row).file;
			}
			return null;
		}
		public void set(int row, String value, String file) {
			rows.set(row, new IconTableRow(value, file));
			this.fireTableDataChanged();
		}
		
	}
}