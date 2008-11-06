/*
 * TagFileReader.java
 *
 * Copyright 2007 Shlomy Reinstein <shlomy@users.sourceforge.net>
 * Copyright 2004 Ollie Rutherfurd <oliver@jedit.org>
 * Portions Copyright (c) 2001, 2002 Kenrick Drew
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
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;
//}}}

public abstract class TagFileReader
{
	//{{{ getForPath() method
	/**
	 * Factory method that returns the appropriate reader for 
	 * the given path.
	 *
	 * If the VFS for the given path is returned, then a 
	 * RandomAccessFileReader is returned, otherwise a 
	 * BufferTagFileReader is returned.
	 *
	 * If unable to open
	 *
	 * @return TagFileReader
	 */
	public static TagFileReader getForPath(View view, String path)
	{
		if(!VFSManager.getVFSForPath(path).getName().equals("file")
			|| jEdit.getBooleanProperty("options.tags.cache-all", false))
		{
			try
			{
				return new BufferTagFileReader(view, path);
			}
			catch(FileNotFoundException fnf)
			{
				Log.log(Log.WARNING, TagFileReader.class, "File not found: " + path);
				return null;
			}
		}
		else
		{
			try
			{
				return new RandomAccessFileReader(view, path);
			}
			catch(FileNotFoundException fnf)
			{
				Log.log(Log.WARNING, TagFileReader.class, "File not found: " + path);
				return null;
			}
			catch(SecurityException se)
			{
				Log.log(Log.WARNING, TagFileReader.class, "Unable to read: " + path);
				return null;
			}
			catch(IOException ioe)
			{
				Log.log(Log.WARNING, TagFileReader.class, "IOException reading: " + path);
				return null;
			}
		}
	} //}}}

	//{{{ abstract methods
	/**
	 * Close any used resources.
	 */
	public abstract void close();

	/**
	 * Return true if this Reader may be re-used
	 * and cached in RAM.
	 */
	public abstract boolean isCacheable();

	/**
	 * Return a Vector of TagLine instances, or an
	 * empty Vector if no matches found.
	 */
	public abstract Vector findTagLines(String name);

	/**
	 * Return path to tag index file.
	 */
	public abstract String getPath();

	/**
	 * Optimization to determine whether to skip this reader.
	 */
	public abstract boolean quickReject(String tagToFind);

	/**
	 * This is only applicable to cachable readers.
	 */
	public abstract void reload(View view);
	//}}}

	//{{{ foundTagMatch() method
	/**
	 * Returns true if the given line contains the
	 * tag we're looking for.
	 */
	protected boolean foundTagMatch(String line, String tag)
	{
		boolean matches = line.startsWith(tag);
		if(matches)	// only "starts with", need to compare tokens
		{ 
			StringTokenizer st = new StringTokenizer(line);
			if(st.hasMoreTokens())
				matches = tag.equals(st.nextToken());
			st = null;
		}
		return matches;
	} //}}}

	//{{{ readTagLine() method
	/**
	 * Returns a TagLine from the current line.
	 */
	public TagLine readTagLine(String tag, String line)
	{
		StringTokenizer st = new StringTokenizer(line);
		tag = st.nextToken();	// skip tag def field (already got it)	// XXX don't do this, use it
		// file tag is defined in
		String tagDefinitionFile = st.nextToken();
		// cygwin workaround
		if(tagDefinitionFile.startsWith("/cygdrive/")){
			char drive = tagDefinitionFile.charAt(10);
			tagDefinitionFile = drive + ":" + 
								tagDefinitionFile.substring(11);
		}
		String tagDefinitionParent = MiscUtilities.getParentOfPath(getPath());
		String tagDefinitionPath = MiscUtilities.constructPath(tagDefinitionParent,
										tagDefinitionFile);
		String tagDefSearchString = null;
		// get string to search for
		if(line.lastIndexOf(";\"") == -1)	// --format=1
			tagDefSearchString = st.nextToken("");
		else
		{
			// XXX this doesn't work with:
			//	/^	public String toString() { return "BSHVariableDeclarator "+name; }$/;"
			//
			// We use '"' b/c we can't use the multi character delimiter that
			// Exuberant C Tags uses (which is ;").  We will add on the "$/" for
			// the string massager.
			tagDefSearchString = st.nextToken("\"");
			// lop off ";" of the format 2 delim of ';"'
			tagDefSearchString = tagDefSearchString.substring(0,
									tagDefSearchString.length() - 1);
		}
	
		// Search string is a line number for "#define" tags
		int lineNumber = 0;
		boolean isNumber = true;
		tagDefSearchString = tagDefSearchString.trim();
		try
		{
			lineNumber = Integer.parseInt(tagDefSearchString);
			tagDefSearchString = null;
		}
		catch(NumberFormatException nfe)
		{
			isNumber = false;
		}

		if(!isNumber && tagDefSearchString != null)
		{
			tagDefSearchString = unescapeSearch(
				tagDefSearchString.substring(2, tagDefSearchString.length() - 2));
		}

		Vector exuberantInfoItems = null;
		if(st.hasMoreTokens()){
			st.nextToken(" \t\n\r\f");  // get rid of ;"
			exuberantInfoItems = new Vector(5);
		}
		while(st.hasMoreTokens())
		{
			String info = st.nextToken("\t");
			if (TagsPlugin.getUseLineNumbers() &&
				info.startsWith("line:"))
			{
				try {
					int l = Integer.parseInt(info.substring(5));
					lineNumber = l;
				}
				catch(NumberFormatException nfe) {}
			}
			exuberantInfoItems.addElement(new ExuberantInfoItem(info));
			Log.log(Log.DEBUG, this, "added ExuberantInfoItem: " + info);
		}

		TagLine tl = new TagLine(tag, tagDefinitionPath,
								  tagDefSearchString,
								  lineNumber,
								  tagDefinitionPath);
		tl.setExuberantInfoItems(exuberantInfoItems);
		return tl;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return getPath();
	} //}}}

	//{{{ unescapeSearch() method
	public String unescapeSearch(String search)
	{
		Log.log(Log.DEBUG, this, "Original search: " + search);	// XXX
		StringBuffer buf = new StringBuffer(search.length());
		char[] chars = search.toCharArray();
		for(int i=0; i < chars.length; i++)
		{
			char c = chars[i];
			// removes '/' from "\// foo"
			if(c == '\\' && (i+1 < chars.length) && chars[i+1] == '/')
				continue;
			else
				buf.append(c);
		}
		search = buf.toString();
		Log.log(Log.DEBUG, this, "New search: " + search);	// XXX
		return search;
	} //}}}
}

/**
 * This reader reads a Tag index file using a RandomAccessFile,
 * so it's suitable for large files, but only works for local
 * files.
 */
class RandomAccessFileReader extends TagFileReader
{
	//{{{ RandomAccessFileReader constructor
	public RandomAccessFileReader(View view, String path)
		throws FileNotFoundException,
				SecurityException
	{
		this.path = path;
		try
		{
			file = new File(path);
			raf = new RandomAccessFile(file, "r");
		}
		catch(FileNotFoundException fnf)
		{
			throw fnf;
		}
		catch(SecurityException se)
		{
			throw se;
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR, this, "Couldn't open RandomAccessFile: " + path);
			ioe.printStackTrace();
		}
	} //}}}

	//{{{ isCacheable() method
	public boolean isCacheable()
	{
		return false;
	} //}}}

	//{{{ close() method
	public void close()
	{
		;	// no-op method, since it's not cacheable it's not reused.
	} //}}}

	//{{{ findTagLines() method
	public Vector findTagLines(String tag)
	{
		Vector tagLines = new Vector();
		try
		{
			// don't bother searching if the last tag
			// in the file is < the tag we're looking for
			if(quickReject(tag))
			{
				Log.log(Log.DEBUG, this, "Quick rejected: " + getPath());
				return tagLines;
			}

			String line = null;
			long start = 0;
			boolean found = false;
			long end = file.length();
			long mid = end / 2;
			long lastPos = 0;
			long forwardPos = 0;
			int compare = 0;

			while(!found && mid != start && mid != end)
			{
				raf.seek(mid);
				lastPos = seekToLineStart();
				line = raf.readLine();
				if(line == null)
					break;
				forwardPos = raf.getFilePointer();
				found = foundTagMatch(line, tag);	// XXX 
				if(found)
					tagLines.addElement(readTagLine(tag,line));
				else
				{
					compare = tag.compareTo(line);
					if(compare < 0)
						end = mid;
					else
						start = mid;
					mid = ((end - start) / 2) + start;
				}
			}

			if(!found)
				return tagLines;

			// linear search backwards, since it's possible we landed
			// in the middle of a group of matching tags
			long backupPos = lastPos - 2;
			long currentPos = 0;
			boolean differentTag = false;
			while(backupPos >= 0 && !differentTag && currentPos != forwardPos)
			{
				raf.seek(backupPos);
				lastPos = seekToLineStart();
				line = raf.readLine();
				if(line == null)
					break;
				differentTag = !foundTagMatch(line, tag);
				if(!differentTag)
				{
					backupPos = lastPos - 2;
					tagLines.insertElementAt(readTagLine(tag, line), 0);
				}
				currentPos = raf.getFilePointer();
			}

			// linear search forward
			boolean foundForward = found;
			raf.seek(forwardPos);
			while(foundForward)
			{
				line = raf.readLine();
				if(line == null)
					break;
				foundForward = foundTagMatch(line, tag);
				if(foundForward)
					tagLines.addElement(readTagLine(tag, line));
			}

		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR, this, "IOException reading: " + getPath());
			ioe.printStackTrace();
		}
		finally
		{
			try
			{
				raf.close();
				file = null;
			}
			catch(IOException ioe)
			{
				Log.log(Log.ERROR,this,ioe);
			}
		}

		return tagLines;
	} //}}}

	//{{{ getPath() method
	public String getPath()
	{
		return this.path;
	} //}}}

	//{{{ quickReject() method
	public boolean quickReject(String tag)
	{
		try
		{
			if(raf.length() == 0)
				return true;
			raf.seek(file.length());
			seekToLineStart();
	
			String line = raf.readLine();
			StringTokenizer st = new StringTokenizer(line);
			if(st.hasMoreTokens())
			{
				String name = st.nextToken();
				if(tag.compareTo(name) > 0)
					return true;
			}
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR, this, "Error quickReject'ing: " + getPath());
			ioe.printStackTrace();
			return false;
		}
		return false;
	} //}}}

	//{{{ reload() method
	public void reload(View view)
	{
		;	// no-op method, since it's not cacheable
	} //}}}

	//{{{ seekToLineStart() method
	/**
	 * Moves the current file position to the start of the current line.
	 *
	 * @return the position started at
	 */
	private long seekToLineStart()
	{
		int c = 'a';
		long offset = 0;

		while(c != '\n' && offset != 1)
		{
			try
			{
				c = raf.read();
				offset = raf.getFilePointer();
				if(offset != 1 && c != '\n')
					raf.seek(offset - 2);
				else if(offset == 1)
				{
					offset = 0;
					raf.seek(0);
					break;
				}
			}
			catch(IOException ioe)
			{
				Log.log(Log.ERROR, this, 
					"Problem skipping backward to beginning of tag line");
				ioe.printStackTrace();
			}
		}
		return offset;
	} //}}}

	//{{{ private declarations
	private RandomAccessFile raf = null;
	private File file = null;
	private String path = null;
	//}}}
}

/**
 * This class reads a Tag file into a temporary buffer
 * so Tag files can be read and searched over a VFS.
 */
class BufferTagFileReader extends TagFileReader
{
	//{{{ constructor
	public BufferTagFileReader(View view, String path)
		throws FileNotFoundException
	{
		this.path = path;
		buffer = jEdit.openTemporary(view, null, path, false);
		if(buffer == null || buffer.isNewFile())
		{
			buffer = null;
			throw new FileNotFoundException();
		}
	} //}}}

	//{{{ isCacheable() method
	public boolean isCacheable()
	{
		return true;
	} //}}}

	//{{{ close() method
	public void close()
	{
		buffer = null;
	} //}}}

	//{{{ findTagLines() method
	public Vector findTagLines(String tag)
	{
		Vector tagLines = new Vector();
		String line = null;
		boolean found = false;
		int start = 0;
		int end = buffer.getLineCount() - 1;
		int mid = end / 2;
		int compare = 0;
		int foundFirst = 0;

		while(!found && mid != start && mid != end)
		{
			line = buffer.getLineText(mid);
			if(line.startsWith(tag))
			{
				int tab = line.indexOf('\t');
				if(tab > -1)
				{
					String tagDef = line.substring(0,tab);
					if(tagDef.equals(tag))
					{
						found = true;
						foundFirst = mid;
						tagLines.add(readTagLine(tag, line));
					}
				}
			}

			if(!found)
			{
				compare = tag.compareTo(line);
				if(compare < 0)
					end = mid;
				else
					start = mid;
				mid = ((end - start) / 2) + start;
			}
		}

		if(!found)
			return tagLines;

		// do a linear search backwards, in case we found
		// one of a set of matches tags and we're in the
		// middle of it
		boolean differentTag = false;
		while(mid > 0 && !differentTag)
		{
			mid--;
			line = buffer.getLineText(mid);
			differentTag = !foundTagMatch(line, tag);
			if(!differentTag)
			{
				tagLines.insertElementAt(readTagLine(tag, line), 0);
			}
			else
				break;
		}

		// do a linear forward search
		mid = foundFirst + 1;
		while(mid < buffer.getLineCount())
		{
			line = buffer.getLineText(mid);
			if(foundTagMatch(line, tag))
			{
				tagLines.addElement(readTagLine(tag, line));
				mid++;
			}
			else
				break;
		}

		return tagLines;
	} //}}}

	//{{{ getPath() method
	public String getPath()
	{
		return this.path;
	} //}}}

	//{{{ quickReject() method
	public boolean quickReject(String tag)
	{
		String last = buffer.getLineText(buffer.getLineCount()-1);
		StringTokenizer st = new StringTokenizer(last);
		if(st.hasMoreTokens())
		{
			String name = st.nextToken();
			return (tag.compareTo(name) > 0);
		}
		else
			return false;
	} //}}}

	//{{{ reload() method
	public void reload(View view)
	{
		buffer.reload(view);
	} //}}}

	//{{{ private declarations
	private Buffer buffer = null;
	private String path = null;
	//}}}
}

// :noTabs=false:tabSize=4:folding=explicit:collapseFolds=1:
