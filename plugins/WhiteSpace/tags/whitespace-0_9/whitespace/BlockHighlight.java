/*
 * BlockHighlight.java
 * Copyright (c) 2000-2001 Andre Kaplan
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


package whitespace;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaHighlight;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.Log;


public class BlockHighlight
    implements TextAreaHighlight
{
    // (EditPane, BlockHighlight) association
    private static Hashtable highlights = new Hashtable();

    private static boolean indentBlock = jEdit.getBooleanProperty(
        "white-space.indent-block", true);

    private static Color blockColor = GUIUtilities.parseColor(
        jEdit.getProperty("white-space.block-color"));

    private JEditTextArea textArea;
    private TextAreaHighlight next;
    private Segment segment = new Segment();


    private BlockHighlight() {}


    public void init(JEditTextArea textArea, TextAreaHighlight next) {
        this.textArea = textArea;
        this.next = next;
    }


    public void paintHighlight(Graphics gfx, final int virtualLine, int y) {
        WhiteSpaceModel model = this.getModel();
        if ((model != null) && model.getBlockHighlight().isEnabled())
        {
            int physicalLine = this.textArea.getBuffer().virtualToPhysical(virtualLine);

            try {
                if (    (this.textArea.getLineStartOffset(physicalLine) == -1)
                    ||  (this.textArea.getLineEndOffset(physicalLine) == -1)
                ) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            if (   (physicalLine > 0)
                && (physicalLine < this.textArea.getLineCount() - 1)
                && ((this.textArea.getLineEndOffset(physicalLine) - this.textArea.getLineStartOffset(physicalLine)) == 1)
                && ((this.textArea.getLineEndOffset(physicalLine - 1) - this.textArea.getLineStartOffset(physicalLine - 1)) == 1)
                && ((this.textArea.getLineEndOffset(physicalLine + 1) - this.textArea.getLineStartOffset(physicalLine + 1)) > 1)
            ) {
                // Log.log(Log.DEBUG, this, "Block Highlight at line: " + line);
                int x0 = 0;
                if (indentBlock) {
                    char c;
                    this.textArea.getLineText(physicalLine + 1, segment);
                    for (int i = 0, off = segment.offset ; i < segment.count; i++, off++) {
                        c = segment.array[off];
                        if (!(c == ' ' || c == '\t')) {
                            x0 = this.textArea.offsetToX(physicalLine + 1, i);
                            break;
                        }
                    }
                }

                TextAreaPainter painter = this.textArea.getPainter();
                FontMetrics fm = painter.getFontMetrics();
                int y0 = y + fm.getDescent() + fm.getLeading();
                gfx.setColor(blockColor);
                gfx.drawLine(x0, y0, painter.getWidth() - 1, y0);
            }
        }

        if (this.next != null) {
            this.next.paintHighlight(gfx, virtualLine, y);
        }
    }


    public String getToolTipText(MouseEvent evt)
    {
        if (this.next == null) { return null; }

        return this.next.getToolTipText(evt);
    }


    public WhiteSpaceModel getModel() {
        return (WhiteSpaceModel) this.textArea.getBuffer().getProperty(
            WhiteSpaceModel.MODEL_PROPERTY
        );
    }


    private void updateTextArea() {
        if (this.textArea == null) { return; }

        Buffer buffer = this.textArea.getBuffer();
        int physicalFirst = buffer.virtualToPhysical(
            this.textArea.getFirstLine()
        );
        int physicalLast  = buffer.virtualToPhysical(
            this.textArea.getFirstLine() + this.textArea.getVisibleLines()
        );
        this.textArea.invalidateLineRange(physicalFirst, physicalLast);
    }


    /**
     * Updates the block highlighting for the <code>JEditTextArea</code>
     * which display the given <code>buffer</code>
     */
    public static void updateTextAreas(Buffer buffer) {
        View[] views = jEdit.getViews();
        for (int i = 0; i < views.length; i++) {
            EditPane[] editPanes = views[i].getEditPanes();
            BlockHighlight highlight;

            for (int j = 0; j < editPanes.length; j++) {
                if (editPanes[j].getBuffer() != buffer) { continue; }

                highlight = (BlockHighlight) highlights.get(editPanes[j]);
                if (highlight != null) {
                    highlight.updateTextArea();
                }
            }
        }
    }


    public static TextAreaHighlight addHighlightTo(EditPane editPane) {
        TextAreaHighlight textAreaHighlight = new BlockHighlight();
        highlights.put(editPane, textAreaHighlight);
        return textAreaHighlight;
    }


    public static void removeHighlightFrom(EditPane editPane) {
        highlights.remove(editPane);
    }


    public static void propertiesChanged() {
        boolean newIndentBlock = jEdit.getBooleanProperty(
            "white-space.indent-block", true
        );

        Color newBlockColor = GUIUtilities.parseColor(
            jEdit.getProperty("white-space.block-color")
        );

        boolean indentBlockChanged = (newIndentBlock != indentBlock);
        boolean blockColorChanged = !(newBlockColor.equals(blockColor));

        if (!indentBlockChanged && !blockColorChanged) {
            return;
        }

        indentBlock = newIndentBlock;
        blockColor = newBlockColor;

        // Propagate the changes to all textareas
        View[] views = jEdit.getViews();
        for (int i = 0; i < views.length; i++) {
            EditPane[] editPanes = views[i].getEditPanes();
            BlockHighlight highlight;
            WhiteSpaceModel model;
            for (int j = 0; j < editPanes.length; j++) {
                highlight = (BlockHighlight) highlights.get(editPanes[j]);
                if (highlight == null) { continue; }

                model = highlight.getModel();
                if (model == null) { continue; }

                if (model.getBlockHighlight().isEnabled()) {
                    highlight.updateTextArea();
                }
            }
        }
    }
}
