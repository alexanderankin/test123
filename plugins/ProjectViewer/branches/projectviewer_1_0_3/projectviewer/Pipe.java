/*
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
package projectviewer;

import java.io.*;


/**
 * Provides implementations for piping streams.
 */
public class Pipe {

  public final static int BUFFER_SIZE = 2048;
  
  /**
   * Pipes the given <code>InputStream</code> to the given <code>OutputStream</code>.
   */
  public static void pipe( InputStream in, OutputStream out )
    throws IOException
  {
    byte[] buf = new byte[ BUFFER_SIZE ];
    
    int bytesRead = in.read( buf, 0, buf.length );
    while ( bytesRead != -1 ) {
      out.write( buf, 0, bytesRead );
      bytesRead = in.read( buf, 0, buf.length );
    }
  }
  
}
