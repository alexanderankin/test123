/*
 * ChooseTagList.java
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

/* This is pretty much ripped from gui/CompleteWord.java */

package tags;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;

public class ChooseTagList extends JWindow {
  
  /***************************************************************************/
	private View view_;
  private JEditTextArea textArea_;
  private Buffer buffer_;
  private boolean newView_;
  private Vector tagNames_;
	private JList tagNamesList_;

  /***************************************************************************/
	public ChooseTagList(View view, JEditTextArea textArea, Buffer buffer,
                       boolean newView, Vector tagNames, Point location)	{
		
    super(view);

		view_ = view;
    textArea_ = textArea;
    buffer_ = buffer;
    newView_ = newView;
    tagNames_ = tagNames;
    
    createComponents();
    setupComponents();
    placeComponents();
    
    GUIUtilities.requestFocus(this,tagNamesList_);
    
    pack();
    setLocation(location);
   
    super.show();

    KeyHandler keyHandler = new KeyHandler();
		addKeyListener(keyHandler);
		getRootPane().addKeyListener(keyHandler);
		tagNamesList_.addKeyListener(keyHandler);
		view_.setKeyEventInterceptor(keyHandler);
	}

  /***************************************************************************/
  protected void createComponents() {
    tagNamesList_ = new JList(tagNames_);

  }

  /***************************************************************************/
  protected void setupComponents() {
		tagNamesList_.setVisibleRowCount(Math.min(tagNames_.size(),8));

		tagNamesList_.addMouseListener(new MouseHandler());
		tagNamesList_.setSelectedIndex(0);
		tagNamesList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    String fontName = TagsPlugin.getOptionString("choose.Font");
    String sizeString = TagsPlugin.getOptionString("choose.font-size");
    int size = 12;
    try { size = Integer.parseInt(sizeString); }
    catch (NumberFormatException nfe) {
      size = 12;
    }
    
    Font font = new Font(fontName, Font.PLAIN, size);
    if (font == null)
      font = new Font("Monospaced", Font.PLAIN, size);
    tagNamesList_.setFont(font);
  }
  
  /***************************************************************************/
  protected void placeComponents() {
		// stupid scrollbar policy is an attempt to work around
		// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
		JScrollPane scroller = new JScrollPane(tagNamesList_,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
		getContentPane().add(scroller, BorderLayout.CENTER);
  }
  
  /***************************************************************************/
	public void dispose()	{
		view_.setKeyEventInterceptor(null);
		super.dispose();
	}

  /***************************************************************************/
	private void selected()	{
    String selectedTagName = (String) tagNamesList_.getSelectedValue();
    Tags.followTag(view_, textArea_, buffer_, newView_, selectedTagName);
		dispose();
	}

  /***************************************************************************/
	class KeyHandler extends KeyAdapter	{
		public void keyPressed(KeyEvent evt) {
			switch(evt.getKeyCode()) {
        case KeyEvent.VK_TAB:
        case KeyEvent.VK_ENTER:
          selected();
          evt.consume();
          break;
        case KeyEvent.VK_ESCAPE:
          dispose();
          evt.consume();
          break;
        case KeyEvent.VK_UP:
          if(getFocusOwner() == tagNamesList_)
            return;

          int selected = tagNamesList_.getSelectedIndex();
          if(selected == 0)
            return;

          selected = selected - 1;
	
          tagNamesList_.setSelectedIndex(selected);
          tagNamesList_.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        case KeyEvent.VK_DOWN:
          if(getFocusOwner() == tagNamesList_)
            return;

          selected = tagNamesList_.getSelectedIndex();
          if(selected == tagNamesList_.getModel().getSize() - 1)
            return;

          selected = selected + 1;

          tagNamesList_.setSelectedIndex(selected);
          tagNamesList_.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        default:
          dispose();
          view_.processKeyEvent(evt);
          break;
			}
		}

    /*************************************************************************/
		public void keyTyped(KeyEvent evt) {
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;
			else
			{
				dispose();
				view_.processKeyEvent(evt);
			}
		}
	}

  /***************************************************************************/
	class MouseHandler extends MouseAdapter	{
		public void mouseClicked(MouseEvent evt) {
			selected();
		}
	}
}
