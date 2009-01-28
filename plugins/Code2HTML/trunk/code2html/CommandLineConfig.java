/*
 * CommandLineConfig.java
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

import org.gjt.sp.jedit.syntax.SyntaxStyle;

import code2html.impl.htmlcss.HtmlCssGutter;
import code2html.impl.html.HtmlGutter;
import code2html.impl.html.HtmlPainter;
import code2html.impl.htmlcss.HtmlCssStyle;
import code2html.impl.html.HtmlStyle;
import code2html.generic.* ;

import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;

import code2html.generic.Config ;

public class CommandLineConfig implements Config
{
    public static class Arguments
    {
        public int     gutterSize        = 3;
        public int     tabSize           = 8;
        public int     wrap              = 0;
        public int     highlightInterval = 5;

        public boolean useCSS     = true;
        public boolean showGutter = true;

        public SyntaxStyle[]     styles           = null;
        public PropertyAccessor  propertyAccessor = null;
    }


    private Style       style       = null;
    private GenericGutter      gutter      = null;
    private GenericPainter     painter     = null;
    private LineTabExpander tabExpander = null;
    private LineWrapper     wrapper     = null;


    public CommandLineConfig(Arguments args) {
        if (args.wrap < 0) { args.wrap = 0; }

        if (args.useCSS) {
            this.style = new HtmlCssStyle();
        } else {
            this.style = new HtmlStyle();
        }

        PropertyAccessor accessor = args.propertyAccessor;
        if (args.showGutter) {
            String bgColor = accessor.getProperty(
                "view.gutter.bgColor", "#ffffff"
            );
            String fgColor = accessor.getProperty(
                "view.gutter.fgColor", "#8080c0"
            );
            String highlightColor = accessor.getProperty(
                "view.gutter.highlightColor", "#000000"
            );

            if (args.useCSS) {
                this.gutter = new HtmlCssGutter(
                    bgColor, fgColor, highlightColor, args.highlightInterval
                );
            } else {
                this.gutter = new HtmlGutter(
                    bgColor, fgColor, highlightColor, args.highlightInterval
                );
            }
            this.gutter.setGutterSize(args.gutterSize);
        }

        this.tabExpander = new LineTabExpander(args.tabSize);

        if (args.wrap > 0) {
            this.wrapper = new LineWrapper(args.wrap);
        }

        this.painter = new HtmlPainter(
            args.styles, this.style, this.gutter, this.tabExpander, this.wrapper
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
    public LineTabExpander getTabExpander() {
        return this.tabExpander;
    }

		@Override
    public LineWrapper getWrapper() {
        return this.wrapper;
    }

		@Override
    public GenericPainter getPainter() {
        return this.painter;
    }
}

