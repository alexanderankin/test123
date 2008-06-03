/*
 * ConnectionManager.java - Manages persistent connections
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002, 2003, 2007 Slava Pestov, Nicholas O'Leary
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

//{{{ Imports
import java.awt.Component;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.jcraft.Compression;
//}}}

public class ConnectionManager
{
	// {{{ members 
	protected static Object lock;
	protected static ArrayList<Connection> connections;
	/**
	 * cached logins by host
	 */
	protected static HashMap<String, ConnectionInfo> logins;
	protected static HashMap<String, String> passwords;
	protected static HashMap<String, String> passphrases;
	static int connectionTimeout = 120000;
	private static File passwordFile = null;
	public static JSch client = null;

	// }}}
	
	//{{{ forgetPasswords() method
	public static void forgetPasswords()
	{
		try {
			if (passwordFile.exists())
				passwordFile.delete();
			passwordFile.createNewFile();
		} catch(IOException e) {
			Log.log(Log.WARNING,ConnectionManager.class,
				"Unable to create password file:"+passwordFile);
		}
		passwords.clear();
		passphrases.clear();
		logins.clear();
		client = null;
	} //}}}

	//{{{ getPassword() method
	protected static String getPassword(String hostInfo)
	{
		return passwords.get(hostInfo);
	} //}}}
	
	//{{{ setPassword() method
	protected static void setPassword(String hostInfo, String password)
	{
		passwords.put(hostInfo, password);
	} //}}}
	
	//{{{ getPassphrase() method
	public static String getPassphrase(String keyFile)
	{
		return passphrases.get(keyFile);
	} //}}}
	
	//{{{ setPassphrase() method
	public static void setPassphrase(String keyFile, String passphrase)
	{
		passphrases.put(keyFile,passphrase);
	} //}}}
	
	//{{{ loadPasswords() method
	protected static void loadPasswords()
	{
		if (passwordFile == null)
		{
			Log.log(Log.WARNING,ConnectionManager.class,"Password File is null - unable to load passwords.");
			return;
		}
		int passwordFileLength = (int)passwordFile.length();
		if (passwordFileLength == 0)
		{
			return;
		}
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try
		{
			byte[] buffer = new byte[passwordFileLength];
			fis = new FileInputStream(passwordFile);
			int read = 0;
			while(read<passwordFileLength) {
				read+=fis.read(buffer,read,passwordFileLength-read);
			}
			Compression comp = new Compression();
			comp.init(Compression.INFLATER,6);
			byte[] uncompressed = comp.uncompress(buffer,0,new int[]{buffer.length});
			ois = new ObjectInputStream(
				new BufferedInputStream(
					new ByteArrayInputStream(
						uncompressed,0,uncompressed.length
						)
					)
				);
			passwords = (HashMap)ois.readObject();
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,ConnectionManager.class,"Failed to restore passwords");
			Log.log(Log.ERROR,ConnectionManager.class,e);
		}
		finally
		{
			if(fis != null)
			{
				try
				{
					fis.close();
				}
				catch(Exception e)
				{
				}
			}
			if(ois != null)
			{
				try { ois.close(); } catch(Exception e) {}
			}
		}
		
	} //}}}

	//{{{ savePasswords() method
	protected static void savePasswords() {
		if (passwordFile == null) {
			Log.log(Log.WARNING,ConnectionManager.class,"Password File is null - unable to save passwords.");
			return;
		}
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		ByteArrayOutputStream baos = null;
		try
		{
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(passwords);
			Compression comp = new Compression();
			comp.init(Compression.DEFLATER,6);
			byte[] objectBuffer = baos.toByteArray();
			int newLength = comp.compress(
				objectBuffer,
				0,
				objectBuffer.length);
			fos = new FileOutputStream(passwordFile);
			fos.write(objectBuffer,0,newLength);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,ConnectionManager.class,e);
		}
		finally
		{
			if(oos != null)
			{
				try
				{
					oos.close();
				}
				catch(Exception e)
				{
				}
			}
			if(baos != null)
			{
				try
				{
					baos.close();
				}
				catch(Exception e)
				{
				}
			}
			if(fos != null)
			{
				try
				{
					fos.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
	} //}}}
	
	//{{{ closeUnusedConnections() method
	public static void closeUnusedConnections()
	{
		synchronized(lock)
		{
			for(int i = 0; i < connections.size(); i++)
			{
				Connection _connect = (Connection)connections.get(i);
				if(!_connect.inUse()) 				{
					closeConnection(_connect);
					i--;
				}
			}
		}
	} //}}}

	//{{{ getConnectionInfo() method
	public static ConnectionInfo getConnectionInfo(Component comp,
		FtpAddress address, boolean secure)
	{
		String host, user;
		String password;

		if(address != null)
		{
			host = address.host;
			if(host.indexOf(":") == -1)
				host = host + ":" + FtpVFS.getDefaultPort(secure);
			user = address.user;
			password = address.password;

			ConnectionInfo info = logins.get(host);

			if(info != null && info.getPassword()!=null && (info.user.equals(user) || user == null)) {
				return info;
			}
		}
		else {
			host = user = password = null;
		}

		/* since this can be called at startup time,
		 * we need to hide the splash screen. */
		GUIUtilities.hideSplashScreen();
		LoginDialog dialog = new LoginDialog(comp, secure, host, user, password);
		if(!dialog.isOK())
			return null;
		host = dialog.getHost();
				
		int port = FtpVFS.getDefaultPort(secure);
		int index = host.indexOf(':');
		if(index != -1)
		{
			try
			{
				port = Integer.parseInt(host.substring(index + 1));
				host = host.substring(0,index);
			}
			catch(NumberFormatException e)
			{
			}
		}

		ConnectionInfo info = new ConnectionInfo(secure,host,port,
			dialog.getUser(),dialog.getPassword(),dialog.getPrivateKey());

		if (secure && dialog.getPrivateKey()!=null)
			jEdit.setProperty("ftp.keys."+host+":"+port+"."+dialog.getUser(),dialog.getPrivateKeyFilename());
		
		// Save password here
		if (jEdit.getBooleanProperty("vfs.ftp.storePassword"))
			setPassword(host+":"+port+"."+dialog.getUser(),dialog.getPassword());
		
		// hash by host name
		logins.put(host + ":" + port,info);

		return info;
	} //}}}

	
	
	//{{{ getConnection() method
	public static Connection getConnection(ConnectionInfo info) throws IOException {
		Connection connect = null;
		synchronized(lock) {
			for(int i = 0; i < connections.size(); i++) {
				Connection _connect = (Connection)connections.get(i);
				if(!_connect.info.equals(info) || _connect.inUse()) continue;
				
				connect = _connect;
				if(!connect.checkIfOpen()) {
					Log.log(Log.DEBUG,ConnectionManager.class, "Connection " + connect + " expired");
					try {
						connect.logout();
					} catch(IOException io) {
					}

					connections.remove(connect);
					connect = null;
				}
				else
					break;
			}

			if(connect == null) {
				Log.log(Log.DEBUG,ConnectionManager.class, Thread.currentThread() + ": Connecting to " + info);
				if(info.secure)  {
					connect = new SFtpConnection(info);
				} else {
					try {
						connect = new FtpConnection(info);
					} catch (FtpLoginException e) {
						Log.log(Log.DEBUG, ConnectionManager.class, "catch FtpLoginException");
						//if (e.getResponse().getReturnCode() == "530")
						info.password = null; // Show login dialog again   
						throw e;
					}
				}
				connections.add(connect);
			} else {
				Log.log(Log.DEBUG, ConnectionManager.class, "Connection found in cache ["+connect+"]");
			}

			connect.lock();
		}
		return connect;
		
	} //}}}

	//{{{ releaseConnection() method
	public static void releaseConnection(Connection connect)
	{
		if (connect==null) return;
		synchronized(lock) {
			connect.unlock();
		}
	} //}}}

	//{{{ closeConnection() method
	static void closeConnection(Connection connect)
	{
		synchronized(lock)
		{
			if(connect.inUse)
				return;

			Log.log(Log.DEBUG,ConnectionManager.class, "Closing connection to "+ connect.info);
			try
			{
				connect.logout();
			}
			catch(IOException io)
			{
				Log.log(Log.ERROR,ConnectionManager.class,io);
			}

			connections.remove(connect);
		}
	} //}}}

	//{{{ Private members
	static
	{
		lock = new Object();
		connections = new ArrayList<Connection>();
		logins = new HashMap<String, ConnectionInfo>();
		passwords = new HashMap<String, String>();
		passphrases = new HashMap<String, String>();

		String settingsDirectory = jEdit.getSettingsDirectory();
		if(settingsDirectory == null)
		{
			Log.log(Log.WARNING,ConnectionManager.class,"-nosettings command line switch specified;");
			Log.log(Log.WARNING,ConnectionManager.class,"passwords will not be saved.");
		}
		else
		{
			String passwordDirectory = MiscUtilities.constructPath(settingsDirectory, "cache");
			passwordFile = new File(MiscUtilities.constructPath(passwordDirectory,"password-cache"));
			passwordFile.getParentFile().mkdirs();
			try {
				passwordFile.createNewFile();
			} catch(IOException e) {
				Log.log(Log.WARNING,ConnectionManager.class,
					"Unable to create password file: "+passwordFile);
			}
		}
	} //}}}

}
