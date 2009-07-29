/*
 * LazyImporter jEdit Plugin (FileUtilities.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
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
 
package org.etheridge.common.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class FileUtilities
{
  public static File findFile(File baseDirectory, String fileName, FileFilter filter) throws IOException 
  {
    File[] filesInDirectory = baseDirectory.listFiles(filter);
    for (int i = 0; i < filesInDirectory.length; i++) {
      if (filesInDirectory[i].isDirectory()) {
        File foundFile = findFile(filesInDirectory[i], fileName, filter);
        if (foundFile != null) {
          return foundFile;
        }
      } else {
        // must be a file - if its the one we are looking for return it
        String currentFileName = filesInDirectory[i].getName().toLowerCase();
        if (currentFileName.equals(fileName)) {
          return filesInDirectory[i];
        }
      }
    }
    
    // guard - will get to here if the file is not found, adn there are no
    // more directories to search.
    return null;
  }
}
