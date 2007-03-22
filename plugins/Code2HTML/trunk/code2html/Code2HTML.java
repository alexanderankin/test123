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

import java.io.BufferedWriter;
import java.io.IOException;
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

import code2html.html.HtmlCssStyle;
import code2html.html.HtmlDocument;
import code2html.html.HtmlGutter;
import code2html.html.HtmlPainter;
import code2html.html.HtmlStyle;


/**
 *  Starting point for the Code2HTML plugin
 *
 * @author     Andre Kaplan
 * @version    0.5
 */
public class Code2HTML {
    private Buffer buffer = null;
    private HtmlDocument document = null;
    private HtmlGutter gutter = null;
    private HtmlPainter painter = null;
    private Selection[] selection = null;
    private HtmlStyle style = null;


    /**
     *  Code2HTML Constructor
     *
     * @param  buffer       The jEdit buffer we are converting
     * @param  syntaxStyle  Collection of syntax tags with which we are to
     *      convert the buffer
     * @param  selection    A selection from the buffer or null
     */
    public Code2HTML(Buffer buffer,
                     SyntaxStyle[] syntaxStyle,
                     Selection[] selection) {
        this.buffer = buffer;
        this.selection = selection;

        Config config = new JEditConfig(syntaxStyle, buffer.getTabSize());

        this.style = config.getStyle();
        this.gutter = config.getGutter();
        this.painter = config.getPainter();

        this.document = new HtmlDocument(
            jEdit.getProperty("view.bgColor", "#ffffff"),
            jEdit.getProperty("view.fgColor", "#000000"),
            syntaxStyle,
            this.style,
            this.gutter,
            buffer.getName(),
            System.getProperty("line.separator"));
    }


    /**
     *  Gets the html buffer of the object
     *
     * @return    The html buffer value
     */
    public Buffer getHtmlBuffer() {
        String htmlString = this.getHtmlString();

        if (htmlString == null) {
            return null;
        }

        Buffer newBuffer = jEdit.newFile(null);

        newBuffer.insert(0, htmlString);

        return newBuffer;
    }


    /**
     *  Gets the html string of the object
     *
     * @return    The html string value
     */
    public String getHtmlString() {
        int physicalFirst = 0;
        int physicalLast = this.buffer.getLineCount() - 1;

        StringWriter sw = new StringWriter();

        try {
            BufferedWriter out = new BufferedWriter(sw);

            this.document.htmlOpen(out);

            if (this.selection == null) {
                this.htmlText(out, physicalFirst, physicalLast);
            } else {
                int last = 0;

                for (int i = 0; i < selection.length; i++) {
                    if (selection[i].getEndLine() > last) {
                        last = selection[i].getEndLine();
                    }
                }

                // Sort selections by their start lines
                Arrays.sort(selection, new SelectionStartLineComparator());

                if (this.gutter != null) {
                    this.gutter.setGutterSize(
                        Integer.toString(last + 1).length());
                }

                int lastLine = -1;

                for (int i = 0; i < selection.length; i++) {
                    physicalFirst = selection[i].getStartLine();
                    physicalLast = selection[i].getEndLine();

                    if (physicalLast <= lastLine) {
                        continue;
                    }

                    this.htmlText(out, Math.max(physicalFirst, lastLine + 1),
                        physicalLast);

                    lastLine = physicalLast;
                }
            }

            this.document.htmlClose(out);

            out.flush();
            out.close();
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, ioe);
            return null;
        }

        return sw.toString();
    }


    /**
     *  Paints the actual HTML using the painter
     *
     * @param  out              The stream to paint HTML code to
     * @param  first            The first line of the buffer to paint
     * @param  last             The last line of the buffer to paint
     * @exception  IOException  In the extremely odd case that the writer can
     *      not be written to
     */
    private void htmlText(Writer out, int first, int last) throws IOException {
        Segment line = new Segment();
        Token tokens = null;

        for (int i = first; i <= last; i++) {
            buffer.getLineText(i, line);
            tokens = buffer.markTokens(i).getFirstToken();
            this.painter.setPos(0);

            if (tokens == null) {
                this.painter.paintPlainLine(out, i + 1, line, null);
            } else {
                SyntaxToken syntaxTokens = SyntaxTokenUtilities.convertTokens(
                    tokens);
                this.painter.paintSyntaxLine(out, i + 1, line, syntaxTokens);
            }

            out.write(this.document.getLineSeparator());
        }
    }


    /**
     *  comparator for Selection start lines - to be passed to Arrays.sort
     *
     * @author     Andre kaplan
     * @version    0.5
     */
    private class SelectionStartLineComparator implements Comparator {
        /**
         *  Implements the compare method of java.util.Comparator
         *
         * @param  obj1  The first object to compare
         * @param  obj2  The second object to compare
         * @return       1, 0, or -1 when obj1 &gt;, = or &lt; obj2 respectively
         */
        public int compare(Object obj1, Object obj2) {
            Selection s1 = (Selection) obj1;
            Selection s2 = (Selection) obj2;

            int diff = s1.getStartLine() - s2.getStartLine();

            return diff == 0 ? 0 : (
                diff > 0 ? 1 : -1);
        }
    }
}

