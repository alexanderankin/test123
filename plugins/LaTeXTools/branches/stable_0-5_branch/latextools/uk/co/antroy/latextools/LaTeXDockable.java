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
import uk.co.antroy.latextools.macros.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.*;
import javax.swing.JComboBox;
import javax.swing.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class LaTeXDockable  extends AbstractToolPanel {

  //~ Instance/static variables ...............................................

  private JComboBox nav_list = new JComboBox();
  private static final LaTeXDockable instance = new LaTeXDockable();
  private JComponent infoPanel = new JLabel("");
  private JLabel infoLabel = new JLabel("");

  private JLabel navig;
  
  //~ Constructors ............................................................

  private LaTeXDockable() {
    super(jEdit.getActiveView(), jEdit.getActiveView().getEditPane().getTextArea().getBuffer(), "LaTeX Tools"); 
    ArrayList nav = new ArrayList(NavigationList.getNavigationData());
    nav_list = new JComboBox(nav.toArray());
    
    navig = new JLabel("Structure Browser: show");
    
    JPanel controls = new JPanel();
    controls.setAlignmentX(Component.LEFT_ALIGNMENT);
    controls.add(navig);
    controls.add(nav_list);
    
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.add(controls);
    this.add(infoPanel);
    
    LaTeXDockableListener listener = new LaTeXDockableListener();
    nav_list.addActionListener(listener);
    refresh();
  }
  
  public void refresh(){
      view = jEdit.getActiveView();
      buffer = jEdit.getActiveView().getEditPane().getTextArea().getBuffer();
      
      if (!ProjectMacros.isTeXFile(buffer)){
          setInfoPanel(new JLabel(""), "<html><b>Not a TeX File.");
      }else{
          ProjectMacros.showInformation(view, buffer);
      }
      
  }
  
  public static LaTeXDockable getInstance(){
      return instance;
  }
  
  public void reload(){}
 
  public JComboBox getComboBox(){
    return nav_list;
  }
  
  public JComponent getInfoPanel(){
    return infoPanel;
  }

  public void setInfoPanel(JComponent panel, String label){
      this.remove(infoPanel);
      this.remove(infoLabel);
      this.infoPanel = panel;
      
      Dimension d = new Dimension(300,300);
      infoPanel.setPreferredSize(d);
      infoLabel = new JLabel("<html><font color='#0000aa'><b>" + label);
      this.add(infoLabel);
      this.add(infoPanel);
  }
  
  private class LaTeXDockableListener implements ActionListener {
    
    private LaTeXDockableListener(){
    }
    
    public void actionPerformed(ActionEvent e) {
      LaTeXPlugin.parse(jEdit.getActiveView(),true);
    }
  }
}
