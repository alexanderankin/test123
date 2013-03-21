/*
 * ExuberantInfoItem.java
 * Copyright (c) 2001, 2002 Kenrick Drew
 * kdrew@earthlink.net
 *
 * This file is part of TagsPlugin
 *
 * TagsPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * TagsPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id: ExuberantInfoItem.java,v 1.6 2004/11/07 15:52:34 orutherfurd Exp $
 */
/*
 * This file originates from the Tags Plugin version 2.0.1
 * whose copyright and licensing is seen above.
 * The original file was modified to become the derived work you see here
 * in accordance with Section 2 of the Terms and Conditions of the GPL v2.
 *
 * The derived work is called the CscopeFinder Plugin and is
 * Copyright 2006 Dean Hall.
 *
 * 2006/08/09
 */

package cscopefinder;

class ExuberantInfoItem
{
	//{{{ private declarations
	private String original;
	private String formatted;
	//}}}

	///{{{ ExuberantInfoItem constructor
	public ExuberantInfoItem(String token) 
	{
		original = token;

		int colon = token.indexOf(':');
		if (colon != -1)
			formatted = token.substring(0,colon) + ": " 
				+  token.substring(colon + 1);
		else
			formatted = token;
	} //}}}

	//{{{ toHTMLString() method
	public String toHTMLString()
	{
		return formatted;
	} //}}}

	//{{{ toString()
	public String toString()
	{
		return original;
	} //}}}
}
