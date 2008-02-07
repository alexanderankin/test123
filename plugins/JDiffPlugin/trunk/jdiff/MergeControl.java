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

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

/**
 * This is the button controls to assist in doing merges.
 */
public class MergeControl extends JPanel {

    private View view;

    public MergeControl( View view ) {
        super();

        if ( view == null ) {
            throw new IllegalArgumentException( "View may not be null." );
        }
        this.view = view;

        // create buttons
        JButton next = new JButton( GUIUtilities.loadIcon( "ArrowD.png" ) );
        JButton prev = new JButton( GUIUtilities.loadIcon( "ArrowU.png" ) );
        JButton move_right = new JButton( GUIUtilities.loadIcon( "ArrowR.png" ));
        JButton move_left = new JButton(GUIUtilities.loadIcon( "ArrowL.png" ) );
        JButton unsplit = new JButton( GUIUtilities.loadIcon( "UnSplit.png" ) );
        JButton swap = new JButton( GUIUtilities.loadIcon( "SplitVertical.png" ) );

        // tooltips
        next.setToolTipText( jEdit.getProperty("jdiff.next-diff.label", "Go to next diff") );
        prev.setToolTipText( jEdit.getProperty("jdiff.move-right.label", "Go to previous diff") );
        unsplit.setToolTipText( jEdit.getProperty("jdiff.unsplit.label", "Unsplit") );
        swap.setToolTipText( jEdit.getProperty("jdiff.swap-textareas", "Swap text areas") );
        move_right.setToolTipText( jEdit.getProperty("jdiff.move-right.label", "Move diff to right") );
        move_left.setToolTipText( jEdit.getProperty("jdiff.move-left.label", "Move diff to left") );


        // create toolbars
        JToolBar left_bar = new JToolBar();
        left_bar.setFloatable(false);
        left_bar.setRollover(true);
        left_bar.add( unsplit );
        left_bar.add( next );
        left_bar.add( move_right );

        JToolBar right_bar = new JToolBar();
        right_bar.setFloatable(false);
        right_bar.setRollover(true);
        right_bar.add( move_left );
        right_bar.add( prev );
        right_bar.add( swap );

        // create action listeners
        move_left.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.moveLeft( MergeControl.this.view.getEditPane() );
                }
            }
        );

        move_right.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.moveRight( MergeControl.this.view.getEditPane() );
                }
            }
        );

        next.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.nextDiff( MergeControl.this.view.getEditPane() );
                }
            }
        );

        prev.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    DualDiff.prevDiff( MergeControl.this.view.getEditPane() );
                }
            }
        );

        unsplit.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    MergeControl.this.view.unsplit();
                }
            }
        );
        swap.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    EditPane left_ep = MergeControl.this.view.getEditPanes() [ 0 ];
                    EditPane right_ep = MergeControl.this.view.getEditPanes() [ 1 ];
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
