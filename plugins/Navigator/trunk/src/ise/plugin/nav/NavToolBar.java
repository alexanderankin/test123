
package ise.plugin.nav;

import java.awt.*;
import javax.swing.*;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * Provides navigation ability for a client object, has a "back" and "forward"
 * button to move through a list of objects.
 *
 * $Id$
 * @author   Dale Anson, danson@germane-software.com, August 2002
 */
public class NavToolBar extends JToolBar {

   private JButton back, forward;
   private Navigator client;


   /**
    * @param client  the client object to provide navigation for
    */
   public NavToolBar( Navigator client ) {
      if ( client == null ) {
         throw new IllegalArgumentException( "client cannot be null" );
      }
      this.client = client;
      setFloatable( true );
      //putClientProperty( "JToolBar.isRollover", Boolean.TRUE );

      // set up the buttons
      back = new JButton( GUIUtilities.loadIcon( "ArrowL.png" ) );
      back.setModel(client.getBackModel());
      forward = new JButton( GUIUtilities.loadIcon( "ArrowR.png" ) );
      forward.setModel(client.getForwardModel());
      back.setMargin( new Insets( 0, 0, 0, 0 ) );
      forward.setMargin( new Insets( 0, 0, 0, 0 ) );
      add( back );
      add( forward );

   }
}

