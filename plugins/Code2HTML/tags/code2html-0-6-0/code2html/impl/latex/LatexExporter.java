/*
 * LatexExporter.java
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
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import code2html.generic.* ;

/**
 * Main class to convert the textarea into latex format
 */
public class LatexExporter extends GenericExporter {
	
		/**
		 * Constructor for the latex exporter
		 * @param buffer buffer we want to convert into latex code
		 * @param syntaxStyle list of styles used by jedit
		 * @param selection selection, if this is not null the output document will only contain the selected text
		 */
    public LatexExporter(
            Buffer buffer, SyntaxStyle[] syntaxStyle, Selection[] selection
    ) {
				super( buffer, syntaxStyle, selection ) ;
				
        LatexJeditConfig config = new LatexJeditConfig(
            syntaxStyle,
            buffer.getTabSize()
        );

        setStyle( config.getStyle() );
        this.gutter   = config.getGutter();
        this.painter  = config.getPainter();

        this.document = new LatexDocument(
            jEdit.getProperty("view.bgColor", "#ffffff"),
            jEdit.getProperty("view.fgColor", "#000000"),
            syntaxStyle,
            this.style,
            this.gutter,
            buffer.getName(),
            "\n"
        );
    }
		
		private static final String MODE = "latex" ;
		
		/** 
		 * Returns the mode the output document should be using (latex)
		 * @see org.gjt.sp.jedit.Mode#toString
		 */
		@Override 
		public String getMode(){
			return "latex" ;
		}
		
}

