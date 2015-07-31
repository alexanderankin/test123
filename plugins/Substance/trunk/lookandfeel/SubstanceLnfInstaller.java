package lookandfeel;

import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class SubstanceLnfInstaller implements LookAndFeelInstaller {

    private static final String[] themeNames = {
                "Autumn",
                "Business",
                "BusinessBlackSteel",
                "BusinessBlueSteel",
                "Cerulean",                 // added in 7.2
                "ChallengerDeep",
                "Creme",
                "CremeCoffee",
                "Dust",
                "DustCoffee",
                "EmeraldDusk",
                "Gemini",           
                "Graphite",
                "GraphiteAqua",     
                "GraphiteGlassLookAndFeel",
                "Magellan",        
                //"Magma",                  // removed in 6.0
                "Mariner",                  // added in 6.1
                "MistAqua",
                "MistSilver",
                "Moderate",
                "Nebula",
                "NebulaBrickWall",
                "OfficeBlack2007",          // added in 6.1
                "OfficeBlue2007",
                "OfficeSilver2007",
                "Raven",
                //"RavenGraphite",          // removed in 6.0
                //"RavenGraphiteGlass",     // removed in 6.0
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
            UIManager.setLookAndFeel( (javax.swing.LookAndFeel)c.newInstance() );
            UIManager.put( "ClassLoader", c.getClassLoader() );
        }
        catch(Exception e) {
            //e.printStackTrace();
            throw new UnsupportedLookAndFeelException(e.getMessage());
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
            
            showMenuSearch = new JCheckBox("Show menu search");
            showMenuSearch.setSelected(jEdit.getBooleanProperty(SubstanceLookAndFeelPlugin.SUBSTANCE_MENU_SEARCH));
            addComponent(showMenuSearch);
        }

        /**
         * Save this configuration.
         */
        public void _save() {
            String theme_setting = ( String ) theme_choices.getSelectedItem();
            if ( theme_setting == null ) {
                jEdit.unsetProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_THEME_PROP );
            }
            else {
                jEdit.setProperty( SubstanceLookAndFeelPlugin.SUBSTANCE_THEME_PROP, theme_setting );
            }
            jEdit.setBooleanProperty(SubstanceLookAndFeelPlugin.SUBSTANCE_MENU_SEARCH, showMenuSearch.isSelected());
            try {
                SubstanceLnfInstaller.this.install();
            }
            catch ( Exception ignored ) {       // NOPMD
            }
        }
    }
}