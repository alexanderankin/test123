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
import javax.swing.*;
import java.util.*;

public class LaTeXDockable  extends JPanel{

//  extends JTabbedPane {

  //~ Instance/static variables ...............................................

  Buffer buffer;
  View view;
  private JComboBox nav_list = new JComboBox();
  
  //~ Constructors ............................................................

  /**
   * Creates a new LaTeXDockable object.
   * 
   * @param v ¤
   * @param b ¤
   */
  public LaTeXDockable() {
    
    ArrayList nav = new ArrayList(NavigationList.getNavigationData());
    nav_list = new JComboBox(nav.toArray());
    
    add(new JLabel("Under Construction!"));
    add(nav_list);
  }
  
  public JComboBox getComboBox(){
    return nav_list;
  }
  
}
