package ise.plugin.nav;

import org.gjt.sp.jedit.Buffer;


/**
 * This object is used to mark caret positions in jEdit buffers for the Nav
 * used by VPT.
 * @author Dale Anson, danson@germane-software.com
 */
public class NavPosition {

   public Buffer buffer = null;
   public int caret = 0;

   public NavPosition( Buffer b, int c ) {
      if ( b == null )
         throw new IllegalArgumentException( "buffer cannot be null" );
      if ( c < 0 )
         throw new IllegalArgumentException( "caret position cannot less than 0" );
      buffer = b;
      caret = c;
   }

   public String toString() {
      return buffer.getFile().getAbsolutePath() + ":" + caret;
   }
}
