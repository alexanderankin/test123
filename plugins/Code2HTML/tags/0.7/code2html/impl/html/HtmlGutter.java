/*
 * HtmlGutter.java
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

import code2html.generic.* ;

public class HtmlGutter extends GenericGutter {
    
    protected HtmlGutter() {
        this("#ffffff", "#8080c0", "#000000", 5);
    }

    public HtmlGutter(
            String bgColor, String fgColor,
            String highlightColor, int highlightInterval
    ) {
        this(4, bgColor, fgColor, highlightColor, highlightInterval);
    }

    public HtmlGutter(
            int gutterSize,
            String bgColor, String fgColor,
            String highlightColor, int highlightInterval
    ) {
        super( gutterSize,
            bgColor, fgColor,
            highlightColor, highlightInterval ) ;
			
    }

		@Override
    public String format(int lineNumber) {
       return formatText( lineNumber, wrapText( lineNumber) ) ; 
    }

		@Override
    public String formatEmpty(int lineNumber){
        return formatText( lineNumber, spacer ) ;
    }

		@Override
    public String style() {
        return "";
    }

		private String formatText( int lineNumber, String text){
			
			StringBuffer buf = new StringBuffer();
      buf.append("<span style=\"background:")
					.append(bgColor)
					.append("; border-right:solid 2px black; margin-right:5px; \"><font color=\"") 
				  .append( getColorString( lineNumber ) )
          .append("\">")
					 .append(text)
          .append(" </font></span>");
      
      return buf.toString();
		}
		
		@Override
		public String getSpaceString( ){
			return " " ;
		}
}

