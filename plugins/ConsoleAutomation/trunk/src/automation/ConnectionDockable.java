package automation;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class ConnectionDockable extends JPanel
{
	private final JTabbedPane tabs;

	public ConnectionDockable()
	{
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		add(tabs);
	}
	public void add(Connection c)
	{
		ConnectionWindow cw = new ConnectionWindow(c);
		tabs.addTab(c.getName(), cw);
	}
	public void remove(Connection c)
	{
		tabs.removeTabAt(tabs.indexOfTab(c.getName()));
	}
	public Connection getCurrent()
	{
		int sel = tabs.getSelectedIndex();
		if (sel >= 0)
		{
			ConnectionWindow cw = (ConnectionWindow) tabs.getComponentAt(sel);
			return cw.getConnection();
		}
		return null;
	}
}
