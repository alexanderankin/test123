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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LaTeXDockable  extends JPanel{

//  extends JTabbedPane {

  //~ Instance/static variables ...............................................

  private JComboBox nav_list = new JComboBox();
  public static final String PARSE_LABELS = "Labels";
  public static final int LABELS = 0;
  public static final int NAVIGATION = 1;
  public static final int BIBLIOGRAPHY = 2;
  private int selected;
  private JRadioButton refs;
//  private JRadioButton navig;
  private JLabel navig;
  
  //~ Constructors ............................................................

  public LaTeXDockable() {
    
    ArrayList nav = new ArrayList(NavigationList.getNavigationData());
    nav_list = new JComboBox(nav.toArray());
    
    refs = new JRadioButton(PARSE_LABELS);
//    navig = new JRadioButton("Select LaTeX Navigation List:");
    navig = new JLabel("Select LaTeX Navigation List:");
//    add(refs);
    add(navig);
    add(nav_list);
    
//    ButtonGroup group = new ButtonGroup();
//    group.add(refs);
//    group.add(navig);
    
//    navig.setSelected(true);
    selected = NAVIGATION;
    
    LaTeXDockableListener listener = new LaTeXDockableListener();
//    refs.addActionListener(listener);
//    navig.addActionListener(listener);
    nav_list.addActionListener(listener);
  }
  
  public int getSelectedButton(){
    return selected;
  }
  
  public JComboBox getComboBox(){
    return nav_list;
  }
  
  private class LaTeXDockableListener implements ActionListener {
    
    private LaTeXDockableListener(){
    }
    
    public void actionPerformed(ActionEvent e) {
      
//      if (e.getSource().equals(refs)){
//        selected = LABELS;
//      }else{
//        selected = NAVIGATION;
//      }
      
      LaTeXPlugin.parse(jEdit.getActiveView(),true);
    }
  }
}
