package automation;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

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
	private void runInEDT(Runnable r)
	{
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	public void add(final Connection c)
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				ConnectionWindow cw = new ConnectionWindow(c);
				tabs.addTab(c.getName(), cw);
			}
		};
		runInEDT(r);
	}
	public void remove(final Connection c)
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				tabs.removeTabAt(tabs.indexOfTab(c.getName()));
			}
		};
		runInEDT(r);
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
