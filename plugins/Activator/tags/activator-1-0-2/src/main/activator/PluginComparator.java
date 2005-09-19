package activator;

import java.util.*;

//{{{ PluginComparator
class PluginComparator implements Comparator {
	public int compare(Object alpha, Object beta) {
		PluginList.Plugin a = (PluginList.Plugin) alpha;
		PluginList.Plugin b = (PluginList.Plugin) beta;
		return a.toString().compareTo(b.toString());
	}
}//}}}
