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

/** 
 * Representation of the way the gutter looks on a latex generated document
 */
public class LatexGutter extends GenericGutter {
	
	protected static final String gutterBorder = "{|}\\ " ;
	protected String spacer ;
	
	/**
	 * Constructor for the latex specific gutter
	 * @param bgColor Background color of the gutter. Currently not used properly
	 * @param fgColor Color of the text of the gutter
	 * @param highlightColor Sets the text Colur when the current line is hilighted
	 * @param highlightInterval The interval at which lines of the gutter get
   *      hilighted in a different colour
	 */
	public LatexGutter(
          String bgColor, String fgColor,
          String highlightColor, int highlightInterval
  ) {
      this(4, bgColor, fgColor, highlightColor, highlightInterval);
  }

	/**
	 * Constructor for the latex specific gutter
	 * @param gutterSize size of the gutter (in number of characters)
	 * @param bgColor Background color of the gutter. Currently not used properly
	 * @param fgColor Color of the text of the gutter
	 * @param highlightColor Sets the text Colur when the current line is hilighted
	 * @param highlightInterval The interval at which lines of the gutter get
   *      hilighted in a different colour
	 */
	public LatexGutter(
        int gutterSize,
        String bgColor, String fgColor,
        String highlightColor, int highlightInterval
				){
		super( gutterSize, bgColor, fgColor,
			highlightColor, highlightInterval ) ;
	}
	
	/** 
	 * Returns a string containing the text of the gutter 
	 * for a given line number
	 * @param lineNumber current line number
	 * @return the latex markup for the gutter line
	 */ 
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
           
	/** 
	 * Returns the latex code for a gutter when the line number 
	 * should not be displayed (e.g. when wrapping)
	 * @param lineNumber current line number
	 * @return the latex markup for the empty gutter
	 */
	@Override
	public String formatEmpty(int lineNumber) {
    return "\\" + gutterStyle(lineNumber) + "{" + spacer + gutterBorder + "}" ;
	}

	/** 
	 * Returns the style definition of the gutter, defines the \\gutter and \\gutterH commands
	 * @return style definitions for the gutter
	 */
	@Override
	public String style() {
    StringBuffer buf = new StringBuffer();
		buf.append( "\\newcommand{\\gutter}[1]{\\textcolor[rgb]{0,0,0}{{|}#1}}\n" )  ;
		buf.append( "\\newcommand{\\gutterH}[1]{\\textcolor[rgb]{1,0,0}{{|}#1}}\n" ) ;
		return buf.toString();
  }
	
	/** 
   * Changes the size of the gutter 
	 * @param gutterSize the number of character the gutter should take 
	 */
	@Override
	public void setGutterSize( int gutterSize ){
		this.gutterSize = gutterSize ;
		StringBuffer buf = new StringBuffer() ;
		for( int i=0; i<gutterSize; i++){
			buf.append( "\\ " ) ;
		}
		spacer = buf.toString( ) ;
	}

	/**
	 * Returns the character used to represent a space character in the gutter
	 * @return Representation of a space character
	 */
	@Override
	public String getSpaceString(){
		return SPACESTRING ;
	}
	
	private static final String SPACESTRING = "{\\ }" ;
}

