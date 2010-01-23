package launcher;

import org.gjt.sp.jedit.View;

public abstract class CompositeLauncher extends Launcher {

	protected final Launcher[] children;
	
	public CompositeLauncher(String name, String shortName, Launcher[] children) {
		super(name, new Object[]{shortName});
		this.children = children;
	}

	@Override
	public boolean noRecord() {
		return true;
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		return null;
	}

}
