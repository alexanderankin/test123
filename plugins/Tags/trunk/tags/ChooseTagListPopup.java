/*
 * ChooseTagListPopup.java
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

class ChooseTagListPopup extends JWindow 
{
  
  /***************************************************************************/
	private TagsParser parser_;
  private View view_;
  private boolean openNewView_;
  private Vector tagIdentifiers_;
	private ChooseTagList tagIdentifierList_;
  private boolean numberKeyProcessed_ = false;

  /***************************************************************************/
	public ChooseTagListPopup(TagsParser parser, View view, boolean openNewView) 
  {
		
    super(view);

    parser_ = parser;
		view_ = view;
    openNewView_ = openNewView;
    
    createComponents();
    setupComponents();
    placeComponents();
    
    GUIUtilities.requestFocus(this,tagIdentifierList_);
    
    pack();
    setLocation();
   
    super.show();

    KeyHandler keyHandler = new KeyHandler();
		addKeyListener(keyHandler);
		getRootPane().addKeyListener(keyHandler);
		tagIdentifierList_.addKeyListener(keyHandler);
    view_.setKeyEventInterceptor(keyHandler);
	}

  /***************************************************************************/
  protected void createComponents() 
  {
    tagIdentifierList_ = parser_.getCollisionListComponent(view_);
  }

  /***************************************************************************/
  protected void setupComponents() 
  {
		tagIdentifierList_.addMouseListener(new MouseHandler());  // not
  }
  
  /***************************************************************************/
  protected void placeComponents() 
  {
		// stupid scrollbar policy is an attempt to work around
		// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
		JScrollPane scroller = new JScrollPane(tagIdentifierList_,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
		getContentPane().add(scroller, BorderLayout.CENTER);
  }
  
  /***************************************************************************/
  public void setLocation() 
  {
		JEditTextArea textArea = view_.getTextArea();
	
		int caretLine = textArea.getCaretLine();
		int lineIdx = textArea.getCaretPosition() - // offsets from beg of file
									textArea.getLineStartOffset(caretLine);
	
		Point location = new Point(textArea.offsetToX(caretLine, lineIdx),
												 textArea.getPainter().getFontMetrics().getHeight() *
												 (textArea.getBuffer().physicalToVirtual(caretLine) - 
												 textArea.getFirstLine() + 1));
		SwingUtilities.convertPointToScreen(location, textArea.getPainter());
	
		// make sure it fits on screen
		Dimension d = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (location.x + d.width > screenSize.width)
    {
      if (d.width >= screenSize.width)
        /* In this intance we should actually resize the number of columns in 
         * the tag index filename, but for now just position it so that you 
         * can at least read the left side of the dialog
         */
        location.x = 0;
      else
        location.x = screenSize.width - d.width;
    }
		if (location.y + d.height > screenSize.height)
			location.y = screenSize.height - d.height;
	
		setLocation(location);
}
  
  /***************************************************************************/
	public void dispose()	
  {
    view_.setKeyEventInterceptor(null);
		super.dispose();
	}

  /***************************************************************************/
	private void selected()	
  {
    Tags.processTagLine(tagIdentifierList_.getSelectedIndex(), 
                        view_, openNewView_, parser_.getTag());
    
		dispose();
	}

  /***************************************************************************/
	class KeyHandler extends KeyAdapter	
  {
    public void keyTyped(KeyEvent evt)
    {
      evt = KeyEventWorkaround.processKeyEvent(evt);
      if (evt == null)
        return;

      switch (evt.getKeyChar())
      {
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          if (numberKeyProcessed_) // Since many components have this handeler
            return;

          /* There may actually be more than 9 items in the list, but since
           * the user would have to scroll to see them either with the mouse
           * or with the arrow keys, then they can select the item they want
           * with those means.
           */
          int selected = Character.getNumericValue(evt.getKeyChar()) - 1;
          if (selected >= 0 && 
              selected < tagIdentifierList_.getModel().getSize())
          {
            tagIdentifierList_.setSelectedIndex(selected);
            selected();
            numberKeyProcessed_ = true;
          }
          evt.consume();
      }
    }
    
		public void keyPressed(KeyEvent evt) 
    {
      evt = KeyEventWorkaround.processKeyEvent(evt);
      if (evt == null)
        return;
      
			switch(evt.getKeyCode()) 
      {
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
          if (getFocusOwner() == tagIdentifierList_)
            return;

          int selected = tagIdentifierList_.getSelectedIndex();
          if (selected == 0)
            selected = tagIdentifierList_.getModel().getSize() - 1;
          else
            selected = selected - 1;
	
          tagIdentifierList_.setSelectedIndex(selected);
          tagIdentifierList_.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        case KeyEvent.VK_DOWN:
          if (getFocusOwner() == tagIdentifierList_)
            return;

          selected = tagIdentifierList_.getSelectedIndex();
          if (selected == tagIdentifierList_.getModel().getSize() - 1)
            selected = 0;
          else
            selected = selected + 1;

          tagIdentifierList_.setSelectedIndex(selected);
          tagIdentifierList_.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        case KeyEvent.VK_1:
        case KeyEvent.VK_2:
        case KeyEvent.VK_3:
        case KeyEvent.VK_4:
        case KeyEvent.VK_5:
        case KeyEvent.VK_6:
        case KeyEvent.VK_7:
        case KeyEvent.VK_8:
        case KeyEvent.VK_9:
          evt.consume();  /* so that we don't automatically dismiss */
          break;

        default:
          dispose();
          evt.consume();
          break;
			}
		}
	}

  /***************************************************************************/
  class MouseHandler extends MouseAdapter	
  {
		public void mouseClicked(MouseEvent evt) 
    {
      selected();
    }
  }
}
