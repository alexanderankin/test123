/*
 * FtpConnection.java - A connection to an FTP server
 * Copyright (C) 2002, 2003 Slava Pestov
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
import com.fooware.net.proxy.Proxy;
import com.fooware.net.proxy.ProxyHTTP;

import ftp.FtpVFS.FtpDirectoryEntry;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

class FtpConnection extends Connection
{
	FtpConnection(ConnectionInfo info) throws IOException
	{
		super(info);
		
		// TODO: Move this data into ConnectionInfo class
		if (jEdit.getBooleanProperty("vfs.ftp.useProxy") 
				&& jEdit.getBooleanProperty("vfs.ftp.passive")
				&& jEdit.getBooleanProperty("firewall.enabled") ) {
			Proxy proxy;
			if (jEdit.getProperty("firewall.user", "").equals(""))
				proxy = new ProxyHTTP(jEdit.getProperty("firewall.host"), jEdit.getIntegerProperty("firewall.port", 3128));
			else 
				proxy = new ProxyHTTP(
					jEdit.getProperty("firewall.host"), jEdit.getIntegerProperty("firewall.port", 3128),
					jEdit.getProperty("firewall.user"), jEdit.getProperty("firewall.password")
				);
			client = new FtpClient(proxy);
		} else {
			client = new FtpClient();
		}

		client.connect(info.host,info.port);

		if(!client.getResponse().isPositiveCompletion())
		{
			throw new FtpException(
				client.getResponse());
		}

		client.userName(info.user);

		if(client.getResponse().isPositiveIntermediary())
		{
			client.password(info.getPassword());

			FtpResponse response = client.getResponse();
			if(!response.isPositiveCompletion())
			{
				client.logout();
				throw new FtpLoginException(response);
			}
		}
		else if(client.getResponse().isPositiveCompletion())
		{
			// do nothing, server let us in without
			// a password
		}
		else
		{
			FtpResponse response = client.getResponse();
			client.logout();
			throw new FtpLoginException(response);
		}

		client.printWorkingDirectory();
		FtpResponse response = client.getResponse();
		if(response != null
			&& response.getReturnCode() != null
			&& response.getReturnCode().charAt(0) == '2')
		{
			String msg = response.getMessage().substring(4);
			if(msg.startsWith("\""))
			{
				int index = msg.indexOf('"',1);
				if(index != -1)
				{
					home = msg.substring(1,index);
					if(!home.startsWith("/"))
						home = "/".concat(home);
				}
			}
		}
	}

	FtpVFS.FtpDirectoryEntry[] listDirectory(String path) throws IOException
	{
		//CWD into the directory - Doing a LIST on a path with spaces in the
		//name fails; however, if you CWD to the dir and then LIST it
		// succeeds.
		client.changeWorkingDirectory(path);

		//Check for successful response
		FtpResponse response = client.getResponse();
		if(response != null
			&& response.getReturnCode() != null
			&& response.getReturnCode().charAt(0) != '2')
		{
			throw new FtpException(response);
		}

		// some servers might not support -a, so if we get an error
		// try without -a
		ArrayList directoryVector = _listDirectory(true);
		if(directoryVector == null || directoryVector.size() == 0)
			directoryVector = _listDirectory(false);

		if(directoryVector == null)
		{
			// error occurred
			return null;
		}

		return (FtpVFS.FtpDirectoryEntry[])directoryVector.toArray(
			new FtpVFS.FtpDirectoryEntry[directoryVector.size()]);
	}

	/**
	 * An incredibly broken implementation! Originally only good for
	 * internal use by resolveSymlinks(), in FTP 0.7.1 we grafted on
	 * support for file type detection, as required by jEdit 4.2.
	 */
	FtpVFS.FtpDirectoryEntry getDirectoryEntry(String path) throws IOException
	{
		//CWD into the directory - Doing a LIST on a path with spaces in the
		//name fails; however, if you CWD to the dir and then LIST it
		// succeeds.

		//First we get the parent path of the file passed to us in the path
		// field. We use the MiscUtilities.getParentOfPath as opposed to our own
		//because the path here is not a URL and our own version expects a URL
		// when it instantiates an FtpAddress object.
		String parentPath = MiscUtilities.getParentOfPath(path);

		client.changeWorkingDirectory(parentPath);
		//Check for successful response
		FtpResponse response = client.getResponse();
		if(response != null
			&& response.getReturnCode() != null
			&& response.getReturnCode().charAt(0) != '2')
		{
			throw new FtpException(response);
		}

		setupSocket();

		String name = MiscUtilities.getFileName(path);

		//Here we do a LIST for on the specific file
		//Since we are in the right dir, we list only the filename, not the
		// whole path...
		Reader _reader = client.list(name);
		if(_reader == null)
		{
			// eg, file not found
			return null;
		}

		BufferedReader reader = new BufferedReader(_reader);

		// to determine if this is a file or a directory, we list it.
		// if the list contains 1 entry, guess that this is a file
		LinkedList listing = new LinkedList();

		try
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				FtpVFS.FtpDirectoryEntry dirEntry = lineToDirectoryEntry(line);
				if(dirEntry != null)
					listing.add(dirEntry);
				else
				{
					Log.log(Log.DEBUG,this,"Discarding "
						+ line);
				}
			}
		}
		finally
		{
			reader.close();
		}

		int type;
		if(listing.size() == 0)
		{
			// probably a file that does not exist.
			type = FtpVFS.FtpDirectoryEntry.FILE;
		}
		else if(listing.size() > 1)
		{
			type = FtpVFS.FtpDirectoryEntry.DIRECTORY;
		}
		else
		{
			FtpVFS.FtpDirectoryEntry dirEntry
				= (FtpVFS.FtpDirectoryEntry)
				listing.get(0);
			//XXX: we even use startsWith to hot have to parse the
			//-> symlink indicator. Broken, broken, broken...
			if(dirEntry.getName().startsWith(name))
			{
				// TODO: this gets just set to true to avoid the "is readonly" warning and use of two-stage-save when saving..
				dirEntry.setWriteable( true );
				dirEntry.setReadable( true );

				return dirEntry;
			}
			else
			{
				// it could be a directory with 1 file in it!
				// but I don't care, I don't use FTP :-)
				type = FtpVFS.FtpDirectoryEntry.FILE;
			}
		}

		// path field filled out by FtpVFS class
		// (String name, String path, String deletePath,
		//	int type, long length, boolean hidden, int permissions)

		// this directory entry only has half an ass.
		FtpVFS.FtpDirectoryEntry dirEntry = new FtpVFS.FtpDirectoryEntry(
			null,null,null,type,0L,false,0,null);

		// TODO: this gets just set to true to avoid the "is readonly" warning and use of two-stage-save when saving..
		dirEntry.setWriteable( true );
		dirEntry.setReadable( true );

		return dirEntry;
	}

	boolean removeFile(String path) throws IOException
	{
		client.delete(path);
		return client.getResponse().isPositiveCompletion();
	}

	boolean removeDirectory(String path) throws IOException
	{
		client.removeDirectory(path);
		return client.getResponse().isPositiveCompletion();
	}

	boolean rename(String from, String to) throws IOException
	{
		client.renameFrom(from);
		client.renameTo(to);
		return client.getResponse().isPositiveCompletion();
	}

	boolean makeDirectory(String path) throws IOException
	{
		client.makeDirectory(path);
		return client.getResponse().isPositiveCompletion();
	}

	InputStream retrieve(String path) throws IOException
	{
		setupSocket();
		InputStream in = client.retrieveStream(path);
		if(in == null)
			throw new FtpException(client.getResponse());
		else
			return in;
	}

	OutputStream store(String path) throws IOException
	{
		setupSocket();
		OutputStream out = client.storeStream(path);
		if(out == null)
			throw new FtpException(client.getResponse());
		else
			return out;
	}

	void chmod(String path, int permissions) throws IOException
	{
		String cmd = "CHMOD " + Integer.toString(permissions,8)
			+ " " + path;
		client.siteParameters(cmd);
	}

	// Passed 'name' in an array as a hack to be able to return multiple values
	public String resolveSymlink(String path, String[] name)
		throws IOException
	{
		String _name = name[0];
		int index = _name.indexOf(" -> ");

		if(index == -1)
		{
			//non-standard link representation. Treat as a file
			//Some Mac and NT based servers do not use the "->" for symlinks
			Log.log(Log.NOTICE,this,"File '"
				+ name
				+ "' is listed as a link, but will be treated"
				+ " as a file because no '->' was found.");
			return null;
		}

		String link = _name.substring(index + " -> ".length());

		name[0] = _name.substring(0,index);
		return link;
	}

	boolean checkIfOpen() throws IOException
	{
		try
		{
			// to ensure that the server didn't disconnect
			// before the keep-alive timeout expires
			client.noOp();
			client.getResponse();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	void logout() throws IOException
	{
		client.logout();
	}

	// Private members
	private FtpClient client;
	// used to parse VMS file listings, which can span more than one line
	private String prevLine;

	private static Pattern[] unixRegexps;
	private static Pattern dosRegexp;
	private static Pattern vmsRegexp;
	private static Pattern vmsPartialRegexp;
	private static Pattern vmsRejectedRegexp;
	private static Pattern as400Regexp;

	static
	{
		unixRegexps = new Pattern[jEdit.getIntegerProperty(
			"vfs.ftp.list.count",-1)];
		for(int i = 0; i < unixRegexps.length; i++)
		{
			unixRegexps[i] = Pattern.compile(jEdit.getProperty(
				"vfs.ftp.list." + i),
				Pattern.UNIX_LINES);
		}

		dosRegexp.compile(jEdit.getProperty(
			"vfs.ftp.list.dos"),
         Pattern.UNIX_LINES);

		vmsRegexp = Pattern.compile(jEdit.getProperty(
			"vfs.ftp.list.vms"),
         Pattern.UNIX_LINES);

		vmsPartialRegexp = Pattern.compile(jEdit.getProperty(
			"vfs.ftp.list.vms.partial"),
         Pattern.UNIX_LINES);

		vmsRejectedRegexp = Pattern.compile(jEdit.getProperty(
			"vfs.ftp.list.vms.rejected"),
         Pattern.UNIX_LINES);

		as400Regexp = Pattern.compile(jEdit.getProperty(
			"vfs.ftp.list.as400"),
         Pattern.UNIX_LINES);
	}

	private void setupSocket()
		throws IOException
	{
		// See if we should use Binary mode to transfer files.
		if (jEdit.getBooleanProperty("vfs.ftp.binary"))
		{
			//Go with Binary
			client.representationType(FtpClient.IMAGE_TYPE);
		}
		else
		{
			//Stick to ASCII - let the line endings get converted
			client.representationType(FtpClient.ASCII_TYPE);
		}

		if(jEdit.getBooleanProperty("vfs.ftp.passive"))
			client.passive();
		else
			client.dataPort();
	}

	private ArrayList<FtpDirectoryEntry> _listDirectory(boolean tryHiddenFiles)
		throws IOException
	{
		BufferedReader in = null;

		try {
			ArrayList<FtpDirectoryEntry> directoryVector = new ArrayList<FtpDirectoryEntry>();

			setupSocket();
			Reader _in = (tryHiddenFiles ? client.list("-a") : client.list());

			if(_in == null)
			{
				if(!tryHiddenFiles)
					throw new FtpException(client.getResponse());
				else
					return null;
			}

			in = new BufferedReader(_in);
			String line;
			while((line = in.readLine()) != null)
			{
				if(line.length() == 0)
					continue;

				FtpVFS.FtpDirectoryEntry entry = lineToDirectoryEntry(line);
				if(entry == null
					|| entry.getName().equals(".")
					|| entry.getName().equals(".."))
				{
					Log.log(Log.DEBUG,this,"Discarding " + line);
					continue;
				}
				else
					; //Log.log(Log.DEBUG,this,"Parsed " + line);

				directoryVector.add(entry);
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
	private FtpVFS.FtpDirectoryEntry lineToDirectoryEntry(String line)
	{
		try
		{
			// we use one of several regexps to obtain
			// the file name, type, and size
			int type = FtpVFS.FtpDirectoryEntry.FILE;
			String name = null;
			long length = 0L;
			int permissions = 0;
			String permissionString = null;

			boolean ok = false;

			if(prevLine != null)
			{
				// handle VMS listings split over several lines
				line = prevLine + line;
				prevLine = null;
			}

			for(int i = 0; i < unixRegexps.length; i++)
			{
				Pattern regexp = unixRegexps[i];
				Matcher match;
				if((match = regexp.matcher(line)) != null && match.matches())
				{
					switch(line.charAt(0))
					{
					case 'd':
						type = FtpVFS.FtpDirectoryEntry.DIRECTORY;
						break;
					case 'l':
						type = FtpVFS.FtpDirectoryEntry.LINK;
						break;
					case '-':
						type = FtpVFS.FtpDirectoryEntry.FILE;
						break;
					}

					permissionString = match.group(1);
					permissions = MiscUtilities.parsePermissions(
						permissionString);

					try
					{
						length = Long.parseLong(match.group(2));
					}
					catch(NumberFormatException nf)
					{
						length = 0L;
					}

					name = match.group(3);
					ok = true;
					break;
				}
			}

			if(!ok)
			{
				Matcher match;

				if(vmsPartialRegexp.matcher(line).matches())
				{
					prevLine = line;
					return null;
				}
				else if(vmsRejectedRegexp.matcher(line).matches() == true)
					return null;
				else if((match = vmsRegexp.matcher(line)) != null && match.matches())
				{
					name = match.group(1);
					length = Long.parseLong(
						match.group(2)) * 512;
					if(name.endsWith(".DIR"))
					{
						name = name.substring(0,
							name.length() - 4);
						type = FtpVFS.FtpDirectoryEntry
							.DIRECTORY;
					}
					permissionString = match.group(3);
					ok = true;
				}
			}

			if(!ok)
			{
				Matcher match;
				if((match = as400Regexp.matcher(line)) != null && match.matches())
				{
 					String dirFlag = match.group(2);
 					if (dirFlag.equals("*DIR"))
 						type = FtpVFS.FtpDirectoryEntry.DIRECTORY;
 					else
 						type = FtpVFS.FtpDirectoryEntry.FILE;

 					try
 					{
 						length = Long.parseLong(match.group(1));
 					}
 					catch(NumberFormatException nf)
 					{
 						length = 0L;
 					}

 					name = match.group(3);
					if(name.endsWith("/"))
						name = name.substring(0,name.length() - 1);
 					ok = true;
 				}
			}

			if(!ok)
			{
				Matcher match;
				if((match = dosRegexp.matcher(line)) != null && match.matches())
				{
					try
					{
						String sizeStr = match.group(1);
						if(sizeStr.equals("<DIR>"))
							type = FtpVFS.FtpDirectoryEntry.DIRECTORY;
						else
							length = Long.parseLong(sizeStr);
					}
					catch(NumberFormatException nf)
					{
						length = 0L;
					}

					name = match.group(2);
					ok = true;
				}
			}

			if(!ok)
				return null;

			// path is null; it will be created later, by listDirectory()
			return new FtpVFS.FtpDirectoryEntry(name,null,null,type,
				length,name.charAt(0) == '.' /* isHidden */,
				permissions,permissionString);
		}
		catch(Exception e)
		{
			Log.log(Log.NOTICE,this,"lineToDirectoryEntry("
				+ line + ") failed:");
			Log.log(Log.NOTICE,this,e);
			return null;
		}
	}
}
