package launcher.text;

import launcher.Launcher;
import launcher.LauncherType;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.OptionPane;

public class TextLauncherType extends LauncherType {
	
	public static final String SERVICE_NAME = TextLauncherType.class.getName();
	
	public static final TextLauncherType INSTANCE = new TextLauncherType();

	private TextLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
	}

	@Override
	public Object resolve(Object resource) {
		return LauncherUtils.resolveToCharSequence(resource);
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
