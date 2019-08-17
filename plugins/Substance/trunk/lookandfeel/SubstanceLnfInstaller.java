
package lookandfeel;


import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceSlices;


public class SubstanceLnfInstaller implements LookAndFeelInstaller {

    private static final String[] themeNames = {
        "Autumn",
        "BusinessBlackSteel",
        "BusinessBlueSteel",
        "Business",
        "Cerulean",
        "CremeCoffee",
        "Creme",
        "DustCoffee",
        "Dust",
        "Gemini",
        "GraphiteAqua",
        "GraphiteChalk",
        "GraphiteGlass",
        "GraphiteGold",
        "Graphite",
        "Magellan",
        "Mariner",
        "MistAqua",
        "MistSilver",
        "Moderate",
        "NebulaBrickWall",
        "Nebula",
        "OfficeBlack2007",
        "OfficeBlue2007",
        "OfficeSilver2007",
        "Raven",
        "Sahara",
        "Twilight"
    };

    public String getName() {
        return "Substance";
    }

    public void install() throws UnsupportedLookAndFeelException {
        String theme = jEdit.getProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_THEME_PROP );
        if ( theme == null ) {
            theme = "Business";
        }

        try {
            Class c = Class.forName( "org.pushingpixels.substance.api.skin.Substance" + theme + "LookAndFeel" );
            UIManager.put( "ClassLoader", c.getClassLoader() );
            UIManager.setLookAndFeel( ( javax.swing.LookAndFeel )c.newInstance() );

            // adjust JOptionPane buttons to yes/no/cancel (as opposed to cancel/no/yes) as this is the Java standard.
            // The other option is to use PLATFORM, which would do the same thing except on Mac, where the buttons would
            // be ordered as cancel/no/yes, but that would be inconsistent with the rest of jEdit.
            SubstanceCortex.GlobalScope.setButtonBarOrder( SubstanceSlices.ButtonOrder.DEFAULT_AS_LEADING );

            // align the JOptionPane buttons to the right, since most (all?) jEdit dialogs are aligned that way
            SubstanceCortex.GlobalScope.setButtonBarGravity( SubstanceSlices.HorizontalGravity.TRAILING );
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
        JCheckBox showMenuSearch;

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
            addComponent( jEdit.getProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_THEME_PROP + ".label", "Theme" ), theme_choices );
            String theme = jEdit.getProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_THEME_PROP );
            if ( theme == null ) {
                theme = "Business";
            }

            theme_choices.setSelectedItem( theme );
            showMenuSearch = new JCheckBox( "Show menu search" );
            showMenuSearch.setSelected( jEdit.getBooleanProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_MENU_SEARCH ) );
            addComponent( showMenuSearch );
        }

        /**
         * Save this configuration.
         */
        public void _save() {
            String theme_setting = ( String )theme_choices.getSelectedItem();
            if ( theme_setting == null ) {
                jEdit.unsetProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_THEME_PROP );
            }
            else {
                jEdit.setProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_THEME_PROP, theme_setting );
            }

            jEdit.setBooleanProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_MENU_SEARCH, showMenuSearch.isSelected() );
            try {
                // delegate to LAF plugin for installation as it does this on the EDT
                LookAndFeelPlugin.installLookAndFeel( SubstanceLnfInstaller.this );
            }
            catch ( Exception ignored ) {    // NOPMD
            }
        }
    }
}
