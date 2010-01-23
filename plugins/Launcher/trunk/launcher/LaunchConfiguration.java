package launcher;

import java.lang.ref.WeakReference;

import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;

public class LaunchConfiguration {
	
	protected final transient WeakReference<Launcher> weakLauncher;
	protected final transient WeakReference<LauncherType> weakLauncherType;
	protected final String launcherName;
	protected final String launcherTypeName;
	protected final transient Object resource;
	protected final Object resolvedResource;
	
	public LaunchConfiguration(LauncherType launcherType, Launcher launcher, Object resource,
			Object resolvedResource) {
		super();
		this.weakLauncher = new WeakReference<Launcher>(launcher);
		this.weakLauncherType = new WeakReference<LauncherType>(launcherType);
		this.launcherName = launcher.getName();
		this.launcherTypeName = launcherType.getServiceName();
		this.resource = resource;
		this.resolvedResource = resolvedResource;
	}
	
	public LauncherType getLauncherType() {
		LauncherType launcherType = weakLauncherType.get();
		if (launcherType == null) {
			launcherType = (LauncherType)ServiceManager.getService(
					LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
					launcherTypeName);
		}
		return launcherType;
	}
	
	public Launcher getLauncher() {
		Launcher launcher = weakLauncher.get();
		if (launcher == null) {
			LauncherType launcherType = getLauncherType();
			if (launcherType != null) {
				launcher = launcherType.getLauncher(launcherName);
			} // else plugin must have been unloaded...
		}
		return launcher;
	}
	
	public Object getResource() {
		return resource;
	}
	public Object getResolvedResource() {
		return resolvedResource;
	}

	public boolean launch(View view) {
		Launcher launcher = getLauncher();
		if (launcher == null)
			return false;
		return launcher.launch(view, getResolvedResource());
	}
}
