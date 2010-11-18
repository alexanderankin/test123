package perl;

import java.util.Vector;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.View;

public class Debugger
{
	public static void selectLaunchConfig(View view)
	{
		LaunchConfigManager mgr = LaunchConfigManager.getInstance();
		Vector<LaunchConfig> configs = mgr.get();
		LaunchConfig sel = (LaunchConfig) JOptionPane.showInputDialog(
			view, "Launch Configuration", "Select:",
			JOptionPane.QUESTION_MESSAGE, null, configs.toArray(),
			mgr.getDefault());
		if (sel == null)
			return;
		mgr.setDefault(sel);
		mgr.save();
		go();
	}
	public static void go()
	{
	}
}
