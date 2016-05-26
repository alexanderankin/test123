
/*
 * GenericDocument.java
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
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import code2html.Code2HTMLPlugin;

import org.gjt.sp.jedit.syntax.SyntaxStyle;


public abstract class GenericDocument {

    protected String viewBgColor;
    protected String viewFgColor;
    protected Color bgColor;
    protected Color fgColor;
    protected SyntaxStyle[] syntaxStyles;
    protected Style style;
    protected GenericGutter gutter;
    protected String title;
    protected String lineSeparator;


    public GenericDocument() {
    }


    public GenericDocument(
    String viewBgColor,
    String viewFgColor,
    SyntaxStyle[] syntaxStyles,
    Style style,
    GenericGutter gutter,
    String title,
    String lineSeparator
    ) {
        this.viewBgColor = viewBgColor;
        this.viewFgColor = viewFgColor;
        this.bgColor = Code2HTMLPlugin.decode( viewBgColor );
        this.fgColor = Code2HTMLPlugin.decode( viewFgColor );

        this.syntaxStyles = Arrays.copyOf(syntaxStyles, syntaxStyles.length);
        this.style = style;
        this.gutter = gutter;
        this.title = title;
        this.lineSeparator = lineSeparator;
    }


    public void open( Writer out ) throws IOException {
        openBeforeStyle( out );
        openStyle( out );
        openAfterStyle( out );
    }


    public void openStyle( Writer out ) throws IOException {
        out.write( this.lineSeparator );
        for ( int i = 0; i < this.syntaxStyles.length; i++ ) {
            out.write( this.style.style( i, this.syntaxStyles[i]  ) );
        }
        out.write( this.lineSeparator );
        out.write( ( this.gutter != null ) ? this.gutter.style() : "" );
        out.write( this.lineSeparator );
    }
    
    public abstract void openBeforeStyle( Writer out ) throws IOException ;


    public abstract void openAfterStyle( Writer out ) throws IOException ;


    public abstract void beforeContent( Writer out ) throws IOException ;


    public abstract void afterContent( Writer out ) throws IOException ;


    public abstract void close( Writer out ) throws IOException ;
}
