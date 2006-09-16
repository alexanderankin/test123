/*
 * LazyImporter jEdit Plugin (JavaUtilities.java) 
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

public class JavaUtilities {
  
  public static String removeStringLiterals(String text) {
    StringBuffer buffer = new StringBuffer(text);
    
    // iterate through buffer, replacing string literals with spaces
    int position = 0;
    
    while (position < buffer.length()) {
      if (buffer.charAt(position) == '"') {
        int stringLiteralStart = position + 1;
        position++;
        while (buffer.charAt(position) != '"') {
          position++;
        }
        
        buffer.replace(stringLiteralStart, position, createEmptyString(position - stringLiteralStart));
      }
      position++;
    }
    
    return buffer.toString();
  }
  
  /**
   * Converts a / separated package name to a . separated package name
   */
  public static String convertPathPackageToDotPackage(String pathPackage) {
    char pathSeparatorChar = File.separatorChar;
    String dotPackage = pathPackage.replace(pathSeparatorChar, '.'); 
    
    // remove a dot at the beginning if there is one
    if (dotPackage.endsWith(".")) {
      dotPackage = dotPackage.substring(0, dotPackage.length() - 1);
    }

    // remove dot at the END if there is one        
    if (dotPackage.startsWith(".")) {
      return dotPackage.substring(1);
    }
    
    return dotPackage;
  }
  
  
  //
  // Private Helper Methods
  //
  
  private static String createEmptyString(int size) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < size; i++) {
      stringBuffer.append(' ');
    }
    return stringBuffer.toString();
  }
 
}
