package perl;

import java.util.Vector;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class Debugger
{
	private static LaunchConfig selectConfig(View view)
	{
		LaunchConfigManager mgr = LaunchConfigManager.getInstance();
		Vector<LaunchConfig> configs = mgr.get();
		LaunchConfig sel = (LaunchConfig) JOptionPane.showInputDialog(
			view, "Launch Configuration", "Select:",
			JOptionPane.QUESTION_MESSAGE, null, configs.toArray(),
			mgr.getDefault());
		return sel;
	}
	public static void selectLaunchConfig(View view)
	{
		LaunchConfigManager mgr = LaunchConfigManager.getInstance();
		LaunchConfig config = selectConfig(view);
		if (config == null)
			return;
		mgr.setDefault(config);
		mgr.save();
		go(view);
	}
	public static void go(View view)
	{
		LaunchConfig config = LaunchConfigManager.getInstance().getDefault();
		if (config == null)
			config = selectConfig(view);
		if (config == null)
				return;
		PerlProcess p = new PerlProcess(config);
		DockableWindowManager dwm = view.getDockableWindowManager();
		dwm.showDockableWindow("perl-dbg-console");
		Console console = (Console) dwm.getDockableWindow("perl-dbg-console");
		console.openSession(p);
		p.startConsumingOutput();
	}
}
