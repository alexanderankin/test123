/*
 * FtpVFS.java - Ftp VFS
 * Copyright (C) 2000, 2001 Slava Pestov
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

	public static final String CLIENT_KEY = "FtpVFS.client";

	public FtpVFS()
	{
		super("ftp");

		regexps = new RE[Integer.parseInt(jEdit.getProperty(
			"vfs.ftp.list.count"))];
		for(int i = 0; i < regexps.length; i++)
		{
			try
			{
				regexps[i] = new RE(jEdit.getProperty(
					"vfs.ftp.list." + i),0,
					RESearchMatcher.RE_SYNTAX_JEDIT);
			}
			catch(REException re)
			{
				Log.log(Log.ERROR,this,re);
			}
		}
	}

	public int getCapabilities()
	{
		return READ_CAP | WRITE_CAP | BROWSE_CAP | DELETE_CAP
			| RENAME_CAP | MKDIR_CAP;
	}

	public String showBrowseDialog(Object[] session, Component comp)
	{
		FtpSession newSession = (FtpSession)createVFSSession(null,comp);
		if(newSession == null)
			return null;

		if(session != null)
			session[0] = newSession;

		return PROTOCOL + "://" + newSession.user
			+ "@" + newSession.host + "/";
	}

	public String getParentOfPath(String path)
	{
		FtpAddress address = new FtpAddress(path);
		address.path = MiscUtilities.getParentOfPath(address.path);
		return address.toString();
	}

	public String constructPath(String parent, String path)
	{
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

	public Object createVFSSession(String path, Component comp)
	{
		FtpSession session = new FtpSession();

		try
		{
			if(path != null)
			{
				FtpAddress address = new FtpAddress(path);
				session.host = address.host;
				session.user = address.user;
			}

			if(FtpPlugin.showLoginDialog(session,comp))
				return session;
			else
				return null;
		}
		catch(IllegalArgumentException ia)
		{
			// FtpAddress.<init> can throw this
			return null;
		}
	}

	public VFS.DirectoryEntry[] _listDirectory(Object _session, String url,
		Component comp) throws IOException
	{
		FtpSession session = (FtpSession)_session;

		VFS.DirectoryEntry[] directory = DirectoryCache.getCachedDirectory(url);
		if(directory != null)
			return directory;

		FtpAddress address = new FtpAddress(url);

		BufferedReader in = null;

		FtpClient client = _getFtpClient(session,address,false,comp);
		if(client == null)
			return null;

		try
		{
			Vector directoryVector = new Vector();

			//Use ASCII mode for dir listing
			client.representationType(com.fooware.net.FtpClient.ASCII_TYPE);

			//CWD into the directory - Doing a LIST on a path with spaces in the
			//name fails; however, if you CWD to the dir and then LIST it
			// succeeds.
			client.changeWorkingDirectory(address.path);
			//Check for successful response
			FtpResponse response = client.getResponse();
			if(response != null
				&& response.getReturnCode() != null
				&& response.getReturnCode().charAt(0) != '2')
			{
				String[] args = { url, response.toString() };
				VFSManager.error(comp,"vfs.ftp.list-error",args);
				return null;
			}

			_setupSocket(client);
			Reader _in = client.list(); //we are in the right dir so just send LIST

			if(_in == null)
			{
				String[] args = { url, client.getResponse().toString() };
				VFSManager.error(comp,"vfs.ftp.list-error",args);
				return null;
			}

			in = new BufferedReader(_in);
			String line;
			while((line = in.readLine()) != null)
			{
				VFS.DirectoryEntry entry = lineToDirectoryEntry(line);
				if(entry == null
					|| entry.name.equals(".")
					|| entry.name.equals(".."))
				{
					//Log.log(Log.DEBUG,this,"Discarding " + line);
					continue;
				}
				else
					; //Log.log(Log.DEBUG,this,"Parsed " + line);

				directoryVector.addElement(entry);
			}

			in.close();

			for(int i = 0; i < directoryVector.size(); i++)
			{
				VFS.DirectoryEntry entry = (VFS.DirectoryEntry)
					directoryVector.elementAt(i);
				if(entry.type == __LINK)
					resolveSymlink(session,url,entry,comp);
				else
				{
					// prepend directory to create full path
					entry.path = constructPath(url,entry.name);
					entry.deletePath = entry.path;
				}
			}

			directory = new VFS.DirectoryEntry[directoryVector.size()];
			directoryVector.copyInto(directory);
			DirectoryCache.setCachedDirectory(url,directory);
			return directory;
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

	public boolean _delete(Object _session, String url, Component comp)
		throws IOException
	{
		FtpSession session = (FtpSession)_session;

		FtpAddress address = new FtpAddress(url);
		FtpClient client = _getFtpClient(session,address,true,comp);
		if(client == null)
			return false;

		VFS.DirectoryEntry directoryEntry = _getDirectoryEntry(
			session,url,comp);
		if(directoryEntry == null)
			return false;

		if(directoryEntry.type == VFS.DirectoryEntry.FILE)
			client.delete(address.path);
		else if(directoryEntry.type == VFS.DirectoryEntry.DIRECTORY)
			client.removeDirectory(address.path);

		VFSManager.sendVFSUpdate(this,url,true);

		return client.getResponse().isPositiveCompletion();
	}

	public boolean _rename(Object _session, String from, String to,
		Component comp) throws IOException
	{
		FtpSession session = (FtpSession)_session;

		FtpAddress address = new FtpAddress(from);
		FtpClient client = _getFtpClient(session,address,true,comp);
		if(client == null)
			return false;

		VFS.DirectoryEntry directoryEntry = _getDirectoryEntry(
			session,from,comp);
		if(directoryEntry == null)
			return false;

		client.renameFrom(address.path);
		client.renameTo(new FtpAddress(to).path);

		VFSManager.sendVFSUpdate(this,from,true);

		return client.getResponse().isPositiveCompletion();
	}

	public boolean _mkdir(Object _session, String directory, Component comp)
		throws IOException
	{
		FtpSession session = (FtpSession)_session;

		FtpAddress address = new FtpAddress(directory);
		FtpClient client = _getFtpClient(session,address,true,comp);
		if(client == null)
			return false;

		client.makeDirectory(address.path);

		VFSManager.sendVFSUpdate(this,directory,true);

		return client.getResponse().isPositiveCompletion();
	}

	// this method is severely broken, and in many cases, most fields
	// of the returned directory entry will not be filled in.
	public VFS.DirectoryEntry _getDirectoryEntry(Object _session, String path,
		Component comp)
		throws IOException
	{
		FtpSession session = (FtpSession)_session;

		FtpAddress address = new FtpAddress(path);
		FtpClient client = _getFtpClient(session,address,true,comp);
		if(client == null)
			return null;

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
		
		client.changeWorkingDirectory(parentPath);
		//Check for successful response
		FtpResponse response = client.getResponse();
		if(response != null
			&& response.getReturnCode() != null
			&& response.getReturnCode().charAt(0) != '2')
		{
			String[] args = { path, response.toString() };
			VFSManager.error(comp,"vfs.ftp.list-error",args);
			return null;
		}
			
		_setupSocket(client);
		//Here we do a LIST for on the specific file
		//Since we are in the right dir, we list only the filename, not the
		// whole path...
		Reader _reader = client.list(address.path.substring(parentPath.length()));
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
			if(line.startsWith("total"))
			{
				// ok, this really sucks.
				// we were asked to get the directory
				// entry for a directory. This stupid
				// implementation will only work for
				// the resolveSymlink() method. A proper
				// version will be written some other time.
				return new VFS.DirectoryEntry(null,null,null,
					VFS.DirectoryEntry.DIRECTORY,0L,false);
			}
			else
			{
				VFS.DirectoryEntry dirEntry = lineToDirectoryEntry(line);
				if(dirEntry == null)
					return null;
				else if(dirEntry.type == __LINK)
					resolveSymlink(session,getParentOfPath(path),
						dirEntry,comp);
				return dirEntry;
			}
		}
		else
			return null;
	}

	public InputStream _createInputStream(Object _session, String path,
		boolean ignoreErrors, Component comp) throws IOException
	{
		FtpSession session = (FtpSession)_session;

		FtpAddress address = new FtpAddress(path);
		FtpClient client = _getFtpClient(session,address,ignoreErrors,comp);
		if(client == null)
			return null;
	
		_setupSocket(client);
		InputStream in = client.retrieveStream(address.path);

		if(in == null)
		{
			if(!ignoreErrors)
			{
				String[] args = { address.host, address.port, address.path,
					client.getResponse().toString() };
				VFSManager.error(comp,"vfs.ftp.download-error",args);
			}
		}

		return in;
	}

	public OutputStream _createOutputStream(Object _session, String path,
		Component comp) throws IOException
	{
		FtpSession session = (FtpSession)_session;

		FtpAddress address = new FtpAddress(path);
		FtpClient client = _getFtpClient(session,address,false,comp);
		if(client == null)
			return null;
		
		_setupSocket(client);
		OutputStream out = client.storeStream(address.path);

		if(out == null)
		{
			String[] args = { address.host, address.port, address.path,
				client.getResponse().toString() };
			VFSManager.error(comp,"vfs.ftp.upload-error",args);
		}

		// commented out for now, because updating VFS browsers
		// every time file is saved gets annoying
		//VFSManager.sendVFSUpdate(this,path,true);

		DirectoryCache.clearCachedDirectory(getParentOfPath(path));

		return out;
	}

	public void _endVFSSession(Object _session, Component comp)
		throws IOException
	{
		FtpSession session = (FtpSession)_session;

		try
		{
			if(session.client != null)
				session.client.logout();
		}
		finally
		{
			// even if we are aborted...
			session.client = null;
		}

		super._endVFSSession(session,comp);
	}

	// private members
	private static final int __LINK = 10;
	private RE[] regexps;

	private FtpClient _getFtpClient(FtpSession session, FtpAddress address,
		boolean ignoreErrors, Component comp)
	{
		if(session.client == null)
		{
			if(address.user == null)
				address.user = session.user;

			session.client = _createFtpClient(address.host,address.port,
				address.user,session.password,ignoreErrors,comp);
		}

		return session.client;
	}

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

	private static FtpClient _createFtpClient(String host, String port,
		String user, String password, boolean ignoreErrors,
		Component comp)
	{
		FtpClient client = new FtpClient();

		try
		{
			Log.log(Log.DEBUG,FtpVFS.class,"Connecting to " + host + ":" + port);
			client.connect(host,Integer.parseInt(port));
			if(!client.getResponse().isPositiveCompletion())
			{
				if(!ignoreErrors)
				{
					String[] args = { host,port, client.getResponse().toString() };
					VFSManager.error(comp,"vfs.ftp.connect-error",args);
				}
				return null;
			}

			client.userName(user);

			if(client.getResponse().isPositiveIntermediary())
			{
				client.password(password);

				if(!client.getResponse().isPositiveCompletion())
				{
					if(!ignoreErrors)
					{
						String[] args = { host, port, user,
							client.getResponse().toString() };
						VFSManager.error(comp,"vfs.ftp.login-error",args);
					}
					client.logout();
					return null;
				}
			}
			else if(client.getResponse().isPositiveCompletion())
			{
				// do nothing, server let us in without
				// a password
			}
			else
			{
				if(!ignoreErrors)
				{
					String[] args = { host, port, user,
						client.getResponse().toString() };
					VFSManager.error(comp,"vfs.ftp.login-error",args);
				}
				client.logout();
				return null;
			}

			return client;
		}
		catch(SocketException se)
		{
			if(ignoreErrors)
				return null;

			String response;
			if(client != null && client.getResponse() != null)
				response = String.valueOf(client.getResponse());
			else
				response = "";
			String[] args = { host, port, response };
			VFSManager.error(comp,"vfs.ftp.connect-error",args);
			return null;
		}
		catch(IOException io)
		{
			if(ignoreErrors)
				return null;

			String[] args = { io.getMessage() };
			VFSManager.error(comp,"ioerror",args);
			return null;
		}
	}

	// Convert a line of LIST output to a VFS.DirectoryEntry
	private VFS.DirectoryEntry lineToDirectoryEntry(String line)
	{
		try
		{
			int type;
			switch(line.charAt(0))
			{
			case 'd':
				type = VFS.DirectoryEntry.DIRECTORY;
				break;
			case 'l':
				type = __LINK;
				break;
			default:
				type = VFS.DirectoryEntry.FILE;
				break;
			}

			// now, we use one of several regexps to obtain
			// the file name and size
			String name = null;
			long length = 0L;

			for(int i = 0; i < regexps.length; i++)
			{
				RE regexp = regexps[i];
				REMatch match;
				if((match = regexp.getMatch(line)) != null)
				{
					try
					{
						length = Long.parseLong(match.toString(1));
					}
					catch(NumberFormatException nf)
					{
						continue;
					}

					name = match.toString(2);
					break;
				}
			}


			if(name == null)
				return null;

			// path is null; it will be created later, by _listDirectory()
			return new VFS.DirectoryEntry(name,null,null,type,
				length,name.charAt(0) == '.' /* isHidden */);
		}
		catch(Exception e)
		{
			Log.log(Log.NOTICE,this,"lineToDirectoryEntry("
				+ line + ") failed:");
			Log.log(Log.NOTICE,this,e);
			return null;
		}
	}

	private void resolveSymlink(FtpSession session, String dir,
		VFS.DirectoryEntry entry, Component comp) throws IOException
	{
		String name = entry.name;
		int index = name.indexOf(" -> ");
		
		if(index == -1)
		{
			//non-standard link representation. Treat as a file
			//Some Mac and NT based servers do not use the "->" for symlinks
			entry.path = constructPath(dir,name);
			entry.type = VFS.DirectoryEntry.FILE;
			Log.log(Log.NOTICE,this,"Dir Entry '"
				+ name
				+ "' is listed as a link, but will be treated"
				+ " as a file because no '->' was found.");
			return;
		}
		String link = name.substring(index + " -> ".length());
		link = constructPath(dir,link);
		VFS.DirectoryEntry linkDirEntry = _getDirectoryEntry(
			session,link,comp);
		if(linkDirEntry == null)
			entry.type = VFS.DirectoryEntry.FILE;
		else
			entry.type = linkDirEntry.type;

		if(entry.type == __LINK)
		{
			// this link links to a link. Don't bother
			// resolving any futher, otherwise we will
			// have to handle symlinks loops, etc, and it
			// will just complicate the code.
			entry.type = VFS.DirectoryEntry.FILE;
		}

		entry.name = name.substring(0,index);
		entry.path = link;
		entry.deletePath = constructPath(dir,entry.name);
	}

	static class FtpSession
	{
		String host;
		String user;
		String password;
		FtpClient client;
	}
}
