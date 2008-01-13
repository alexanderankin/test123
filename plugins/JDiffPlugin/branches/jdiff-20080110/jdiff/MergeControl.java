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
import org.gjt.sp.jedit.EditPane;

public class MergeControl extends JPanel {

    private EditPane editPane;

    public MergeControl( EditPane editPane, int direction ) {
        if (editPane == null) {
            throw new IllegalArgumentException("EditPane may not be null.");
        }
        if ( direction != SwingConstants.RIGHT && direction != SwingConstants.LEFT ) {
            throw new IllegalArgumentException( "invalid direction, must be SwingConstands.RIGHT or LEFT" );
        }
        this.editPane = editPane;
        int arrow_dir = direction == SwingConstants.RIGHT ? SwingConstants.EAST : SwingConstants.WEST;

        Dimension dim = getPreferredSize();
        dim.height = 18;
        setPreferredSize( dim );
        JButton next = new BasicArrowButton( SwingConstants.SOUTH );
        JButton prev = new BasicArrowButton( SwingConstants.NORTH );
        JButton move = new BasicArrowButton( arrow_dir );
        next.setToolTipText( "Go to next diff" );
        prev.setToolTipText( "Go to previous diff" );

        switch ( direction ) {
            case SwingConstants.RIGHT:
                setLayout( new FlowLayout( FlowLayout.RIGHT, 2, 0 ) );
                add( next );
                add( prev );
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
                setLayout( new FlowLayout( FlowLayout.LEFT, 2, 0 ) );
                add( move );
                add( prev );
                add( next );
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
    }
}
