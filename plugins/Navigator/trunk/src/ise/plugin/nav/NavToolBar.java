
package ise.plugin.nav;

import java.awt.*;
import javax.swing.*;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * A simple toolbar for a Navigator
 
 * $Id$
 * @author   Dale Anson, danson@germane-software.com, August 2002
 */
public class NavToolBar extends JToolBar {

   private JButton back, forward;

   /**
    * @param client  the client object to provide navigation for
    */
   public NavToolBar( Navigator client ) {
      if ( client == null ) {
         throw new IllegalArgumentException( "client cannot be null" );
      }
      setFloatable( false );
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

