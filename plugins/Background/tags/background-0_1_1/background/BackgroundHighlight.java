/*
 * BackgroundHighlight.java
 * Copyright (c) 2002 Andre Kaplan
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


package background;


import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.FoldVisibilityManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.Log;


public class BackgroundHighlight extends TextAreaExtension
{
    // (EditPane, BackgroundHighlight) association
    private static Hashtable highlights = new Hashtable();

    private static String    iconName = null;
    private static ImageIcon icon     = null;

    private boolean enabled = true;

    private JEditTextArea textArea;


    static {
        iconName = jEdit.getProperty("background.file", (String) null);

        if ("".equals(iconName)) { iconName = null; }

        try {
            if (iconName != null) {
                icon = new ImageIcon(iconName);
            }
        } catch (Exception e) {
            iconName = null;
            icon     = null;
            Log.log(Log.DEBUG, BackgroundHighlight.class, e);
        }
    }


    private BackgroundHighlight(JEditTextArea textArea) {
        this.textArea = textArea;
    }


    public boolean isEnabled() {
        return this.enabled;
    }


    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }


    private void paintLine(final Graphics2D gfx, final int lineY) {
        if (!this.isEnabled()) { return; }

        if (icon == null) { return; }

        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) { return; }

        TextAreaPainter painter = this.textArea.getPainter();
        FontMetrics fm = this.textArea.getPainter().getFontMetrics();

        final int lineX = 0;

        // Line width
        int width  = painter.getWidth();

        // Line height
        int height = fm.getHeight();

        if ((lineY + height) >= painter.getHeight()) {
            height = painter.getHeight() - lineY;
        }

        int x0 = 0; // (lineX / icon.getIconWidth()) * icon.getIconWidth();
        int x1 = ((lineX + width - 1) / icon.getIconWidth()) * icon.getIconWidth();

        int y0 = (lineY / icon.getIconHeight()) * icon.getIconHeight();
        int y1 = ((lineY + height - 1) / icon.getIconHeight()) * icon.getIconHeight();

        // Remember the original clip bounds
        Rectangle rect = gfx.getClipBounds();

        gfx.setClip(lineX, lineY, width, height);

        for (int x = x0; x <= x1; x += icon.getIconWidth()) {
            for (int y = y0; y <= y1; y += icon.getIconHeight()) {
                icon.paintIcon(this.textArea, gfx, x, y);
            }
        }

        // Restore the original clip bounds
        gfx.setClip(rect);
    }


    public void paintValidLine(
            final Graphics2D gfx, final int screenLine, final int physicalLine,
            final int start, final int end, final int y
    ) {
        this.paintLine(gfx, y);
    }


    public void paintInvalidLine(
            final Graphics2D gfx, final int screenLine, final int y
    ) {
        this.paintLine(gfx, y);
    }


    private void updateTextArea() {
        if (this.textArea == null) { return; }

        FoldVisibilityManager foldVisibilityManager = this.textArea.getFoldVisibilityManager();

        int physicalFirst = foldVisibilityManager.getFirstVisibleLine();
        int physicalLast  = foldVisibilityManager.getLastVisibleLine();

        this.textArea.invalidateLineRange(physicalFirst, physicalLast);
    }


    public static TextAreaExtension addHighlightTo(EditPane editPane) {
        TextAreaExtension textAreaHighlight = new BackgroundHighlight(editPane.getTextArea());
        highlights.put(editPane, textAreaHighlight);
        return textAreaHighlight;
    }


    public static void removeHighlightFrom(EditPane editPane) {
        highlights.remove(editPane);
    }


    public static BackgroundHighlight getHighlightFor(EditPane editPane) {
        return (BackgroundHighlight) highlights.get(editPane);
    }


    public static boolean isHighlightEnabledFor(EditPane editPane) {
        BackgroundHighlight highlight = getHighlightFor(editPane);

        if (highlight == null) { return false; }

        return highlight.isEnabled();
    }


    public static void toggleHighlightEnabledFor(EditPane editPane) {
        BackgroundHighlight highlight = getHighlightFor(editPane);

        if (highlight == null) { return; }

        highlight.toggleEnabled();
        highlight.updateTextArea();
    }


    public static void propertiesChanged() {
        String    newIconName = jEdit.getProperty("background.file", (String) null);
        ImageIcon newIcon     = null;

        if ("".equals(newIconName)) { newIconName = null; }

        try {
            if (newIconName != null) {
                newIcon = new ImageIcon(newIconName);
            }
        } catch (Exception e) {
            newIconName = null;
            newIcon     = null;
            Log.log(Log.DEBUG, BackgroundHighlight.class, e);
        }


        iconName = newIconName;
        icon     = newIcon;

        // Propagate the changes to all textareas
        View[] views = jEdit.getViews();
        for (int i = 0; i < views.length; i++) {
            EditPane[] editPanes = views[i].getEditPanes();
            BackgroundHighlight highlight;

            for (int j = 0; j < editPanes.length; j++) {
                highlight = (BackgroundHighlight) highlights.get(editPanes[j]);
                if (highlight == null) { continue; }

                if (highlight.isEnabled()) {
                    highlight.updateTextArea();
                }
            }
        }
    }
}

