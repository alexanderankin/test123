package activator;


import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;

import common.gui.util.ConstraintFactory;

public class ReloadPanel extends JPanel implements Observer {
	private static ReloadPanel instance;
	List <PluginList.Plugin> plugins = new ArrayList<PluginList.Plugin>();
	HashMap<File, PluginList.Plugin> confirmed = new HashMap<File, PluginList.Plugin>(); 
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
		plugins.clear();
		removeAll();
		PluginList pl = PluginList.getInstance();
		int numPlugins = pl.size();
		PluginJAR jar;
		for (int i=0; i<numPlugins; ++i) {
			PluginList.Plugin plugin = pl.get(i);
			jar = plugin.getJAR();
			if (jar == null || jar.getPlugin() == null) {
				continue;
			}
			confirmed.put(plugin.getFile(), plugin);
			
		}
		
		plugins.addAll(confirmed.values());
		Collections.sort(plugins,new PluginComparator());
		int row = 0;
		
		for (PluginList.Plugin plugin : plugins) {
			JLabel name = new JLabel(plugin.toString());
			jar = plugin.getJAR();

			if (jar.getPlugin() == null) {
				continue;
			}
			JButton button = new JButton(new Reload(jar, plugin.toString()));
			String status = PluginManager.getPluginStatus(jar);
			if (status.equals("Loaded")) {
				button.setBackground(Color.YELLOW);
			} else if (status.equals(PluginList.ACTIVATED)) {
				button = new JButton(new Reload(jar, plugin.toString()));
				button.setBackground(Color.GREEN);
			} else if (status.equals(PluginList.NOT_LOADED)) {
				button.setBackground(Color.GRAY);
			} else if (status.equals(PluginList.ERROR)) {
				button.setBackground(Color.RED);
			}
//			add(name,cf.buildConstraints(0,row,1,1));
			add(button, cf.buildConstraints(1,row,1,1)); 
			row++;
		}
	}


}


