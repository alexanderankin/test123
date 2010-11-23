/*
 * HexVFS.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
 * Portions copyright (C) 2010 Matthieu Casanova
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


package hex;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class HexVFS extends VFS
{
	public static final String PROTOCOL = "hex";


	public HexVFS()
	{
		super(PROTOCOL, VFS.BROWSE_CAP | VFS.READ_CAP);
	}

	public char getFileSeparator()
	{
		return File.separatorChar;
	}


	public String getFileName(String path)
	{
		String protocol = this.getName();

		if (path.startsWith(protocol + ':'))
		{
			String hexPath = path.substring((protocol + ':').length());
			VFS vfs = VFSManager.getVFSForPath(hexPath);

			return vfs.getFileName(hexPath);
		}
		else
		{
			VFS vfs = VFSManager.getVFSForPath(path);

			return vfs.getFileName(path);
		}
	}


	public String getParentOfPath(String path)
	{
		String protocol = this.getName();

		if (path.startsWith(protocol + ':'))
		{
			String hexPath = path.substring((protocol + ':').length());
			VFS vfs = VFSManager.getVFSForPath(hexPath);

			return protocol + ':' + vfs.getParentOfPath(hexPath);
		}
		else
		{
			VFS vfs = VFSManager.getVFSForPath(path);

			return vfs.getParentOfPath(path);
		}
	}


	public String constructPath(String parent, String path)
	{
		String protocol = this.getName();

		if (path.startsWith(protocol + ':'))
		{
			String hexPath = parent.substring((protocol + ':').length());
			VFS vfs = VFSManager.getVFSForPath(hexPath);

			return protocol + ':' + vfs.constructPath(hexPath, path);
		}
		else
		{
			VFS vfs = VFSManager.getVFSForPath(parent);

			return vfs.constructPath(parent, path);
		}
	}

	//{{{ _listFiles() method
	public VFSFile[] _listFiles(Object session, String directory,
				    Component comp)
		throws IOException
	{
		String protocol = getName();

		String hexPath = directory;
		if (directory.startsWith(protocol + ':'))
		{
			hexPath = hexPath.substring(protocol.length() + 1);
		}

		VFS vfs = VFSManager.getVFSForPath(hexPath);

		try
		{
			VFSFile[] vfsFiles =
				vfs._listFiles(session, hexPath, comp);

			if (vfsFiles == null)
			{
				return null;
			}

			VFSFile[] retVal = new VFSFile[vfsFiles.length];

			for (int i = 0; i < vfsFiles.length; i++)
			{
				retVal[i] = new VFSFile(
					vfsFiles[i].getName(),
					protocol + ':' + vfsFiles[i].getPath(),
					protocol + ':' + vfsFiles[i].getDeletePath(),
					vfsFiles[i].getType(),
					vfsFiles[i].getLength(),
					vfsFiles[i].isHidden()
				);
			}

			return retVal;
		}
		catch (IOException ioe)
		{
			Log.log(Log.ERROR, this, ioe);
		}

		return null;
	} //}}}

	//{{{ _getFile() method
	/**
	 * Returns the specified directory entry.
	 * @param session The session get it with {@link VFS#createVFSSession(String, Component)}
	 * @param path The path
	 * @param comp The component that will parent error dialog boxes
	 * @exception IOException if an I/O error occurred
	 * @return The specified directory entry, or null if it doesn't exist.
	 * @since jEdit 4.3pre2
	 */
	public VFSFile _getFile(Object session, String path,
		Component comp)
		throws IOException
	{
		String protocol = getName();

		String hexPath = path;
		if (path.startsWith(protocol + ':'))
		{
			hexPath = hexPath.substring(protocol.length() + 1);
		}

		VFS vfs = VFSManager.getVFSForPath(hexPath);

		try
		{
			VFSFile file =
				vfs._getFile(session, hexPath, comp);

			if (file == null)
			{
				return null;
			}

			return new VFSFile(
				file.getName(),
				protocol + ':' + file.getPath(),
				protocol + ':' + file.getDeletePath(),
				file.getType(),
				file.getLength(),
				file.isHidden()
			);
		}
		catch (IOException ioe)
		{
			Log.log(Log.ERROR, this, ioe);
		}

		return null;
	} //}}}

	public InputStream _createInputStream(Object session,
					      String path, boolean ignoreErrors, Component comp)
		throws IOException
	{
		String protocol = this.getName();

		String hexPath = path;
		if (path.startsWith(protocol + ':'))
		{
			hexPath = hexPath.substring(protocol.length() + 1);
		}

		VFS vfs = VFSManager.getVFSForPath(hexPath);

		if (hexPath.endsWith(".marks"))
		{
			// .marks not supported
			return null;
		}

		try
		{
			InputStream in = new HexInputStream(
				vfs._createInputStream(session, hexPath, ignoreErrors, comp)
			);

			return in;
		}
		catch (IOException ioe)
		{
			Log.log(Log.ERROR, this, ioe);
		}

		return null;
	}
}

