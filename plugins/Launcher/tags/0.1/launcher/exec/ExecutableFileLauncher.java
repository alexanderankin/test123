package launcher.exec;

import java.io.File;

import launcher.Launcher;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class ExecutableFileLauncher extends Launcher {
	
	protected static final String PROP_PREFIX = ExecutableFileLauncher.class.getName();
	protected static final String SHORT_LABEL = jEdit.getProperty(PROP_PREFIX + ".label.short");

	public static final ExecutableFileLauncher INSTANCE =
		new ExecutableFileLauncher();
	
	protected ExecutableFileLauncher() {
		super(PROP_PREFIX, new Object[]{SHORT_LABEL});
	}

	@Override
	public boolean launch(View view, Object resource) {
        try {
        	File file = LauncherUtils.resolveToFile(resource);
        	if (file == null || !file.exists()) return false;
        	LauncherUtils.runCmd(file.toString());
        } catch (Exception exp) {
        	logFailedLaunch(this, resource, exp);
            return false;
        }
		return true;
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		// We can launch any File that exists
		// Because this launcher can be used as a SystemApplicationOrBrowserLauncher
		// which resolves to File, URI, or String, we need to resolve again
		// to a File instance.
		if (resolvedResource == null)
			return false;
		File file = LauncherUtils.resolveToFile(resolvedResource);
		if (file == null)
			return false;
		return file.exists();
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		if (false)
				launcher.exec.ExecutableFileLauncher.INSTANCE.launch(view,
						resolvedResource);
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "launcher.exec.ExecutableFileLauncher.INSTANCE.launch(view, \"" +
						resolvedResource.toString() + "\");";
	}

}
