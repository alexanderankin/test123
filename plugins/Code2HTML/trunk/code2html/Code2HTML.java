/*
 * Code2HTML.java
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
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import org.gjt.sp.util.Log;

import code2html.html.HtmlCssStyle;
import code2html.html.HtmlGutter;
import code2html.html.HtmlPainter;
import code2html.html.HtmlStyle;


public class Code2HTML
{
    private boolean useSelection;

    private JEditTextArea   textArea = null;
    private HtmlStyle       style    = null;
    private HtmlGutter      gutter   = null;
    private HtmlPainter     painter  = null;


    public Code2HTML(JEditTextArea textArea, boolean useSelection) {
        this.textArea     = textArea;
        this.useSelection = useSelection;

        Config config = new JEditConfig(
            textArea.getPainter().getStyles(),
            textArea.getBuffer().getTabSize()
        );
        
        this.style   = config.getStyle();
        this.gutter  = config.getGutter();
        this.painter = config.getPainter();
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

                // Sort selections by their start lines
                MiscUtilities.quicksort(selection, new SelectionStartLineCompare());

                if (this.gutter != null) {
                    this.gutter.setGutterSize(Integer.toString(last + 1).length());
                }

                int lastLine = -1;
                for (int i = 0; i < selection.length; i++) {
                    physicalFirst = selection[i].getStartLine();
                    physicalLast  = selection[i].getEndLine();

                    if (physicalLast <= lastLine) { continue; }

                    this.htmlText(out, Math.max(physicalFirst, lastLine + 1) , physicalLast);

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

        Code2HTML.setBufferText(newBuffer, sw.toString());
    }


    private void htmlOpen(Writer out) throws IOException
    {
        Buffer buffer = this.textArea.getBuffer();
        out.write(
              "<HTML>\n"
            + "<HEAD>\n"
            + "<TITLE>" + buffer.getName() + "</TITLE>\n"
        );
        if (this.style instanceof HtmlCssStyle) {
            out.write(
                  "<STYLE TYPE=\"text/css\"><!--\n"
                + this.style.toCSS()
                + ((this.gutter != null) ? this.gutter.toCSS() : "")
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
        this.paintLines(out, this.textArea, first, last);
        long end = System.currentTimeMillis();
        Log.log(Log.DEBUG, this, "Time: " + (end - start) + " ms");
    }


    private void paintLines(
            Writer out, JEditTextArea textArea, int first, int last
    ) throws IOException
    {
        Buffer buffer = textArea.getBuffer();

        Segment line = new Segment();
        Token tokens = null;
        for (int i = first; i <= last; i++) {
            textArea.getLineText(i, line);
            tokens = buffer.markTokens(i).getFirstToken();
            this.painter.setPos(0);
            if (tokens == null) {
                this.painter.paintPlainLine(out, i + 1, line, null);
            } else {
                SyntaxToken syntaxTokens = SyntaxTokenUtilities.convertTokens(
                    tokens
                );
                this.painter.paintSyntaxLine(out, i + 1, line, syntaxTokens);
            }
            out.write("\n");
        }
    }


    private void htmlClose(Writer out) throws IOException {
        out.write("</PRE>");
        out.write(
              "</BODY>\n"
            + "</HTML>\n"
        );
    }


    private static void setBufferText(Buffer buffer, String text) {
        buffer.beginCompoundEdit();
        buffer.remove(0, buffer.getLength());
        buffer.insert(0, text);
        buffer.endCompoundEdit();
    }


    private class SelectionStartLineCompare implements MiscUtilities.Compare
    {
        public int compare(Object obj1, Object obj2) {
            Selection s1 = (Selection) obj1;
            Selection s2 = (Selection) obj2;

            int diff = s1.getStartLine() - s2.getStartLine();

            if (diff == 0) {
                return 0;
            } else if (diff > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}

