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


import java.io.IOException;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.syntax.TokenMarker;

import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public class Code2HTML
    implements EBComponent
{
    private int     wrap;
    private boolean useCSS;
    private boolean showGutter;


    public Code2HTML() {
        this.wrap = Code2HTMLUtilities.getIntegerProperty("code2html.wrap", 0);
        if (this.wrap < 0) { this.wrap = 0; }

        this.useCSS     = jEdit.getBooleanProperty("code2html.use-css", false);
        this.showGutter = jEdit.getBooleanProperty("code2html.show-gutter", false);
    }


    public void toHTML(View view) {
        this.toHTML(view, false);
    }


    public void toHTML(View view, boolean selection) {
        EditPane      editPane = view.getEditPane();
        JEditTextArea textArea = view.getTextArea();

        int first = 0;
        int last  = textArea.getLineCount() - 1;

        if (selection && textArea.getSelectionStart() != textArea.getSelectionEnd()) {
            first = textArea.getSelectionStartLine();
            last  = textArea.getSelectionEndLine();
        }

        try {
            StringWriter   sw  = new StringWriter();
            BufferedWriter out = new BufferedWriter(sw);

            this.toHTML(out, textArea, first, last);
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


    private void toHTML(Writer out, JEditTextArea textArea,
                        int first, int last)
    {
        Buffer      buffer      = textArea.getBuffer();
        TokenMarker tokenMarker = textArea.getTokenMarker();

        SyntaxStyle[] styles = textArea.getPainter().getStyles();
        HTMLStyle htmlStyle = null;
        if (this.useCSS) {
            htmlStyle = new HTMLCSSStyle(styles);
        } else {
            htmlStyle = new HTMLStyle(styles);
        }

        HTMLGutter htmlGutter = null;
        if (this.showGutter) {
            int gutterSize = Integer.toString(last).length();
            if (this.useCSS) {
                htmlGutter = new HTMLCSSGutter(gutterSize);
            } else {
                htmlGutter = new HTMLGutter(gutterSize);
            }
        }

        LineTabExpander expander = new LineTabExpander(buffer.getTabSize());

        LineWrapper     wrapper  = null;
        if (this.wrap > 0) {
            wrapper = new LineWrapper(this.wrap);
        }

        HTMLPainter htmlPainter =
            new HTMLPainter(htmlStyle, htmlGutter, expander, wrapper);

        try {
            out.write(
                  "<HTML>\n"
                + "<HEAD>\n"
                + "<TITLE>" + buffer.getName() + "</TITLE>\n"
            );
            if (this.useCSS) {
                out.write(
                      "<STYLE TYPE=\"text/css\"><!--\n"
                    + HTMLCSSStyle.toCSS(styles)
                    + ((this.showGutter) ? htmlGutter.toCSS() : "")
                    + "-->\n"
                    + "</STYLE>\n"
                );
            }
            out.write(
                  "</HEAD>\n"
                + "<BODY BGCOLOR=\"#FFFFFF\">\n"
            );
            out.write("<PRE>");

            long start = System.currentTimeMillis();
            htmlPainter.paintLines(out, textArea, first, last);
            long end = System.currentTimeMillis();
            Log.log(Log.DEBUG, this, "Time: " + (end - start) + " ms");

            out.write("</PRE>");
            out.write(
                  "</BODY>\n"
                + "</HTML>\n"
            );
        } catch (IOException ioe) {}
    }


    public static String toHTML(String s) {
        return Code2HTML.toHTML(s.toCharArray(), 0, s.length());
    }


    public static String toHTML(char[] str, int strOff, int strLen) {
        StringBuffer buf = new StringBuffer();
        char c;
        int len = 0;
        int off = strOff;
        for (int i = 0; i < strLen; i++) {
            c = str[strOff + i];

            String entity = HTMLEntity.lookupEntity((short) c);
            if (entity != null) {
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
