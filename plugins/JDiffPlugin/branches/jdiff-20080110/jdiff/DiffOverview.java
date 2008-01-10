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


package jdiff;

import java.awt.Dimension;

import javax.swing.JComponent;

import jdiff.util.Diff;

import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public abstract class DiffOverview extends JComponent
{
    protected Diff.change edits;
    protected int lineCount0;
    protected int lineCount1;
    protected JEditTextArea textArea0;
    protected JEditTextArea textArea1;


    public DiffOverview(
        Diff.change edits,
        int lineCount0,
        int lineCount1,
        JEditTextArea textArea0,
        JEditTextArea textArea1)
    {
        this.edits      = edits;
        this.lineCount0 = lineCount0;
        this.lineCount1 = lineCount1;
        this.textArea0  = textArea0;
        this.textArea1  = textArea1;

        Dimension dim = getPreferredSize();
        dim.width = 40;
        setPreferredSize(dim);
    }


    public void synchroScrollRight() {
        Diff.change hunk = this.edits;

        int leftFirstLine  = this.textArea0.getFirstLine();
        int rightFirstLine = -1;

        if (hunk == null) {
            this.textArea1.setFirstLine(leftFirstLine);
            return;
        }

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

            if (hunk.link == null) {
                rightFirstLine = prevRightOffset + (leftFirstLine - prevLeftOffset);
                break;
            }
        }

        if (rightFirstLine >= 0) {
            this.textArea1.setFirstLine(rightFirstLine);
        }
    }


    public void synchroScrollLeft() {
        Diff.change hunk = this.edits;

        int leftFirstLine  = -1;
        int rightFirstLine = this.textArea1.getFirstLine();

        if (hunk == null) {
            this.textArea0.setFirstLine(rightFirstLine);
            return;
        }

        int prevLeftOffset  = 0;
        int prevRightOffset = 0;
        int leftOffset  = 0;
        int rightOffset = 0;
        for (; hunk != null; hunk = hunk.link) {
            leftOffset  = hunk.line0;
            rightOffset = hunk.line1;

            if (   (rightFirstLine >= prevRightOffset)
                && (rightFirstLine <  rightOffset)
            ) {
                leftFirstLine = prevLeftOffset + (rightFirstLine - prevRightOffset);
                break;
            }

            if (   (rightFirstLine >= rightOffset)
                && (rightFirstLine <  (rightOffset + hunk.inserted))
            ) {
                leftFirstLine = leftOffset;
                break;
            }

            prevLeftOffset  = leftOffset  + hunk.deleted;
            prevRightOffset = rightOffset + hunk.inserted;

            if (hunk.link == null) {
                leftFirstLine = prevLeftOffset + (rightFirstLine - prevRightOffset);
                break;
            }
        }

        if (leftFirstLine >= 0) {
            this.textArea0.setFirstLine(leftFirstLine);
        }
    }
}
