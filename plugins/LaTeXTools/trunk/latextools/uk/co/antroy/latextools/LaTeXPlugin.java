
/*
 * LaTeXPlugin.java
 * Copyright (C) 2002 Anthony Roy
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

package uk.co.antroy.latextools;

import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import uk.co.antroy.latextools.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;

import sidekick.*;

public class LaTeXPlugin
  extends SideKickPlugin {

   //~ Methods .................................................................

  /**
   * ¤
   * 
   * @param message ¤
   */
  public void handleMessage(EBMessage message) {

/*     if (message instanceof BufferUpdate) {

      BufferUpdate bu = (BufferUpdate) message;
      Buffer buff = bu.getBuffer();
      String name = buff.getName().toLowerCase();

      if (name.endsWith(".tex")) {

        if (bu.getWhat() == BufferUpdate.CREATED ||
				    bu.getWhat() == BufferUpdate.LOADED) {
          speedTeX.addBuffer(buff);
          Log.log(Log.MESSAGE, this, "Add Buffer");
        } 
				else if (bu.getWhat() == BufferUpdate.CLOSED) {
          speedTeX.removeBuffer(buff);
        }
      }

      return;
    }
 */
    //IDEA: Use this update to set the new buffer as the buffer to listen to (or null if not a tex file).
    // Add a BufferChangeListener (subclass the Adapter) to listen for typed events.
  }

}
