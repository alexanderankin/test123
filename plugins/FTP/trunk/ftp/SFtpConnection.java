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

import ftp.FtpVFS.FtpDirectoryEntry;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

/**
 * Secure FTP connection class
 * @author Slava Pestov
 * @author Vadim Voituk
 */
public class SFtpConnection extends Connection implements UserInfo
{
	
	SFtpConnection(final ConnectionInfo info) throws IOException
	{
		super(info);
		try {
			if (ConnectionManager.client == null)  {
				ConnectionManager.client = new JSch();
			}
			JSch.setLogger(new SftpLogger());
			String settingsDirectory = jEdit.getSettingsDirectory();
			if(settingsDirectory != null)
			{
				String cacheDir    = MiscUtilities.constructPath(settingsDirectory, "cache");
				String known_hosts = MiscUtilities.constructPath(cacheDir, "known_hosts");
				try {
					(new File(known_hosts)).createNewFile();
					ConnectionManager.client.setKnownHosts(known_hosts);
				} catch(IOException e) {
					Log.log(Log.WARNING,ConnectionManager.class,
						"Unable to create password file:"+known_hosts);
				}
			}
			
			// {{{ Detect proxy settings if need
			Proxy proxy = null;
			if (jEdit.getBooleanProperty("vfs.ftp.useProxy")) {
				
				if (jEdit.getBooleanProperty("firewall.socks.enabled", false) ) {
					//Detect SOCKS Proxy 
					proxy = new ProxySOCKS5(jEdit.getProperty("firewall.socks.host"), jEdit.getIntegerProperty("firewall.socks.port", 3128));
				} else if (jEdit.getBooleanProperty("firewall.enabled", false)) {
					// HTTP-Proxy detect
					ProxyHTTP httpProxy =  new ProxyHTTP(jEdit.getProperty("firewall.host"), jEdit.getIntegerProperty("firewall.port", 3128) );
					if (!jEdit.getProperty("firewall.user", "").equals(""))
						httpProxy.setUserPasswd(jEdit.getProperty("firewall.user"), jEdit.getProperty("firewall.password"));
					proxy = httpProxy;
				}
			}
			// }}}
			
			Session session = ConnectionManager.client.getSession(info.user, info.host, info.port);
			if (proxy != null)
				session.setProxy(proxy);
			
			Log.log(Log.DEBUG, this, "info.privateKey=" + info.privateKey);
			if (info.privateKey != null && info.privateKey.length()>0) {
				Log.log(Log.DEBUG,this,"Attempting public key authentication");
				Log.log(Log.DEBUG,this,"Using key: "+info.privateKey);
				ConnectionManager.client.addIdentity(info.privateKey);
			}
			keyAttempts = 0;
			session.setUserInfo(this);
			
			//FIXME: Timeout hardcoded to 60seconds
			session.connect(60000);
			
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftp=(ChannelSftp)channel;
			home=sftp.getHome();
			keyAttempts = 0;
		} catch(Exception e) {
			throw new IOException(e.toString());
			//throw new IOException(e); // This will work since java 1.6
		}
	}
	
	@SuppressWarnings("unchecked")
	FtpVFS.FtpDirectoryEntry[] listDirectory(String path) throws IOException
	{
		ArrayList<FtpDirectoryEntry> listing = new ArrayList<FtpDirectoryEntry>();
		int count=0;
		
		try
		{
			Vector<com.jcraft.jsch.ChannelSftp.LsEntry> vv = sftp.ls(path);
			if(vv!=null) {
				for(int ii=0; ii<vv.size(); ii++){
					Object obj=vv.elementAt(ii);
					if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
						count++;
						com.jcraft.jsch.ChannelSftp.LsEntry entry = (com.jcraft.jsch.ChannelSftp.LsEntry)obj;
						listing.add(createDirectoryEntry(entry.getFilename(), entry.getAttrs()));
					}
				}
			}
		} catch (SftpException e) {
			return null;
		}
		FtpVFS.FtpDirectoryEntry[] result = listing.toArray(
			new FtpVFS.FtpDirectoryEntry[listing.size()]);
		return result;
	}
	
	FtpVFS.FtpDirectoryEntry getDirectoryEntry(String path) throws IOException
	{
		FtpVFS.FtpDirectoryEntry returnValue = null;
		try {
			SftpATTRS attrs = sftp.stat(path);
			returnValue = createDirectoryEntry(path, attrs);
			returnValue.setPath(path);
			returnValue.setDeletePath(path);
		} catch(SftpException e) {
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
	
	public String resolveSymlink(String path, String[] name) throws IOException
	{
		String returnValue = path;
		return returnValue;
	}
	
	void logout() throws IOException {
		sftp.disconnect();
	}
	
	private ChannelSftp sftp;
	private int keyAttempts = 0;
	
	// private int symLinkDepth = 0; // not used now
	private FtpVFS.FtpDirectoryEntry createDirectoryEntry(String name, SftpATTRS attrs)
	{
		long length = attrs.getSize();
		int permissions = attrs.getPermissions();
		
		// remove file mode bits from the permissions
		permissions &= 0x1ff; // == binary 111111111
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
		//boolean w = (permissions&00200)!=0;
		//boolean r = (permissions&00400)!=0;
		entry.setWriteable( (permissions&00200)!=0 );
		entry.setReadable( (permissions&00400)!=0 );
		return entry;
	}
	
	private String passphrase = null;
	
	public String getPassphrase()
	{
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
		passphrase = ConnectionManager.getPassphrase(info.privateKey);
		if (passphrase==null || keyAttempts != 0)
		{
			PasswordDialog pd = new PasswordDialog(jEdit.getActiveView(),"Enter Passphrase for key",message);
			if (!pd.isOK())
				return false;
			passphrase = new String(pd.getPassword());
			ConnectionManager.setPassphrase(info.privateKey,passphrase);
		}
		keyAttempts++;
		return true;
	}
	public boolean promptYesNo(final String message)
	{
		final int ret[] = new int[1];
		try
		{
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					Object[] options = {"yes", "no"};
					ret[0] = JOptionPane.showOptionDialog(jEdit.getActiveView(),
						message,
						"Warning",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null, options, options[0]);
				}
			};
			if (EventQueue.isDispatchThread())
			{
				runnable.run();
			}
			else
			{
				EventQueue.invokeAndWait(runnable);
			}
		}
		catch (InterruptedException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		catch (InvocationTargetException e)
		{
			Log.log(Log.ERROR, this, e); 
		}
		return ret[0]==0;
	}
	public void showMessage(final String message)
	{
		try
		{
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					JOptionPane.showMessageDialog(jEdit.getActiveView(), message);
				}
			};
			if (EventQueue.isDispatchThread())
			{
				runnable.run();
			}
			else
			{
				EventQueue.invokeAndWait(runnable);
			}
		}
		catch (InterruptedException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		catch (InvocationTargetException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}
}
