/*
 * HtmlCssStyle.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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
package code2html.html;

import java.awt.Color;

import org.gjt.sp.jedit.GUIUtilities;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.util.Log;


/**
 *  Gets the CSS Style for a buffer
 *
 * @author     Andre Kaplan
 * @version    0.5
 */
public class HtmlCssStyle extends HtmlStyle {
    /**
     *  HtmlCssStyle Constructor
     */
    public HtmlCssStyle() {
        super();
    }


    /**
     *  Creates css code to display a buffer tag
     *
     * @param  styleId  The ID of the style
     * @param  style    The actual style
     * @return          A String containing the CSS stylesheet for a style
     */
    public String toCSS(int styleId, SyntaxStyle style) {
        if (style == null) {
            Log.log(Log.DEBUG, this, "toCSS: null style");
            return "";
        }
        StringBuffer buf = new StringBuffer();

        buf.append("." + Token.TOKEN_TYPES[styleId] + " {\n");

        Color c;
        if ((c = style.getBackgroundColor()) != null) {
            buf.append("\tbackground: ")
                .append(GUIUtilities.getColorHexString(c))
                .append(";\n");
        }

        if ((c = style.getForegroundColor()) != null) {
            buf.append("\tcolor: ")
                .append(GUIUtilities.getColorHexString(c))
                .append(";\n");
        }

        if (style.getFont().isBold()) {
            buf.append("\tfont-weight: bold;\n");
        }

        if (style.getFont().isItalic()) {
            buf.append("\tfont-style: italic;\n");
        }

        buf.append("}\n");

        return buf.toString();
    }


    /**
     *  Gets a string surrounding the given text with span tags referencing the
     *  apropriate style
     *
     * @param  styleId  The ID of the style to use
     * @param  style    The actual style
     * @param  text     The text to put in between spans
     * @return          A piece of HTML with span tags referencing an adequate
     *      style in the CSS
     */
    public String toHTML(int styleId, SyntaxStyle style, String text) {
        StringBuffer buf = new StringBuffer();
        buf.append("<span class=\"" + Token.TOKEN_TYPES[styleId] + "\">")
            .append(text)
            .append("</span>");
        return buf.toString();
    }
}

