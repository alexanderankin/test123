
package lookandfeel;


import javax.swing.*;
import java.lang.reflect.Method;
import java.util.Properties;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class JTattooLnfInstaller implements LookAndFeelInstaller {

    private static final String[] themeNames = {
        "Acryl",
        "Aero",
        "Aluminium",
        "Bernstein",
        "Fast",
        "Graphite",
        "Hifi",
        "Luna",
        "McWin",
        "Noire",
        "Smart",
        "Texture"
    };

    public String getName() {
        return "JTattoo";
    }

    public void install() throws UnsupportedLookAndFeelException {
        String theme = jEdit.getProperty( JTattooLookAndFeelPlugin.JTattoo_THEME_PROP );
        if ( theme == null ) {
            theme = "McWin";
        }

        try {
            Class c = Class.forName( "com.jtattoo.plaf." + theme.toLowerCase() + '.' + theme + "LookAndFeel" );
            
            // hide the silly menu logo thing. Could put 'jEdit' here...
            Properties props = new Properties();
            props.put( "logoString", "" );
            Method m = c.getDeclaredMethod("setCurrentTheme", Properties.class);
            m.invoke(c, props);
            
            UIManager.setLookAndFeel( ( javax.swing.LookAndFeel )c.newInstance() );
            UIManager.put( "ClassLoader", c.getClassLoader() );
        }
        catch ( Exception e ) {
            //e.printStackTrace();
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
            super( "JTattoo" );
            init();
        }

        /**
         * Layout this component.
         */
        public void _init() {
            theme_choices = new JComboBox( themeNames );
            theme_choices.setEditable( false );
            addComponent( jEdit.getProperty( JTattooLookAndFeelPlugin.JTattoo_THEME_PROP + ".label", "Theme" ), theme_choices );
            String theme = jEdit.getProperty( JTattooLookAndFeelPlugin.JTattoo_THEME_PROP );
            if ( theme == null ) {
                theme = "McWin";
            }

            theme_choices.setSelectedItem( theme );
        }

        /**
         * Save this configuration.
         */
        public void _save() {
            String theme_setting = ( String )theme_choices.getSelectedItem();
            if ( theme_setting == null ) {
                jEdit.unsetProperty( JTattooLookAndFeelPlugin.JTattoo_THEME_PROP );
            }
            else {
                jEdit.setProperty( JTattooLookAndFeelPlugin.JTattoo_THEME_PROP, theme_setting );
            }

            try {
                LookAndFeelPlugin.installLookAndFeel( JTattooLnfInstaller.this );
            }
            catch ( Exception ignored ) {    // NOPMD
            }
        }
    }
}
