/*
 * TagLine.java
 *
 * Copyright (c) 2001, 2002 Kenrick Drew <kdrew@earthlink.net>
 * Copyright (c) 2004 Ollie Rutherfurd <oliver@jedit.org>
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
 * $Id: TagLine.java,v 1.7 2004/11/07 15:52:36 orutherfurd Exp $
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

//{{{ imports
import java.util.Vector;
//}}}

public class TargetLine 
{
	//{{{ private declarations
	private int index = -1;
	private String tag;
	private String definitionFile;
	private String searchString;
	private int definitionLineNumber = -1;
	private Vector exuberantInfoItems;
	//}}}

	//{{{ TargetLine constructor
	public TargetLine(String tag, String definitionFile,
					String searchString, int definitionLineNumber,
					String tagIndexFile)
	{
		this.index = -1;
		this.tag = tag;
		this.definitionFile = definitionFile;
		this.searchString = searchString;
		this.definitionLineNumber = definitionLineNumber;
	} //}}}

	//{{{ getExuberantInfoItems() method
	public Vector getExuberantInfoItems()
	{
		return exuberantInfoItems;
	} //}}}

	//{{{ setExuberantInfoItems() method
	public void setExuberantInfoItems(Vector exuberantInfoItems)
	{
		this.exuberantInfoItems = exuberantInfoItems;
	} //}}}

	//{{{ getDefinitionFileName() method
	/**
	 * Path to file containing the Target definition.
	 */
	public String getDefinitionFileName()
	{
		return definitionFile;
	} //}}}

	//{{{ getDefinitionLineNumber() method
	/**
	 * Target definition line number or -1 if there is none.
	 */
	public int getDefinitionLineNumber()
	{
		return definitionLineNumber;
	} //}}}

	//{{{ getIndex() method
	public int getIndex()
	{
		return this.index;
	} //}}}

	//{{{ setIndex() method
	public void setIndex(int index)
	{
		this.index = index;
	} //}}}

	//{{{ getSearchString() method
	/**
	 * Target definition string
	 */
	public String getSearchString()
	{
		return searchString;
	} //}}}

	//{{{ getTag() method
	/**
	 * Returns the tag name.
	 */
	public String getTag()
	{
		return tag;
	} //}}}

	//{{{ toString() method
	public String toString() 
	{
		StringBuffer b = new StringBuffer();
		if((index) < 10)
			b.append(" " + (index));
		else
			b.append((index));
		
		b.append(": " + tag + " (" + definitionFile + ")");
		return b.toString();
	} //}}}

}
