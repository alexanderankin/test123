package activator;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

class Reload extends AbstractAction
{
	private Plugin plugin;
	private ReloadPanel parent;

	public Reload(ReloadPanel parent, Plugin plugin, String dispName)
	{
		super(dispName);
		this.parent = parent;
		this.plugin = plugin;
	}

	public void actionPerformed(ActionEvent event)
	{
	    parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		PluginManager.getInstance().reload(plugin);
		parent.setCursor(Cursor.getDefaultCursor());
	}
}
