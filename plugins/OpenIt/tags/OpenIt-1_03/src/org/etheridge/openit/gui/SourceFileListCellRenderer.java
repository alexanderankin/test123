/*
 * OpenIt jEdit Plugin (SourceFileListCellRenderer.java) 
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

package org.etheridge.openit.gui;

import java.awt.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ToolTipManager;

import org.etheridge.openit.sourcepath.JavaSourcePathFile;
import org.etheridge.openit.sourcepath.SourcePathFile;
import org.etheridge.openit.OpenItProperties;

import org.gjt.sp.jedit.jEdit;

/**
 * Renders items in the source file list
 */
public class SourceFileListCellRenderer extends DefaultListCellRenderer 
{
  // static property strings
  private static String msUnknownLookupProperty = 
    jEdit.getProperty("openit.FindFileWindow.ImagePropertyLookup.unknown", "");
  private static String msOtherLookupProperty = 
    jEdit.getProperty("openit.FindFileWindow.ImagePropertyLookup.other", "");
  
  // image map
  private static Map msImageMap;
  static {
    loadImageMap();
  }
  
  public Component getListCellRendererComponent
    (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
  {
    Component comp = 
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    
    if (comp instanceof JLabel) {
      StringBuffer showBuffer = new StringBuffer();
          
      if (value instanceof SourcePathFile) {
          SourcePathFile file = (SourcePathFile) value;
          
          // ALWAYS set the name first
          String fileName = 
            (jEdit.getBooleanProperty(OpenItProperties.DISPLAY_EXTENSIONS, true) 
              ? file.getFullName() : file.getName());
          showBuffer.append(fileName + " ");
          
          // show the icon if we need to
          if (jEdit.getBooleanProperty(OpenItProperties.DISPLAY_ICONS, true)) {
            ((JLabel)comp).setIcon(getImageIcon(file));
          }
          
          // if the file is a java file, then show the package & direcotry if 
          // required.
          if (file instanceof JavaSourcePathFile) {
            if (jEdit.getBooleanProperty(OpenItProperties.JAVA_FILE_DISPLAY_PACKAGES, true)) {
              showBuffer.append("(" + ((JavaSourcePathFile)file).getPackageName() + ") ");
            }
            
            if (jEdit.getBooleanProperty(OpenItProperties.JAVA_FILE_DISPLAY_DIRECTORIES, false)) {
              showBuffer.append("[" + file.getDirectoryString() + "] ");
            }
          } else { // not a java file
            // show directory if user wants          
            if (jEdit.getBooleanProperty(OpenItProperties.DISPLAY_DIRECTORIES, false)) {
              showBuffer.append("[" + file.getDirectoryString() + "] ");
            }  
          }
                  
          // show size if user wants
          if (jEdit.getBooleanProperty(OpenItProperties.DISPLAY_SIZE, false)) {
            showBuffer.append("(" + file.getFileSize() + ")");  
          }
      }
      
      ((JLabel)comp).setText(showBuffer.toString());
    }
       
    return comp;
  }
  
  //
  // Private Helper Methods
  //
  
  /**
   * Return the image to use to render the specified source path file.
   */
  private ImageIcon getImageIcon(SourcePathFile sourcePathFile)
  {
    String fileExtension = sourcePathFile.getFileExtension();

    // if there is no file extension, use the "unknown" icon
    if (fileExtension == null) {
      return (ImageIcon) msImageMap.get(msUnknownLookupProperty);
    } 

    // otherwise, the file has a file extension, so if it is one of the ones
    // we know about, use the image, otherwise use the "other" icon.    
    ImageIcon icon = (ImageIcon) msImageMap.get(fileExtension);
    icon = (icon == null ? (ImageIcon) msImageMap.get(msOtherLookupProperty) : icon);
    return icon;
  }
  
  /**
   * Loads images from properties file
   */
  private static void loadImageMap()
  {
    msImageMap = new HashMap();
    
    // get the list of files types
    String fileTypes = jEdit.getProperty("openit.FindFileWindow.ImageFileTypes", "");
    String prefixProperty = jEdit.getProperty("openit.FindFileWindow.ImagePropertyPrefix", "");
    StringTokenizer tokenizer = new StringTokenizer(fileTypes, " ");
    while (tokenizer.hasMoreTokens()) {
      // each token will be someting like "java", or "xml"
      String currentToken = tokenizer.nextToken();
      String imageFileName = jEdit.getProperty(prefixProperty + currentToken);
      msImageMap.put(currentToken,
        new ImageIcon(SourceFileListCellRenderer.class.getResource(imageFileName)));
    }
  }
}
