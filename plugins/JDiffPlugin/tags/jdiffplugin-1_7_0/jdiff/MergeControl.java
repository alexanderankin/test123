/*
* MergeControl.java
* Copyright (c) 2008 Dale Anson
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

package jdiff;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicArrowButton;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * This is the button controls to assist in doing merges.
 */
public class MergeControl extends JPanel {

    private EditPane editPane;

    /**
     * @param editPane the EditPane to add this control to.
     * @param direction use SwingConstants.RIGHT for a control to move diffs from
     * the left to the right, use SwingConstans.LEFT for a control to move diffs
     * from the right to the left.
     */
    public MergeControl( EditPane editPane ) {
        super();

        if ( editPane == null ) {
            throw new IllegalArgumentException( "EditPane may not be null." );
        }
        this.editPane = editPane;

        // create buttons
        JButton next = new JButton( GUIUtilities.loadIcon( "ArrowD.png" ) );
        JButton prev = new JButton( GUIUtilities.loadIcon( "ArrowU.png" ) );
        JButton move_right = new JButton( GUIUtilities.loadIcon( "ArrowR.png" ));
        JButton move_left = new JButton(GUIUtilities.loadIcon( "ArrowL.png" ) );
        JButton unsplit = new JButton( GUIUtilities.loadIcon( "UnSplit.png" ) );
        JButton swap = new JButton( GUIUtilities.loadIcon( "SplitVertical.png" ) );

        // tooltips
        next.setToolTipText( "Go to next diff" );
        prev.setToolTipText( "Go to previous diff" );
        unsplit.setToolTipText( "Unsplit" );
        swap.setToolTipText( "Swap text areas" );
        move_right.setToolTipText( "Move diff to right" );
        move_left.setToolTipText( "Move diff to left" );


        // create toolbars
        JToolBar left_bar = new JToolBar();
        left_bar.add( unsplit );
        left_bar.add( next );
        left_bar.add( move_right );

        JToolBar right_bar = new JToolBar();
        right_bar.add( move_left );
        right_bar.add( prev );
        right_bar.add( swap );

        // create action listeners
        move_left.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.moveLeft( MergeControl.this.editPane );
                }
            }
        );

        move_right.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.moveRight( MergeControl.this.editPane );
                }
            }
        );

        next.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.nextDiff( MergeControl.this.editPane );
                }
            }
        );

        prev.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.prevDiff( MergeControl.this.editPane );
                }
            }
        );

        unsplit.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    MergeControl.this.editPane.getView().unsplit();
                }
            }
        );
        swap.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    EditPane left_ep = MergeControl.this.editPane.getView().getEditPanes() [ 0 ];
                    EditPane right_ep = MergeControl.this.editPane.getView().getEditPanes() [ 1 ];
                    Buffer left = left_ep.getBuffer();
                    Buffer right = right_ep.getBuffer();
                    left_ep.setBuffer( right );
                    right_ep.setBuffer( left );
                }
            }
        );

        // layout
        setLayout(new FlowLayout());
        add(left_bar);
        add(right_bar);
    }
}
