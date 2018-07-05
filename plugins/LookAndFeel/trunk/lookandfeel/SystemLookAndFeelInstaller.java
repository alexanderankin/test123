
package lookandfeel;


import java.awt.Font;
import java.awt.Insets;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.DimensionUIResource;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.gui.FontSelector;
import org.gjt.sp.jedit.jEdit;


public class SystemLookAndFeelInstaller implements LookAndFeelInstaller {

    UIManager.LookAndFeelInfo info = null;

    public SystemLookAndFeelInstaller( UIManager.LookAndFeelInfo info ) {
        this.info = info;
    }

    /**
     * @return the name of this look and feel
     */
    public String getName() {
        return info.getName();
    }

    /**
     * @return A configuration panel for this look and feel.  May return null
     * if there is nothing to configure for this look and feel.
     */
    public AbstractOptionPane getOptionPane() {

        // return a pane for Metal only, all others return null. For Metal,
        // need to show the font properties like in Global Options/Appearance.
        if ( info.getClassName().equals( "javax.swing.plaf.metal.MetalLookAndFeel" ) ) {
            return new OptionComponent();
        }
        else {
            return null;
        }
    }

    /**
     * Install this look and feel.
     */
    public void install() throws UnsupportedLookAndFeelException {
        try {
            UIManager.setLookAndFeel( info.getClassName() );
            if ( info.getName().equals( "Nimbus" ) ) {

                // Nimbus has bugs, this fixes the problem with the scroll bars.
                // The bugs seem to have been fixed in OpenJDK 9, but not in Oracle java 9. Odd.
                // See: https://stackoverflow.com/questions/11493273/scroll-horizontally-in-jtable-with-nimbus-look-and-feel
                // https://bugs.openjdk.java.net/browse/JDK-8072677
                // and jEdit tracker #3368
                LookAndFeel laf = UIManager.getLookAndFeel();
                laf.getDefaults().put( "ScrollBar.maximumThumbSize", new DimensionUIResource( 4096, 4096 ) );
                // this sort of fixes the button issue, sometimes, with the right version of java 8
                laf.getDefaults().put( "Button.contentMargins", new Insets( 0, 0, 0, 0 ) );
                UIManager.setLookAndFeel( laf );
            }
            UIManager.put( "ClassLoader", ClassLoader.getSystemClassLoader() );
        }
        catch ( Exception e ) {
            throw new UnsupportedLookAndFeelException( e.getMessage() );
        }
    }




    /**
     * The configuration component for the Metal look and feel.
     */
    class OptionComponent extends AbstractOptionPane {

        private FontSelector primaryFont;
        private FontSelector secondaryFont;

        /**
         * Create a new <code>OptionComponent</code>.
         */
        public OptionComponent() {
            super( "Metal" );
            init();
        }

        /**
         * Layout this component.
         */
        public void _init() {

            /* Primary Metal L&F font */
            Font pf = jEdit.getFontProperty( "metal.primary.font" );
            primaryFont = new FontSelector( pf );
            addComponent( jEdit.getProperty( "options.appearance.primaryFont" ), primaryFont );

            /* Secondary Metal L&F font */
            secondaryFont = new FontSelector( jEdit.getFontProperty( "metal.secondary.font" ) );
            addComponent( jEdit.getProperty( "options.appearance.secondaryFont" ), secondaryFont );
        }

        /**
         * Save this configuration.
         */
        public void _save() {
            jEdit.setFontProperty( "metal.primary.font", primaryFont.getFont() );
            jEdit.setFontProperty( "metal.secondary.font", secondaryFont.getFont() );
            try {
                SystemLookAndFeelInstaller.this.install();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
