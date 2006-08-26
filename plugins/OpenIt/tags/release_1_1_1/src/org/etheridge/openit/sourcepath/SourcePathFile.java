/*
 * OpenIt jEdit Plugin (SourcePathFile.java) 
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

import java.text.DecimalFormat;

public abstract class SourcePathFile implements Comparable
{
  // the SourcePath that this file is a part of
  private SourcePathElement mSourcePathElement;
  
  // the file name (without extension)
  private String mFileName;
  
  // the full file name (with extension)
  private String mFullFileName;
  
  // the file extension
  private String mFileExtension;
  
  // the file size
  private String mFileSize;
  
  // directory string
  private String mDirectoryString;
  
  // decimal formatter
  private static DecimalFormat msDecimalFormat;
  static {
    msDecimalFormat = new DecimalFormat();
    msDecimalFormat.setMaximumFractionDigits(2);
    msDecimalFormat.setGroupingSize(3);
    msDecimalFormat.setGroupingUsed(true);
  }
  
  /**
   * Factory/Creational method to create the correct subclass of SourcePathFile
   *
   * @return a new SourcePathFile if the file-type is allowed, otherwise null. 
   */
  public static SourcePathFile createSourcePathFile(SourcePathElement sourcePathElement, File file)
  {
    String fileName = file.getName().toLowerCase();
    if (fileName.endsWith(".java")) {
      return new JavaSourcePathFile(sourcePathElement, file);
    } else {
      return new OtherSourcePathFile(sourcePathElement, file);
    }
  }
  
  
  public SourcePathFile(SourcePathElement sourcePathElement, File file)
  {
    mSourcePathElement = sourcePathElement;
    calculateAttributes(file);
  }
  
  /**
   * @return this file's extension, or null if it does not have one.
   */
  public String getFileExtension()
  {
    return mFileExtension;
  }
  
  /**
   * @return the directory this file is in
   */
  public String getDirectoryString()
  {
    return mDirectoryString;
  }
  
  /**
   * @return the file's length in bytes
   */
  public String getFileSize()
  {
    return mFileSize;
  }
  
  public String getName()
  {
    return mFileName;
  }
  
  public String getFullName()
  {
    return mFullFileName;
  }
  
  public SourcePathElement getSourcePathElement()
  {
    return mSourcePathElement;
  }

  //
  // Private Helper Methods
  //
  
  private void calculateAttributes(File file)
  {
    // calculate file extension
    int extensionIndex = file.getName().lastIndexOf(".");
    if (extensionIndex < 0) {
      mFileExtension = null;
    } else {
      mFileExtension = file.getName().substring(extensionIndex + 1);
    }
    
    // set full file name
    mFullFileName = file.getName();
    
    // set filename (without extension)
    mFileName = mFullFileName;
    if (mFileExtension != null) {
      mFileName = file.getName().substring(0, extensionIndex);
    }
    
    // set file size
    long sizeInBytes = file.length();
    if (sizeInBytes >= 1000000) {
      mFileSize = msDecimalFormat.format((float) sizeInBytes / 1000000) + "M";
    } else if (sizeInBytes >= 10000) {
      mFileSize = msDecimalFormat.format((float) sizeInBytes / 1000) + "kb";
    } else {
      mFileSize = msDecimalFormat.format(sizeInBytes) + "b";
    }
    
    // set directory string
    mDirectoryString = file.getAbsolutePath();
  }
  
  //
  // Comparable Interface
  //
  
  public int compareTo(Object o)
  {
    SourcePathFile compareObject = (SourcePathFile) o;
    return getFullName().compareTo(compareObject.getFullName());
  }
}
