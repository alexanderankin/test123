/*
 * IO.java
 * Copyright (c) 2002 Calvin Yu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package templates;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.gjt.sp.util.Log;

/**
 * IO utilities.
 */
public class IO
{

   /**
    * Close input stream.
    */
   public static void close(InputStream in)
   {
      if (in == null) {
         return;
      }
      try {
         in.close();
      } catch (IOException e) {
         Log.log(Log.ERROR, IO.class, e);
      }
   }

   /**
    * Close output stream.
    */
   public static void close(OutputStream out)
   {
      if (out == null) {
         return;
      }
      try {
         out.close();
      } catch (IOException e) {
         Log.log(Log.ERROR, IO.class, e);
      }
   }

}
