package activator;

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

public class PluginList extends Observable {
	public static final String LOADED = "Loaded";
	public static final String ERROR = "Error";
	public static final String ACTIVATED = "Activated";
	public static final String NOT_LOADED = "Not Loaded";
	
	private static PluginList instance;
	private static List<Plugin> plugins = new ArrayList<Plugin>();
	
	private PluginList() {
	}
	
	public static PluginList getInstance() {
		if (instance == null) {
			instance = new PluginList();
		}
		return instance;
	}
	
	public void addPlugin(PluginJAR jar) {
		plugins.add(new Plugin(jar));
	}
	
	public void addPlugin(File file) {
		plugins.add(new Plugin(file));
	}
	
	public void update() {
		plugins = new ArrayList<Plugin>();
		for (PluginJAR pj : jEdit.getPluginJARs()) {
			addPlugin(pj);
		}
		for (String file : jEdit.getNotLoadedPluginJARs()) {
			addPlugin(new File(file));
			Log.log(Log.DEBUG,this,file);
		}
		Collections.sort(plugins,new PluginComparator());
		setChanged();
		notifyObservers();
	}
	
	public Plugin get(int i) {
		return plugins.get(i);
	}
	
	public int size() {
		return plugins.size();
	}
	
	//{{{ Plugin
	class Plugin {
		private PluginJAR jar;
		private File file;
		public Plugin(PluginJAR jar) {
			this.jar = jar;
		}
		public Plugin(File file) {
			this.file=file;
		}
		
		public String getStatus() {
			if (jar == null) {
				return NOT_LOADED;
			}
			if (jar.getPlugin() == null) {
				return LOADED+" (library)";
			} else if (jar.getPlugin() instanceof EditPlugin.Deferred) {
				return LOADED;
			} else if (jar.getPlugin() instanceof EditPlugin.Broken) {
				return ERROR;
			} else {
				return ACTIVATED;
			}
		}
		
		public PluginJAR getJAR() {
			return jar;
		}
		
		public File getFile() {
			return file;
		}
		
		public boolean isLoaded() {
			return getStatus() != NOT_LOADED;
		}
		
		public boolean isActivated() {
			return getStatus() == ACTIVATED;
		}
		
		public boolean isLibrary() {
			if (jar == null) {
				return false;
			}
			return jar.getPlugin() == null;
		}
		
		public String toString() {
			if (jar == null) {
				return file.getName();
			}
			if (jar.getPlugin() == null) {
				return jar.getFile().getName();
			}
			if (jar.getPlugin() instanceof EditPlugin.Deferred) {
				return jar.getFile().getName();
			} else {
				return jEdit.getProperty("plugin."+jar.getPlugin().getClassName()+".name","No name property");
			}
		}
	} //}}}
	
}
