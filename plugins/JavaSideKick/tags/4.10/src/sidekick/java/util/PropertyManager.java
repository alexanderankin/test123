
package sidekick.java.util;


import java.util.*;

import org.gjt.sp.jedit.JARClassLoader;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.jEdit;


/**
 * This PropertyManager loads a copy of the plugin properties, and checks if the
 * properties have changed. This allows the JavaParser to skip sending a
 * SideKickUpdate message if the properties have not changed. All plugins should
 * have a class like this, since it reduces a lot of EditBus traffic.
 */
public class PropertyManager {

    // singleton instance
    private static PropertyManager instance = null;
    // original properties
    private static Properties properties = null;

    private PropertyManager() {
    }

    public static PropertyManager getInstance() {
        if ( instance == null ) {
            instance = new PropertyManager();
            instance.load();
        }
        return instance;
    }

    // load the plugin properties from the properties file in the plugin jar
    private static void load() {
        if ( properties == null ) {
            properties = new Properties();
            try {
                PluginJAR jar = jEdit.getPlugin( "sidekick.java.JavaSideKickPlugin" ).getPluginJAR();
                if ( jar != null ) {
                    JARClassLoader loader = jar.getClassLoader();
                    if ( loader != null ) {
                        properties.load( loader.getResourceAsStream( "JavaSideKick.props" ) );
                        properties.list( System.out );
                    }
                }
            }
            catch ( Exception e ) {
                properties = null;
            }
        }
    }

    /**
     * Checks the initial values of the plugin properties against the current values
     * stored by jEdit. If any of the values have changed since last check, this
     * class updates its internal store with those new values and returns <code>true</code>.
     * @return true if the properties are different than last time they were checked.
     */
    public static boolean hasChanged() {
        if ( properties == null ) {
            load();
        }
        if ( properties == null ) {
            return false;
        }

        // go through the stored properties, check all the values against those
        // stored by jEdit, update property values to new values as necessary
        boolean toReturn = false;
        Set keys = new HashSet( properties.keySet() );
        for ( Object key : keys ) {
            String oldValue = properties.getProperty( key.toString() );
            String newValue = jEdit.getProperty( key.toString() );
            if ( oldValue.compareTo( newValue ) != 0 ) {
                toReturn = true;
                properties.setProperty( key.toString(), newValue );
            }
        }
        return toReturn;
    }
}
