/*
 * LatexUtilities.java
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

package code2html.impl.latex ;

public class LatexUtilities {
    
		public static String format(String s) {
        return LatexUtilities.format(s.toCharArray(), 0, s.length());
    }


    public static String format(char[] str, int strOff, int strLen) {
        StringBuffer buf = new StringBuffer();
        char c;
        int off = strOff;
        for (int i = 0; i < strLen; strOff++, i++) {
            c = str[strOff];
						
						if( c == ' ' ){
							// buf.append( "\\syntax").append( LatexStyle.getCleanStyle(0) ).append("{\\ }" ) ;
							buf.append("{\\ }" ) ;
						} else if( c == '<' ) {
							buf.append( "\\usebox{\\lessthan}" ) ; 
						} else if( c == '>' ) {
							buf.append( "\\usebox{\\greaterthan}" ) ;
						} else if( c == '-' ) {
							buf.append( "{-}" ) ;
						} else if( c == '~' ) {
							buf.append( "\\urltilda" ) ;
						} else if( c == '{' ){
							buf.append( "\\usebox{\\opencurlybracket}" ) ;
						} else if( c == '}' ){
							buf.append( "\\usebox{\\closecurlybracket}" ) ;
						} else if( c == '[') {
							buf.append( "{[}" ) ;
						} else if( c == ']' ) {
							buf.append( "{]}" ) ;
						} else if( c == '\\' ) {
							buf.append( "\\usebox{\\backslashbox}" ) ;
						} else if( c == '$' ) {
							buf.append( "\\usebox{\\dollarbox}" ) ;
						} else if( c == '_' ) {
							buf.append( "\\usebox{\\underscorebox}" ) ;
						} else if( c == '&' ) {
							buf.append( "\\usebox{\\andbox}" ) ;
						} else if( c == '#' ) {
							buf.append( "\\usebox{\\hashbox}" ) ;
						} else if( c == '@' ) {
							buf.append( "\\usebox{\\atbox}" ) ;
						} else if( c == '%' ) {
							buf.append( "\\usebox{\\percentbox}" ) ;
						} else if( c == '^' ) {
							buf.append( "\\usebox{\\hatbox}" ) ;
						} else { 
							buf.append( c ) ;
						}
        }

        return buf.toString();
    }
}

