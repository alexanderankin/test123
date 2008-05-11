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
import javax.swing.JOptionPane;
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

	private static final String MISSING = "<missing attribute>";
	private static final String OTHER = "<other>";
	/**
	 * 
	 */
	private IconTableModel items;
	private JTextField name;
	private JTable values;
	private JTextField value;
	private FileTextField icon;
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
		items.add(OTHER, null);
		items.add(MISSING, null);
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
		JButton del = new JButton("Remove");
		buttons.add(del);

		c.gridx = 0;
		c.gridy += c.gridheight;
		c.gridheight = 1;
		c.gridwidth = 2;
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
				int index = items.getRowCount() - 1;
				values.getSelectionModel().setSelectionInterval(
						index, index);
				value.selectAll();
				value.requestFocus();
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
	public boolean canClose() {
		String attr = name.getText();
		if (attr == null || attr.length() == 0)
		{
			JOptionPane.showMessageDialog(this,
					"Please specify the attribute name.");
			return false;
		}
		return true;
	}

	@Override
	public void save() {
		Vector<String> params = new Vector<String>();
		params.add(name.getText());
		String missingIcon = null;
		String otherIcon = null;
		for (int i = 0; i < items.getRowCount(); i++)
		{
			String value = items.getValueAt(i, 1).toString();
			String icon = items.getValueAt(i, 2).toString();
			if (value.equals(OTHER))
				otherIcon = icon;
			else if (value.equals(MISSING))
				missingIcon = icon;
			else {
				params.add(value);
				params.add(icon);
			}
		}
		params.add(otherIcon);
		params.add(missingIcon);
		processor.setParams(params);
	}
	
	public static class IconTableModel extends AbstractTableModel {

		public static class IconTableRow {
			IconTableRow(String v, String f) {
				if (f == null)
					f = "";
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