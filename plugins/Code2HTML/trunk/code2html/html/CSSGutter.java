/*
 *  CSSGutter.java
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

/**
 *  This object defines the way the gutter will look in a generated document
 *  using CSS stylesheets
 *
 *@author     dsm - Using large portions of code from Andre Kaplan
 *@version    0.1
 */
public class CSSGutter extends AbstractGutter {
    /**
     *  Constructor for the CSSGutter object
     *
     *@param  bgColor            Sets the Background Colour of the gutter
     *@param  fgColor            Sets the Text Colour of the gutter
     *@param  highlightColor     Sets the text Colur when the current line is
     *      hilighted
     *@param  gutterBorder       The character to use when the gutter border
     *      cannot be set through CSS
     *@param  spacer             Spacer string (empty spaces) to print before
     *      the line number (if any)
     *@param  gutterFontSize     Font size of the gutter
     *@param  highlightInterval  The interval at which lines of the gutter get
     *      hilighted in a different colour
     *@param  showingNumbers     true when the line numbers should be printed
     *      out
     *@param  showingGutter      Set to false to turn off the gutter
     */
    public CSSGutter(String bgColor,
                     String fgColor,
                     String highlightColor,
                     String gutterBorder,
                     String spacer,
                     String gutterFontSize,
                     int highlightInterval,
                     boolean showingNumbers,
                     boolean showingGutter) {
        /*
         *  spacer is substringed because an extra space is added to the SPAN
         *  tags in order to get a proper gutter margin showing in the output
         */
        super(bgColor,
            fgColor,
            highlightColor,
            gutterBorder,
            spacer.length() > 0 ? spacer.substring(1) : "",
            gutterFontSize,
            highlightInterval,
            showingNumbers,
            showingGutter);
    }


    /**
     *  Gets the header attribute of the CSSGutter object
     *
     *@return    The header value
     */
    public String getHeader() {
        if (!isShowingGutter()) {
            return "";
        }

        StringBuffer bufr = new StringBuffer();

        bufr.append(".outerGutter{\n")
            .append("\tborder-right: solid;\n")
            .append("\tborder-right-color: #660066;\n")
            .append("\tborder-right-width: 2;\n")
            .append("\tpadding-right:2px;\n")
            .append("\tbackground: ").append(getBgColor()).append(";\n")
            .append("\tline-height: 100%;\n")
            .append("}\n")
            .append(".gutter {\n")
            .append("\tcolor: ").append(getFgColor()).append(";\n")
            .append("\tfont-size: ").append(getGutterFontSize()).append(";\n")
            .append("}\n")
            .append(".gutterH {\n")
            .append("\tcolor: ").append(getHighlightColor()).append(";\n")
            .append("\tfont-size: ").append(getGutterFontSize()).append(";\n")
            .append("}\n");

        return bufr.toString();
    }


    /**
     *  Gets the line attribute of the CSSGutter object
     *
     *@param  lineNumber  The line we are getting code for
     *@return             The line value
     */
    public String getLine(int lineNumber) {
        if (!isShowingGutter()) {
            return "";
        }

        StringBuffer bufr = new StringBuffer();

        String style = (getHighlightInterval() > 0)
             && (lineNumber % getHighlightInterval() == 0) ?
            "gutterH" : "gutter";

        String s = Integer.toString(lineNumber);

        bufr.append("<span class=\"outerGutter\"> ")
            .append("<span class=\"" + style + "\">");

        if(lineNumber == WRAPPED_LINE){
            bufr.append(getSpacer()).append(" ");
        }else if (isShowingNumbers()) {
            bufr.append(
                getSpacer().substring(0, getSpacer().length() + 1 - s.length()))
                .append(s);
        } else {
            bufr.append(getSpacer());
        }

        bufr.append("</span></span>");

        return bufr.toString();
    }
}

