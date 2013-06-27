package activator;

import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.jEdit;

import common.gui.actions.CustomAction;

public class Load extends CustomAction
{
	Plugin plugin;

	public Load(Plugin plug) {
		super(plug.toString());
		plugin = plug;
	}

	public void actionPerformed(ActionEvent e)
	{
		StopWatch sw = new StopWatch();
		sw.start();
		PluginManager.getInstance().load(plugin);
		sw.stop();
		jEdit.getActiveView().getStatus().setMessage( plugin + " " + jEdit.getProperty("activator.loaded_in", "loaded in") + " " + sw);
	}
}
