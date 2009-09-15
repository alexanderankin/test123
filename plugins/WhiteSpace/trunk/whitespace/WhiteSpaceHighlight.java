/*
 * WhiteSpaceHighlight.java
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
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
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


public class WhiteSpaceHighlight extends TextAreaExtension
{
    // ASCII control characters strictly below SPACE (0x20)
    private static final String[] ASCII_CONTROLS = new String[] {
        /* 000    001    002    003    004    005 */
          "NUL", "SOH", "STX", "ETX", "EOT", "ENQ"
        /* 006    007    008    009    00A    00B */
        , "ACK", "BEL", "BS",  "HT",  "LF",  "VT"
        /* 00C    00D    00E    00F    010    011 */
        , "FF",  "CR",  "SO",  "SI",  "DLE", "DC1"
        /* 012    013    014    015    016    017 */
        , "DC2", "DC3", "DC4", "NAK", "SYN", "ETB"
        /* 018    019    01A    01B    01C    01D */
        , "CAN", "EM",  "SUB", "ESC", "FS",  "GS"
        /* 01E    01F */
        , "RS",  "US"
        /* 07F */
        /* DEL */
    };

    // (EditPane, WhiteSpaceHighlight) association
    private static Hashtable highlights = new Hashtable();

    private static Color spaceColor      = GUIUtilities.parseColor(
        jEdit.getProperty("white-space.space-color"));
    private static Color tabColor        = GUIUtilities.parseColor(
        jEdit.getProperty("white-space.tab-color"));
    private static Color whitespaceColor = GUIUtilities.parseColor(
        jEdit.getProperty("white-space.whitespace-color"));

    private static boolean displayControlChars = jEdit.getBooleanProperty(
        "white-space.display-control-chars", false);

    private static Painter spacePainter      = new SpacePainter();
    private static Painter tabPainter        = new TabPainter();
    private static Painter whitespacePainter = new WhiteSpacePainter();

    private JEditTextArea textArea;

    private Segment lineSegment = new Segment();


    private WhiteSpaceHighlight(JEditTextArea textArea) {
        this.textArea = textArea;
    }


    public void paintValidLine(
            Graphics2D gfx, final int screenLine, final int physicalLine,
            final int start, final int end, final int y
    ) {
        WhiteSpaceModel model = this.getModel();

        if (    (model != null)
             && (    model.getSpaceHighlight().isEnabled()
                  || model.getTabHighlight().isEnabled()
                  || model.getWhitespaceHighlight().isEnabled()
             )
        ) {
            // Avoid most <code>getfield</code>s
            final JEditTextArea ta = this.textArea;
            final Segment s = this.lineSegment;

            try {
                if (    (ta.getLineStartOffset(physicalLine) == -1)
                    ||  (ta.getLineEndOffset(physicalLine) == -1)
                ) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            FontMetrics fm = ta.getPainter().getFontMetrics();

            ta.getLineText(physicalLine, s);

            final int height = fm.getHeight();

            Point p0 = new Point();

            final char[] array = s.array;
            final int count = s.count;

            Painter painter;

            Painter[] spacePainters = {null, null, null};
            Painter[] tabPainters   = {null, null, null};

            if (model.getSpaceHighlight().isEnabled()) {
                if (model.getLeadingSpaceHighlight().isEnabled()) {
                    spacePainters[0] = spacePainter;
                }
                if (model.getInnerSpaceHighlight().isEnabled()) {
                    spacePainters[1] = spacePainter;
                }
                if (model.getTrailingSpaceHighlight().isEnabled()) {
                    spacePainters[2] = spacePainter;
                }
            }

            if (model.getTabHighlight().isEnabled()) {
                if (model.getLeadingTabHighlight().isEnabled()) {
                    tabPainters[0] = tabPainter;
                }
                if (model.getInnerTabHighlight().isEnabled()) {
                    tabPainters[1] = tabPainter;
                }
                if (model.getTrailingTabHighlight().isEnabled()) {
                    tabPainters[2] = tabPainter;
                }
            }

            // Trailing whitespaces; have higher priority than leading whitespaces
            int i_t   = count - 1;
            int idx_t = s.offset + i_t;

            for (painter = null; i_t >= 0; i_t--, idx_t--) {
                char c = array[idx_t];

                if (c == ' ') {
                    painter = spacePainters[2];
                } else if (c == '\t') {
                    painter = tabPainters[2];
                } else {
                    break;
                }

                if (painter != null) {
                    ta.offsetToXY(physicalLine, i_t, p0);
                    painter.paint(gfx, p0.x + 1, p0.y + (height / 2));
                    painter = null;
                }
            }

            // Leading whitespaces
            int i_l   = 0;
            int idx_l = s.offset;

            for (painter = null; i_l < count; i_l++, idx_l++) {
                char c = array[idx_l];

                if (c == ' ') {
                    painter = spacePainters[0];
                } else if (c == '\t') {
                    painter = tabPainters[0];
                } else {
                    break;
                }

                if (painter != null) {
                    ta.offsetToXY(physicalLine, i_l, p0);
                    painter.paint(gfx, p0.x + 1, p0.y + (height / 2));
                    painter = null;
                }
            }

            // Inner whitespaces
            int i_i   = i_l;
            int idx_i = idx_l;
            for (painter = null; i_i <= i_t && i_i > 0; i_i++, idx_i++) {
                char c = array[idx_i];

                if (c == ' ') {
                    painter = spacePainters[1];
                } else if (c == '\t') {
                    painter = tabPainters[1];
                } else {
                    if (    (    Character.isWhitespace(c)
                              || (displayControlChars && Character.isISOControl(c))
                            )
                         && (c != '\t')
                         //  && (c != '\n') // Not needed here
                         && (c != ' ')
                    ) {
                        if (model.getWhitespaceHighlight().isEnabled()) {
                            painter = whitespacePainter;
                        }
                    }
                }

                if (painter != null) {
                    ta.offsetToXY(physicalLine, i_i, p0);
                    painter.paint(gfx, p0.x + 1, p0.y + (height / 2));
                    painter = null;
                }
            }
        }
    }


    public String getToolTipText(int x, int y) {
        WhiteSpaceModel model = this.getModel();

        if ((model == null) || !model.getWhitespaceHighlight().isEnabled()) {
            return null;
        }

        JEditTextArea ta = this.textArea;

        int offset = ta.xyToOffset(x, y, false);
        if ((offset == -1) || (offset >= ta.getBuffer().getLength())) {
            return null;
        }

        String s = ta.getText(offset, 1);
        if (s == null) { return null; }

        char c = s.charAt(0);
        if (    (    Character.isWhitespace(c)
                  || (displayControlChars && Character.isISOControl(c))
                )
             && (c != '\t')
             && (c != '\n')
             && (c != ' ')
        ) {
            int i = (int) c;
            String tooltip = i + " - " + "0x" + Integer.toHexString(i);
            if ((i >= 0) && (i < ASCII_CONTROLS.length)) {
                tooltip += " - " + ASCII_CONTROLS[i];
                if ((i >= 7) && (i <= 13)) {
                    String c_escape = "abtnvfr";
                    tooltip += " (\\" + c_escape.charAt(i - 7) + ")";
                }
            }
            return tooltip;
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
            WhiteSpaceHighlight highlight;

            for (int j = 0; j < editPanes.length; j++) {
                if (editPanes[j].getBuffer() != buffer) { continue; }

                highlight = (WhiteSpaceHighlight) highlights.get(editPanes[j]);
                if (highlight != null) {
                    highlight.updateTextArea();
                }
            }
        }
    }


    public static TextAreaExtension getHighlightFor(EditPane editPane) {
        return (TextAreaExtension) highlights.get(editPane);
    }


    public static TextAreaExtension addHighlightTo(EditPane editPane) {
        JEditTextArea textArea = editPane.getTextArea();
        TextAreaPainter painter = textArea.getPainter();
        TextAreaExtension highlight = null;
        highlight = (WhiteSpaceHighlight)painter.getClientProperty(WhiteSpaceHighlight.class);
        if(highlight == null) {
             highlight = new WhiteSpaceHighlight(textArea);
             highlights.put(editPane, highlight);
             painter.addExtension(TextAreaPainter.DEFAULT_LAYER, highlight);
             painter.putClientProperty(WhiteSpaceHighlight.class, highlight);
        }
        return highlight;
    }


    public static void removeHighlightFrom(EditPane editPane) {
        WhiteSpaceHighlight highlight = (WhiteSpaceHighlight)editPane
            .getTextArea().getPainter().getClientProperty(WhiteSpaceHighlight.class);
        if(highlight != null) {
            editPane.getTextArea().getPainter().removeExtension(highlight);
            editPane.getTextArea().getPainter().putClientProperty(WhiteSpaceHighlight.class,null);
        }
        highlights.remove(editPane);
    }


    public static void propertiesChanged() {
        Color newSpaceColor = GUIUtilities.parseColor(
            jEdit.getProperty("white-space.space-color"));
        Color newTabColor = GUIUtilities.parseColor(
            jEdit.getProperty("white-space.tab-color"));
        Color newWhitespaceColor = GUIUtilities.parseColor(
            jEdit.getProperty("white-space.whitespace-color"));
        boolean newDisplayControlChars = jEdit.getBooleanProperty(
            "white-space.display-control-chars", false
        );

        boolean spaceColorChanged          = !(newSpaceColor.equals(spaceColor));
        boolean tabColorChanged            = !(newTabColor.equals(tabColor));
        boolean whitespaceColorChanged     = !(newWhitespaceColor.equals(whitespaceColor));
        boolean displayControlCharsChanged = (newDisplayControlChars != displayControlChars);

        if (    !spaceColorChanged
             && !tabColorChanged
             && !whitespaceColorChanged
             && !displayControlCharsChanged
        ) {
            return;
        }

        spaceColor          = newSpaceColor;
        tabColor            = newTabColor;
        whitespaceColor     = newWhitespaceColor;
        displayControlChars = newDisplayControlChars;

        // Propagate the changes to all textareas
        View[] views = jEdit.getViews();
        for (int i = 0; i < views.length; i++) {
            EditPane[] editPanes = views[i].getEditPanes();
            WhiteSpaceHighlight highlight;
            WhiteSpaceModel model;
            for (int j = 0; j < editPanes.length; j++) {
                highlight = (WhiteSpaceHighlight) highlights.get(editPanes[j]);
                if (highlight == null) { continue; }

                model = highlight.getModel();
                if (model == null) { continue; }

                if (   (spaceColorChanged      && model.getSpaceHighlight().isEnabled())
                    || (tabColorChanged        && model.getTabHighlight().isEnabled())
                    || (whitespaceColorChanged && model.getWhitespaceHighlight().isEnabled())
                ) {
                    highlight.updateTextArea();
                }
            }
        }
    }


    private interface Painter {
        void paint(Graphics2D gfx, int x0, int y0);
    }


    private static class SpacePainter implements Painter {
        public void paint(Graphics2D gfx, int x0, int y0) {
            gfx.setColor(WhiteSpaceHighlight.spaceColor);
            gfx.drawRect(x0 + 1, y0 - 1, 2, 2);
        }
    }


    private static class TabPainter implements Painter {
        public void paint(Graphics2D gfx, int x0, int y0) {
            gfx.setColor(WhiteSpaceHighlight.tabColor);
            int[] xl0 = {x0, x0 + 2, x0};
            int[] xl1 = {x0 + 2 , x0 + 4, x0 + 2};
            int[] yl0 = {y0 - 2, y0, y0 + 2};
            gfx.drawPolyline(xl0, yl0, 3);
            gfx.drawPolyline(xl1, yl0, 3);
        }
    }


    private static class WhiteSpacePainter implements Painter {
        public void paint(Graphics2D gfx, int x0, int y0) {
            gfx.setColor(WhiteSpaceHighlight.whitespaceColor);
            gfx.setColor(whitespaceColor);
            int[] xl0 = {x0, x0 + 2, x0 + 4, x0 + 2};
            int[] yl0 = {y0, y0 - 2, y0, y0 + 2};
            gfx.drawPolygon(xl0, yl0, 4);
        }
    }
}

