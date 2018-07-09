package lookandfeel;

import java.util.HashMap;
import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import de.muntjak.tinylookandfeel.*;

public class TinyLnfInstaller  implements LookAndFeelInstaller {
	public String getName() {
        return "Tiny";		
	}
	
    public void install() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new de.muntjak.tinylookandfeel.TinyLookAndFeel() );
        UIManager.put( "ClassLoader", de.muntjak.tinylookandfeel.TinyLookAndFeel.class.getClassLoader() );
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
        HashMap<String, ThemeDescription> themeMap;

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
            ThemeDescription[] themes = Theme.getAvailableThemes();
            if (themes.length > 0) {
                String[] themeNames = new String[themes.length];
                themeMap = new HashMap<String, ThemeDescription>();
                for (int i = 0; i < themes.length; i++) {
                    themeNames[i] = themes[i].getName();
                    themeMap.put(themes[i].getName(), themes[i]);
                }
                theme_choices = new JComboBox( themeNames );
                theme_choices.setEditable( false );
                addComponent(jEdit.getProperty("lookandfeel.tiny.themes.label"), theme_choices);
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
            String theme_setting = ( String ) theme_choices.getSelectedItem();
            if ( theme_setting == null ) {
                jEdit.unsetProperty( "lookandfeel.tiny.theme" );
            }
            else {
                jEdit.setProperty( "lookandfeel.tiny.theme", theme_setting );
                Theme.loadTheme(themeMap.get(theme_setting));
            }
            try {
                TinyLnfInstaller.this.install();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
