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
 
public class CacheStore {


    /**
    A convenient way to take a file and generate a unique cache directory for it
    to use.  The directory it returns is created on disk.

    @param  base The base for the file cache
    @param  reference The file to use to determine how to generate the cache
                      namespace
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public synchronized static String getCacheDirectory(File basedir, File reference) {

        //StaticLogger.log( "Reference is: " + reference.getAbsolutePath() );
       


        if (basedir.exists() == false) {
            basedir.mkdir();
        }

        String root = "";

        if (reference.isDirectory()){
            root = reference.getAbsolutePath();
        } else {
            root = reference.getParent();
        }

        root = root.substring( 
            root.indexOf( System.getProperty("file.separator") ) + 1,  
            root.length() );
        
        //StaticLogger.log("Root is: " + root);

        File cachedir = new File( basedir.getAbsolutePath() + 
            System.getProperty("file.separator") +
            root );
                          

        //StaticLogger.log( "Cache is: " + cachedir.getAbsolutePath() );
                          
        if (cachedir.exists() == false) {
            //StaticLogger.log( "mkdir" );
            cachedir.mkdirs();
        }
        
        return cachedir.getAbsolutePath();
    }

}
