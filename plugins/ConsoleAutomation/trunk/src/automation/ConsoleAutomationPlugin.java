package automation;

import java.util.HashMap;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class ConsoleAutomationPlugin extends EditPlugin {

	private static final String CONNECTION_DOCKABLE = "console-automation";
	private ConnectionDockable dockable;
	private HashMap<String, Connection> connections = new HashMap<String, Connection>();

	public void start()
	{
	}

	public void stop()
	{
	}

	public Connection getConnection(String name)
	{
		return connections.get(name);
	}

	public void showConnectionDialog()
	{
		String[] parts;
		do
		{
			String s = JOptionPane.showInputDialog("Please enter name:host:port");
			if (s == null)
				return;
			parts = s.split(":");
		}
		while (parts.length != 3);
		String name = parts[0];
		String host = parts[1];
		String port = parts[2];
		connect(name, host, Integer.valueOf(port));
	}
	public Connection connect(String name, String host, int port)
	{
		Connection c = new Connection(name, host, port);
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
			connections.put(name, c);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return c;
	}
	
}

