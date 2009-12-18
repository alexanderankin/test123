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


package jdiff.component;

import java.awt.Color;
import java.util.*;

import jdiff.*;
import jdiff.text.FileLine;
import jdiff.util.Diff;
import jdiff.util.JDiffDiff;

import org.gjt.sp.jedit.jEdit;

public class DiffLineModel {

    private String leftLine;
    private String rightLine;
    private List<Character> leftCharacters;
    private List<Color> leftColors;
    private List<Character> rightCharacters;
    private List<Color> rightColors;

    public DiffLineModel() {}

    public DiffLineModel( String leftLine, String rightLine ) {
        this.leftLine = leftLine;
        this.rightLine = rightLine;
        prepData();
    }

    public String toString() {
        return new StringBuffer().append("DiffLineModel[left[").append(leftLine).append("], right[").append(rightLine).append("]]").toString();
    }

    public List<Character> getLeftCharacters() {
        return leftCharacters;
    }

    public List<Color> getLeftColors() {
        return leftColors;
    }

    public List<Character> getRightCharacters() {
        return rightCharacters;
    }

    public List<Color> getRightColors() {
        return rightColors;
    }

    public void setLeftLine( String line ) {
        this.leftLine = line;
        prepData();
    }

    public String getLeftLine() {
        return leftLine;
    }

    public void setRightLine( String line ) {
        this.rightLine = line;
        prepData();
    }

    public String getRightLine() {
        return rightLine;
    }

    private void prepData() {
        leftCharacters = null;
        leftColors = null;
        rightCharacters = null;
        rightColors = null;

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
        Diff d = new JDiffDiff( leftLines, rightLines );
        Diff.Change edits = d.diff_2();

        if ( edits == null ) {
            // lines are identical
            return ;
        }

        // arrays to associate a color per character for the left line
        leftCharacters = new ArrayList<Character>();
        leftColors = new ArrayList<Color>();
        for ( int i = 0; i < leftLine.length(); i++ ) {
            leftCharacters.add( leftLine.charAt( i ) );
            leftColors.add( jEdit.getColorProperty("view.bgColor", Color.WHITE) );
        }

        // arrays to associate a color per character for the right line
        rightCharacters = new ArrayList<Character>();
        rightColors = new ArrayList<Color>();
        for ( int i = 0; i < rightLine.length(); i++ ) {
            rightCharacters.add( rightLine.charAt( i ) );
            rightColors.add( jEdit.getColorProperty("view.bgColor", Color.WHITE) );
        }

        // calculate the colors per character, use the same colors the user has
        // defined for the text area diff coloring.
        Color color;
        for ( Diff.Change hunk = edits; hunk != null; hunk = hunk.next ) {
            // left line colors
            if ( hunk.lines0 == 0 ) {
                color = JDiffPlugin.overviewInvalidColor;
            }
            else {
                if ( hunk.lines1 == 0 ) {
                    color = JDiffPlugin.overviewDeletedColor;
                }
                else {
                    color = JDiffPlugin.overviewChangedColor;
                }
            }
            for ( int i = 0; i < hunk.lines0; i++ ) {
                leftColors.set( hunk.first0 + i, color );
            }

            // right line colors
            if ( hunk.lines1 == 0 ) {
                color = JDiffPlugin.overviewInvalidColor;
            }
            else {
                if ( hunk.lines0 == 0 ) {
                    color = JDiffPlugin.overviewDeletedColor;
                }
                else {
                    color = JDiffPlugin.overviewChangedColor;
                }
            }
            for ( int i = 0; i < hunk.lines1; i++ ) {
                rightColors.set( hunk.first1 + i, color );
            }
        }
    }
}
