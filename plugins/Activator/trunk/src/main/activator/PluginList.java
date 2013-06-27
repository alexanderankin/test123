package activator;

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

/**
 * Data model containing information about plugins. This is a singleton class.
 */
public class PluginList extends Observable {

    // TODO: figure out where these are used and make sure they still work.
    public static final String LOADED = jEdit.getProperty( "activator.Loaded", "Loaded" );
    public static final String ERROR = jEdit.getProperty( "activator.Error", "Error" );
    public static final String ACTIVATED = jEdit.getProperty( "activator.Activated", "Activated" );
    public static final String NOT_LOADED = jEdit.getProperty( "activator.Not_Loaded", "Not Loaded" );

    private static PluginList instance;
    private static final List<Plugin> plugins = new ArrayList<Plugin>();

    private static final HashMap<String, Plugin> pluginMap = new HashMap<String, Plugin>();

    private PluginList() {
    }

    public static PluginList getInstance() {
        if ( instance == null ) {
            instance = new PluginList();
        }
        return instance;
    }

    public void addPlugin( PluginJAR jar ) {
        Plugin plugin = new Plugin( jar );
        if ( "Activator.jar".equals( plugin.getFile().getName() ) ) {
            return;
        }
        Plugin old = pluginMap.get( jar.getPath() );
        if ( old != null ) {
            old.copyTo( plugin );
        }
        plugins.add( plugin );
        pluginMap.put( plugin.getFile().getAbsolutePath(), plugin );
    }

    public void addPlugin( File file ) {
        // don't add the jar for Activator, it really doesn't work well to try
        // to make it reload itself
        if ( "Activator.jar".equals( file.getName() ) ) {
            return;
        }
        Plugin plugin = new Plugin( file );
        Plugin old = pluginMap.get( file.getAbsolutePath() );
        if ( old != null ) {
            old.copyTo( plugin );
        }
        plugins.add( plugin );
        pluginMap.put( file.getAbsolutePath(), plugin );
    }

    public void update() {
        clear();
        for ( PluginJAR pj : jEdit.getPluginJARs() ) {
            addPlugin( pj );
        }
        for ( String file : jEdit.getNotLoadedPluginJARs() ) {
            addPlugin( new File( file ) );
            Log.log( Log.DEBUG, this, file );
        }
        Collections.sort( plugins, new PluginComparator() );
        setChanged();
        notifyObservers();
    }

    public void clear() {
        plugins.clear();
    }

    public Plugin get( int i ) {
        try {
            return plugins.get( i );
        } catch ( IndexOutOfBoundsException iobe ) {
            return null;
        }
    }

    public Plugin get( String path ) {
        return pluginMap.get( path );
    }

    public int size() {
        return plugins.size();
    }

    /**
     * @return the count of just the plugins in this list, does not count
     * any library files.
     */
    public int pluginCount() {
        int count = 0;
        for ( Plugin plugin : plugins ) {
            if ( !plugin.isLibrary() ) {
                ++count;
            }
        }
        return count;
    }
}
