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

import java.util.Vector;

/**
Misc utils for handing source files.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class FileUtils {



    /**
    return the extension of a file or null of it doesn't exist.

    If the extension is anything past the right of the last ".".  So for a file 
    like "test.java" the extension would be "java"
    
    @return the extension of a file or null of it doesn't exist.
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static String getExtension(String file) {
        int begin = file.lastIndexOf(".");
        if (begin < 0) {
            return null;
        } else {
            
            return file.substring(begin + 1, file.length());
            
        }
    }

    /**
    <p>Given a directory and an array of extensions... return an array of compliant
    files.
    
    <p>The given extensions should be like "java" and not like ".java"
    
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public static String[] getFilesFromExtension(String directory, String[] extensions) {

        
        //
        Vector files = new Vector();
        
        //get the 
        java.io.File currentDir = new java.io.File(directory);

        String[] unknownFiles = currentDir.list();

        if (unknownFiles == null) {
            return new String[0];
        }
        
        for (int i = 0;i < unknownFiles.length;++i) {
            String currentFileName = directory + System.getProperty("file.separator") + unknownFiles[i];
            java.io.File currentFile = new java.io.File(currentFileName);

            if (currentFile.isDirectory()) {


                //ignore all CVS directories...
                if ( currentFile.getName().equals("CVS") ) {
                    continue;
                }


                //ok... transverse into this directory and get all the files... then combine
                //them with the current list.

                String[] fetchFiles = getFilesFromExtension(currentFileName, extensions);
                files = blendFilesToVector( files, fetchFiles);
                
            } else {
                //ok... add the file
    
                String add = currentFile.getAbsolutePath();
                if ( isValidFile( add, extensions ) ) {
                    files.addElement( add );
                    
                }

            }
        }

        //ok... move the Vector into the files list...

        String[] foundFiles = new String[files.size()];
        files.copyInto(foundFiles);
        
        return foundFiles;

    }
    

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    private static Vector blendFilesToVector(Vector v, String[] files) {
        
        for (int i = 0; i < files.length; ++i) {
            v.addElement(files[i]);
        }
        
        return v;
    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    private static boolean isValidFile(String file, String[] extensions) {


        String extension = FileUtils.getExtension(file);
        if (extension == null) {
            return false;
        }
        
        //ok.. now that we have the "extension" go through the current know
        //excepted extensions and determine if this one is OK.
        
        for (int i = 0; i < extensions.length; ++i) {
            if (extensions[i].equals(extension)) 
                return true;
        }

        return false;
        
    }


}
 
