package automation;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Macros.Handler;
import org.gjt.sp.jedit.Macros.Macro;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;

public class ConsoleAutomationPlugin extends EditPlugin {

	private static final String GLOBAL_MACROS = "Global";
	private static final String CONNECTION_DOCKABLE = "console-automation";
	private ConnectionDockable dockable;
	private HashMap<String, Connection> connections = new HashMap<String, Connection>();

	public void start()
	{
	}

	public void reloadConnections() {
		String configured = jEdit.getProperty("console.automation.connections");
		if (configured != null)
		{
			String [] connectionStrings = configured.split("\\s+");
			for (String connectionString: connectionStrings)
				addConnection(connectionString);
		}
	}

	public void stop()
	{
	}

	public Connection getConnection(String name)
	{
		return connections.get(name);
	}

	private File getMacroFile(String key, String name)
	{
		return new File(getPluginHome().getAbsoluteFile() + File.separator +
			key + File.separator + name);
	}
	public void editMacro(String key, String name)
	{
		File f = getMacroFile(key, name);
		if (! f.exists())
			return;
		jEdit.openFile(jEdit.getActiveView(), f.getAbsolutePath());
	}
	public void runMacro(final String key, String name)
	{
		File f = getMacroFile(key, name);
		if (! f.exists())
			return;
		String path = f.getAbsolutePath();
		Handler handler = Macros.getHandlerForPathName(path);
		if (handler != null)
		{
			try
			{
				final Macro macro = handler.createMacro(
					MiscUtilities.getFileName(path), path);
				Thread t = new Thread(new Runnable() {
					public void run()
					{
						Connection c = getConnection(key);
						if (c != null)
							c.abortScript();
						macro.invoke(jEdit.getActiveView());
					}
				});
				t.start();
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, Macros.class, e);
				return;
			}
		}
	}
	public void showMessage(final String message)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				JOptionPane.showMessageDialog(null, message);
			}
		});
	}
	public HashMap<String, Vector<String>> getMacros()
	{
		File home = getPluginHome();
		if (! home.exists())
			return null;
		String [] items = home.list();
		HashMap<String, Vector<String>> macros = new HashMap<String, Vector<String>>();
		for (String item: items)
		{
			File file = new File(home.getAbsoluteFile() + File.separator + item);
			if (file.isDirectory())
			{
				Vector<String> children = macros.get(item);
				if (children == null)
				{
					children = new Vector<String>();
					macros.put(item, children);
				}
				String [] grandChildren = file.list();
				for (String grandChild: grandChildren)
				{
					if (grandChild.endsWith(".bsh"))
						children.add(grandChild);
				}
			}
			else if (file.getName().endsWith(".bsh"))
			{
				Vector<String> children = macros.get(GLOBAL_MACROS);
				if (children == null)
				{
					children = new Vector<String>();
					macros.put(GLOBAL_MACROS, children);
				}
				children.add(item);
			}
		}
		return macros;
	}

	public void showConnectionDialog()
	{
		String s;
		do
		{
			s = JOptionPane.showInputDialog("Please enter name:host:port");
			if (s == null)
				return;
		}
		while (addConnection(s) == false);
	}
	private boolean addConnection(String connectionString)
	{
		String [] parts = connectionString.split(":");
		if (parts.length != 3)
			return false;
		String name = parts[0];
		String host = parts[1];
		String port = parts[2];
		connect(name, host, Integer.valueOf(port));
		return true;
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

