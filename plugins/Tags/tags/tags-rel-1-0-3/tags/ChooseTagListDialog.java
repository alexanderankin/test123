/*
 * ChooseTagListDialog.java
 * Copyright (c) 2001 Kenrick Drew, Slava Pestov
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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.awt.Toolkit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;

class ChooseTagListDialog extends JDialog 
{
  /***************************************************************************/
  ChooseTagList tagList_;
  View view_;
  boolean canceled_ = false;
  
  /***************************************************************************/
  public ChooseTagListDialog(TagsParser parser, View view, boolean openNewView)
  {
    super(view, 
        (view != null) ? jEdit.getProperty("tag-collision-dlg.title") : 
                         "Tag Collisions",
        true);

    view_ = view;
        
    // create components
    tagList_ = parser.getCollisionListComponent(view_);
    JScrollPane scrollPane = new JScrollPane(tagList_, 
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JLabel label = new JLabel(
          (view_ != null) ? jEdit.getProperty("tag-collision-dlg.label") : 
                            "Choose tag:");
    JButton ok = new JButton(
             (view_ != null) ? jEdit.getProperty("options.tags.tag-ok.label") : 
                               "OK");
    JButton cancel = new JButton(
          (view_ != null) ? jEdit.getProperty("options.tags.tag-cancel.label") : 
                            "Cancel");
    
    JPanel contentPanel_ = new JPanel(new BorderLayout(0,5));
    JPanel buttonPanelFlow = new JPanel(new FlowLayout());
    JPanel buttonPanelGrid = new JPanel(new GridLayout(1,0,5,0));
    
    // setup
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    getRootPane().setDefaultButton(ok);
    ok.addActionListener(okButtonListener_);
    cancel.addActionListener(cancelButtonListener_);
    tagList_.addKeyListener(keyListener_);
    tagList_.addMouseListener(mouseListener_);
    addKeyListener(keyListener_);
    
    // place
    getContentPane().setLayout(new BorderLayout());

    getContentPane().add(contentPanel_, BorderLayout.CENTER);
      contentPanel_.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
			contentPanel_.add(label, BorderLayout.NORTH);
			contentPanel_.add(scrollPane, BorderLayout.CENTER);
			contentPanel_.add(buttonPanelFlow, BorderLayout.SOUTH);
				buttonPanelFlow.add(buttonPanelGrid);
					buttonPanelGrid.add(ok);
					buttonPanelGrid.add(cancel);
				
    showDialog();
    
    if (!canceled_)
    {
      Tags.processTagLine(tagList_.getSelectedIndex(), view_, openNewView, 
                          parser.getTag());

    }
  }

  /***************************************************************************/
  protected void showDialog() {
    pack();
    
    if (view_ != null)
      setLocationRelativeTo(view_);
      
    tagList_.requestFocus();
    
    show();
  }

  /*+*************************************************************************/
  protected ActionListener okButtonListener_ = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      dispose();
    }
  };
  
  /*+*************************************************************************/
  protected ActionListener cancelButtonListener_ = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      canceled_ = true;
      dispose();
    }
  };

  /*+*************************************************************************/
  protected KeyListener keyListener_ = new KeyListener() {
    public void keyPressed(KeyEvent e) 
    {
      e = KeyEventWorkaround.processKeyEvent(e);
      if(e == null)
        return;
    
      switch (e.getKeyCode()) 
      {
        case KeyEvent.VK_ESCAPE:
          cancelButtonListener_.actionPerformed(null);
          break;
      }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) 
    {
      e = KeyEventWorkaround.processKeyEvent(e);
      if(e == null)
        return;

      switch (e.getKeyChar()) 
      {
        case KeyEvent.VK_1:
        case KeyEvent.VK_2:
        case KeyEvent.VK_3:
        case KeyEvent.VK_4:
        case KeyEvent.VK_5:
        case KeyEvent.VK_6:
        case KeyEvent.VK_7:
        case KeyEvent.VK_8:
        case KeyEvent.VK_9:
          if (getFocusOwner() != tagList_)
            return;
              
          /* There may actually be more than 9 items in the list, but since
           * the user would have to scroll to see them either with the mouse
           * or with the arrow keys, then they can select the item they want
           * with those means.
           */
          int selected = Character.getNumericValue(e.getKeyChar()) - 1;
          if (selected >= 0 && selected < tagList_.getModel().getSize())
          {
            tagList_.setSelectedIndex(selected);
            tagList_.ensureIndexIsVisible(selected);
            dispose();
          }
          break;
      }
    }
  };
  
  /***************************************************************************/
  protected MouseListener mouseListener_ = new MouseAdapter() {
    public void mouseClicked(MouseEvent e) 
    {
      if (e.getClickCount() == 2) 
      {
        int selected = tagList_.locationToIndex(e.getPoint());
        tagList_.setSelectedIndex(selected);
        tagList_.ensureIndexIsVisible(selected);
        dispose();
      }
    }
  };
  
}
