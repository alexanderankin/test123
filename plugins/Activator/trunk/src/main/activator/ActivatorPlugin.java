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

    public static final String NAME = "activator";
    public static final String RELOADER = "activator.reloader";
    public static final String OPTION_PREFIX = "options.activator.";
    public static final String PROPERTY_PREFIX = "plugin.activator.";

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof PluginUpdate ) {
            PluginUpdate pu = (PluginUpdate)msg;
            if (PluginUpdate.REMOVED.equals(pu.getWhat())) {
                PluginList.getInstance().removePlugin(pu.getFile());   
            } else {
                PluginList.getInstance().update();
            }
        }
    }

    public void addNotify() {
        EditBus.addToBus( this );
    }

    public void removeNotify() {
        EditBus.removeFromBus( this );
    }

    public void stop() {
        PluginList.getInstance().clear();
    }
}

