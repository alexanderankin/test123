package lookandfeel;

import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class LipstikLnfInstaller  implements LookAndFeelInstaller {

    public final static String LIPSTIK_MENU_THEME_PROP = "lipstikLF.menuBar";
    public final static String LIPSTIK_THEME_PROP = "lipstikLF.theme";
    private static final String[] themeNames = { "KlearlooksTheme", "LightGrayTheme", "DefaultTheme", "ZenburnTheme" };


 	public String getName() {
        return "Lipstik";		
	}
	
   public void install() throws UnsupportedLookAndFeelException {
        String menu_theme = jEdit.getProperty( LIPSTIK_MENU_THEME_PROP );
        String theme = jEdit.getProperty( LIPSTIK_THEME_PROP );
        if ( !LookAndFeelPlugin.isEmpty( menu_theme ) ) {
            System.setProperty( LIPSTIK_MENU_THEME_PROP, menu_theme );
        }
        if ( !LookAndFeelPlugin.isEmpty( theme ) ) {
            System.setProperty( LIPSTIK_THEME_PROP, theme );
        }
        else {
            System.setProperty( LIPSTIK_THEME_PROP, "LightGrayTheme" );
        }

        UIManager.setLookAndFeel( new com.lipstikLF.LipstikLookAndFeel() );
        UIManager.put( "ClassLoader", com.lipstikLF.LipstikLookAndFeel.class.getClassLoader() );
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
        JComboBox menu_choices;
        JComboBox theme_choices;

        /**
         * Create a new <code>OptionComponent</code>.
         */
        public OptionComponent() {
            super( "Lipstik" );
            init();
        }

        /**
         * Layout this component.
         */
        public void _init() {
            String[] menu_settings = new String[] {"gradient", "solid", "none"};
            menu_choices = new JComboBox( menu_settings );
            menu_choices.setEditable( false );
            theme_choices = new JComboBox( themeNames );
            theme_choices.setEditable( false );
            addComponent( jEdit.getProperty(LIPSTIK_MENU_THEME_PROP + ".label"), menu_choices );
            addComponent( jEdit.getProperty(LIPSTIK_THEME_PROP + ".label"), theme_choices );

            String menu_theme = jEdit.getProperty( LIPSTIK_MENU_THEME_PROP );
            String theme = jEdit.getProperty( LIPSTIK_THEME_PROP );
            if ( !LookAndFeelPlugin.isEmpty( menu_theme ) ) {
                menu_choices.setSelectedItem( menu_theme );
            }
            if ( !LookAndFeelPlugin.isEmpty( theme ) ) {
                System.setProperty( LIPSTIK_THEME_PROP, theme );
                theme_choices.setSelectedItem( theme );
            }
        }

        /**
         * Save this configuration.
         */
        public void _save() {
            String menu_setting = ( String ) menu_choices.getSelectedItem();
            String theme_setting = ( String ) theme_choices.getSelectedItem();
            if ( menu_setting == null || menu_setting.equals( "Gradient" ) ) {
                jEdit.unsetProperty( LIPSTIK_MENU_THEME_PROP );
                System.clearProperty( LIPSTIK_MENU_THEME_PROP );
            }
            else {
                jEdit.setProperty( LIPSTIK_MENU_THEME_PROP, menu_setting );
                System.setProperty( LIPSTIK_MENU_THEME_PROP, menu_setting );
            }
            if ( theme_setting == null ) {
                jEdit.unsetProperty( LIPSTIK_THEME_PROP );
                System.clearProperty( LIPSTIK_THEME_PROP );
            }
            else {
                jEdit.setProperty( LIPSTIK_THEME_PROP, theme_setting );
                System.setProperty( LIPSTIK_THEME_PROP, theme_setting );
            }
            try {
                LipstikLnfInstaller.this.install();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
