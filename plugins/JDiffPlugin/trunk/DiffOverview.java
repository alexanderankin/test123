/*
 * DiffOverview.java - JComponent subclass providing a graphical overview of a
 *                     Diff and synchronized scrolling
 * Copyright (C) 2000 Andre Kaplan, original code by mike dillon
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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.UIManager;

import jdiff.util.Diff;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public class DiffOverview extends JComponent
{
    private static Color currentLineColor  = Color.blue;

    private static Color changedLineColor;
    private static Color deletedLineColor;
    private static Color insertedLineColor;

    private static Color invalidLineColor = UIManager.getColor("Panel.background");

    static {
        propertiesChanged();
    }

    private Diff.change edits;
    private int lineCount0;
    private int lineCount1;
    private JEditTextArea textArea;
    private JEditTextArea textArea1;

    public DiffOverview(Diff.change edits,
        int lineCount0,
        int lineCount1,
        JEditTextArea textArea0,
        JEditTextArea textArea1)
    {
        this.edits     = edits;
        this.lineCount0 = lineCount0;
        this.lineCount1 = lineCount1;
        this.textArea  = textArea0;
        this.textArea1 = textArea1;

        Dimension dim = getPreferredSize();
        dim.width = 40;
        setPreferredSize(dim);
    }


    public void paint(Graphics gfx)
    {
        Rectangle size = getBounds();

        gfx.setColor(getBackground());
        gfx.fillRect(0, 0, size.width, size.height);

        Rectangle inner = new Rectangle(4, 4, size.width - 8, size.height - 8);

        int lines = Math.max(this.lineCount0, this.lineCount1);
        double pxlPerLine = ((double) inner.height) / lines;

        Rectangle left = new Rectangle(
            inner.x,
            inner.y,
            inner.width / 3,
            Math.max(1, (int) Math.round(pxlPerLine * this.lineCount0))
        );
        Rectangle right = new Rectangle(
            inner.x + (inner.width - left.width),
            inner.y,
            left.width,
            Math.max(1, (int) Math.round(pxlPerLine * this.lineCount1))
        );

        Rectangle cursor = new Rectangle(inner.x + inner.width / 2 - 1, inner.y,
            2, 0);

        Color leftColor  = DiffOverview.invalidLineColor;
        Color rightColor = DiffOverview.invalidLineColor;

        gfx.setColor(Color.black);
        gfx.drawRect(left.x - 1, left.y - 1, left.width + 1, left.height + 1);
        gfx.drawRect(right.x - 1, right.y - 1, right.width + 1, right.height + 1);

        gfx.setColor(Color.white);
        gfx.fillRect(left.x, left.y, left.width, left.height);
        gfx.fillRect(right.x, right.y, right.width, right.height);

        Diff.change hunk = this.edits;

        int leftOffset  = 0;
        int rightOffset = 0;
        for (; hunk != null; hunk = hunk.link) {
            leftOffset  = hunk.line0;
            rightOffset = hunk.line1;

            if (hunk.inserted == 0 && hunk.deleted != 0) { // DELETE
               leftColor  = DiffOverview.deletedLineColor;
               rightColor = DiffOverview.invalidLineColor;
            } else if (hunk.inserted != 0 && hunk.deleted == 0) { // INSERT
               leftColor  = DiffOverview.invalidLineColor;
               rightColor = DiffOverview.insertedLineColor;
            } else { // CHANGE
               leftColor  = DiffOverview.changedLineColor;
               rightColor = DiffOverview.changedLineColor;
            }

            left.y  = inner.y + (int) Math.round(leftOffset * pxlPerLine);
            right.y = inner.y + (int) Math.round(rightOffset * pxlPerLine);
            left.height  = Math.max(1, (int) Math.round(hunk.deleted * pxlPerLine));
            right.height = Math.max(1, (int) Math.round(hunk.inserted * pxlPerLine));
            gfx.setColor(leftColor);
            gfx.fillRect(left.x, left.y, left.width, left.height);
            gfx.setColor(rightColor);
            gfx.fillRect(right.x, right.y, right.width, right.height);

            gfx.setColor(Color.black);
            gfx.drawLine(left.x + left.width + 1, left.y, right.x - 1, right.y);
        }

        // Display the textArea cursor
        this.paintCursor(gfx);
    }


    public void paintCursor(Graphics gfx) {
        Rectangle size = getBounds();

        Rectangle inner = new Rectangle(4, 4, size.width - 8, size.height - 8);

        int lines = Math.max(this.lineCount0, this.lineCount1);
        double pxlPerLine = ((double) inner.height) / lines;

        Rectangle leftCursor = new Rectangle(
            inner.x, inner.y + ((int) Math.round(pxlPerLine * this.textArea.getFirstLine())),
            inner.width / 3,
            Math.max(1, (int) Math.round(pxlPerLine * this.textArea.getVisibleLines()))
        );

        Rectangle rightCursor = new Rectangle(
            inner.x + (inner.width - leftCursor.width),
            inner.y + ((int) Math.round(pxlPerLine * this.textArea1.getFirstLine())),
            leftCursor.width,
            Math.max(1, (int) Math.round(pxlPerLine * this.textArea1.getVisibleLines()))
        );

        gfx.setColor(DiffOverview.currentLineColor);
        gfx.drawRect(leftCursor.x, leftCursor.y, leftCursor.width - 1, leftCursor.height - 1);
        gfx.drawRect(rightCursor.x, rightCursor.y, rightCursor.width - 1, rightCursor.height - 1);
    }


    public void synchroScrollVertical() {
        Diff.change hunk = this.edits;

        int leftFirstLine  = this.textArea.getFirstLine();
        int rightFirstLine = -1;

        int prevLeftOffset  = 0;
        int prevRightOffset = 0;
        int leftOffset  = 0;
        int rightOffset = 0;
        for (; hunk != null; hunk = hunk.link) {
            leftOffset  = hunk.line0;
            rightOffset = hunk.line1;

            if (   (leftFirstLine >= prevLeftOffset)
                && (leftFirstLine <  leftOffset)
            ) {
                rightFirstLine = prevRightOffset + (leftFirstLine - prevLeftOffset);
                break;
            }

            if (   (leftFirstLine >= leftOffset)
                && (leftFirstLine <  (leftOffset + hunk.deleted))
            ) {
                rightFirstLine = rightOffset;
                break;
            }

            prevLeftOffset  = leftOffset  + hunk.deleted;
            prevRightOffset = rightOffset + hunk.inserted;
        }

        if (rightFirstLine >= 0) {
            this.textArea1.setFirstLine(rightFirstLine);
        }
    }


    public static void propertiesChanged() {
        changedLineColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.changed-color", "#B2B200")
        );
        deletedLineColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.deleted-color", "#B20000")
        );
        insertedLineColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.inserted-color", "#00B200")
        );
    }
}
