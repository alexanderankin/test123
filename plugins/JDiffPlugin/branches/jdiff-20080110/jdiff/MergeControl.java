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
 * This is the button controls that are added at the top of the text areas
 * to assist in doing merges.
 */
public class MergeControl extends JToolBar {

    private EditPane editPane;

    /**
     * @param editPane the EditPane to add this control to.
     * @param direction use SwingConstants.RIGHT for a control to move diffs from
     * the left to the right, use SwingConstans.LEFT for a control to move diffs
     * from the right to the left.
     */
    public MergeControl( EditPane editPane, int direction ) {
        super();
        setOrientation(SwingConstants.HORIZONTAL);
        setFloatable(false);

        if ( editPane == null ) {
            throw new IllegalArgumentException( "EditPane may not be null." );
        }
        if ( direction != SwingConstants.RIGHT && direction != SwingConstants.LEFT ) {
            throw new IllegalArgumentException( "invalid direction, must be SwingConstands.RIGHT or LEFT" );
        }
        this.editPane = editPane;



        // movement arrow
        int arrow_dir = direction == SwingConstants.RIGHT ? SwingConstants.EAST : SwingConstants.WEST;

        JButton next = new JButton( GUIUtilities.loadIcon( "ArrowD.png" ) );
        JButton prev = new JButton( GUIUtilities.loadIcon( "ArrowU.png" ) );
        JButton move = new JButton( GUIUtilities.loadIcon( direction == SwingConstants.RIGHT ? "ArrowR.png" : "ArrowL.png" ) );

        JButton unsplit = new JButton( GUIUtilities.loadIcon( "UnSplit.png" ) );
        JButton swap = new JButton( GUIUtilities.loadIcon( "SplitVertical.png" ) );

        // tooltips, the move tooltip is set below in the switch
        next.setToolTipText( "Go to next diff" );
        prev.setToolTipText( "Go to previous diff" );
        unsplit.setToolTipText( "Unsplit" );
        swap.setToolTipText( "Swap text areas" );

        // add the buttons to this panel, set the tooltip and action listener
        // for the move button
        switch ( direction ) {
            case SwingConstants.RIGHT:
                add( unsplit );
                add( next );
                add( move );
                move.setToolTipText( "Move diff to right" );
                move.addActionListener(
                    new ActionListener() {
                        public void actionPerformed( ActionEvent ae ) {
                            DualDiff.moveRight( MergeControl.this.editPane );
                        }
                    }
                );
                break;
            case SwingConstants.LEFT:
                add( move );
                add( prev );
                add( swap );
                move.setToolTipText( "Move diff to left" );
                move.addActionListener(
                    new ActionListener() {
                        public void actionPerformed( ActionEvent ae ) {
                            DualDiff.moveLeft( MergeControl.this.editPane );
                        }
                    }
                );
                break;
        }

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
                    EditPane left_ep = MergeControl.this.editPane.getView().getEditPanes()[0];
                    EditPane right_ep = MergeControl.this.editPane.getView().getEditPanes()[1];
                    Buffer left = left_ep.getBuffer();
                    Buffer right = right_ep.getBuffer();
                    left_ep.setBuffer(right);
                    right_ep.setBuffer(left);
                }
            }
        );
    }
}
