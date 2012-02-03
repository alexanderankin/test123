/*
 * FoldHighlight.java
 * Copyright (c) 2001 Andre Kaplan
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
import java.awt.Graphics2D;
import java.util.Hashtable;
import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.DisplayManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.StandardUtilities;


public class FoldHighlight extends TextAreaExtension
{
    // (EditPane, FoldHighlight) association
    private static final Hashtable highlights = new Hashtable();

    private static Color foldColor = GUIUtilities.parseColor(
        jEdit.getProperty("white-space.fold-color")
    );

    private JEditTextArea textArea;
    private EditPane editPane;

    private FoldHighlight(JEditTextArea textArea, EditPane editPane) {
        this.textArea = textArea;
        this.editPane = editPane;
    }


    public void paintValidLine(
            Graphics2D gfx, final int screenLine, final int physicalLine,
            final int start, final int end, final int y
    ) {
        WhiteSpaceModel model = this.getModel();

        if ((model != null) && model.getFoldHighlight().isEnabled()) {
            try {
                if (    (this.textArea.getLineStartOffset(physicalLine) == -1)
                    ||  (this.textArea.getLineEndOffset(physicalLine) == -1)
                ) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            Buffer buffer = this.editPane.getBuffer();
            int foldLevel = buffer.getFoldLevel(physicalLine);

            // Log.log(Log.DEBUG, this, "Fold Highlight at line: " + line);

            TextAreaPainter painter = this.textArea.getPainter();
            FontMetrics fm = painter.getFontMetrics();
            int spaceWidth = fm.charWidth(' ');
            Vector foldLevels = new Vector();
            int level = foldLevel;
            for (int i = physicalLine - 1; i >= 0; i--) {
                level = buffer.getFoldLevel(i);
                if (level == 0) {
                    break;
                }
                if (level < foldLevel) {
                    foldLevels.addElement(new Integer(level));
                    foldLevel = level;
                }
            }
            for (int i = 0; i < foldLevels.size(); i++) {
                level = ((Integer) foldLevels.elementAt(i)).intValue();
                int x0 = level * spaceWidth + 1 + this.textArea.getHorizontalOffset();
                gfx.setColor(foldColor);
                gfx.drawLine(x0, y, x0, y + fm.getHeight() - 1);
            }
        }
    }


    public String getToolTipText(int x, int y) {
        WhiteSpaceModel model = this.getModel();

        if (    (model != null)
             &&  model.getFoldHighlight().isEnabled()
             &&  model.getFoldTooltip().isEnabled()
        ) {
            JEditTextArea ta = this.textArea;
            Buffer buffer = this.editPane.getBuffer();

            int offset = ta.xyToOffset(x, y, false);
            if ((offset == -1) || (offset >= ta.getBuffer().getLength())) {
                return null;
            }

            int physicalLine = ta.getLineOfOffset(offset);

            int foldLevel = buffer.getFoldLevel(physicalLine);

            // tabs not expanded
            int virtualLineOffset  = offset - ta.getLineStartOffset(physicalLine);
            // tabs expanded
            int physicalLineOffset = 0;

            if (virtualLineOffset > 0) {
                // We expand tabs into physicalLineOffset
                physicalLineOffset = StandardUtilities.getLeadingWhiteSpaceWidth(
                    ta.getText(ta.getLineStartOffset(physicalLine), virtualLineOffset),
                    buffer.getTabSize()
                );
            } else {
                // If the line is empty, we do as if it was infinitely filled with spaces
                if (ta.getLineLength(physicalLine) == 0) {
                    TextAreaPainter painter = this.textArea.getPainter();
                    FontMetrics fm = painter.getFontMetrics();
                    int spaceWidth = fm.charWidth(' ');
                    physicalLineOffset = x / spaceWidth;
                }
            }

            StringBuffer tooltip = new StringBuffer();
            if (physicalLineOffset <= 0 || physicalLineOffset >= foldLevel) {
                // Nothing to do here
            } else {
                int level = 0;
                loop:
                for (int i = physicalLine - 1; i >= 0; i--) {
                    level = buffer.getFoldLevel(i);
                    if (level <= physicalLineOffset) {
                        // We found a tooltip candidate
                        // But no need to have a tooltip if the line is visible
                        int referenceLevel = level;
                        int firstVisibleLine = ta.getFirstPhysicalLine();
                        String text;
                        if (i < firstVisibleLine) {
                            // We now get some significant text at referenceLevel
                            for (; i >= 0; i--) {
                                level = buffer.getFoldLevel(i);
                                text = ta.getLineText(i).trim();
                                // line level < referenceLevel: we get out of
                                // the considered scope: exit
                                if (level < referenceLevel) {
                                    break loop;
                                } else if (level == referenceLevel) {
                                    if (hasSignificantChars(text)) {
                                        // We found some text with significant
                                        // chars. We can now exit
                                        if (tooltip.length() > 0) {
                                            tooltip.insert(0, ' ');
                                        }
                                        tooltip.insert(0, text);

                                        break loop;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }

            if (tooltip.length() > 0) {
                return (
                      tooltip.toString()
                    // + " -> "
                    // + "(" + x + ", " + y + ")"
                    // + " -> "
                    // + "(" + physicalLine + ", " + physicalLineOffset + ")"
                    // + " -> "
                    // + foldLevel
                );
            }
        }

        return null;
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
     * Updates the highlighting for the <code>JEditTextArea</code>
     * which display the given <code>buffer</code>
     */
    public static void updateTextAreas(Buffer buffer) {
        View[] views = jEdit.getViews();
        for (int i = 0; i < views.length; i++) {
            EditPane[] editPanes = views[i].getEditPanes();
            FoldHighlight highlight;

            for (int j = 0; j < editPanes.length; j++) {
                if (editPanes[j].getBuffer() != buffer) { continue; }

                highlight = (FoldHighlight) highlights.get(editPanes[j]);
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
        highlight = (FoldHighlight)painter.getClientProperty(FoldHighlight.class);
        if(highlight == null) {
             highlight = new FoldHighlight(textArea, editPane);
             highlights.put(editPane, highlight);
             painter.addExtension(TextAreaPainter.DEFAULT_LAYER, highlight);
             painter.putClientProperty(FoldHighlight.class, highlight);
        }
        return highlight;
    }


    public static void removeHighlightFrom(EditPane editPane) {
        FoldHighlight highlight = (FoldHighlight)editPane
            .getTextArea().getPainter().getClientProperty(FoldHighlight.class);
        if(highlight != null) {
            editPane.getTextArea().getPainter().removeExtension(highlight);
            editPane.getTextArea().getPainter().putClientProperty(FoldHighlight.class,null);
        }
        highlights.remove(editPane);
    }


    public static void propertiesChanged() {
        Color newFoldColor = GUIUtilities.parseColor(
            jEdit.getProperty("white-space.fold-color")
        );

        boolean foldColorChanged = !(newFoldColor.equals(foldColor));

        if (!foldColorChanged) {
            return;
        }

        foldColor = newFoldColor;

        // Propagate the changes to all textareas
        View[] views = jEdit.getViews();
        for (int i = 0; i < views.length; i++) {
            EditPane[] editPanes = views[i].getEditPanes();
            FoldHighlight highlight;
            WhiteSpaceModel model;
            for (int j = 0; j < editPanes.length; j++) {
                highlight = (FoldHighlight) highlights.get(editPanes[j]);
                if (highlight == null) { continue; }

                model = highlight.getModel();
                if (model == null) { continue; }

                if (model.getFoldHighlight().isEnabled()) {
                    highlight.updateTextArea();
                }
            }
        }
    }


    public static boolean hasSignificantChars(String text) {
        for (int i = text.length() - 1; i >= 0; i--) {
            if (Character.isLetterOrDigit(text.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}
