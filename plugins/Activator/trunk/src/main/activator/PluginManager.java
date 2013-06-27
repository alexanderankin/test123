package activator;

import java.io.File;
import java.util.*;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.jEdit;

/**
 * Loads, unloads, and reloads plugins. This is a singleton constructed using
 * the method devised by Bill Pugh of FindBugs fame.
 */
public class PluginManager {

    public static final int LOADED = 0;
    public static final int ACTIVATED = 1;
    public static final int ERROR = 2;
    public static final int NOT_LOADED = 3;
    public static final int LIBRARY = 4;

    private static class Singleton {
        public static final PluginManager INSTANCE = new PluginManager();
    }

    private PluginManager() { }

    public static PluginManager getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * @param plugin The plugin to load, only the file name is needed here.
     */
    public void load( Plugin plugin ) {
        if ( plugin == null ) {
            return;
        }
        load( plugin.getFile().getAbsolutePath() );
    }

    /**
     * @param pluginPath The local path to the plugin jar file.
     */
    public void load( String pluginPath ) {
        loadPluginJAR( pluginPath, true );
    }

    public void activate( Plugin plugin ) {
        if ( plugin.isActivated() ) {
            return;
        }
        if ( !plugin.isLoaded() ) {
            load( plugin );
        }
        plugin.getJAR().activatePlugin();
    }

    public void deactivate( final Plugin plugin ) {
        if ( plugin.isActivated() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    plugin.getJAR().deactivatePlugin( false );
                }
            } );
        }
    }

    /**
     * unload
     * - unload selected plugin
     * - unload all dependent plugins
     * - load optionally dependent plugins
     * - activate previously active optionally dependent plugins
     */
    public Deque<UnloadedStatus> unload( Plugin plugin ) {
        return unloadPlugin( plugin );
    }

    /**
     * reload
     * - unload selected plugin
     * - unload all dependent plugins
     * - load selected plugin
     * - activate selected plugin if previously active
     * - reload optionally dependent plugins
     * - activate previously active optionally dependent plugins
     * - reload previously loaded dependent plugins
     * - activate previously active dependent plugins
     */
    public void reload( Plugin plugin ) {
        Deque<UnloadedStatus> unloaded = unload( plugin );
        if ( unloaded.isEmpty() ) {
            return;
        }
        Set<String> reloaded = new HashSet<String>();
        UnloadedStatus status = null;
        do {
            status = unloaded.pop();
            String path = status.getPath();
            if ( path != null && !reloaded.contains( path ) ) {
                PluginJAR jar = loadPluginJAR( path, false );
                reloaded.add( path );
                if (status.getActivated()) {
                    jar.activatePlugin();
                }
            }
        } while ( !unloaded.isEmpty() );
    }

    // {{{ loadPluginJAR()
    /**
     * load
     * - load selected plugin
     * - reload optionally dependent plugins
     * - activate previously active optionally dependent plugins
     * - ask to load dependent plugins
     */
    private PluginJAR loadPluginJAR( String jarPath, boolean askForDependents ) {
        jEdit.addPluginJAR( jarPath );
        PluginJAR jar = jEdit.getPluginJAR( jarPath );
        if ( jar == null || jar.getPlugin() == null ) {
            return null;
        }

        String className = jar.getPlugin().getClassName();
        String jars = jEdit.getProperty( "plugin." + className + ".jars" );

        if ( jars != null ) {
            String dir = MiscUtilities.getParentOfPath( jarPath );

            StringTokenizer st = new StringTokenizer( jars );
            while ( st.hasMoreTokens() ) {
                String _jarPath = MiscUtilities.constructPath( dir, st.nextToken() );
                PluginJAR _jar = jEdit.getPluginJAR( _jarPath );
                if ( _jar == null ) {
                    jEdit.addPluginJAR( _jarPath );
                }
            }
        }

        if ( jar.checkDependencies() ) {
            jar.activatePluginIfNecessary();
        }

        reloadOptionallyDependentPlugins( jar, className );
        if ( askForDependents ) {
            askToLoadDependents( jar );
        }
        return jar;
    }

    private void askToLoadDependents( PluginJAR jar ) {
        PluginList pl = PluginList.getInstance();
        Plugin plugin = pl.get( jar.getPath() );
        if ( plugin == null ) {
            return;
        }
        Set<String> dependents = plugin.getAllDependentPlugins();
        Set<Plugin> potentials = new HashSet<Plugin>();
        for ( String dep : dependents ) {
            Plugin potential = pl.get( dep );
            if ( potential != null && !potential.isLoaded() ) {
                potentials.add( potential );
            }
        }
        if ( potentials.size() > 0 ) {
            DependentSelectionDialog dialog = new DependentSelectionDialog( potentials );
            Collection<Plugin> choices = dialog.getChoices();
            for ( Plugin p : choices ) {
                load( p );
            }
        }
    }

    /**
     * Checks all loaded plugins to see if any of them have an optional
     * dependency on the given jar. Reload those that do so the classloaders
     * work together.
     * @param jar A plugin jar that was just loaded.
     * @param className The plugin core class className, that is, the value
     * returned by EditPlugin.getClassName().
     */
    private void reloadOptionallyDependentPlugins( PluginJAR jar, String className ) {
        // check all other installed plugins to see if any of them
        // use the given jar. Reload those that do so the classloaders work together.
        PluginJAR[] installedPlugins = jEdit.getPluginJARs();
        for ( PluginJAR installed : installedPlugins ) {
            if ( installed == null || installed.equals( jar ) ) {
                continue;
            }
            EditPlugin ep = installed.getPlugin();
            if ( ep == null ) {
                continue;                // library jar
            }
            String installedClassname = ep.getClassName();
            PluginDepends[] deps = getPluginDepends( installedClassname );
            for ( PluginDepends dep : deps ) {
                if ( "plugin".equals( dep.what ) && className.equals( dep.name ) ) {
                    int status = getStatus( installed );
                    String reloadPath = installed.getPath();
                    jEdit.removePluginJAR( ep.getPluginJAR(), false );
                    PluginJAR reloaded = PluginJAR.load( reloadPath, true );
                    if ( status == ACTIVATED ) {
                        reloaded.activatePlugin();
                    }
                }
            }
        }
    }    // }}}

    // {{{ getPluginDepends() method
    private PluginDepends[] getPluginDepends( String classname ) throws IllegalArgumentException {
        List<PluginDepends> ret = new ArrayList<PluginDepends>();
        int i = 0;
        String dep;
        while ( ( dep = jEdit.getProperty( "plugin." + classname + ".depend." + i++ ) ) != null ) {
            boolean optional;
            if ( dep.startsWith( "optional " ) ) {
                optional = true;
                dep = dep.substring( "optional ".length() );
            } else {
                optional = false;
            }

            int index = dep.indexOf( ' ' );
            if ( index == -1 ) {
                throw new IllegalArgumentException( "wrong dependency" );
            }

            String what = dep.substring( 0, index );
            String arg = dep.substring( index + 1 );
            PluginDepends depends = new PluginDepends();
            depends.what = what;
            depends.arg = arg;
            depends.optional = optional;
            depends.dep = dep;
            if ( "plugin".equals( what ) ) {
                depends.name = arg.indexOf( ' ' ) > 0 ? arg.substring( 0, arg.indexOf( ' ' ) ) : arg;
            }
            ret.add( depends );
        }
        return ret.toArray( new PluginDepends[0] );
    }    // }}}

    // {{{ PluginDepends class
    private class PluginDepends {
        String dep;        // full string, e.g. plugin errorlist.ErrorList 1.3
        String what;        // depends type, e.g. jedit, jdk, plugin
        String arg;        // classname + version, e.g errorlist.ErrorList 1.3
        String name;        // just the class name, e.g. errorlist.ErrorList, only filled in if what is plugin
        boolean optional;

    }    // }}}


    /**
     * Safely unloads plugins, and deactivates all plugins that depend
     * on this one.
     * @param jar the plugin you wish to unload
     * @return a stack of strings, one for each deactivated plugin, in the reverse order
     *    they were unloaded.
     */
    // {{{ unloadPluginJar()
    public Deque<UnloadedStatus> unloadPlugin( Plugin plugin ) {
        Deque<UnloadedStatus> unloaded = new ArrayDeque<UnloadedStatus>();
        Set<String> unloadedSet = new HashSet<String>();
        unloadedSet = Collections.synchronizedSet( unloadedSet );

        // keep track of the optionally dependent plugins so they can be reloaded
        // and reactivated after this plugin is unloaded
        PluginJAR jar = plugin.getJAR();
        String[] optionals = jar.getOptionallyDependentPlugins();
        List<String> activated = new ArrayList<String>();
        PluginList pl = PluginList.getInstance();
        for ( String path : optionals ) {
            Plugin p = pl.get( path );
            if ( p.isActivated() ) {
                activated.add( path );
            }
        }

        unloadRecursive( plugin, unloaded, unloadedSet );

        // reload and maybe activate the optionally dependent plugins since they
        // can run without this plugin
        for ( String opt : optionals ) {
            PluginJAR.load( opt, true );
            unloaded.remove( opt );
        }
        for ( String path : activated ) {
            PluginJAR pj = jEdit.getPluginJAR( path );
            pj.activatePlugin();
        }
        return unloaded;
    }

    private void unloadRecursive( Plugin plugin, Deque<UnloadedStatus> unloaded, Set<String> unloadedSet ) {
        PluginJAR jar = plugin.getJAR();
        String[] dependents = jar.getAllDependentPlugins();
        PluginList pl = PluginList.getInstance();
        for ( String path : dependents ) {
            if ( !unloadedSet.contains( path ) ) {
                unloadedSet.add( path );
                Plugin dependent = pl.get( path );
                if ( dependent != null ) {
                    unloadRecursive( dependent, unloaded, unloadedSet );
                }
            }
        }
        unloaded.push( new UnloadedStatus( plugin.getPath(), plugin.isActivated() ) );
        jEdit.removePluginJAR( jar, false );
        String cachePath = jar.getCachePath();
        if ( cachePath != null ) {
            new File( cachePath ).delete();
        }
    }    // }}}


    public class UnloadedStatus {
        private String path;
        boolean activated;
        public UnloadedStatus( String path, boolean activated ) {
            this.path = path;
            this.activated = activated;
        }
        public String getPath() {
            return path;
        }
        public boolean getActivated() {
            return activated;
        }
    }

    /**
     * @return One of NOT_LOADED, LOADED, ERROR, ACTIVATED, or LIBRARY. A plugin
     * is considered "deactivated" if it is loaded but not activated. Library
     * jars don't have a status, such jars are simply noted as LIBRARY.
     */
    public static int getStatus( PluginJAR jar ) {
        if ( jar == null ) {
            return NOT_LOADED;
        }
        if ( jar.getPlugin() == null ) {
            return LIBRARY;
        } else if ( jar.getPlugin() instanceof EditPlugin.Deferred ) {
            return LOADED;
        } else if ( jar.getPlugin() instanceof EditPlugin.Broken ) {
            return ERROR;
        }
        return ACTIVATED;
    }

    public static String getStatusText( int status ) {
        switch ( status ) {
            case ERROR:
                return jEdit.getProperty( "activator.Error", "Error" );
            case LOADED:
                return jEdit.getProperty( "activator.Loaded", "Loaded" );
            case ACTIVATED:
                return jEdit.getProperty( "activator.Activated", "Activated" );
            case NOT_LOADED:
                return jEdit.getProperty( "activator.Not_Loaded", "Not Loaded" );
            default:
                return "";
        }
    }
}
