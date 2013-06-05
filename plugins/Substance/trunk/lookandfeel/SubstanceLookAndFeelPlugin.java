package lookandfeel;

import javax.swing.SwingUtilities;

import java.awt.event.*;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.ViewUpdate;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.SubstanceWidgetType;

public class SubstanceLookAndFeelPlugin extends EBPlugin {

    public static final String SUBSTANCE_THEME_PROP = "substance.theme";
    public static final String SUBSTANCE_MENU_SEARCH = "substance.menu.search";

    public void start() {
        SubstanceLookAndFeelPlugin.initMenuSearch();
    }

    public static void initMenuSearch() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                for ( View view : jEdit.getViews() ) {
                    SubstanceLookAndFeel.setWidgetVisible( view.getRootPane(), jEdit.getBooleanProperty( SUBSTANCE_MENU_SEARCH, false ), SubstanceWidgetType.MENU_SEARCH );
                    view.getJMenuBar().revalidate();
                }
            }
        } );
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) msg;
            if ( ViewUpdate.CREATED == vu.getWhat() ) {
                SubstanceLookAndFeelPlugin.initMenuSearch();
                vu.getView().getJMenuBar().addContainerListener( new ContainerAdapter() {
                    @Override public void componentRemoved( ContainerEvent e ) {
                        SubstanceLookAndFeelPlugin.initMenuSearch();
                    }
                } );

            }
        }
    }
}