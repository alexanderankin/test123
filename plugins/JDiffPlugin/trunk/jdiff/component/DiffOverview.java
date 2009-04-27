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

import org.gjt.sp.jedit.jEdit;
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
        if (!jEdit.getBooleanProperty("jdiff.synchroscroll-on", true) ) {
            return;
        }
        Diff.Change hunk = edits;

        int leftFirstLine = textArea0.getFirstLine();
        int rightFirstLine = textArea1.getFirstLine();
        int rightMaxFirstLine = textArea1.getLineCount() - textArea1.getVisibleLines() + 1;

        if ( hunk == null ) {
            // no diffs, so scroll to same line number
            textArea1.setFirstLine( Math.min( leftFirstLine, rightMaxFirstLine ) );
            return ;
        }

        for ( ; hunk != null; hunk = hunk.next ) {
            // if before first hunk, scroll line per line
            if ( leftFirstLine < hunk.first0 && hunk.prev == null ) {
                textArea1.setFirstLine( leftFirstLine );
                return ;
            }

            // if in a hunk, scroll a proportional amount.
            if ( leftFirstLine >= hunk.first0 && leftFirstLine < hunk.first0 + hunk.lines0 ) {
                int distance = 0;
                // 5 cases:
                // 1. hunk.lines0 = 0, hunk.lines1 > 0
                // 2. hunk.lines0 > 0, hunk.lines1 = 0;
                // 3. hunk.lines0 =    hunk.lines1
                // 4. hunk.lines0 >    hunk.lines1
                // 5. hunk.lines0 <    hunk.lines1
                // I think I have these in the right order to avoid divide by 0 errors.
                // Note there is no possibility of hunk.lines0 and hunk.lines1
                // both being 0, the diff algorithm will not produce such a hunk.
                
                // case 1
                if ( hunk.lines0 == 0 && hunk.lines1 > 0 ) {
                    distance = rightFirstLine - hunk.first1;
                }
                // case 2
                else if ( hunk.lines0 > 0 && hunk.lines1 == 0 ) {
                    distance = 0;
                }
                // case 3
                else if ( hunk.lines0 == hunk.lines1 ) {
                    distance = leftFirstLine - hunk.first0;
                }
                // case 4
                else if ( hunk.lines0 > hunk.lines1 ) {
                    int left_increment = hunk.lines0 / hunk.lines1;
                    int right_increment = ( int ) Math.round( ( float ) ( hunk.lines1 * left_increment ) / ( float ) hunk.lines0 );
                    distance = ( ( leftFirstLine - hunk.first0 ) / left_increment ) * right_increment;
                }
                // case 5
                else if ( hunk.lines0 < hunk.lines1 ) {
                    int right_increment = hunk.lines1 / hunk.lines0;
                    int left_increment = ( int ) Math.round( ( float ) ( hunk.lines0 * right_increment ) / ( float ) hunk.lines1 );
                    distance = ( ( leftFirstLine - hunk.first0 ) / left_increment ) * right_increment;
                }
                // bad case
                else {
                    return;   
                }
                textArea1.setFirstLine( hunk.first1 + distance );
                return ;
            }

            // if between hunks, scroll line per line
            if ( leftFirstLine > hunk.last0 && ( hunk.next != null && leftFirstLine < hunk.next.first0 ) ) {
                int distance = leftFirstLine - hunk.last0;
                textArea1.setFirstLine( hunk.last1 + distance );
                return ;
            }

            // if after last hunk scroll line per line
            if ( leftFirstLine > hunk.last0 && hunk.next == null ) {
                int distance = leftFirstLine - hunk.last0;
                textArea1.setFirstLine( hunk.last1 + distance );
                return ;
            }
        }
    }


    public void synchroScrollLeft() {
        if (!jEdit.getBooleanProperty("jdiff.synchroscroll-on", true) ) {
            return;
        }
        Diff.Change hunk = edits;

        int leftFirstLine = textArea0.getFirstLine();
        int rightFirstLine = textArea1.getFirstLine();
        int leftMaxFirstLine = textArea0.getLineCount() - textArea0.getVisibleLines() + 1;

        if ( hunk == null ) {
            // no diffs, so scroll to same line number
            textArea0.setFirstLine( Math.min( rightFirstLine, leftMaxFirstLine ) );
            return ;
        }


        for ( ; hunk != null; hunk = hunk.next ) {
            // if before first hunk, scroll line per line
            if ( rightFirstLine < hunk.first1 && hunk.prev == null ) {
                textArea0.setFirstLine( rightFirstLine );
                return ;
            }

            // if in a hunk, scroll a proportional amount
            if ( rightFirstLine >= hunk.first1 && rightFirstLine < hunk.first1 + hunk.lines1 ) {
                int distance = 0;
                // 5 cases:
                // 1. hunk.lines1 = 0, hunk.lines0 > 0
                // 2. hunk.lines1 > 0, hunk.lines0 = 0;
                // 3. hunk.lines1 =    hunk.lines0
                // 4. hunk.lines1 >    hunk.lines0
                // 5. hunk.lines1 <    hunk.lines0
                // I think I have these in the right order to avoid divide by 0 errors.
                // Note there is no possibility of hunk.lines0 and hunk.lines1
                // both being 0, the diff algorithm will not produce such a hunk.
                
                // case 1
                if ( hunk.lines1 == 0 && hunk.lines0 > 0 ) {
                    distance = leftFirstLine - hunk.first0;
                }
                // case 2
                else if ( hunk.lines1 > 0 && hunk.lines0 == 0 ) {
                    distance = 0;
                }
                // case 3
                else if ( hunk.lines1 == hunk.lines0 ) {
                    distance = rightFirstLine - hunk.first1;
                }
                // case 4
                else if ( hunk.lines1 > hunk.lines0 ) {
                    int right_increment = hunk.lines1 / hunk.lines0;
                    int left_increment = ( int ) Math.round( ( float ) ( hunk.lines0 * right_increment ) / ( float ) hunk.lines1 );
                    distance = ( ( rightFirstLine - hunk.first1 ) / right_increment ) * left_increment;
                }
                // case 5
                else if ( hunk.lines1 < hunk.lines0 ) {
                    int left_increment = hunk.lines0 / hunk.lines1;
                    int right_increment = ( int ) Math.round( ( float ) ( hunk.lines1 * left_increment ) / ( float ) hunk.lines0 );
                    distance = ( ( rightFirstLine - hunk.first1 ) / right_increment ) * left_increment;
                }
                // bad case
                else {
                    return;   
                }
                textArea0.setFirstLine( hunk.first0 + distance );
                return ;
            }

            // if between hunks, scroll line per line
            if ( rightFirstLine > hunk.last1 && ( hunk.next != null && rightFirstLine < hunk.next.first1 ) ) {
                int distance = rightFirstLine - hunk.last1;
                textArea0.setFirstLine( hunk.last0 + distance );
                return ;
            }

            // if after last hunk, scroll line per line
            if ( rightFirstLine > hunk.last1 && hunk.next == null ) {
                int distance = rightFirstLine - hunk.last1;
                textArea0.setFirstLine( hunk.last0 + distance );
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