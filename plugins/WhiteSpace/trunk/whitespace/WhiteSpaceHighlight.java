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

    private JEditTextArea textArea;
    private TextAreaHighlight next;

    private boolean spaceHighlightEnabled      = false;
    private boolean tabHighlightEnabled        = false;
    private boolean whitespaceHighlightEnabled = false;

    private Segment lineSegment = new Segment();


    private WhiteSpaceHighlight() {}


    public void init(JEditTextArea textArea, TextAreaHighlight next) {
        this.textArea = textArea;
        this.next = next;
    }


    public void paintHighlight(Graphics gfx, int virtualLine, int y) {
        if (    this.isSpaceHighlightEnabled()
            ||  this.isTabHighlightEnabled()
            ||  this.isWhitespaceHighlightEnabled()
        ) {
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

            TextAreaPainter painter = this.textArea.getPainter();
            FontMetrics fm = painter.getFontMetrics();

            this.textArea.getLineText(physicalLine, this.lineSegment);

            int height = fm.getHeight();
            int descent = fm.getDescent();
            int y0 = y + descent + (height / 2);
            int x0;
            int x1;

            boolean space = false;
            int i = 0;
            int idx = this.lineSegment.offset;
            int off0 = 0;
            int off1 = 0;
            char[] array = this.lineSegment.array;
            int count = this.lineSegment.count;
            for (; i < count; i++, idx++) {
                // Showing spaces
                if (this.isSpaceHighlightEnabled()) {
                    if (array[idx] == ' ') {
                        if (!space) {
                            space = true;
                            off0 = i;
                        }
                        if (i < count - 1) {
                            continue;
                        }
                        // Falls through if the last char is a ' ': I don't want
                        // to repeat the code outside of the loop
                    }

                    // We have collected a bunch of contiguous spaces,
                    // now display them
                    if (space) {
                        space = false;
                        // off1 is the last position at which a space is found
                        // off1 -off + 1 spaces to paint
                        off1 = (array[idx] == ' ') ? i : (i - 1);
                        x0 = this.textArea.offsetToX(physicalLine, off0);
                        gfx.setColor(spaceColor);
                        if (off1 == off0) { // One space to paint
                            gfx.drawRect(x0 + 1, y0 - 1, 2, 2);
                        } else { // Several spaces to paint
                            x1 = this.textArea.offsetToX(physicalLine, off1);
                            int step = (x1 - x0) / (off1 - off0);
                            int x = x0 + 1;
                            int k = (off1 - off0 + 1);
                            for (; k > 0; k--, x += step) {
                                gfx.drawRect(x, y0 - 1, 2, 2);
                            }
                        }
                    }
                }

                // Showing tabs
                if (this.isTabHighlightEnabled()) {
                    if (array[idx] == '\t') {
                        x0 = this.textArea.offsetToX(physicalLine, i) + 1;
                        gfx.setColor(tabColor);
                        int[] xl0 = {x0, x0 + 2, x0};
                        int[] xl1 = {x0 + 2 , x0 + 4, x0 + 2};
                        int[] yl0 = {y0 - 2, y0, y0 + 2};
                        gfx.drawPolyline(xl0, yl0, 3);
                        gfx.drawPolyline(xl1, yl0, 3);
                    }
                }

                // Showing all other whitespace
                if (this.isWhitespaceHighlightEnabled()) {
                    char c = array[idx];
                    if (    (    Character.isWhitespace(c)
                              || (displayControlChars && Character.isISOControl(c))
                            )
                         && (c != '\t')
                         //  && (c != '\n') // Not needed here
                         && (c != ' ')
                    ) {
                        x0 = this.textArea.offsetToX(physicalLine, i) + 1;
                        gfx.setColor(whitespaceColor);
                        int[] xl0 = {x0, x0 + 2, x0 + 4, x0 + 2};
                        int[] yl0 = {y0, y0 - 2, y0, y0 + 2};
                        gfx.drawPolygon(xl0, yl0, 4);
                    }
                }
            }
        }

        if (this.next != null) {
            this.next.paintHighlight(gfx, virtualLine, y);
        }
    }


    public String getToolTipText(MouseEvent evt)
    {
        if (this.isWhitespaceHighlightEnabled()) {
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


    public boolean isSpaceHighlightEnabled() {
        return this.spaceHighlightEnabled;
    }


    public void setSpaceHighlightEnabled(boolean enabled) {
        this.spaceHighlightEnabled = enabled;
    }


    public void toggleSpaceHighlightEnabled() {
        this.spaceHighlightEnabled = !this.spaceHighlightEnabled;
    }


    public boolean isTabHighlightEnabled() {
        return this.tabHighlightEnabled;
    }


    public void setTabHighlightEnabled(boolean enabled) {
        this.tabHighlightEnabled = enabled;
    }


    public void toggleTabHighlightEnabled() {
        this.tabHighlightEnabled = !this.tabHighlightEnabled;
    }


    public boolean isWhitespaceHighlightEnabled() {
        return this.whitespaceHighlightEnabled;
    }


    public void setWhitespaceHighlightEnabled(boolean enabled) {
        this.whitespaceHighlightEnabled = enabled;
    }


    public void toggleWhitespaceHighlightEnabled() {
        this.whitespaceHighlightEnabled = !this.whitespaceHighlightEnabled;
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
     * Tests if space highlighting is enabled for a view
    **/
    public static boolean isSpaceHighlightEnabledFor(View view) {
        EditPane[] editPanes = view.getEditPanes();
        WhiteSpaceHighlight highlight;
        for (int i = 0; i < editPanes.length; i++) {
            if (editPanes[i] == null) { continue; }
            highlight = (WhiteSpaceHighlight) highlights.get(editPanes[i]);
            if (highlight != null && highlight.isSpaceHighlightEnabled()) {
                return true;
            }
        }

        return false;
    }


    /**
     * Sets space highlighting enabled or disabled for a view
    **/
    public static void setSpaceHighlightEnabledFor(View view, boolean enabled) {
        EditPane[] editPanes = view.getEditPanes();
        WhiteSpaceHighlight highlight;
        for (int i = 0; i < editPanes.length; i++) {
            highlight = (WhiteSpaceHighlight) highlights.get(editPanes[i]);
            if (   (highlight != null)
                && (highlight.isSpaceHighlightEnabled() != enabled)
            ) {
                highlight.setSpaceHighlightEnabled(enabled);
                highlight.updateTextArea();
            }
        }
    }


    /**
     * Enables space highlights for a view
    **/
    public static void enableSpaceHighlightFor(View view) {
        WhiteSpaceHighlight.setSpaceHighlightEnabledFor(view, true);
    }


    /**
     * Disables space highlights for a view
    **/
    public static void disableSpaceHighlightFor(View view) {
        WhiteSpaceHighlight.setSpaceHighlightEnabledFor(view, false);
    }


    /**
     * Toggles space highlights for a view
    **/
    public static void toggleSpaceHighlightFor(View view) {
        if (WhiteSpaceHighlight.isSpaceHighlightEnabledFor(view)) {
            WhiteSpaceHighlight.disableSpaceHighlightFor(view);
        } else {
            WhiteSpaceHighlight.enableSpaceHighlightFor(view);
        }
    }


    /**
     * Tests if tab highlighting is enabled for a view
    **/
    public static boolean isTabHighlightEnabledFor(View view) {
        EditPane[] editPanes = view.getEditPanes();
        WhiteSpaceHighlight highlight;
        for (int i = 0; i < editPanes.length; i++) {
            if (editPanes[i] == null) { continue; }
            highlight = (WhiteSpaceHighlight) highlights.get(editPanes[i]);
            if (highlight != null && highlight.isTabHighlightEnabled()) {
                return true;
            }
        }

        return false;
    }


    /**
     * Sets tab highlighting enabled or disabled for a view
    **/
    public static void setTabHighlightEnabledFor(View view, boolean enabled) {
        EditPane[] editPanes = view.getEditPanes();
        WhiteSpaceHighlight highlight;
        for (int i = 0; i < editPanes.length; i++) {
            highlight = (WhiteSpaceHighlight) highlights.get(editPanes[i]);
            if (   (highlight != null)
                && (highlight.isTabHighlightEnabled() != enabled)
            ) {
                highlight.setTabHighlightEnabled(enabled);
                highlight.updateTextArea();
            }
        }
    }


    /**
     * Enables tab highlights for a view
    **/
    public static void enableTabHighlightFor(View view) {
        WhiteSpaceHighlight.setTabHighlightEnabledFor(view, true);
    }


    /**
     * Disables tab highlights for a view
    **/
    public static void disableTabHighlightFor(View view) {
        WhiteSpaceHighlight.setTabHighlightEnabledFor(view, false);
    }


    /**
     * Toggles tab highlights for a view
    **/
    public static void toggleTabHighlightFor(View view) {
        if (WhiteSpaceHighlight.isTabHighlightEnabledFor(view)) {
            WhiteSpaceHighlight.disableTabHighlightFor(view);
        } else {
            WhiteSpaceHighlight.enableTabHighlightFor(view);
        }
    }


    /**
     * Tests if whitespace highlighting is enabled for a view
    **/
    public static boolean isWhitespaceHighlightEnabledFor(View view) {
        EditPane[] editPanes = view.getEditPanes();
        WhiteSpaceHighlight highlight;
        for (int i = 0; i < editPanes.length; i++) {
            if (editPanes[i] == null) { continue; }
            highlight = (WhiteSpaceHighlight) highlights.get(editPanes[i]);
            if (highlight != null && highlight.isWhitespaceHighlightEnabled()) {
                return true;
            }
        }

        return false;
    }


    /**
     * Sets whitespace highlighting enabled or disabled for a view
    **/
    public static void setWhitespaceHighlightEnabledFor(View view, boolean enabled) {
        EditPane[] editPanes = view.getEditPanes();
        WhiteSpaceHighlight highlight;
        for (int i = 0; i < editPanes.length; i++) {
            highlight = (WhiteSpaceHighlight) highlights.get(editPanes[i]);
            if (   (highlight != null)
                && (highlight.isWhitespaceHighlightEnabled() != enabled)
            ) {
                highlight.setWhitespaceHighlightEnabled(enabled);
                highlight.updateTextArea();
            }
        }
    }


    /**
     * Enables whitespace highlights for a view
    **/
    public static void enableWhitespaceHighlightFor(View view) {
        WhiteSpaceHighlight.setWhitespaceHighlightEnabledFor(view, true);
    }


    /**
     * Disables whitespace highlights for a view
    **/
    public static void disableWhitespaceHighlightFor(View view) {
        WhiteSpaceHighlight.setWhitespaceHighlightEnabledFor(view, false);
    }


    /**
     * Toggles whitespace highlights for a view
    **/
    public static void toggleWhitespaceHighlightFor(View view) {
        if (WhiteSpaceHighlight.isWhitespaceHighlightEnabledFor(view)) {
            WhiteSpaceHighlight.disableWhitespaceHighlightFor(view);
        } else {
            WhiteSpaceHighlight.enableWhitespaceHighlightFor(view);
        }
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
            for (int j = 0; j < editPanes.length; j++) {
                highlight = (WhiteSpaceHighlight) highlights.get(editPanes[j]);
                if (   (spaceColorChanged      && highlight.isSpaceHighlightEnabled())
                    || (tabColorChanged        && highlight.isTabHighlightEnabled())
                    || (whitespaceColorChanged && highlight.isWhitespaceHighlightEnabled())
                ) {
                    highlight.updateTextArea();
                }
            }
        }
    }
}
