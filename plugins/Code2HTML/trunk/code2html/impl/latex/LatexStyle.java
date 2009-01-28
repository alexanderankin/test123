/*
 * LatexStyle.java
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


package code2html.impl.latex;

import java.awt.Color;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.syntax.SyntaxStyle;

import org.gjt.sp.util.Log;
import java.util.HashMap ;
import code2html.generic.* ;
import code2html.services.LinkProvider ;

public class LatexStyle extends Style {
    
		@Override
    public String format(int styleId, SyntaxStyle style, String text) {
				StringBuffer buf = new StringBuffer();
				buf.append("\\syntax").append(getCleanStyle(styleId)).append("{").append(text).append("}") ;
        String formattedText = buf.toString( ) ;
				return getLinkText( styleId, text, formattedText ) ;
    }

		@Override
    public String style(int styleId, SyntaxStyle style) {
        StringBuffer buf = new StringBuffer();

        buf.append("\\newcommand{\\syntax" + getCleanStyle( styleId ) + "}[1]{");

        Color c;
				StringBuffer brackets = new StringBuffer() ;
				brackets.append("}");
				
				if ((c = style.getBackgroundColor()) != null) { 
					buf.append("\\colorbox[rgb]{") ;
					buf.append( (c.getRed()  / 255.)  + ","  ) ;  
					buf.append( (c.getGreen() / 255.) + "," ) ;  
					buf.append( (c.getBlue()  / 255.) + "}{" ) ;
					brackets.append("}") ;
				}
				
				buf.append("\\textcolor[rgb]{") ;
				if ((c = style.getForegroundColor()) != null) {
          buf.append( (c.getRed()  / 255.) + "," ) ;  
					buf.append( (c.getGreen() / 255.) + "," ) ;  
					buf.append( (c.getBlue()  / 255.) + "}{" ) ;  
				}
				brackets.append("}") ;
				
				if (style.getFont().isBold()) {
						buf.append("\\textbf{") ;
						brackets.append("}") ;
        }

        if (style.getFont().isItalic()) {
						buf.append( "{\\it " ) ;
						brackets.append("}") ;
        }
				
				buf.append("#1").append( brackets.toString() ).append("\n") ;
        return buf.toString();
    }
		
		private static HashMap<String,String> map = new HashMap<String,String>() ;
		
		public static String getCleanStyle( int id ){
			return getCleanStyle( getTokenString(id) ) ;
		}
		public static String getCleanStyle( String str ){
			if( !map.isEmpty() && map.containsKey( str ) ){
			} else {
				String str_ = cleanStyle( str ) ;
				map.put( str, str_ ) ;
			}
			return map.get( str ) ;
		}
		
		private static String cleanStyle( String str ){
			String out = str ;
			for( int i=1; i<5; i++){
				out = out.replaceAll( i+"", ""+(char)(64+i) ) ;
			}
			return out; 
		}
		
		@Override
		public String getMode(){
			return "latex" ;
		}
}

