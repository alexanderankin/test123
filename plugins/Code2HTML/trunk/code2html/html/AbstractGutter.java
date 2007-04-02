/*
 *  AbstractGutter.java
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
 *  Abstract class that generates code for the gutter of a jEdit buffer
 *
 *@author     dsm
 *@version    0.1
 */
public abstract class AbstractGutter {
    private String bgColor;
    private String fgColor;
    private String highlightColor;
    private String gutterBorder;
    private String gutterFontSize;
    private String spacer;
    private int highlightInterval;
    private boolean showingNumbers;
    private boolean showingGutter;
    /**
     *  Use this as line number when wrapping so that the number doesn't show in
     *  the output HTML
     */
    public static final int WRAPPED_LINE = -1;


    /**
     *  Constructor for the AbstractGutter object
     *
     *@param  bgColor            Sets the Background Colour of the gutter
     *@param  fgColor            Sets the Text Colour of the gutter
     *@param  highlightColor     Sets the text Colur when the current line is
     *      hilighted
     *@param  spacer             Spacer string (empty spaces) to print before
     *      the line number (if any)
     *@param  gutterBorder       The character to use when the gutter border
     *      cannot be set through CSS
     *@param  highlightInterval  The interval at which lines of the gutter get
     *      hilighted in a different colour
     *@param  showingNumbers     true when the line numbers should be printed
     *      out
     *@param  showingGutter      Set to false to turn off the gutter
     *@param  gutterFontSize     Font size of the gutter
     */
    public AbstractGutter(String bgColor,
                          String fgColor,
                          String highlightColor,
                          String gutterBorder,
                          String spacer,
                          String gutterFontSize,
                          int highlightInterval,
                          boolean showingNumbers,
                          boolean showingGutter) {
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.gutterBorder = gutterBorder;
        this.highlightColor = highlightColor;
        this.highlightInterval = highlightInterval;
        this.spacer = spacer;
        this.showingNumbers = showingNumbers;
        this.showingGutter = showingGutter;
        this.gutterFontSize = gutterFontSize;
    }


    /**
     *  Gets the spacer for the size of the gutter
     *
     *@param  size  THe size of the spacer
     *@return       The spacer for value
     */
    public static synchronized String getSpacerFor(int size) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < size; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }


    /**
     *  Gets the gutter font size attribute of the AbstractGutter object
     *
     *@return    The gutter font size value
     */
    public String getGutterFontSize() {
        return this.gutterFontSize;
    }


    /**
     *  Gets the bg color attribute of the AbstractGutter object
     *
     *@return    The bg color value
     */
    public String getBgColor() {
        return bgColor;
    }


    /**
     *  Gets the fg color attribute of the AbstractGutter object
     *
     *@return    The fg color value
     */
    public String getFgColor() {
        return fgColor;
    }


    /**
     *  Gets the gutter border attribute of the AbstractGutter object
     *
     *@return    The gutter border value
     */
    public String getGutterBorder() {
        return gutterBorder;
    }


    /**
     *  Gets the highlight color attribute of the AbstractGutter object
     *
     *@return    The highlight color value
     */
    public String getHighlightColor() {
        return highlightColor;
    }


    /**
     *  Gets the highlight interval attribute of the AbstractGutter object
     *
     *@return    The highlight interval value
     */
    public int getHighlightInterval() {
        return highlightInterval;
    }


    /**
     *  Gets the spacer attribute of the AbstractGutter object
     *
     *@return    The spacer value
     */
    public String getSpacer() {
        return spacer;
    }


    /**
     *  Gets the showing numbers attribute of the AbstractGutter object
     *
     *@return    The showing numbers value
     */
    public boolean isShowingNumbers() {
        return showingNumbers;
    }


    /**
     *  Gets the showing gutter attribute of the AbstractGutter object
     *
     *@return    The showing gutter value
     */
    public boolean isShowingGutter() {
        return showingGutter;
    }


    /**
     *  Makes a String representation of the object
     *
     *@return    A String representing the Object
     */
    public String toString() {
        return new StringBuffer(getClass().getName())
            .append("{")
            .append("bgColor:").append(bgColor)
            .append(";fgColor:").append(fgColor)
            .append(";highlightColor:").append(highlightColor)
            .append(";gutterBorder:").append(gutterBorder)
            .append(";spacer:").append(spacer)
            .append(";gutterFontSize:").append(gutterFontSize)
            .append(";gutterFontSize:").append(gutterFontSize)
            .append(";highlightInterval:").append(highlightInterval)
            .append(";showingNumbers:").append(showingNumbers)
            .append(";showingGutter:").append(showingGutter)
            .append("}").toString();
    }


    /**
     *  Gets the header attribute of the AbstractGutter object
     *
     *@return    The header value
     */
    public abstract String getHeader();


    /**
     *  Gets the line attribute of the AbstractGutter object
     *
     *@param  lineNumber  The line we are getting code for
     *@return             The line value
     */
    public abstract String getLine(int lineNumber);
}

