package automation;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class ConnectionDockable extends JPanel
{
	private JTabbedPane tabs;

	public ConnectionDockable()
	{
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		add(tabs);
	}
	public void add(Connection c)
	{
		ConnectionWindow cw = new ConnectionWindow(c);
		tabs.addTab("Connection " + tabs.getTabCount(), cw);
	}
}
