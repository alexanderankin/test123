package activator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import common.gui.actions.*;
import common.gui.util.*;

public class ReloadPanel extends JPanel implements Observer {
	private static ReloadPanel instance;
	java.util.List<PluginList.Plugin> plugins = new ArrayList<PluginList.Plugin>();
	ConstraintFactory cf = new ConstraintFactory();
	
	private ReloadPanel() {
		setLayout(new GridBagLayout());
		setBackground(Color.GRAY);
		update();
		PluginList.getInstance().addObserver(this);
	}
	
	public static ReloadPanel getInstance() {
		if (instance == null) {
			instance = new ReloadPanel();
		}
		return instance;
	}
	
	public void update(Observable o, Object arg) {
		update();
	}
	
	public void update() {
		plugins.removeAll(plugins);
		removeAll();
		for (PluginJAR pj : jEdit.getPluginJARs()) {
			if (pj.getPlugin() != null) {
				plugins.add(PluginList.getInstance().new Plugin(pj));
			}
		}
		Collections.sort(plugins,new PluginComparator());
		int row = 0;
		PluginJAR jar;
		for (PluginList.Plugin plugin : plugins) {
			jar = plugin.getJAR();
			if (jar.getPlugin() == null) {
				continue;
			}
			JLabel name = new JLabel(plugin.toString());
			String status = PluginManager.getPluginStatus(jar);
			if (status.equals("Loaded")) {
				name.setForeground(Color.YELLOW);
			} else if (status.equals("Activated")) {
				name.setForeground(Color.GREEN);
			} else if (status.equals("Error")) {
				name.setForeground(Color.RED);
			}
			add(name,cf.buildConstraints(0,row,1,1));
			add(new JButton(new Reload(jar)),cf.buildConstraints(1,row,1,1));
			row++;
		}
	}
}


