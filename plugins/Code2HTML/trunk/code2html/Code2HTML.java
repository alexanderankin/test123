/*
 * Code2HTML.java
 * Copyright (c) 2000-2001 Andre Kaplan
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
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import org.gjt.sp.util.Log;


public class Code2HTML {
    private int     wrap;
    private boolean useCSS;
    private boolean showGutter;

    private boolean useSelection;
    private JEditTextArea   textArea = null;
    private HTMLStyle       style    = null;
    private HTMLGutter      gutter   = null;
    private HTMLPainter     painter  = null;


    public Code2HTML(JEditTextArea textArea, boolean useSelection) {
        this.textArea     = textArea;
        this.useSelection = useSelection;

        this.wrap = Code2HTMLUtilities.getIntegerProperty("code2html.wrap", 0);
        if (this.wrap < 0) { this.wrap = 0; }

        this.useCSS     = jEdit.getBooleanProperty("code2html.use-css", false);
        this.showGutter = jEdit.getBooleanProperty("code2html.show-gutter", false);

        SyntaxStyle[] styles = textArea.getPainter().getStyles();
        if (this.useCSS) {
            this.style = new HTMLCSSStyle(styles);
        } else {
            this.style = new HTMLStyle(styles);
        }

        if (this.showGutter) {
            int gutterSize = Integer.toString(this.textArea.getLineCount()).length();
            if (this.useCSS) {
                this.gutter = new HTMLCSSGutter();
            } else {
                this.gutter = new HTMLGutter();
            }
        }

        Buffer buffer = textArea.getBuffer();
        LineTabExpander expander = new LineTabExpander(buffer.getTabSize());

        LineWrapper wrapper  = null;
        if (this.wrap > 0) {
            wrapper = new LineWrapper(this.wrap);
        }

        this.painter = new HTMLPainter(this.style, this.gutter, expander, wrapper);
    }


    public void toHTML(View view) {
        int physicalFirst = 0;
        int physicalLast  = this.textArea.getLineCount() - 1;

        StringWriter sw  = new StringWriter();

        try {
            BufferedWriter out = new BufferedWriter(sw);

            this.htmlOpen(out);

            if (!this.useSelection) {
                this.htmlText(out, physicalFirst, physicalLast);
            } else {
                Selection[] selection = textArea.getSelection();

                int last = 0;
                for (int i = 0; i < selection.length; i++) {
                    if (selection[i].getEndLine() > last) {
                        last = selection[i].getEndLine();
                    }
                }

                this.gutter.setGutterSize(Integer.toString(last + 1).length());

                int lastLine = -1;
                for (int i = 0; i < selection.length; i++) {
                    physicalFirst = selection[i].getStartLine();
                    physicalLast  = selection[i].getEndLine();

                    if (physicalLast <= lastLine) { continue; }

                    this.htmlText(out, physicalFirst, physicalLast);

                    lastLine = physicalLast;
                }
            }

            this.htmlClose(out);

            out.flush();
            out.close();
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
            return;
        }

        Buffer newBuffer = jEdit.newFile(view);

        if (newBuffer == null) {
            new WaitForBuffer(sw.toString());
        } else {
            Code2HTML.setBufferText(newBuffer, sw.toString());
        }
    }


    private void htmlOpen(Writer out) throws IOException
    {
        Buffer buffer = this.textArea.getBuffer();
        out.write(
              "<HTML>\n"
            + "<HEAD>\n"
            + "<TITLE>" + buffer.getName() + "</TITLE>\n"
        );
        if (this.useCSS) {
            out.write(
                  "<STYLE TYPE=\"text/css\"><!--\n"
                + this.style.toCSS()
                + ((this.showGutter) ? this.gutter.toCSS() : "")
                + "-->\n"
                + "</STYLE>\n"
            );
        }
        out.write(
              "</HEAD>\n"
            + "<BODY BGCOLOR=\"#FFFFFF\">\n"
        );
        out.write("<PRE>");
    }


    private void htmlText(Writer out, int first, int last) throws IOException {
        long start = System.currentTimeMillis();
        this.painter.paintLines(out, this.textArea, first, last);
        long end = System.currentTimeMillis();
        Log.log(Log.DEBUG, this, "Time: " + (end - start) + " ms");
    }


    private void htmlClose(Writer out) throws IOException {
        out.write("</PRE>");
        out.write(
              "</BODY>\n"
            + "</HTML>\n"
        );
    }


    private static void setBufferText(Buffer buffer, String text) {
        try {
            buffer.beginCompoundEdit();
            buffer.remove(0, buffer.getLength());
            buffer.insertString(0, text, null);
        } catch (BadLocationException ble) {
            Log.log(Log.ERROR, Code2HTML.class, ble);
        } finally {
            buffer.endCompoundEdit();
        }
    }


    private static class WaitForBuffer implements EBComponent {
        private String text;


        public WaitForBuffer(String text) {
            this.text = text;

            EditBus.addToBus(this);
        }


        public void handleMessage(EBMessage message) {
            if (message instanceof BufferUpdate) {
                BufferUpdate bu = (BufferUpdate) message;
                if (bu.getWhat() == BufferUpdate.CREATED) {
                    EditBus.removeFromBus(this);

                    Buffer buffer = bu.getBuffer();
                    Log.log(Log.DEBUG, this, "**** Buffer CREATED new file? [" + buffer.isNewFile() + "]");
                    Log.log(Log.DEBUG, this, "**** Buffer CREATED length:   [" + buffer.getLength() + "]");
                    Code2HTML.setBufferText(buffer, this.text);
                }
            }
        }
    }
}
