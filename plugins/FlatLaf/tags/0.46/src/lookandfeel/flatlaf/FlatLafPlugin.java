
package lookandfeel.flatlaf;


import java.util.*;

import org.gjt.sp.jedit.EditPlugin;


public class FlatLafPlugin extends EditPlugin {

    private static Properties themes = null;
    public static final String FLATLAF_CURRENT_THEME_PROP = "flatlaf.current.theme";

    public void start() {
        loadThemes();
    }

    private void loadThemes() {
        themes = new Properties();
        String resource = "lookandfeel/flatlaf/themes.properties";
        try {
            themes.load( getClass().getClassLoader().getResourceAsStream( resource ) );
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Vector<String> getThemeNames() {
        Set<String> names = themes.stringPropertyNames();
        Vector<String> list = new Vector<String>(names);
        list.sort( String.CASE_INSENSITIVE_ORDER );
        return list;
    }
    
    public static String getThemeClassName(String themeName) {
        return themes.getProperty(themeName);   
    }
}

