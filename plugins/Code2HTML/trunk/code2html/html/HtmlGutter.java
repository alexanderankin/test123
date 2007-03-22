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


/**
 *  Gutter for generated code in HTML
 *
 * @author     Andre Kaplan
 * @version    0.5
 */
public class HtmlGutter {
    /**
     *  The background colour
     */
    protected String bgColor;
    /**
     *  the foreground colour
     */
    protected String fgColor;
    /**
     *  the gutter border char
     */
    protected char gutterBorder = ':';
    /**
     *  the gutter border size
     */
    protected int gutterBorderSize = 1;
    /**
     *  size of the gutter
     */
    protected int gutterSize;
    /**
     *  The gutter hilihght colour
     */
    protected String highlightColor;
    /**
     *  the gutter hilight interval
     */
    protected int highlightInterval;
    /**
     *  The gutter spacer
     */
    protected String spacer;


    /**
     *  HtmlGutter Constructor
     *
     * @param  bgColor            Background colour
     * @param  fgColor            Foreground colour
     * @param  highlightColor     The hilighted lines colour
     * @param  highlightInterval  The hilight interval
     */
    public HtmlGutter(String bgColor,
                      String fgColor,
                      String highlightColor,
                      int highlightInterval) {
        this(4, bgColor, fgColor, highlightColor, highlightInterval);
    }


    /**
     *  HtmlGutter Constructor
     *
     * @param  gutterSize         The size of the gutter
     * @param  bgColor            Background colour
     * @param  fgColor            Foreground colour
     * @param  highlightColor     The hilighted lines colour
     * @param  highlightInterval  The hilight interval
     */
    public HtmlGutter(int gutterSize,
                      String bgColor,
                      String fgColor,
                      String highlightColor,
                      int highlightInterval) {
        this.gutterSize = gutterSize;

        this.bgColor = bgColor;
        this.fgColor = fgColor;

        this.highlightColor = highlightColor;
        this.highlightInterval = highlightInterval;

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < this.gutterSize; i++) {
            buf.append(' ');
        }

        this.spacer = buf.toString();
    }


    /**
     *  HtmlGutter Constructor
     */
    protected HtmlGutter() {
        this("#ffffff", "#8080c0", "#000000", 5);
    }


    /**
     *  Sets the gutter size of the object
     *
     * @param  gutterSize  The new gutter size value
     */
    public void setGutterSize(int gutterSize) {
        this.gutterSize = gutterSize;

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < this.gutterSize; i++) {
            buf.append(' ');
        }

        this.spacer = buf.toString();
    }


    /**
     *  Gets the gutter border size of the object
     *
     * @return    The gutter border size value
     */
    public int getGutterBorderSize() {
        return this.gutterBorderSize;
    }


    /**
     *  Gets the gutter size of the object
     *
     * @return    The gutter size value
     */
    public int getGutterSize() {
        return this.gutterSize;
    }


    /**
     *  Gets the size of the object
     *
     * @return    The size value
     */
    public int getSize() {
        return this.gutterSize + this.gutterBorderSize;
    }


    /**
     *  Does nothing! use the correct class!
     *
     * @return    an empty string
     */
    public String toCSS() {
        return "";
    }


    /**
     *  Gets a gutter without line numbering
     *
     * @param  lineNumber  The number of the line
     * @return             A string with the code to paint the line in HTML code
     */
    public String toEmptyHTML(int lineNumber) {
        boolean highlighted = (
            (this.highlightInterval > 0)
             && (lineNumber % this.highlightInterval == 0)
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
         *bufOpen.append("<font")
         *.append(" bgcolor=\"")
         *.append(this.bgColor)
         *.append("\">");
         *bufClose.insert(0, "</font>");
         */
        StringBuffer buf = new StringBuffer();
        buf.append(bufOpen.toString())
            .append(spacer)
            .append(this.gutterBorder)
            .append(bufClose.toString());

        return buf.toString();
    }


    /**
     *  Gets a String of HTML code to display the line number
     *
     * @param  lineNumber  The number of the line
     * @return             A string of HTML code representing the position in
     *      the gutter
     */
    public String toHTML(int lineNumber) {
        StringBuffer bufOpen = new StringBuffer();
        StringBuffer bufClose = new StringBuffer();

        if ((this.highlightInterval > 0)
             && (lineNumber % this.highlightInterval == 0)
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
         *bufOpen.append("<font")
         *.append(" bgcolor=\"")
         *.append(this.bgColor)
         *.append("\">");
         *bufClose.insert(0, "</font>");
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
}

