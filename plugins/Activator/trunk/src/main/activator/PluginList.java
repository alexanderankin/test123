package activator;

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

public class PluginList extends Observable {
	public static final String LOADED = jEdit.getProperty("activator.Loaded", "Loaded");
	public static final String ERROR = jEdit.getProperty("activator.Error", "Error");
	public static final String ACTIVATED = jEdit.getProperty("activator.Activated", "Activated");
	public static final String NOT_LOADED = jEdit.getProperty("activator.Not_Loaded", "Not Loaded");

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

	public void clear() {
	     plugins.clear();
	}

	public Plugin get(int i) {
		try {
			return plugins.get(i);
		}
		catch (IndexOutOfBoundsException iobe) {
			return null;
		}
	}

	public int size() {
		return plugins.size();
	}

	/**
 	* @return the count of just the plugins in this list, does not count
 	* any library files.
 	*/
	public int pluginCount() {
        int count = 0;
        for (Plugin plugin : plugins) {
            if (!plugin.isLibrary()) {
                ++count;
            }
        }
        return count;
	}

	//{{{ Plugin
	class Plugin {
		private PluginJAR jar;
		private File file;
		int hashValue = -1;
		public Plugin(PluginJAR jar) {
			this.jar = jar;
			this.file = jar.getFile();
		}
		public Plugin(File file) {
			this.file=file;
		}

		public String getStatus() {
			if (jar == null) {
				return NOT_LOADED;
			}
			if (jar.getPlugin() == null) {
				return LOADED + " " + jEdit.getProperty("activator.(library)", "(library)");
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

		/**
 		 * @return true if the plugin should be activated on jEdit startup.
 		 */
		public boolean loadOnStartup() {
		    if (jar == null) {
		         return false;
		    }
		    if (jar.getPlugin() == null) {
		        return false;
		    }
		    String activate = jEdit.getProperty("plugin." + jar.getPlugin().getClassName() + ".activate", "defer");
		    return "startup".equals(activate);
		}

		public void setLoadOnStartup(boolean b) {
		    jEdit.setProperty("plugin." + jar.getPlugin().getClassName() + ".activate", b ? "startup" : "defer");
		}

		public boolean canLoadOnStartup() {
		    if (jar == null) {
		         return false;
		    }
		    if (jar.getPlugin() == null) {
		        return false;
		    }
		    return true;
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
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return file.toString().hashCode();
		}

        public boolean equals( Object obj )
        {
            // check for reference equality
            if ( this == obj )
            {
                return true;
            }

            // type check
            if ( !( obj instanceof PluginList.Plugin ) )
            {
                return false;
            }

            // cast to correct type
            PluginList.Plugin other = ( PluginList.Plugin ) obj;

            // check fields
            return getFile().equals(other.getFile() );
        }

	} //}}}

}
