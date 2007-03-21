/*
 * HtmlDocument.java
 * Copyright (c) 2002 Andre Kaplan
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


package code2html.html;

import java.io.IOException;
import java.io.Writer;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.jEdit;


public class HtmlDocument
{
    private String        viewBgColor;
    private String        viewFgColor;

    private SyntaxStyle[] syntaxStyles;

    private HtmlStyle     style;
    private HtmlGutter    gutter;

    private String        title;
    private String        lineSeparator;


    public HtmlDocument(
            String        viewBgColor,
            String        viewFgColor,
            SyntaxStyle[] syntaxStyles,
            HtmlStyle     style,
            HtmlGutter    gutter,
            String        title,
            String        lineSeparator
    ) {
        this.viewBgColor   = viewBgColor;
        this.viewFgColor   = viewFgColor;
        this.syntaxStyles  = syntaxStyles;
        this.style         = style;
        this.gutter        = gutter;
        this.title         = title;
        this.lineSeparator = lineSeparator;
    }


    public void htmlOpen(Writer out)
        throws IOException
    {
        out.write("<html>");
        out.write(this.lineSeparator);
        out.write("<head>");
        out.write(this.lineSeparator);
        out.write("<title>" + this.title + "</title>");
        out.write(this.lineSeparator);
        if (this.style instanceof HtmlCssStyle) {
            out.write("<style type=\"text/css\"><!--");
            out.write(this.lineSeparator);

            for (int i = 0; i < this.syntaxStyles.length; i++) {
                out.write(this.style.toCSS(i, this.syntaxStyles[i]));
            }

            out.write((this.gutter != null) ? this.gutter.toCSS() : "");
            out.write(jEdit.getProperty("options.code2html.body.style.open"));
            out.write(this.lineSeparator);
            out.write(jEdit.getProperty("options.code2html.body.style.value"));
            out.write(this.lineSeparator);
            out.write(jEdit.getProperty("options.code2html.body.style.close"));
            out.write(this.lineSeparator);
            out.write(jEdit.getProperty("options.code2html.pre.style.open"));
            out.write(this.lineSeparator);
            out.write(jEdit.getProperty("options.code2html.pre.style.value"));
            out.write(this.lineSeparator);
            out.write(jEdit.getProperty("options.code2html.pre.style.close"));
            out.write(this.lineSeparator);
            out.write("-->");
            out.write("</style>");
            out.write(this.lineSeparator);
        }
        out.write("</head>");
        out.write(this.lineSeparator);
        
        if (style instanceof HtmlCssStyle) {
            out.write("<body>");
        out.write(this.lineSeparator);
        out.write("<pre>");
            out.write("<span class=\"syntax0\">");
        } else {
            out.write("<body ");
            out.write(jEdit.getProperty("options.code2html.body.html.value"));
            out.write(" >");
            out.write(this.lineSeparator);
            out.write("<pre ");
            out.write(jEdit.getProperty("options.code2html.pre.html.value"));
            out.write(" >");
            out.write("<font color=\"");
            out.write(this.viewFgColor);
            out.write("\">");
        }
    }


    public void htmlClose(Writer out)
        throws IOException
    {
        if (style instanceof HtmlCssStyle) {
            out.write("</span>");
        } else {
            out.write("</font>");
        }
        out.write("</pre>");
        out.write(this.lineSeparator);
        out.write("</body>");
        out.write(this.lineSeparator);
        out.write("</html>");
        out.write(this.lineSeparator);
    }
}

