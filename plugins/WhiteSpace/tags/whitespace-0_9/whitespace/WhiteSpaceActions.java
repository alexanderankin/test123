/*
 * WhiteSpaceActions.java
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


public class WhiteSpaceActions
{
    private WhiteSpaceActions() {}


    private static WhiteSpaceModel getWhiteSpaceModel(Buffer buffer) {
        return (WhiteSpaceModel) buffer.getProperty(
            WhiteSpaceModel.MODEL_PROPERTY
        );
    }


    public static void toggleBlockHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getBlockHighlight().toggleEnabled();

        BlockHighlight.updateTextAreas(buffer);
    }


    public static boolean isBlockHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getBlockHighlight().isEnabled();
    }


    public static void toggleFoldHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getFoldHighlight().toggleEnabled();

        FoldHighlight.updateTextAreas(buffer);
    }


    public static boolean isFoldHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getFoldHighlight().isEnabled();
    }


    public static void toggleFoldTooltip(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getFoldTooltip().toggleEnabled();

        FoldHighlight.updateTextAreas(buffer);
    }


    public static boolean isFoldTooltipSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getFoldTooltip().isEnabled();
    }


    public static void toggleSpaceHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getSpaceHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isSpaceHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getSpaceHighlight().isEnabled();
    }


    public static void toggleLeadingSpaceHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getLeadingSpaceHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isLeadingSpaceHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getLeadingSpaceHighlight().isEnabled();
    }


    public static void toggleInnerSpaceHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getInnerSpaceHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isInnerSpaceHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getInnerSpaceHighlight().isEnabled();
    }


    public static void toggleTrailingSpaceHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getTrailingSpaceHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isTrailingSpaceHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getTrailingSpaceHighlight().isEnabled();
    }


    public static void toggleTabHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getTabHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isTabHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getTabHighlight().isEnabled();
    }


    public static void toggleLeadingTabHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getLeadingTabHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isLeadingTabHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getLeadingTabHighlight().isEnabled();
    }


    public static void toggleInnerTabHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getInnerTabHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isInnerTabHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getInnerTabHighlight().isEnabled();
    }


    public static void toggleTrailingTabHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getTrailingTabHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isTrailingTabHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getTrailingTabHighlight().isEnabled();
    }


    public static void toggleWhitespaceHighlight(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getWhitespaceHighlight().toggleEnabled();

        WhiteSpaceHighlight.updateTextAreas(buffer);
    }


    public static boolean isWhitespaceHighlightSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getWhitespaceHighlight().isEnabled();
    }


    public static void toggleRemoveTrailing(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        model.getRemoveTrailingWhitespace().toggleEnabled();
    }


    public static boolean isRemoveTrailingSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getRemoveTrailingWhitespace().isEnabled();
    }


    public static void toggleSoftTabifyLeading(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        WhiteSpaceModel.Option option = model.getSoftTabifyLeadingWhitespace();

        option.toggleEnabled();

        if (option.isEnabled()) {
            // model.getSoftTabifyLeadingWhitespace().setEnabled(false);
            model.getTabifyLeadingWhitespace().setEnabled(false);
            model.getUntabifyLeadingWhitespace().setEnabled(false);
        }
    }


    public static boolean isSoftTabifyLeadingSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getSoftTabifyLeadingWhitespace().isEnabled();
    }


    public static void toggleTabifyLeading(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        WhiteSpaceModel.Option option = model.getTabifyLeadingWhitespace();

        option.toggleEnabled();

        if (option.isEnabled()) {
            model.getSoftTabifyLeadingWhitespace().setEnabled(false);
            // model.getTabifyLeadingWhitespace().setEnabled(false);
            model.getUntabifyLeadingWhitespace().setEnabled(false);
        }
    }


    public static boolean isTabifyLeadingSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getTabifyLeadingWhitespace().isEnabled();
    }


    public static void toggleUntabifyLeading(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return; }

        WhiteSpaceModel.Option option = model.getUntabifyLeadingWhitespace();

        option.toggleEnabled();

        if (option.isEnabled()) {
            model.getSoftTabifyLeadingWhitespace().setEnabled(false);
            model.getTabifyLeadingWhitespace().setEnabled(false);
            // model.getUntabifyLeadingWhitespace().setEnabled(false);
        }
    }


    public static boolean isUntabifyLeadingSelected(Buffer buffer) {
        WhiteSpaceModel model = getWhiteSpaceModel(buffer);
        if (model == null) { return false; }

        return model.getUntabifyLeadingWhitespace().isEnabled();
    }
}

