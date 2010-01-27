package launcher.text.selected;

import launcher.Launcher;
import launcher.LauncherPlugin;
import launcher.LauncherType;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;

public class SelectedTextLauncherType extends LauncherType {
	
	public static final String SERVICE_NAME = SelectedTextLauncherType.class.getName();
	
	public static final SelectedTextLauncherType INSTANCE = new SelectedTextLauncherType();

	private SelectedTextLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
		PluginJAR pluginJAR = jEdit.getPlugin(LauncherPlugin.class.getName()).getPluginJAR();
		// The URL Launcher can browse selected URL
		ServiceManager.registerService(SERVICE_NAME,
				URLBrowserLauncher.INSTANCE.getName(),
				URLBrowserLauncher.class.getName() + ".INSTANCE;",
				pluginJAR);
		
	}

	@Override
	public Object resolve(Object resource) {
		return LauncherUtils.resolveToSelectedText(resource);
	}

	@Override
	public OptionPane getOptionPane() {
		return null;
	}

	@Override
	public Launcher getDefaultLauncher() {
		return null;
	}

}
