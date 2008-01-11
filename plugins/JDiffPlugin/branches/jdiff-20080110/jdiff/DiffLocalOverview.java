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

    private Rectangle border0;
    private Rectangle border1;
    private int pxlPerLine = 1;

    public DiffLocalOverview(
        Diff.change edits,
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
        int count0 = this.textArea0.getVisibleLines();
        int count1 = this.textArea1.getVisibleLines();

        // default width is 40 pixels, set in DiffOverview
        Rectangle size = getBounds();

        gfx.setColor( getBackground() );
        gfx.fillRect( 0, 0, size.width, size.height );

        // slightly narrower rectangle, leaves a 4 pixel border on the left and
        // right sides to separate the diff area from the text area and its
        // scroll bar.  This could be a little narrower, maybe even 1 since the
        // diff rectangles have a black border around them.
        Rectangle inner = new Rectangle( 4, 0, size.width - 8, size.height );   // width = 32

        int lines = Math.max( count0, count1 );
        pxlPerLine = this.textArea0.getPainter().getFontMetrics().getHeight();

        // for drawing the diffs for the left text area
        Rectangle rect0 = new Rectangle(
                    inner.x,                        // 4
                    inner.y,                        // 0
                    inner.width / 3,                // (40 - 4 - 4) / 3 = 8
                    Math.max( 1, pxlPerLine * count0 )
                );

        // border for the left rectangle
        border0 = new Rectangle( rect0 );

        // for drawing the diffs for the right text area
        Rectangle rect1 = new Rectangle(
                    inner.x + ( inner.width - ( inner.width / 3 ) ),                // 4 + (32 - 8) = 28
                    inner.y,                        // 0
                    inner.width / 3,                // 8
                    Math.max( 1, pxlPerLine * count1 )
                );

        // border for the right rectangle
        border1 = new Rectangle( rect1 );

        // make the left and right rectangles white
        gfx.setColor( Color.white );
        gfx.fillRect( rect0.x, rect0.y, rect0.width, rect0.height );
        gfx.fillRect( rect1.x, rect1.y, rect1.width, rect1.height );

        Color color;

        // arrows
        Polygon arrow0;
        Polygon arrow1;

        // fill in the left rectangle to show where left text area has different
        // lines than the right text area
        Diff.change hunk = this.edits;
        int start_line0 = 0;
        Color previous_color = null;

        for ( int i0 = 0; ( i0 < count0 ); i0++ ) {
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
                    rect0.height = 1;
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
                    // always be pxlPerLine:
                    //rect0.height = Math.max( 1, pxlPerLine );
                    rect0.height = pxlPerLine;
                }

                rect0.y = inner.y + ( i0 * pxlPerLine );
                gfx.setColor( color );
                gfx.fillRect( rect0.x, rect0.y, rect0.width, rect0.height );
                break;
            }
        }

        // fill in the right rectangle to show where right text area has different
        // lines than the left text area
        hunk = this.edits;
        for ( int i1 = 0; ( i1 < count1 ); i1++ ) {
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
                    rect1.height = 1;
                }
                else {
                    if ( hunk.deleted == 0 ) {
                        color = JDiffPlugin.overviewInsertedColor;
                    }
                    else {
                        color = JDiffPlugin.overviewChangedColor;
                    }

                    rect1.height = Math.max( 1, pxlPerLine );
                }

                rect1.y = inner.y + ( i1 * pxlPerLine );
                gfx.setColor( color );
                gfx.fillRect( rect1.x, rect1.y, rect1.width, rect1.height );
                break;
            }
        }

        // draw a line to connect corresponding differences in the left and
        // right rectangles.  Draw a right and left arrow.
        hunk = this.edits;
        for ( int i0 = 0, i1 = 0; ( hunk != null ) && ( i0 < count0 ) && ( i1 < count1 ); ) {
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

                int y0 = inner.y + ( i0 * pxlPerLine );
                int y1 = inner.y + ( i1 * pxlPerLine );

                // draw the lines
                gfx.setColor( Color.BLACK );
                gfx.drawLine( rect0.x + rect0.width + 1, y0, rect1.x - 1, y1 );

                // draw the "move it right" arrow
                if ( hunk.inserted == 0 || hunk.deleted > 0 ) {
                    arrow0 = new Polygon();
                    arrow0.addPoint( rect0.x + 1, y0 + 1 );
                    arrow0.addPoint( rect0.x + 1, y0 + pxlPerLine - 2 );
                    arrow0.addPoint( rect0.x + 7, y0 + ( pxlPerLine / 2 ) );
                    gfx.fillPolygon( arrow0 );
                }

                // draw the "move it left" arrow
                if ( hunk.deleted == 0 || hunk.inserted > 0 ) {
                    arrow1 = new Polygon();
                    arrow1.addPoint( rect1.x + 1, y1 + ( pxlPerLine / 2 ) );
                    arrow1.addPoint( rect1.x + 7, y1 + 1 );
                    arrow1.addPoint( rect1.x + 7, y1 + pxlPerLine - 2 );
                    gfx.setColor( Color.BLACK );
                    gfx.fillPolygon( arrow1 );
                }
            }
        }

        // draw the borders around the left and right rectangles
        gfx.setColor( Color.black );
        gfx.drawRect( border0.x - 1, border0.y, border0.width + 1, border0.height - 1 );
        gfx.drawRect( border1.x - 1, border1.y, border1.width + 1, border1.height - 1 );
    }

    public void mouseClicked( MouseEvent e ) {
        if ( border0.contains( e.getX(), e.getY() ) ) {
            // handle click on left side
            int line_number = ( e.getY() / pxlPerLine ) + textArea0.getFirstPhysicalLine();
            Diff.change hunk = this.edits;
            for ( ; hunk != null; hunk = hunk.link ) {
                // find the hunk pertaining to this line number
                if ( ( hunk.line0 + Math.max( 0, hunk.deleted - 1 ) ) < line_number ) {
                    continue;   // before this line
                }

                if ( hunk.line0 > line_number ) {
                    break;  // after this line, signals end of loop
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
            }
        }
        else if ( border1.contains( e.getX(), e.getY() ) ) {
            // handle click on right side
            int line_number = ( e.getY() / pxlPerLine ) + textArea1.getFirstPhysicalLine();
            Diff.change hunk = this.edits;
            for ( ; hunk != null; hunk = hunk.link ) {
                // find the hunk pertaining to this line number
                if ( ( hunk.line1 + Math.max( 0, hunk.inserted - 1 ) ) < line_number ) {
                    continue;   // before this line
                }

                if ( hunk.line1 > line_number ) {
                    break;  // after this line, signals end of loop
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
            }
        }
    }
    public void mouseEntered( MouseEvent e ) {}
    public void mouseExited( MouseEvent e ) {}
    public void mousePressed( MouseEvent e ) {}
    public void mouseReleased( MouseEvent e ) {}
}
