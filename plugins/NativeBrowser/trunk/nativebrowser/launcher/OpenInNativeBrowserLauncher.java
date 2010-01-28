package nativebrowser.launcher;

import java.net.URI;

import launcher.Launcher;
import launcher.LauncherUtils;
import nativebrowser.NativeBrowser;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class OpenInNativeBrowserLauncher extends Launcher {

	protected static final String PROP_PREFIX = OpenInNativeBrowserLauncher.class.getName();
	protected static final String SHORT_LABEL = jEdit.getProperty(PROP_PREFIX + ".label.short");
	
	public static final OpenInNativeBrowserLauncher INSTANCE = new OpenInNativeBrowserLauncher();
	
	private OpenInNativeBrowserLauncher() {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL});
	}

	OpenInNativeBrowserLauncher(Object resource) {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL}, true, false);
		setResource(resource);
	}

	@Override
	public boolean launch(View view, Object resource) {
		try {
			URI uri = LauncherUtils.resolveToURI(resource);
			DockableWindowManager wm = view.getDockableWindowManager();
			wm.addDockableWindow(nativebrowser.NativeBrowserPlugin.NAME);
			NativeBrowser nb = (NativeBrowser)wm.getDockableWindow(nativebrowser.NativeBrowserPlugin.NAME);
			nb.browse(view, uri.toString());
			return true;
		} catch (Exception exp) {
		}
		return false;
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		// We can launch any URI
		return resolvedResource instanceof URI;
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		if (false)
				nativebrowser.launcher.OpenInNativeBrowserLauncher.INSTANCE.launch(view,
						 launcher.LauncherUtils.resolveToURI(
									resolvedResource.toString()));
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "nativebrowser.launcher.OpenInNativeBrowserLauncher.INSTANCE.launch(view, \"" +
						"launcher.LauncherUtils.resolveToURI(\"" +
									resolvedResource.toString() + "\"));";
	}
	
}
