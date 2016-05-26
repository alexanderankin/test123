
/*
 * HtmlStyle.java
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
package code2html.impl.html;

import code2html.Code2HTMLPlugin;
import code2html.generic.Style;

import java.awt.Color;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.SyntaxUtilities;


public class HtmlStyle extends Style {

    @Override
    public String format( int styleId, SyntaxStyle style, String text ) {
        if ( style == null ) {

            // Log.log( Log.DEBUG, this, "format: null style" );
            return text;
        }

        StringBuffer bufOpen = new StringBuffer();
        StringBuffer bufClose = new StringBuffer();

        Color c;
        if ( ( c = style.getForegroundColor() ) != null ) {
            bufOpen.append( "<font color=\"" )
            .append( Code2HTMLPlugin.encode( c ) )
            .append( "\">" );
            bufClose.insert( 0, "</font>" );
        }


        if ( style.getFont().isBold() ) {
            bufOpen.append( "<strong>" );
            bufClose.insert( 0, "</strong>" );
        }


        if ( style.getFont().isItalic() ) {
            bufOpen.append( "<em>" );
            bufClose.insert( 0, "</em>" );
        }


        StringBuffer buf = new StringBuffer();
        buf.append( bufOpen ).append( text ).append( bufClose );
        String formattedText = buf.toString();

        return getLinkText( styleId, text, formattedText );
    }


    @Override
    public String style( int styleId, SyntaxStyle style ) {
        return "";
    }


    @Override
    public String getMode() {
        return null;
    }
}

