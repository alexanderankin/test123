/*
 * ChooseTagListDialog.java
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
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;

public class ChooseTagListDialog {
  public ChooseTagListDialog(TagsParser parser, View view, boolean openNewView) {
    Object messageObjects[] = new Object[2];
    messageObjects[0] = "Tag collisions:";
    messageObjects[1] = new ChooseTagList2(parser, view, openNewView);
    
    JOptionPane.showMessageDialog(view, messageObjects, "Tag Collisions",
		                              JOptionPane.PLAIN_MESSAGE);
  }
}
