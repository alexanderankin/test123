package projectbuilder.utils

import projectviewer.vpt.VPTRoot
import projectviewer.vpt.VPTNode
import projectviewer.vpt.VPTGroup
import org.gjt.sp.jedit.Macros
import org.gjt.sp.jedit.jEdit as JEDIT

public class PVUtils {
	private static ArrayList groups
	private static ArrayList names
	private static String depth
	public static ArrayList listGroups() {
		groups = []
		names = []
		depth = ""
		parseNode(VPTRoot.getInstance())
		return groups
	}
	public static ArrayList groupNames() { return names; }
	private static void parseNode(VPTNode node) {
		groups.add(node)
		names.add(depth+" "+node.getName())
		for (int i = 0; i<node.getChildCount(); i++) {
			VPTNode child = node.getChildAt(i)
			if (child.isGroup()) {
				depth += "--"
				parseNode(child)
			}
		}
		try {
			depth = depth.substring(0, depth.length()-2)
		} catch (Exception e) {}
	}
}
