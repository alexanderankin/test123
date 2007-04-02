/*
 *  HTMLStyle.java
 *  Copyright (c) 2007 David Moss
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package code2html.html;

import java.awt.Color;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.syntax.SyntaxStyle;

/**
 *  Abstract definition of a style, subclassed into HTML version
 *
 *@author     dsm - Using large portions of Andre Kaplan's code
 *@version    0.1
 */
public class HTMLStyle {
    /**
     *  Constructor for the HTMLStyle
     */
    public HTMLStyle() { }


    /**
     *  Gets the header attribute of the HTMLStyle object
     *
     *@param  styleId  The ID of the Style we are printing. Jedit will make
     *      sense of this
     *@param  style    The actual style we are using
     *@return          The header value
     */
    public String getHeader(int styleId,
                            SyntaxStyle style) {
        return "<!-- Hacked with the power of jEdit -->";
        // The header doesn't need any code
    }


    /**
     *  Gets the token attribute of the HTMLStyle object. Background colours
     *  don't work in HTML mode!
     *
     *@param  styleId  The ID of the Style we are printing. Jedit will make
     *      sense of this
     *@param  style    The actual style we are using
     *@param  text     The text that will be tagged by this style
     *@return          The token value
     */
    public String getToken(int styleId,
                           SyntaxStyle style,
                           String text) {
        if (style == null) {
            return text;
        }

        StringBuffer bufo = new StringBuffer();
        StringBuffer bufc = new StringBuffer();

        Color c;

        if ((c = style.getForegroundColor()) != null) {
            bufo.append("<font")
                .append(" color=\"")
                .append(GUIUtilities.getColorHexString(c))
                .append("\">");
            bufc.insert(0, "</font>");
        }

        if (style.getFont().isBold()) {
            bufo.append("<strong>");
            bufc.insert(0, "</strong>");
        }

        if (style.getFont().isItalic()) {
            bufo.append("<em>");
            bufc.insert(0, "</em>");
        }

        StringBuffer buf = new StringBuffer();
        buf.append(bufo).append(text).append(bufc);

        return buf.toString();
    }
}

