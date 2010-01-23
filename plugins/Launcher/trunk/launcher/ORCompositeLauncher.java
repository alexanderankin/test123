package launcher;

import org.gjt.sp.jedit.View;

public class ORCompositeLauncher extends CompositeLauncher {

	public ORCompositeLauncher(String prop, String shortName, Launcher[] children) {
		super(prop, shortName, children);
	}

	@Override
	public boolean launch(View view, Object resource) {
		for(Launcher launcher: children) {
			if (launcher != null && launcher.launch(view, resource))
				return true;
		}
		return false;
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		for (Launcher child : children) {
			if (child.canLaunch(resolvedResource))
				return true;
		}
		return false;
	}

	public boolean isStateful() {
		return false;
	}

}
