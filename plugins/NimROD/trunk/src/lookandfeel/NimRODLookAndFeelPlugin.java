
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
        copyBundledProperties();
        loadThemes();
    }
    
    // copies bundled theme files from the plugin jar to
    // the plugin home directory, but does not overwrite files of the same
    // name that already exist in the plugin home directory.
    private void copyBundledProperties() {
        // this property has a comma separated list of the just the names of the properties
        // files.  The files are located in the jar file at nimrod/themes.
        String propsFiles = jEdit.getProperty("nimrod.includedThemes");
        if (propsFiles == null || propsFiles.length() == 0) {
            return ;
        }
        String[] filenames = propsFiles.split(",");
        File homeDir = jEdit.getPlugin("lookandfeel.NimRODLookAndFeelPlugin").getPluginHome();
        homeDir.mkdirs();
        for (String filename : filenames) {
            filename = filename.trim() + ".properties";
            File outfile = new File(homeDir, filename);
            if (outfile.exists()) {
                continue;
            }
            String resource = "nimrod/themes/" + filename;
            copyToFile(getClass().getClassLoader().getResourceAsStream(resource), outfile);
        }
    }
    
    /**
     * Old school copy to file method, works well, and is sufficient for this purpose.
     * Copies a stream to a file. If destination file exists, it will be
     * overwritten. The input stream will be closed when this method returns.
     *
     * @param from           stream to copy from, will be closed after copy
     * @param to             file to write
     * @exception Exception  most likely an IOException
     */
    public static void copyToFile(InputStream from, File to) {
        try {
            FileOutputStream out = new FileOutputStream(to);
            byte[] buffer = new byte[1024];
            int bytes_read;
            while (true) {
                bytes_read = from.read(buffer);
                if (bytes_read == -1) {
                    break;
                }
                out.write(buffer, 0, bytes_read);
            }
            out.flush();
            out.close();
            from.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the properties files containing the definitions any saved themes.
     * These files are stored in the plugin home directory.  They are named with
     * the theme name followed by .properties and are stored in the themes map.
     * I'm just loading them all, they are small files, and likely not many of them.
     */
    private static void loadThemes() {
        themes.clear();
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
        themeNames.sort( String.CASE_INSENSITIVE_ORDER );
        themeNames.remove("___temp___");
        return themeNames.toArray( new String [themeNames.size()]  );
    }

    // these are the default NimROD values, this is not stored in the file system
    // like user created themes.
    protected static Properties getDefaultTheme() {
        Properties props = new Properties();
        props.setProperty( "nimrodlf.p", "#ffe3a300" );
        props.setProperty( "nimrodlf.p1", "#ffebb000" );
        props.setProperty( "nimrodlf.p2", "#fff5bc00" );
        props.setProperty( "nimrodlf.p3", "#fff5bc00" );
        props.setProperty( "nimrodlf.s", "#ffaba98a" );
        props.setProperty( "nimrodlf.s1", "#ffaba98a" );
        props.setProperty( "nimrodlf.s2", "#ffb3b092" );
        props.setProperty( "nimrodlf.s3", "#ffbdbb9d" );
        props.setProperty( "nimrodlf.b", "#ff000000" );
        props.setProperty( "nimrodlf.w", "#ffffffff" );
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
            loadThemes();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
