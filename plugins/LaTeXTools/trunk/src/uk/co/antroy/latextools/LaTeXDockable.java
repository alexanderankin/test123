package uk.co.antroy.latextools; 

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


/*:folding=indent:
 * AbstractToolPanel.java - Abstract class representing a tool panel.
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
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;


public class LaTeXDockable
  extends JTabbedPane {

  //~ Instance/static variables ...............................................

  Buffer buffer;
  JPanel jp = new JPanel();
  Action refresh;
  Action reload;
  JTabbedPane tp = new JTabbedPane(JTabbedPane.BOTTOM);
  View view;

  //~ Constructors ............................................................

  /**
   * Creates a new LaTeXDockable object.
   * 
   * @param v ¤
   * @param b ¤
   */
  public LaTeXDockable(View v, Buffer b) {
    this.buffer = b;
    this.view = v;
    this.setTabPlacement(JTabbedPane.BOTTOM);
    add(new NavigationPanel(view, buffer));
    add(new BibTeXPanel(view, buffer));
    add(new ReferencePanel(view, buffer));
  }
}
