/*
 * ChooseTagList2.java
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

public class ChooseTagList2 extends JWindow {
  
  /***************************************************************************/
	private TagsParser parser_;
  private View view_;
  private View tagToView_;
  private boolean newView_;
  private Vector tagIdentifiers_;
	private JList tagIdentifierList_;

  /***************************************************************************/
	public ChooseTagList2(TagsParser parser, View view, boolean newView)	{
		
    super(view);

    parser_ = parser;
		view_ = view;
    newView_ = newView;
    
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
  protected void createComponents() {
    int size = parser_.getNumberOfFoundTags();
    tagIdentifiers_ = new Vector(size);
    for (int i = 0; i < size; i++)
      tagIdentifiers_.addElement(parser_.getCollisionChooseString(i));
    
    tagIdentifierList_ = new JList(tagIdentifiers_);

  }

  /***************************************************************************/
  protected void setupComponents() {
		tagIdentifierList_.setVisibleRowCount(Math.min(tagIdentifiers_.size(),8));

		tagIdentifierList_.addMouseListener(new MouseHandler());
		tagIdentifierList_.setSelectedIndex(0);
		tagIdentifierList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
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
    tagIdentifierList_.setFont(font);
  }
  
  /***************************************************************************/
  protected void placeComponents() {
		// stupid scrollbar policy is an attempt to work around
		// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
		JScrollPane scroller = new JScrollPane(tagIdentifierList_,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
		getContentPane().add(scroller, BorderLayout.CENTER);
  }
  
  /***************************************************************************/
  public void setLocation() {
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
    int width = getWidth();
    int height = getHeight();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    if (location.x + width > screenSize.width)
      location.x = screenSize.width - width;
    if (location.y + height > screenSize.height)
      location.y = screenSize.height - height;
    
    setLocation(location);
  }
  
  /***************************************************************************/
	public void dispose()	{
		view_.setKeyEventInterceptor(null);
		super.dispose();
	}

  /***************************************************************************/
	private void selected()	{
    String selectedTagName = (String) tagIdentifierList_.getSelectedValue();
    
    int numCollisionTags = parser_.getNumberOfFoundTags();
    String tagIdentifier = null;
    int index;
    for (index = 0; index < numCollisionTags; index++) {
      tagIdentifier = (String) tagIdentifiers_.elementAt(index);
      if (selectedTagName.equals(tagIdentifier))
        break;
    }
    tagIdentifier = null;
    //Macros.message(view_, parser_.getTagLine(index));
    
    Tags.processTagLine(index, view_, newView_, parser_.getTag());
    
    //Tags.followTag(view_, view_.getTextArea(), view_.getBuffer(), newView_, 
    //               selectedTagName);
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
          if(getFocusOwner() == tagIdentifierList_)
            return;

          int selected = tagIdentifierList_.getSelectedIndex();
          if(selected == 0)
            return;

          selected = selected - 1;
	
          tagIdentifierList_.setSelectedIndex(selected);
          tagIdentifierList_.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        case KeyEvent.VK_DOWN:
          if(getFocusOwner() == tagIdentifierList_)
            return;

          selected = tagIdentifierList_.getSelectedIndex();
          if(selected == tagIdentifierList_.getModel().getSize() - 1)
            return;

          selected = selected + 1;

          tagIdentifierList_.setSelectedIndex(selected);
          tagIdentifierList_.ensureIndexIsVisible(selected);

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
