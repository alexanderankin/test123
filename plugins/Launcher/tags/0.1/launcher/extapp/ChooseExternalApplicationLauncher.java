package launcher.extapp;


import java.io.File;

import launcher.Launcher;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class ChooseExternalApplicationLauncher extends Launcher {

	protected static final String PROP_PREFIX = ChooseExternalApplicationLauncher.class.getName();
	protected static final String SHORT_LABEL = jEdit.getProperty(PROP_PREFIX + ".label.short");

	public static final ChooseExternalApplicationLauncher INSTANCE = 
		new ChooseExternalApplicationLauncher();


	private ChooseExternalApplicationLauncher() {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL});
	}

	@Override
	public boolean launch(View view, Object resource) {
		File file = LauncherUtils.resolveToFile(resource);
    	if (file == null || !file.exists()) return false;
		String ext = LauncherUtils.getFileExtension(file.getName());
		String execPath = ExternalApplicationLaunchers.getInstance().pickApp(
								ext, view.getFocusOwner());
		if (execPath == null)
			return false;
        try {
        	LauncherUtils.runCmd( new String[] {execPath, resource.toString()});
        } catch (Exception exp) {
        	logFailedLaunch(this, execPath + " " + resource.toString(), exp);
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
				launcher.extapp.ChooseExternalApplicationLauncher.INSTANCE.launch(view,
						resolvedResource);
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "launcher.extapp.ChooseExternalApplicationLauncher.INSTANCE.launch(view, \"" +
						resolvedResource.toString() + "\");";
	}

}
