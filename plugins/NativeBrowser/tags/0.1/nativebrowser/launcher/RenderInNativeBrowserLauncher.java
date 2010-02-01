package nativebrowser.launcher;

import launcher.LauncherUtils;
import launcher.text.TextLauncher;
import nativebrowser.NativeBrowser;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class RenderInNativeBrowserLauncher extends TextLauncher {

	protected static final String PROP_PREFIX = RenderInNativeBrowserLauncher.class.getName();
	protected static final String SHORT_LABEL = jEdit.getProperty(PROP_PREFIX + ".label.short");
	
	public static final RenderInNativeBrowserLauncher INSTANCE = new RenderInNativeBrowserLauncher();
	
	private RenderInNativeBrowserLauncher() {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL});
	}

	@Override
	public boolean launch(View view, Object resource) {
		try {
			CharSequence text = LauncherUtils.resolveToCharSequence(resource);
			if (text == null)
				return false;
			DockableWindowManager wm = view.getDockableWindowManager();
			wm.addDockableWindow(NativeBrowser.NAME);
			NativeBrowser nb =
				(NativeBrowser)wm.getDockableWindow(NativeBrowser.NAME);
			nb.render(text.toString());
			return true;
		} catch (Exception exp) {
		}
		return false;
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		if (false)
				nativebrowser.launcher.RenderInNativeBrowserLauncher.INSTANCE.launch(view, launcher.LauncherUtils.resolveToCharSequence(view.getTextArea()));
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "nativebrowser.launcher.RenderInNativeBrowserLauncher.INSTANCE.launch(view, launcher.LauncherUtils.resolveToCharSequence(view.getTextArea()));";
	}

}
