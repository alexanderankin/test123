/*
* DiffLocalOverview.java
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import jdiff.util.Diff;
import jdiff.DualDiff;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import org.gjt.sp.util.Log;

/**
 * danson:  There weren't ANY comments when I started work on adding merge
 * ability to this class.  The original class simply showed diffs, but didn't
 * have any code to move a change from one text area to the other.
 */
public class DiffLocalOverview extends DiffOverview implements MouseListener {

    private Rectangle leftBorder;
    private Rectangle rightBorder;
    private int pixelsPerLine = 1;
    private int leftVisibleLineCount;
    private int rightVisibleLineCount;
    private Rectangle leftRectangle;
    private Rectangle rightRectangle;
    private Rectangle centerRectangle;

    public DiffLocalOverview(
        Diff.Change edits,
        int lineCount0,
        int lineCount1,
        JEditTextArea textArea0,
        JEditTextArea textArea1
    ) {
        super( edits, lineCount0, lineCount1, textArea0, textArea1 );
        addMouseListener( this );
    }


    public void paint( Graphics gfx ) {
        // draw 3 rectangles the height of the text area.  The left rectangle
        // shows lines in the left text area that are different from those in
        // the right text area, the right rectangle shows lines in the right
        // text area that are different from those in the left text area, and
        // the center area connects the corresponding left and right changed
        // areas.
        leftVisibleLineCount = this.textArea0.getVisibleLines();
        rightVisibleLineCount = this.textArea1.getVisibleLines();
        pixelsPerLine = this.textArea0.getPainter().getFontMetrics().getHeight();

        // default width is 40 pixels, set in DiffOverview
        Rectangle size = getBounds();
        gfx.setColor( getBackground() );
        gfx.fillRect( 0, 0, size.width, size.height );

        // slightly narrower rectangle, leaves a 4 pixel border on the left and
        // right sides to separate the diff area from the text area and its
        // scroll bar.  This could be a little narrower, maybe even 1 since the
        // diff rectangles have a black border around them.
        centerRectangle = new Rectangle( 4, 0, size.width - 8, size.height );   // width = 32

        // for drawing the diffs for the left text area
        leftRectangle = new Rectangle(
                    centerRectangle.x,                            // 4
                    centerRectangle.y,                            // 0
                    centerRectangle.width / 3,                    // (40 - 4 - 4) / 3 = 8
                    Math.max( 1, pixelsPerLine * leftVisibleLineCount )
                );

        // for drawing the diffs for the right text area
        rightRectangle = new Rectangle(
                    centerRectangle.x + ( centerRectangle.width - ( centerRectangle.width / 3 ) ),     // 4 + (32 - 8) = 28
                    centerRectangle.y,                            // 0
                    centerRectangle.width / 3,                    // 8
                    Math.max( 1, pixelsPerLine * rightVisibleLineCount )
                );

        // borders for the left and right rectangles
        leftBorder = new Rectangle( leftRectangle );
        rightBorder = new Rectangle( rightRectangle );

        // make the left and right rectangles white
        gfx.setColor( Color.white );
        gfx.fillRect( leftRectangle.x, leftRectangle.y, leftRectangle.width, leftRectangle.height );
        gfx.fillRect( rightRectangle.x, rightRectangle.y, rightRectangle.width, rightRectangle.height );

        // draw the diff areas in the left and right rectangles, and draw the
        // connecting line between corresponding diffs in the center rectangle
        fillLeft( gfx );
        fillRight( gfx );
        fillCenter( gfx );

        // draw the borders around the left and right rectangles
        gfx.setColor( Color.black );
        gfx.drawRect( leftBorder.x - 1, leftBorder.y, leftBorder.width + 1, leftBorder.height - 1 );
        gfx.drawRect( rightBorder.x - 1, rightBorder.y, rightBorder.width + 1, rightBorder.height - 1 );
    }

    private void fillLeft( Graphics gfx ) {
        // fill in the left rectangle to show where left text area has different
        // lines than the right text area
        Diff.Change hunk = this.edits;
        int start_line0 = 0;
        Color color;

        for ( int i0 = 0; ( i0 < leftVisibleLineCount ); i0++ ) {
            // for each line in the left text area, get the current line to consider
            int physicalLine0 = this.textArea0.getPhysicalLineOfScreenLine( i0 );

            if ( physicalLine0 == -1 ) {
                continue;
            }

            for ( ; hunk != null; hunk = hunk.link ) {  // find the hunk pertaining to this line
                if ( ( hunk.line0 + Math.max( 0, hunk.deleted - 1 ) ) < physicalLine0 ) {
                    continue;   // before this line
                }

                if ( hunk.line0 > physicalLine0 ) {
                    break;  // after this line, signals end of loop
                }

                if ( hunk.deleted == 0 ) {
                    // 0 means no lines of the left text area were deleted, so
                    // some lines must have been inserted in the right text area
                    // at this location, or else there wouldn't be a diff.  Draw
                    // a 1 pixel tall line in the left rectangle to match up with
                    // the inserted block in the right rectangle
                    color = JDiffPlugin.overviewInvalidColor;
                    leftRectangle.height = 1;
                }
                else {
                    if ( hunk.inserted == 0 ) {
                        // 0 means no lines were inserted into the right text area
                        // so there are lines in the left text area that were removed
                        // from the right text area
                        color = JDiffPlugin.overviewDeletedColor;
                    }
                    else {
                        // if here then there are lines in both text areas that are
                        // different from the lines in the other text area.
                        color = JDiffPlugin.overviewChangedColor;
                    }

                    // next line is unnecessary, if, here, the height should
                    // always be pixelsPerLine:
                    //leftRectangle.height = Math.max( 1, pixelsPerLine );
                    leftRectangle.height = pixelsPerLine;
                }

                leftRectangle.y = centerRectangle.y + ( i0 * pixelsPerLine );
                gfx.setColor( color );
                gfx.fillRect( leftRectangle.x, leftRectangle.y, leftRectangle.width, leftRectangle.height );
                break;
            }
        }
    }

    private void fillRight( Graphics gfx ) {
        // fill in the right rectangle to show where right text area has different
        // lines than the left text area
        Color color;
        Diff.Change hunk = this.edits;
        for ( int i1 = 0; ( i1 < rightVisibleLineCount ); i1++ ) {
            int physicalLine1 = this.textArea1.getPhysicalLineOfScreenLine( i1 );

            if ( physicalLine1 == -1 ) {
                continue;
            }

            for ( ; hunk != null; hunk = hunk.link ) {
                if ( ( hunk.line1 + Math.max( 0, hunk.inserted - 1 ) ) < physicalLine1 ) {
                    continue;
                }

                if ( hunk.line1 > physicalLine1 ) {
                    break;
                }

                if ( hunk.inserted == 0 ) {
                    color = JDiffPlugin.overviewInvalidColor;
                    rightRectangle.height = 1;
                }
                else {
                    if ( hunk.deleted == 0 ) {
                        color = JDiffPlugin.overviewInsertedColor;
                    }
                    else {
                        color = JDiffPlugin.overviewChangedColor;
                    }

                    rightRectangle.height = Math.max( 1, pixelsPerLine );
                }

                rightRectangle.y = centerRectangle.y + ( i1 * pixelsPerLine );
                gfx.setColor( color );
                gfx.fillRect( rightRectangle.x, rightRectangle.y, rightRectangle.width, rightRectangle.height );
                break;
            }
        }
    }

    private void fillCenter( Graphics gfx ) {
        // draw a line to connect corresponding differences in the left and
        // right rectangles.  Draw a right and left arrow.
        Polygon arrow0;
        Polygon arrow1;
        Diff.Change hunk = this.edits;
        for ( int i0 = 0, i1 = 0; ( hunk != null ) && ( i0 < leftVisibleLineCount ) && ( i1 < rightVisibleLineCount ); ) {
            int physicalLine0 = this.textArea0.getPhysicalLineOfScreenLine( i0 );
            int physicalLine1 = this.textArea1.getPhysicalLineOfScreenLine( i1 );

            if ( physicalLine0 == -1 ) {
                break;
            }
            if ( physicalLine1 == -1 ) {
                break;
            }

            for ( ; hunk != null; hunk = hunk.link ) {
                if ( hunk.line0 < physicalLine0 ) {
                    continue;
                }

                if ( hunk.line1 < physicalLine1 ) {
                    continue;
                }

                if ( ( hunk.line0 + hunk.deleted ) < physicalLine0 ) {
                    continue;
                }

                if ( ( hunk.line1 + hunk.inserted ) < physicalLine1 ) {
                    continue;
                }

                if ( hunk.line0 > physicalLine0 ) {
                    i0++;
                    break;
                }

                if ( hunk.line1 > physicalLine1 ) {
                    i1++;
                    break;
                }

                int y0 = centerRectangle.y + ( i0 * pixelsPerLine ) + 1;
                int y1 = centerRectangle.y + ( i1 * pixelsPerLine ) + 1;

                // draw the lines
                gfx.setColor( Color.BLACK );
                gfx.drawLine( leftRectangle.x + leftRectangle.width + 1, y0, rightRectangle.x - 1, y1 );

                // draw the "move it right" arrow
                if ( hunk.inserted == 0 || hunk.deleted > 0 ) {
                    arrow0 = new Polygon();
                    arrow0.addPoint( leftRectangle.x + 1, y0 + 1 );
                    arrow0.addPoint( leftRectangle.x + 1, y0 + pixelsPerLine - 2 );
                    arrow0.addPoint( leftRectangle.x + 7, y0 + ( pixelsPerLine / 2 ) );
                    gfx.fillPolygon( arrow0 );
                }

                // draw the "move it left" arrow
                if ( hunk.deleted == 0 || hunk.inserted > 0 ) {
                    arrow1 = new Polygon();
                    arrow1.addPoint( rightRectangle.x + 1, y1 + ( pixelsPerLine / 2 ) );
                    arrow1.addPoint( rightRectangle.x + 7, y1 + 1 );
                    arrow1.addPoint( rightRectangle.x + 7, y1 + pixelsPerLine - 2 );
                    gfx.setColor( Color.BLACK );
                    gfx.fillPolygon( arrow1 );
                }
            }
        }
    }

    public void mouseClicked( MouseEvent e ) {
        if ( leftBorder.contains( e.getX(), e.getY() ) ) {
            // handle click on left side
            int line_number = ( e.getY() / pixelsPerLine ) + textArea0.getFirstPhysicalLine();
            moveRight( line_number );
        }
        else if ( rightBorder.contains( e.getX(), e.getY() ) ) {
            // handle click on right side
            int line_number = ( e.getY() / pixelsPerLine ) + textArea1.getFirstPhysicalLine();
            moveLeft( line_number );
        }
    }

    // copies a diff starting at the given line number in the left text area and
    // replaces the corresponding diff in the right text area
    public void moveRight( int line_number ) {
        Diff.Change hunk = this.edits;
        for ( ; hunk != null; hunk = hunk.link ) {
            // find the hunk pertaining to this line number
            if ( ( hunk.line0 + Math.max( 0, hunk.deleted - 1 ) ) < line_number ) {
                continue;   // before this line, keep looking
            }

            if ( hunk.line0 > line_number ) {
                // after this line, didn't find a line with a corresponding hunk
                this.textArea0.getToolkit().beep();
                break;
            }

            // on a line with a right arrow --
            // get the text from the left text area to move to the right
            String line_separator = textArea1.getBuffer().getStringProperty( "lineSeparator" );
            StringBuffer sb = new StringBuffer();
            for ( int i = 0; i < hunk.deleted; i++ ) {
                sb.append( textArea0.getLineText( hunk.line0 + i ) ).append( line_separator );
            }

            // replace text on right with text from left
            textArea1.selectNone();
            int start_sel = textArea1.getLineStartOffset( hunk.line1 );
            int end_sel = textArea1.getLineStartOffset( hunk.line1 + hunk.inserted );
            textArea1.setCaretPosition( start_sel );
            Selection.Range selection;
            if ( hunk.inserted == 0 ) {
                selection = new Selection.Range( start_sel, start_sel );
            }
            else {
                selection = new Selection.Range( start_sel, end_sel );
            }
            textArea1.setSelectedText( selection, sb.toString() );
            textArea1.selectNone();
            DualDiff.refreshFor( textArea1.getView() );
            break;
        }
    }

    // copies a diff starting at the given line number in the right text area and
    // replaces the corresponding diff in the left text area
    public void moveLeft( int line_number ) {
        Diff.Change hunk = this.edits;
        for ( ; hunk != null; hunk = hunk.link ) {
            // find the hunk pertaining to this line number
            if ( ( hunk.line1 + Math.max( 0, hunk.inserted - 1 ) ) < line_number ) {
                continue;   // before this line, keep looking
            }

            if ( hunk.line1 > line_number ) {
                // after this line, didn't find a line with a corresponding hunk
                this.textArea1.getToolkit().beep();
                break;
            }

            // on a line with a left arrow --
            // get the text from the right text area to move to the left
            String line_separator = textArea0.getBuffer().getStringProperty( "lineSeparator" );
            StringBuffer sb = new StringBuffer();
            for ( int i = 0; i < hunk.inserted; i++ ) {
                sb.append( textArea1.getLineText( hunk.line1 + i ) ).append( line_separator );
            }

            // replace text on left with text from right
            textArea0.selectNone();
            int start_sel = textArea0.getLineStartOffset( hunk.line0 );
            int end_sel = textArea0.getLineStartOffset( hunk.line0 + hunk.deleted );
            textArea0.setCaretPosition( start_sel );
            Selection.Range selection;
            if ( hunk.deleted == 0 ) {
                selection = new Selection.Range( start_sel, start_sel );
            }
            else {
                selection = new Selection.Range( start_sel, end_sel );
            }
            textArea0.setSelectedText( selection, sb.toString() );
            textArea0.selectNone();
            DualDiff.refreshFor( textArea0.getView() );
            break;
        }
    }

    public void mouseEntered( MouseEvent e ) {}
    public void mouseExited( MouseEvent e ) {}
    public void mousePressed( MouseEvent e ) {}
    public void mouseReleased( MouseEvent e ) {}
}
