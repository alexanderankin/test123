package ise.plugin.nav;

import org.gjt.sp.jedit.Buffer;


/**
 * This object is used to mark caret positions in jEdit buffers
 * @author Dale Anson, danson@germane-software.com
 */
public class NavPosition {

   public String path = null;
   public int caret = 0;
   public int line = 0;

   public NavPosition(Buffer b, int c) {
      if ( b == null )
         throw new IllegalArgumentException( "buffer cannot be null" );
      if ( c < 0 )
         throw new IllegalArgumentException( "caret position cannot less than 0" );
      path = b.getPath();
      caret = c;
      line = b.getLineOfOffset(caret);
   }

   public boolean equals(NavPosition other) {
       return (path.equals(other.path) && caret == other.caret);
   }

   public String toString() {
      return path + ":" + (line + 1);
   }
}
