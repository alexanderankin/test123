/*
 * TagsEnterTagPanel.java
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
import java.lang.*;
import java.lang.System.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.util.Log;

class TagsEnterTagPanel extends JPanel 
{
  static final boolean TESTING = true;
  
  /***************************************************************************/
  protected JLabel            textFieldLabel_;
  protected HistoryTextField  tagFuncTextField_;
  protected JCheckBox         otherWindowCheckBox_;
  protected JCheckBox         keepDialogCheckBox_;
  protected ChooseTagList     chooseTagList_;
  protected JButton           findButton_;
  
  View       view_;
  TagsParser parser_;
  
  /***************************************************************************/
  public TagsEnterTagPanel(String initialValue, View view, TagsParser parser) 
  {
    view_ = view;
    parser_ = parser;
    
    // tag input field
    textFieldLabel_ = new JLabel(
              jEdit.getProperty("tags.enter-tag-dlg.tag-to.label"));
    tagFuncTextField_ = new HistoryTextField("tags.enter-tag.history", false, 
                                             false);
    tagFuncTextField_.setColumns(16);
    tagFuncTextField_.addActionListener(findButtonListener_);
    textFieldLabel_.setLabelFor(tagFuncTextField_);
    
    JPanel p = new JPanel(new BorderLayout(5,5));                                         
    p.add(textFieldLabel_, BorderLayout.WEST);
    p.add(tagFuncTextField_, BorderLayout.CENTER);
    
    // find button
    JPanel fieldAndFindPanel = new JPanel(new BorderLayout(5,5));
    if (TESTING)
    {
      findButton_ = new JButton("Find");
      findButton_.setPreferredSize(
               new Dimension(findButton_.getPreferredSize().width , 
                             tagFuncTextField_.getPreferredSize().height));
      findButton_.addActionListener(findButtonListener_);
      fieldAndFindPanel.add(p, BorderLayout.CENTER);
      fieldAndFindPanel.add(findButton_, BorderLayout.EAST);
    }
    
    // open in other window check box                                             
    otherWindowCheckBox_ = new JCheckBox(
              jEdit.getProperty("tags.enter-tag-dlg.new-view-checkbox.label"));
    otherWindowCheckBox_.setMnemonic(KeyEvent.VK_V);
    otherWindowCheckBox_.addActionListener(otherWindowCheckboxListener_);
    
    JPanel checkPanel = new JPanel();
    checkPanel.setLayout(new GridLayout(TESTING ? 2 : 1, 1));
    
    checkPanel.add(otherWindowCheckBox_);
    
    setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
    setLayout(new BorderLayout());    
    if (TESTING)
      add(fieldAndFindPanel, BorderLayout.NORTH);
    else
      add(p, BorderLayout.NORTH);
        
    // view found tags
    if (TESTING)
    {
      // incrementally found tags panel
      chooseTagList_ = parser_.getCollisionListComponent(view_);;
      chooseTagList_.setVisibleRowCount(12);
      JScrollPane scrollPane = new JScrollPane(chooseTagList_, 
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(scrollPane, BorderLayout.CENTER);
            
      // keep dialog
      keepDialogCheckBox_ = new JCheckBox(
                    jEdit.getProperty("tags.enter-tag-dlg.keep-dialog.label"));
      //keepDialogCheckBox_.setSelected(jEdit.getBooleanProperty(
      //                                      "tags.enter-tag-dlg.keep-dialog"));
      keepDialogCheckBox_.setMnemonic(KeyEvent.VK_K);
      checkPanel.add(keepDialogCheckBox_);
      scrollPane = null;
    }

    add(checkPanel, BorderLayout.SOUTH);
        
    // re-init
    reinitialize(initialValue);
    
    // update GUI
    updateGUI();
    
    p = null;
    fieldAndFindPanel = null;
    checkPanel = null;
  }
  
  /***************************************************************************/
  public void reinitialize(String initialValue) {
    tagFuncTextField_.setText(initialValue);
    if (initialValue != null) {
      tagFuncTextField_.setSelectionStart(0);
      tagFuncTextField_.setSelectionEnd(initialValue.length());
    }
  }
  
  /***************************************************************************/
  protected HistoryTextField getFuncTextField() { return tagFuncTextField_; }
  
  /***************************************************************************/
  public String getFuncName() {
    String funcName = tagFuncTextField_.getText();
    if (funcName != null) {
      funcName = funcName.trim();
      if (funcName.length() == 0)
        funcName = null;
    }
    return funcName;
  }

  /***************************************************************************/
  public boolean getOtherWindow() { 
    return otherWindowCheckBox_.isSelected(); 
  }

  /***************************************************************************/
  protected void updateGUI() {
    tagFuncTextField_.requestFocus();
  }
  
  /***************************************************************************/
  protected ActionListener viewFoundTagsListener_ = new ActionListener()
  {
    public void actionPerformed(ActionEvent e)
    {
      tagFuncTextField_.requestFocus();
      Log.log(Log.DEBUG, this, "Show results");
    }
  };
  
  /***************************************************************************/
  protected ActionListener otherWindowCheckboxListener_ = new ActionListener() {
    public void actionPerformed(ActionEvent e) 
    {
      tagFuncTextField_.requestFocus();
    }
  };

  /***************************************************************************/
  protected ActionListener findButtonListener_ = new ActionListener() 
  {
    public void actionPerformed(ActionEvent e)
    {
      String name = tagFuncTextField_.getText();
      final String funcName = name.trim();
      
      Tags.followTag(null, null, null, false, false, funcName);

      Vector tagIdentifiers = parser_.getTagLines();
      
      if (tagIdentifiers != null)
      {
/*
        if (tagIdentifiers.size() == 1 &&
            !keepDialogCheckBox_.isSelected())
        {
          Tags.processTagLine(0, view_, otherWindowCheckBox_.isSelected(),
                              parser_.getTag());
        }
*/        
        chooseTagList_.setListData(tagIdentifiers);
        
        // This doesn't currently work...
        chooseTagList_.revalidate();
        chooseTagList_.repaint();
      }
    }
  };
  
}
