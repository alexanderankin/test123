package activator;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.msg.PluginUpdate;

/**
 * Core class of Activator plugin
 *
 * @author     mace
 * @version    $Revision$ $Date$ by $Author$
 */
public class ActivatorPlugin extends EBPlugin {
	
	public final static String NAME = "activator";
	public final static String RELOADER = "activator.reloader";
	public final static String OPTION_PREFIX = "options.activator.";
	public final static String PROPERTY_PREFIX = "plugin.activator.";
	
	public void handleMessage(EBMessage msg) {
		if (msg instanceof PluginUpdate) {
			PluginList.getInstance().update();
		}
	}
	
	public void addNotify() {
		EditBus.addToBus(this);
	}
	
	public void removeNotify() {
		EditBus.removeFromBus(this);
	}
}

