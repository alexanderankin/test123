
package lookandfeel;


import de.muntjak.tinylookandfeel.*;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class TinyLnfInstaller implements LookAndFeelInstaller {

    private TreeMap<String, ThemeDescription> themeMap;

    public String getName() {
        return "Tiny";
    }

    public void install() throws UnsupportedLookAndFeelException {
        loadThemeMap();
        String theme_name = jEdit.getProperty( "lookandfeel.tiny.theme" );
        if ( theme_name != null ) {
            Theme.loadTheme( themeMap.get( theme_name ) );
        }
        UIManager.setLookAndFeel( new de.muntjak.tinylookandfeel.TinyLookAndFeel() );
        UIManager.put( "ClassLoader", de.muntjak.tinylookandfeel.TinyLookAndFeel.class.getClassLoader() );
    }

    private void loadThemeMap() {
        ThemeDescription[] themes = Theme.getAvailableThemes();
        if ( themes.length > 0 ) {
            String[] themeNames = new String [themes.length];
            themeMap = new TreeMap<String, ThemeDescription>();
            for ( int i = 0; i < themes.length; i++ ) {
                themeNames[i] = themes[i].getName();
                themeMap.put( themes[i].getName(), themes[i] );
            }
        }
    }

    /**
     * Returns a component used to configure the look and feel.
     */
    public AbstractOptionPane getOptionPane() {
        return new OptionComponent();
    }




    /**
     * The configuration component.
     */
    class OptionComponent extends AbstractOptionPane {

        JComboBox theme_choices;

        /**
         * Create a new <code>OptionComponent</code>.
         */
        public OptionComponent() {
            super( "Tiny" );
            init();
        }

        /**
         * Layout this component.
         */
        public void _init() {
            loadThemeMap();
            if ( !themeMap.isEmpty() ) {
                theme_choices = new JComboBox( new Vector<String>( themeMap.keySet() ) );
                theme_choices.setEditable( false );
                addComponent( jEdit.getProperty( "lookandfeel.tiny.themes.label" ), theme_choices );
            }
            String theme = jEdit.getProperty( "lookandfeel.tiny.theme" );
            if ( !LookAndFeelPlugin.isEmpty( theme ) ) {
                theme_choices.setSelectedItem( theme );
            }
        }

        /**
         * Save this configuration.
         */
        public void _save() {
            String theme_setting = ( String )theme_choices.getSelectedItem();
            if ( theme_setting == null ) {
                jEdit.unsetProperty( "lookandfeel.tiny.theme" );
            }
            else {
                jEdit.setProperty( "lookandfeel.tiny.theme", theme_setting );
                Theme.loadTheme( themeMap.get( theme_setting ) );
            }
        }
    }
}
