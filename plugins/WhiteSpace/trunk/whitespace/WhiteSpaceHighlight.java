/*
 * WhiteSpaceHighlight.java
 * Copyright (c) 2000 Andre Kaplan
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


import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.JPopupMenu;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.BadLocationException;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaHighlight;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.Log;


public class WhiteSpaceHighlight
    implements TextAreaHighlight
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
    private TextAreaHighlight next;

    private Segment lineSegment = new Segment();


    private WhiteSpaceHighlight() {}


    public void init(JEditTextArea textArea, TextAreaHighlight next) {
        this.textArea = textArea;
        this.next = next;
    }


    public void paintHighlight(Graphics gfx, int virtualLine, int y) {
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

            int physicalLine = ta.getBuffer().virtualToPhysical(virtualLine);

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

            int height = fm.getHeight();
            int descent = fm.getDescent();
            final int y0 = y + descent + (height / 2);
            int x0;

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

            // Leading whitespaces
            int i0   = 0;
            int idx0 = s.offset;

            leading:
            for (painter = null; i0 < count; i0++, idx0++) {
                char c = array[idx0];

                if (c == ' ') {
                    painter = spacePainters[0];
                } else if (c == '\t') {
                    painter = tabPainters[0];
                } else {
                    break leading;
                }

                if (painter != null) {
                    x0 = ta.offsetToX(physicalLine, i0) + 1;
                    painter.paint(gfx, x0, y0);
                    painter = null;
                }
            }

            // Trailing whitespaces
            int i1   = count - 1;
            int idx1 = s.offset + count - 1;

            trailing:
            for (painter = null; i1 > i0; i1--, idx1--) {
                char c = array[idx1];

                if (c == ' ') {
                    painter = spacePainters[2];
                } else if (c == '\t') {
                    painter = tabPainters[2];
                } else {
                    break trailing;
                }

                if (painter != null) {
                    x0 = ta.offsetToX(physicalLine, i1) + 1;
                    painter.paint(gfx, x0, y0);
                    painter = null;
                }
            }

            // Inner whitespaces
            int i   = i0;
            int idx = idx0;
            for (; i < i1; i++, idx++) {
                char c = array[idx];

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
                    x0 = ta.offsetToX(physicalLine, i) + 1;
                    painter.paint(gfx, x0, y0);
                    painter = null;
                }
            }
        }

        if (this.next != null) {
            this.next.paintHighlight(gfx, virtualLine, y);
        }
    }


    public String getToolTipText(MouseEvent evt)
    {
        WhiteSpaceModel model = this.getModel();

        if ((model != null) && model.getWhitespaceHighlight().isEnabled()) {
            JEditTextArea ta = this.textArea;
            int x = evt.getX();
            int y = evt.getY();

            int offset = ta.xyToOffset(x, y);

            String s = ta.getText(offset, 1);
            if (s != null) {
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
            }
        }

        if (this.next == null) { return null; }

        return this.next.getToolTipText(evt);
    }


    public WhiteSpaceModel getModel() {
        return (WhiteSpaceModel) this.textArea.getBuffer().getProperty(
            WhiteSpaceModel.MODEL_PROPERTY
        );
    }


    public void updateTextArea() {
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


    public static TextAreaHighlight getHighlightFor(EditPane editPane) {
        return (TextAreaHighlight) highlights.get(editPane);
    }


    public static TextAreaHighlight addHighlightTo(EditPane editPane) {
        TextAreaHighlight textAreaHighlight = new WhiteSpaceHighlight();
        highlights.put(editPane, textAreaHighlight);
        return textAreaHighlight;
    }


    public static void removeHighlightFrom(EditPane editPane) {
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
                model = highlight.getModel();

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
        void paint(Graphics gfx, int x0, int y0);
    }


    private static class SpacePainter implements Painter {
        public void paint(Graphics gfx, int x0, int y0) {
            gfx.setColor(WhiteSpaceHighlight.spaceColor);
            gfx.drawRect(x0 + 1, y0 - 1, 2, 2);
        }
    }


    private static class TabPainter implements Painter {
        public void paint(Graphics gfx, int x0, int y0) {
            gfx.setColor(WhiteSpaceHighlight.tabColor);
            int[] xl0 = {x0, x0 + 2, x0};
            int[] xl1 = {x0 + 2 , x0 + 4, x0 + 2};
            int[] yl0 = {y0 - 2, y0, y0 + 2};
            gfx.drawPolyline(xl0, yl0, 3);
            gfx.drawPolyline(xl1, yl0, 3);
        }
    }


    private static class WhiteSpacePainter implements Painter {
        public void paint(Graphics gfx, int x0, int y0) {
            gfx.setColor(WhiteSpaceHighlight.whitespaceColor);
            gfx.setColor(whitespaceColor);
            int[] xl0 = {x0, x0 + 2, x0 + 4, x0 + 2};
            int[] yl0 = {y0, y0 - 2, y0, y0 + 2};
            gfx.drawPolygon(xl0, yl0, 4);
        }
    }
}
