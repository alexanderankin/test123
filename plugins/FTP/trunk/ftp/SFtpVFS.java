/*
 * SFtpVFS.java - Secure Ftp VFS
 * Copyright (C) 2002 Slava Pestov
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

package ftp;

import java.awt.Component;
import java.io.*;
import java.net.*;
import java.util.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

/**
 * SFTP VFS.
 * @author Slava Pestov
 * @version $Id$
 */
public class SFtpVFS extends VFS
{
	public static final String PROTOCOL = "sftp";

	// same as File VFS permissions key!
	public static final String PERMISSIONS_PROPERTY = "FileVFS__perms";

	public SFtpVFS()
	{
		super("sftp",READ_CAP | WRITE_CAP | BROWSE_CAP | DELETE_CAP
			| RENAME_CAP | MKDIR_CAP);
	}

	public String showBrowseDialog(Object[] session, Component comp)
	{
		ConnectionManager.ConnectionInfo newSession =
			(ConnectionManager.ConnectionInfo)createVFSSession(
			null,comp);
		if(newSession == null)
			return null;

		if(session != null)
			session[0] = newSession;

		// need trailing / for 'open from ftp server' and 'save to ftp
		// server' commands to detect that the path is in fact a
		// directory
		return PROTOCOL + "://" + newSession.user
			+ "@" + newSession.host
			+ (newSession.port == 21
			? "" : ":" + newSession.port) + "/~/";
	}

	public String getFileName(String path)
	{
		FtpAddress address = new FtpAddress(path);
		if(address.path.equals("/") || address.path.length() == 1)
		{
			address.path = "";
			return address.toString();
		}
		else
			return super.getFileName(address.path);
	}

	public String getParentOfPath(String path)
	{
		FtpAddress address = new FtpAddress(path);
		address.path = super.getParentOfPath(address.path);
		return address.toString();
	}

	public String constructPath(String parent, String path)
	{
		if(path.startsWith("~"))
			path = "/" + path;

		if(path.startsWith("/"))
		{
			FtpAddress address = new FtpAddress(parent);
			address.path = path;
			return address.toString();
		}
		else if(parent.endsWith("/"))
			return parent + path;
		else
			return parent + '/' + path;
	}

	public void reloadDirectory(String path)
	{
		DirectoryCache.clearCachedDirectory(path);
	}

	public Object createVFSSession(String path, Component comp)
	{
		try
		{
			return ConnectionManager.getConnectionInfo(comp,
				path == null ? null : new FtpAddress(path),true);
		}
		catch(IllegalArgumentException ia)
		{
			// FtpAddress.<init> can throw this
			return null;
		}
	}

	public String _canonPath(Object _session, String path, Component comp)
		throws IOException
	{
		FtpAddress address = new FtpAddress(path);

		if(address.path.startsWith("/~"))
		{
			ConnectionManager.Connection session
				= ConnectionManager.getConnection(
				(ConnectionManager.ConnectionInfo)_session);

			try
			{
				if(session.home != null)
				{
					if(session.home.endsWith("/"))
						address.path = session.home + address.path.substring(2);
					else
						address.path = session.home + '/' + address.path.substring(2);
					if(address.path.endsWith("/") && address.path.length() != 1)
					{
						address.path = address.path.substring(0,
						address.path.length() - 1);
					}
				}
			}
			finally
			{
				ConnectionManager.releaseConnection(session);
			}
		}

		return address.toString();
	}

	public VFS.DirectoryEntry[] _listDirectory(Object _session, String url,
		Component comp) throws IOException
	{
		VFS.DirectoryEntry[] directory = DirectoryCache.getCachedDirectory(url);
		if(directory != null)
			return directory;

		ConnectionManager.Connection session
			= ConnectionManager.getConnection(
			(ConnectionManager.ConnectionInfo)_session);

		try
		{
			FtpAddress address = new FtpAddress(url);

			// XXX
			Vector directoryVector = new Vector();

			directory = new FtpDirectoryEntry[directoryVector.size()];
			directoryVector.copyInto(directory);
			DirectoryCache.setCachedDirectory(url,directory);
			return directory;
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}

	public boolean _delete(Object _session, String url, Component comp)
		throws IOException
	{
		ConnectionManager.Connection session
			= ConnectionManager.getConnection(
			(ConnectionManager.ConnectionInfo)_session);

		try
		{
			FtpAddress address = new FtpAddress(url);

			VFS.DirectoryEntry directoryEntry = _getDirectoryEntry(
				session,url,comp);
			if(directoryEntry == null)
				return false;

			// XXX

			DirectoryCache.clearCachedDirectory(getParentOfPath(url));
			VFSManager.sendVFSUpdate(this,url,true);

			return session.client.getResponse().isPositiveCompletion();
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}

	public boolean _rename(Object _session, String from, String to,
		Component comp) throws IOException
	{
		ConnectionManager.Connection session
			= ConnectionManager.getConnection(
			(ConnectionManager.ConnectionInfo)_session);

		try
		{
			FtpAddress address = new FtpAddress(from);

			String toPath = new FtpAddress(to).path;

			VFS.DirectoryEntry directoryEntry = _getDirectoryEntry(
				session,from,comp);
			if(directoryEntry == null)
				return false;

			// XXX

			DirectoryCache.clearCachedDirectory(getParentOfPath(from));
			DirectoryCache.clearCachedDirectory(getParentOfPath(to));
			VFSManager.sendVFSUpdate(this,from,true);
			VFSManager.sendVFSUpdate(this,to,true);

			return session.client.getResponse().isPositiveCompletion();
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}

	public boolean _mkdir(Object _session, String directory, Component comp)
		throws IOException
	{
		ConnectionManager.Connection session
			= ConnectionManager.getConnection(
			(ConnectionManager.ConnectionInfo)_session);

		try
		{
			FtpAddress address = new FtpAddress(directory);

			// XXX

			DirectoryCache.clearCachedDirectory(getParentOfPath(directory));
			VFSManager.sendVFSUpdate(this,directory,true);

			return session.client.getResponse().isPositiveCompletion();
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}

	// Silly hack
	static class FtpDirectoryEntry extends VFS.DirectoryEntry
	{
		public static final int LINK = 10;
		int permissions;

		public FtpDirectoryEntry(String name, String path, String deletePath,
			int type, long length, boolean hidden, int permissions)
		{
			super(name,path,deletePath,type,length,hidden);
			this.permissions = permissions;
		}
	}

	// this method is severely broken, and in many cases, most fields
	// of the returned directory entry will not be filled in.
	public VFS.DirectoryEntry _getDirectoryEntry(Object _session, String path,
		Component comp)
		throws IOException
	{
		boolean doNotRelease;
		ConnectionManager.Connection session;
		if(_session instanceof ConnectionManager.Connection)
		{
			doNotRelease = true;
			session = (ConnectionManager.Connection)_session;
		}
		else
		{
			doNotRelease = false;
			session = ConnectionManager.getConnection(
				(ConnectionManager.ConnectionInfo)_session);
		}

		try
		{
			FtpAddress address = new FtpAddress(path);

			//XXX
			return null;
		}
		/* catch(FtpException e)
		{
			// if being called from resolveSymlink()
			if(doNotRelease)
				return null;
			else
				throw e;
		} */
		finally
		{
			if(!doNotRelease)
				ConnectionManager.releaseConnection(session);
		}
	}

	public InputStream _createInputStream(Object _session, String path,
		boolean ignoreErrors, Component comp) throws IOException
	{
		ConnectionManager.Connection session
			= ConnectionManager.getConnection(
			(ConnectionManager.ConnectionInfo)_session);

		try
		{
			FtpAddress address = new FtpAddress(path);

			// XXX
			return null;
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}

	public OutputStream _createOutputStream(Object _session, String path,
		Component comp) throws IOException
	{
		ConnectionManager.Connection session
			= ConnectionManager.getConnection(
			(ConnectionManager.ConnectionInfo)_session);

		try
		{
			FtpAddress address = new FtpAddress(path);

			DirectoryCache.clearCachedDirectory(getParentOfPath(path));

			// XXX
			return null;
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}

	public void _saveComplete(Object _session, Buffer buffer, Component comp)
		throws IOException
	{
		ConnectionManager.Connection session
			= ConnectionManager.getConnection(
			(ConnectionManager.ConnectionInfo)_session);

		try
		{
			FtpAddress address = new FtpAddress(buffer.getPath());

			int permissions = buffer.getIntegerProperty(PERMISSIONS_PROPERTY,0);
			if(permissions != 0)
			{
				// XXX
			}
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}
}
