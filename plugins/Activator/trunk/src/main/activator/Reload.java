package activator;

import java.awt.event.ActionEvent;
import java.util.Stack;

import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.util.Log;

import common.gui.actions.CustomAction;

class Reload extends CustomAction {
	private String name;
	private PluginList.Plugin plugin;
	private PluginJAR jar;
	
	public Reload(PluginJAR pluginJAR, String dispName) {
		super(dispName);
		name = dispName;
		jar = pluginJAR;
		setToolTipText("Click to reload: " + name);
	}
	
	public Reload(PluginList.Plugin plugin) {
		super(plugin.getJAR().getPlugin().getClassName());
		this.plugin = plugin;
		jar = plugin.getJAR();
		name = jar.getPlugin().getClassName();
		setToolTipText("Click to reload: " + name);
//		setName(jar.getPlugin().getClassName());
		
	}
	
	public void actionPerformed(ActionEvent event) {

		Log.log(Log.DEBUG,this,"Reloading "+jar);
		Stack<String> unloaded = PluginManager.unloadPluginJAR(jar);
		if (jar != null) {
			PluginManager.unloadPluginJAR(jar);
		}
		String path = null;
		do {
			path = unloaded.pop();
			if (path != null) PluginManager.loadPluginJAR(path);
		} while (path != null);
	}
}
