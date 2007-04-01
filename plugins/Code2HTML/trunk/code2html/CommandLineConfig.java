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

import code2html.html.CSSGutter;
import code2html.html.HtmlCssStyle;
import code2html.html.AbstractGutter;
import code2html.html.HTMLGutter;
import code2html.html.HtmlPainter;
import code2html.html.HtmlStyle;

import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;


/**
 *  Command line configuration for the Code2HTML plugin
 *
 * @author     Andre Kaplan
 * @version    0.5
 */
public class CommandLineConfig implements Config {
    private AbstractGutter gutter = null;
    private HtmlPainter painter = null;
    private HtmlStyle style = null;
    private LineTabExpander tabExpander = null;
    private LineWrapper wrapper = null;


    /**
     *  CommandLineConfig Constructor
     *
     * @param  args  Holds the properties to be used in the generated code
     */
    public CommandLineConfig(Arguments args) {
        if (args.wrap < 0) {
            args.wrap = 0;
        }

        if (args.useCSS) {
            this.style = new HtmlCssStyle();
        } else {
            this.style = new HtmlStyle();
        }

        PropertyAccessor accessor = args.propertyAccessor;

        if (args.showGutter) {
            String bgColor = accessor.getProperty(
                "view.gutter.bgColor", "#ffffff");
            String fgColor = accessor.getProperty(
                "view.gutter.fgColor", "#8080c0");
            String highlightColor = accessor.getProperty(
                "view.gutter.highlightColor", "#000000");

            if (args.useCSS) {
                this.gutter = new CSSGutter(
                    bgColor,
                    fgColor,
                    highlightColor,
                    ":",
                    "    ",
                    "10",
                    args.highlightInterval,
                    true,
                    true);
            } else {
                this.gutter = new HTMLGutter(
                    bgColor,
                    fgColor,
                    highlightColor,
                    ":",
                    "    ",
                    "10",
                    args.highlightInterval,
                    true,
                    true);
            }

            // set in constructor
            //this.gutter.setGutterSize(args.gutterSize);
        }

        this.tabExpander = new LineTabExpander(args.tabSize);

        if (args.wrap > 0) {
            this.wrapper = new LineWrapper(args.wrap);
        }

        this.painter = new HtmlPainter(
            args.styles,
            this.style,
            this.gutter,
            this.tabExpander,
            this.wrapper);
    }


    /**
     *  Gets the gutter of the object
     *
     * @return    The gutter value
     */
    public AbstractGutter getGutter() {
        return this.gutter;
    }


    /**
     *  Gets the painter of the object
     *
     * @return    The painter value
     */
    public HtmlPainter getPainter() {
        return this.painter;
    }


    /**
     *  Gets the style of the object
     *
     * @return    The style value
     */
    public HtmlStyle getStyle() {
        return this.style;
    }


    /**
     *  Gets the tab expander of the object
     *
     * @return    The tab expander value
     */
    public LineTabExpander getTabExpander() {
        return this.tabExpander;
    }


    /**
     *  Gets the wrapper of the object
     *
     * @return    The wrapper value
     */
    public LineWrapper getWrapper() {
        return this.wrapper;
    }


    /**
     *  Holds properties relating to the output code
     *
     * @author     Andre Kaplan
     * @version    0.5
     * @todo       This should really be called something different, along the
     *      lines of properties or similar
     * @todo       Make this implement Config & pass config args to anything
     *      requesting one of this -> ? Or not ?
     */
    public static class Arguments {
        /**
         *  The size of the Gutter
         */
        public int gutterSize = 3;
        /**
         *  The interval at which to highlight the gutter number
         */
        public int highlightInterval = 5;
        /**
         *  A property accessor
         */
        public PropertyAccessor propertyAccessor = null;
        /**
         *  Whether to show the gutter
         */
        public boolean showGutter = true;
        /**
         *  A list of styles
         */
        public SyntaxStyle[] styles = null;
        /**
         *  The defauklt size of the tab character
         */
        public int tabSize = 8;
        /**
         *  Whether to use CSS in the generated code
         */
        public boolean useCSS = true;
        /**
         *  Wrap column
         */
        public int wrap = 0;
    }
}

