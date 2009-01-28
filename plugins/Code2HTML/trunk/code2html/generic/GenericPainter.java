/*
 * GenericPainter.java
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


package code2html.generic ;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import code2html.line.LinePosition;
import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;

import code2html.services.LinkProvider ;

public abstract class GenericPainter {
	
    protected Style       style;
    protected GenericGutter      gutter;
    
		protected LineTabExpander expander;
    protected LineWrapper     wrapper;

    protected SyntaxStyle[]   syntaxStyles;
	  protected LinePosition    position;

    protected boolean         showGutter;
    protected int             wrap;

		public GenericPainter( ){ }
		
    public GenericPainter(
            SyntaxStyle[]   syntaxStyles,
            Style    style,
            GenericGutter   gutter,
            LineTabExpander expander,
            LineWrapper     wrapper
    ) {
				this.syntaxStyles = syntaxStyles ;
				this.position = new LinePosition( ) ;
				
        this.style    = style;
        this.gutter   = gutter;
        this.expander = expander;
        this.wrapper  = wrapper;
				
        this.showGutter = (gutter != null);
        this.wrap       = (wrapper == null) ? 0 : wrapper.getWrapSize();
		}

		
		public SyntaxStyle[] getSyntaxStyles() {
        return this.syntaxStyles;
    }

    public void setPos(int pos) {
        this.position.setPos(pos);
    }


		public void paintPlainLine(Writer out, int lineNumber, Segment line, Token tokens)
    {
        try {
            if (this.showGutter) {
                out.write(this.gutter.format(lineNumber));
            }

            int pos = this.position.getPos();
            String expandedText = this.expander.expand(pos, line.array, line.offset, line.count);
            this.position.incPos(expandedText.length());

            int[] wraps = null;

            if (this.wrapper != null) {
                wraps = this.wrapper.wrap(pos, expandedText.length());

                if (pos > 0 && (pos % this.wrap) == 0) {
                    out.write( newLine() );
                    if (this.showGutter) {
                        out.write(this.gutter.formatEmpty(lineNumber));
                    }
                }
            }

            if (wraps == null) {
                out.write( format(expandedText) );
            } else {
                for (int i = 0; i < wraps.length - 1; i++) {
                    if (i >= 1) {
                        out.write("\n");
                        if (this.showGutter) {
                            out.write(this.gutter.formatEmpty(lineNumber));
                        }
                    }

                    out.write( format(
                        expandedText.substring(wraps[i], wraps[i + 1])
                    ));
                }
            }
        } catch (IOException ioe) {}
    }

    public void paintSyntaxLine(
            Writer out, int lineNumber, Segment line, Token tokens
    ) {
        
				try {
            if (this.showGutter) {
                out.write(this.gutter.format(lineNumber));
            }
        } catch (IOException ioe) {}

        for (;;) {
            byte id = tokens.id;
            if (id == Token.END) {
                break;
            }

            int length = tokens.length;
            line.count = length;

            try {
                int pos = this.position.getPos();
                String expandedText = this.expander.expand(pos, line.array, line.offset, length);
                this.position.incPos(expandedText.length());

                int[] wraps = null;

                if (this.wrapper != null) {
                    wraps = this.wrapper.wrap(pos, expandedText.length());

                    if (pos > 0 && (pos % this.wrap) == 0) {
                        out.write( newLine() );
                        if (this.showGutter) {
                            out.write(this.gutter.formatEmpty(lineNumber));
                        }
                    }
                }

                if (wraps == null) {
                    String text = format(expandedText);
                    if (id == Token.NULL) {
                        out.write(text);
                    } else {
                        out.write(this.style.format(id, this.syntaxStyles[id], text));
                    }
                } else {
                    String text;
                    for (int i = 0; i < wraps.length - 1; i++) {
                        if (i >= 1) {
                            out.write( newLine( ) );
                            if (this.showGutter) {
                                out.write(this.gutter.formatEmpty(lineNumber));
                            }
                        }

                        text = format(
                            expandedText.substring(wraps[i], wraps[i + 1])
                        );

                        if (id == Token.NULL) {
                            out.write(text);
                        } else {
                            out.write(this.style.format(id, this.syntaxStyles[id], text));
                        }
                    }
                }
            } catch (IOException ioe) {}

            line.offset += length;

            tokens = tokens.next;
        }
    }
		
		protected abstract String newLine( ) ;
		
		protected abstract String format( String text) ;
		
}

