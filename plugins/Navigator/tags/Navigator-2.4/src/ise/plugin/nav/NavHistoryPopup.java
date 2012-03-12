/*
* ChooseTagListPopup.java
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
* $Id: ChooseTagListPopup.java,v 1.10 2004/11/07 15:52:34 orutherfurd Exp $
*/

/* This is pretty much ripped from gui/CompleteWord.java */

package ise.plugin.nav;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.Collection;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;


class NavHistoryPopup extends JPopupMenu {

    private JList list;
    private View view;
    private boolean numberKeyProcessed = false;
    
    private NavHistoryList historyList = null;

    private boolean useCSS = false;
    private boolean showGutter = false;

    public NavHistoryPopup( View view, Navigator navigator, Collection<NavPosition> positions ) {
        this( view, navigator, positions, null );
    }

    public NavHistoryPopup( View view, Navigator navigator, Collection<NavPosition> positions, NavPosition currentPosition ) {
        if (positions == null || positions.size() == 0) {
            return;   
        }
        this.view = view;
        historyList = new NavHistoryList(view, navigator, positions, currentPosition);

        add( historyList );

        // add listeners
        KeyHandler keyHandler = new KeyHandler();
        addKeyListener( keyHandler );
        historyList.addKeyListener( keyHandler );
        this.view.setKeyEventInterceptor( keyHandler );
        historyList.addMouseListener(new MouseHandler());

        // show components
        pack();
        setLocation();
        setVisible( true );
    }

    public Dimension getSize() {
        return getPreferredSize();   
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(600, super.getPreferredSize().height);
    }

    /**
     * Set the location of the popup on the screen.
     */
    public void setLocation() {
        JEditTextArea textArea = view.getTextArea();

        int caretLine = textArea.getCaretLine();
        textArea.getLineStartOffset( caretLine );

        Rectangle rect = view.getGraphicsConfiguration().getBounds();
        Dimension d = getSize();
        Point location = new Point( rect.x + ( rect.width - d.width ) / 2,
                rect.y + ( rect.height - d.height ) / 2 );
        // make sure it fits on screen
        Dimension screenSize = rect.getSize();
        if ( location.x + d.width > screenSize.width ) {
            if ( d.width >= screenSize.width ) {
                /* In this instance we should actually resize the number of columns in
                 * the tag index filename, but for now just position it so that you
                 * can at least read the left side of the dialog
                 */
                location.x = rect.x;
            }
            else {
                location.x = rect.x + rect.width - d.width - 200;
            }
        }
        if ( location.y + d.height > screenSize.height ) {
            location.y = screenSize.height - d.height;
        }

        setLocation( location );

        textArea = null;
        location = null;
        d = null;
        screenSize = null;
    }


    public void dispose() {
        // restore Code2Html properties to original values
        jEdit.setBooleanProperty( "code2html.use-css", useCSS );
        jEdit.setBooleanProperty( "code2html.show-gutter", showGutter );

        view.setKeyEventInterceptor( null );
        setVisible( false );
        view.getTextArea().requestFocus();
    }


    private void selected() {
        historyList.jump();
        dispose();
    }


    class KeyHandler extends KeyAdapter {

        public void keyTyped( KeyEvent evt ) {
            evt = KeyEventWorkaround.processKeyEvent( evt );
            if ( evt == null )
                return ;

            switch ( evt.getKeyChar() ) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if ( numberKeyProcessed )                        // Since many components have this handler
                        return ;

                    /* There may actually be more than 9 items in the list, but since
                     * the user would have to scroll to see them either with the mouse
                     * or with the arrow keys, then they can select the item they want
                     * with those means.
                     */
                    int selected = Character.getNumericValue( evt.getKeyChar() ) - 1;
                    if ( selected >= 0 &&
                            selected < list.getModel().getSize() ) {
                        list.setSelectedIndex( selected );
                        selected();
                        numberKeyProcessed = true;
                    }
                    evt.consume();
            }

            evt = null;
        }


        public void keyPressed( KeyEvent evt ) {
            evt = KeyEventWorkaround.processKeyEvent( evt );
            if ( evt == null )
                return ;

            switch ( evt.getKeyCode() ) {
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
                    int selected = list.getSelectedIndex();
                    if ( selected == 0 )
                        selected = list.getModel().getSize() - 1;
                    //else if ( getFocusOwner() == list )
                    //    return ; // Let JList handle the event
                    else
                        selected = selected - 1;

                    list.setSelectedIndex( selected );
                    list.ensureIndexIsVisible( selected );

                    evt.consume();
                    break;
                case KeyEvent.VK_DOWN:
                    selected = list.getSelectedIndex();
                    if ( selected == list.getModel().getSize() - 1 )
                        selected = 0;
                    //else if ( getFocusOwner() == list )
                    //    return ; // Let JList handle the event
                    else
                        selected = selected + 1;

                    list.setSelectedIndex( selected );
                    list.ensureIndexIsVisible( selected );

                    evt.consume();
                    break;
                case KeyEvent.VK_SPACE:
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

                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_PAGE_DOWN:
                    break;

                default:
                    dispose();
                    evt.consume();
                    break;
            }
            evt = null;
        }
    }

    class MouseHandler extends MouseAdapter {
        public void mouseClicked( MouseEvent evt ) {
            selected();
        }
    }
}