/*
 * LatexGutter.java
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

import code2html.generic.* ;

public class LatexGutter extends GenericGutter {
	
	protected static final String gutterBorder = "{|}\\ " ;
	protected String spacer ;
	
	public LatexGutter(
          String bgColor, String fgColor,
          String highlightColor, int highlightInterval
  ) {
      this(4, bgColor, fgColor, highlightColor, highlightInterval);
  }

	public LatexGutter(
        int gutterSize,
        String bgColor, String fgColor,
        String highlightColor, int highlightInterval
				){
		super( gutterSize, bgColor, fgColor,
			highlightColor, highlightInterval ) ;
	}
	
	
	@Override
	public String format(int lineNumber) {
		StringBuffer buf = new StringBuffer();
		
		String s = Integer.toString(lineNumber);
		buf.append("\\" + gutterStyle(lineNumber) + "{" ) ;
		for( int i=this.gutterSize - s.length(); i>=0; i--){
			buf.append( "\\ ") ; 
		}
		buf.append( s ) ;
		buf.append( this.gutterBorder + "}" ) ;
		return buf.toString();
  }
           
	@Override
	public String formatEmpty(int lineNumber) {
    return "\\" + gutterStyle(lineNumber) + "{" + spacer + gutterBorder + "}" ;
	}

	@Override
	public String style() {
    StringBuffer buf = new StringBuffer();
		buf.append( "\\newcommand{\\gutter}[1]{\\textcolor[rgb]{0,0,0}{{|}#1}}\n" )  ;
		buf.append( "\\newcommand{\\gutterH}[1]{\\textcolor[rgb]{1,0,0}{{|}#1}}\n" ) ;
		return buf.toString();
  }
		
	@Override
	public void setGutterSize( int gutterSize ){
		this.gutterSize = gutterSize ;
		StringBuffer buf = new StringBuffer() ;
		for( int i=0; i<gutterSize; i++){
			buf.append( "\\ " ) ;
		}
		spacer = buf.toString( ) ;
	}

	@Override
	public String getSpaceString(){
		return SPACESTRING ;
	}
	
	private static final String SPACESTRING = "{\\ }" ;
}

