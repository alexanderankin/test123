/*
 * LatexLink.java
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

import code2html.generic.HyperLink ;

/**
 * Class used to make hyperlinks in latex documents
 */ 
public class LatexHyperLink extends HyperLink{
	
	/** 
	 * Returns the latex markup for representing and hyperlink, using \\href from the hyperref package
	 * @param text not used
	 * @param url url of the link
	 * @param content of the text
	 * @return latex markup for the hyperlink
	 */
	@Override
	public String getLinkText( String text, String url, String formattedText ){
		StringBuffer buf = new StringBuffer( ) ;
		buf.append( "\\href{" )
				.append( url )
				.append( "}{" )
				.append( formattedText )
				.append( "}" ) ;
		return buf.toString( ) ;
	}
	
}

