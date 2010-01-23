package launcher.sysapp;

import java.awt.Desktop.Action;
import java.io.File;

import launcher.JavaDesktopHelper;
import launcher.Launcher;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class SystemApplicationJavaDesktopLauncher extends Launcher {
	
	protected static final String PROP_PREFIX = SystemApplicationJavaDesktopLauncher.class.getName();
	protected static final String SHORT_LABEL = jEdit.getProperty(PROP_PREFIX + ".label.short");

	private static boolean isAvailable =
		JavaDesktopHelper.isDesktopAPIAvailable() &&
		JavaDesktopHelper.getDesktop().isSupported(Action.OPEN);
	
	public static final SystemApplicationJavaDesktopLauncher INSTANCE =
		isAvailable ? new SystemApplicationJavaDesktopLauncher() : null;
	
	protected static boolean isAvailable() {
		return isAvailable;
	}

	protected SystemApplicationJavaDesktopLauncher() {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL});
	}

	@Override
	public boolean launch(View view, Object resource) {
        try {
        	File file = LauncherUtils.resolveToFile(resource);
        	if (file == null || !file.exists()) return false;
        	JavaDesktopHelper.getDesktop().open(file);
        } catch (Exception exp) {
        	logFailedLaunch(this, resource, exp);
        	return false;
        }
        return true;
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		// We can launch any File
		return resolvedResource instanceof File;
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		if (false)
				launcher.sysapp.SystemApplicationJavaDesktopLauncher.INSTANCE.launch(view,
						resolvedResource);
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "launcher.sysapp.SystemApplicationJavaDesktopLauncher.INSTANCE.launch(view, \"" +
						resolvedResource.toString() + "\");";
	}

}
