package activator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import common.gui.actions.*;
import common.gui.util.*;

import static activator.PluginList.*;

public class ActivationPanel extends JPanel implements ActionListener,MouseListener {
	private static ActivationPanel instance;
	
	private AbstractTableModel model;
	private JTable table;
	
	private JButton load = new JButton("Load");
	private JButton unload = new JButton("Unload");
	private JButton activate = new JButton("Activate");
	private JButton deactivate = new JButton("Deactivate");
	
	private ActivationPanel() {
		load.setEnabled(false);
		unload.setEnabled(false);
		activate.setEnabled(false);
		deactivate.setEnabled(false);
		load.addActionListener(this);
		unload.addActionListener(this);
		activate.addActionListener(this);
		deactivate.addActionListener(this);
		setLayout(new GridBagLayout());
		model = new ActivationTableModel();
		table = new JTable(model);
		table.addMouseListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(table.getColumnClass(0),new ActivationRenderer());
		ConstraintFactory cf = new ConstraintFactory();
		add(new JScrollPane(table),cf.buildConstraints(0,0,10,1,cf.N,cf.BOTH));
		add(load,cf.buildConstraints(0,1,1,1,cf.CENTER,cf.NONE,0,0));
		add(unload,cf.buildConstraints(1,1,1,1,cf.CENTER,cf.NONE,0,0));
		add(activate,cf.buildConstraints(2,1,1,1,cf.CENTER,cf.NONE,0,0));
		add(deactivate,cf.buildConstraints(3,1,1,1,cf.CENTER,cf.NONE,0,0));
	}
	
	public static ActivationPanel getInstance() {
		if (instance == null) {
			instance = new ActivationPanel();
		}
		return instance;
	}
	
	//{{{ actionPerformed()
	public void actionPerformed(ActionEvent e) {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		PluginList.Plugin plugin = (PluginList.Plugin) table.getValueAt(row,0);
		
		if (e.getSource() == load) {
			PluginManager.loadPluginJAR(plugin.getFile().toString());
		}
		if (e.getSource() == unload) {
			PluginManager.unloadPluginJAR(plugin.getJAR());
		}
		if (e.getSource() == activate) {
			plugin.getJAR().activatePlugin();
		}
		if (e.getSource() == deactivate) {
			plugin.getJAR().deactivatePlugin(false);
		}
	}//}}}
	
	//{{{ MouseListener impl
	
	public void mousePressed(MouseEvent e) {
	}
	
	public void mouseReleased(MouseEvent e) {
		int row = table.getSelectedRow();
		PluginList.Plugin plugin;
		if (row >= 0) {
			plugin = (PluginList.Plugin) table.getValueAt(row,0);
			load.setVisible(!plugin.isLoaded());
			unload.setVisible(plugin.isLoaded());
			activate.setVisible(!plugin.isActivated());
			deactivate.setVisible(plugin.isActivated());
			load.setEnabled(!plugin.isLoaded());
			unload.setEnabled(plugin.isLoaded());
			activate.setEnabled(!plugin.isActivated());
			deactivate.setEnabled(plugin.isActivated());
			if (plugin.getStatus() == PluginList.ERROR || plugin.isLibrary()) {
				activate.setEnabled(false);
			}
		} else {
			load.setVisible(true);
			unload.setVisible(true);
			activate.setVisible(true);
			deactivate.setVisible(true);
			load.setEnabled(false);
			unload.setEnabled(false);
			activate.setEnabled(false);
			deactivate.setEnabled(false);
		}
	}
	
	public void mouseClicked(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	//}}}
	
	//{{{ ActivationTableModel
	class ActivationTableModel extends DefaultTableModel implements Observer {		
		public ActivationTableModel() {
			PluginList.getInstance().addObserver(this);
		}
		
		public int getRowCount() {
			return PluginList.getInstance().size();
		}
		
		public int getColumnCount() {
			return 2;
		}
		
		public String getColumnName(int col) {
			switch(col) {
				case 0:
				return "Plugin";
				case 1:
				return "State";
				default:
				return "error";
			}
		}
		
		public Object getValueAt(int row, int col) {
			PluginList.Plugin p = PluginList.getInstance().get(row);
			if (col == 0) {
				return p;
			}
			
			if (col == 1) {
				return p.getStatus();
			}
			return "error";
		}
		
		public void update(Observable o, Object arg) {
			fireTableDataChanged();
		}
	}//}}}

}

//{{{ ActivationRenderer
class ActivationRenderer extends DefaultTableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, 
												boolean isSelected, boolean hasFocus, 
												int row, int column) {
		super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
		setForeground(Color.BLACK);
		if (column == 1) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				if (value == LOADED) {
					setForeground(Color.YELLOW);
				} else if (value == ACTIVATED) {
					setForeground(Color.GREEN);
				} else if (value == PluginList.ERROR) {
					setForeground(Color.RED);
				} 
			} else {
				if (value == LOADED) {
					setBackground(Color.YELLOW);
				} else if (value == ACTIVATED) {
					setBackground(Color.GREEN);
				} else if (value == PluginList.ERROR) {
					setBackground(Color.RED);
				} else {
					setBackground(Color.WHITE);
				}
			}
		} else {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(Color.WHITE);
			}
		}
		
		if (column == 2) {
			return new JButton((Action) value);
		}
		
		return this;
	}
} //}}}


