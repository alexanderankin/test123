/*
* DiffGlobalPhysicalOverview.java
* Copyright (c) 2000, 2001, 2002 Andre Kaplan
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
import java.util.*;

import jdiff.util.Diff;
import jdiff.text.FileLine;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

/**
 * Performs and displays a character-by-character diff of two lines of text.
 */
public class DiffLineOverview extends JPanel implements LineProcessor {
    private DualDiff dualDiff;
    private Diff.change edits;
    private MergeControl mergeControl;
    private JPanel linePanel;

    private int leftMargin = 6;


    public DiffLineOverview( DualDiff dualDiff ) {
        if ( dualDiff == null ) {
            throw new IllegalArgumentException();
        }
        this.dualDiff = dualDiff;
        setBackground( Color.WHITE );
        setLayout( new BorderLayout() );
        mergeControl = new MergeControl( dualDiff.getView().getEditPanes() [ 0 ] );
        add( mergeControl, BorderLayout.NORTH );

        linePanel = new JPanel();
        linePanel.setBackground( dualDiff.getBackground() );
        add( linePanel, BorderLayout.CENTER );
    }

    /**
     * Performs a character-by-character diff of two lines of text and show
     * the results to this panel.
     * @param leftLine a line of text from the left edit pane
     * @param rightLine a line of text from the right edit pane
     */
    public void processLines( String leftLine, String rightLine ) {
        if (leftLine == null || rightLine == null) {
            return;
        }

        // prep for drawing the two lines, use the same font and background
        // color as the main text areas
        Graphics gfx = getGraphics();
        gfx.translate( 0, mergeControl.getSize().height );
        Font font = dualDiff.getFont();
        Font bold = font.deriveFont( Font.BOLD );
        gfx.setFont( font );
        FontMetrics fm = gfx.getFontMetrics();

        // clear the display area
        Rectangle all = linePanel.getBounds();
        gfx.setColor( dualDiff.getBackground() );
        gfx.fillRect( 0, 0, all.width, all.height );

        // diff the lines by character
        FileLine[] leftLines = new FileLine[ leftLine.length() ];
        FileLine[] rightLines = new FileLine[ rightLine.length() ];
        for ( int i = 0; i < leftLine.length(); i++ ) {
            char c = leftLine.charAt( i );
            String text = String.valueOf( c );
            leftLines[ i ] = new FileLine( text, text );
        }
        for ( int i = 0; i < rightLine.length(); i++ ) {
            char c = rightLine.charAt( i );
            String text = String.valueOf( c );
            rightLines[ i ] = new FileLine( text, text );
        }
        Diff d = new Diff( leftLines, rightLines );
        edits = d.diff_2( false );

        if ( edits == null ) {
            // lines are identical
            return ;
        }

        // arrays to associate a color per character for the left line
        ArrayList<Character> leftChars = new ArrayList<Character>();
        ArrayList<Color> leftColors = new ArrayList<Color>();
        for ( int i = 0; i < leftLine.length(); i++ ) {
            leftChars.add( leftLine.charAt( i ) );
            leftColors.add( Color.BLACK );
        }

        // arrays to associate a color per character for the right line
        ArrayList<Character> rightChars = new ArrayList<Character>();
        ArrayList<Color> rightColors = new ArrayList<Color>();
        for ( int i = 0; i < rightLine.length(); i++ ) {
            rightChars.add( rightLine.charAt( i ) );
            rightColors.add( Color.BLACK );
        }

        // calculate the colors per character, use the same colors the user has
        // defined for the text area diff coloring.
        Color color;
        for ( Diff.change hunk = edits; hunk != null; hunk = hunk.link ) {
            // left line colors
            if ( hunk.deleted == 0 ) {
                color = JDiffPlugin.overviewInvalidColor;
            }
            else {
                if ( hunk.inserted == 0 ) {
                    color = JDiffPlugin.overviewDeletedColor;
                }
                else {
                    color = JDiffPlugin.overviewChangedColor;
                }
            }
            for ( int i = 0; i < hunk.deleted; i++ ) {
                leftColors.set( hunk.line0 + i, color );
            }

            // right line colors
            if ( hunk.inserted == 0 ) {
                color = JDiffPlugin.overviewInvalidColor;
            }
            else {
                if ( hunk.deleted == 0 ) {
                    color = JDiffPlugin.overviewDeletedColor;
                }
                else {
                    color = JDiffPlugin.overviewChangedColor;
                }
            }
            for ( int i = 0; i < hunk.inserted; i++ ) {
                rightColors.set( hunk.line1 + i, color );
            }
        }

        // draw the characters, left line above the right line
        int x = leftMargin;
        gfx.setColor(Color.BLACK);
        gfx.drawString("Left", x, fm.getHeight());
        int left_width = fm.stringWidth(leftLine);
        int tick_height = fm.getHeight() / 2;
        int y = fm.getHeight() + tick_height;
        gfx.drawLine(x, y, x + left_width, y);
        gfx.drawLine(x, y, x, y + tick_height);
        gfx.drawLine(x + left_width, y, x + left_width, y + tick_height);

        x = leftMargin;
        int y0 = 3 * fm.getHeight();
        int y1 = y0 + fm.getHeight() + 3;
        for ( int i = 0; i < leftChars.size(); i++ ) {
            char c = leftChars.get( i );
            color = leftColors.get( i );
            gfx.setColor( color );
            gfx.setFont( color == Color.BLACK ? font : bold );
            gfx.drawString( String.valueOf( c ), x, y0 );
            x += fm.charWidth( c );
        }
        x = leftMargin;
        for ( int i = 0; i < rightChars.size(); i++ ) {
            char c = rightChars.get( i );
            color = rightColors.get( i );
            gfx.setColor( color );
            gfx.setFont( color == Color.BLACK ? font : bold );
            gfx.drawString( String.valueOf( c ), x, y1 );
            x += fm.charWidth( c );
        }

        x = leftMargin;
        gfx.setColor(Color.BLACK);
        gfx.setFont(font);
        gfx.drawString("Right", x, 6 * fm.getHeight());
        int right_width = fm.stringWidth(rightLine);
        y = y1 + (fm.getHeight() / 2);
        gfx.drawLine(x, y, x + right_width, y);
        gfx.drawLine(x, y, x, y - tick_height);
        gfx.drawLine(x + right_width, y, x + right_width, y - tick_height);
    }
}
