package activator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
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
			String dir = MiscUtilities.getParentOfPath(jarPath);

			StringTokenizer st = new StringTokenizer(jars);
			while(st.hasMoreTokens())
			{
				String _jarPath = MiscUtilities.constructPath(dir,st.nextToken());
				PluginJAR _jar = jEdit.getPluginJAR(_jarPath);
				if(_jar == null)
				{
					jEdit.addPluginJAR(_jarPath);
				}
			}
		}

		if (jar.checkDependencies())
			jar.activatePluginIfNecessary();
	} //}}}

	//{{{ unloadPluginJar()


	
	private static Stack<String> unloaded;
	private static Set<String> unloadedSet;
	/**
	 * Safely unloads plugins, and deactivates all plugins that depend
	 * on this one.
	 * @param jar the plugin you wish to unload
	 * @return a stack of strings, one for each deactivated plugin, in the reverse order
	 *    they were unloaded.
	 */
	public static Stack<String> unloadPluginJAR(PluginJAR jar)
	{
		unloaded = new Stack<String>();
		unloadedSet = new HashSet<String>();
		unloadedSet = Collections.synchronizedSet(unloadedSet);
		
		unloadRecursive(jar);
		return unloaded;
		
	}
	
	private static void unloadRecursive(PluginJAR jar)
	{
		String[] dependents = jar.getDependentPlugins();
		for (String dependent : dependents) 
		{
			if (!unloadedSet.contains(dependent)) 
			{
				unloadedSet.add(dependent);
				PluginJAR _jar = jEdit.getPluginJAR(dependent);
				if(_jar != null)  {
					
					unloadRecursive(_jar);
				}
			}
		}
		unloaded.push(jar.getPath());
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
