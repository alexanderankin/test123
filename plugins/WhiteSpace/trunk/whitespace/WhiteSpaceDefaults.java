/*
 * WhiteSpaceDefaults.java
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

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;


public class WhiteSpaceDefaults
{
    public static boolean getFoldHighlightDefault() {
       return jEdit.getBooleanProperty(
            "white-space.show-fold-default", true
        );
    }


    public static boolean getFoldTooltipDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-fold-tooltip-default", true
        );
    }


    public static boolean getBlockHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-block-default", false
        );
    }


    public static boolean getSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-space-default", true
        );
    }


    public static boolean getLeadingSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-leading-space-default", true
        );
    }


    public static boolean getInnerSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-inner-space-default", true
        );
    }


    public static boolean getTrailingSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-trailing-space-default", true
        );
    }


    public static boolean getTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-tab-default", true
        );
    }


    public static boolean getLeadingTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-leading-tab-default", true
        );
    }


    public static boolean getInnerTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-inner-tab-default", true
        );
    }


    public static boolean getTrailingTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-trailing-tab-default", true
        );
    }


    public static boolean getWhitespaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-whitespace-default", false
        );
    }


    public static boolean getRemoveTrailingWhitespace() {
        return jEdit.getBooleanProperty(
            "white-space.remove-trailing-white-space", false
        );
    }


    public static boolean getSoftTabifyLeadingWhitespace() {
        return jEdit.getBooleanProperty(
            "white-space.soft-tabify-leading-white-space", false
        );
    }


    public static boolean getTabifyLeadingWhitespace() {
        return jEdit.getBooleanProperty(
            "white-space.tabify-leading-white-space", false
        );
    }


    public static boolean getUntabifyLeadingWhitespace() {
        return jEdit.getBooleanProperty(
            "white-space.untabify-leading-white-space", false
        );
    }


    public static void bufferCreated(Buffer buffer) {
        // Paragraph separators highlighting option
        putBooleanProperty(
            buffer,
            BlockHighlight.BLOCK_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getBlockHighlightDefault()
        );

        // Folds highlighting options
        putBooleanProperty(
            buffer,
            FoldHighlight.FOLD_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getFoldHighlightDefault()
        );
        putBooleanProperty(
            buffer,
            FoldHighlight.FOLD_TOOLTIP_PROPERTY,
            WhiteSpaceDefaults.getFoldTooltipDefault()
        );

        WhiteSpaceModel model = (WhiteSpaceModel) buffer.getProperty(
            WhiteSpaceModel.MODEL_PROPERTY
        );
        if (model == null) {
            model = new WhiteSpaceModel(buffer);
            buffer.putProperty(WhiteSpaceModel.MODEL_PROPERTY, model);
        }

        // Space highlighting options
        model.getSpaceHighlight().setEnabled(
            WhiteSpaceDefaults.getSpaceHighlightDefault()
        );
        model.getLeadingSpaceHighlight().setEnabled(
            WhiteSpaceDefaults.getLeadingSpaceHighlightDefault()
        );
        model.getInnerSpaceHighlight().setEnabled(
            WhiteSpaceDefaults.getInnerSpaceHighlightDefault()
        );
        model.getTrailingSpaceHighlight().setEnabled(
            WhiteSpaceDefaults.getTrailingSpaceHighlightDefault()
        );

        // Tab highlighting options
        model.getTabHighlight().setEnabled(
            WhiteSpaceDefaults.getTabHighlightDefault()
        );
        model.getLeadingTabHighlight().setEnabled(
            WhiteSpaceDefaults.getLeadingTabHighlightDefault()
        );
        model.getInnerTabHighlight().setEnabled(
            WhiteSpaceDefaults.getInnerTabHighlightDefault()
        );
        model.getTrailingTabHighlight().setEnabled(
            WhiteSpaceDefaults.getTrailingTabHighlightDefault()
        );

        // Whitespace highlighting option
        model.getWhitespaceHighlight().setEnabled(
            WhiteSpaceDefaults.getWhitespaceHighlightDefault()
        );

        // On save actions options
        putBooleanProperty(
              buffer
            , "white-space.remove-trailing-white-space"
            , WhiteSpaceDefaults.getRemoveTrailingWhitespace()
        );

        putBooleanProperty(
              buffer
            , "white-space.soft-tabify-leading-white-space"
            , WhiteSpaceDefaults.getSoftTabifyLeadingWhitespace()
        );

        putBooleanProperty(
              buffer
            , "white-space.tabify-leading-white-space"
            , WhiteSpaceDefaults.getTabifyLeadingWhitespace()
        );

        putBooleanProperty(
              buffer
            , "white-space.untabify-leading-white-space"
            , WhiteSpaceDefaults.getUntabifyLeadingWhitespace()
        );
    }


    public static void editorStarted() {
        Buffer[] buffers = jEdit.getBuffers();
        for (int i = 0; i < buffers.length; i++) {
            WhiteSpaceDefaults.bufferCreated(buffers[i]);
        }
    }


    private static void putBooleanProperty(
            Buffer buffer, String propertyName, boolean propertyValue
    ) {
        if (buffer.getProperty(propertyName) == null) {
            buffer.putBooleanProperty(propertyName, propertyValue);
        }
    }
}

