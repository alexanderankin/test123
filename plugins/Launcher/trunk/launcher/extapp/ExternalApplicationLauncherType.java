package launcher.extapp;

import java.io.IOException;

import launcher.Launcher;
import launcher.LauncherPlugin;
import launcher.LauncherType;
import launcher.LauncherUtils;
import launcher.sysapp.SystemApplicationLauncherType;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public class ExternalApplicationLauncherType extends LauncherType {
	
	public static final String SERVICE_NAME = ExternalApplicationLauncherType.class.getName();
	public final String OPT_USE_DEFAULT = OPT_BASE_PREFIX + getPropertyPrefix() + ".use-default";
	
	private boolean useDefaultLauncher = 
		jEdit.getBooleanProperty(OPT_USE_DEFAULT, false);
	
	public static final ExternalApplicationLauncherType INSTANCE = new ExternalApplicationLauncherType();
	
	private ExternalApplicationLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
		PluginJAR pluginJAR = jEdit.getPlugin(LauncherPlugin.class.getName()).getPluginJAR();
		ServiceManager.registerService(
			ExternalApplicationLauncherType.SERVICE_NAME,
			ChooseExternalApplicationLauncher.INSTANCE.getName(),
			ChooseExternalApplicationLauncher.class.getName() + ".INSTANCE;",
			pluginJAR);
		try {
			ExternalApplicationLaunchers.getInstance().loadExts();
		} catch (IOException e) {
			Log.log(Log.ERROR, this, LauncherPlugin.ERR_EXCEPTION, e);
		}
	}

	@Override
	public Object resolve(Object resource) {
		return LauncherUtils.resolveToFile(resource);
	}

	@Override
	public OptionPane getOptionPane() {
		return new ExternalApplicationLauncherTypeOptionPane();
	}

	public boolean useDefaultLauncher() {
		return useDefaultLauncher;
	}

	public void setUseDefaultLauncher(boolean useDefault) {
		useDefaultLauncher = useDefault;
		jEdit.setBooleanProperty(OPT_USE_DEFAULT, useDefault);
	}

	public Launcher getDefaultLauncher() {
		Launcher defaultLauncher = super.getDefaultLauncher();
		if (defaultLauncher == null) {
			// We're probably using a launcher from SystemApplicationLauncherType
			// which is why LauncherType could not retrieve it from ServiceManager
			// because it searches launchers of this type
			String chooseLauncherName = ChooseExternalApplicationLauncher.INSTANCE.getName();
			String defaultLauncherName =  jEdit.getProperty(
					getDefaultLauncherPropertyName());
			if (defaultLauncherName == null || defaultLauncherName.isEmpty()) {
				// keep defaultLauncher=null to make sure it's initialized later on 
			} else if (defaultLauncherName.equals(chooseLauncherName))
				defaultLauncher = ChooseExternalApplicationLauncher.INSTANCE;
			else
				defaultLauncher = (Launcher)ServiceManager.getService(
					SystemApplicationLauncherType.SERVICE_NAME, defaultLauncherName);
		}
		if (defaultLauncher == null) {
			// Default not found, make sure there's one
			defaultLauncher = ChooseExternalApplicationLauncher.INSTANCE;
			setDefaultLauncher(defaultLauncher);
		}		
		return defaultLauncher;
	}

	@Override
	public Launcher[] getDefaultLaunchersChoice() {
		Launcher[] sysLaunchers = SystemApplicationLauncherType.INSTANCE.getLaunchers();
		Launcher[] defaultLaunchers = new Launcher[sysLaunchers.length + 1]; 
		System.arraycopy(sysLaunchers, 0, defaultLaunchers, 0, sysLaunchers.length);
		defaultLaunchers[sysLaunchers.length] = 
				ChooseExternalApplicationLauncher.INSTANCE;
		return defaultLaunchers;
	}

}
