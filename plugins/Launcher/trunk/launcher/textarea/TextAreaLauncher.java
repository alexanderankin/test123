package launcher.textarea;

import launcher.Launcher;

import org.gjt.sp.jedit.textarea.JEditTextArea;

public abstract class TextAreaLauncher extends Launcher {
	
	protected TextAreaLauncher(String prop, Object[] args) {
		super(prop, args);
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		// We can deal with any JEditTextArea
		return resolvedResource instanceof JEditTextArea;
	}

}
