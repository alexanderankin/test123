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
import org.gjt.sp.jedit.msg.*;
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
  private static final String DISPLAY_IMAGE = "View Image";
  private static final String INFO          = "Information";
  private static final String DUPLICATES    = "Duplicates";
  private static final String ORPHANS       = "Orphans";
  private static final Icon DISPLAY_IMAGE_ICON = UtilityMacros.getIcon("image.png");
  private static final Icon INFO_ICON          = UtilityMacros.getIcon("info.png");
  private static final Icon DUPLICATES_ICON    = UtilityMacros.getIcon("duplicate.png");
  private static final Icon ORPHANS_ICON       = UtilityMacros.getIcon("orphan.png");
      

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
    controls.add(new JButton(new ButtonAction(INFO, INFO_ICON))); 
    controls.add(new JButton(new ButtonAction(DISPLAY_IMAGE, DISPLAY_IMAGE_ICON)));
    controls.add(new JButton(new ButtonAction(DUPLICATES, DUPLICATES_ICON)));
    controls.add(new JButton(new ButtonAction(ORPHANS, ORPHANS_ICON))); 
    
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.add(controls);
    this.add(infoPanel);
    this.setPreferredSize(new Dimension(500,300));

    LaTeXDockableListener listener = new LaTeXDockableListener();
    nav_list.addActionListener(listener);
  }
  
  public void refresh(){
      view = jEdit.getActiveView();
      buffer = jEdit.getActiveView().getEditPane().getTextArea().getBuffer();
      
      
      if (!ProjectMacros.isTeXFile(buffer)){
          this.setInfoPanel(new JLabel(""), "<html><b>Not a TeX File.");
      }else{
          ProjectMacros.showInformation(view, buffer);
      }
      super.refresh();
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
  
    public synchronized void setInfoPanel(JComponent panel, String label){
        this.remove(infoPanel);
        this.remove(infoLabel);
        this.infoPanel = panel;
        
        Dimension d = new Dimension(300,300);
        infoPanel.setPreferredSize(d);
        infoLabel = new JLabel("<html><font color='#0000aa'><b>" + label);
        this.add(infoLabel);
        this.add(infoPanel);
        this.sendUpdateEvent("latextools-navigation-dock");
    }
  
  private class LaTeXDockableListener implements ActionListener {
    
    private LaTeXDockableListener(){
    }
    
    public void actionPerformed(ActionEvent e) {
      LaTeXPlugin.parse(jEdit.getActiveView(),true);
    }
  } 
  
  private class ButtonAction extends AbstractAction {
    
     
    private ButtonAction(String name, Icon icon){
        super(name, icon);
        //putValue(Action.LONG_DESCRIPTION, name);
        //putValue(Action.SMALL_ICON, icon);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (!ProjectMacros.isTeXFile(buffer)){
            setInfoPanel(new JLabel(""), "<html><b>Not a TeX File.");
            return;
        }
        
        String command = e.getActionCommand();
        
        if (command.equals(DISPLAY_IMAGE)){
            ImageViewer.showInInfoPane(view, buffer);
        }
        else if (command.equals(DUPLICATES)){
            ErrorFindingMacros.displayDuplicateLabels(view, buffer);        
        }
        else if (command.equals(ORPHANS)){
            ErrorFindingMacros.displayOrphanedRefs(view, buffer);            
        }
        else if (command.equals(INFO)){
            ProjectMacros.showInformation(view, buffer);           
        }
    }
  }

}
