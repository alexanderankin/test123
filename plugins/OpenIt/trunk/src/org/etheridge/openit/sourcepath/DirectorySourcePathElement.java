/*
 * OpenIt jEdit Plugin (DirectorySourcePathElement.java) 
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

package org.etheridge.openit.sourcepath;

import java.io.File;

/**
 * A directory element in a source path (ie. D:\source)
 */
public class DirectorySourcePathElement extends SourcePathElement
{
  private boolean mIsLink = false;
  
  public DirectorySourcePathElement(String elementName)
  {
    super(elementName);
  }
  
  protected void loadFiles()
  {
    try {
      // get the name and attempt 
      File file = new File(getName());

      if (file.isDirectory()) {
        findSourceFilesInDirectory(file, file);
      }
      
      // determine whether or not the directory is a link (ie. symbolic link)
      mIsLink = !file.getCanonicalPath().equals(file.getAbsolutePath());
    } catch (Exception e) {
      // this should really log a message, but there is no logging, so 
      // a stack trace will do for now.
      e.printStackTrace();
    }
  }
  
  public boolean isLink()
  {
    return mIsLink;
  }
  
  //
  // Private Helper Methods
  //
  
  private void findSourceFilesInDirectory(File baseDirectory, File directory) 
  {
    // see if there are any subdirectories
    File[] fileList = directory.listFiles();
    
    // for each subdirectory, recursively call this method
    for (int i = 0; i < fileList.length; i++) {
      // if the current file is a directory call method recursively
      if (fileList[i].isDirectory()) {
        findSourceFilesInDirectory(baseDirectory, fileList[i]);
      } else {
        mSourceFiles.add(SourcePathFile.createSourcePathFile(this, fileList[i]));
      }
      
      fileList[i] = null;
    }
    
  }
}
