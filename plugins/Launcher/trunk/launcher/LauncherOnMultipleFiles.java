package launcher;

import java.io.File;

import org.gjt.sp.jedit.View;

public abstract class LauncherOnMultipleFiles extends Launcher {

	public LauncherOnMultipleFiles(String property, Object[] args,
			boolean stateful, boolean userDefined) {
		super(property, args, stateful, userDefined);
	}

	public LauncherOnMultipleFiles(String property, Object[] args) {
		super(property, args);
	}

	@Override
	public boolean canLaunch(Object resolvedResource) {
		return resolvedResource instanceof File[];
	}

	@Override
	public final boolean launch(View view, Object resource) {
		File files[] = LauncherUtils.resolveToFileArray(resource);
		if (files == null)
			return false;
		return launchWith(view, files);
	}
	
	public abstract boolean launchWith(View view, File[] files);

	@Override
	public boolean noRecord() {
		return true;
	}

	@Override
	public String getCode(View view, Object resolvedResource) {
		return null;
	}

}
