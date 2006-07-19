package activator;

import java.util.HashSet;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.jEdit;

public class PluginManager {
	private PluginManager() {}
	
	//{{{ loadPluginJAR()
	public static void loadPluginJAR(String jarPath)
	{
		jEdit.addPluginJAR(jarPath);
		PluginJAR jar = jEdit.getPluginJAR(jarPath);
		if(jar == null || jar.getPlugin() == null)
			return;

		String jars = jEdit.getProperty("plugin."
			+ jar.getPlugin().getClassName() + ".jars");

		if(jars != null)
		{
			String dir = MiscUtilities.getParentOfPath(
				jarPath);

			StringTokenizer st = new StringTokenizer(jars);
			while(st.hasMoreTokens())
			{
				String _jarPath
					= MiscUtilities.constructPath(
					dir,st.nextToken());
				PluginJAR _jar = jEdit.getPluginJAR(
					_jarPath);
				if(_jar == null)
				{
					jEdit.addPluginJAR(_jarPath);
				}
			}
		}

		jar.checkDependencies();
		jar.activatePluginIfNecessary();
	} //}}}

	//{{{ unloadPluginJar()

	private static HashSet<String> unloaded;
	/**
	 * Safely unloads plugins, and deactivates all plugins that depend
	 * on this one.
	 * @param jar the plugin you wish to unload
	 */
	public static HashSet<String> unloadPluginJAR(PluginJAR jar)
	{
		unloaded = new HashSet<String>();
		unloadRecursive(jar);
		return unloaded;
		
	}
	
	private static void unloadRecursive(PluginJAR jar)
	{
		String[] dependents = jar.getDependentPlugins();
		for (String dependent : dependents) 
		{
			if (!unloaded.contains(dependent)) 
			{
				PluginJAR _jar = jEdit.getPluginJAR(dependent);
				unloaded.add(dependent);
				if(_jar != null) unloadRecursive(_jar);
			}
		}

		jEdit.removePluginJAR(jar,false);
	} //}}}

	
	
	public static String getPluginStatus(PluginJAR jar) {
		if (jar == null) {
			return "Not Loaded";
		}
		if (jar.getPlugin() == null) {
			return "Library";
		} else if (jar.getPlugin() instanceof EditPlugin.Deferred) {
			return "Loaded";
		} else if (jar.getPlugin() instanceof EditPlugin.Broken) {
			return "Error";
		} else {
			return "Activated";
		}
	}
}
