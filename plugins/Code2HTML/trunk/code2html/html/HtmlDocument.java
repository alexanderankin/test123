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


public class HtmlDocument
{
    private HtmlStyle       style;
    private HtmlGutter      gutter;

    private String          title;
    private String          lineSeparator;


    public HtmlDocument(
            HtmlStyle  style,
            HtmlGutter gutter,
            String     title,
            String     lineSeparator
    ) {
        this.style         = style;
        this.gutter        = gutter;
        this.title         = title;
        this.lineSeparator = lineSeparator;
    }


    public void htmlOpen(Writer out)
        throws IOException
    {
        out.write("<HTML>");
        out.write(this.lineSeparator);
        out.write("<HEAD>");
        out.write(this.lineSeparator);
        out.write("<TITLE>" + this.title + "</TITLE>");
        out.write(this.lineSeparator);

        if (this.style instanceof HtmlCssStyle) {
            out.write("<STYLE TYPE=\"text/css\"><!--");
            out.write(this.lineSeparator);
            out.write(this.style.toCSS());
            out.write((this.gutter != null) ? this.gutter.toCSS() : "");
            out.write("-->");
            out.write(this.lineSeparator);
            out.write("</STYLE>");
            out.write(this.lineSeparator);
        }
        out.write("</HEAD>");
        out.write(this.lineSeparator);
        out.write("<BODY BGCOLOR=\"#FFFFFF\">");
        out.write(this.lineSeparator);
        out.write("<PRE>");
    }


    public void htmlClose(Writer out)
        throws IOException
    {
        out.write("</PRE>");
        out.write("</BODY>");
        out.write(this.lineSeparator);
        out.write("</HTML>");
        out.write(this.lineSeparator);
    }
}

