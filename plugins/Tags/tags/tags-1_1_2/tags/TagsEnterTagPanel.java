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
 *
 * $Id$
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
  /***************************************************************************/
  protected JLabel textFieldLabel_;
  protected HistoryTextField tagFuncTextField_;
  protected JCheckBox  otherWindowCheckBox_;
  protected JCheckBox  viewFoundTagsCheckBox_;
  protected JCheckBox  keepDialogCheckBox_;
  protected ChooseTagList chooseTagList_;
  
  View view_;
  ExuberantCTagsParser parser_;
  
  /***************************************************************************/
  public TagsEnterTagPanel(String initialValue, View view, ExuberantCTagsParser parser) 
  {
    view_ = view;
    parser_ = parser;
    
    // tag input field
    textFieldLabel_ = new JLabel(
              jEdit.getProperty("tags.enter-tag-dlg.tag-to.label"));
    tagFuncTextField_ = new HistoryTextField("tags.enter-tag.history", false, 
                                             false);
    tagFuncTextField_.setColumns(16);
    textFieldLabel_.setLabelFor(tagFuncTextField_);
                                         
    JPanel p = new JPanel();                                         
    p.setLayout(new BorderLayout(5,5));
    p.add(textFieldLabel_, BorderLayout.WEST);
    p.add(tagFuncTextField_, BorderLayout.CENTER);
    
    // open in other window check box                                             
    otherWindowCheckBox_ = new JCheckBox(
              jEdit.getProperty("tags.enter-tag-dlg.new-view-checkbox.label"));
    otherWindowCheckBox_.setMnemonic(KeyEvent.VK_V);
    otherWindowCheckBox_.addActionListener(otherWindowCheckboxListener_);
    p.add(otherWindowCheckBox_, BorderLayout.SOUTH);
    
    setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
    setLayout(new BorderLayout());    
    add(p, BorderLayout.NORTH);
    
    // view found tags
/*    
    viewFoundTagsCheckBox_ = new JCheckBox(
                jEdit.getProperty("tags.enter-tag-dlg.view-found-tags.label"));
    viewFoundTagsCheckBox_.setSelected(jEdit.getBooleanProperty(
                                        "tags.enter-tag-dlg.view-found-tags"));
    viewFoundTagsCheckBox_.setMnemonic(KeyEvent.VK_F);
    viewFoundTagsCheckBox_.addActionListener(viewFoundTagsListener_);
    add(viewFoundTagsCheckBox_, BorderLayout.CENTER);
    
    // incrementally found tags panel
    chooseTagList_ = new ChooseTagList(view_, parser_);
    chooseTagList_.setVisibleRowCount(4);
    add(chooseTagList_, BorderLayout.SOUTH);
    
    // keep dialog
    keepDialogCheckBox_ = new JCheckBox(
                    jEdit.getProperty("tags.enter-tag-dlg.keep-dialog.label"));
    //keepDialogCheckBox_.setSelected(jEdit.getBooleanProperty(
    //                                      "tags.enter-tag-dlg.keep-dialog"));
    keepDialogCheckBox_.setMnemonic(KeyEvent.VK_K);
*/
    
    // re-init
    reinitialize(initialValue);
    
    // update GUI
    updateGUI();
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

}
