/*
* Copyright (c) 2008, Dale Anson
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package jdiff.component.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import jdiff.DualDiff;
import jdiff.component.*;

public class BasicMergeToolBarUI extends MergeToolBarUI {

    private MergeToolBar toolbar;
    private View view;
    private JButton next;
    private JButton prev;
    private JButton move_right;
    private JButton move_left;
    private JButton unsplit;
    private JButton swap;
    private JButton diff;
    private JButton refresh;

    /**
     * Required by super class.
     * @param c not used
     * @return one of these
     */
    public static ComponentUI createUI( JComponent c ) {
        return new BasicMergeToolBarUI();
    }

    /**
     * Configures the specified component appropriate for the look and feel.
     * This method is invoked when the <code>ComponentUI</code> instance is being installed
     * as the UI delegate on the specified component.  This method should
     * completely configure the component for the look and feel,
     * including the following:
     * <ol>
     * <li>Install any default property values for color, fonts, borders,
     *     icons, opacity, etc. on the component.  Whenever possible,
     *     property values initialized by the client program should <i>not</i>
     *     be overridden.
     * </li><li>Install a <code>LayoutManager</code> on the component if necessary.
     * </li><li>Create/add any required sub-components to the component.
     * </li><li>Create/install event listeners on the component.
     * </li><li>Create/install a <code>PropertyChangeListener</code> on the component in order
     *     to detect and respond to component property changes appropriately.
     * </li><li>Install keyboard UI (mnemonics, traversal, etc.) on the component.
     * </li><li>Initialize any appropriate instance data.
     * </li></ol>
     */
    public void installUI( JComponent c ) {
        toolbar = ( MergeToolBar ) c;
        view = toolbar.getView();
        installDefaults();
        installComponents();
        installListeners();
    }

    /**
     * Install default values for colors, fonts, borders, etc.
     */
    public void installDefaults() {
        toolbar.setLayout( createLayoutManager() );
        toolbar.setBorder( new EmptyBorder( 1, 1, 1, 1 ) );
    }

    /**
     * Create and install any sub-components.
     */
    public void installComponents() {
        JPanel button_panel = new JPanel();

        // create buttons
        diff = new JButton( new ImageIcon( BasicMergeToolBarUI.class.getClassLoader().getResource( "jdiff/component/resources/delta.png" ) ) );
        next = new JButton( GUIUtilities.loadIcon( "ArrowD.png" ) );
        prev = new JButton( GUIUtilities.loadIcon( "ArrowU.png" ) );
        move_right = new JButton( GUIUtilities.loadIcon( "ArrowR.png" ) );
        move_left = new JButton( GUIUtilities.loadIcon( "ArrowL.png" ) );
        unsplit = new JButton( GUIUtilities.loadIcon( "UnSplit.png" ) );
        swap = new JButton( GUIUtilities.loadIcon( "SplitVertical.png" ) );
        refresh = new JButton( GUIUtilities.loadIcon( "Reload.png" ) );

        // tooltips
        diff.setToolTipText( jEdit.getProperty( "jdiff.diff-btn.label", "Diff" ) );
        next.setToolTipText( jEdit.getProperty( "jdiff.next-diff.label", "Go to next diff" ) );
        prev.setToolTipText( jEdit.getProperty( "jdiff.move-right.label", "Go to previous diff" ) );
        unsplit.setToolTipText( jEdit.getProperty( "jdiff.unsplit.label", "Unsplit" ) );
        swap.setToolTipText( jEdit.getProperty( "jdiff.swap-textareas", "Swap text areas" ) );
        move_right.setToolTipText( jEdit.getProperty( "jdiff.move-right.label", "Move diff to right" ) );
        move_left.setToolTipText( jEdit.getProperty( "jdiff.move-left.label", "Move diff to left" ) );
        refresh.setToolTipText( jEdit.getProperty( "jdiff.refresh.label", "Refresh diff" ) );

        toolbar.add( diff );
        toolbar.add( unsplit );
        toolbar.add( next );
        toolbar.add( move_right );
        toolbar.add( move_left );
        toolbar.add( prev );
        toolbar.add( swap );
        toolbar.add( refresh);
    }

    /**
     * Install any action listeners, mouse listeners, etc.
     */
    public void installListeners() {

        move_left.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( view != null ) {
                        DualDiff.moveLeft( BasicMergeToolBarUI.this.view.getEditPane() );
                    }
                }
            }
        );

        move_right.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( view != null ) {
                        DualDiff.moveRight( BasicMergeToolBarUI.this.view.getEditPane() );
                    }
                }
            }
        );

        next.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( view != null ) {
                        DualDiff.nextDiff( BasicMergeToolBarUI.this.view.getEditPane() );
                    }
                }
            }
        );

        prev.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( view != null ) {
                        DualDiff.prevDiff( BasicMergeToolBarUI.this.view.getEditPane() );
                    }
                }
            }
        );

        unsplit.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( view != null ) {
                        BasicMergeToolBarUI.this.view.unsplit();
                    }
                }
            }
        );

        swap.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( view != null ) {
                        EditPane left_ep = BasicMergeToolBarUI.this.view.getEditPanes() [ 0 ];
                        EditPane right_ep = BasicMergeToolBarUI.this.view.getEditPanes() [ 1 ];
                        Buffer left = left_ep.getBuffer();
                        Buffer right = right_ep.getBuffer();
                        left_ep.setBuffer( right );
                        right_ep.setBuffer( left );
                    }
                }
            }
        );

        diff.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    View view = toolbar.getView();
                    DualDiff.getDualDiffFor( view ).toggleFor( view );
                    toolbar.repaint();
                }
            }
        );

        refresh.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    View view = toolbar.getView();
                    DualDiff.getDualDiffFor( view ).refreshFor( view );
                    toolbar.repaint();
                }
            }
        );
    }

    /**
     * Tear down and clean up.
     */
    public void uninstallUI( JComponent c ) {
        c.setLayout( null );
        uninstallListeners();
        uninstallComponents();
        uninstallDefaults();
    }

    /**
     * Tear down and clean up.
     */
    public void uninstallDefaults() {}

    /**
     * Tear down and clean up.
     */
    public void uninstallComponents() {
    }

    /**
     * Tear down and clean up.
     */
    public void uninstallListeners() {
    }

    /**
     * @return a BorderLayout
     */
    protected LayoutManager createLayoutManager() {
        return new GridLayout(1, 8, 2, 2);
    }

}
