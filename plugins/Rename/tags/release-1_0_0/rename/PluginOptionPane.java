package rename;

import org.gjt.sp.jedit.*;

public class PluginOptionPane extends PropertyOptionPane {
	public PluginOptionPane() {
		super("Plugin","name");
		EditPlugin[] plugins = jEdit.getPlugins();
		idArray = new String[plugins.length];
		for (int i = 0; i < plugins.length; i++) {
			idArray[i] = "plugin."+plugins[i].getClassName();
		}
	}
}
