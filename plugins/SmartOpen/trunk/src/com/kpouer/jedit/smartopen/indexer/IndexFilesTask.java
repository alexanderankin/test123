/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.smartopen.indexer;

//{{{ Imports
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.kpouer.jedit.smartopen.SmartOpenOptionPane;
import com.kpouer.jedit.smartopen.SmartOpenPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSFileFilter;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.Task;
//}}}

/**
 * @author Matthieu Casanova
 */
public class IndexFilesTask extends Task
{
	//{{{ _run() method
	@Override
	public void _run()
	{
		long start = System.currentTimeMillis();
		setStatus("Listing files");
		String property = jEdit.getProperty("options.ancestor.paths", "");
		if (!property.isEmpty())
		{
			StringTokenizer tokenizer = new StringTokenizer(property, File.pathSeparator);
			Set<VFSFile> files = new HashSet<VFSFile>();
			while (tokenizer.hasMoreTokens())
			{
				String s = tokenizer.nextToken();
				files.addAll(listFiles(s));
			}
			VFSFile[] f = new VFSFile[files.size()];
			files.toArray(f);
			SmartOpenPlugin.itemFinder.addFiles(new FileArrayProvider(f), this);
		}
		else
		{
			SmartOpenPlugin.itemFinder.addFiles(new FileArrayProvider(new VFSFile[0]), this);
		}
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this, "Indexation took ms:" + (end - start));
	} //}}}

	//{{{ listFiles() method
	private Collection<VFSFile> listFiles(String path)
	{
		VFS vfs = VFSManager.getVFSForPath(path);
		Object vfsSession = null;
		View activeView = jEdit.getActiveView();
		Collection<VFSFile> files = new HashSet<VFSFile>();
		try
		{
			vfsSession = vfs.createVFSSession(path, activeView);
			long listStart = System.currentTimeMillis();
			String[] strings =
				vfs._listDirectory(vfsSession, path, new MyVFSFilter(), true, activeView, false, false);
			long listEnd = System.currentTimeMillis();
			Log.log(Log.MESSAGE, this, "Listing files took ms:" + (listEnd - listStart));
			setStatus("preparing data");
			setMaximum(strings.length);
			for (int i = 0; i < strings.length; i++)
			{
				setValue(i);
				String string = strings[i];
				VFSFile vfsFile = vfs._getFile(vfsSession, string, activeView);
				if (vfsFile != null)
					files.add(vfsFile);
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			try
			{
				vfs._endVFSSession(vfsSession, activeView);
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
			}
		}
		return files;
	} //}}}

	//{{{ MyVFSFilter class
	private static class MyVFSFilter implements VFSFileFilter
	{
		private final String[] excludedDirectories;

		private MyVFSFilter()
		{
			String property = jEdit.getProperty("options.smartopen.ExcludeDirectories", "CVS .svn .git");
			excludedDirectories = property.split(" ");
			Arrays.sort(excludedDirectories);
		}

		@Override
		public boolean accept(VFSFile file)
		{
			String name = file.getName();
			if (file.getType() == VFSFile.DIRECTORY || file.getType() == VFSFile.FILESYSTEM)
			{
				return Arrays.binarySearch(excludedDirectories, name) < 0;
			}
			else
			{
				return accept(name);
			}
		}

		@Override
		public boolean accept(String url)
		{
			return SmartOpenOptionPane.accept(url);
		}

		@Override
		public String getDescription()
		{
			return null;
		}
	} //}}}
}