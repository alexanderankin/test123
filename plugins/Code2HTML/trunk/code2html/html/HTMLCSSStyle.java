/*
 * HTMLCSSStyle.java
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

import org.gjt.sp.util.Log;


public class HTMLCSSStyle extends HTMLStyle
{
    public HTMLCSSStyle(SyntaxStyle[] styles) {
        super(styles);
    }


    public String toHTML(int styleId, String text) {
        StringBuffer buf = new StringBuffer();
        buf.append("<SPAN CLASS=\"syntax" + styleId + "\">")
            .append(text)
            .append("</SPAN>");
        return buf.toString();
    }


    public String toCSS() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < this.styles.length; i++) {
            buf.append(".syntax" + i + " {\n")
                .append(toCSS(this.styles[i]))
                .append("}\n");
        }

        return buf.toString();
    }


    public static String toCSS(SyntaxStyle style) {
        if (style == null) {
            Log.log(Log.DEBUG, HTMLCSSStyle.class,
                    "toCSS(SyntaxStyle style): null style");
            return "";
        }
        StringBuffer buf = new StringBuffer();

        Color c;
        if ((c = style.getBackgroundColor()) != null) {
            buf.append("background: ")
                .append(GUIUtilities.getColorHexString(c))
                .append(";\n");
        }

        if ((c = style.getForegroundColor()) != null) {
            buf.append("color: ")
                .append(GUIUtilities.getColorHexString(c))
                .append(";\n");
        }

        if (style.getFont().isBold()) {
            buf.append("font-weight: bold;\n");
        }

        if (style.getFont().isItalic()) {
            buf.append("font-style: italic;\n");
        }

        return buf.toString();
    }
}

