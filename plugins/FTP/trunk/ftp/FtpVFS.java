/*
 * FtpVFS.java - Ftp VFS
 * Copyright (C) 2000, 2001, 2002 Slava Pestov
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

import com.fooware.net.*;
import gnu.regexp.*;
import java.awt.Component;
import java.io.*;
import java.net.*;
import java.util.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

/**
 * FTP VFS.
 * @author Slava Pestov
 * @version $Id$
 */
public class FtpVFS extends VFS
{
	public static final String PROTOCOL = "ftp";

	// same as File VFS permissions key!
	public static final String PERMISSIONS_PROPERTY = "FileVFS__perms";

	public FtpVFS()
	{
		super("ftp",READ_CAP | WRITE_CAP | BROWSE_CAP | DELETE_CAP
			| RENAME_CAP | MKDIR_CAP);

		unixRegexps = new UncheckedRE[jEdit.getIntegerProperty(
			"vfs.ftp.list.count",-1)];
		for(int i = 0; i < unixRegexps.length; i++)
		{
			unixRegexps[i] = new UncheckedRE(jEdit.getProperty(
				"vfs.ftp.list." + i),0,
				RESearchMatcher.RE_SYNTAX_JEDIT);
		}

		dosRegexp = new UncheckedRE(jEdit.getProperty(
			"vfs.ftp.list.dos"),0,
			RESearchMatcher.RE_SYNTAX_JEDIT);

		vmsRegexp = new UncheckedRE(jEdit.getProperty(
			"vfs.ftp.list.vms"),0,
			RESearchMatcher.RE_SYNTAX_JEDIT);
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
				path == null ? null : new FtpAddress(path),false);
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

			//Use ASCII mode for dir listing
			session.client.representationType(FtpClient.ASCII_TYPE);

			//CWD into the directory - Doing a LIST on a path with spaces in the
			//name fails; however, if you CWD to the dir and then LIST it
			// succeeds.
			session.client.changeWorkingDirectory(address.path);
			//Check for successful response
			FtpResponse response = session.client.getResponse();
			if(response != null
				&& response.getReturnCode() != null
				&& response.getReturnCode().charAt(0) != '2')
			{
				throw new FtpException(response);
			}

			// some servers might not support -a, so if we get an error
			// try without -a
			Vector directoryVector = _listDirectory(session.client,url,comp,true);
			if(directoryVector == null || directoryVector.size() == 0)
				directoryVector = _listDirectory(session.client,url,comp,false);

			if(directoryVector == null)
			{
				// error occurred
				return null;
			}

			for(int i = 0; i < directoryVector.size(); i++)
			{
				FtpDirectoryEntry entry = (FtpDirectoryEntry)
					directoryVector.elementAt(i);
				if(entry.type == FtpDirectoryEntry.LINK)
					resolveSymlink(session,url,entry,comp);
				else
				{
					// prepend directory to create full path
					entry.path = constructPath(url,entry.name);
					entry.deletePath = entry.path;
				}
			}

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

			if(directoryEntry.type == VFS.DirectoryEntry.FILE)
				session.client.delete(address.path);
			else if(directoryEntry.type == VFS.DirectoryEntry.DIRECTORY)
				session.client.removeDirectory(address.path);

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

			directoryEntry = _getDirectoryEntry(session,to,comp);
			if(directoryEntry != null && directoryEntry.type == VFS.DirectoryEntry.FILE
				&& !address.path.equalsIgnoreCase(toPath))
				session.client.delete(toPath);

			session.client.renameFrom(address.path);
			session.client.renameTo(toPath);

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

			session.client.makeDirectory(address.path);

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

			//Use ASCII mode for dir listing
			//client.representationType(com.fooware.net.FtpClient.ASCII_TYPE);

			//CWD into the directory - Doing a LIST on a path with spaces in the
			//name fails; however, if you CWD to the dir and then LIST it
			// succeeds.

			//First we get the parent path of the file passed to us in the path
			// field. We use the MiscUtilities.getParentOfPath as opposed to our own
			//because the path here is not a URL and our own version expects a URL
			// when it instantiates an FtpAddress object.
			String parentPath = MiscUtilities.getParentOfPath(address.path);

			session.client.changeWorkingDirectory(parentPath);
			//Check for successful response
			FtpResponse response = session.client.getResponse();
			if(response != null
				&& response.getReturnCode() != null
				&& response.getReturnCode().charAt(0) != '2')
			{
				throw new FtpException(response);
			}

			_setupSocket(session.client);
			//Here we do a LIST for on the specific file
			//Since we are in the right dir, we list only the filename, not the
			// whole path...
			Reader _reader = session.client.list(address.path.substring(parentPath.length()));
			if(_reader == null)
			{
				// eg, file not found
				return null;
			}

			BufferedReader reader = new BufferedReader(_reader);
			String line = reader.readLine();
			reader.close();
			if(line != null)
			{
				while(line.length() == 0)
				{
					line = reader.readLine();
					if(line == null)
						return null;
				}

				FtpDirectoryEntry dirEntry = lineToDirectoryEntry(line);
				if(dirEntry == null)
				{
					// ok, this really sucks.
					// we were asked to get the directory
					// entry for a directory. This stupid
					// implementation will only work for
					// the resolveSymlink() method. A proper
					// version will be written some other time.
					return new FtpDirectoryEntry(null,null,null,
						VFS.DirectoryEntry.DIRECTORY,0L,false,0);
				}
				else
				{
					if(dirEntry.type == FtpDirectoryEntry.LINK)
					{
						resolveSymlink(session,getParentOfPath(path),
							dirEntry,comp);
					}

					// since _getDirectoryEntry() is always called
					// before the file is loaded (this is undocumented,
					// but true, because BufferIORequest needs to know
					// the size of the file being loaded) we can
					// check if the path name in the session is the
					// path this method was passed (the path in the
					// session will always be the file loaded or
					// saved, yet again another semi-documented
					// feature).
					/* if(address.path.equals(session.path))
					{
						Buffer buffer = jEdit.getBuffer(
							/ not address.path, but
							   full URI /path);
						if(buffer != null)
						{
							Log.log(Log.DEBUG,this,
								path
								+ " has permissions 0"
								+ Integer.toString(
								dirEntry.permissions,8));

							buffer.setIntegerProperty(PERMISSIONS_PROPERTY,
								dirEntry.permissions);
						}
					} */

					return dirEntry;
				}
			}
			else
				return null;
		}
		catch(FtpException e)
		{
			// if being called from resolveSymlink()
			if(doNotRelease)
				return null;
			else
				throw e;
		}
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

			_setupSocket(session.client);
			InputStream in = session.client.retrieveStream(address.path);

			if(in == null)
				throw new FtpException(session.client.getResponse());

			return in;
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

			_setupSocket(session.client);
			OutputStream out = session.client.storeStream(address.path);

			if(out == null)
				throw new FtpException(session.client.getResponse());

			// commented out for now, because updating VFS browsers
			// every time file is saved gets annoying
			//VFSManager.sendVFSUpdate(this,path,true);

			DirectoryCache.clearCachedDirectory(getParentOfPath(path));

			return out;
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
				String cmd = "CHMOD " + Integer.toString(permissions,8)
					+ " " + address.path;
				session.client.siteParameters(cmd);
			}
		}
		finally
		{
			ConnectionManager.releaseConnection(session);
		}
	}

	// private members
	private UncheckedRE[] unixRegexps;
	private UncheckedRE dosRegexp;
	private UncheckedRE vmsRegexp;

	private static void _setupSocket(FtpClient client)
		throws IOException
	{
		if(jEdit.getBooleanProperty("vfs.ftp.passive"))
			client.passive();
		else
			client.dataPort();

		// See if we should use Binary mode to transfer files.
		if (jEdit.getBooleanProperty("vfs.ftp.binary"))
		{
			//Go with Binary
            		client.representationType(com.fooware.net.FtpClient.IMAGE_TYPE);
        	}
		else
		{
			//Stick to ASCII - let the line endings get converted
			client.representationType(com.fooware.net.FtpClient.ASCII_TYPE);
		}
	}

	private Vector _listDirectory(FtpClient client, String url, Component comp,
		boolean tryHiddenFiles) throws IOException
	{
		BufferedReader in = null;

		try
		{
			Vector directoryVector = new Vector();

			_setupSocket(client);
			Reader _in = (tryHiddenFiles ? client.list("-a") : client.list());

			if(_in == null)
			{
				if(!tryHiddenFiles)
					throw new FtpException(client.getResponse());
			}

			in = new BufferedReader(_in);
			String line;
			while((line = in.readLine()) != null)
			{
				if(line.length() == 0)
					continue;

				VFS.DirectoryEntry entry = lineToDirectoryEntry(line);
				if(entry == null
					|| entry.name.equals(".")
					|| entry.name.equals(".."))
				{
					Log.log(Log.DEBUG,this,"Discarding " + line);
					continue;
				}
				else
					; //Log.log(Log.DEBUG,this,"Parsed " + line);
	
				directoryVector.addElement(entry);
			}

			return directoryVector;
		}
		finally
		{
			if(in != null)
			{
				try
				{
					in.close();
				}
				catch(Exception e)
				{
					Log.log(Log.ERROR,this,e);
				}
			}
		}
	}

	// Convert a line of LIST output to an FtpDirectoryEntry
	private FtpDirectoryEntry lineToDirectoryEntry(String line)
	{
		try
		{
			// we use one of several regexps to obtain
			// the file name, type, and size
			int type = VFS.DirectoryEntry.FILE;
			String name = null;
			long length = 0L;
			int permissions = 0;

			boolean ok = false;

			for(int i = 0; i < unixRegexps.length; i++)
			{
				UncheckedRE regexp = unixRegexps[i];
				REMatch match;
				if((match = regexp.getMatch(line)) != null)
				{
					switch(line.charAt(0))
					{
					case 'd':
						type = VFS.DirectoryEntry.DIRECTORY;
						break;
					case 'l':
						type = FtpDirectoryEntry.LINK;
						break;
					case '-':
						type = VFS.DirectoryEntry.FILE;
						break;
					}

					permissions = parsePermissions(match.toString(1));

					try
					{
						length = Long.parseLong(match.toString(2));
					}
					catch(NumberFormatException nf)
					{
						length = 0L;
					}

					name = match.toString(3);
					ok = true;
					break;
				}
			}

			if(!ok)
			{
				REMatch match;
				if((match = dosRegexp.getMatch(line)) != null)
				{
					try
					{
						String sizeStr = match.toString(1);
						if(sizeStr.equals("<DIR>"))
							type = VFS.DirectoryEntry.DIRECTORY;
						else
							length = Long.parseLong(sizeStr);
					}
					catch(NumberFormatException nf)
					{
						length = 0L;
					}

					name = match.toString(2);
					ok = true;
				}
			}

			if(!ok)
			{
				REMatch match;
				if((match = vmsRegexp.getMatch(line)) != null)
				{
					name = match.toString(1);
					if(name.endsWith(".DIR"))
						type = VFS.DirectoryEntry.DIRECTORY;
					ok = true;
				}
			}

			if(!ok)
				return null;

			// path is null; it will be created later, by _listDirectory()
			return new FtpDirectoryEntry(name,null,null,type,
				length,name.charAt(0) == '.' /* isHidden */,
				permissions);
		}
		catch(Exception e)
		{
			Log.log(Log.NOTICE,this,"lineToDirectoryEntry("
				+ line + ") failed:");
			Log.log(Log.NOTICE,this,e);
			return null;
		}
	}

	private void resolveSymlink(Object _session,
		String dir, FtpDirectoryEntry entry, Component comp)
		throws IOException
	{
		String name = entry.name;
		int index = name.indexOf(" -> ");
		
		if(index == -1)
		{
			//non-standard link representation. Treat as a file
			//Some Mac and NT based servers do not use the "->" for symlinks
			entry.path = constructPath(dir,name);
			entry.type = VFS.DirectoryEntry.FILE;
			Log.log(Log.NOTICE,this,"File '"
				+ name
				+ "' is listed as a link, but will be treated"
				+ " as a file because no '->' was found.");
			return;
		}
		String link = name.substring(index + " -> ".length());
		link = constructPath(dir,link);
		FtpDirectoryEntry linkDirEntry = (FtpDirectoryEntry)
			_getDirectoryEntry(_session,link,comp);
		if(linkDirEntry == null)
			entry.type = VFS.DirectoryEntry.FILE;
		else
		{
			entry.type = linkDirEntry.type;
			entry.permissions = linkDirEntry.permissions;
		}

		entry.name = name.substring(0,index);
		entry.path = link;
		entry.deletePath = constructPath(dir,entry.name);
	}

	private int parsePermissions(String s)
	{
		if(s.length() != 9)
			return 0;

		int permissions = 0;

		if(s.charAt(0) == 'r')
			permissions += 0400;
		if(s.charAt(1) == 'w')
			permissions += 0200;
		if(s.charAt(2) == 'x')
			permissions += 0100;
		else if(s.charAt(2) == 's')
			permissions += 04100;
		else if(s.charAt(2) == 'S')
			permissions += 04000;
		if(s.charAt(3) == 'r')
			permissions += 040;
		if(s.charAt(4) == 'w')
			permissions += 020;
		if(s.charAt(5) == 'x')
			permissions += 010;
		else if(s.charAt(5) == 's')
			permissions += 02010;
		else if(s.charAt(5) == 'S')
			permissions += 02000;
		if(s.charAt(6) == 'r')
			permissions += 04;
		if(s.charAt(7) == 'w')
			permissions += 02;
		if(s.charAt(8) == 'x')
			permissions += 01;
		else if(s.charAt(8) == 't')
			permissions += 01001;
		else if(s.charAt(8) == 'T')
			permissions += 01000;

		return permissions;
	}
}
