/*
 * HtmlCssHyperLink.java
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

package code2html.impl.htmlcss ;

import code2html.generic.HyperLink ;
import code2html.impl.html.HtmlHyperLink ;

public class HtmlCssHyperLink extends HyperLink{
	
	@Override
	public String getLinkText( String text, String url, String formattedText ){
		StringBuffer buf = new StringBuffer( ) ;
		buf.append( "<a href=\"").append( url )
			.append( "\">")
			.append( formattedText )
			.append( "</a>" ) ;
		return buf.toString( ) ;
	}
	
}

