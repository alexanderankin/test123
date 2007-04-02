/*
 *  HtmlPainter.java
 *  Copyright (c) 2000, 2001, 2002 Andre Kaplan
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package code2html.html;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import code2html.SyntaxToken;

import code2html.line.LinePosition;
import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;


/**
 *  Managing class used to paint the buffer
 *
 *@author     Andre Kaplan
 *@version    0.5
 */
public class HtmlPainter {
    private LineTabExpander expander;
    private AbstractGutter gutter;
    private String nl = jEdit.getProperty("plugin.code2html.line.separator");
    private LinePosition position;
    private boolean showGutter;
    private AbstractStyle style;
    private SyntaxStyle[] syntaxStyles;
    private int wrap;
    private LineWrapper wrapper;


    /**
     *  HtmlPainter Constructor
     *
     *@param  syntaxStyles  A list of styles to be used
     *@param  style         The HTML style
     *@param  gutter        The gutter
     *@param  expander      The tab expander
     *@param  wrapper       The line wrapper
     */
    public HtmlPainter(SyntaxStyle[] syntaxStyles,
                       AbstractStyle style,
                       AbstractGutter gutter,
                       LineTabExpander expander,
                       LineWrapper wrapper) {
        this.syntaxStyles = syntaxStyles;
        this.style = style;
        this.gutter = gutter;
        this.expander = expander;
        this.wrapper = wrapper;
        this.position = new LinePosition();
        this.showGutter = (gutter != null);
        this.wrap = (wrapper == null) ? 0 : wrapper.getWrapSize();
    }


    /**
     *  Sets the pos of the object
     *
     *@param  pos  The new pos value
     */
    public void setPos(int pos) {
        this.position.setPos(pos);
    }


    /**
     *  Gets the syntax styles of the object
     *
     *@return    The syntax styles value
     */
    public SyntaxStyle[] getSyntaxStyles() {
        return this.syntaxStyles;
    }


    /**
     *  Paints a plain line (i.e. in HTML, no CSS)
     *
     *@param  out         The writer we are to paint to
     *@param  lineNumber  The number of the line
     *@param  line        The text of the line
     *@param  tokens      The tokens for the line
     */
    public void paintPlainLine(Writer out,
                               int lineNumber,
                               Segment line,
                               SyntaxToken tokens) {
        try {
            if (this.showGutter) {
                out.write(this.gutter.getLine(lineNumber));
            }

            int pos = this.position.getPos();
            String expandedText = this.expander.expand(
                pos, line.array, line.offset, line.count);
            this.position.incPos(expandedText.length());
            int[] wraps = null;

            if (this.wrapper != null) {
                wraps = this.wrapper.wrap(pos, expandedText.length());

                if (pos > 0 && (pos % this.wrap) == 0) {
                    out.write(nl);
                    if (this.showGutter) {
                        out.write(this.gutter.getLine(lineNumber));
                    }
                }
            }

            if (wraps == null) {
                out.write(HtmlUtilities.toHTML(expandedText));
            } else {
                for (int i = 0; i < wraps.length - 1; i++) {
                    if (i >= 1) {
                        out.write(nl);

                        if (this.showGutter) {
                            out.write(this.gutter.getLine(lineNumber));
                        }
                    }

                    out.write(HtmlUtilities.toHTML(
                        expandedText.substring(wraps[i], wraps[i + 1])));
                }
            }
        } catch (IOException ioe) {}
    }


    /**
     *  Paints a line with CSS hilighting
     *
     *@param  out         The writer we are to write to
     *@param  lineNumber  The number of the line being painted
     *@param  line        The text of the line
     *@param  tokens      The tokens representing the line
     *@todo               fix for(;;) loop
     */
    public void paintSyntaxLine(Writer out,
                                int lineNumber,
                                Segment line,
                                SyntaxToken tokens) {
        try {
            if (this.showGutter) {
                out.write(this.gutter.getLine(lineNumber));
            }
        } catch (IOException ioe) {}

        for (; ; ) {
            byte id = tokens.id;

            if (id == Token.END) {
                break;
            }

            int length = tokens.length;
            line.count = length;

            try {
                int pos = this.position.getPos();
                String expandedText = this.expander.expand(
                    pos, line.array, line.offset, length);
                this.position.incPos(expandedText.length());

                int[] wraps = null;

                if (this.wrapper != null) {
                    wraps = this.wrapper.wrap(pos, expandedText.length());

                    if (pos > 0 && (pos % this.wrap) == 0) {
                        out.write(nl);

                        if (this.showGutter) {
                            out.write(this.gutter.getLine(lineNumber));
                        }
                    }
                }

                if (wraps == null) {
                    String text = HtmlUtilities.toHTML(expandedText);

                    if (id == Token.NULL) {
                        out.write(text);
                    } else {
                        out.write(this.style.getToken(id, this.syntaxStyles[id], text));
                    }
                } else {
                    String text;

                    for (int i = 0; i < wraps.length - 1; i++) {
                        if (i >= 1) {
                            out.write(nl);

                            if (this.showGutter) {
                                out.write(this.gutter.getLine(lineNumber));
                            }
                        }

                        text = HtmlUtilities.toHTML(
                            expandedText.substring(wraps[i], wraps[i + 1]));

                        if (id == Token.NULL) {
                            out.write(text);
                        } else {
                            out.write(this.style.getToken(id, this.syntaxStyles[id], text));
                        }
                    }
                }
            } catch (IOException ioe) {}

            line.offset += length;
            tokens = tokens.next;
        }
    }
}

