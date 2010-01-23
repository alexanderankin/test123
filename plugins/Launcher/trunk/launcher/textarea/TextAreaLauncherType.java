package launcher.textarea;

import launcher.Launcher;
import launcher.LauncherType;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class TextAreaLauncherType extends LauncherType {
	
	public static final String SERVICE_NAME = TextAreaLauncherType.class.getName();
	
	public static final TextAreaLauncherType INSTANCE = new TextAreaLauncherType();

	private TextAreaLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
	}

	@Override
	public Object resolve(Object resource) {
		return resource instanceof JEditTextArea ? resource : null;
	}

	@Override
	public OptionPane getOptionPane() {
		return null;
	}

	@Override
	public Launcher getDefaultLauncher() {
		return null;
	}

}
