package activator;

import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import common.gui.actions.*;

class Reload extends CustomAction {
	private String name;
	private PluginList.Plugin plugin;
	private PluginJAR jar;
	
	public Reload(PluginJAR pluginJAR, String dispName) {
		super(dispName);
		name = dispName;
		jar = pluginJAR;
		
	}
	
	public Reload(PluginList.Plugin plugin) {
		super(plugin.getJAR().getPlugin().getClassName());
		this.plugin = plugin;
		jar = plugin.getJAR();
		name = jar.getPlugin().getClassName();
		setToolTipText("Click to reload: " + jar.toString());
//		setName(jar.getPlugin().getClassName());
		
	}
	
	public void actionPerformed(ActionEvent event) {
		PluginList pl = PluginList.getInstance();
		Log.log(Log.DEBUG,this,"Reloading "+jar);
		HashSet<String> unloaded = PluginManager.unloadPluginJAR(jar);
		if (jar != null) {
			PluginManager.unloadPluginJAR(jar);
		}
		PluginManager.loadPluginJAR(jar.getPath());
		// Reload the dependent plugins that were unloaded before
		for (String path: unloaded) 
			PluginManager.loadPluginJAR(path);
	}
}
