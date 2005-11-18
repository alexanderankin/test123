// $Id$
package ise.plugin.nav;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;

/**
 * NavigatorPlugin, mostly static methods, allows one Navigator per View.
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision$
 * @since     Oct 25, 2003
 */
public class NavigatorPlugin extends EBPlugin {

    /** Description of the Field */
    public final static String NAME = "Navigator";
    /** Description of the Field */
    public final static String MENU = "Navigator.menu";
    /** View/Navigator map */
    private final static HashMap map = new HashMap();

    private static boolean _showOnToolbar = false;
    private static boolean _viewShowingToolbar = false;

    public void start() {
        _showOnToolbar = jEdit.getBooleanProperty( "navigator.showOnToolbar", false );
        _viewShowingToolbar = jEdit.getBooleanProperty( "view.showToolbar" );
        if ( _showOnToolbar ) {
            addToAllToolbars();
        }
    }

    public void stop() {
        for ( Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            View view = ( View ) it.next();
            if (view.isClosed())
                continue;
            Navigator navigator = ( Navigator ) map.get( view );
            Nav nav = navigator.getNav();
            view.getDockableWindowManager().hideDockableWindow( "Navigator" );
            view.getDockableWindowManager().removeDockableWindow( "Navigator" );
            view.removeToolBar( nav );
        }
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) msg;
            if ( vu.getWhat().equals( ViewUpdate.CREATED ) ) {
                if ( _showOnToolbar ) {
                    View view = vu.getView();
                    Navigator navigator = createNavigator( view );
                    addToToolbar( view, navigator );
                }
            }
            else if ( vu.getWhat().equals( ViewUpdate.CLOSED ) ) {
                View view = vu.getView();
                Navigator navigator = getNavigator( view );
                if ( navigator != null )
                    removeFromToolbar( view, navigator );
                removeNavigator( view );
            }
        }
        else if ( msg instanceof PropertiesChanged ) {
            boolean viewShowingToolbar = jEdit.getBooleanProperty( "view.showToolbar" );
            boolean showOnToolbar = jEdit.getBooleanProperty( "navigator.showOnToolbar", false );
            boolean vst_changed = viewShowingToolbar != _viewShowingToolbar;
            boolean nst_changed = showOnToolbar != _showOnToolbar;
            if (vst_changed)
                _viewShowingToolbar = viewShowingToolbar;
            if (nst_changed)
                _showOnToolbar = showOnToolbar;
            
            if ( vst_changed || nst_changed) {
                if ( _viewShowingToolbar && _showOnToolbar) {
                    addToAllToolbars();
                }
                else {
                    // jEdit tool bar has been removed from all views, so make sure
                    // Navigator is removed from the toolbars also
                    removeFromAllToolbars();
                }
            }
            else {
                // no change
                return;
            }
        }
    }

    private static void addToToolbar( View view, Navigator navigator ) {
        boolean jEditToolbarShowing = jEdit.getBooleanProperty( "view.showToolbar" );
        if ( jEditToolbarShowing ) {
            Nav nav = navigator.getNav();
            navigator.remove( nav );
            view.getDockableWindowManager().hideDockableWindow( "Navigator" );
            view.getDockableWindowManager().removeDockableWindow( "Navigator" );
            view.removeToolBar( nav );  // just in case
            view.addToolBar( View.TOP_GROUP, View.TOP_LAYER, nav );
        }
    }

    private static void removeFromToolbar( View view, Navigator navigator ) {
        Nav nav = navigator.getNav();
        view.removeToolBar( nav );
        navigator.remove( nav );    // just in case
        navigator.add( nav );
    }

    /**
     * Adds a Navigator to the toolbars for all Views, but only if the 'Show tool bar'
     * checkbox is checked for the jEdit Global Options/Tool Bar settings.
     */
    private static void addToAllToolbars() {
        boolean jEditToolbarShowing = jEdit.getBooleanProperty( "view.showToolbar" );
        if ( jEditToolbarShowing ) {
            View[] views = jEdit.getViews();
            for ( int i = 0; i < views.length; i++ ) {
                View view = views[ i ];
                Navigator navigator = createNavigator( view );
                addToToolbar( view, navigator );
            }
        }
    }

    private static void removeFromAllToolbars() {
        for ( Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            View view = ( View ) it.next();
            Navigator navigator = ( Navigator ) map.get( view );
            removeFromToolbar( view, navigator );
        }
    }

    /**
     * create the menu items for the Plugins menu
     *
     * @param menuItems
     */
    public void createMenuItems( Vector menuItems ) {
        menuItems.addElement( GUIUtilities.loadMenu( MENU ) );
    }

    /**
     * Adds a Navigator. Navigators are tracked by view.
     *
     * @param view       The view for the Navigator
     * @param navigator  The Navigator
     */
    public static void addNavigator( View view, Navigator navigator ) {
        if ( view == null ) {
            return ;
        }
        if ( map.containsKey( view ) )
            return ;
        map.put( view, navigator );
    }

    public static void removeNavigator( View view ) {
        if ( view == null ) {
            return ;
        }
        if ( !map.containsKey( view ) )
            return ;
        map.remove( view );
    }

    public static Navigator getNavigator( View view ) {
        return ( Navigator ) map.get( view );
    }

    public static Navigator createNavigator( View view ) {
        Navigator navigator = getNavigator( view );
        if ( navigator == null ) {
            navigator = new Navigator( view );
            addNavigator( view, navigator );
        }
        return navigator;
    }

    /**
     * Wrapper for the 'goBack' method of the Navigator for the given view.
     *
     * @param view The view for the Navigator
     */
    public static void goBack( View view ) {
        Navigator navigator = ( Navigator ) map.get( view );
        if ( navigator != null ) {
            navigator.goBack();
        }
    }

    /**
     * Wrapper for the 'goForward' method of the Navigator for the given view.
     *
     * @param view The view for the Navigator
     */
    public static void goForward( View view ) {
        Navigator navigator = ( Navigator ) map.get( view );
        if ( navigator != null ) {
            navigator.goForward();
        }
    }
}

