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
  protected JLabel indexLabel_ = null;
  protected JLabel tagLabel_ = null;
  protected JLabel fileLabel_ = null;
  protected JLabel searchString_ = null;

  /***************************************************************************/
  public TagListCellRenderer() 
  {
    super();
    
    indexLabel_ = new JLabel();
    tagLabel_ = new JLabel();
    fileLabel_ = new JLabel();
    searchString_ = new JLabel();
    
    Font plain = new Font("Monospaced", Font.PLAIN, 12);
    Font bold  = new Font("Monospaced", Font.BOLD, 12);
    indexLabel_.setFont(plain); 
    tagLabel_.setFont(bold);
    fileLabel_.setFont(plain);
    searchString_.setFont(plain);
    
    //setOpaque(false);
    setLayout(new BorderLayout());
    
    add(BorderLayout.WEST, indexLabel_);
    add(BorderLayout.CENTER, tagLabel_);
    add(BorderLayout.EAST, fileLabel_);
    add(BorderLayout.SOUTH, searchString_);
  }

  /***************************************************************************/
  public Component getListCellRendererComponent(JList list, Object value,
                                                int index, boolean isSelected,
                                                boolean cellHasFocus) {
    
    TagLine tagLine = (TagLine) value;
    
    if (tagLine.index_ < 9)
      indexLabel_.setText(" " + tagLine.index_ + ": ");
    else
      indexLabel_.setText(String.valueOf(tagLine.index_) + ": ");
    tagLabel_.setText(tagLine.tag_);
    fileLabel_.setText("  " + tagLine.definitionFile_ + "  ");
    searchString_.setText("     " + tagLine.searchString_.trim());
    
    Color background = (isSelected ? list.getSelectionBackground()
                                                       : list.getBackground());
    Color foreground = (isSelected ? list.getSelectionForeground()
                                                       : list.getForeground());
    setBackground(background);
    indexLabel_.setForeground(foreground);
    tagLabel_.setForeground(foreground);
    fileLabel_.setForeground(Color.blue);
    searchString_.setForeground(foreground);
    
    return this;
  }
}
