/*
 * XSearchPlugin.java - Plugin for a number of text related functions
 * Copyright (C) 1999, 2001 mike dillon
 *
 * Revised for jEdit 4.0 by John Gellene
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
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

import java.util.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class XSearchPlugin extends EditPlugin
{
  public void createMenuItems(Vector menuItems)
  {
    menuItems.addElement(GUIUtilities.loadMenu("xsearch"));
		XSearchAndReplace.load();
  }

}
