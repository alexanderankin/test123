/*
 * GenericChooseTagList.java
 * Copyright (c) 2001 Kenrick Drew, Slava Pestov
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

/* This is pretty much ripped from gui/CompleteWord.java */

package tags;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import org.gjt.sp.jedit.*;

public class GenericChooseTagList extends JList {
  
  /***************************************************************************/
	private TagsParser parser_;
  private Vector tagIdentifiers_;
  private View view_;
  
  protected int choosenIndex_ = -1;
  
  /***************************************************************************/
	public GenericChooseTagList(View view, TagsParser parser) {
    super();

    view_ = view;
    parser_ = parser;
    
    // Setup items for JList
    int size = parser_.getNumberOfFoundTags();
    tagIdentifiers_ = new Vector(size);
    for (int i = 0; i < size; i++)
      tagIdentifiers_.addElement(parser_.getCollisionChooseString(i));

    setListData(tagIdentifiers_);
    
    // Setup JList
    setVisibleRowCount(Math.min(tagIdentifiers_.size(),8));  
    setSelectedIndex(0);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Setup font
    String fontName = null;
    String sizeString = null;
    if (view_ != null) {
      fontName = jEdit.getProperty("tags.tag-collide-list.Font");
      sizeString = jEdit.getProperty("tags.tag-collide-list.font-size");
    }
    else {
      fontName = "Monospaced";
    }
    
    size = 12;
    try { size = Integer.parseInt(sizeString); }
    catch (NumberFormatException nfe) {
      size = 12;
    }
    
    Font font = new Font(fontName, Font.PLAIN, size);
    setFont(font);

	}

}
