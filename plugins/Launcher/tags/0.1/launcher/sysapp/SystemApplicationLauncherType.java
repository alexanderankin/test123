package launcher.sysapp;

import java.util.ArrayList;

import launcher.Launcher;
import launcher.LauncherPlugin;
import launcher.LauncherType;
import launcher.LauncherUtils;
import launcher.ORCompositeLauncher;
import launcher.exec.ExecutableFileLauncher;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;

public class SystemApplicationLauncherType extends LauncherType {
	
	public static final String SERVICE_NAME =
		SystemApplicationLauncherType.class.getName();
	
	public final String PROP_CASCADING = getPropertyPrefix() + 
                          ".cascadingSystemApplicationLauncher";
	private Launcher cascadingSystemApplicationLauncher;
	public final String CASCADING_SHORT_LABEL =
		jEdit.getProperty(PROP_CASCADING + SHORT_LABEL_SUFFIX);

	public static final SystemApplicationLauncherType INSTANCE = new SystemApplicationLauncherType();
	
	
	private SystemApplicationLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
		PluginJAR pluginJAR = jEdit.getPlugin(LauncherPlugin.class.getName()).getPluginJAR();
		// Register system applicationPath launchers. Most cannot be declared in
		// services.xml because their availability depend on the runtime platform
		ArrayList<Launcher> systemLaunchers = new ArrayList<Launcher>();
		if (SystemApplicationOrBrowserLauncher.isAvailable()) {
			ServiceManager.registerService(SERVICE_NAME,
					SystemApplicationOrBrowserLauncher.INSTANCE.getName(),
					SystemApplicationOrBrowserLauncher.class.getName() + ".INSTANCE;",
					pluginJAR);
			systemLaunchers.add(SystemApplicationOrBrowserLauncher.INSTANCE);
		}
		if (SystemApplicationJavaDesktopLauncher.isAvailable()) {
			ServiceManager.registerService(SERVICE_NAME,
					SystemApplicationJavaDesktopLauncher.INSTANCE.getName(),
					SystemApplicationJavaDesktopLauncher.class.getName() + ".INSTANCE;",
					pluginJAR);
			systemLaunchers.add(SystemApplicationJavaDesktopLauncher.INSTANCE);
		}
		ServiceManager.registerService(SERVICE_NAME,
				ExecutableFileLauncher.INSTANCE.getName(),
				ExecutableFileLauncher.class.getName() + ".INSTANCE;",
				pluginJAR);
		systemLaunchers.add(ExecutableFileLauncher.INSTANCE);
		
		// The cascading system applicationPath launcher is a composite of the 
		// other system applicationPath launchers: it launches each one
		// and stops at the first successful launch
		cascadingSystemApplicationLauncher = new ORCompositeLauncher(
				PROP_CASCADING, CASCADING_SHORT_LABEL,
				systemLaunchers.toArray(new Launcher[systemLaunchers.size()]));
		ServiceManager.registerService(SERVICE_NAME,
				cascadingSystemApplicationLauncher.getName(),
				SystemApplicationLauncherType.class.getName() +
					".INSTANCE.getCascadingSystemApplicationLauncher();",
				pluginJAR);
	}

	public Launcher getCascadingSystemApplicationLauncher() {
		return cascadingSystemApplicationLauncher;
	}

	@Override
	public Object resolve(Object resource) {
		return LauncherUtils.resolveToFileOrURIOrString(resource);
	}

	@Override
	public OptionPane getOptionPane() {
		return new SystemApplicationLauncherTypeOptionPane();
	}

	@Override
	public Launcher getDefaultLauncher() {
		Launcher defaultLauncher = super.getDefaultLauncher();
		if (defaultLauncher == null) {
			defaultLauncher = getCascadingSystemApplicationLauncher();
			setDefaultLauncher(defaultLauncher);
		}
		return defaultLauncher;
	}

}
