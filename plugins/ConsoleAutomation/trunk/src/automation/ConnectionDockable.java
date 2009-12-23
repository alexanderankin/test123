package automation;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class ConnectionDockable extends JPanel
{
	private final JTabbedPane tabs;

	public ConnectionDockable()
	{
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		tabs.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isMiddleMouseButton(e))
				{
					Connection c = getCurrent();
					ConsoleAutomationPlugin.getInstance().closeConnection(c);
				}
			}
		});
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
