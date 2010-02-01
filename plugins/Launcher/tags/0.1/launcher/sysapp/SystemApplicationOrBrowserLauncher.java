package launcher.sysapp;


import java.io.File;
import java.net.URI;

import launcher.LauncherUtils;
import launcher.extapp.ExternalApplicationLauncher;

import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.View;

public class SystemApplicationOrBrowserLauncher extends ExternalApplicationLauncher {

	protected static final String PROP_PREFIX = SystemApplicationOrBrowserLauncher.class.getName();

	public static final SystemApplicationOrBrowserLauncher INSTANCE;
	
	static {
		String exe = null;
		if (OperatingSystem.isX11()) {
			exe = "xdg-open";
		} else if (OperatingSystem.isMacOS()) {
			exe = "open";
		} else if (OperatingSystem.isWindows()) {
			exe = "start";
		}
		if (exe == null)
			INSTANCE = null;
		else
			INSTANCE = new SystemApplicationOrBrowserLauncher(exe);
	}

	private SystemApplicationOrBrowserLauncher(String path) {
		super(PROP_PREFIX, new Object[]{path});
	}
	
	public static boolean isAvailable() {
		return INSTANCE!=null;
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		// We can launch any File or URI or String
		return resolvedResource!=null && (
				resolvedResource instanceof File ||
				resolvedResource instanceof URI ||
				resolvedResource instanceof String);
	}

	@Override
	public boolean launch(View view, Object resource) {
        try {
        	Object resolvedResource = LauncherUtils.resolveToFileOrURIOrString(resource);
        	if (resolvedResource == null) return false;
        	LauncherUtils.runCmd(new String[] {
        			getApplicationPath(), resolvedResource.toString()});
        } catch (Exception exp) {
        	logFailedLaunch(this, getApplicationPath() + " " + resource.toString(), exp);
            return false;
        }
		return true;
	}

}
