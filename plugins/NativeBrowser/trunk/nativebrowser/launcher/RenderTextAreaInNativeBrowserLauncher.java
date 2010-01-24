package nativebrowser.launcher;

import launcher.textarea.TextAreaLauncher;
import nativebrowser.NativeBrowser;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class RenderTextAreaInNativeBrowserLauncher extends TextAreaLauncher {

	protected static final String PROP_PREFIX = RenderTextAreaInNativeBrowserLauncher.class.getName();
	protected static final String SHORT_LABEL = jEdit.getProperty(PROP_PREFIX + ".label.short");
	
	public static final RenderTextAreaInNativeBrowserLauncher INSTANCE = new RenderTextAreaInNativeBrowserLauncher();
	
	private RenderTextAreaInNativeBrowserLauncher() {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL});
	}

	@Override
	public boolean launch(View view, Object resource) {
		try {
			if (!(resource instanceof JEditTextArea))
				return false;
			JEditTextArea textArea = (JEditTextArea)resource;
			String html = textArea.getText();
			DockableWindowManager wm = view.getDockableWindowManager();
			wm.addDockableWindow(nativebrowser.NativeBrowserPlugin.NAME);
			NativeBrowser nb = (NativeBrowser)wm.getDockableWindow(nativebrowser.NativeBrowserPlugin.NAME);
			nb.render(html);
			return true;
		} catch (Exception exp) {
		}
		return false;
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		if (false)
				nativebrowser.launcher.RenderTextAreaInNativeBrowserLauncher.INSTANCE.launch(view, view.getTextArea());
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "nativebrowser.launcher.RenderTextAreaInNativeBrowserLauncher.INSTANCE.launch(view, view.getTextArea());";
	}

}
