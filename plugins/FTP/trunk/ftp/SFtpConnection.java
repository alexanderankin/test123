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

import com.sshtools.j2ssh.authentication.*;
import com.sshtools.j2ssh.configuration.*;
import com.sshtools.j2ssh.connection.*;
import com.sshtools.j2ssh.session.*;
import com.sshtools.j2ssh.sftp.*;
import com.sshtools.j2ssh.transport.*;
import com.sshtools.j2ssh.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

class SFtpConnection extends ConnectionManager.Connection
{
	SFtpConnection(final ConnectionManager.ConnectionInfo info) throws IOException
	{
		super(info);

		client = new SshClient();
		client.connect(info.host,info.port,new DialogHostKeyVerification(null));
		PasswordAuthentication auth = new PasswordAuthentication();
		auth.setUsername(info.user);
		auth.setPassword(info.password);
		client.authenticate(auth);

		session = client.openSessionChannel();
		if(!session.startSubsystem("sftp"))
			throw new IOException("Cannot start sftp subsystem");

		sftp = new SftpSubsystemClient();
		sftp.setInputStream(session.getInputStream());
		sftp.setOutputStream(session.getOutputStream());
		sftp.start();

		home = sftp.getDefaultDirectory();
	}

	FtpVFS.FtpDirectoryEntry[] listDirectory(String path) throws IOException
	{
		FtpVFS.FtpDirectoryEntry[] returnValue = null;
		SftpFile dir = null;

		try
		{
			dir = sftp.openDirectory(path);
			ArrayList listing = new ArrayList();
			int count = 0;
			do
			{
				count = sftp.listChildren(dir,listing);
			}
			while(count != -1);

			returnValue = new FtpVFS.FtpDirectoryEntry[listing.size()];
			for(int i = 0; i < listing.size(); i++)
			{
				SftpFile file = (SftpFile)listing.get(i);
				returnValue[i] = createDirectoryEntry(file);
			}
		}
		finally
		{
			if(dir != null)
				sftp.closeFile(dir);
		}

		return returnValue;
	}

	FtpVFS.FtpDirectoryEntry getDirectoryEntry(String path) throws IOException
	{
		SftpFile file = null;
		FtpVFS.FtpDirectoryEntry returnValue = null;

		try
		{
			file = sftp.openFile(path,SftpSubsystemClient.OPEN_READ);
			returnValue = createDirectoryEntry(file);
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
				| SftpSubsystemClient.OPEN_TRUNCATE);
		}

		return new SftpFileOutputStream(file);
	}

	void chmod(String path, int permissions) throws IOException
	{
	}

	boolean checkIfOpen() throws IOException
	{
		return client.isConnected();
	}

	void logout() throws IOException
	{
		session.close();
		client.disconnect();
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
		String path = file.getLongname();
		String name = file.getFilename();

		int type;
		if(file.isDirectory())
			type = FtpVFS.FtpDirectoryEntry.DIRECTORY;
		else
			type = FtpVFS.FtpDirectoryEntry.FILE;

		// (String name, String path, String deletePath,
		//	int type, long length, boolean hidden, int permissions)
		return new FtpVFS.FtpDirectoryEntry(name,
			path,path,type,length,
			name.startsWith("."),
			permissions);
	}
}
