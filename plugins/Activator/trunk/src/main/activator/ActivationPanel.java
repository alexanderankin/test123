package activator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import common.gui.actions.*;
import common.gui.util.*;

public class ActivationPanel extends JPanel {
	private AbstractTableModel model;
	private JTable table;
	
	public ActivationPanel() {
		setLayout(new GridBagLayout());
		model = new ActivationTableModel();
		table = new JTable(model);
		table.setDefaultRenderer(table.getColumnClass(0),new ActivationRenderer());
		ConstraintFactory cf = new ConstraintFactory();
		add(new JScrollPane(table),cf.buildConstraints(0,0,1,1,cf.N,cf.BOTH));
	}
	
	//{{{ ActivationTableModel
	class ActivationTableModel extends AbstractTableModel {
		private PluginJAR[] jars;
		
		public ActivationTableModel() {
			jars = jEdit.getPluginJARs();
		}
		
		public int getRowCount() {
			return jars.length;
		}
		
		public int getColumnCount() {
			return 3;
		}
		
		public String getColumnName(int col) {
			switch(col) {
				case 0:
				return "Plugin";
				case 1:
				return "State";
				case 2:
				return "Reload";
				default:
				return "error";
			}
		}
		
		public Object getValueAt(int row, int col) {
			//Log.log(Log.DEBUG,this,"getValueAt("+row+","+col+")");
			if (col == 0) {
				if (jars[row].getPlugin() instanceof EditPlugin.Deferred) {
					return jars[row].getFile().getName();
				} else {
					if (jars[row].getPlugin() == null) {
						return jars[row].getFile().getName();
					} else {
						Log.log(Log.DEBUG,this,jars[row].getPlugin().getClassName());
						return jEdit.getProperty("plugin."+jars[row].getPlugin().getClassName()+".name","No name property");
					}
				}
			}
			
			if (col == 1) {
				if (jars[row].getPlugin() == null) {
					return "Library";
				} else if (jars[row].getPlugin() instanceof EditPlugin.Deferred) {
					return "Loaded";
				} else {
					return "Activated";
				}
			}
			if (col == 2) {
				return new Reload(jars[row]);
			}
			return "error";
		}
		
	}//}}}
}

class Reload extends CustomAction {
	private PluginJAR jar;
	public Reload(PluginJAR jar) {
		super("Reload");
		this.jar=jar;
	}
	
	public void actionPerformed(ActionEvent event) {
		Log.log(Log.DEBUG,this,"Reloading "+jar);
	}
}

class ActivationRenderer extends DefaultTableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, 
												boolean isSelected, boolean hasFocus, 
												int row, int column) {
		super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
		
		if (column == 1) {
			if (value.equals("Loaded")) {
				setBackground(Color.YELLOW);
			} else if (value.equals("Activated")) {
				setBackground(Color.GREEN);
			}
		} else {
			setBackground(Color.WHITE);
		}
		
		if (column == 2) {
			return new JButton((Action) value);
		}
		
		return this;
	}
}
