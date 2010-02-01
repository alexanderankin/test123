/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package launcher;

import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;


/**
 * Class with helper functions for dealing with jEdit's VFS.
 *
 * @version $Id: VFSHelper.java 16366 2009-10-20 07:26:31Z vanza $
 * @since PV 3.0.0
 */
public class VFSHelper
{

	/**
	 * Returns whether the path references a local file.
	 *
	 * @param url VFS URL to check.
	 */
	public static boolean isLocal(String url)
	{
		return (VFSManager.getVFSForPath(url) == VFSManager.getFileVFS());
	}

	/**
	 * Returns whether a given VFSFile instance references a local file.
	 *
	 * @param vfsFile VFSFile instance to check.
	 */
	public static boolean isLocal(VFSFile vfsFile)
	{
		return (vfsFile.getVFS() == VFSManager.getFileVFS());
	}

	/**
	 * Returns whether the path references a URL.
	 *
	 * @param url VFS URL to check.
	 */
	public static boolean isURL(String url)
	{
		return (VFSManager.getVFSForPath(url) == VFSManager.getUrlVFS());
	}

	/**
	 * Returns whether a given VFSFile instance references a URL.
	 *
	 * @param vfsFile VFSFile instance to check.
	 */
	public static boolean isURL(VFSFile vfsFile)
	{
		return (vfsFile.getVFS() == VFSManager.getUrlVFS());
	}

}

