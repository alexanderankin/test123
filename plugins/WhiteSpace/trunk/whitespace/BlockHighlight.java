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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.DisplayManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.Log;


public class BlockHighlight extends TextAreaExtension
{
    // (EditPane, BlockHighlight) association
    private static Hashtable highlights = new Hashtable();

    private static boolean indentBlock = jEdit.getBooleanProperty(
        "white-space.indent-block", true);

    private static Color blockColor = GUIUtilities.parseColor(
        jEdit.getProperty("white-space.block-color"));

    private JEditTextArea textArea;
    private Segment segment = new Segment();


    private BlockHighlight(JEditTextArea textArea) {
        this.textArea = textArea;
    }


    public void paintValidLine(
            Graphics2D gfx, final int screenLine, final int physicalLine,
            final int start, final int end, final int y
    ) {
        WhiteSpaceModel model = this.getModel();
        if ((model != null) && model.getBlockHighlight().isEnabled())
        {
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
                Point p0 = new Point();
                if (indentBlock) {
                    char c;
                    this.textArea.getLineText(physicalLine + 1, segment);
                    for (int i = 0, off = segment.offset ; i < segment.count; i++, off++) {
                        c = segment.array[off];
                        if (!(c == ' ' || c == '\t')) {
                            this.textArea.offsetToXY(physicalLine + 1, i, p0);
                            break;
                        }
                    }
                }

                TextAreaPainter painter = this.textArea.getPainter();
                gfx.setColor(blockColor);
                gfx.drawLine(p0.x, y, painter.getWidth() - 1, y);
            }
        }
    }


    public WhiteSpaceModel getModel() {
        return (WhiteSpaceModel) this.textArea.getBuffer().getProperty(
            WhiteSpaceModel.MODEL_PROPERTY
        );
    }


    private void updateTextArea() {
        if (this.textArea == null) { return; }
        DisplayManager displayManager = this.textArea.getDisplayManager();
        int physicalFirst = displayManager.getFirstVisibleLine();
        int physicalLast  = displayManager.getLastVisibleLine();
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


    public static TextAreaExtension addHighlightTo(EditPane editPane) {
        JEditTextArea textArea = editPane.getTextArea();
        TextAreaPainter painter = textArea.getPainter();
        TextAreaExtension highlight = null;
        highlight = (BlockHighlight)painter.getClientProperty(BlockHighlight.class);
        if(highlight == null) {
             highlight = new BlockHighlight(textArea);
             highlights.put(editPane, highlight);
             painter.addExtension(TextAreaPainter.DEFAULT_LAYER, highlight);
             painter.putClientProperty(BlockHighlight.class, highlight);
        }
        return highlight;
    }


    public static void removeHighlightFrom(EditPane editPane) {
        BlockHighlight highlight = (BlockHighlight)editPane
            .getTextArea().getPainter().getClientProperty(BlockHighlight.class);
        if(highlight != null) {
            editPane.getTextArea().getPainter().removeExtension(highlight);
            editPane.getTextArea().getPainter().putClientProperty(BlockHighlight.class,null);
        }
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
