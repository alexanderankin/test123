/*
 * Code2HTML.java
 * Copyright (c) 2000 Andre Kaplan
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

import java.io.*;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.syntax.TokenMarker;

import org.gjt.sp.jedit.textarea.*;

import org.gjt.sp.util.Log;

public class Code2HTML
    implements EBComponent
{
    private boolean useCSS;
    private boolean showGutter;

    public Code2HTML() {
        this.useCSS     = jEdit.getBooleanProperty("code2html.use-css", false);
        this.showGutter = jEdit.getBooleanProperty("code2html.show-gutter", false);
    }

    public void toHTML(View view, Buffer buffer) {
        toHTML(view, buffer, false);
    }

    public void toHTML(View view, Buffer buffer, boolean selection) {
        EditPane editPane = view.getEditPane();
        JEditTextArea textArea = editPane.getTextArea();

        int first = 0;
        int last  = textArea.getLineCount() - 1;

        if (selection && textArea.getSelectionStart() != textArea.getSelectionEnd()) {
            first = textArea.getSelectionStartLine();
            last  = textArea.getSelectionEndLine();
        }

        try {
            StringWriter   sw  = new StringWriter();
            BufferedWriter out = new BufferedWriter(sw);

            this.toHTML(out, buffer, textArea, first, last);
            out.flush();
            this.job = new BufferJob(editPane, sw.toString());
            out.close();
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
            this.job = null;
            return;
        }

        EditBus.addToBus(this);
        Buffer newBuffer = jEdit.newFile(view);
        if (newBuffer == null) {
            EditBus.removeFromBus(this);
            this.job = null;
        }
    }

    public void handleMessage(EBMessage message) {
        if (message instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate) message;
            if (bu.getWhat() == BufferUpdate.CREATED) {
                if (this.job != null) {
                    this.job.setBuffer(bu.getBuffer());
                }
            }
        } else if (message instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) message;
            if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED) {
                if (this.job != null) {
                    this.job.run();
                    this.job = null;
                }
                EditBus.removeFromBus(this);
            }
        }
    }

    private void toHTML(Writer out, Buffer buffer, JEditTextArea textArea,
            int first, int last)
    {
        TokenMarker tokenMarker = buffer.getTokenMarker();
        LineTabExpander expander = new LineTabExpander(buffer.getTabSize());
        SyntaxStyle[] styles = textArea.getPainter().getStyles();

        try {
            HTMLGutter gutter = null;

            if (this.showGutter) {
                int gutterSize = Integer.toString(last).length();
                gutter = new HTMLGutter(gutterSize);
            }

            out.write(
                  "<HTML>\n"
                + "<HEAD>\n"
                + "<TITLE>" + buffer.getName() + "</TITLE>\n"
            );
            if (this.useCSS) {
                out.write(
                      "<STYLE TYPE=\"text/css\"><!--\n"
                    + HTMLStyle.toCSS(styles)
                    + ((this.showGutter) ? gutter.toCSS() : "")
                    + "-->\n"
                    + "</STYLE>\n"
                );
            }
            out.write(
                  "</HEAD>\n"
                + "<BODY BGCOLOR=\"#FFFFFF\">\n"
            );
            out.write("<PRE>");

            for (int i = first; i <= last; i++) {
                if (this.showGutter) {
                    if (this.useCSS) {
                        out.write(gutter.toSpan(i + 1));
                    } else {
                        out.write(gutter.toHTML(i + 1));
                    }
                }

                expander.resetPos();
                Segment line = new Segment();
                textArea.getLineText(i, line);
                Token tokens = null;
                if (tokenMarker != null) {
                    tokens = tokenMarker.markTokens(buffer, i).firstToken;
                }
                paintLine(out, line, tokens, expander, styles);
                out.write("\n");
            }

            out.write("</PRE>");
            out.write(
                  "</BODY>\n"
                + "</HTML>\n"
            );
        } catch (IOException ioe) {}
    }

    private void paintLine(Writer out, Segment line, Token tokens,
            LineTabExpander expander, SyntaxStyle[] styles)
    {
        if (tokens == null) {
            try {
                out.write(
                    toHTML(expander.expand(line.array, line.offset, line.count))
                );
            } catch (IOException ioe) {}
        } else {
            paintSyntaxLine(out, line, tokens, expander, styles);
        }
    }

    private void paintSyntaxLine(Writer out, Segment line, Token tokens,
            LineTabExpander expander, SyntaxStyle[] styles)
    {
        for (;;) {
            byte id = tokens.id;
            if(id == Token.END) {
                break;
            }

            int length = tokens.length;
            line.count = length;

            try {
                String text =
                    toHTML(expander.expand(line.array, line.offset, length));
                if(id == Token.NULL) {
                    out.write(text);
                } else {
                    if (this.useCSS) {
                        out.write(HTMLStyle.toSpan(id, text));
                    } else {
                        out.write(HTMLStyle.toHTML(styles[id], text));
                    }
                }
            } catch (IOException ioe) {}

            line.offset += length;

            tokens = tokens.next;
        }
    }

    private String toHTML(String s) {
        return this.toHTML(s.toCharArray(), 0, s.length());
    }

    private String toHTML(char[] str, int strOff, int strLen) {
        StringBuffer buf = new StringBuffer();
        char c;
        int len = 0;
        int off = strOff;
        for (int i = 0; i < strLen; i++) {
            c = str[strOff + i];

            String entity = HTMLEntity.lookupEntity((short) c);
            if (entity != null) {
                // buf.append(str,off,len).append("&#").append((short)c).append(";");
                buf.append(str,off,len).append("&").append(entity).append(";");
                off += len + 1; len = 0;
            } else if (((short) c) > 255) {
                buf.append(str,off,len).append("&#").append((short)c).append(";");
                off += len + 1; len = 0;
            } else {
                len++;
            }
        }

        buf.append(str, off, len);
        return buf.toString();
    }

    private BufferJob job;

    private static class BufferJob
    {
        private Buffer   buffer;
        private EditPane editPane;
        private String   text;

        private BufferJob() {}

        public BufferJob(EditPane editPane, String text) {
            this.editPane = editPane;
            this.text     = text;
        }

        public void run() {
            if (this.buffer != this.editPane.getBuffer()) {
                Log.log(Log.DEBUG, this, "buffer != editPane.getBuffer()");
            } else {
                this.editPane.getTextArea().setText(this.text);
            }
        }

        public void setBuffer(Buffer buffer) {
            this.buffer = buffer;
        }
    }
}
