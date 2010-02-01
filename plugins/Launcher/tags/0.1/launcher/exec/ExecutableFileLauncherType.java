package launcher.exec;

import launcher.Launcher;
import launcher.LauncherPlugin;
import launcher.LauncherType;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;

public class ExecutableFileLauncherType extends LauncherType {
	
	public static final String SERVICE_NAME = ExecutableFileLauncherType.class.getName();
	
	public static final ExecutableFileLauncherType INSTANCE = new ExecutableFileLauncherType();

	private ExecutableFileLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
		PluginJAR pluginJAR = jEdit.getPlugin(LauncherPlugin.class.getName()).getPluginJAR();
		ServiceManager.registerService(SERVICE_NAME,
				ExecutableFileLauncher.INSTANCE.getName(),
				ExecutableFileLauncher.class.getName() + ".INSTANCE;",
				pluginJAR);
	}

	@Override
	public Object resolve(Object resource) {
		return LauncherUtils.resolveToFile(resource);
	}

	@Override
	public OptionPane getOptionPane() {
		return new ExecutableFileLauncherTypeOptionPane();
	}

	@Override
	public Launcher getDefaultLauncher() {
		Launcher defaultLauncher = super.getDefaultLauncher();
		if (defaultLauncher == null) {
			defaultLauncher = ExecutableFileLauncher.INSTANCE;
			setDefaultLauncher(defaultLauncher);
		}
		return defaultLauncher;
	}

}
