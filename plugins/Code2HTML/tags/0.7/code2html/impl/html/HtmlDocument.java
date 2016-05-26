
/*
 * HtmlDocument.java
 * Copyright (c) 2002 Andre Kaplan
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
package code2html.impl.html;


import code2html.generic.*;

import java.io.IOException;
import java.io.Writer;

import org.gjt.sp.jedit.syntax.SyntaxStyle;


public class HtmlDocument extends GenericDocument {

    public HtmlDocument(
    String viewBgColor,
    String viewFgColor,
    SyntaxStyle[] syntaxStyles,
    Style style,
    GenericGutter gutter,
    String title,
    String lineSeparator
    ) {
        super( viewBgColor, viewFgColor, syntaxStyles,
        style, gutter, title, lineSeparator );
        if (viewBgColor.length() > 7)
        {
            this.viewBgColor = "#" + viewBgColor.substring(3);  
        }
        if (viewFgColor.length() > 7)
        {
            this.viewFgColor = "#" + viewFgColor.substring(3);
        }
    }


    @Override
    public void openBeforeStyle( Writer out ) throws IOException {
        out.write( "<html>" );
        out.write( this.lineSeparator );
        out.write( "<head>" );
        out.write( this.lineSeparator );
        out.write( "<title>" + this.title + "</title>" );
        out.write( this.lineSeparator );
        out.write( "</head>" );
        out.write( this.lineSeparator );
        out.write( "<body bgcolor=\"" );
        out.write( this.viewBgColor );
        out.write( "\">" );
        out.write( this.lineSeparator );
    }


    @Override
    public void openAfterStyle( Writer out ) throws IOException {
    }


    @Override
    public void beforeContent( Writer out ) throws IOException {
        out.write( "<pre>" );
        out.write( "<font color=\"" );
        out.write( this.viewFgColor );
        out.write( "\">" );
    }


    @Override
    public void afterContent( Writer out ) throws IOException {
        out.write( "</font>" );
        out.write( "</pre>" );
        out.write( this.lineSeparator );
    }


    @Override
    public void close( Writer out ) throws IOException {
        out.write( "</body>" );
        out.write( this.lineSeparator );
        out.write( "</html>" );
        out.write( this.lineSeparator );
    }
}
