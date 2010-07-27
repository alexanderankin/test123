package automation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
	private static final String CONNECTION_FILE = "connections.txt";
	private static ConsoleAutomationPlugin instance = null;
	private final Map<String, Connection> connections =
		new HashMap<String, Connection>();
	private Vector<String> presetConnections = new Vector<String>();

	public void start()
	{
		instance = this;
		loadPresetConnections();
	}

	private static String getConnectionFile()
	{
		return System.getProperty("user.home") + File.separator + CONNECTION_FILE;
	}
	private void loadPresetConnections()
	{
		String connectionFile = getConnectionFile();
		File f = new File(connectionFile);
		if (! f.exists())
			return;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(connectionFile));
			String line;
			while ((line = br.readLine()) != null)
				presetConnections.add(line);
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void reloadConnections(boolean headless)
	{
		String configured = jEdit.getProperty("console.automation.connections");
		if (configured != null)
		{
			String [] connectionStrings = configured.split("\\s+");
			for (String connectionString: connectionStrings)
				addConnection(connectionString, headless);
		}
	}

	public void stop()
	{
		instance = null;
	}

	static public ConsoleAutomationPlugin getInstance()
	{
		return instance;
	}

	public Connection getConnection(String name)
	{
		synchronized(connections)
		{
			return connections.get(name);
		}
	}

	private File getMacroFile(String key, String name)
	{
		StringBuilder s = new StringBuilder(getPluginHome().getAbsolutePath() +
			File.separator);
		if (! key.equals(GLOBAL_MACROS))
			s.append(key + File.separator);
		s.append(name);
		return new File(s.toString());
	}
	public void editMacro(String key, String name)
	{
		File f = getMacroFile(key, name);
		if (! f.exists())
			return;
		jEdit.openFile(jEdit.getActiveView(), f.getAbsolutePath());
	}
	public Connection getCurrentConnection()
	{
		return Connection.getCurrentConnection();
	}
	private Connection getCurrentConnectionInDockable()
	{
		ConnectionDockable dockable = getConnectionDockable();
		if (dockable == null)
			return null;
		return dockable.getCurrent();
		
	}
	private Object[] getConnectionNames()
	{
		return connections.keySet().toArray();
	}
	public Connection selectConnectionDialog()
	{
		synchronized(connections)
		{
			Object[] names = getConnectionNames();
			if ((names == null) || (names.length == 0))
				return null;
			Object connectionName = JOptionPane.showInputDialog(null,
				"Select connection to run this macro on:",
				"Select Connection", JOptionPane.QUESTION_MESSAGE,
				null, names, names[0]);
			if (connectionName == null)
				return null;
			return connections.get(connectionName);
		}
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
				Runnable script = new Runnable() {
					public void run()
					{
						// Run without an associated view, to avoid the
						// compound edit around the macro invocation.
						macro.invoke(null);
					}
				};
				// "Global" macros are not associated with the current connection,
				// e.g. macros that create the connection to work on.
				if (! key.equals("Global"))
				{
					Connection c = getCurrentConnectionInDockable();
					if (c == null)
						c = selectConnectionDialog();
					if (c == null)
						return;
					c.abortScript();
					c.addScript(script);
				}
				else
					new Thread(script).start();
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
		try
		{
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run()
				{
					JOptionPane.showMessageDialog(null, message);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public Map<String, Vector<String>> getMacros()
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

	// Close the connection that is currently visible in the dockable
	public void closeCurrentConnection()
	{
		Connection c = getCurrentConnectionInDockable();
		if (c == null)
		{
			JOptionPane.showMessageDialog(null, "No current connection to close");
			return;
		}
		closeConnection(c);
	}
	// Show a "close connection" dialog
	public void closeConnection()
	{
		Object [] names;
		synchronized(connections)
		{
			names = getConnectionNames();
		}
		if ((names == null) || (names.length == 0))
			return;
		Arrays.sort(names);
		String sel = (String) JOptionPane.showInputDialog(null,
			"Select connection to close:", "Close Connection",
			JOptionPane.PLAIN_MESSAGE, null, names, null);
		if (sel == null)
			return;
		synchronized(connections)
		{
			closeConnection(connections.get(sel));
		}
	}
	public void showSelectConnectionDialog(boolean headless)
	{
		if (presetConnections.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "No preset connections exist");
			return;
		}
		Object sel = JOptionPane.showInputDialog(null, "Select connection",
			"Connection Selection", JOptionPane.QUESTION_MESSAGE, null,
			presetConnections.toArray(), presetConnections.get(0));
		if (sel == null)
			return;
		addConnection((String)sel, headless);
	}
	public void presetCurrent()
	{
		Connection c = getCurrentConnectionInDockable();
		if (c == null)
		{
			JOptionPane.showMessageDialog(null, "No current connection to preset");
			return;
		}
		String connectionFile = getConnectionFile();
		File f = new File(connectionFile);
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			String line = c.getName() + ":" + c.getHost() + ":" + c.getPort();
			bw.write(line + "\n");
			bw.close();
			presetConnections.add(line);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public void showConnectionDialog(boolean headless)
	{
		String s;
		do
		{
			s = JOptionPane.showInputDialog("Please enter name:host:port");
			if (s == null)
				return;
		}
		while (! addConnection(s, headless));
	}
	private boolean addConnection(String connectionString, boolean headless)
	{
		String [] parts = connectionString.split(":");
		if (parts.length != 3)
			return false;
		String name = parts[0];
		String host = parts[1];
		String port = parts[2];
		connect(name, host, Integer.valueOf(port), headless);
		return true;
	}
	public Connection getConnection(String name, String host, int port,
		boolean headless)
	{
		Connection c = getConnection(name);
		if (c == null)
			c = connect(name, host, port, headless);
		return c;
	}
	public void closeConnection(Connection c)
	{
		try
		{
			c.disconnect();
			ConnectionDockable dockable = getConnectionDockable();
			dockable.remove(c);
			synchronized(connections)
			{
				connections.remove(c.getName());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public Connection connect(String name, String host, int port, boolean headless)
	{
		Connection c = new Connection(name, host, port);
		try
		{
			c.connect();
			if (! headless)
			{
				ConnectionDockable dockable = getConnectionDockable();
				dockable.add(c);
			}
			synchronized(connections)
			{
				connections.put(name, c);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return c;
	}
	private ConnectionDockable getConnectionDockable()
	{
		DockableWindowManager dwm = jEdit.getActiveView().getDockableWindowManager();
		ConnectionDockable dockable;
		dockable = (ConnectionDockable) dwm.getDockable(CONNECTION_DOCKABLE); 
		if (dockable == null)
		{
			dwm.addDockableWindow(CONNECTION_DOCKABLE);
			dockable = (ConnectionDockable) dwm.getDockable(CONNECTION_DOCKABLE); 
		}
		return dockable;
	}
	
}

