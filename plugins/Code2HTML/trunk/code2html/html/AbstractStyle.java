/*
 *  AbstractStyle.java
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

import org.gjt.sp.jedit.syntax.SyntaxStyle;

/**
 *  Abstract definition of a style, subclassed into HTML and CSS versions
 *
 *@author     dsm
 *@version    0.1
 */
public abstract class AbstractStyle {
    /**
     *  Constructor for the AbstractStyle
     */
    public AbstractStyle() { }


    /**
     *  Gets the header attribute of the AbstractStyle object
     *
     *@param  styleId  The ID of the Style we are printing. Jedit will make
     *      sense of this
     *@param  style    The actual style we are using
     *@return          The header value
     */
    public abstract String getHeader(int styleId,
                                     SyntaxStyle style);


    /**
     *  Gets the token attribute of the AbstractStyle object
     *
     *@param  styleId  The ID of the Style we are printing. Jedit will make
     *      sense of this
     *@param  style    The actual style we are using
     *@param  text     The text that will be tagged by this style
     *@return          The token value
     */
    public abstract String getToken(int styleId,
                                    SyntaxStyle style,
                                    String text);


    /**
     *  Gets a String representation of the class
     *
     *@return    A String representing the class
     */
    public String toString() {
        return getClass().getName();
    }
}

