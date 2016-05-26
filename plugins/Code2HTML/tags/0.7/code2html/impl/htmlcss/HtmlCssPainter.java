/*
 * HtmlCssPainter.java
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


package code2html.impl.htmlcss ;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import code2html.line.LinePosition;
import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;

import code2html.generic.* ;
import code2html.impl.html.HtmlUtilities ;

public class HtmlCssPainter extends GenericPainter {
	
    public HtmlCssPainter(
            SyntaxStyle[]   syntaxStyles,
            Style       style,
            GenericGutter      gutter,
            LineTabExpander expander,
            LineWrapper     wrapper
    ) {
				super( syntaxStyles, style, gutter, 
					expander, wrapper) ;
    }

		@Override
		public String newLine( ){
			return "\n" ;
		}
		
		@Override 
		public String format( String text ){
			return HtmlUtilities.format( text ) ; 
		}
}

