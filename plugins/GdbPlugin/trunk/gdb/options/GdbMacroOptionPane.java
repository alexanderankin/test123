package gdb.options;

import gdb.variables.TypeMacroMap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class GdbMacroOptionPane extends AbstractOptionPane {

	private static final class ReadOnlyTable extends JTable {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	private JTextField typePatternTF;
	private JTextField typeReplacementTF;
	private JTable table;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_MACRO_MAP_LABEL = PREFIX + "gdb_macro_map_label";
	private static final String TYPE_PATTERN_LABEL = PREFIX + "type_pattern_label";
	private static final String TYPE_REPLACEMENT_LABEL = PREFIX + "type_replacement_label";
	protected static final String TYPE_PATTERN_DEFAULT = PREFIX + "type_pattern_default";
	protected static final String TYPE_REPLACEMENT_DEFAULT = PREFIX + "type_replacement_default";

	private DefaultTableModel model;
	
	public GdbMacroOptionPane() {
		super("debugger.macros");
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridBagLayout());
		
		// Type inferrence panel
		JPanel typePanel = new JPanel(new GridLayout(0, 1));
		typePanel.add(new JLabel("The fields below specify a regexp substitution for type names:"));
		JPanel typeFields = new JPanel();
		typeFields.add(new JLabel(jEdit.getProperty(TYPE_PATTERN_LABEL)));
		typePatternTF = new JTextField(20);
		typePatternTF.setText(jEdit.getProperty(TypeMacroMap.TYPE_PATTERN));
		typeFields.add(typePatternTF);
		typeFields.add(new JLabel(jEdit.getProperty(TYPE_REPLACEMENT_LABEL)));
		typeReplacementTF = new JTextField(10);
		typeReplacementTF.setText(jEdit.getProperty(TypeMacroMap.TYPE_REPLACEMENT));
		typeFields.add(typeReplacementTF);
		JButton resetTypeSubst = new JButton("Reset");
		typeFields.add(resetTypeSubst);
		resetTypeSubst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				typePatternTF.setText(jEdit.getProperty(TYPE_PATTERN_DEFAULT));
				typeReplacementTF.setText(jEdit.getProperty(TYPE_REPLACEMENT_DEFAULT));
			}
		});
		typePanel.add(typeFields);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.ipady = 20;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		add(typePanel, c);
		
		// Type -> Macro table
		c.gridy++;
		add(new JLabel(jEdit.getProperty(GDB_MACRO_MAP_LABEL)), c);
		table = new ReadOnlyTable();
		model = new DefaultTableModel();
		model.addColumn("Type");
		model.addColumn("Macro");
		table.setModel(model);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editRow(table.getSelectedRow());
			}
		});
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
				editRow(model.getRowCount() - 1);
			}
		});
		buttons.add(add);
		JButton del = new JButton("Remove");
		buttons.add(del);
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sel = table.getSelectedRow();
				if (sel == -1)
					return;
				model.removeRow(sel);
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
	@Override
	protected void _save()
	{
		// Workaround for committing the last cell editing operation
		TypeMacroMap tmm = TypeMacroMap.getInstance();
		tmm.clear();
		for (int i = 0; i < model.getRowCount(); i++) {
			String type = (String) model.getValueAt(i, 0); 
			String macro = (String) model.getValueAt(i, 1);
			if (type == null || macro == null || type.equals("") || macro.equals(""))
				continue;
			tmm.put(type, macro);
		}
		tmm.setTypeInferrence(typePatternTF.getText(), typeReplacementTF.getText());
		tmm.save();
	}

	private void editRow(int row) {
		MacroTypePairEditor editor = new MacroTypePairEditor(model, row,
				GUIUtilities.getParentDialog(GdbMacroOptionPane.this));
		editor.setVisible(true);
		String type = (String) model.getValueAt(row, 0);
		String macro = (String) model.getValueAt(row, 1);
		if (type == null || macro == null || type.matches("^\\s*$") ||
				macro.matches("^\\s*$"))
			model.removeRow(row);
	}

}
