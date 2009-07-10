/*
 * LatexJeditConfig.java
 * Copyright (c) 2009 Romain Francois <francoisromain@free.fr>
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

package code2html.impl.latex ;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import code2html.generic.* ;

/**
 * Configuation specific to Latex, responsible for creating
 * style, gutter and painter 
 */
public class LatexJeditConfig extends GenericJeditConfig implements Config {
    private LatexStyle       style       = null;
    private LatexGutter      gutter      = null;
    private LatexPainter     painter     = null;
                 
		/**
		 * Creates the configuration from the array of styles and the 
		 * size of tabulation
		 * @param styles array of styles used by jEdit
		 * @param tabSize Size of one tabulation character
		 */
    public LatexJeditConfig(SyntaxStyle[] styles, int tabSize) {
        super( styles, tabSize ) ;
				
        this.style = new LatexStyle();
        
				if (showGutter) {
					this.gutter = new LatexGutter(
						bgColor, fgColor, highlightColor, highlightInterval
					);
				}
        
        this.painter = new LatexPainter(
            styles, this.style, this.gutter, this.tabExpander, this.wrapper
        );
    }

		/**
		 * Returns the gutter object
		 * @return gutter object specific to latex (or null if the gutter is not displayed)
		 */
		
    public GenericGutter getGutter() {
        return this.gutter;
    }

		/**
		 * Returns the style object
		 * @return The style associated with this configuration
		 */
		
    public Style getStyle() {
        return this.style;
    };

		/** 
		 * Returns the painter object
		 * @return The painter associated with this configuration
		 */ 
		
		public GenericPainter getPainter() {
        return this.painter;
    }
}

