package launcher.text.selected;


import org.gjt.sp.jedit.View;

public class URLBrowserLauncher extends ComputeURLBrowserLauncher {

	protected static final String PROP_PREFIX = URLBrowserLauncher.class.getName();
	
	public static final URLBrowserLauncher INSTANCE = new URLBrowserLauncher();
	
	public URLBrowserLauncher() {
		super(PROP_PREFIX, null, false, false);
	}

	@Override
	protected String computeURL(View view, Object resource) {
		return resource == null ? null : resource.toString();
	}
	
}
