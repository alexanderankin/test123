/*
 * TagsOptionsDialog.java
 * Copyright (c) 2001 Kenrick Drew
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

import org.gjt.sp.jedit.View;

public class TagsOptionsDialog extends JDialog implements WindowListener {
  
  /***************************************************************************/
  static public final int APPROVE_OPTION = 1;
  static public final int CANCEL_OPTION = 2;
  
  static protected boolean debug_ = false;

  /***************************************************************************/
  protected JPanel contentPanel_ = new JPanel(new BorderLayout());
    protected TagsOptionsPanel tagPanel_ = null;

    protected JPanel buttonPanelFlow_ = 
                                  new JPanel(new FlowLayout(FlowLayout.RIGHT));
      protected JPanel buttonPanelGrid_ = new JPanel(new GridLayout(1,0,5,5));
        protected JButton okButton_ = new JButton(
                                   TagsPlugin.getOptionString("tag-ok.label"));
        protected JButton cancelButton_ = new JButton(
                               TagsPlugin.getOptionString("tag-cancel.label"));
      
  protected View view_ = null;
  
  protected int returnValue_ = APPROVE_OPTION;
  
  /***************************************************************************/
  public TagsOptionsDialog(View view) {
    super(view, TagsPlugin.getOptionString("tag-options.title"), true);
    
    tagPanel_ = new TagsOptionsPanel(view);
    
    setup(view); 
  }
  
  /***************************************************************************/
  public int showDialog() {
    pack();

    // Place dialog in center of screen
    setLocationRelativeTo(view_);

    show();
    
    return returnValue_;
  }
  
  /***************************************************************************/
  protected void setup(View view) {
    view_ = view;

    tagPanel_.setDebug(debug_);
    
    createComponents();
    setupComponents();
    placeComponents();
  }
  
  /***************************************************************************/
  protected void createComponents() {
    
  }
  
  /***************************************************************************/
  protected void setupComponents() {
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    getContentPane().setLayout(new BorderLayout());
    
    okButton_.addActionListener(okButtonListener_);
    cancelButton_.addActionListener(cancelButtonListener_);

    okButton_.setMnemonic(KeyEvent.VK_O);
    cancelButton_.setMnemonic(KeyEvent.VK_C);

    addKeyListener(keyListener_);

    getRootPane().setDefaultButton(okButton_);
  }
  
  /***************************************************************************/
  protected void placeComponents() {
    
    buttonPanelGrid_.add(okButton_);
    buttonPanelGrid_.add(cancelButton_);
    
    buttonPanelFlow_.add(buttonPanelGrid_);
    
    getContentPane().add(tagPanel_, BorderLayout.CENTER);
    getContentPane().add(buttonPanelFlow_, BorderLayout.SOUTH);
  }

  /*+*************************************************************************/
  protected ActionListener okButtonListener_ = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      Tags.setUseCurrentBufTagFile(
                         tagPanel_.useCurrentBufTagFileCheckBox_.isSelected());
      Tags.setSearchAllTagFiles(
                         tagPanel_.searchAllFilesCheckBox_.isSelected());
      returnValue_ = APPROVE_OPTION;
      setVisible(false);
    }
  };
  
  /*+*************************************************************************/
  protected ActionListener cancelButtonListener_ = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      // Clear out any tag files and replace with original ones...
      Tags.tagFiles_.removeAllElements();
      
      int numOrigs = tagPanel_.origList_.size();
      for (int i = 0; i < numOrigs; i++)
        Tags.tagFiles_.addElement(tagPanel_.origList_.elementAt(i));

      // Set original parser type
      Tags.setParserType(tagPanel_.origParserType_);
        
      returnValue_ = CANCEL_OPTION;
      setVisible(false);
    }
  };

  /*+*************************************************************************/
  protected KeyListener keyListener_ = new KeyListener() {
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
          case KeyEvent.VK_ESCAPE:
            cancelButtonListener_.actionPerformed(null);
            break;

          case KeyEvent.VK_INSERT:  // INSERT on SUN keyboard isn't working?
            tagPanel_.addButtonListener_.actionPerformed(null);
            break;

          case KeyEvent.VK_DELETE:
          case KeyEvent.VK_BACK_SPACE:
            tagPanel_.removeButtonListener_.actionPerformed(null);
            break;
        }
    }
  };
  
  /*+*************************************************************************/
  public void windowClosing(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowActivated(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }

  /***************************************************************************/
  static public void main(String args[]) {
    debug_ = true;
    
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.1");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.2");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.3");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.4");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.5");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.o.1");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.o.2");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.o.3");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.o.4");
    Tags.appendTagFile("/sportsrc/spg/system_1/softdb/tags.o.5");

    TagsOptionsDialog dialog = new TagsOptionsDialog(null);
    
    if (dialog.showDialog() == TagsOptionsDialog.APPROVE_OPTION)
      System.out.println("New file set:");
    else
      System.out.println("Canceled");
      
    Tags.displayTagFiles(null);

    System.exit(0);
  }

}
