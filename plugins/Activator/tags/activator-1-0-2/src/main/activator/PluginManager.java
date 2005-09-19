package activator;

import java.util.*;

import org.gjt.sp.jedit.*;

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
	public static void unloadPluginJAR(PluginJAR jar)
	{
		String[] dependents = jar.getDependentPlugins();
		for(int i = 0; i < dependents.length; i++)
		{
			PluginJAR _jar = jEdit.getPluginJAR(
				dependents[i]);
			if(_jar != null)
			{
				unloadPluginJAR(_jar);
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
