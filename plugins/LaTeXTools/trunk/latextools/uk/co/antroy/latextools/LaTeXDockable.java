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

package uk.co.antroy.latextools; 

import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.*;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LaTeXDockable  extends JPanel{

  //~ Instance/static variables ...............................................

  private JComboBox nav_list = new JComboBox();
  public static final LaTeXDockable instance = new LaTeXDockable();

  private JLabel navig;
  
  //~ Constructors ............................................................

  private LaTeXDockable() {
    
    ArrayList nav = new ArrayList(NavigationList.getNavigationData());
    for (Iterator it = nav.iterator(); it.hasNext();){
        NavigationList nl = (NavigationList) it.next();
        for (Iterator it2 = nl.iterator(); it2.hasNext();){
        }
    }
    nav_list = new JComboBox(nav.toArray());
    
    navig = new JLabel("Select LaTeX Navigation List:");
    add(navig);
    add(nav_list);
    
    LaTeXDockableListener listener = new LaTeXDockableListener();
    nav_list.addActionListener(listener);
  }
  
  public JComboBox getComboBox(){
    return nav_list;
  }
  
  private class LaTeXDockableListener implements ActionListener {
    
    private LaTeXDockableListener(){
    }
    
    public void actionPerformed(ActionEvent e) {
      LaTeXPlugin.parse(jEdit.getActiveView(),true);
    }
  }
}
