
package lookandfeel;


import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;


public class NimRODLookAndFeelPlugin extends EditPlugin {

    // map of theme name <-> theme properties
    private static HashMap<String, Properties> themes = new HashMap<String, Properties>();

    public void start() {
        loadThemes();
    }

    /**
     * Load the properties files containing the definitions any saved themes.
     * These files are stored in the plugin home directory.  They are named with
     * the theme name followed by .properties and are stored in the themes map.
     * I'm just loading them all, they are small files, and likely not many of them.
     */
    private static void loadThemes() {
        themes.put( "default", getDefaultTheme() );
        try {
            File homeDir = jEdit.getPlugin( "lookandfeel.NimRODLookAndFeelPlugin" ).getPluginHome();
            homeDir.mkdirs();
            File[] files = homeDir.listFiles(         new FileFilter(){

                        public boolean accept( File pathname ) {
                            return pathname.getName().endsWith( ".properties" );
                        }
                    }
            );
            for ( File file : files ) {
                String filename = file.getName();
                String themeName = filename.substring( 0, filename.lastIndexOf( ".properties" ) );
                Properties props = new Properties();
                props.load( new FileReader( file ) );
                themes.put( themeName, props );
            }
        }
        catch ( Exception ignored ) {    // NOPMD
        }
    }

    /**
     * @return a Properties containing the values for a theme, or null if there is
     * no theme with the given name.
     */
    protected static Properties getTheme( String name ) {
        return themes.get( name );
    }

    /**
     * @return a list of stored theme names
     */
    protected static String[] getThemeList() {
        ArrayList<String> themeNames = new ArrayList<String>( themes.keySet() );
        themeNames.sort( null );
        return themeNames.toArray( new String [themeNames.size()]  );
    }

    // these are the default NimROD values, this is not stored in the file system
    // like user created themes.
    protected static Properties getDefaultTheme() {
        Properties props = new Properties();
        props.setProperty( "nimrodlf.p", "0xe3a300" );
        props.setProperty( "nimrodlf.p1", "0xebb000" );
        props.setProperty( "nimrodlf.p2", "0xf5bc00" );
        props.setProperty( "nimrodlf.p3", "0xf5bc00" );
        props.setProperty( "nimrodlf.s", "0xaba98a" );
        props.setProperty( "nimrodlf.s1", "0xaba98a" );
        props.setProperty( "nimrodlf.s2", "0xb3b092" );
        props.setProperty( "nimrodlf.s3", "0xbdbb9d" );
        props.setProperty( "nimrodlf.b", "0x000000" );
        props.setProperty( "nimrodlf.w", "0xffffff" );
        props.setProperty( "nimrodlf.frameOpacity", "180" );
        props.setProperty( "nimrodlf.menuOpacity", "195" );
        return props;
    }

    protected static void saveTheme( String name, Properties props ) {
        if ( name == null || name.isEmpty() || props == null ) {
            return;
        }
        if (name.equals("default")) {
            JOptionPane.showMessageDialog( null, jEdit.getProperty("nimrod.duplicateThemeName.text", "Theme may not be named \"default\"."), jEdit.getProperty( "nimrod.duplicateThemeName.title", "Duplicate Theme Name" ), JOptionPane.ERROR_MESSAGE );
            return;
        }
        try {
            File homeDir = jEdit.getPlugin( "lookandfeel.NimRODLookAndFeelPlugin" ).getPluginHome();
            File outFile = new File( homeDir, name + ".properties" );
            props.store( new FileWriter( outFile ), "Theme: " + name );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        themes.clear();
        loadThemes();
    }

    protected static void deleteTheme( String name ) {
        if ( name == null || name.isEmpty() ) {
            return;
        }
        try {
            File homeDir = jEdit.getPlugin( "lookandfeel.NimRODLookAndFeelPlugin" ).getPluginHome();
            File file = new File( homeDir, name + ".properties" );
            file.delete();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
