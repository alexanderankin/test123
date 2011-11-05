/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 jEdit contributors
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

package gatchan.jedit.smartopen.indexer;

import gatchan.jedit.smartopen.indexer.FileProvider;
import org.gjt.sp.jedit.io.VFSFile;

/**
 * @author Matthieu Casanova
 */
public class FileArrayProvider implements FileProvider
{
	private final VFSFile[] fileArray;
	private int index;

	public FileArrayProvider(VFSFile[] files)
	{
		fileArray = files;
	}

	@Override
	public VFSFile next()
	{
		if (index >= fileArray.length)
			return null;
		return fileArray[index++];
	}

	@Override
	public int size()
	{
		return fileArray.length;
	}
}
