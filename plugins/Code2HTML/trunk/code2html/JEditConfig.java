/*
 * JEditConfig.java
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


package code2html;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.SyntaxStyle;

import code2html.html.HtmlCssGutter;
import code2html.html.HtmlGutter;
import code2html.html.HtmlPainter;
import code2html.html.HtmlCssStyle;
import code2html.html.HtmlStyle;
import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;


public class JEditConfig implements Config
{
    private HtmlStyle       style       = null;
    private HtmlGutter      gutter      = null;
    private HtmlPainter     painter     = null;
    private LineTabExpander tabExpander = null;
    private LineWrapper     wrapper     = null;


    public JEditConfig(SyntaxStyle[] styles, int tabSize) {
        int wrap = jEdit.getIntegerProperty("code2html.wrap", 0);
        if (wrap < 0) { wrap = 0; }

        boolean useCSS     = jEdit.getBooleanProperty("code2html.use-css", false);
        boolean showGutter = jEdit.getBooleanProperty("code2html.show-gutter", false);

        if (useCSS) {
            this.style = new HtmlCssStyle();
        } else {
            this.style = new HtmlStyle();
        }

        if (showGutter) {
            String bgColor = jEdit.getProperty(
                "view.gutter.bgColor", "#ffffff"
            );
            String fgColor = jEdit.getProperty(
                "view.gutter.fgColor", "#8080c0"
            );
            String highlightColor = jEdit.getProperty(
                "view.gutter.highlightColor", "#000000"
            );
            int highlightInterval = jEdit.getIntegerProperty(
                "view.gutter.highlightInterval", 5
            );

            if (useCSS) {
                this.gutter = new HtmlCssGutter(
                    bgColor, fgColor, highlightColor, highlightInterval
                );
            } else {
                this.gutter = new HtmlGutter(
                    bgColor, fgColor, highlightColor, highlightInterval
                );
            }
        }

        this.tabExpander = new LineTabExpander(tabSize);

        if (wrap > 0) {
            this.wrapper = new LineWrapper(wrap);
        }

        this.painter = new HtmlPainter(
            styles, this.style, this.gutter, this.tabExpander, this.wrapper
        );
    }


    public HtmlGutter getGutter() {
        return this.gutter;
    }


    public HtmlStyle getStyle() {
        return this.style;
    };


    public LineTabExpander getTabExpander() {
        return this.tabExpander;
    }


    public LineWrapper getWrapper() {
        return this.wrapper;
    }


    public HtmlPainter getPainter() {
        return this.painter;
    }
}

