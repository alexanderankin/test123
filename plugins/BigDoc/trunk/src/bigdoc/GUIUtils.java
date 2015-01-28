package bigdoc;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class GUIUtils {

   /**
    * Centers <code>you</code> on <code>me</code>. Useful for centering
    * dialogs on their parent frames.
    *
    * @param me   Component to use as basis for centering.
    * @param you  Component to center on <code>me</code>.
    */
   public static void center( Component me, Component you ) {
      Rectangle my = me.getBounds();
      Dimension your = you.getSize();
      int x = my.x + ( my.width - your.width ) / 2;
      if ( x < 0 )
         x = 0;
      int y = my.y + ( my.height - your.height ) / 2;
      if ( y < 0 )
         y = 0;
      you.setLocation( x, y );
   }

   /**
    * Centers a component on the screen.
    *
    * @param me  Component to center.
    */
   public static void centerOnScreen( Component me ) {
      Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension window_size = me.getSize();
      me.setBounds( ( screen_size.width - window_size.width ) / 2,
            ( screen_size.height - window_size.height ) / 2,
            window_size.width,
            window_size.height );
   }
}

