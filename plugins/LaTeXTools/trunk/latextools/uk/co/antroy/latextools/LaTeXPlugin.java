
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

  //~ Instance/static variables ...............................................

  private SpeedTeX speedTeX;
  private LaTeXParser parser;
  //~ Methods .................................................................

  /**
   * Adds the appropriate menu items to the Plugins Menu.
   * 
   * @param menuItems Vector of menuitems in plugins menu.
   */
//  public void createMenuItems(Vector menuItems) {
//
//     TODO: Remove this method and add menu items to LaTeXTools.props
//
//    JMenu menu = GUIUtilities.loadMenu("latex-main-menu");
//    JMenu insertMenu = GUIUtilities.loadMenu("latex-insert-menu");
//    
//    menu.addSeparator();
//    menu.add(insertMenu);
//    menuItems.addElement(menu);
//  }
//
  /**
   * ¤
   * 
   * @param dialog ¤
   */
//  public void createOptionPanes(OptionsDialog dialog) {
//
///* TODO: Remove this method, and add options to the props file. eg.
//
//    plugin.templates.TemplatesPlugin.option-group=templates accelerators
//    options.templates.label=General
//    options.templates.code=new templates.TemplatesOptionPane();
//    options.accelerators.label=Accelerators
//    options.accelerators.code=new templates.AcceleratorOptionPane();
// */
//
//    
//    OptionGroup grp = new OptionGroup("latex");
//    grp.addOptionPane(new BibTeXOptionPane());
//    grp.addOptionPane(new ReferenceOptionPane());
//    grp.addOptionPane(new NavigationOptionPane());
//    dialog.addOptionGroup(grp);
//  }

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

  /**
   * ¤
   */
  public void start() {
//    parser = new LaTeXParser("latex_parser");
//    registerParser(parser);
//    speedTeX = new SpeedTeX();
  }

   public void stop() {
//    unregisterParser(parser);
  }

  private void popup(String s) {
    JOptionPane.showMessageDialog(null, s);
  }

  private void popup() {
    popup("Green Eggs and Ham");
  }
}
