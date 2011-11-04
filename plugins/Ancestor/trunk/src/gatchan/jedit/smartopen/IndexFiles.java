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

package gatchan.jedit.smartopen;

import java.io.IOException;
import java.util.Arrays;

import gatchan.jedit.ancestor.AncestorPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSFileFilter;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.Task;

/**
 * @author Matthieu Casanova
 */
public class IndexFiles extends Task
{
	@Override
	public void _run()
	{
		long start = System.currentTimeMillis();
		setStatus("Listing files");
		String property = jEdit.getProperty("options.smartIndexer.folder", "");
		if (!property.isEmpty())
		{
			VFS vfs = VFSManager.getVFSForPath(property);
			Object vfsSession = null;
			View activeView = jEdit.getActiveView();
			try
			{
				vfsSession = vfs.createVFSSession(property, activeView);
				long listStart = System.currentTimeMillis();
				String[] strings =
					vfs._listDirectory(vfsSession, property, new MyVFSFilter(), true, activeView,
							   false, false);
				long listEnd = System.currentTimeMillis();
				Log.log(Log.MESSAGE, this, "Listing files took ms:" + (listEnd - listStart));
				setStatus("preparing data");
				setMaximum(strings.length);
				VFSFile[] files = new VFSFile[strings.length];
				for (int i = 0; i < strings.length; i++)
				{
					setValue(i);
					String string = strings[i];
					files[i] = vfs._getFile(vfsSession, string, activeView);
				}
				AncestorPlugin.itemFinder.addFiles(new FileArrayProvider(files), this);
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
		}
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this, "Indexation took ms:" + (end - start));
	}

	private static class MyVFSFilter implements VFSFileFilter
	{
		private final String[] excludedDirectories;

		private MyVFSFilter()
		{
			String property = jEdit.getProperty("options.ancestor.ExcludeDirectories", "CVS .svn .git");
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
				return true;
			}
		}

		@Override
		public boolean accept(String url)
		{
			return true;
		}

		@Override
		public String getDescription()
		{
			return null;
		}
	}
}