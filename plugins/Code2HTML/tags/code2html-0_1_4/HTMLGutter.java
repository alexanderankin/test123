/*
 * HTMLGutter.java
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


import org.gjt.sp.jedit.jEdit;


public class HTMLGutter {
    private int gutterSize;
    private String spacer;
    private String bgColor;
    private String fgColor;
    private String highlightColor;
    private int highlightInterval;


    public HTMLGutter() {
        this(4);
    }


    public HTMLGutter(int gutterSize) {
        this.gutterSize = gutterSize;

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.gutterSize; i++) {
            buf.append(' ');
        }
        this.spacer = buf.toString();

        this.bgColor = jEdit.getProperty("view.gutter.bgColor", "#ffffff");
        this.fgColor = jEdit.getProperty("view.gutter.fgColor", "#8080c0");
        this.highlightColor = jEdit.getProperty("view.gutter.highlightColor",
                "#000000"
        );
        try
        {
            this.highlightInterval = Integer.parseInt(
                jEdit.getProperty("view.gutter.highlightInterval", "5")
            );
        }
        catch(NumberFormatException nfe) {}
    }


    public String toHTML(int lineNumber) {
        StringBuffer bufOpen = new StringBuffer();
        StringBuffer bufClose = new StringBuffer();

        if (    (this.highlightInterval > 0)
            &&  (lineNumber % this.highlightInterval == 0)
        ) {
            bufOpen.append("<FONT")
                .append(" COLOR=\"")
                .append(this.highlightColor)
                .append("\">");

        } else {
            bufOpen.append("<FONT")
                .append(" COLOR=\"")
                .append(this.fgColor)
                .append("\">");
        }
        bufClose.insert(0, "</FONT>");

        /*
        bufOpen.append("<FONT")
            .append(" BGCOLOR=\"")
            .append(this.bgColor)
            .append("\">");
        bufClose.insert(0, "</FONT>");
        */

        StringBuffer buf = new StringBuffer();
        String s = Integer.toString(lineNumber);
        buf.append(bufOpen.toString())
            .append(spacer.substring(0, this.gutterSize - s.length()))
            .append(s)
            .append(':')
            .append(bufClose.toString());

        return buf.toString();
    }


    public String toSpan(int lineNumber) {
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
