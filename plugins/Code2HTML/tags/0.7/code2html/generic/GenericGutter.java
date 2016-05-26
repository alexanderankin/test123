
/*
 * GenericGutter.java
 * Copyright (c) 2009 Romain Francois <francoisromain@free.fr>
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
package code2html.generic;


import java.awt.Color;
import code2html.Code2HTMLPlugin;


public abstract class GenericGutter {

    protected int gutterSize;
    protected char gutterBorder = ':';
    protected int gutterBorderSize = 1;
    protected String bgColor;
    protected String fgColor;
    protected Color bg;
    protected Color fg;
    protected String highlightColor;
    protected int highlightInterval;
    protected String spacer;


    protected GenericGutter() {
        this( "#ffffff", "#000000", "#8080c0", 5 );
    }


    public GenericGutter(
    String bgColor, String fgColor,
    String highlightColor, int highlightInterval
    ) {
        this( 4, bgColor, fgColor, highlightColor, highlightInterval );
    }


    public GenericGutter(
    int gutterSize,
    String bgColor, String fgColor,
    String highlightColor, int highlightInterval
    ) {
        setGutterSize( gutterSize );

        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.bg = Code2HTMLPlugin.decode( bgColor );
        this.fg = Code2HTMLPlugin.decode( fgColor );

        this.highlightColor = highlightColor;
        this.highlightInterval = highlightInterval;
    }


    public boolean isHighlighted( int lineNumber ) {
        return ( this.highlightInterval > 0 ) && ( lineNumber % this.highlightInterval == 0 );
    }


    public String gutterStyle( int lineNumber ) {
        return isHighlighted( lineNumber ) ? "gutterH" : "gutter";
    }


    public String getColorString( int lineNumber ) {
        return isHighlighted( lineNumber ) ? highlightColor : fgColor;
    }


    public int getGutterBorderSize() {
        return gutterBorderSize;
    }


    public int getGutterSize() {
        return gutterSize;
    }


    public int getSize() {
        return getGutterSize() + getGutterBorderSize();
    }


    public abstract String format( int lineNumber ) ;


    public abstract String formatEmpty( int lineNumber ) ;


    public abstract String style() ;


    public void setGutterSize( int gutterSize ) {
        this.gutterSize = gutterSize;
        this.spacer = getSpacer();
    }


    public abstract String getSpaceString() ;


    public String getSpacer( int size ) {
        String space = getSpaceString();

        StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < size; i++ ) {
            buf.append( space );
        }
        return buf.toString();
    }


    public String getSpacer() {
        return getSpacer( gutterSize );
    }


    public String wrapText( int lineNumber ) {
        StringBuffer buf = new StringBuffer();
        String s = Integer.toString( lineNumber );
        String spaces = getSpacer( getGutterSize() - s.length() );
        buf.append( spaces ).append( s );
        return buf.toString();
    }
}
