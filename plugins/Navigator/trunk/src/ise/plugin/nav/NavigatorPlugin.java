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
import org.gjt.sp.jedit.msg.ViewUpdate;

/**
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision$
 * @since     Oct 25, 2003
 */
public class NavigatorPlugin extends EBPlugin {

   /** Description of the Field */
   public final static String NAME = "Navigator";
   /** Description of the Field */
   public final static String MENU = "Navigator.menu";
   /** Description of the Field */
   private final static HashMap map = new HashMap();

   public void handleMessage(EBMessage msg) {
      if (msg instanceof ViewUpdate) {
         ViewUpdate vu = (ViewUpdate)msg;
         if (vu.getWhat().equals(ViewUpdate.CREATED)){
            View view = vu.getView();
            boolean showOnToolbar = jEdit.getBooleanProperty("ise.plugin.nav.Navigator.showOnToolbar", true);
            if (showOnToolbar) {
               Navigator navigator = getNavigator(view);
               if (navigator == null) {
                  navigator = new Navigator(view, "");  
                  addNavigator(view, navigator);
               }
               Nav nav = navigator.getNav();
               javax.swing.Box toolbar = view.getToolBar();
               if (toolbar != null && toolbar.isVisible() ) {
                  if (showOnToolbar) {
                     navigator.remove( nav );
                     toolbar.remove( nav );
                     toolbar.add( nav );
                  }
                  else {
                     toolbar.remove( nav );
                     navigator.remove( nav );
                     navigator.add( nav );
                  }
                  toolbar.repaint();
               }
               else if (showOnToolbar) {
                  jEdit.setBooleanProperty("view.showToolbar", true);
                  view.addToolBar(nav);
               }
            }
         }
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
      if (map.containsKey(view))
         return;
      map.put( view, navigator );
   }
   
   public static Navigator getNavigator(View view) {
      return (Navigator)map.get(view);  
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

