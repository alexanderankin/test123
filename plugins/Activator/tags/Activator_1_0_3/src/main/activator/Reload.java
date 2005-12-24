package activator;

import java.awt.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import common.gui.actions.*;

class Reload extends CustomAction {
	private PluginJAR jar;
	public Reload(PluginJAR jar, String name) {
		super(name);
		setToolTipText("Click to reload: " + jar.toString());
//		setName(jar.getPlugin().getClassName());
		this.jar=jar;
	}
	
	public void actionPerformed(ActionEvent event) {
		Log.log(Log.DEBUG,this,"Reloading "+jar);
		PluginManager.unloadPluginJAR(jar);
		PluginManager.loadPluginJAR(jar.getPath());
	}
}
