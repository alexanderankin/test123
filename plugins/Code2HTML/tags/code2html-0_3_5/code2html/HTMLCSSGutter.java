/*
 * HTMLCSSGutter.java
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


package code2html;

import org.gjt.sp.jedit.jEdit;


public class HTMLCSSGutter extends HTMLGutter
{
    public HTMLCSSGutter() {
        super();
    }


    public HTMLCSSGutter(int gutterSize) {
        super(gutterSize);
    }


    public String toHTML(int lineNumber) {
        StringBuffer buf = new StringBuffer();

        String style = "gutter";

        if (    (this.highlightInterval > 0)
            &&  (lineNumber % this.highlightInterval == 0)
        ) {
            style = "gutterH";
        }

        String s = Integer.toString(lineNumber);
        buf.append("<SPAN CLASS=\"" + style + "\">")
            .append(spacer.substring(0, this.gutterSize - s.length()))
            .append(s)
            .append(this.gutterBorder)
            .append("</SPAN>");

        return buf.toString();
    }


    public String toEmptyHTML(int lineNumber) {
        boolean highlighted = (
                (this.highlightInterval > 0)
            &&  (lineNumber % this.highlightInterval == 0)
        );
        StringBuffer buf = new StringBuffer();

        String style = "gutter";

        if (highlighted) {
            style = "gutterH";
        }

        buf.append("<SPAN CLASS=\"" + style + "\">")
            .append(spacer)
            .append(':')
            .append("</SPAN>");

        return buf.toString();
    }


    public String toCSS() {
        StringBuffer buf = new StringBuffer();

        buf.append(".gutter {\n")
            .append("background: " + this.bgColor + ";\n")
            .append("color: " + this.fgColor + ";\n")
            .append("}\n")
            .append(".gutterH {\n")
            .append("background: " + this.bgColor + ";\n")
            .append("color: " + this.highlightColor + ";\n")
            .append("}\n");

        return buf.toString();
    }
}

