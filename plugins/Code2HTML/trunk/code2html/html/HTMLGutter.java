/*
 *  HTMLGutter.java
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
 *@author     dsm
 *@version    0.1
 */
public class HTMLGutter extends AbstractGutter {
    /**
     *  Constructor for the HTMLGutter object
     *
     *@param  bgColor            Sets the Background Colour of the gutter -
     *      DOESN'T WORK IN HTML MODE!
     *@param  fgColor            Sets the Text Colour of the gutter
     *@param  highlightColor     Sets the text Colur when the current line is
     *      hilighted
     *@param  gutterBorder       The character to use when the gutter border
     *      cannot be set through CSS
     *@param  spacer             Spacer string (empty spaces) to print before
     *      the line number (if any)
     *@param  gutterFontSize     Font size of the gutter - DOESN'T WORK IN HTML
     *      MODE!
     *@param  highlightInterval  The interval at which lines of the gutter get
     *      hilighted in a different colour
     *@param  showingNumbers     true when the line numbers should be printed
     *      out
     *@param  showingGutter      Set to false to turn off the gutter
     */
    public HTMLGutter(String bgColor,
                      String fgColor,
                      String highlightColor,
                      String gutterBorder,
                      String spacer,
                      String gutterFontSize,
                      int highlightInterval,
                      boolean showingNumbers,
                      boolean showingGutter) {
        super(bgColor,
            fgColor,
            highlightColor,
            gutterBorder,
            spacer,
            gutterFontSize,
            highlightInterval,
            showingNumbers,
            showingGutter);
    }


    /**
     *  Gets the header attribute of the HTMLGutter object
     *
     *@return    The header value
     */
    public String getHeader() {
        return "";
    }


    /**
     *  Gets the line attribute of the HTMLGutter object
     *
     *@param  lineNumber  The line we are getting code for
     *@return             The line value
     */
    public String getLine(int lineNumber) {
        if (!isShowingGutter()) {
            return "";
        }

        StringBuffer bufr = new StringBuffer();
        StringBuffer bufo = new StringBuffer();
        StringBuffer bufc = new StringBuffer();
        String s = Integer.toString(lineNumber);

        bufo.append("<font color=\"");

        if ((getHighlightInterval() > 0)
             && (lineNumber % getHighlightInterval() == 0)) {
            bufo.append(getHighlightColor());
        } else {
            bufo.append(getFgColor());
        }

        bufo.append("\">");
        bufc.insert(0, "</font>");
        bufr.append(bufo.toString());

        if (isShowingNumbers()) {
            bufr.append(
                getSpacer().substring(0, getSpacer().length() + 1 - s.length()))
                .append(s);
        } else {
            bufr.append(getSpacer());
        }

        bufr.append(getGutterBorder())
            .append(bufc.toString());

        return bufr.toString();
    }
}

