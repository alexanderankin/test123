package activator;

import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.jEdit;

import javax.swing.AbstractAction;

public class Load extends AbstractAction
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
