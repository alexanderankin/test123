/*
* SFtpConnection.java - A connection to an SSH FTP server
* Copyright (C) 2002, 2007 Slava Pestov, Nicholas O'Leary
*
* :tabSize=8:indentSize=8:noTabs=false:
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

package ftp;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.JARClassLoader;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

class SFtpConnection extends ConnectionManager.Connection implements UserInfo
{
	SFtpConnection(final ConnectionManager.ConnectionInfo info) throws IOException
	{
		super(info);
		try {
			client = new JSch();
			client.setLogger(new SftpLogger());
			String settingsDirectory = jEdit.getSettingsDirectory();
			if(settingsDirectory != null)
			{
				String cacheDir = MiscUtilities.constructPath(settingsDirectory,
					"cache");
				String known_hosts = MiscUtilities.constructPath(cacheDir,"known_hosts");
				try {
					(new File(known_hosts)).createNewFile();
					client.setKnownHosts(known_hosts);
				} catch(IOException e) {
					Log.log(Log.WARNING,ConnectionManager.class,
						"Unable to create password file:"+known_hosts);
				}
			}
			Session session=client.getSession(info.user, info.host,info.port);
			if (info.privateKey != null) {
				Log.log(Log.DEBUG,this,"Attempting public key authentication");
				Log.log(Log.DEBUG,this,"Using key: "+info.privateKey);
				client.addIdentity(info.privateKey);
			}
			keyAttempts = 0;
			session.setUserInfo(this);
			
			// Timeout hardcoded to 60seconds
			session.connect(60000);
			
			Channel channel=session.openChannel("sftp");
			channel.connect();
			sftp=(ChannelSftp)channel;
			home=sftp.getHome();
		} catch(Exception e) {
			throw new IOException(e.toString());
		}
	}
	
	FtpVFS.FtpDirectoryEntry[] listDirectory(String path) throws IOException
	{
		ArrayList listing = new ArrayList();
		int count=0;
		
		try
		{
			java.util.Vector vv=sftp.ls(path);
			if(vv!=null) {
				for(int ii=0; ii<vv.size(); ii++){
					Object obj=vv.elementAt(ii);
					if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
						count++;
						listing.add(createDirectoryEntry((com.jcraft.jsch.ChannelSftp.LsEntry)obj));
					}
				}
			}
		} catch (SftpException e) {
			return null;
		}
		FtpVFS.FtpDirectoryEntry[] result = (FtpVFS.FtpDirectoryEntry[])listing.toArray(
			new FtpVFS.FtpDirectoryEntry[listing.size()]);
		return result;
	}
	
	FtpVFS.FtpDirectoryEntry getDirectoryEntry(String path) throws IOException
	{
		FtpVFS.FtpDirectoryEntry returnValue = null;
		FtpVFS.FtpDirectoryEntry[] dir = listDirectory(path);
		if (dir != null && dir.length == 1) {
			returnValue = dir[0];
			returnValue.setPath(path);
			returnValue.setDeletePath(path);
		}
		return returnValue;
	}
	
	boolean removeFile(String path) throws IOException
	{
		try
		{
			sftp.rm(path);
			return true;
		}
		catch(SftpException e)
		{
			return false;
		}
	}
	
	boolean removeDirectory(String path) throws IOException
	{
		try
		{
			sftp.rmdir(path);
			return true;
		}
		catch(SftpException e)
		{
			return false;
		}
	}
	
	boolean rename(String from, String to) throws IOException
	{
		try
		{
			sftp.rename(from,to);
			return true;
		}
		catch(SftpException e)
		{
			return false;
		}
	}
	
	boolean makeDirectory(String path) throws IOException
	{
		try
		{
			sftp.mkdir(path);
			return true;
		}
		catch(SftpException e)
		{
			return false;
		}
	}
	
	InputStream retrieve(String path) throws IOException
	{
		try {
			return sftp.get(path);
		} catch (SftpException e) {
			throw new IOException(e.toString());
		}
	}
	
	OutputStream store(String path) throws IOException
	{
		OutputStream returnValue;
		try {
			returnValue = sftp.put(path);
		} catch(SftpException e) {
			throw new IOException(e.toString());
		}
		return returnValue;
	}
	
	void chmod(String path, int permissions) throws IOException
	{
		try {
			sftp.chmod(permissions,path);
		} catch (SftpException e) {
			throw new IOException(e.toString());
		}
	}
	
	boolean checkIfOpen() throws IOException
	{
		return sftp.isConnected();
	}
	
	public String resolveSymlink(String path, String[] name)
	throws IOException
	{
		String returnValue;
		try
		{
			returnValue = sftp.readlink(path);
		}
		catch(SftpException e)
		{
			returnValue = null;
		}
		
		return returnValue;
	}
	
	void logout() throws IOException
	{
		sftp.disconnect();
	}
	
	//private static FileAttributes DEFAULT_ATTRIBUTES;
	//static
	//{
		//	ConfigurationLoader.setContextClassLoader(new JARClassLoader());
		//	DEFAULT_ATTRIBUTES = new FileAttributes();
		//	DEFAULT_ATTRIBUTES.setPermissions(new UnsignedInteger32(600));
	//}
	
	private JSch client;
	private ChannelSftp sftp;
	private int keyAttempts = 0;
	
	private FtpVFS.FtpDirectoryEntry createDirectoryEntry(com.jcraft.jsch.ChannelSftp.LsEntry file)
	{
		SftpATTRS attrs = file.getAttrs();
		long length = attrs.getSize();
		int permissions = attrs.getPermissions();
		
		// remove file mode bits from the permissions
		permissions &= 0x1ff; // == binary 111111111
		String name = file.getFilename();
		int type;
		if(attrs.isDir())
			type = FtpVFS.FtpDirectoryEntry.DIRECTORY;
		else if(attrs.isLink())
			type = FtpVFS.FtpDirectoryEntry.LINK;
		else
			type = FtpVFS.FtpDirectoryEntry.FILE;
		
		// path field filled out by FtpVFS class
		// (String name, String path, String deletePath,
			//	int type, long length, boolean hidden, int permissions)
		FtpVFS.FtpDirectoryEntry entry = new FtpVFS.FtpDirectoryEntry(
			name, null, null, type, length, name.startsWith("."), permissions,null);
		boolean w = (permissions&00200)!=0;
		boolean r = (permissions&00400)!=0;
		entry.setWriteable( (permissions&00200)!=0 );
		entry.setReadable( (permissions&00400)!=0 );
		return entry;
	}
	
	private String passphrase = null;
	
	public String getPassphrase()
	{
		if (passphrase == null)
			return null;
		return passphrase;
	}
	
	public String getPassword()
	{
		return new String(info.password);
	}
	
	public boolean promptPassword(String message){ return true;}
	public boolean promptPassphrase(String message)
	{
		Log.log(Log.DEBUG,this,message);
		String pass = ConnectionManager.getPassword(info.privateKey);
		if (pass==null || keyAttempts != 0)
		{
			PasswordDialog pd = new PasswordDialog(null,"Enter Passphrase for key",message);
			if (!pd.isOK())
				return false;
			passphrase = new String(pd.getPassword());
			if (jEdit.getBooleanProperty("vfs.ftp.storePassword"))
			{
				ConnectionManager.setPassword(info.privateKey,passphrase);
			}
			return true;
		}
		keyAttempts++;
		passphrase = pass;
		return true;
	}
	public boolean promptYesNo(String message)
	{
		Object[] options={ "yes", "no" };
		int foo=JOptionPane.showOptionDialog(null, 
			message,
			"Warning",
			JOptionPane.DEFAULT_OPTION, 
			JOptionPane.WARNING_MESSAGE,
			null, options, options[0]);
		return foo==0;
	}
	public void showMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}
}
