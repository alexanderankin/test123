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
 * $Id$
 */

package tags;

//{{{ imports
import java.util.Vector;
//}}}

public class TagLine 
{
	//{{{ private declarations
	private int index = -1;
	private String tag;
	private String definitionFile;
	private String searchString;
	private int definitionLineNumber = -1;
	private Vector<ExuberantInfoItem> exuberantInfoItems;
	//}}}

	//{{{ TagLine constructor
	public TagLine(String tag, String definitionFile,
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
	public Vector<ExuberantInfoItem> getExuberantInfoItems()
	{
		return exuberantInfoItems;
	} //}}}

	//{{{ setExuberantInfoItems() method
	public void setExuberantInfoItems(Vector<ExuberantInfoItem> exuberantInfoItems)
	{
		this.exuberantInfoItems = exuberantInfoItems;
	} //}}}

	//{{{ getDefinitionFileName() method
	/**
	 * Path to file containing the Tag definition.
	 */
	public String getDefinitionFileName()
	{
		return definitionFile;
	} //}}}

	//{{{ getDefinitionLineNumber() method
	/**
	 * Tag definition line number or -1 if there is none.
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
	 * Tag definition string
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

// :noTabs=false:tabSize=4:folding=explicit:collapseFolds=1:
