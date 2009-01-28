/*
 * LatexPainter.java
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

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import code2html.SyntaxToken;
import code2html.line.LinePosition;
import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;     

import code2html.generic.* ;

public class LatexPainter extends GenericPainter {
	
	private static final String NEWLINE = "\\hspace*{\\fill}\\\\\n" ;
	
	public LatexPainter(
            SyntaxStyle[]   syntaxStyles,
            Style    style,
            GenericGutter   gutter,
            LineTabExpander expander,
            LineWrapper     wrapper
    ) {
		super( syntaxStyles, style, gutter, expander, wrapper ) ;
	}
		
	
	@Override
  protected String newLine( ){
		return NEWLINE ;
	}
	
	@Override
	protected String format( String text){
		return LatexUtilities.format( text ) ;
	}

}

