/*
 * TagListCellRenderer.java
 * Copyright (c) 2001 Kenrick Drew
 * kdrew@earthlink.net
 *
 * This file is part of TagsPlugin
 *
 * TagsPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * TagsPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tags;

import java.io.*;
import java.lang.*;
import java.lang.System.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class TagListCellRenderer extends JPanel implements ListCellRenderer 
{
  /***************************************************************************/
  protected JPanel tagNameAndFilePanel_ = null;
  protected JLabel indexLabel_ = null;
  protected JLabel tagLabel_ = null;
  protected JLabel pathLabel_ = null;
  protected JLabel fileLabel_ = null;
  protected JLabel searchString_ = null;

  protected String fileSeperator;
  
  /***************************************************************************/
  public TagListCellRenderer() 
  {
    super();
    
    fileSeperator = System.getProperty("file.separator");
    
    // create components
    indexLabel_ = new JLabel();
    tagLabel_ = new JLabel();
    pathLabel_ = new JLabel();
    fileLabel_ = new JLabel();
    searchString_ = new JLabel();
    tagNameAndFilePanel_ = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    
    // setup
    Font plain = new Font("Courier New", Font.PLAIN, 12);
    Font bold  = new Font("Courier New", Font.BOLD, 12);
    indexLabel_.setFont(plain); 
    tagLabel_.setFont(bold);
    pathLabel_.setFont(plain);
    fileLabel_.setFont(bold);
    searchString_.setFont(plain);
    
    //setOpaque(false);
    
    // Layout
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, tagNameAndFilePanel_);
      tagNameAndFilePanel_.add(indexLabel_);
      tagNameAndFilePanel_.add(tagLabel_);
      tagNameAndFilePanel_.add(pathLabel_);
      tagNameAndFilePanel_.add(fileLabel_);
    add(BorderLayout.SOUTH, searchString_);
  }

  /***************************************************************************/
  public Component getListCellRendererComponent(JList list, Object value,
                                                int index, boolean isSelected,
                                                boolean cellHasFocus) {
    
    TagLine tagLine = (TagLine) value;
    
    if (tagLine.index_ <= 9)
      indexLabel_.setText(" " + tagLine.index_ + ": ");
    else
      indexLabel_.setText("    ");
    tagLabel_.setText(tagLine.tag_);
    File file = new File(tagLine.definitionFile_);
    pathLabel_.setText("  " + file.getParent() + fileSeperator);
    // space is added so that edge of popup is immediately next to end of label
    fileLabel_.setText(file.getName() + " ");
    if (tagLine.origSearchString_ != null)
      searchString_.setText("     " + tagLine.origSearchString_.trim() + " ");
    else
      searchString_.setText("     Line:  " + tagLine.definitionLineNumber_);
      
    Color background = (isSelected ? list.getSelectionBackground()
                                                       : list.getBackground());
    Color foreground = (isSelected ? list.getSelectionForeground()
                                                       : list.getForeground());
    // set backgrounds on panels
    setBackground(background);
    tagNameAndFilePanel_.setBackground(background);
    
    // set foregrounds on text labels
    indexLabel_.setForeground(foreground);
    tagLabel_.setForeground(foreground);
    pathLabel_.setForeground(Color.blue);
    fileLabel_.setForeground(Color.blue);
    searchString_.setForeground(foreground);
    
    return this;
  }
}
