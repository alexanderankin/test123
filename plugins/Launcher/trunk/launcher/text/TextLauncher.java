package launcher.text;

import launcher.Launcher;

public abstract class TextLauncher extends Launcher {
	
	protected TextLauncher(String prop, Object[] args) {
		this(prop, args, false, false);
	}

	protected TextLauncher(String prop, Object[] args, boolean stateful, boolean userDefined) {
		super(prop, args, stateful, userDefined);
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		// We can deal with any CharSequence (String, StringBuffer, ...)
		return resolvedResource instanceof CharSequence;
	}

}
