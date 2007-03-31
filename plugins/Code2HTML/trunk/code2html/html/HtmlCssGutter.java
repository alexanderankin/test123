/*
 * HtmlCssGutter.java
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

/**
 *  Manager class for the gutter when CSS mode in enabled
 *
 * @author     Andre Kaplan
 * @version    0.5
 */
public class HtmlCssGutter extends HtmlGutter {
    private String nl = jEdit.getProperty("plugin.code2html.line.separator");


    /**
     *  HtmlCssGutter Constructor
     *
     * @param  bgColor            Background colour
     * @param  fgColor            Foregroud colour
     * @param  highlightColor     Hilighted row colour
     * @param  highlightInterval  Interval at which to highlight rows
     */
    public HtmlCssGutter(String bgColor,
                         String fgColor,
                         String highlightColor,
                         int highlightInterval) {
        super(bgColor, fgColor, highlightColor, highlightInterval);
    }


    /**
     *  HtmlCssGutter Constructor
     *
     * @param  gutterSize         Size of the gutter in spaces
     * @param  bgColor            Background colour
     * @param  fgColor            Foregroud colour
     * @param  highlightColor     Hilighted row colour
     * @param  highlightInterval  Interval at which to highlight rows
     */
    public HtmlCssGutter(int gutterSize,
                         String bgColor,
                         String fgColor,
                         String highlightColor,
                         int highlightInterval) {
        super(gutterSize, bgColor, fgColor, highlightColor, highlightInterval);
    }


    /**
     *  HtmlCssGutter Constructor
     */
    protected HtmlCssGutter() {
        super();
    }


    /**
     *  Gets a string with the CSS code necessary to render the gutter
     *
     * @return    A string with the gutter CSS code
     */
    public String toCSS() {
        StringBuffer buf = new StringBuffer();

        buf.append(".outerGutter{").append(nl)
	        .append("\tborder-right: solid;").append(nl)
	        .append("\tborder-right-color: #660066;").append(nl)
	        .append("\tborder-right-width: 2;").append(nl)
            .append("\tpadding-right:2px;").append(nl)
	        .append("\tbackground: " + this.bgColor + ";").append(nl)
            .append("\tline-height: 100%;").append(nl)
            .append("}").append(nl)
            .append(".gutter {").append(nl)
            .append("\tcolor: " + this.fgColor + ";").append(nl)
            .append("\tfont-size: ")
            .append(jEdit.getProperty("view.gutter.fontsize")).append(";")
            .append(nl)
            .append("}").append(nl)
            .append(".gutterH {").append(nl)
            .append("\tcolor: " + this.highlightColor + ";").append(nl)
            .append("\tfont-size: ")
            .append(jEdit.getProperty("view.gutter.fontsize")).append(";")
            .append(nl)
            .append("}").append(nl);

        return buf.toString();
    }


    /**
     *  Gets a String with the reference to the CSS needed to draw the gutter.
     *  This method does not print the line numbers
     *
     * @param  lineNumber  The line number
     * @return             A String with the necessary span tags
     */
    public String toEmptyHTML(int lineNumber) {
        boolean highlighted = ((this.highlightInterval > 0)
             && (lineNumber % this.highlightInterval == 0));
        StringBuffer buf = new StringBuffer();

        String style = highlighted ? "gutterH" : "gutter";

        buf.append("<span class=\"outerGutter\"> ")
            .append("<span class=\"" + style + "\">")
            .append(spacer.substring(1))
            .append(this.gutterBorder)
            .append("</span></span>");

        return buf.toString();
    }


    /**
     *  Gets a String with the reference to the CSS needed to draw the gutter
     *
     * @param  lineNumber  The line number
     * @return             A String with the necessary span tags
     */
    public String toHTML(int lineNumber) {
        StringBuffer buf = new StringBuffer();

        String style = (this.highlightInterval > 0)
             && (lineNumber % this.highlightInterval == 0) ?
            "gutterH" : "gutter";

        String s = Integer.toString(lineNumber);

        buf.append("<span class=\"outerGutter\"> ")
            .append("<span class=\"" + style + "\">")
            .append(spacer.substring(1, this.gutterSize - s.length()))
            .append(s)
            //.append(this.gutterBorder)
            .append("</span></span>");

        return buf.toString();
    }
}
