package activator;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

/**
 * Core class of Activator plugin
 *
 * @author     mace
 * @version    $Revision$ $Date$ by $Author$
 */
public class ActivatorPlugin extends EBPlugin {
	public final static String NAME = "activator";
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

