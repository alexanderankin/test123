/*
 * HTMLStyle.java
 * Copyright (c) 2000 Andre Kaplan
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


package code2html;


import java.awt.Color;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.syntax.SyntaxStyle;

import org.gjt.sp.util.Log;


public class HTMLStyle
{
    protected SyntaxStyle[] styles = null;


    public HTMLStyle(SyntaxStyle[] styles) {
        this.styles = styles;
    }


    public String toHTML(int styleId, String text) {
        return this.toHTML(this.styles[styleId], text);
    }


    public String toHTML(SyntaxStyle style, String text) {
        if (style == null) {
            Log.log(Log.DEBUG, HTMLStyle.class,
                    "toHTML(SyntaxStyle style): null style");
            return text;
        }
        StringBuffer bufOpen  = new StringBuffer();
        StringBuffer bufClose = new StringBuffer();

        Color c;
        /*
        if ((c = style.getBackgroundColor()) != null) {
            bufOpen.append("<FONT")
                .append(" BGCOLOR=\"")
                .append(GUIUtilities.getColorHexString(c))
                .append("\">");
            bufClose.insert(0, "</FONT>");
        }
        */

        if ((c = style.getForegroundColor()) != null) {
            bufOpen.append("<FONT")
                .append(" COLOR=\"")
                .append(GUIUtilities.getColorHexString(c))
                .append("\">");
            bufClose.insert(0, "</FONT>");
        }

        if (style.isBold()) {
            bufOpen.append("<STRONG>");
            bufClose.insert(0, "</STRONG>");
        }

        if (style.isItalic()) {
            bufOpen.append("<EM>");
            bufClose.insert(0, "</EM>");
        }

        StringBuffer buf = new StringBuffer();
        buf.append(bufOpen).append(text).append(bufClose);

        return buf.toString();
    }
}
