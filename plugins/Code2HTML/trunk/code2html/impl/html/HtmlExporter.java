
/*
 * HtmlExporter.java
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
package code2html.impl.html;


import code2html.generic.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.textarea.Selection;


public class HtmlExporter extends GenericExporter {

    public HtmlExporter(
    Buffer buffer, SyntaxStyle[] syntaxStyle, Selection[] selection
    ) {
        super( buffer, syntaxStyle, selection );
        HtmlJeditConfig config = new HtmlJeditConfig(
        syntaxStyle,
        buffer.getTabSize()
        );

        setStyle( config.getStyle() );
        this.gutter = config.getGutter();
        this.painter = config.getPainter();
        this.document = new HtmlDocument(
        jEdit.getProperty( "view.bgColor", "#ffffff" ),
        jEdit.getProperty( "view.fgColor", "#000000" ),
        syntaxStyle,
        this.style,
        this.gutter,
        buffer.getName(),
        "\n"
        );
    }
    private static final String MODE = "html";


    @Override
    public String getMode() {
        return MODE;
    }
}

