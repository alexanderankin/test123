/*
 * Style.java
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


package code2html.generic ;

import java.awt.Color;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;

import code2html.services.LinkProvider ;

public abstract class Style {
    
		protected HyperLink       hyperlink ;
		protected LinkProvider    linkProvider ;
		protected boolean doLinks; 
		
		public Style( ){
			linkProvider = null ;
			doLinks = false ;
		}
		
		public void setLinkProvider(LinkProvider linkProvider){
			this.linkProvider = linkProvider;
			this.doLinks   = linkProvider != null ;
		}
		
		public abstract String format(int styleId, SyntaxStyle style, String text) ;

    public abstract String style(int styleId, SyntaxStyle style) ;
		
		public static String getTokenString( int id ){
			return Token.tokenToString( (byte)id ) ;
		}
		
		public String getLinkText( int styleId, String text, String formattedText ){
			if( !doLinks || linkProvider == null ) return formattedText ;
			String url = linkProvider.getUrl( getTokenString(styleId), text ) ;
			if( url == null ) {
				return formattedText ;
			} else{
				return hyperlink.getLinkText( text, url , formattedText ) ;
			}
		}
		
		public abstract String getMode() ;
		
}

