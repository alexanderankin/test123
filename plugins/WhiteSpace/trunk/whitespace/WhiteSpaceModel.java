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


public class WhiteSpaceModel
{
    public static final String MODEL_PROPERTY = "white-space.model";

    private HighlightOption spaceHighlight;
    private HighlightOption leadingSpaceHighlight;
    private HighlightOption innerSpaceHighlight;
    private HighlightOption trailingSpaceHighlight;

    private HighlightOption tabHighlight;
    private HighlightOption leadingTabHighlight;
    private HighlightOption innerTabHighlight;
    private HighlightOption trailingTabHighlight;

    private HighlightOption whitespaceHighlight;


    public WhiteSpaceModel() {
        this.spaceHighlight         = new HighlightOption(
            WhiteSpaceDefaults.getSpaceHighlightDefault()
        );
        this.leadingSpaceHighlight  = new HighlightOption(
            WhiteSpaceDefaults.getLeadingSpaceHighlightDefault()
        );
        this.innerSpaceHighlight    = new HighlightOption(
            WhiteSpaceDefaults.getInnerSpaceHighlightDefault()
        );
        this.trailingSpaceHighlight = new HighlightOption(
            WhiteSpaceDefaults.getTrailingSpaceHighlightDefault()
        );

        this.tabHighlight           = new HighlightOption(
            WhiteSpaceDefaults.getTabHighlightDefault()
        );
        this.leadingTabHighlight    = new HighlightOption(
            WhiteSpaceDefaults.getLeadingTabHighlightDefault()
        );
        this.innerTabHighlight      = new HighlightOption(
            WhiteSpaceDefaults.getInnerTabHighlightDefault()
        );
        this.trailingTabHighlight   = new HighlightOption(
            WhiteSpaceDefaults.getTrailingTabHighlightDefault()
        );

        this.whitespaceHighlight    = new HighlightOption(
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
        private boolean enabled;


        public HighlightOption() {
            this(false);
        }


        public HighlightOption(boolean enabled) {
            this.enabled = enabled;
        }


        public boolean isEnabled() {
            return this.enabled;
        }


        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }


        public void toggleEnabled() {
            this.setEnabled(!this.isEnabled());
        }
    }
}
