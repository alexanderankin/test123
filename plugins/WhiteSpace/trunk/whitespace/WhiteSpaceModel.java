/*
 * WhiteSpaceModel.java
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
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class WhiteSpaceModel
{
    public static final String SPACE_HIGHLIGHT_PROPERTY          = "white-space.space-highlight";
    public static final String LEADING_SPACE_HIGHLIGHT_PROPERTY  = "white-space.leading-space-highlight";
    public static final String INNER_SPACE_HIGHLIGHT_PROPERTY    = "white-space.inner-space-highlight";
    public static final String TRAILING_SPACE_HIGHLIGHT_PROPERTY = "white-space.trailing-space-highlight";

    public static final String TAB_HIGHLIGHT_PROPERTY            = "white-space.tab-highlight";
    public static final String LEADING_TAB_HIGHLIGHT_PROPERTY    = "white-space.leading-tab-highlight";
    public static final String INNER_TAB_HIGHLIGHT_PROPERTY      = "white-space.inner-tab-highlight";
    public static final String TRAILING_TAB_HIGHLIGHT_PROPERTY   = "white-space.trailing-tab-highlight";

    public static final String WHITESPACE_HIGHLIGHT_PROPERTY     = "white-space.whitespace-highlight";

    private JEditTextArea textArea;

    private HighlightOption spaceHighlight;
    private HighlightOption leadingSpaceHighlight;
    private HighlightOption innerSpaceHighlight;
    private HighlightOption trailingSpaceHighlight;

    private HighlightOption tabHighlight;
    private HighlightOption leadingTabHighlight;
    private HighlightOption innerTabHighlight;
    private HighlightOption trailingTabHighlight;

    private HighlightOption whitespaceHighlight;


    public WhiteSpaceModel(JEditTextArea textArea) {
        this.textArea = textArea;

        this.spaceHighlight         = new HighlightOption(
            SPACE_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getSpaceHighlightDefault()
        );
        this.leadingSpaceHighlight  = new HighlightOption(
            LEADING_SPACE_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getLeadingSpaceHighlightDefault()
        );
        this.innerSpaceHighlight    = new HighlightOption(
            INNER_SPACE_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getInnerSpaceHighlightDefault()
        );
        this.trailingSpaceHighlight = new HighlightOption(
            TRAILING_SPACE_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getTrailingSpaceHighlightDefault()
        );

        this.tabHighlight           = new HighlightOption(
            TAB_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getTabHighlightDefault()
        );
        this.leadingTabHighlight    = new HighlightOption(
            LEADING_TAB_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getLeadingTabHighlightDefault()
        );
        this.innerTabHighlight      = new HighlightOption(
            INNER_TAB_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getInnerTabHighlightDefault()
        );
        this.trailingTabHighlight   = new HighlightOption(
            TRAILING_TAB_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getTrailingTabHighlightDefault()
        );

        this.whitespaceHighlight    = new HighlightOption(
            WHITESPACE_HIGHLIGHT_PROPERTY,
            WhiteSpaceDefaults.getWhitespaceHighlightDefault()
        );
    }


    public HighlightOption getSpaceHighlight() {
        return this.spaceHighlight;
    }


    public HighlightOption getLeadingSpaceHighlight() {
        return this.leadingSpaceHighlight;
    }


    public HighlightOption getInnerSpaceHighlight() {
        return this.innerSpaceHighlight;
    }


    public HighlightOption getTrailingSpaceHighlight() {
        return this.trailingSpaceHighlight;
    }


    public HighlightOption getTabHighlight() {
        return this.tabHighlight;
    }


    public HighlightOption getLeadingTabHighlight() {
        return this.leadingTabHighlight;
    }


    public HighlightOption getInnerTabHighlight() {
        return this.innerTabHighlight;
    }


    public HighlightOption getTrailingTabHighlight() {
        return this.trailingTabHighlight;
    }


    public HighlightOption getWhitespaceHighlight() {
        return this.whitespaceHighlight;
    }


    public class HighlightOption {
        private final String propertyName;


        public HighlightOption(String propertyName) {
            this(propertyName, false);
        }


        public HighlightOption(String propertyName, boolean enabled) {
            this.propertyName = propertyName;
            this.setEnabled(enabled);
        }


        public boolean isEnabled() {
            Buffer buffer = WhiteSpaceModel.this.textArea.getBuffer();
            return buffer.getBooleanProperty(this.propertyName);
        }


        public void setEnabled(boolean enabled) {
            Buffer buffer = WhiteSpaceModel.this.textArea.getBuffer();
            buffer.putBooleanProperty(this.propertyName, enabled);
        }


        public void toggleEnabled() {
            this.setEnabled(!this.isEnabled());
        }
    }
}
