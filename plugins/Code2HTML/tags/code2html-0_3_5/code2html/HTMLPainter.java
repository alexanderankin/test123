/*
 * HTMLPainter.java
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


package code2html;


import java.io.IOException;
import java.io.Writer;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class HTMLPainter {
    private HTMLStyle       style;
    private HTMLGutter      gutter;
    private LineTabExpander expander;
    private LineWrapper     wrapper;

    private LinePosition    position;

    private boolean         showGutter;
    private int             wrap;


    public HTMLPainter(HTMLStyle style, HTMLGutter gutter,
                       LineTabExpander expander, LineWrapper wrapper
    ) {
        this.style    = style;
        this.gutter   = gutter;
        this.expander = expander;
        this.wrapper  = wrapper;

        this.position = new LinePosition();

        this.showGutter = (gutter != null);
        this.wrap       = (wrapper == null) ? 0 : wrapper.getWrapSize();
    }


    public void paintLines(Writer out, JEditTextArea textArea,
                           int first, int last
    )   throws IOException
    {
        Buffer buffer = textArea.getBuffer();

        Segment line = new Segment();
        Token tokens = null;
        for (int i = first; i <= last; i++) {
            textArea.getLineText(i, line);
            tokens = buffer.markTokens(i).getFirstToken();
            this.position.setPos(0);
            if (tokens == null) {
                this.paintPlainLine(out, i + 1, line, tokens);
            } else {
                this.paintSyntaxLine(out, i + 1, line, tokens);
            }
            out.write("\n");
        }
    }


    public void paintPlainLine(Writer out, int lineNumber, Segment line, Token tokens)
    {
        try {
            if (this.showGutter) {
                out.write(this.gutter.toHTML(lineNumber));
            }

            int pos = this.position.getPos();
            String expandedText = this.expander.expand(pos, line.array, line.offset, line.count);
            this.position.incPos(expandedText.length());

            int[] wraps = null;

            if (this.wrapper != null) {
                wraps = this.wrapper.wrap(pos, expandedText.length());

                if (pos > 0 && (pos % this.wrap) == 0) {
                    out.write("\n");
                    if (this.showGutter) {
                        out.write(this.gutter.toEmptyHTML(lineNumber));
                    }
                }
            }

            if (wraps == null) {
                out.write(Code2HTMLUtilities.toHTML(expandedText));
            } else {
                for (int i = 0; i < wraps.length - 1; i++) {
                    if (i >= 1) {
                        out.write("\n");
                        if (this.showGutter) {
                            out.write(this.gutter.toEmptyHTML(lineNumber));
                        }
                    }

                    out.write(Code2HTMLUtilities.toHTML(
                        expandedText.substring(wraps[i], wraps[i + 1])
                    ));
                }
            }
        } catch (IOException ioe) {}
    }


    private void paintSyntaxLine(
            Writer out, int lineNumber, Segment line, Token tokens
    ) {
        try {
            if (this.showGutter) {
                out.write(this.gutter.toHTML(lineNumber));
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
                        out.write("\n");
                        if (this.showGutter) {
                            out.write(this.gutter.toEmptyHTML(lineNumber));
                        }
                    }
                }

                if (wraps == null) {
                    String text = Code2HTMLUtilities.toHTML(expandedText);
                    if (id == Token.NULL) {
                        out.write(text);
                    } else {
                        out.write(this.style.toHTML(id, text));
                    }
                } else {
                    String text;
                    for (int i = 0; i < wraps.length - 1; i++) {
                        if (i >= 1) {
                            out.write("\n");
                            if (this.showGutter) {
                                out.write(this.gutter.toEmptyHTML(lineNumber));
                            }
                        }

                        text = Code2HTMLUtilities.toHTML(
                            expandedText.substring(wraps[i], wraps[i + 1])
                        );

                        if (id == Token.NULL) {
                            out.write(text);
                        } else {
                            out.write(this.style.toHTML(id, text));
                        }
                    }
                }
            } catch (IOException ioe) {}

            line.offset += length;

            tokens = tokens.next;
        }
    }
}

