package launcher;

import org.gjt.sp.jedit.View;

public class ANDCompositeLauncher extends CompositeLauncher {

	public ANDCompositeLauncher(String prop, String shortName, Launcher[] children) {
		super(prop, shortName, children);
	}

	@Override
	public boolean launch(View view, Object resource) {
		boolean returnValue = false;
		for(Launcher launcher: children) {
			if (launcher != null)
				returnValue = returnValue | launcher.launch(view, resource);
		}
		return returnValue;
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		for (Launcher child : children) {
			if (!child.canLaunch(resolvedResource))
				return false;
		}
		return true;
	}
	
	public boolean isStateful() {
		return false;
	}

}
