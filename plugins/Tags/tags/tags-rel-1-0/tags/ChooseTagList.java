/*
 * ChooseTagList.java
 * Copyright (c) 2001 Kenrick Drew
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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import org.gjt.sp.jedit.*;

class ChooseTagList extends JList {
  
  /***************************************************************************/
	private TagsParser parser_;
  private Vector tagIdentifiers_;
  private View view_;
  
  /***************************************************************************/
	public ChooseTagList(View view, TagsParser parser) {
    super();

    view_ = view;
    parser_ = parser;
    
    // Setup items for JList
    tagIdentifiers_ = parser_.getTagLines();

    setListData(tagIdentifiers_);
    
    // Setup JList
    /* Generally 8 is the magic number for the number of visible items/rows in 
     * a list or menu, but we do 8 to 12 b/c each item is actually 2 or 3 rows
     */
    setVisibleRowCount(Math.min(tagIdentifiers_.size(),4));  
    setSelectedIndex(0);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Setup renderer
    setCellRenderer(new TagListCellRenderer());
	}

}
