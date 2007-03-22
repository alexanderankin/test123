/*
 * Config.java
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

import code2html.html.HtmlGutter;
import code2html.html.HtmlPainter;
import code2html.html.HtmlStyle;
import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;


/**
 *  Configuration for the Code2HTML plugin
 *
 * @author     Andre Kaplan
 * @version    0.5
 * @todo       Extend this class to use all of the properties used in
 *      CommandLineConfig.Arguments and unify the two into this one
 */
public interface Config {
    /**
     *  Gets the gutter of the object
     *
     * @return    The gutter value
     */
    HtmlGutter getGutter();


    /**
     *  Gets the style of the object
     *
     * @return    The style value
     */
    HtmlStyle getStyle();


    /**
     *  Gets the tab expander of the object
     *
     * @return    The tab expander value
     */
    LineTabExpander getTabExpander();


    /**
     *  Gets the wrapper of the object
     *
     * @return    The wrapper value
     */
    LineWrapper getWrapper();


    /**
     *  Gets the painter of the object
     *
     * @return    The painter value
     */
    HtmlPainter getPainter();
}

