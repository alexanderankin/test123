package slime;
//{{{ Imports
import console.Console;
import console.Shell;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PluginUpdate;
//}}}
public class SlimePlugin extends EditPlugin {
	
	public static final String[] SHELLS = new String[] {
		"Clojure", "Groovy", "Python"
	};
	
	public void start() {
		for (int i = 0; i<SHELLS.length; i++) {
			String prop = "options.slime.show-"+SHELLS[i].toLowerCase();
			if (!jEdit.getBooleanProperty(prop)) {
				removeShell(SHELLS[i]);
			}
		}
	}
	
	public void stop() {
		View[] views = jEdit.getViews();
		for (int i = 0; i<views.length; i++) {
			DockableWindowManager wm = views[i].getDockableWindowManager();
			Console console = (Console) wm.getDockable("console");
			if (console != null) {
				for (int j = 0; j<SHELLS.length; j++) {
					Shell shell = Shell.getShell(SHELLS[j]);
					if (shell != null) {
						shell.stop(console);
					}
				}
			}
		}
	}
	
	private void removeShell(String shell) {
		ServiceManager.unregisterService("console.Shell", shell);
		EditBus.send(new PluginUpdate(getPluginJAR(), PluginUpdate.LOADED,
			false));
	}
}
