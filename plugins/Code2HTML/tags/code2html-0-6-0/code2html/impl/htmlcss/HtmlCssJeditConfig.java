/*
 * HtmlCssJEditConfig.java
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


package code2html.impl.htmlcss ;

import org.gjt.sp.jedit.syntax.SyntaxStyle;

import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;

import code2html.generic.* ;

public class HtmlCssJeditConfig extends GenericJeditConfig {
    private HtmlCssStyle       style       = null;
    private HtmlCssGutter      gutter      = null;
    private HtmlCssPainter     painter     = null;
    
    public HtmlCssJeditConfig(SyntaxStyle[] styles, int tabSize) {
        super( styles, tabSize ) ;
				this.style = new HtmlCssStyle();
        
        if (showGutter) {
        		this.gutter = new HtmlCssGutter(
        		    bgColor, fgColor, highlightColor, highlightInterval
        		);
        }
        this.painter = new HtmlCssPainter(
            styles, this.style, this.gutter, this.tabExpander, this.wrapper
        );
    }

		@Override
    public GenericGutter getGutter() {
        return this.gutter;
    }

    @Override
    public Style getStyle() {
        return this.style;
    };

		@Override
    public GenericPainter getPainter() {
        return this.painter;
    }
}

