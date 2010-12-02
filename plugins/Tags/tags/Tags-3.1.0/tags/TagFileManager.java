/*
 * TagFileManager.java
 *
 * Copyright 2004 Ollie Rutherfurd <oliver@jedit.org>
 *
 * This file is part of Tags plugin.
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
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

import java.util.ArrayList;
import java.util.Vector;
//}}}

/**
 * A utility class for managing Tag Index files.
 *
 * It can locate Tag Index files an keeps a MRU-based
 * cache of TagIndexFileReaders.  Every time a reader
 * is used, it's placed at the top of the cache list
 * so that least-recently used reader will drop off the list
 * once it's full.
 */
public class TagFileManager
{
	//{{{ constants
	public static final int CACHE_SIZE = 5;
	//}}}

	//{{{ private declarations
	private ArrayList cache = null;
	private int size = 0;
	private Vector tagFiles = null;
	//}}}

	//{{{ TagFileManager constructor
	public TagFileManager()
	{
		this(jEdit.getIntegerProperty("options.tags.cache-size",CACHE_SIZE));
	} //}}}

	//{{{ TagFileManager constructor
	private TagFileManager(int size)
	{
		this.size = size;
		this.cache = new ArrayList(size);
		this.tagFiles = new Vector(10);
		loadTagFiles();
	} //}}}

	//{{{ clear() method
	/**
	 * Clears the cache of stored readers.
	 */
	public void clear()
	{
		for(int i = cache.size()-1; i >= 0; i--)
		{
			TagFileReader reader = (TagFileReader)cache.remove(i);
			reader.close();
		}
	} //}}}

	//{{{ findTagIndexFiles() method
	public Vector findTagIndexFiles(View view, String path)
		throws java.io.IOException
	{
		return findTagIndexFiles(view,path,-1);
	} //}}}

	//{{{ findTagIndexFiles() method
	/**
	 * Finds tag index files for a given given filename or directory.
	 * This function search in the directory and up the directory tree
	 * looking for tag index files, until it either reaches the root
	 * or finds the maximum number of tag index files requested.
	 *
	 * @param view Parent for all errors, dialogs, etc...
	 * @param path Location to start search from.  This may be a filename 
	 * 		  or directory.
	 * @param max Maximum number of tag index files to find. If not greater
	 * 		  than 0, this is ignored.
	 *
	 * @return A vector of tag index file paths.
	 */
	public Vector findTagIndexFiles(View view, String path, int max)
		throws java.io.IOException
	{
		String tagFilename = TagsPlugin.getCurrentBufferTagFilename();
		Log.log(Log.DEBUG, this, "findTagIndexFiles: " + path); // ##
		Vector tagFiles = new Vector();
		String directory = null;
		Object session = null;
		VFS vfs = VFSManager.getVFSForPath(path);

		try
		{
			Log.log(Log.DEBUG, this, "using VFS: " + vfs.getName());  // ##
			session = vfs.createVFSSession(path, view);
			boolean stop = false;
	
			VFSFile entry = vfs._getFile(session, path, view);
			if(entry == null || entry.getType() == VFSFile.FILE)
				directory = vfs.getParentOfPath(path);
			else
				directory = path;
	
			while(stop != true)
			{
				String tagFilePath = MiscUtilities.constructPath(directory,tagFilename);
				entry = vfs._getFile(session, tagFilePath, view);
				Log.log(Log.DEBUG, this, "entry for ("+tagFilePath+"): " + entry);	// ##

				if(entry == null)
					Log.log(Log.DEBUG, this, "'" + tagFilePath + "' doesn't exist.");
				else if(entry.getType() == VFSFile.FILE)
				{
					if(max <= 0 || tagFiles.size() < max)
					{
						tagFiles.addElement(tagFilePath);
						Log.log(Log.DEBUG, this, "found: " + tagFilePath);
					}

					if((max > 0) && (tagFiles.size() >= max))
					{
						Log.log(Log.DEBUG, this, "reached max of " + max);	// ##
						stop = true;
						break;
					}
				}
				else
					Log.log(Log.DEBUG, this, "'" + tagFilePath 
								+ "' isn't a file");
	
				// go to the parent directory
				String parent = vfs.getParentOfPath(directory);
				// make sure we are able to go up a level
				if(parent.equals(directory))
				{
					break;
				}
				// make sure we're not at the FS root
				entry = vfs._getFile(session, parent, view);
				if(entry != null && entry.getType() == VFSFile.FILESYSTEM)
				{
					Log.log(Log.DEBUG, this, "stopping recurse at: " + parent);
					stop = true;
					break;
				}
				directory = parent;
			}
		}
		finally
		{
			if(session != null)
				vfs._endVFSSession(session,view);
		}
		Log.log(Log.DEBUG, this, "tagFiles: " + tagFiles); // ##
		return tagFiles;
	} //}}}

	//{{{ getCache() method
	public Vector getCache()
	{
		Vector vector = new Vector(this.cache);
		return vector;
	} //}}}

	//{{{ getTagFiles() method
	public Vector getTagFiles()
	{
		return tagFiles;
	} //}}}

	//{{{ getTagIndexFiles() method
	/**
	 * Returns a Vector of tag index file paths for the given
	 * filename.  The Vector contains both explicitly
	 * listed tag index files and any found in the filename's
	 * directory or parent directories if configured for that.
	 *
	 * The order of the paths returned are the same as the order
	 * specified in the option pane (with any found files inserted
	 * in the correct order).
	 */
	public Vector getTagIndexFiles(View view, String filename)
	{
		Vector tagIndexFiles = new Vector();
		for(int i=0; i < tagFiles.size(); i++)
		{
			TagFile tf = (TagFile)tagFiles.elementAt(i);
			if(!tf.isEnabled())
			{
				Log.log(Log.DEBUG, this, "ignoring disabled: " + tf);
				continue;
			}
			else
			{
				Log.log(Log.DEBUG, this, "adding explicit: " + tf);	// XXX
				tagIndexFiles.addElement(tf.getPath());
			}
		}
		return tagIndexFiles;
	} //}}}

	//{{{ getReader() method
	/**
	 * Returns a TagIndexFileReader for the given path.
	 * 
	 * @param tagFilePath should either be a path to a tag index file
	 * 		  or 
	 */
	public TagFileReader getReader(View view, String tagFilePath, String bufferPath)
		throws java.io.IOException
	{

		// handle looking for SEARCH_DIRECTORY and SEARCH_DIRECTORY_AND_PARENTS
		if(TagFile.SEARCH_DIRECTORY.equals(tagFilePath) 
			|| TagFile.SEARCH_DIRECTORY_AND_PARENTS.equals(tagFilePath))
		{
			Vector tagFiles = findTagIndexFiles(view, bufferPath, 1);
			if(tagFiles.size() == 0)
			{
				Log.log(Log.DEBUG, this, "didn't find a tag file for: " + bufferPath);
				return null;
			}
			else
				Log.log(Log.DEBUG, this, "searching '" + tagFilePath 
							+ "' found '" + tagFiles.elementAt(0) + 
							"' for '" + bufferPath);
			tagFilePath = (String)tagFiles.elementAt(0);
		}

		Log.log(Log.DEBUG, this, "checking cache for " + tagFilePath);	// ##
		// look for the reader in the cache
		for(int i=0; i < cache.size(); i++)
		{
			TagFileReader reader = (TagFileReader)cache.get(i);
			if(reader.getPath().equals(tagFilePath))
			{
				// if not at the front of the list
				// move it to the front
				if(i != 0)
				{
					cache.remove(i);
					cache.add(0,reader);
				}
				return reader;
			}
		}

		TagFileReader reader = TagFileReader.getForPath(view,tagFilePath);
		if(reader != null && reader.isCacheable())
		{
			cache.add(0,reader);
		}

		while(cache.size() > this.size)
		{
			Log.log(Log.DEBUG, this, "removing reader[" + (cache.size()-1) + "] from cache"); // ##
			cache.remove(cache.size()-1);
		}
		return reader;
	} //}}}

	//{{{ getSize() method
	public int getSize()
	{
		return this.size;
	} //}}}

	//{{{ setSize() method
	public void setSize(int size)
	{
		this.size = size;
		while(size > cache.size())
		{
			remove(--size);
		}
	} //}}}

	//{{{ loadTagFiles()
	/**
	 * Loads user-defined set of explicit tag index files to search.
	 */
	private void loadTagFiles()
	{
		Log.log(Log.DEBUG, this, "Loading tag index files"); // XX ##
		int i = 0;
		String path = null;
		String defaultTagIndexFile = TagsPlugin.getCurrentBufferTagFilename();
		while((path = jEdit.getProperty("tags.tagfile.path." + i)) != null)
		{
			boolean enabled = jEdit.getBooleanProperty("tags.tagfile.enabled." + i, true);
			// migrate property to new format, where "." and ".." are used
			if(path.equals(defaultTagIndexFile))
			{
				boolean searchParentDirs = jEdit.getBooleanProperty("options.tags.tags-search-parent-dirs",true);
				// unset since no longer used
				jEdit.unsetProperty("options.tags.tags-search-parent-dirs");
				String newPath = searchParentDirs ? TagFile.SEARCH_DIRECTORY_AND_PARENTS : TagFile.SEARCH_DIRECTORY;
				jEdit.setProperty("tags.tagfile.path." + i, newPath);
				Log.log(Log.DEBUG, this, "changed 'tags.tagfile.path." 
							+ i + "' from " + path + " to " + newPath);
				path = newPath;
			}
			TagFile tagFile = new TagFile(path, enabled);
			tagFiles.addElement(tagFile);
			i++;
		}
		// if none specified by default, add default tag index file
		if(tagFiles.size() == 0)
		{
			TagFile tf = new TagFile(TagFile.SEARCH_DIRECTORY_AND_PARENTS, true);
			tagFiles.addElement(tf);
			Log.log(Log.DEBUG, this, "added default tag index: " + tf);
		}
	} //}}}

	//{{{ reload() method
	public void reload()
	{
		tagFiles = new Vector();
		loadTagFiles();
	} //}}}

	//{{{ remove() method
	/**
	 * Removes an entry from the cache of stored readers.
	 */
	public void remove(int index)
	{
		if(index >= 0 && index < cache.size())
		{
			cache.remove(index);
		}
	} //}}}

}

// :noTabs=false:tabSize=4:folding=explicit:collapseFolds=1:
