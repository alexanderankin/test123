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
            // if left side is scrolled to bottom, scroll right to bottom.
            if ( leftFirstLine + textArea0.getVisibleLines() >= textArea0.getLineCount() ) {
                textArea1.setFirstLine( rightMaxFirstLine );
                return ;
            }

            // if before first hunk, scroll line per line
            if ( leftFirstLine < hunk.line0 && hunk.prev == null ) {
                textArea1.setFirstLine( leftFirstLine );
                return ;
            }

            // if in a hunk, scroll a proportional amount.
            if ( leftFirstLine >= hunk.line0 && leftFirstLine < hunk.line0 + hunk.deleted ) {
                int distance = 0;
                // 5 cases:
                // 1. hunk.deleted = 0, hunk.inserted > 0
                // 2. hunk.deleted > 0, hunk.inserted = 0;
                // 3. hunk.deleted =    hunk.inserted
                // 4. hunk.deleted >    hunk.inserted
                // 5. hunk.deleted <    hunk.inserted
                // I think I have these in the right order to avoid divide by 0 errors.
                // Note there is no possibility of both hunk.deleted and hunk.inserted
                // both being 0, the diff algorithm will not produce such a hunk.
                if ( hunk.deleted == 0 && hunk.inserted > 0 ) {
                    distance = rightFirstLine - hunk.line1;
                }
                else if ( hunk.deleted > 0 && hunk.inserted == 0 ) {
                    distance = 0;
                }
                else if ( hunk.deleted == hunk.inserted ) {
                    distance = leftFirstLine - hunk.line0;
                }
                else if ( hunk.deleted > hunk.inserted ) {
                    int left_increment = hunk.deleted / hunk.inserted;
                    int right_increment = ( int ) Math.round( ( float ) ( hunk.inserted * left_increment ) / ( float ) hunk.deleted );
                    distance = ( ( leftFirstLine - hunk.line0 ) / left_increment ) * right_increment;
                }
                else if ( hunk.deleted < hunk.inserted ) {
                    int right_increment = hunk.inserted / hunk.deleted;
                    int left_increment = ( int ) Math.round( ( float ) ( hunk.deleted * right_increment ) / ( float ) hunk.inserted );
                    distance = ( ( leftFirstLine - hunk.line0 ) / left_increment ) * right_increment;
                }
                textArea1.setFirstLine( hunk.line1 + distance );
                return ;
            }

            // if between hunks, scroll line per line
            if ( leftFirstLine > hunk.line0 + hunk.deleted && ( hunk.next != null && leftFirstLine < hunk.next.line0 ) ) {
                int distance = leftFirstLine - ( hunk.line0 + hunk.deleted );
                textArea1.setFirstLine( hunk.line1 + hunk.inserted + distance );
                return ;
            }

            // if after last hunk scroll line per line
            if ( leftFirstLine > hunk.line0 + hunk.deleted && hunk.next == null ) {
                int distance = leftFirstLine - ( hunk.line0 + hunk.deleted );
                textArea1.setFirstLine( hunk.line1 + hunk.inserted + distance );
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
            // if right side is scrolled to bottom, scroll left to bottom
            if ( rightFirstLine + textArea1.getVisibleLines() >= textArea1.getLineCount() ) {
                textArea0.setFirstLine( leftMaxFirstLine );
                return ;
            }

            // if before first hunk, scroll line per line
            if ( rightFirstLine < hunk.line1 && hunk.prev == null ) {
                textArea0.setFirstLine( rightFirstLine );
                return ;
            }

            // if in a hunk, scroll a proportional amount
            if ( rightFirstLine >= hunk.line1 && rightFirstLine < hunk.line1 + hunk.inserted ) {
                int distance = 0;
                // 5 cases:
                // 1. hunk.inserted = 0, hunk.deleted > 0
                // 2. hunk.inserted > 0, hunk.deleted = 0;
                // 3. hunk.inserted =    hunk.deleted
                // 4. hunk.inserted >    hunk.deleted
                // 5. hunk.inserted <    hunk.deleted
                // I think I have these in the right order to avoid divide by 0 errors.
                // Note there is no possibility of both hunk.deleted and hunk.inserted
                // both being 0, the diff algorithm will not produce such a hunk.
                if ( hunk.inserted == 0 && hunk.deleted > 0 ) {
                    distance = leftFirstLine - hunk.line0;
                }
                else if ( hunk.inserted > 0 && hunk.deleted == 0 ) {
                    distance = 0;
                }
                else if ( hunk.inserted == hunk.deleted ) {
                    distance = rightFirstLine - hunk.line1;
                }
                else if ( hunk.inserted > hunk.deleted ) {
                    int right_increment = hunk.inserted / hunk.deleted;
                    int left_increment = ( int ) Math.round( ( float ) ( hunk.deleted * right_increment ) / ( float ) hunk.inserted );
                    distance = ( ( rightFirstLine - hunk.line1 ) / right_increment ) * left_increment;
                }
                else if ( hunk.inserted < hunk.deleted ) {
                    int left_increment = hunk.deleted / hunk.inserted;
                    int right_increment = ( int ) Math.round( ( float ) ( hunk.inserted * left_increment ) / ( float ) hunk.deleted );
                    distance = ( ( rightFirstLine - hunk.line1 ) / right_increment ) * left_increment;
                }
                textArea0.setFirstLine( hunk.line0 + distance );
                return ;
            }

            // if between hunks, scroll line per line
            if ( rightFirstLine > hunk.line1 + hunk.inserted && ( hunk.next != null && rightFirstLine < hunk.next.line1 ) ) {
                int distance = rightFirstLine - ( hunk.line1 + hunk.inserted );
                textArea0.setFirstLine( hunk.line0 + hunk.deleted + distance );
                return ;
            }

            // if after last hunk, scroll line per line
            if ( rightFirstLine > hunk.line1 + hunk.inserted && hunk.next == null ) {
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