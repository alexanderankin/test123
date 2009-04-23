/*
* DiffOverview.java - JComponent subclass providing a graphical overview of a
*                     Diff and synchronized scrolling
* Copyright (c) 2000 portions by mike dillon
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


package jdiff.component;

import javax.swing.JComponent;

import jdiff.DualDiff;
import jdiff.util.Diff;

import org.gjt.sp.jedit.textarea.JEditTextArea;

public abstract class DiffOverview extends JComponent {
    protected DualDiff dualDiff;
    private DiffTextAreaModel model = null;
    protected Diff.Change edits;
    protected int lineCount0;
    protected int lineCount1;
    protected JEditTextArea textArea0;
    protected JEditTextArea textArea1;

    public DiffOverview() {
        this( null );
    }

    public DiffOverview( DualDiff dualDiff ) {
        this.dualDiff = dualDiff;
        setModel( new DiffTextAreaModel( dualDiff ) );
    }

    public void setModel( DiffTextAreaModel model ) {
        this.model = model;
        edits = model.getEdits();
        lineCount0 = model.getLeftLineCount();
        lineCount1 = model.getRightLineCount();
        textArea0 = model.getLeftTextArea();
        textArea1 = model.getRightTextArea();
    }

    public DiffTextAreaModel getModel() {
        return model;
    }

    public void synchroScrollRight() {
        Diff.Change hunk = edits;

        int leftFirstLine = textArea0.getFirstLine();
        int rightMaxFirstLine = textArea1.getLineCount() - textArea1.getVisibleLines() + 1;

        if ( hunk == null ) {
            // no diffs, so scroll to same line number
            textArea1.setFirstLine( Math.min( leftFirstLine, rightMaxFirstLine ) );
            return ;
        }

        for ( ; hunk != null; hunk = hunk.next ) {
            // if left side is scrolled to bottom, scroll right to bottom
            if ( leftFirstLine + textArea0.getVisibleLines() >= textArea0.getLineCount() ) {
                // System.out.println( "+++++ left scrolling to bottom" );
                textArea1.setFirstLine( rightMaxFirstLine );
                return ;
            }

            // if before first hunk, scroll line per line
            if ( leftFirstLine < hunk.line0 && hunk.prev == null ) {
                // System.out.println( "+++++ left before first hunk" );
                textArea1.setFirstLine( leftFirstLine );
                return ;
            }

            // if in a hunk, scroll a proportional amount.
            if ( leftFirstLine >= hunk.line0 && leftFirstLine < hunk.line0 + hunk.deleted ) {
                float percent = ( float ) ( leftFirstLine - hunk.line0 ) / ( float ) hunk.deleted;
                int distance = ( int ) ( ( float ) ( hunk.inserted ) * percent );
                // System.out.println( "+++++ left in a hunk, distance = " + distance + ", percent = " + percent );
                textArea1.setFirstLine( hunk.line1 + distance );
                return ;
            }

            // if between hunks, scroll line per line
            if ( leftFirstLine > hunk.line0 + hunk.deleted && ( hunk.next != null && leftFirstLine < hunk.next.line0 ) ) {
                // System.out.println( "+++++ left between hunks" );
                int distance = leftFirstLine - ( hunk.line0 + hunk.deleted );
                textArea1.setFirstLine( hunk.line1 + hunk.inserted + distance );
                return ;
            }

            // if after last hunk scroll line per line
            if ( leftFirstLine > hunk.line0 + hunk.deleted && hunk.next == null ) {
                // System.out.println( "+++++ left after last hunk" );
                int distance = leftFirstLine - ( hunk.line0 + hunk.deleted );
                textArea1.setFirstLine( hunk.line1 + hunk.inserted + distance );
                return ;
            }
        }
    }


    public void synchroScrollLeft() {
        Diff.Change hunk = edits;

        int rightFirstLine = textArea1.getFirstLine();
        int leftMaxFirstLine = textArea0.getLineCount() - textArea0.getVisibleLines() + 1;

        if ( hunk == null ) {
            // no diffs, so scroll to same line number
            textArea0.setFirstLine( Math.min( rightFirstLine, leftMaxFirstLine ) );
            return ;
        }


        for ( ; hunk != null; hunk = hunk.next ) {
            // if right side is scrolled to bottom, scroll left to bottom
            if ( rightFirstLine + textArea1.getVisibleLines() >= textArea1.getLineCount() ) {
                // System.out.println( "+++++ right scrolling to bottom" );
                textArea0.setFirstLine( leftMaxFirstLine );
                return ;
            }

            // if before first hunk, scroll line per line
            if ( rightFirstLine < hunk.line1 && hunk.prev == null ) {
                // System.out.println( "+++++ right before first hunk" );
                textArea0.setFirstLine( rightFirstLine );
                return ;
            }

            // if in a hunk, scroll a proportional amount
            if ( rightFirstLine >= hunk.line1 && rightFirstLine < hunk.line1 + hunk.inserted ) {
                float percent = ( float ) ( rightFirstLine - hunk.line1 ) / ( float ) hunk.inserted;
                int distance = ( int ) ( ( float ) ( hunk.deleted ) * percent );
                // System.out.println( "+++++ right in a hunk, distance = " + distance + ", percent = " + percent );
                textArea0.setFirstLine( hunk.line0 + distance );
                return ;
            }

            // if between hunks, scroll line per line
            if ( rightFirstLine > hunk.line1 + hunk.inserted && ( hunk.next != null && rightFirstLine < hunk.next.line1 ) ) {
                // System.out.println( "+++++ right between hunks" );
                int distance = rightFirstLine - ( hunk.line1 + hunk.inserted );
                textArea0.setFirstLine( hunk.line0 + hunk.deleted + distance );
                return ;
            }

            // if after last hunk, scroll line per line
            if ( rightFirstLine > hunk.line1 + hunk.inserted && hunk.next == null ) {
                // System.out.println( "+++++ right after last hunk" );
                int distance = rightFirstLine - ( hunk.line1 + hunk.inserted );
                textArea0.setFirstLine( hunk.line0 + hunk.deleted + distance );
                return ;
            }
        }
    }

    /**
     * Default implementation does nothing, this is for subclasses to override.
     */
    public void moveRight( int line_number ) {}

    /**
     * Default implementation does nothing, this is for subclasses to override.
     */
    public void moveLeft( int line_number ) {}

}