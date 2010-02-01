package launcher.browser;

import java.util.ArrayList;

import launcher.Launcher;
import launcher.LauncherPlugin;
import launcher.LauncherType;
import launcher.LauncherUtils;
import launcher.ORCompositeLauncher;
import launcher.sysapp.SystemApplicationOrBrowserLauncher;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;

public class BrowserLauncherType extends LauncherType {
	
	public final static String SERVICE_NAME = BrowserLauncherType.class.getName();
	
	public final String PROP_CASCADING = getPropertyPrefix() + 
	                      ".cascadingSystemBrowserLauncher";
	private Launcher cascadingSystemBrowserLauncher;
	public final String CASCADING_SHORT_LABEL =
		jEdit.getProperty(PROP_CASCADING + SHORT_LABEL_SUFFIX);

	public static final BrowserLauncherType INSTANCE = new BrowserLauncherType();

	private BrowserLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
		PluginJAR pluginJAR = jEdit.getPlugin(LauncherPlugin.class.getName()).getPluginJAR();
		// Register browser launchers. Most cannot be declared in
		// services.xml because their availability depend on the runtime platform
		ArrayList<Launcher> systemBrowserLaunchers = new ArrayList<Launcher>();
		if (SystemApplicationOrBrowserLauncher.isAvailable()) {
			ServiceManager.registerService(BrowserLauncherType.SERVICE_NAME,
					SystemApplicationOrBrowserLauncher.INSTANCE.getName(),
					SystemApplicationOrBrowserLauncher.class.getName() + ".INSTANCE;",
					pluginJAR);
			systemBrowserLaunchers.add(SystemApplicationOrBrowserLauncher.INSTANCE);
		}
		if (JavaDesktopBrowserLauncher.isAvailable()) {
			ServiceManager.registerService(BrowserLauncherType.SERVICE_NAME,
					JavaDesktopBrowserLauncher.INSTANCE.getName(),
					JavaDesktopBrowserLauncher.class.getName() + ".INSTANCE;",
					pluginJAR);
			systemBrowserLaunchers.add(JavaDesktopBrowserLauncher.INSTANCE);
		}
		
		// The system browser launcher is a composite of the above launchers:
		// it launches each launcher above in their order and stops at
		// the first successful launch
		if (systemBrowserLaunchers.size() > 0) {
			cascadingSystemBrowserLauncher = new ORCompositeLauncher(
					PROP_CASCADING, CASCADING_SHORT_LABEL,
					systemBrowserLaunchers.toArray(new Launcher[systemBrowserLaunchers.size()]));
			ServiceManager.registerService(SERVICE_NAME,
				cascadingSystemBrowserLauncher.getName(),
				BrowserLauncherType.class.getName() + ".INSTANCE.getCascadingSystemBrowserLauncher();",
				pluginJAR);
		}

	}

	@Override
	public Object resolve(Object resource) {
		return LauncherUtils.resolveToURI(resource);
	}

	public Launcher getCascadingSystemBrowserLauncher() {
		return cascadingSystemBrowserLauncher;
	}

	@Override
	public OptionPane getOptionPane() {
		return new BrowserLauncherTypeOptionPane();
	}

	@Override
	public Launcher getDefaultLauncher() {
		Launcher defaultLauncher = super.getDefaultLauncher();
		if (defaultLauncher == null) {
			defaultLauncher = getCascadingSystemBrowserLauncher();
			setDefaultLauncher(defaultLauncher);
		}
		return defaultLauncher;
	}

}
