package activator;

import java.util.*;

//{{{ PluginComparator
/**
 * Sorts plugins in PluginList.  Sort first by type, with library jars
 * after plugins, then by name, so plugins are listed first and sorted
 * by name, followed by library jars sorted by name.
 */
class PluginComparator implements Comparator {
	public int compare(Object alpha, Object beta) {
		Plugin a = (Plugin) alpha;
		Plugin b = (Plugin) beta;

		// check if plugin is a library, if so, it sorts after a plugin
		if (a.isLibrary() && !b.isLibrary()) {
		    return 1;
		}
		else if (b.isLibrary() && !a.isLibrary()) {
		     return -1;
		}
		return a.toString().toLowerCase().compareTo(b.toString().toLowerCase());
	}
}//}}}
