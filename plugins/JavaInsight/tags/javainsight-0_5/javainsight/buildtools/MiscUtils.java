/*
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

package javainsight.buildtools;


import java.io.File;
import org.gjt.sp.jedit.MiscUtilities;


/**
 * Miscelleanous utilities.
 *
  * @author Kevin A. Burton
  * @version $Id$
  */
public class MiscUtils {

    /**
     * A cross platform (JDK and OS) way to determine the temp directory.
     *
     * <code>java.io.tmpdir</code> is only available on JDK 1.2 and there
     * is no real easy way to figure this out under JDK 1.1.8
     */
    public static String getTempDir() {
        String os_name = System.getProperty("os.name");

        // check for Windows 95, 98, ME, NT:
        if (os_name.toLowerCase().indexOf("windows") >= 0) {
            if (new File("C:\\WINNT\\TEMP").exists())
                return "C:\\WINNT\\TEMP";
            if (new File("C:\\WINDOWS\\TEMP").exists())
                return "C:\\WINDOWS\\TEMP";
            if (new File("C:\\TEMP").exists())
                return "C:\\TEMP";
        }

        // check for Unix:
        if (File.separatorChar == '/') {
            // maybe we're unix
            if (new File("/tmp").exists())
                return "/tmp";
        }

        // I give up. Return a temp folder in the user's home:
        String user_home = System.getProperty("user.home");
        return MiscUtilities.constructPath(user_home, "tmp");
    }


    /**
     * Get a temp dir for a certain product.
     *
     * <B>Implementation note:</B> This implementation just returns
     * concats the system temp dir with the product string, resulting in
     * a new subdirectory.
     *
     * @author Dirk Moebius
     */
    public static String getTempDir(String product) {
        return MiscUtilities.constructPath(getTempDir(), product);
    }

}
