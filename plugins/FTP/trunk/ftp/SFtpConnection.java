/*
 * SFtpConnection.java - A connection to an SSH FTP server
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

import com.sshtools.j2ssh.*;
import com.sshtools.j2ssh.authentication.*;
import com.sshtools.j2ssh.configuration.*;
import com.sshtools.j2ssh.connection.*;
import com.sshtools.j2ssh.io.UnsignedInteger32;
import com.sshtools.j2ssh.session.*;
import com.sshtools.j2ssh.sftp.*;
import com.sshtools.j2ssh.transport.*;
import com.sshtools.common.hosts.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.JARClassLoader;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.util.Log;

class SFtpConnection extends ConnectionManager.Connection
{
	SFtpConnection(final ConnectionManager.ConnectionInfo info) throws IOException
	{
		super(info);

		client = new SshClient();
		client.connect(info.host,info.port,new DialogHostKeyVerification(null));
		PasswordAuthenticationClient auth = new PasswordAuthenticationClient();
		auth.setUsername(info.user);
		auth.setPassword(info.password);
		client.authenticate(auth);

		session = client.openSessionChannel();
		if(!session.startSubsystem("sftp"))
			throw new IOException("Cannot start sftp subsystem");

		sftp = new SftpSubsystemClient();
		sftp.setSessionChannel(session);
		sftp.start();

		home = sftp.getDefaultDirectory();
	}

	FtpVFS.FtpDirectoryEntry[] listDirectory(String path) throws IOException
	{
		FtpVFS.FtpDirectoryEntry[] returnValue = null;
		SftpFile dir = null;

		ArrayList listing = new ArrayList();

		try
		{
			dir = sftp.openDirectory(path);
			int count = 0;
			do
			{
				count = sftp.listChildren(dir,listing);
			}
			while(count != -1);

			for(int i = 0; i < listing.size(); i++)
			{
				SftpFile file = (SftpFile)listing.get(i);
				String name = file.getFilename();
				if(name.equals(".") || name.equals(".."))
				{
					listing.remove(i);
					i--;
				}
				else
				{
					listing.set(i,createDirectoryEntry(
						file));
				}
			}
		}
		finally
		{
			if(dir != null)
				sftp.closeFile(dir);
		}

		return (FtpVFS.FtpDirectoryEntry[])listing.toArray(
			new FtpVFS.FtpDirectoryEntry[listing.size()]);
	}

	FtpVFS.FtpDirectoryEntry getDirectoryEntry(String path) throws IOException
	{
		SftpFile file = null;
		FtpVFS.FtpDirectoryEntry returnValue = null;

		try
		{
			file = sftp.openFile(path,SftpSubsystemClient.OPEN_READ);
			returnValue = createDirectoryEntry(file);
			returnValue.path = returnValue.deletePath = path;
		}
		catch(IOException io)
		{
		}
		finally
		{
			if(file != null)
				sftp.closeFile(file);
		}

		return returnValue;
	}

	boolean removeFile(String path) throws IOException
	{
		try
		{
			sftp.removeFile(path);
			return true;
		}
		catch(SshException e)
		{
			return false;
		}
	}

	boolean removeDirectory(String path) throws IOException
	{
		try
		{
			sftp.removeDirectory(path);
			return true;
		}
		catch(SshException e)
		{
			return false;
		}
	}

	boolean rename(String from, String to) throws IOException
	{
		try
		{
			sftp.renameFile(from,to);
			return true;
		}
		catch(SshException e)
		{
			return false;
		}
	}

	boolean makeDirectory(String path) throws IOException
	{
		try
		{
			sftp.makeDirectory(path);
			return true;
		}
		catch(SshException e)
		{
			return false;
		}
	}

	InputStream retrieve(String path) throws IOException
	{
		return new SftpFileInputStream(sftp.openFile(path,
			SftpSubsystemClient.OPEN_READ));
	}

	OutputStream store(String path) throws IOException
	{
		// ugh...
		SftpFile file;
		// try
		// {
			// file = sftp.openFile(path,SftpSubsystemClient.OPEN_WRITE);
		// }
		// catch(Exception e)
		{
			file = sftp.openFile(path,SftpSubsystemClient.OPEN_WRITE
				| SftpSubsystemClient.OPEN_CREATE
				| SftpSubsystemClient.OPEN_TRUNCATE,
				DEFAULT_ATTRIBUTES);
		}

		return new SftpFileOutputStream(file);
	}

	void chmod(String path, int permissions) throws IOException
	{
		sftp.changePermissions(path,permissions);
	}

	boolean checkIfOpen() throws IOException
	{
		return client.isConnected();
	}

	public String resolveSymlink(String path, String[] name)
		throws IOException
	{
		SftpFile file = null;
		String returnValue;
		try
		{
			file = sftp.openFile(path,SftpSubsystemClient.OPEN_READ);
			returnValue = file.getAbsolutePath();
		}
		catch(IOException io)
		{
			returnValue = null;
		}
		finally
		{
			if(file != null)
				sftp.closeFile(file);
		}

		return returnValue;
	}

	void logout() throws IOException
	{
		session.close();
		client.disconnect();
	}

	private static FileAttributes DEFAULT_ATTRIBUTES;
	static
	{
		ConfigurationLoader.setContextClassLoader(new JARClassLoader());
		DEFAULT_ATTRIBUTES = new FileAttributes();
		DEFAULT_ATTRIBUTES.setPermissions(new UnsignedInteger32(600));
	}

	private SshClient client;
	private SessionChannelClient session;
	private SftpSubsystemClient sftp;

	private FtpVFS.FtpDirectoryEntry createDirectoryEntry(SftpFile file)
	{
		FileAttributes attrs = file.getAttributes();
		long length = (attrs.getSize() == null ? 0L : attrs.getSize().longValue());
		int permissions = (attrs.getPermissions() == null
			? 0 : attrs.getPermissions().intValue());
		String name = file.getFilename();

		int type;
		if(file.isDirectory())
			type = FtpVFS.FtpDirectoryEntry.DIRECTORY;
		else if(file.isLink())
			type = FtpVFS.FtpDirectoryEntry.LINK;
		else
			type = FtpVFS.FtpDirectoryEntry.FILE;

		// path field filled out by FtpVFS class
		// (String name, String path, String deletePath,
		//	int type, long length, boolean hidden, int permissions)
		return new FtpVFS.FtpDirectoryEntry(name,
			null,null,type,length,
			name.startsWith("."),
			permissions,null);
	}
}
