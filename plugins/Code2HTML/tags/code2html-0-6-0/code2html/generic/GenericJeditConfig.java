/*
* GenericJEditConfig.java
* Copyright (c) 2009 Romain Francois
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


package code2html.generic ;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.SyntaxStyle;

import code2html.line.LineTabExpander;
import code2html.line.LineWrapper;

public abstract class GenericJeditConfig implements Config {
    protected LineTabExpander tabExpander = null;
    protected LineWrapper wrapper = null;

    protected boolean showGutter ;
    protected String highlightColor ;
    protected int highlightInterval ;
    protected String bgColor ;
    protected String fgColor ;

    public GenericJeditConfig( SyntaxStyle[] styles, int tabSize ) {
        int wrap = jEdit.getIntegerProperty( "code2html.wrap", 0 );
        if ( wrap < 0 ) {
            wrap = 0;
        }

        showGutter = jEdit.getBooleanProperty( "code2html.show-gutter", false );

        if ( showGutter ) {
            bgColor = jEdit.getProperty(
                        "view.gutter.bgColor", "#ffffff"
                    );
            fgColor = jEdit.getProperty(
                        "view.gutter.fgColor", "#8080c0"
                    );
            highlightColor = jEdit.getProperty(
                        "view.gutter.highlightColor", "#000000"
                    );
            highlightInterval = jEdit.getIntegerProperty(
                        "view.gutter.highlightInterval", 5
                    );
        }

        this.tabExpander = new LineTabExpander( tabSize );

        if ( wrap > 0 ) {
            this.wrapper = new LineWrapper( wrap );
        }

    }


    public LineTabExpander getTabExpander() {
        return this.tabExpander;
    }


    public LineWrapper getWrapper() {
        return this.wrapper;
    }

}