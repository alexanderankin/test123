
package jdiff.component;

import java.awt.Color;
import java.util.*;

import jdiff.*;
import jdiff.text.FileLine;
import jdiff.util.Diff;

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
        Diff d = new Diff( leftLines, rightLines );
        Diff.Change edits = d.diff_2( false );

        if ( edits == null ) {
            // lines are identical
            return ;
        }

        // arrays to associate a color per character for the left line
        leftCharacters = new ArrayList<Character>();
        leftColors = new ArrayList<Color>();
        for ( int i = 0; i < leftLine.length(); i++ ) {
            leftCharacters.add( leftLine.charAt( i ) );
            leftColors.add( Color.BLACK );
        }

        // arrays to associate a color per character for the right line
        rightCharacters = new ArrayList<Character>();
        rightColors = new ArrayList<Color>();
        for ( int i = 0; i < rightLine.length(); i++ ) {
            rightCharacters.add( rightLine.charAt( i ) );
            rightColors.add( Color.BLACK );
        }

        // calculate the colors per character, use the same colors the user has
        // defined for the text area diff coloring.
        Color color;
        for ( Diff.Change hunk = edits; hunk != null; hunk = hunk.link ) {
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
    }
}
