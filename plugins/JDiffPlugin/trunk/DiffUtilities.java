/*
 * DiffUtilities.java
 * Copyright (c) 2000 Andre Kaplan
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


import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import gnu.regexp.RE;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;


public class DiffUtilities
{
    public static final jdiff.text.DiffDocument diffFilesOrDirs(String file1,
        String file2, String glob, boolean recurse) 
    {
        File f1 = new File((new File(file1)).getAbsolutePath());
        File f2 = new File((new File(file2)).getAbsolutePath());

        if (f1.isFile() && f2.isFile()) {
            jdiff.text.DiffDocument doc = jdiff.gui.GUIUtilities.loadDiffDocument(
                file1, file2
            );

            if (doc != null) {
                return doc;
            }
        } else if (f1.isDirectory() && f2.isDirectory()) {
            String dir1 = f1.getAbsolutePath();
            String dir2 = f2.getAbsolutePath();
            Log.log(Log.DEBUG, DiffUtilities.class, dir1);
            Log.log(Log.DEBUG, DiffUtilities.class, dir2);
            return diffDirs(dir1, dir2, glob, recurse);
        }

        return null;
    }

    public static final jdiff.text.DiffDocument
        diffDirs(String d1, String d2, String glob, boolean recurse)
    {
        jdiff.text.FileData fd1 = fileListToFileData("<dir1>", 
            d1 + File.separator, listFiles(d1, glob, recurse));
        jdiff.text.FileData fd2 = fileListToFileData("<dir2>", 
            d2 + File.separator, listFiles(d2, glob, recurse));

        return new jdiff.text.DiffDocument(fd1, fd2);
    }

    public static final jdiff.text.FileData fileListToFileData(String name, 
        String dir, Vector fileList) 
    {
        MiscUtilities.quicksort(fileList, new DiffUtilities.FileComparator());

        Enumeration e = fileList.elements();
        
        jdiff.text.FileLine[] fl = new jdiff.text.FileLine[fileList.size()];

        String dirLCase = dir.toLowerCase();

        int idx = 0;
        while (e.hasMoreElements()) {
            String fileName = (String)e.nextElement();
            if (fileName.toLowerCase().startsWith(dirLCase)) {
                fileName = fileName.substring(dir.length());
            }
            fl[idx++] = new jdiff.text.FileLine(fileName);
        }

        return new jdiff.text.FileData(name, fl);
    } 

    public static final class FileComparator
        implements MiscUtilities.Compare
    {
        public int compare(Object o1,
                           Object o2) {
            String f1 = (String)o1;
            String f2 = (String)o2;
    
            return f1.compareTo(f2);
        }
    }


	/**
	Borrowed form org.gjt.sp.jedit.search.DirectoryListSet
	*/
	private static Vector listFiles(String directory,
		String glob, boolean recurse)
	{
		Log.log(Log.DEBUG,DiffUtilities.class,"Searching in "
			+ directory);
		Vector files = new Vector(50);

		RE filter;
		try
		{
			filter = new RE(MiscUtilities.globToRE(glob));
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,DiffUtilities.class,e);
			return files;
		}

		listFiles(new Vector(),files,new File(directory),filter,recurse);

		return files;
	}

	private static void listFiles(Vector stack, Vector files,
		File directory, RE filter, boolean recurse)
	{
		if(stack.contains(directory))
		{
			Log.log(Log.ERROR,DiffUtilities.class,
				"Recursion in DiffUtilities: "
				+ directory.getPath());
			return;
		}
		else
			stack.addElement(directory);
		
		String[] _files = directory.list();
		if(_files == null)
			return;

		MiscUtilities.quicksort(_files,new MiscUtilities.StringICaseCompare());

		for(int i = 0; i < _files.length; i++)
		{
			String name = _files[i];

			File file = new File(directory,name);
			if(file.isDirectory())
			{
				if(recurse)
					listFiles(stack,files,file,filter,recurse);
			}
			else
			{
				if(!filter.isMatch(name))
					continue;

				Log.log(Log.DEBUG,DiffUtilities.class,file.getPath());
				String canonPath;
				try
				{
					canonPath = file.getCanonicalPath();
				}
				catch(IOException io)
				{
					canonPath = file.getPath();
				}
				files.addElement(canonPath);
			}
		}
	}

	private DiffUtilities() {}
}

