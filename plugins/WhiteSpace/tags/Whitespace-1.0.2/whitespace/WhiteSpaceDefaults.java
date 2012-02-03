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
import org.gjt.sp.jedit.buffer.JEditBuffer;
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
            "white-space.show-fold-tooltip-default", false
        );
    }


    public static boolean getBlockHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-block-default", false
        );
    }


    public static boolean getSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-space-default", false
        );
    }


    public static boolean getLeadingSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-leading-space-default", false
        );
    }


    public static boolean getInnerSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-inner-space-default", false
        );
    }


    public static boolean getTrailingSpaceHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-trailing-space-default", false
        );
    }


    public static boolean getTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-tab-default", false
        );
    }


    public static boolean getLeadingTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-leading-tab-default", false
        );
    }


    public static boolean getInnerTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-inner-tab-default", false
        );
    }


    public static boolean getTrailingTabHighlightDefault() {
        return jEdit.getBooleanProperty(
            "white-space.show-trailing-tab-default", false
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


    public static void bufferCreated(JEditBuffer buffer) {
        WhiteSpaceModel model = (WhiteSpaceModel) buffer.getProperty(
            WhiteSpaceModel.MODEL_PROPERTY
        );
        if (model == null) {
            model = new WhiteSpaceModel();
            buffer.setProperty(WhiteSpaceModel.MODEL_PROPERTY, model);
        }
    }


    public static void editorStarted() {
        Buffer[] buffers = jEdit.getBuffers();
        for (int i = 0; i < buffers.length; i++) {
            WhiteSpaceDefaults.bufferCreated(buffers[i]);
        }
    }
}

