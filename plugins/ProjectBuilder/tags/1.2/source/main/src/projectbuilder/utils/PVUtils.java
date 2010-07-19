package projectbuilder.utils;
// imports {{{
import projectviewer.vpt.VPTRoot;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTGroup;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.jEdit;
import java.util.HashMap;
import java.util.ArrayList;
// }}} imports
public class PVUtils {
	private static HashMap groups;
	private static ArrayList names;
	public static HashMap listGroups() {
		names = new ArrayList();
		groups = new HashMap();
		parseNode(VPTRoot.getInstance());
		return groups;
	}
	public static ArrayList groupNames() { return names; }
	private static void parseNode(VPTNode node) {
		String name = node.getName();
		names.add(name);
		groups.put(name, node);
		for (int i = 0; i<node.getChildCount(); i++) {
			VPTNode child = (VPTNode) node.getChildAt(i);
			if (child.isGroup()) {
				parseNode(child);
			}
		}
	}
}
