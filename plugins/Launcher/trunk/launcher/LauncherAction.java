package launcher;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;

public class LauncherAction extends EditAction {
	
	protected LaunchConfiguration launchConfiguration;
	
	public LauncherAction(String labelProperty, LaunchConfiguration config) {
		super(labelProperty, new Object[]{config.getLauncher().getShortLabel()});
		this.launchConfiguration = config;
	}

	@Override
	public void invoke(View view) {
		launchConfiguration.launch(view);
	}

	public LaunchConfiguration getLaunchConfiguration() {
		return launchConfiguration;
	}

}
