package activator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import common.gui.actions.*;
import common.gui.util.*;

public class ReloadPanel extends JPanel {
	private static ReloadPanel instance;
	java.util.List<Entry> plugins = new ArrayList<Entry>();
	ConstraintFactory cf = new ConstraintFactory();
	
	private ReloadPanel() {
		setLayout(new GridBagLayout());
		setBackground(Color.GRAY);
		update();
	}
	
	public static ReloadPanel getInstance() {
		if (instance == null) {
			instance = new ReloadPanel();
		}
		return instance;
	}
	
	public void update() {
		plugins.removeAll(plugins);
		removeAll();
		for (PluginJAR pj : jEdit.getPluginJARs()) {
			if (pj.getPlugin() != null) {
				plugins.add(new Entry(pj));
			}
		}
		Collections.sort(plugins,new EntryComparator());
		int row = 0;
		PluginJAR jar;
		for (Entry plugin : plugins) {
			jar = plugin.getJar();
			if (jar.getPlugin() == null) {
				continue;
			}
			JLabel name;
			if (jar.getPlugin() instanceof EditPlugin.Deferred) {
				name = new JLabel(jar.getFile().getName());
			} else {
				name = new JLabel(jEdit.getProperty("plugin."+jar.getPlugin().getClassName()+".name","No name property"));
			}
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

class Entry {
	private PluginJAR jar;
	public Entry(PluginJAR jar) {
		this.jar = jar;
	}
	
	public String toString() {
		if (jar.getPlugin() instanceof EditPlugin.Deferred) {
			return jar.getFile().getName();
		} else {
			return jEdit.getProperty("plugin."+jar.getPlugin().getClassName()+".name","No name property");
		}
	}
	
	public PluginJAR getJar() {
		return jar;
	}
}

class EntryComparator implements Comparator {
	public int compare(Object alpha, Object beta) {
		Entry a = (Entry) alpha;
		Entry b = (Entry) beta;
		return a.toString().compareTo(b.toString());
	}
}
