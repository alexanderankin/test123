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
}

