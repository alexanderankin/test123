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

package jdiff.component.ui;

import java.awt.*;
import java.awt.event.*;

import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ComponentUI;

import jdiff.DualDiff;
import jdiff.JDiffPlugin;
import jdiff.component.*;
import jdiff.util.Diff;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

public class BasicDiffLocalOverviewUI extends DiffLocalOverviewUI implements MouseListener, ChangeListener, CaretListener {

    private DiffLocalOverview diffLocalOverview;
    private LocalRendererPane localRendererPane;

    private Rectangle leftBorder;
    private Rectangle rightBorder;
    private int pixelsPerLine = 1;
    private int leftVisibleLineCount;
    private int rightVisibleLineCount;
    private Rectangle leftRectangle;
    private Rectangle rightRectangle;
    private Rectangle centerRectangle;

    public static ComponentUI createUI( JComponent c ) {
        return new BasicDiffLocalOverviewUI();
    }

    public void installUI( JComponent c ) {
        diffLocalOverview = ( DiffLocalOverview ) c;
        diffLocalOverview.setLayout( createLayoutManager() );
        installDefaults();
        installComponents();
        installListeners();

    }

    public void uninstallUI( JComponent c ) {
        c.setLayout( null );
        uninstallListeners();
        uninstallComponents();
        uninstallDefaults();

        diffLocalOverview = null;
    }

    public void installDefaults() {}

    public void installComponents() {
        localRendererPane = new LocalRendererPane();
        diffLocalOverview.add( localRendererPane, BorderLayout.CENTER );
    }

    public void installListeners() {
        diffLocalOverview.addMouseListener( this );
        diffLocalOverview.addChangeListener( this );
        diffLocalOverview.getModel().getLeftTextArea().addCaretListener( this );
        diffLocalOverview.getModel().getRightTextArea().addCaretListener( this );
    }

    public void uninstallDefaults() {}

    public void uninstallComponents() {
        diffLocalOverview.remove( localRendererPane );
        diffLocalOverview = null;
    }

    public void uninstallListeners() {
        diffLocalOverview.removeMouseListener( this );
        diffLocalOverview.removeChangeListener( this );
        diffLocalOverview.getModel().getLeftTextArea().removeCaretListener( this );
        diffLocalOverview.getModel().getRightTextArea().removeCaretListener( this );
    }

    protected LayoutManager createLayoutManager() {
        return new BorderLayout();
    }

    public void stateChanged( ChangeEvent event ) {
        localRendererPane.repaint();
    }

    public class LocalRendererPane extends JPanel {

        public LocalRendererPane( ) {
            Dimension dim = getPreferredSize();
            dim.width = 36;
            setPreferredSize( dim );
        }

        public void paintComponent( Graphics gfx ) {
            super.paintComponent( gfx );

            DiffTextAreaModel model = diffLocalOverview.getModel();
            if ( model == null ) {
                return ;
            }

            // draw 3 rectangles the height of the text area.  The left rectangle
            // shows lines in the left text area that are different from those in
            // the right text area, the right rectangle shows lines in the right
            // text area that are different from those in the left text area, and
            // the center area connects the corresponding left and right changed
            // areas.
            leftVisibleLineCount = model.getLeftTextArea().getVisibleLines();
            rightVisibleLineCount = model.getRightTextArea().getVisibleLines();
            pixelsPerLine = model.getLeftTextArea().getPainter().getFontMetrics().getHeight();

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
                        centerRectangle.x,                                        // 4
                        centerRectangle.y,                                        // 0
                        centerRectangle.width / 3,                                // (40 - 4 - 4) / 3 = 8
                        Math.max( 1, pixelsPerLine * leftVisibleLineCount )
                    );

            // for drawing the diffs for the right text area
            rightRectangle = new Rectangle(
                        centerRectangle.x + ( centerRectangle.width - ( centerRectangle.width / 3 ) ),                 // 4 + (32 - 8) = 28
                        centerRectangle.y,                                        // 0
                        centerRectangle.width / 3,                                // 8
                        Math.max( 1, pixelsPerLine * rightVisibleLineCount )
                    );

            // borders for the left and right rectangles
            leftBorder = new Rectangle( leftRectangle );
            rightBorder = new Rectangle( rightRectangle );

            // make the left and right rectangles white
            gfx.setColor( Color.white );
            gfx.fillRect( leftRectangle.x, leftRectangle.y, leftRectangle.width, leftRectangle.height );
            gfx.fillRect( rightRectangle.x, rightRectangle.y, rightRectangle.width, rightRectangle.height );

            // clear hunk cursors
            paintCurrentHunkCursor(gfx, null);

            // draw the diff areas in the left and right rectangles, and draw the
            // connecting line between corresponding diffs in the center rectangle
            fillLeft( gfx, model );
            fillRight( gfx, model );
            fillCenter( gfx, model );

            // draw the borders around the left and right rectangles
            gfx.setColor( Color.black );
            gfx.drawRect( leftBorder.x - 1, leftBorder.y, leftBorder.width + 1, leftBorder.height - 1 );
            gfx.drawRect( rightBorder.x - 1, rightBorder.y, rightBorder.width + 1, rightBorder.height - 1 );
        }
    }

    // draw the diff areas in the left rectangle
    private void fillLeft( Graphics gfx, DiffTextAreaModel model ) {
        // get the visible lines, only need to draw hunks between these lines
        int leftFirstLine = model.getLeftTextArea().getFirstPhysicalLine();
        int leftLastLine = model.getLeftTextArea().getLastPhysicalLine();

        // map of line number to hunk
        HashMap<Integer, Diff.Change> leftHunkMap = model.getLeftHunkMap();

        // output color for diff area
        Color color;

        if (leftHunkMap != null) {
            // go through each of the visible lines, see if there is a corresponding
            // diff for that line
            for ( int i = leftFirstLine; i <= leftLastLine; i++ ) {
                Diff.Change hunk = leftHunkMap.get( i );
                int first_visible_line = 0;
                int visible_lines = 0;
                if ( hunk != null ) {
                    // found a diff for a line, set the color and size.  Set the
                    // size all at once for the height of the hunk to minimize looping.
                    if ( hunk.deleted == 0 ) {
                        color = JDiffPlugin.overviewInvalidColor;
                        leftRectangle.height = 1;
                        leftRectangle.y = centerRectangle.y + ( ( hunk.line0 - leftFirstLine ) * pixelsPerLine );
                    }
                    else {
                        color = hunk.inserted == 0 ? JDiffPlugin.overviewDeletedColor : JDiffPlugin.overviewChangedColor;
                        // might be in the middle of a hunk because the hunk is
                        // scrolling off the top of the screen
                        visible_lines = hunk.line0 >= leftFirstLine ? hunk.deleted : Math.max(1, hunk.last0 - leftFirstLine + 1);
                        first_visible_line = visible_lines == 1 ? hunk.line0 : hunk.last0 - visible_lines + 1;
                        leftRectangle.height = Math.max( 1, pixelsPerLine * visible_lines );
                        leftRectangle.y = centerRectangle.y + ( ( first_visible_line - leftFirstLine ) * pixelsPerLine );
                    }
                    gfx.setColor( color );

                    // draw the hunk
                    System.out.println("+++++ leftRectangle.height = " + leftRectangle.height);
                    gfx.fillRect( leftRectangle.x, leftRectangle.y, leftRectangle.width, leftRectangle.height );

                    // skip any other lines covered by this hunk
                    i += visible_lines;
                }
            }

            // if the left text area is the active area and the caret line for the
            // left text area is in a hunk, draw the hunk cursor to indicate the
            // current hunk.  Don't trigger a paintCurrentHunkCursor just because
            // the caret line is in a hunk, the caret doesn't move in the inactive
            // text area, the caret in the inactive text area shouldn't trigger a
            // hunk cursor repaint, only the caret in the active text area should
            // do that.
            int caret_line = model.getLeftTextArea().getCaretLine();
            Diff.Change hunk = leftHunkMap.get(caret_line);
            // here's a NPE waiting to happen...
            if (hunk != null && model.getLeftTextArea().getView().getEditPane().getTextArea().equals(model.getLeftTextArea())) {
                paintCurrentHunkCursor(gfx, hunk);
            }
        }
    }

    private void fillRight( Graphics gfx, DiffTextAreaModel model ) {
        int rightFirstLine = model.getRightTextArea().getFirstPhysicalLine();
        int rightLastLine = model.getRightTextArea().getLastPhysicalLine();
        HashMap<Integer, Diff.Change> rightHunkMap = model.getRightHunkMap();
        Color color;
        if (rightHunkMap != null) {
            for ( int i = rightFirstLine; i <= rightLastLine; i++ ) {
                Diff.Change hunk = rightHunkMap.get( i );
                int first_visible_line = 0;
                int visible_lines = 0;
                if ( hunk != null ) {
                    if ( hunk.inserted == 0 ) {
                        color = JDiffPlugin.overviewInvalidColor;
                        rightRectangle.height = 1;
                        rightRectangle.y = centerRectangle.y + ( ( hunk.line1 - rightFirstLine ) * pixelsPerLine );
                    }
                    else {
                        color = hunk.deleted == 0 ? JDiffPlugin.overviewInsertedColor : JDiffPlugin.overviewChangedColor;
                        // might be in the middle of a hunk because the hunk is
                        // scrolling off the top of the screen
                        visible_lines = hunk.line1 >= rightFirstLine ? hunk.inserted : Math.max(1, hunk.last1 - rightFirstLine + 1);
                        first_visible_line = visible_lines == 1 ? hunk.line1 : hunk.last1 - visible_lines + 1;
                        rightRectangle.height = Math.max( 1, pixelsPerLine * visible_lines );
                        rightRectangle.y = centerRectangle.y + ( ( first_visible_line - rightFirstLine ) * pixelsPerLine );
                    }
                    gfx.setColor( color );
                    gfx.fillRect( rightRectangle.x, rightRectangle.y, rightRectangle.width, rightRectangle.height );
                    i += visible_lines;
                }
            }
            int caret_line = model.getRightTextArea().getCaretLine();
            Diff.Change hunk = rightHunkMap.get(caret_line);
            if (hunk != null && model.getRightTextArea().getView().getEditPane().getTextArea().equals(model.getRightTextArea())) {
                paintCurrentHunkCursor(gfx, hunk);
            }
        }
    }

    private void fillCenter( Graphics gfx, DiffTextAreaModel model ) {
        // draw a line to connect corresponding differences in the left and
        // right rectangles.  Draw a right and left arrow.
        // TODO:  optimize to use the hunk maps from the model rather than looping
        Diff.Change hunk = model.getEdits();
        JEditTextArea textArea0 = model.getLeftTextArea();
        JEditTextArea textArea1 = model.getRightTextArea();
        for ( int i0 = 0, i1 = 0; ( hunk != null ) && ( i0 < leftVisibleLineCount ) && ( i1 < rightVisibleLineCount ); ) {
            int physicalLine0 = textArea0.getPhysicalLineOfScreenLine( i0 );
            int physicalLine1 = textArea1.getPhysicalLineOfScreenLine( i1 );

            if ( physicalLine0 == -1 || physicalLine1 == -1 ) {
                break;
            }

            for ( ; hunk != null; hunk = hunk.link ) {
                if ( hunk.line0 < physicalLine0 || hunk.line1 < physicalLine1 ) {
                    continue;
                }

                if ( ( hunk.line0 + hunk.deleted ) < physicalLine0 || ( hunk.line1 + hunk.inserted ) < physicalLine1 ) {
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

                int y0 = centerRectangle.y + ( i0 * pixelsPerLine );
                int y1 = centerRectangle.y + ( i1 * pixelsPerLine );

                // draw the "move it right" arrow
                gfx.setColor( Color.BLACK );
                int arrow_height = ( pixelsPerLine - 2 ) % 2 == 0 ? pixelsPerLine - 3 : pixelsPerLine - 2;
                int center = arrow_height / 2 + 1;
                if ( hunk.inserted == 0 || hunk.deleted > 0 ) {
                    for ( int i = 0; i < 6; i++ ) {
                        gfx.drawLine( leftRectangle.x + 2 + i, y0 + 1 + i, leftRectangle.x + 2 + i, y0 + i + arrow_height - ( 2 * i ) );
                    }
                    y0 += center;
                }

                // draw the "move it left" arrow
                if ( hunk.deleted == 0 || hunk.inserted > 0 ) {
                    for ( int i = 0; i < 6; i++ ) {
                        gfx.drawLine( rightRectangle.x + 1 + i, y1 + center - i, rightRectangle.x + 1 + i, y1 + center + i );
                    }
                    y1 += center;
                }

                // draw the lines
                gfx.drawLine( leftRectangle.x + leftRectangle.width + 1, y0, rightRectangle.x - 1, y1 );

            }
        }
    }

    public void mouseClicked( MouseEvent e ) {
        DiffTextAreaModel model = diffLocalOverview.getModel();
        if ( model == null ) {
            return ;
        }
        if ( leftBorder.contains( e.getX(), e.getY() ) ) {
            // handle click on left side
            int line_number = ( e.getY() / pixelsPerLine ) + model.getLeftTextArea().getFirstPhysicalLine();
            moveRight( line_number );
        }
        else if ( rightBorder.contains( e.getX(), e.getY() ) ) {
            // handle click on right side
            int line_number = ( e.getY() / pixelsPerLine ) + model.getRightTextArea().getFirstPhysicalLine();
            moveLeft( line_number );
        }
    }

    // copies a diff starting at the given line number in the left text area and
    // replaces the corresponding diff in the right text area
    public void moveRight( int line_number ) {
        // TODO:  optimize to use the hunk maps from the model rather than looping
        DiffTextAreaModel model = diffLocalOverview.getModel();
        if ( model == null ) {
            return ;
        }
        JEditTextArea leftTextArea = model.getLeftTextArea();
        JEditTextArea rightTextArea = model.getRightTextArea();

        Diff.Change hunk = model.getEdits();
        for ( ; hunk != null; hunk = hunk.link ) {
            // find the hunk pertaining to this line number
            if ( ( hunk.line0 + Math.max( 0, hunk.deleted - 1 ) ) < line_number ) {
                continue;   // before this line, keep looking
            }

            if ( hunk.line0 > line_number ) {
                // after this line, didn't find a line with a corresponding hunk
                leftTextArea.getToolkit().beep();
                break;
            }

            // on a line with a right arrow --
            // get the text from the left text area to move to the right
            leftTextArea.selectNone();
            int start_sel = leftTextArea.getLineStartOffset( hunk.line0 );
            int end_sel = leftTextArea.getLineStartOffset( hunk.line0 + hunk.deleted );
            leftTextArea.setCaretPosition( start_sel );
            Selection.Range leftSelection;
            if ( hunk.deleted == 0 ) {
                leftSelection = new Selection.Range( start_sel, start_sel );
            }
            else {
                leftSelection = new Selection.Range( start_sel, end_sel );
            }
            String leftText = leftTextArea.getSelectedText(leftSelection);

            // replace text on right with text from left
            rightTextArea.selectNone();
            start_sel = rightTextArea.getLineStartOffset( hunk.line1 );
            end_sel = rightTextArea.getLineStartOffset( hunk.line1 + hunk.inserted );
            rightTextArea.setCaretPosition( start_sel );
            Selection.Range selection;
            if ( hunk.inserted == 0 ) {
                selection = new Selection.Range( start_sel, start_sel );
            }
            else {
                selection = new Selection.Range( start_sel, end_sel );
            }
            rightTextArea.setSelectedText( selection, leftText );
            rightTextArea.selectNone();
            DualDiff.refreshFor( rightTextArea.getView() );
            break;
        }
    }

    // copies a diff starting at the given line number in the right text area and
    // replaces the corresponding diff in the left text area
    public void moveLeft( int line_number ) {
        // TODO:  optimize to use the hunk maps from the model rather than looping
        DiffTextAreaModel model = diffLocalOverview.getModel();
        if ( model == null ) {
            return ;
        }
        JEditTextArea leftTextArea = model.getLeftTextArea();
        JEditTextArea rightTextArea = model.getRightTextArea();
        Diff.Change hunk = model.getEdits();
        for ( ; hunk != null; hunk = hunk.link ) {
            // find the hunk pertaining to this line number
            if ( ( hunk.line1 + Math.max( 0, hunk.inserted - 1 ) ) < line_number ) {
                continue;   // before this line, keep looking
            }

            if ( hunk.line1 > line_number ) {
                // after this line, didn't find a line with a corresponding hunk
                rightTextArea.getToolkit().beep();
                break;
            }

            // on a line with a left arrow --
            // get the text from the right text area to move to the left
            rightTextArea.selectNone();
            int start_sel = rightTextArea.getLineStartOffset( hunk.line1 );
            int end_sel = rightTextArea.getLineStartOffset( hunk.line1 + hunk.inserted );
            rightTextArea.setCaretPosition( start_sel );
            Selection.Range rightSelection;
            if ( hunk.inserted == 0 ) {
                rightSelection = new Selection.Range( start_sel, start_sel );
            }
            else {
                rightSelection = new Selection.Range( start_sel, end_sel );
            }
            String rightText = rightTextArea.getSelectedText(rightSelection);

            // replace text on left with text from right
            leftTextArea.selectNone();
            start_sel = leftTextArea.getLineStartOffset( hunk.line0 );
            end_sel = leftTextArea.getLineStartOffset( hunk.line0 + hunk.deleted );
            leftTextArea.setCaretPosition( start_sel );
            Selection.Range leftSelection;
            if ( hunk.deleted == 0 ) {
                leftSelection = new Selection.Range( start_sel, start_sel );
            }
            else {
                leftSelection = new Selection.Range( start_sel, end_sel );
            }
            leftTextArea.setSelectedText( leftSelection, rightText );
            leftTextArea.selectNone();
            DualDiff.refreshFor( leftTextArea.getView() );
            break;
        }
    }

    public void mouseEntered( MouseEvent e ) {}
    public void mouseExited( MouseEvent e ) {}
    public void mousePressed( MouseEvent e ) {}
    public void mouseReleased( MouseEvent e ) {}

    /**
     * Handle caret movement in the text areas, paint the hunk cursors as
     * appropriate.
     */
    public void caretUpdate( final CaretEvent e ) {
        if ( e.getSource().equals( diffLocalOverview.getModel().getLeftTextArea() ) ) {
            paintCurrentHunkCursor( null, inLeftHunk() );
        }
        else if ( e.getSource().equals( diffLocalOverview.getModel().getRightTextArea() ) ) {
            paintCurrentHunkCursor( null, inRightHunk() );
        }
    }

    // check if the caret line in the left text area is in a hunk.  If so, return
    // the hunk, otherwise, return null.
    private Diff.Change inLeftHunk() {
        DiffTextAreaModel model = diffLocalOverview.getModel();
        HashMap<Integer, Diff.Change> leftHunkMap = model.getLeftHunkMap();
        if ( leftHunkMap != null ) {
            int caret_line = model.getLeftTextArea().getCaretLine();
            if ( leftHunkMap.get( caret_line ) != null && caret_line >= model.getLeftTextArea().getFirstPhysicalLine() && caret_line <= model.getLeftTextArea().getLastPhysicalLine() ) {
                return leftHunkMap.get( caret_line );
            }
        }
        return null;
    }

    // check if the caret line in the right text area is in a hunk.  If so, return
    // the hunk, otherwise, return null.
    private Diff.Change inRightHunk() {
        DiffTextAreaModel model = diffLocalOverview.getModel();
        HashMap<Integer, Diff.Change> rightHunkMap = model.getRightHunkMap();
        if ( rightHunkMap != null ) {
            int caret_line = model.getRightTextArea().getCaretLine();
            if ( rightHunkMap.get( caret_line ) != null && caret_line >= model.getRightTextArea().getFirstPhysicalLine() && caret_line <= model.getRightTextArea().getLastPhysicalLine() ) {
                return rightHunkMap.get( caret_line );
            }
        }
        return null;
    }

    /**
     * Paint the cursors for the current hunk.  The "current" hunk is the hunk
     * in the active text area that contains the caret for that text area.
     * @param gfx graphics context, if null, will use graphics context from diffLocalOverview component
     * @param currentHunk the diff to draw cursors for.  If null, will clear all cursors.
     */
    private void paintCurrentHunkCursor( Graphics gfx, Diff.Change currentHunk ) {
        if ( gfx == null ) {
            gfx = diffLocalOverview.getGraphics();
        }
        if ( gfx == null ) {
            return ;        // no graphics context to draw on
        }

        // clear the current hunk cursors, if any
        if ( leftRectangle == null || rightRectangle == null ) {
            return;
        }
        DiffTextAreaModel model = diffLocalOverview.getModel();
        int leftFirstLine = model.getLeftTextArea().getFirstPhysicalLine();
        int leftLastLine = model.getLeftTextArea().getLastPhysicalLine();
        gfx.setColor( localRendererPane.getBackground() );
        gfx.drawRect( leftRectangle.x - 3, 0, 1, ( leftLastLine - leftFirstLine ) * pixelsPerLine );

        int rightFirstLine = model.getRightTextArea().getFirstPhysicalLine();
        int rightLastLine = model.getRightTextArea().getLastPhysicalLine();
        gfx.setColor( localRendererPane.getBackground() );
        gfx.drawRect( rightRectangle.x + rightRectangle.width + 1, 0, 1, ( rightLastLine - rightFirstLine ) * pixelsPerLine );

        if ( currentHunk == null ) {
            return ;    // nothing to draw
        }

        HashMap<Integer, Diff.Change> leftHunkMap = model.getLeftHunkMap();
        if ( leftHunkMap != null ) {
            // paint left cursor for current hunk
            for ( int i = leftFirstLine; i <= leftLastLine; i++ ) {
                Diff.Change hunk = leftHunkMap.get( i );
                if ( hunk != null && hunk.equals( currentHunk ) ) {
                    if ( hunk.deleted == 0 ) {
                        leftRectangle.height = 1;
                    }
                    else {
                        leftRectangle.height = Math.max( 1, pixelsPerLine * hunk.deleted );
                    }
                    leftRectangle.y = centerRectangle.y + ( ( i - leftFirstLine ) * pixelsPerLine );
                    gfx.setColor( Color.BLACK );
                    gfx.drawRect( leftRectangle.x - 3, leftRectangle.y, 1, leftRectangle.height );
                    break;
                }
            }
        }
        HashMap<Integer, Diff.Change> rightHunkMap = model.getRightHunkMap();
        if ( rightHunkMap != null ) {
            // paint right cursor for current hunk
            for ( int i = rightFirstLine; i <= rightLastLine; i++ ) {
                Diff.Change hunk = rightHunkMap.get( i );
                if ( hunk != null && hunk.equals( currentHunk ) ) {
                    if ( hunk.inserted == 0 ) {
                        rightRectangle.height = 1;
                    }
                    else {
                        rightRectangle.height = Math.max( 1, pixelsPerLine * hunk.inserted );
                    }
                    rightRectangle.y = centerRectangle.y + ( ( i - rightFirstLine ) * pixelsPerLine );
                    gfx.setColor( Color.BLACK );
                    gfx.drawRect( rightRectangle.x + rightRectangle.width + 1, rightRectangle.y, 1, rightRectangle.height );
                    break;
                }
            }
        }
    }
}
