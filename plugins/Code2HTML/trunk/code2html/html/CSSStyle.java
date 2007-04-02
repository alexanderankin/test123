/*
 *  CSSStyle.java
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
import org.gjt.sp.jedit.syntax.Token;

/**
 *  Abstract definition of a style, subclassed into CSS version
 *
 *@author     dsm - Using large portions of Andre Kaplan's code
 *@version    0.1
 */
public class CSSStyle extends AbstractStyle {
    /**
     *  Constructor for the CSSStyle
     */
    public CSSStyle() { }


    /**
     *  Gets the header attribute of the CSSStyle object
     *
     *@param  styleId  The ID of the Style we are printing. Jedit will make
     *      sense of this
     *@param  style    The actual style we are using
     *@return          The header value
     */
    public String getHeader(int styleId,
                            SyntaxStyle style) {
        if (style == null) {
            return "";
        }

        StringBuffer bufr = new StringBuffer(".")
            .append(Token.TOKEN_TYPES[styleId])
            .append(" {\n");

        Color c;

        if ((c = style.getBackgroundColor()) != null) {
            bufr.append("\tbackground: ")
                .append(GUIUtilities.getColorHexString(c))
                .append(";\n");
        }

        if ((c = style.getForegroundColor()) != null) {
            bufr.append("\tcolor: ")
                .append(GUIUtilities.getColorHexString(c))
                .append(";\n");
        }

        if (style.getFont().isBold()) {
            bufr.append("\tfont-weight: bold;\n");
        }

        if (style.getFont().isItalic()) {
            bufr.append("\tfont-style: italic;\n");
        }

        bufr.append("}\n");

        return bufr.toString();
    }


    /**
     *  Gets the token attribute of the CSSStyle object
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
        return new StringBuffer("<span class=\"")
            .append(Token.TOKEN_TYPES[styleId])
            .append("\">")
            .append(text)
            .append("</span>")
            .toString();
    }
}

