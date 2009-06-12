package activator;

import java.util.*;

//{{{ PluginComparator
class PluginComparator implements Comparator {
	public int compare(Object alpha, Object beta) {
		PluginList.Plugin a = (PluginList.Plugin) alpha;
		PluginList.Plugin b = (PluginList.Plugin) beta;

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
