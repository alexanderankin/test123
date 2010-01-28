package launcher.browser;

import java.awt.Desktop.Action;
import java.net.URI;

import launcher.JavaDesktopHelper;
import launcher.Launcher;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class JavaDesktopBrowserLauncher extends Launcher {
	
	protected static final String PROP_PREFIX = JavaDesktopBrowserLauncher.class.getName();
	protected static final String SHORT_LABEL = jEdit.getProperty(PROP_PREFIX + ".label.short");
	
	private static boolean isAvailable =
		JavaDesktopHelper.isDesktopAPIAvailable() &&
		JavaDesktopHelper.getDesktop().isSupported(Action.BROWSE);
	
	public static final JavaDesktopBrowserLauncher INSTANCE =
		isAvailable ? new JavaDesktopBrowserLauncher() : null;
	
	protected static boolean isAvailable() {
		return isAvailable;
	}

	protected JavaDesktopBrowserLauncher() {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL});
	}

	@Override
	public boolean launch(View view, Object resource) {
        try {
    		URI uri = LauncherUtils.resolveToURI(resource);
        	if (uri == null) return false;
        	JavaDesktopHelper.getDesktop().browse(uri);
        } catch (Exception exp) {
        	logFailedLaunch(this, resource, exp);
        	return false;
        }
        return true;
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		// We can launch any URI
		// Because this launcher can be used as a SystemApplicationOrBrowserLauncher
		// which resolves to File, URI, or String, we need to resolve again
		// to a URI instance.
		if (resolvedResource == null)
			return false;
		URI uri = LauncherUtils.resolveToURI(resolvedResource);
		return uri != null;
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		if (false)
				launcher.browser.JavaDesktopBrowserLauncher.INSTANCE.launch(view,
						 launcher.LauncherUtils.resolveToURI(
								resolvedResource.toString()));
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "launcher.browser.JavaDesktopBrowserLauncher.INSTANCE.launch(view, " +
						"launcher.LauncherUtils.resolveToURI(\"" +
								resolvedResource.toString() + "\"));";
	}

}
