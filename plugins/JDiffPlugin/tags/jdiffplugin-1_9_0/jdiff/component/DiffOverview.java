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
        Diff.Change hunk = this.edits;

        int leftFirstLine = this.textArea0.getFirstLine();
        int rightFirstLine = -1;

        if ( hunk == null ) {
            this.textArea1.setFirstLine( leftFirstLine );
            return ;
        }

        int prevLeftOffset = 0;
        int prevRightOffset = 0;
        int leftOffset = 0;
        int rightOffset = 0;
        for ( ; hunk != null; hunk = hunk.link ) {
            leftOffset = hunk.line0;
            rightOffset = hunk.line1;

            if ( ( leftFirstLine >= prevLeftOffset )
                    && ( leftFirstLine < leftOffset )
               ) {
                rightFirstLine = prevRightOffset + ( leftFirstLine - prevLeftOffset );
                break;
            }

            if ( ( leftFirstLine >= leftOffset )
                    && ( leftFirstLine < ( leftOffset + hunk.deleted ) )
               ) {
                rightFirstLine = rightOffset;
                break;
            }

            prevLeftOffset = leftOffset + hunk.deleted;
            prevRightOffset = rightOffset + hunk.inserted;

            if ( hunk.link == null ) {
                rightFirstLine = prevRightOffset + ( leftFirstLine - prevLeftOffset );
                break;
            }
        }

        if ( rightFirstLine >= 0 ) {
            this.textArea1.setFirstLine( rightFirstLine );
        }
    }


    public void synchroScrollLeft() {
        Diff.Change hunk = this.edits;

        int leftFirstLine = -1;
        int rightFirstLine = this.textArea1.getFirstLine();

        if ( hunk == null ) {
            this.textArea0.setFirstLine( rightFirstLine );
            return ;
        }

        int prevLeftOffset = 0;
        int prevRightOffset = 0;
        int leftOffset = 0;
        int rightOffset = 0;
        for ( ; hunk != null; hunk = hunk.link ) {
            leftOffset = hunk.line0;
            rightOffset = hunk.line1;

            if ( ( rightFirstLine >= prevRightOffset )
                    && ( rightFirstLine < rightOffset )
               ) {
                leftFirstLine = prevLeftOffset + ( rightFirstLine - prevRightOffset );
                break;
            }

            if ( ( rightFirstLine >= rightOffset )
                    && ( rightFirstLine < ( rightOffset + hunk.inserted ) )
               ) {
                leftFirstLine = leftOffset;
                break;
            }

            prevLeftOffset = leftOffset + hunk.deleted;
            prevRightOffset = rightOffset + hunk.inserted;

            if ( hunk.link == null ) {
                leftFirstLine = prevLeftOffset + ( rightFirstLine - prevRightOffset );
                break;
            }
        }

        if ( leftFirstLine >= 0 ) {
            this.textArea0.setFirstLine( leftFirstLine );
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

    /**
     */

}
