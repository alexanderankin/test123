/*
 * HtmlGutter.java
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


public class HtmlGutter
{
    protected char gutterBorder     = ':';
    protected int  gutterBorderSize = 1;

    protected int gutterSize;

    protected String bgColor;
    protected String fgColor;

    protected String highlightColor;
    protected int highlightInterval;

    protected String spacer;


    protected HtmlGutter() {
        this("#ffffff", "#8080c0", "#000000", 5);
    }


    public HtmlGutter(
            String bgColor, String fgColor,
            String highlightColor, int highlightInterval
    ) {
        this(4, bgColor, fgColor, highlightColor, highlightInterval);
    }


    public HtmlGutter(
            int gutterSize,
            String bgColor, String fgColor,
            String highlightColor, int highlightInterval
    ) {
        this.gutterSize = gutterSize;

        this.bgColor = bgColor;
        this.fgColor = fgColor;

        this.highlightColor    = highlightColor;
        this.highlightInterval = highlightInterval;

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.gutterSize; i++) {
            buf.append(' ');
        }
        this.spacer = buf.toString();
    }


    public String toHTML(int lineNumber) {
        StringBuffer bufOpen = new StringBuffer();
        StringBuffer bufClose = new StringBuffer();

        if (    (this.highlightInterval > 0)
            &&  (lineNumber % this.highlightInterval == 0)
        ) {
            bufOpen.append("<font")
                .append(" color=\"")
                .append(this.highlightColor)
                .append("\">");

        } else {
            bufOpen.append("<font")
                .append(" color=\"")
                .append(this.fgColor)
                .append("\">");
        }
        bufClose.insert(0, "</font>");

        /*
        bufOpen.append("<font")
            .append(" bgcolor=\"")
            .append(this.bgColor)
            .append("\">");
        bufClose.insert(0, "</font>");
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
            bufOpen.append("<font")
                .append(" color=\"")
                .append(this.highlightColor)
                .append("\">");

        } else {
            bufOpen.append("<font")
                .append(" color=\"")
                .append(this.fgColor)
                .append("\">");
        }
        bufClose.insert(0, "</font>");

        /*
        bufOpen.append("<font")
            .append(" bgcolor=\"")
            .append(this.bgColor)
            .append("\">");
        bufClose.insert(0, "</font>");
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

