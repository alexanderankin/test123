
/*
 * Copyright (c) 2007, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package errorlist;


import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;


/**
 */
public class ErrorList extends JPanel {

    private JTabbedPane tabs;
    private View view;
    private HashMap<ErrorSource, ErrorListPanel> sourcePanelMap;


    public ErrorList( View view ) {
        super( new BorderLayout() );
        this.view = view;
        sourcePanelMap = new HashMap<ErrorSource, ErrorListPanel>();
        tabs = new JTabbedPane();
        ErrorListPanel panel = new ErrorListPanel( ErrorList.this.view );
        panel.setIsActive( true );
        JScrollPane scroller = new JScrollPane( panel );
        JScrollBar bar = scroller.getVerticalScrollBar();
        bar.setUnitIncrement( 15 );
        final Component c = tabs.add( "ErrorList", scroller );
        tabs.setTabComponentAt( tabs.getTabCount() - 1, new ButtonTabComponent( tabs ) );
        tabs.setSelectedComponent( c );
        add( tabs );

        // add a mouse listener to be able to close results tabs
        tabs.addMouseListener( new MouseAdapter(){

                public void mousePressed( MouseEvent me ) {
                    if ( me.isPopupTrigger() ) {
                        handleIsPopup( me );
                    }
                }


                public void mouseReleased( MouseEvent me ) {
                    if ( me.isPopupTrigger() ) {
                        handleIsPopup( me );
                    }
                }


                private void handleIsPopup( MouseEvent me ) {
                    final int x = me.getX();
                    final int y = me.getY();
                    int index = tabs.indexAtLocation( x, y );


                    final Component c = tabs.getComponentAt( index );
                    final JPopupMenu pm = new JPopupMenu();

                    JMenuItem addTab = new JMenuItem( jEdit.getProperty("error-list.addTab", "Add Tab") );
                    addTab.addActionListener( new ActionListener(){

                            public void actionPerformed( ActionEvent ae ) {
                                String name = JOptionPane.showInputDialog( ErrorList.this, jEdit.getProperty("error-list.tabName", "Enter tab name") );
                                if ( name == null || name.isEmpty() ) {
                                    return;
                                }


                                ErrorListPanel panel = new ErrorListPanel( ErrorList.this.view );
                                JScrollPane scroller = new JScrollPane( panel );
                                JScrollBar bar = scroller.getVerticalScrollBar();
                                bar.setUnitIncrement( 15 );
                                final Component c = tabs.add( name, scroller );
                                tabs.setTabComponentAt( tabs.getTabCount() - 1, new ButtonTabComponent( tabs ) );
                                tabs.setSelectedComponent( c );
                            }
                        }
                    );
                    pm.add( addTab );

                    if ( index > 0 ) {

                        // TODO: fix the properties
                        JMenuItem close_mi = new JMenuItem( jEdit.getProperty( "error-list.close", "Close" ) );
                        pm.add( close_mi );
                        close_mi.addActionListener( new ActionListener(){

                                public void actionPerformed( ActionEvent ae ) {
                                    tabs.remove( c );
                                }
                            }
                        );

                        // TODO: fix the properties
                        JMenuItem close_all_mi = new JMenuItem( jEdit.getProperty( "error-list.closeAll", "Close All" ) );
                        pm.add( close_all_mi );
                        close_all_mi.addActionListener( new ActionListener(){

                                public void actionPerformed( ActionEvent ae ) {
                                    for ( int i = 1; i < tabs.getTabCount(); i++ ) {
                                        Component comp = tabs.getComponentAt( i );
                                        tabs.remove( comp );
                                        comp = null;
                                    }
                                }
                            }
                        );
                    }

                    showPopupMenu( pm, tabs, x, y );
                }
            }
        );

        tabs.addChangeListener( new ChangeListener(){

                public void stateChanged( ChangeEvent ce ) {
                    int selectedIndex = tabs.getSelectedIndex();
                    for ( int i = 0; i < tabs.getTabCount(); i++ ) {
                        JScrollPane scrollPane = ( JScrollPane )tabs.getComponentAt( i );
                        ErrorListPanel panel = ( ErrorListPanel )scrollPane.getViewport().getView();
                        panel.setIsActive( i == selectedIndex );
                    }
                }
            } );
    }


    /**
     * Shows the specified popup menu, ensuring it is displayed within
     * the bounds of the screen.
     *
     * @param popup  The popup menu
     * @param comp   The component to show it for
     * @param x      The x coordinate
     * @param y      The y coordinate
     */
    private void showPopupMenu( javax.swing.JPopupMenu popup, Component comp, int x, int y ) {
        Point p = getBestAnchorPoint( comp, x, y );
        popup.show( comp, p.x, p.y );
    }


    /**
     * Calculates the best location to show the component based on the given (x, y)
     * coordinates. The returned point will be as close as possible to the original
     * point while allowing the entire component to be displayed on screen. This is
     * useful for showing dialogs and popups.
     * @param comp the component that will be shown.
     * @param x the original x-coordinate of the component or of the desired location.
     * @param y the original y-coordinate of the component or of the desired location.
     * @return a point as close to the given (x, y) that will allow the entire
     * component to be shown on the screen.
     */
    private Point getBestAnchorPoint( Component comp, int x, int y ) {
        int new_x = x;
        int new_y = y;
        Point p = new Point( new_x, new_y );
        javax.swing.SwingUtilities.convertPointToScreen( p, comp );

        Dimension size = comp.getSize();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        boolean move_horizontally = false;
        boolean move_vertically = false;

        // calculate new x coordinate. If the component width is less than the
        // screen width and the right side of the component is off the screen,
        // move it left.
        if ( p.x + size.width > screen.width
        && size.width < screen.width ) {
            new_x += ( screen.width - p.x - size.width );
            move_horizontally = true;
        }


        // calculate new y coordinate. If the component height is less than the
        // screen height and the bottom of the component is off the screen, move
        // it up.
        if ( p.y + size.height > screen.height
        && size.height < screen.height ) {
            new_y += ( screen.height - p.y - size.height );
            move_vertically = true;
        }


        // If the component is a popup and it needed to be moved both horizontally
        // and vertically, the mouse pointer might end up over a menu item, which
        // will be invoked when the mouse is released. In this case, move the
        // component to a location that is not under the point.
        if ( move_horizontally && move_vertically && ( comp instanceof javax.swing.JPopupMenu ) ) {

            // first try to move it more left
            if ( x - size.width - 2 > 0 ) {
                new_x = x - size.width - 2;
            }
            else if ( y - size.height - 2 > 0 ) {

                // try to move it up some more
                new_y = y - size.height - 2;
            }
        }


        return new Point( new_x, new_y );
    }


    public void unload() {
        for ( int i = 1; i < tabs.getTabCount();  ) {
            ErrorListPanel panel = ( ErrorListPanel )tabs.getComponentAt( i );
            panel.unload();
        }
    }
}
