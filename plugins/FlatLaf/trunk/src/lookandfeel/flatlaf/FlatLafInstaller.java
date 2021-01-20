
package lookandfeel.flatlaf;


import com.formdev.flatlaf.IntelliJTheme;

import javax.swing.*;

import lookandfeel.LookAndFeelInstaller;
import lookandfeel.LookAndFeelPlugin;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class FlatLafInstaller implements LookAndFeelInstaller {

    private IntelliJTheme.ThemeLaf currentTheme = null;

    public String getName() {
        return "FlatLaf";
    }

    public void install() throws UnsupportedLookAndFeelException {
        String theme = jEdit.getProperty( FlatLafPlugin.FLATLAF_CURRENT_THEME_PROP );
        if ( theme == null ) {
            theme = "Flat Light";
        }

        try {
            String classname = FlatLafPlugin.getThemeClassName( theme );
            Class c = Class.forName( classname );
            UIManager.put( "ClassLoader", c.getClassLoader() );
            UIManager.setLookAndFeel( ( javax.swing.LookAndFeel )c.newInstance() );
            UIManager.put( "TabbedPane.tabInsets", new java.awt.Insets(1,8,1,8));
            UIManager.put("TabbedPane.tabHeight", 12);
        }
        catch ( Exception e ) {

            // e.printStackTrace();
            throw new UnsupportedLookAndFeelException( e.getMessage() );
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
            super( "FlatLaf" );
            init();
        }

        /**
         * Layout this component.
         */
        public void _init() {

            theme_choices = new JComboBox( FlatLafPlugin.getThemeNames() );
            theme_choices.setEditable( false );
            addComponent( jEdit.getProperty( FlatLafPlugin.FLATLAF_CURRENT_THEME_PROP + ".label", "Theme" ), theme_choices );
            String currentThemeName = jEdit.getProperty( FlatLafPlugin.FLATLAF_CURRENT_THEME_PROP );
            if ( currentThemeName != null ) {
                try {
                    String themeClassName = FlatLafPlugin.getThemeClassName( currentThemeName );
                    if ( themeClassName != null ) {
                        currentTheme = (IntelliJTheme.ThemeLaf)Class.forName( themeClassName ).newInstance();
                    }
                    theme_choices.setSelectedItem( currentThemeName );
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * Save this configuration.
         */
        public void _save() {
            String theme_setting = ( String )theme_choices.getSelectedItem();
            if ( theme_setting == null ) {
                jEdit.unsetProperty( FlatLafPlugin.FLATLAF_CURRENT_THEME_PROP );
            }
            else {
                jEdit.setProperty( FlatLafPlugin.FLATLAF_CURRENT_THEME_PROP, theme_setting );
            }

            try {

                // delegate to LAF plugin for installation as it does this on the EDT
                LookAndFeelPlugin.installLookAndFeel( FlatLafInstaller.this );
            }
            catch ( Exception ignored ) {    // NOPMD
            }
        }
    }
}
