/*
 * TagFile.java
 * Copyright (c) 2001, 2002 Kenrick Drew (kdrew@earthlink.net)
 * Copyright (c) 2004 Ollie Rutherfurd (oliver@jedit.org)
 *
 * This file is part of the TagsPlugin
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

public class TagFile
{
	//{{{ constants
	public static final String SEARCH_DIRECTORY = ".";
	public static final String SEARCH_DIRECTORY_AND_PARENTS = "..";
	//}}}

	//{{{ private declarations
	/** tag index file path */
	protected String path;

	/** does the tag index file represent the tag file in the current buffer's directory */
	protected boolean currentDirIndexFile = false;

	/** whether to search tag index file */
	protected boolean enabled;
	//}}}

	//{{{ TagFile constructor
	public TagFile(String path, boolean enabled)
	{
		if(path.equalsIgnoreCase(TagsPlugin.getCurrentBufferTagFilename())
			|| SEARCH_DIRECTORY.equals(path)
			|| SEARCH_DIRECTORY_AND_PARENTS.equals(path))
		{
			this.currentDirIndexFile = true;
		}
		this.path = path;
		this.enabled = enabled;
	} //}}}

	//{{{ isCurrentDirIndexFile()
	public boolean isCurrentDirIndexFile()
	{
		return currentDirIndexFile;
	} //}}}

	//{{{ isEnabled() method
	public boolean isEnabled()
	{
		return enabled;
	} //}}}

	//{{{ setEnabled() method
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	} //}}}

	//{{{ getPath() method
	public String getPath()
	{
		return path;
	} //}}}

	//{{{ setPath() method
	public void setPath(String path)
	{
		this.path = path;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return "TagFile path=" + getPath() + ", enabled=" + isEnabled()
			+ ", currentDirIndexFile=" + currentDirIndexFile;
	} //}}}

}

// :collapseFolds=1:noTabs=false:lineSeparator=\r\n:deepIndent=false:folding=explicit:
