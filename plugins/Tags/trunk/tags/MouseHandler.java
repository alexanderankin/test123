/*
 * MouseHandler.java
 * Copyright (c) 2001, 2002 Kenrick Drew
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
import java.lang.System.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;

final class MouseHandler extends MouseAdapter implements MouseMotionListener
{
  /***************************************************************************/
  public void mouseReleased(MouseEvent e) {}
  
  /***************************************************************************/
  public void mouseDragged(MouseEvent e) {}
  
  /***************************************************************************/
  public void mouseMoved(MouseEvent e) 
  { 
    Point p = e.getPoint();
    SwingUtilities.convertPointToScreen(p, e.getComponent()); 
    Tags.setMousePosition(p);
    p = null;
  }
}
