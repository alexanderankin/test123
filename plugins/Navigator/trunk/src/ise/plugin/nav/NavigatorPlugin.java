// $Id$
package ise.plugin.nav;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

import javax.swing.JComponent;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

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

    public void start() {
	    View[] views = jEdit.getViews();
	    for (int i=0; i<views.length; ++i) {
		    createNavigator(views[i]);
	    }
    }

    public static void setToolBars() {
	    View[] views = jEdit.getViews();
	    for (int i=0; i<views.length; ++i) {
		    Navigator nv = (Navigator) map.get(views[i]);
		    nv.setToolBar();
	    }
    }
    
    
    public void stop() {
        for ( Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            View view = ( View ) it.next();
            if (view.isClosed())
                continue;
            Navigator navigator = ( Navigator ) map.get( view );
            navigator.stop();
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

    public static JComponent getToolBar(View view) {
	    Navigator nav = getNavigator(view);
	    NavToolBar toolBar = new NavToolBar(nav);
	    return toolBar;
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

