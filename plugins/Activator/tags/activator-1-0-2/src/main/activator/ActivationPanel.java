package activator;

import static activator.PluginList.ACTIVATED;
import static activator.PluginList.LOADED;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.gjt.sp.jedit.jEdit;

import common.gui.util.ConstraintFactory;

public class ActivationPanel extends JPanel implements ActionListener,
		MouseListener {
	private static final long serialVersionUID = -3331461368052873786L;

	private static ActivationPanel instance;

	private AbstractTableModel model;

	private JTable table;
	private JButton activate = new JButton("Activate");

	private JButton deactivate = new JButton("Deactivate");

	private JButton load = new JButton("Load");

	private JButton unload = new JButton("Unload");


	@SuppressWarnings("static-access")
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
		table.setDefaultRenderer(table.getColumnClass(0),
				new ActivationRenderer());
		ConstraintFactory cf = new ConstraintFactory();
		add(new JScrollPane(table), cf.buildConstraints(0, 0, 10, 1, cf.N,
				cf.BOTH));
		add(load, cf.buildConstraints(0, 1, 1, 1, cf.CENTER, cf.NONE, 0, 0));
		add(unload, cf.buildConstraints(1, 1, 1, 1, cf.CENTER, cf.NONE, 0, 0));
		add(activate, cf.buildConstraints(2, 1, 1, 1, cf.CENTER, cf.NONE, 0, 0));
		add(deactivate, cf.buildConstraints(3, 1, 1, 1, cf.CENTER, cf.NONE, 0, 0));
		int row = jEdit.getIntegerProperty("activator.rowselected", 1);
		ListSelectionModel smodel = table.getSelectionModel();
		smodel.setSelectionInterval(row, row);
		mouseReleased(null);
	}

	public void setVisible(boolean isVisible) {
		if (isVisible) {
		int row = jEdit.getIntegerProperty("activator.rowselected", 1);
		ListSelectionModel smodel = table.getSelectionModel();
		smodel.setSelectionInterval(row, row);
		mouseReleased(null);
		}
		super.setVisible(isVisible);
	}
	
	public static ActivationPanel getInstance() {
		if (instance == null) {
			instance = new ActivationPanel();
		}
		return instance;
	}

	// {{{ actionPerformed()
	public void actionPerformed(ActionEvent e) {
		int row = table.getSelectedRow();

		if (row < 0) {
			return;
		}
		PluginList.Plugin plugin = (PluginList.Plugin) table.getValueAt(row, 0);

		StopWatch sw = new StopWatch();

		if (e.getSource() == load) {
			sw.start();
			PluginManager.loadPluginJAR(plugin.getFile().toString());
			sw.stop();
			jEdit.getActiveView().getStatus().setMessage(
					plugin + " loaded in " + sw);
		}
		if (e.getSource() == unload) {
			sw.start();
			PluginManager.unloadPluginJAR(plugin.getJAR());
			sw.stop();
			jEdit.getActiveView().getStatus().setMessage(
					plugin + " unloaded in " + sw);
		}
		if (e.getSource() == activate) {
			sw.start();
			plugin.getJAR().activatePlugin();
			sw.stop();
			jEdit.getActiveView().getStatus().setMessage(
					plugin + " activated in " + sw);
		}
		if (e.getSource() == deactivate) {
			sw.start();
			plugin.getJAR().deactivatePlugin(false);
			sw.stop();
			jEdit.getActiveView().getStatus().setMessage(
					plugin + " deactivated in " + sw);
		}
		// Re-select the thing that was selected
		ListSelectionModel smodel =table.getSelectionModel();
		smodel.setSelectionInterval(row, row);
		// Fix up the buttons below
		mouseReleased(null);
	}// }}}

	// {{{ MouseListener impl

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent unused) {
		int row = table.getSelectedRow();
		table.updateUI();
		PluginList.Plugin plugin;
		if (row >= 0) {
			jEdit.setIntegerProperty("activator.rowselected", row);
			plugin = (PluginList.Plugin) table.getValueAt(row, 0);
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

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	// }}}

	// {{{ ActivationTableModel
	class ActivationTableModel extends DefaultTableModel implements Observer {

		private static final long serialVersionUID = 8707449444197059917L;

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
			switch (col) {
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
	}// }}}

}

// {{{ ActivationRenderer
class ActivationRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -3823797564394450958L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
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
} // }}}

