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
package projectviewer;

import java.io.IOException;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;


/**
 * Class with helper functions for dealing with jEdit's VFS.
 *
 * @version $Id$
 * @since PV 3.0.0
 */
public class VFSHelper
{

	/**
	 * Creates a VFS session. Makes sure the VFS method for creating
	 * a session is called from the AWT thread.
	 *
	 * @param vfs The VFS instance.
	 * @param path The path for which a session is being created.
	 */
	public static Object createVFSSession(VFS vfs, String path)
	{
		return createVFSSession(vfs, path, jEdit.getActiveView());
	}


	/**
	 * Creates a VFS session. Makes sure the VFS method for creating
	 * a session is called from the AWT thread.
	 *
	 * @param vfs The VFS instance.
	 * @param path The path for which a session is being created.
	 * @param v View for error reporting.
	 */
	public static Object createVFSSession(VFS vfs, String path, View v)
	{
		VFSSessionWrapper wrapper = new VFSSessionWrapper();
		wrapper.vfs = vfs;
		wrapper.path = path;
		wrapper.view = v;
		wrapper.create = true;
		PVActions.swingInvoke(wrapper);
		return wrapper.session;
	}

	/**
	 * Ends a VFS session, logging any exceptions.
	 *
	 * @param vfs The VFS instance.
	 * @param session The session to end.
	 * @param v A jEdit view.
	 */
	public static void endVFSSession(VFS vfs, Object session, View v)
	{
		VFSSessionWrapper wrapper = new VFSSessionWrapper();
		wrapper.vfs = vfs;
		wrapper.session = session;
		wrapper.view = (v != null) ? v : jEdit.getActiveView();
		wrapper.create = false;
		PVActions.swingInvoke(wrapper);
	}


	/**
	 * Deletes a file.
	 *
	 * @param url The URL for the file to delete.
	 */
	public static void deleteFile(String url) throws IOException
	{
		View view = jEdit.getActiveView();
		VFS vfs = VFSManager.getVFSForPath(url);
		Object session = createVFSSession(vfs, url, view);
		vfs._delete(session, url, view);
		endVFSSession(vfs, session, view);
	}


	/**
	 * Creates a VFSFile instance for the given URL.
	 */
	public static VFSFile getFile(String url) throws IOException
	{
		View view = jEdit.getActiveView();
		VFS vfs = VFSManager.getVFSForPath(url);
		Object session = createVFSSession(vfs, url, view);
		if (session == null)
			return null;
		try {
			VFSFile file = vfs._getFile(session, url, view);
			endVFSSession(vfs, session, view);
			return file;
		} catch (Exception e) {
			Log.log(Log.ERROR, vfs, "VFS error getting file.", e);
			return null;
		}
	}


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
	 * Returns whether a VFS path exists.
	 *
	 * @param path Path to check.
	 */
	public static boolean pathExists(String path)
	{
		VFS vfs;

		vfs = VFSManager.getVFSForPath(path);
		if (vfs != null) {
			View view;
			VFSFile entry = null;
			Object session;

			view = jEdit.getActiveView();
			session = createVFSSession(vfs, path, view);

			try {
				entry = vfs._getFile(session, path, view);
			} catch (IOException ioe) {
				Log.log(Log.ERROR, PVActions.class, "Error calling into VFS", ioe);
			} finally {
				endVFSSession(vfs, session, view);
			}

			return entry != null;
		}

		return false;
	}



	/** Used internally to create and end VFS sessions. */
	private static class VFSSessionWrapper implements Runnable
	{
		public Object session;
		public VFS vfs;
		public String path;
		public View view;
		public boolean create;

		public void run() {
			if (create) {
				session = vfs.createVFSSession(path, view);
			} else {
				try {
					vfs._endVFSSession(session, view);
				} catch (IOException ioe) {
					Log.log(Log.ERROR, PVActions.class,
							"Error ending VFS session", ioe);
				}
			}
		}
	}

}

