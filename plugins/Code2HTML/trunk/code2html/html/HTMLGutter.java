/*
 * HTMLGutter.java
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

import org.gjt.sp.jedit.jEdit;


public class HTMLGutter
{
    protected char gutterBorder     = ':';
    protected int  gutterBorderSize = 1;

    protected int gutterSize;
    protected String spacer;
    protected String bgColor;
    protected String fgColor;
    protected String highlightColor;
    protected int highlightInterval;


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


    public String toEmptyHTML(int lineNumber) {
        boolean highlighted = (
                (this.highlightInterval > 0)
            &&  (lineNumber % this.highlightInterval == 0)
        );

        StringBuffer bufOpen = new StringBuffer();
        StringBuffer bufClose = new StringBuffer();

        if (highlighted) {
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
        buf.append(bufOpen.toString())
            .append(spacer)
            .append(this.gutterBorder)
            .append(bufClose.toString());

        return buf.toString();
    }


    public String toCSS() {
        return "";
    }


    public int getGutterSize() {
        return this.gutterSize;
    }


    public void setGutterSize(int gutterSize) {
        this.gutterSize = gutterSize;

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.gutterSize; i++) {
            buf.append(' ');
        }
        this.spacer = buf.toString();
    }


    public int getGutterBorderSize() {
        return this.gutterBorderSize;
    }


    public int getSize() {
        return this.gutterSize + this.gutterBorderSize;
    }
}

