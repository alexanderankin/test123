
package lookandfeel;

import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * The configuration component, this allows the user to choose a theme, the same
 * themes apply to all 4 JGoodies look and feels.
 */
public class OptionComponent extends AbstractOptionPane {

    private JComboBox themeChoices;
    private LookAndFeelInstaller installer;

    private static final String[] themeNames = {
        "BrownSugar",
        "DarkStar",
        "DesertBlue",
        "DesertBluer",
        "DesertGreen",
        "DesertRed",
        "DesertYellow",
        "ExperienceBlue",
        "ExperienceGreen",
        "ExperienceRoyale",
        "LightGray",
        "Silver",
        "SkyBlue",
        "SkyBluer",
        "SkyGreen",
        "SkyKrupp",
        "SkyPink",
        "SkyRed",
        "SkyYellow"
    };

    /**
     * Create a new <code>OptionComponent</code>.
     */
    public OptionComponent(LookAndFeelInstaller installer) {
        super( "JGoodies" );
        this.installer = installer;
        init();
    }

    /**
     * Layout this component.
     */
    public void _init() {
        themeChoices = new JComboBox( themeNames );
        themeChoices.setEditable( false );
        addComponent( jEdit.getProperty( JGoodiesLookAndFeelPlugin.JGOODIES_THEME_PROP + ".label", "Theme" ), themeChoices );
        String theme = jEdit.getProperty( JGoodiesLookAndFeelPlugin.JGOODIES_THEME_PROP );
        if ( theme == null ) {
            theme = "SkyBlue";
        }

        themeChoices.setSelectedItem( theme );
    }

    /**
     * Save this configuration.
     */
    public void _save() {
        String theme_setting = ( String )themeChoices.getSelectedItem();
        if ( theme_setting == null ) {
            jEdit.unsetProperty( JGoodiesLookAndFeelPlugin.JGOODIES_THEME_PROP );
        }
        else {
            jEdit.setProperty( JGoodiesLookAndFeelPlugin.JGOODIES_THEME_PROP, theme_setting );
        }

        try {

            // delegate to LAF plugin for installation as it does this on the EDT
            LookAndFeelPlugin.installLookAndFeel( installer );
        }
        catch ( Exception ignored ) {    // NOPMD
        }
    }
}
