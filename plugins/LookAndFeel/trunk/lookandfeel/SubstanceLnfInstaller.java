package lookandfeel;

import java.awt.Component;
import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class SubstanceLnfInstaller extends LnfInstaller {

    public final static String SUBSTANCE_THEME_PROP = "substance.theme";

    private static final String[] themeNames = {
                "Autumn",
                "Business",
                "BusinessBlackSteel",
                "BusinessBlueSteel",
                "ChallengerDeep",
                "Creme",
                "CremeCoffee",
                "Dust",
                "DustCoffee",
                "EmeraldDusk",
                "Gemini",           // api package
                "GraphiteAqua",     // api package
                "Magellan",         // api package
                "Magma",
                "MistAqua",
                "MistSilver",
                "Moderate",
                "Nebula",
                "NebulaBrickWall",
                "OfficeBlue2007",
                "OfficeSilver2007",
                "Raven",
                "RavenGraphite",
                "RavenGraphiteGlass",
                "Sahara",
                "Twilight"
            };

    public void install() throws UnsupportedLookAndFeelException {
        String theme = jEdit.getProperty( SUBSTANCE_THEME_PROP );
        if ( theme == null ) {
            theme = "Business";
        }

        try {
            Class c;
            if (theme.equals("Gemini") || theme.equals("GraphiteAqua") || theme.equals("Magellan")) {
                c = Class.forName( "org.jvnet.substance.api.skin.Substance" + theme + "LookAndFeel");
            }
            else {
                c = Class.forName( "org.jvnet.substance.skin.Substance" + theme + "LookAndFeel" );
            }
            UIManager.setLookAndFeel( (LookAndFeel)c.newInstance() );
            UIManager.put( "ClassLoader", c.getClassLoader() );
        }
        catch(Exception e) {
            throw new UnsupportedLookAndFeelException(e.getMessage());
        }
    }

    /**
     * Returns a component used to configure the look and feel.
     */
    public Component getOptionComponent() {
        return new OptionComponent();
    }

    /**
     * Save the configuration from the given {@link getOptionComponent()}.
     */
    public void saveOptions( Component comp ) {
        ( ( OptionComponent ) comp ).save();
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
            super( "Substance" );
            init();
        }

        /**
         * Layout this component.
         */
        public void _init() {
            theme_choices = new JComboBox( themeNames );
            theme_choices.setEditable( false );
            addComponent( jEdit.getProperty( SUBSTANCE_THEME_PROP + ".label", "Theme" ), theme_choices );

            String theme = jEdit.getProperty( SUBSTANCE_THEME_PROP );
            if ( theme == null ) {
                theme = "Business";
            }
            theme_choices.setSelectedItem( theme );
        }

        /**
         * Save this configuration.
         */
        public void _save() {
            String theme_setting = ( String ) theme_choices.getSelectedItem();
            if ( theme_setting == null ) {
                jEdit.unsetProperty( SUBSTANCE_THEME_PROP );
            }
            else {
                jEdit.setProperty( SUBSTANCE_THEME_PROP, theme_setting );
            }
            try {
                SubstanceLnfInstaller.this.install();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}