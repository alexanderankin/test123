/*
 * LatexDocument.java
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

import java.io.IOException;
import java.io.Writer;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import java.awt.Color ;

import code2html.generic.* ;

public class LatexDocument extends GenericDocument {
    
		public LatexDocument(
            String        viewBgColor,
            String        viewFgColor,
            SyntaxStyle[] syntaxStyles,
            Style  style,
            GenericGutter gutter,
            String        title,
            String        lineSeparator
    ) {
			super( viewBgColor, viewFgColor, syntaxStyles,
             style, gutter, title, lineSeparator ) ;
		}
    
    @Override
		public void openBeforeStyle(Writer out) throws IOException {                      
				
			  out.write( "\\documentclass{article}      " + this.lineSeparator ) ;
				out.write( "\\usepackage{color}           " + this.lineSeparator ) ;
				out.write( "\\usepackage{alltt}           " + this.lineSeparator ) ;
				out.write( "\\usepackage[latin1]{inputenc}" + this.lineSeparator ) ;
				out.write( "\\usepackage{hyperref}"         + this.lineSeparator ) ;
				out.write( "\\title{"+ this.title +"}"      + this.lineSeparator ) ;
				
		}		
			
		@Override
		public void openAfterStyle( Writer out ) throws IOException { 
			out.write( "\\begin{document}" + this.lineSeparator );
      out.write( "\\pagecolor{bgcolor}" + this.lineSeparator ) ;
		}
		
		@Override
		public void beforeContent( Writer out ) throws IOException {
				out.write( "\\noindent" + this.lineSeparator  ) ;
				out.write( "\\ttfamily" + this.lineSeparator  ) ;
				out.write("\\syntax" + LatexStyle.getCleanStyle( 0 ) +"{}");
    }

		@Override
		public void afterContent( Writer out ) throws IOException {
				out.write( "\\mbox{}"         + this.lineSeparator ) ; 
				out.write( "\\normalfont"     + this.lineSeparator ) ; 
		}
		
		@Override
    public void close(Writer out) throws IOException {
				out.write( "\\end{document}"  + this.lineSeparator ) ;
    }
		
		@Override
		public void openStyle( Writer out ) throws IOException {
			  super.openStyle( out ) ;
				out.write( "\\definecolor{bgcolor}{rgb}{" ) ; 
				out.write( ( bgColor.getRed() / 255. ) + "," ) ;
				out.write( ( bgColor.getGreen() / 255. ) + "," ) ;
				out.write( ( bgColor.getBlue() / 255. ) + "}" + this.lineSeparator ) ;
				
				out.write( "\\newsavebox{\\opencurlybracket}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\closecurlybracket}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\lessthan}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\greaterthan}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\dollarbox}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\underscorebox}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\andbox}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\hashbox}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\backslashbox}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\atbox}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\percentbox}%" + this.lineSeparator ) ;
				out.write( "\\newsavebox{\\hatbox}%" + this.lineSeparator ) ;
				
				out.write( "\\setbox\\opencurlybracket=\\hbox{\\verb.{.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\closecurlybracket=\\hbox{\\verb.}.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\lessthan=\\hbox{\\verb.<.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\dollarbox=\\hbox{\\verb.$.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\underscorebox=\\hbox{\\verb._.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\andbox=\\hbox{\\verb.&.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\hashbox=\\hbox{\\verb.#.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\atbox=\\hbox{\\verb.@.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\backslashbox=\\hbox{\\verb.\\.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\greaterthan=\\hbox{\\verb.>.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\percentbox=\\hbox{\\verb.\\%.}%" + this.lineSeparator ) ;
				out.write( "\\setbox\\hatbox=\\hbox{\\verb.^.}%" + this.lineSeparator ) ;
				out.write( "\\def\\urltilda{\\kern -.15em\\lower .7ex\\hbox{\\~{}}\\kern .04em}" + this.lineSeparator) ;				
		}
		
}

