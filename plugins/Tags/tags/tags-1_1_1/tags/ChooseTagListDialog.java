/*
 * ChooseTagListDialog.java
 * Copyright (c) 2001, 2002 Kenrick Drew, Slava Pestov
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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.awt.Toolkit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;
import org.gjt.sp.util.Log;

class ChooseTagListDialog extends JDialog
{
  /***************************************************************************/
  protected ChooseTagList tagList_;
  protected JCheckBox keepDialogCheckBox_;
  protected JButton cancelButton_;
	protected View view_;
  protected boolean canceled_ = false;
  protected boolean openNewView_;
  protected ExuberantCTagsParser parser_;

  /***************************************************************************/
  public ChooseTagListDialog(ExuberantCTagsParser parser, View view, boolean openNewView)
  {
    super(view,
          (view != null) ? jEdit.getProperty("tag-collision-dlg.title") :
                         "Tag Collisions",
          false);

    view_ = view;
    openNewView_ = openNewView;
    parser_ = parser;

    getContentPane().setLayout(new BorderLayout());

    // label
    JLabel label = new JLabel(
          (view_ != null) ? jEdit.getProperty("tag-collision-dlg.label") :
                            "Choose tag:");

    // collision list
    tagList_ = parser.getCollisionListComponent(view_);
    JScrollPane scrollPane = new JScrollPane(tagList_,
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    tagList_.addKeyListener(keyListener_);
    tagList_.addMouseListener(mouseListener_);
    JPanel contentPanel = new JPanel(new BorderLayout(0,5));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
    contentPanel.add(label, BorderLayout.NORTH);
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(contentPanel, BorderLayout.CENTER);

    // keep dialog
    keepDialogCheckBox_ = new JCheckBox(
                    jEdit.getProperty("tags.enter-tag-dlg.keep-dialog.label"));
    keepDialogCheckBox_.addActionListener(keepDialogListener_);
    keepDialogCheckBox_.setMnemonic(KeyEvent.VK_K);
    if (view_ == null)
      keepDialogCheckBox_.setEnabled(false);
    contentPanel.add(keepDialogCheckBox_, BorderLayout.SOUTH);

    // OK/Cancel/Close buttons
    JButton ok = new JButton(
             (view_ != null) ? jEdit.getProperty("options.tags.tag-ok.label") :
                               "OK");
    getRootPane().setDefaultButton(ok);
    ok.addActionListener(okButtonListener_);

    cancelButton_ = new JButton(
          (view_ != null) ? jEdit.getProperty("options.tags.tag-cancel.label") :
                            "Cancel");
    cancelButton_.addActionListener(cancelButtonListener_);

    JPanel buttonPanelFlow = new JPanel(new FlowLayout());
    JPanel buttonPanelGrid = new JPanel(new GridLayout(1,0,5,0));
    buttonPanelFlow.add(buttonPanelGrid);
    buttonPanelGrid.add(ok);
    buttonPanelGrid.add(cancelButton_);
    getContentPane().add(buttonPanelFlow, BorderLayout.SOUTH);

    // dialog setup
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    addKeyListener(keyListener_);

		// show
    showDialog();

    scrollPane = null;
    contentPanel = null;
    ok = null;
    buttonPanelFlow = null;
    buttonPanelGrid = null;
  }

  /***************************************************************************/
  protected void showDialog() {
    pack();
    // Place dialog
    Tags.setDialogPosition(view_, this);
    show();
	GUIUtilities.requestFocus(this, tagList_);
  }

  /***************************************************************************/
  protected void followSelectedTag()
  {
    Tags.processTagLine(tagList_.getSelectedIndex(), view_, openNewView_,
                        parser_.getTag());
  }

  /*+*************************************************************************/
  protected ActionListener keepDialogListener_ = new ActionListener()
  {
    public void actionPerformed(ActionEvent e)
    {
      if (keepDialogCheckBox_.isSelected())
        cancelButton_.setText((view_ != null) ?
                          jEdit.getProperty("options.tags.tag-close.label") :
                          "Cancel");
      else
        cancelButton_.setText((view_ != null) ?
                          jEdit.getProperty("options.tags.tag-cancel.label") :
                          "Cancel");
      tagList_.requestFocus();
    }
  };


  /*+*************************************************************************/
  protected ActionListener okButtonListener_ = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      followSelectedTag();
      if (!keepDialogCheckBox_.isSelected())
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
        case KeyEvent.VK_UP:
          int selected = tagList_.getSelectedIndex();
          if (selected == 0)
            selected = tagList_.getModel().getSize() - 1;
          else if (getFocusOwner() == tagList_)
            return; // Let JList handle the event
          else
            selected = selected - 1;


          tagList_.setSelectedIndex(selected);
          tagList_.ensureIndexIsVisible(selected);

          e.consume();
          break;
        case KeyEvent.VK_DOWN:
          selected = tagList_.getSelectedIndex();
          if (selected == tagList_.getModel().getSize() - 1)
            selected = 0;
          else if (getFocusOwner() == tagList_)
            return; // Let JList handle the event
          else
            selected = selected + 1;

          tagList_.setSelectedIndex(selected);
          tagList_.ensureIndexIsVisible(selected);

          e.consume();
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
        followSelectedTag();
        if (!keepDialogCheckBox_.isSelected())
          dispose();
      }
    }
  };

}
