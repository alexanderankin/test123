package automation;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class ConsoleAutomationPlugin extends EditPlugin {

	private static final String CONNECTION_DOCKABLE = "console-automation";
	private ConnectionDockable dockable;

	public void start()
	{
	}

	public void stop()
	{
	}

	public void showConnectionDialog()
	{
		String s = JOptionPane.showInputDialog("Please enter host:port - ");
		if (s == null)
			return;
		int sep = s.lastIndexOf(":");
		String host = s.substring(0, sep);
		String port = s.substring(sep + 1);
		connect(host, Integer.valueOf(port));
	}
	public Connection connect(String host, int port)
	{
		Connection c = new Connection(host, port);
		try
		{
			c.connect();
			DockableWindowManager dwm = jEdit.getActiveView().getDockableWindowManager();
			dockable = (ConnectionDockable)
				dwm.getDockable(CONNECTION_DOCKABLE); 
			if (dockable == null)
			{
				dwm.addDockableWindow(CONNECTION_DOCKABLE);
				dockable = (ConnectionDockable)
					dwm.getDockable(CONNECTION_DOCKABLE); 
			}
			dockable.add(c);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return c;
	}
	
}

