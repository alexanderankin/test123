package gdb.options;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import gdb.variables.TypeMacroMap;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.gjt.sp.jedit.AbstractOptionPane;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class GdbMacroOptionPane extends AbstractOptionPane {
	private JTable macroTable;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_MACRO_MAP_LABEL = PREFIX + "gdb_macro_map_label";
	
	public GdbMacroOptionPane() {
		super("debugger.macros");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		addComponent(new JLabel("df"));
		macroTable = new JTable();
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Type");
		model.addColumn("Macro");
		macroTable.setModel(model);
		addComponent(macroTable);
		TypeMacroMap tmm = TypeMacroMap.getInstance();
		Iterator<Entry<String, String>> entries = tmm.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, String> entry = entries.next();
			model.addRow(new String[] {entry.getKey(), entry.getValue()});
		}
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void _save()
	{
	}

}
