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

 
package buildtools; 

import java.io.File;

public class MiscUtils {

    /**
    Given a string... replaces all occurences of "find" with "replacement" in "original"

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public static String globalStringReplace(String original, String find, String replacement) {

        StringUtil buffer = new StringUtil( original );
        
        
        int space_location = buffer.toString().indexOf(find);

        while(space_location != -1) {

            int start = space_location;
            int end = space_location + find.length();

            buffer.replace(start, end, replacement);

            //this speed could be improved by starting off where the last string was found...
            //this is why it starts off from space_location.. the length of the string you are finding.. plus 1 

            //space_location = buffer.toString().indexOf(find, space_location + find.length() + 1);
            space_location = buffer.toString().indexOf(find, space_location + find.length() + 1);
      }

      return buffer.toString();

    }

    
    /**
    A cross platform (JDK and OS) way to determine the temp directory.  
    java.io.tmpdir is only available on JDK 1.2 and there is no real easy way to
    figure this out under JDK 1.1.8
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public static String getTempDir() {
        
        String separator = System.getProperty("file.separator");
        
        if ( separator.equals("/") && new File("/tmp").exists() ) {
            return "/tmp";
        } else if ( separator.equals("\\") && new File("c:\\temp").exists() ) {
            return "c:\\temp";
        } else {
            return "/tmp";
        }
        
    }

}
