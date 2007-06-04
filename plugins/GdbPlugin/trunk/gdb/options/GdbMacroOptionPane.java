package gdb.options;

import gdb.variables.TypeMacroMap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class GdbMacroOptionPane extends AbstractOptionPane {

	private JTable table;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_MACRO_MAP_LABEL = PREFIX + "gdb_macro_map_label";

	private DefaultTableModel model;
	
	public GdbMacroOptionPane() {
		super("debugger.macros");
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.ipady = 20;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		add(new JLabel(jEdit.getProperty(GDB_MACRO_MAP_LABEL)), c);
		table = new JTable();
		model = new DefaultTableModel();
		model.addColumn("Type");
		model.addColumn("Macro");
		table.setModel(model);
		TypeMacroMap tmm = TypeMacroMap.getInstance();
		String [] keys = new String[tmm.size()];
		tmm.keySet().toArray(keys);
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++) 
			model.addRow(new String[] {keys[i], tmm.get(keys[i])});
		c.fill = GridBagConstraints.BOTH;
		c.gridy++;
		c.weighty = 1.0;
		add(new JScrollPane(table), c);
		JPanel buttons = new JPanel();
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addRow(new String [] {"", ""});
			}
		});
		buttons.add(add);
		JButton del = new JButton("Remove");
		buttons.add(del);
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.removeRow(table.getSelectedRow());
			}
		});
		JButton clear = new JButton("Remove all");
		buttons.add(clear);
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setRowCount(0);
			}
		});
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy++;
		c.weighty = 0.0;
		add(buttons, c);
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void _save()
	{
		// Workaround for committing the last cell editing operation
		table.getCellEditor().stopCellEditing();
		TypeMacroMap tmm = TypeMacroMap.getInstance();
		tmm.clear();
		for (int i = 0; i < model.getRowCount(); i++) {
			String type = (String) model.getValueAt(i, 0); 
			String macro = (String) model.getValueAt(i, 1);
			if (type == null || macro == null || type.equals("") || macro.equals(""))
				continue;
			tmm.put(type, macro);
		}
		tmm.save();
	}

}
