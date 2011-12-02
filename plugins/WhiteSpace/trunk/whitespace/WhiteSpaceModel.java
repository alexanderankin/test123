/*
 * WhiteSpaceModel.java
 * Copyright (c) 2001 Andre Kaplan
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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

import java.util.*;

public class WhiteSpaceModel
{
    public static final String MODEL_PROPERTY = "white-space.model";

    private Option blockHighlight;

    private Option foldHighlight;
    private Option foldTooltip;

    private Option spaceHighlight;
    private Option leadingSpaceHighlight;
    private Option innerSpaceHighlight;
    private Option trailingSpaceHighlight;

    private Option tabHighlight;
    private Option leadingTabHighlight;
    private Option innerTabHighlight;
    private Option trailingTabHighlight;

    private Option whitespaceHighlight;

    private Option removeTrailingWhitespace;
    private Option softTabifyLeadingWhitespace;
    private Option tabifyLeadingWhitespace;
    private Option untabifyLeadingWhitespace;

    private Map<String, Option> optsMap;

    public WhiteSpaceModel() {
        // Paragraph separators highlighting option
        this.blockHighlight         = new Option(
            WhiteSpaceDefaults.getBlockHighlightDefault()
        );

        // Folds highlighting options
        this.foldHighlight         = new Option(
            WhiteSpaceDefaults.getFoldHighlightDefault()
        );
        this.foldTooltip           = new Option(
            WhiteSpaceDefaults.getFoldTooltipDefault()
        );

        // Space highlighting options
        this.spaceHighlight         = new Option(
            WhiteSpaceDefaults.getSpaceHighlightDefault()
        );
        this.leadingSpaceHighlight  = new Option(
            WhiteSpaceDefaults.getLeadingSpaceHighlightDefault()
        );
        this.innerSpaceHighlight    = new Option(
            WhiteSpaceDefaults.getInnerSpaceHighlightDefault()
        );
        this.trailingSpaceHighlight = new Option(
            WhiteSpaceDefaults.getTrailingSpaceHighlightDefault()
        );

        // Tab highlighting options
        this.tabHighlight           = new Option(
            WhiteSpaceDefaults.getTabHighlightDefault()
        );
        this.leadingTabHighlight    = new Option(
            WhiteSpaceDefaults.getLeadingTabHighlightDefault()
        );
        this.innerTabHighlight      = new Option(
            WhiteSpaceDefaults.getInnerTabHighlightDefault()
        );
        this.trailingTabHighlight   = new Option(
            WhiteSpaceDefaults.getTrailingTabHighlightDefault()
        );

        // Whitespace highlighting option
        this.whitespaceHighlight    = new Option(
            WhiteSpaceDefaults.getWhitespaceHighlightDefault()
        );

        // On save actions options
        this.removeTrailingWhitespace    = new Option(
            WhiteSpaceDefaults.getRemoveTrailingWhitespace()
        );
        this.softTabifyLeadingWhitespace = new Option(
            WhiteSpaceDefaults.getSoftTabifyLeadingWhitespace()
        );
        this.tabifyLeadingWhitespace     = new Option(
            WhiteSpaceDefaults.getTabifyLeadingWhitespace()
        );
        this.untabifyLeadingWhitespace   = new Option(
            WhiteSpaceDefaults.getUntabifyLeadingWhitespace()
        );
        
        optsMap = new HashMap<String, Option>();
        // {{{ fill optsMap
		optsMap.put("space-highlight", this.spaceHighlight);
		optsMap.put("tab-highlight", this.tabHighlight);
		optsMap.put("whitespace-highlight", this.whitespaceHighlight);
		optsMap.put("block-highlight", this.blockHighlight);
		optsMap.put("fold-highlight", this.foldHighlight);
		optsMap.put("fold-tooltip", this.foldTooltip);
		optsMap.put("leading-space-highlight", this.leadingSpaceHighlight);
		optsMap.put("inner-space-highlight", this.innerSpaceHighlight);
		optsMap.put("trailing-space-highlight", this.trailingSpaceHighlight);
		optsMap.put("leading-tab-highlight", this.leadingTabHighlight);
		optsMap.put("inner-tab-highlight", this.innerTabHighlight);
		optsMap.put("trailing-tab-highlight", this.trailingTabHighlight);
		optsMap.put("remove-trailing-white-space", this.removeTrailingWhitespace);
		optsMap.put("soft-tabify-leading-white-space", this.softTabifyLeadingWhitespace);
		optsMap.put("tabify-leading-white-space", this.tabifyLeadingWhitespace);
		optsMap.put("untabify-leading-white-space", this.untabifyLeadingWhitespace);
		// }}}

    }

    public Option getOption(String sName) {
    	return optsMap.get(sName);
    }

    public Option getBlockHighlight() {
        return this.blockHighlight;
    }


    public Option getFoldHighlight() {
        return this.foldHighlight;
    }


    public Option getFoldTooltip() {
        return this.foldTooltip;
    }


    public Option getSpaceHighlight() {
        return this.spaceHighlight;
    }


    public Option getLeadingSpaceHighlight() {
        return this.leadingSpaceHighlight;
    }


    public Option getInnerSpaceHighlight() {
        return this.innerSpaceHighlight;
    }


    public Option getTrailingSpaceHighlight() {
        return this.trailingSpaceHighlight;
    }


    public Option getTabHighlight() {
        return this.tabHighlight;
    }


    public Option getLeadingTabHighlight() {
        return this.leadingTabHighlight;
    }


    public Option getInnerTabHighlight() {
        return this.innerTabHighlight;
    }


    public Option getTrailingTabHighlight() {
        return this.trailingTabHighlight;
    }


    public Option getWhitespaceHighlight() {
        return this.whitespaceHighlight;
    }


    public Option getRemoveTrailingWhitespace() {
        return this.removeTrailingWhitespace;
    }


    public Option getSoftTabifyLeadingWhitespace() {
        return this.softTabifyLeadingWhitespace;
    }


    public Option getTabifyLeadingWhitespace() {
        return this.tabifyLeadingWhitespace;
    }


    public Option getUntabifyLeadingWhitespace() {
        return this.untabifyLeadingWhitespace;
    }


    public class Option {
        private boolean enabled;


        public Option() {
            this(false);
        }


        public Option(boolean enabled) {
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
