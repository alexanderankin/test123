package lookandfeel;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.event.*;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.SubstanceWidgetType;

public class SubstanceLookAndFeelPlugin extends EBPlugin {

    public static final String SUBSTANCE_THEME_PROP = "substance.theme";
    public static final String SUBSTANCE_MENU_SEARCH = "substance.menu.search";
    
    private static boolean isShowingMenuSearch = jEdit.getBooleanProperty( SUBSTANCE_MENU_SEARCH, false );

    private static void initMenuSearch(final boolean visible) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                for ( View view : jEdit.getViews() ) {
                    SubstanceLookAndFeel.setWidgetVisible( view.getRootPane(), visible, SubstanceWidgetType.MENU_SEARCH );
                    view.getJMenuBar().revalidate();
                }
            }
        } );
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) msg;
            if ( ViewUpdate.CREATED == vu.getWhat() && UIManager.getLookAndFeel().getClass().getName().startsWith("org.pushingpixels.substance.api.skin.") ) {
                SubstanceLookAndFeelPlugin.initMenuSearch(isShowingMenuSearch);
            }
        } else if ( msg instanceof PropertiesChanged) {
            boolean visible = jEdit.getBooleanProperty( SUBSTANCE_MENU_SEARCH, false );
            if (visible != isShowingMenuSearch) {
                isShowingMenuSearch = visible;
                initMenuSearch(isShowingMenuSearch);
            }
        }
    }
}