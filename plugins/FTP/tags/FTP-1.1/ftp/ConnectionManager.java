/*
 * ConnectionManager.java - Manages persistent connections
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002-2006 Slava Pestov, Nicholas O'Leary,
 * Copyright (C) 2007-2014 Vadim Voituk, Alan Ezust
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
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;
import com.jcraft.jsch.JSch;
//}}}

//{{{ ConnectionManager Class

public class ConnectionManager
{
	// {{{ static data
	protected static Object lock;
	protected static boolean restoredPasswords = false;
	protected static ArrayList<Connection> connections;
	/**
	 * cached logins by host
	 */
	protected static HashMap<String, ConnectionInfo> logins;
	private static HashMap<String, String> passwords;
	private static HashMap<String, String> passphrases;
	/** a 256 byte SHA1 hash of the master password, actually */
	static byte[] masterKey = null;

	static int connectionTimeout = 60000;
	private static File passwordFile = null;

	// this is used from SshConsole too:
	public static JSch client = null;
	// }}}

	//{{{ forgetPasswords() method
	public static void forgetPasswords()
	{
		clearStoredFtpKeys();

		try {
			if (client != null)
				client.removeAllIdentity();
			if (passwordFile.exists())
				passwordFile.delete();
		}
		catch (Exception e) {}

		masterKey = null;
		saveKeyFile();		// clear out the master key file, actually
		restoredPasswords = false;
		passwords.clear();
		passphrases.clear();
		logins.clear();
		client = null;
	} //}}}

	//{{{ getPassword() method
	protected static String getPassword(String hostInfo)
	{
		if (!restoredPasswords)
			loadPasswords();
		return passwords.get(hostInfo);
	} //}}}

	//{{{ setPassword() method
	protected static void setPassword(String hostInfo, String password)
	{
		passwords.put(hostInfo, password);
		savePasswords();
	} //}}}

	//{{{ getPassphrase() method
	public static String getPassphrase(String keyFile)
	{
		if (!restoredPasswords)
			loadPasswords();
		return passphrases.get(keyFile);
	} //}}}

	//{{{ setPassphrase() method
	public static void setPassphrase(String keyFile, String passphrase)
	{
		passphrases.put(keyFile,passphrase);
		savePasswords();

	} //}}}


	//{{{ clearStoredFtpKeys()
	/** These ftp keys are stored in properties and there is no GUI yet to change
		them.
	*/
	public static void clearStoredFtpKeys()
	{
		Properties p = jEdit.getProperties();
		for (Object keyobj: p.keySet()) {
			String key = keyobj.toString();
			if (key.startsWith("ftp.keys."))
				jEdit.unsetProperty(key);
		}
	}//}}}

	//{{{ getStoredFtpKey()
	/**

	 * @return null if no
	 */
	public static String getStoredFtpKey(String hostport, String user) {
		String s = jEdit.getProperty("ftp.keys."+hostport+"."+user);
		if (s==null || s.length()<=0)
			return null;
		return new File(s).exists() ? s : null;
	}//}}}

	//{{{ getKeyFile()
	/** If we have a keyFile, load the hash of the master password from a file
	    instead of prompting the user for it. */
	protected static void getKeyFile() {

		if (!jEdit.getBooleanProperty("ftp.useKeyFile")) return;
		try {
			File f = new File(jEdit.getProperty("ftp.passKeyFile"));
			if (!f.exists()) return;
			int length = (int) f.length();
			masterKey = new byte[length];
			FileInputStream fis = new FileInputStream(f);
			fis.read(masterKey);
			fis.close();
		}
		catch (Exception e) {
			Log.log(Log.ERROR, ConnectionManager.class, e);
		}

	}//}}}

	//{{{ saveKeyFile()
	protected static void saveKeyFile()
	{
		if (!jEdit.getBooleanProperty("ftp.useKeyFile")) return;
		try {
			File f = new File(jEdit.getProperty("ftp.passKeyFile"));
			if (masterKey == null) {
				f.delete();
				return;
			}
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(masterKey);
			fos.close();
		}
		catch (Exception e) {
			Log.log(Log.ERROR, ConnectionManager.class, e);
		}

	}//}}}

	//{{{ promptMasterPassword() methods

	protected static boolean promptMasterPassword() {
		return promptMasterPassword(
				jEdit.getProperty("login.masterpassword.title"),
				jEdit.getProperty("login.masterpassword.message"));
	}

	protected static boolean promptMasterPasswordCreate() {
		return promptMasterPassword(
				jEdit.getProperty("login.masterpassword.title"),
				jEdit.getProperty("login.masterpassword.message.create"));
	}

	protected static boolean promptMasterPassword(final String title, final String message) {

		if (!jEdit.getBooleanProperty("vfs.ftp.storePassword")) return false;

		getKeyFile();
		if (masterKey != null) return true;

		// Show a dialog from the active view asking user for master password
		try {
			ThreadUtilities.runInDispatchThreadAndWait(new Runnable() {
				@Override
				public void run() {
					GUIUtilities.hideSplashScreen();
					PasswordDialog pd = new PasswordDialog(jEdit.getActiveView(), title, message);
					String masterPassword = "";
					if (pd.isOK())
						masterPassword = new String(pd.getPassword());
					if (masterPassword.isEmpty() || !pd.isOK()) {
						jEdit.setBooleanProperty("vfs.ftp.storePassword", false);
						String msg2 = jEdit.getProperty("ftp.cancel-master-password");
						Log.log(Log.MESSAGE, ConnectionManager.class, msg2);
						jEdit.getActiveView().getStatus().setMessage(msg2);
						return;
					}
					// make a SHA256 digest of it (32 bytes)
					try {
						MessageDigest digest = MessageDigest.getInstance("SHA-256");
						masterKey = digest.digest(masterPassword.getBytes("utf-8"));
					}
					catch (Exception e) {
						Log.log(Log.ERROR, ConnectionManager.class, e, e);
						return;
					}
					saveKeyFile();
					}
					// Log.log(Log.MESSAGE, ConnectionManager.class, "masterPasswordhash: " + masterPwStr);
			});
		}
		catch (Exception e) {
			Log.log (Log.ERROR, ConnectionManager.class, e);
			return false;
		}
		return true;
	}//}}}

	//{{{ loadPasswords() method
	@SuppressWarnings("unchecked")
	protected static void loadPasswords()
	{
		if (!jEdit.getBooleanProperty("vfs.ftp.storePassword")) return;

		if (passwordFile == null)
		{
			Log.log(Log.WARNING,ConnectionManager.class,
				"Password File is null - unable to load passwords.");
			return;
		}

		if (!passwordFile.exists()) return;

		int passwordFileLength = (int)passwordFile.length();
		if (passwordFileLength == 0) return;
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		int i=0;
		while (!restoredPasswords) try
		{
			if (masterKey == null) {
				if ( (i ==0 ) && !promptMasterPassword())
					return;
				if ((i > 0 ) && !promptMasterPassword(jEdit.getProperty("ftp.bad-master-password"), jEdit.getProperty("login.masterpassword.message")))
					return;
			}

			i++;
			byte[] buffer = new byte[passwordFileLength];
			fis = new FileInputStream(passwordFile);
			int read = 0;
			while(read<passwordFileLength) {
				read+=fis.read(buffer,read,passwordFileLength-read);
			}

			Cipher c = getCipher(Cipher.DECRYPT_MODE);
			byte[] objectBuffer = c.doFinal(buffer);

			ois = new ObjectInputStream(new BufferedInputStream(
					new ByteArrayInputStream( objectBuffer,0,objectBuffer.length )));
			passwords = (HashMap<String, String>)ois.readObject();
			passphrases = (HashMap<String, String>)ois.readObject();
			Log.log(Log.DEBUG, ConnectionManager.class, "Passwords loaded: " + passwords.size());
			Log.log(Log.DEBUG, ConnectionManager.class, "Passphrases loaded: " + passphrases.size());
			restoredPasswords = true;
		}
		catch (BadPaddingException bpe) {
			masterKey = null;
			saveKeyFile();  // wipes out the key file in case invalid pw was saved
//			String message = jEdit.getProperty("ftp.bad-master-password");
//			jEdit.getActiveView().getStatus().setMessage(message);
		}
		catch(Exception e)	{
			Log.log(Log.ERROR, ConnectionManager.class, "loadPasswords()", e);
			return;
		}
		finally
		{
			IOUtilities.closeQuietly((Closeable)fis);
			IOUtilities.closeQuietly((Closeable)ois);
		}

	} //}}}

	//{{{ getCipher() method
	protected static Cipher getCipher(int opmode) throws Exception {
		// First try AES256
		String TRANSFORMATION = "AES";
		try {
			Cipher c = Cipher.getInstance(TRANSFORMATION);
			SecretKeySpec k = new SecretKeySpec(masterKey, TRANSFORMATION);
			c.init(opmode, k);
			return c;
		}
		catch (Exception ike) {
			if (jEdit.getBooleanProperty("ftp.disableWeakCrypto")) {
				String msg = jEdit.getProperty("ftp.jce.strongkeys.missing");
				Log.log(Log.ERROR, ConnectionManager.class, msg, ike);
				jEdit.setBooleanProperty("vfs.ftp.storePassword", false);
				jEdit.getActiveView().getStatus().setMessage(msg);
				return null;
			}
		}
		Log.log(Log.WARNING, ConnectionManager.class, jEdit.getProperty("ftp.using.weak-crypto"));
		// TODO: use something better than DES here?
		TRANSFORMATION = "DES";
		DESKeySpec keySpec = new DESKeySpec(masterKey);
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance(TRANSFORMATION);
		SecretKey k = keyFac.generateSecret(keySpec);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(opmode, k);
		return cipher;
	}//}}}

	//{{{ savePasswords() method
	protected static void savePasswords() {
		if (!jEdit.getBooleanProperty("vfs.ftp.storePassword")) return;

		if (passwordFile == null) {
			Log.log(Log.WARNING,ConnectionManager.class,"Password File is null - unable to save passwords.");
			return;
		}

		if (masterKey == null)
			if (!promptMasterPasswordCreate()) return;

		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		ByteArrayOutputStream baos = null;
		try
		{
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(passwords);
			oos.writeObject(passphrases);
			byte[] objectBuffer = baos.toByteArray();

			Cipher c = getCipher(Cipher.ENCRYPT_MODE);
			objectBuffer = c.doFinal(objectBuffer);

			int newLength = objectBuffer.length;
			fos = new FileOutputStream(passwordFile);
			fos.write(objectBuffer,0,newLength);
			Log.log(Log.DEBUG, ConnectionManager.class, "Passwords saved: " + passwords.size());
			Log.log(Log.DEBUG, ConnectionManager.class, "Passphrases saved: " + passphrases.size());
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,ConnectionManager.class,e);
		}
		finally
		{
			IOUtilities.closeQuietly((Closeable)oos);
			IOUtilities.closeQuietly((Closeable)baos);
			IOUtilities.closeQuietly((Closeable)fos);
		}

	} //}}}

	//{{{ closeUnusedConnections() method
	public static void closeUnusedConnections()
	{
		synchronized(lock)
		{
			for(int i = 0; i < connections.size(); i++)
			{
				Connection _connect = connections.get(i);
				if(!_connect.inUse()) 				{
					closeConnection(_connect);
					i--;
				}
			}
		}
	} //}}}

	//{{{ getConnectionInfo() method
	public static synchronized ConnectionInfo getConnectionInfo(Component comp, FtpAddress address, boolean _secure)
	{
		Log.log(Log.DEBUG, "ConnectionManager.getConnectionInfo", address);
		String hostport, user;
		String password;
		boolean secure;

		if(address != null)
		{
			hostport     = address.getHost()+":"+address.getPort();
			user     = address.getUser();
			password = address.getPassword();
			secure	 = address.isSecure();

			// Check for cached connection info
			ConnectionInfo info = logins.get(hostport);
			if( (info != null) )
				return info;

			// Try to create connection from pre-defined params
			String key = ConnectionManager.getStoredFtpKey(hostport, user);

			if ( hostport!=null && user!=null && (password!=null || key!=null) ) {
				Log.log(Log.DEBUG, ConnectionManager.class, "key="+key);
				info = new ConnectionInfo(address.isSecure(), address.getHost(), address.getPort(), user, password, key);
				logins.put(hostport, info);
				return info;
			}

		}
		else
		{
			hostport = user = password = null;
			secure = _secure;
		}

		/* since this can be called at startup time,
		 * we need to hide the splash screen. */
		GUIUtilities.hideSplashScreen();
		final LoginDialog dialog = new LoginDialog(comp, secure, hostport, user, password);
		ThreadUtilities.runInDispatchThreadAndWait(new Runnable() {
			@Override
			public void run() {
				dialog.setVisible(true);
			}
		});
		if(!dialog.isOK())
			return null;
		String host = dialog.getHost();
		int port = FtpVFS.getDefaultPort(secure);
		int index = host.indexOf(':');
		if(index != -1) {
			try {
				port = Integer.parseInt(host.substring(index + 1));
				host = host.substring(0,index);
			}
			catch(NumberFormatException e){}
		}

		ConnectionInfo info = new ConnectionInfo(secure, host, port,
			dialog.getUser(), dialog.getPassword(), dialog.getPrivateKeyFilename() );

		// Should this be stored in properties?
		if (secure && dialog.getPrivateKeyFilename() != null)
			jEdit.setProperty("ftp.keys."+host+":"+port+"."+dialog.getUser(),dialog.getPrivateKeyFilename());

		// Save password here
		if (jEdit.getBooleanProperty("vfs.ftp.storePassword"))
			setPassword(host+":"+port+"."+dialog.getUser(), dialog.getPassword() );

		// hash by host name
		logins.put(host + ":" + port, info);

		return info;
	} //}}}

	//{{{ getConnection() method
	public static Connection getConnection(ConnectionInfo info) throws IOException {
		Connection connect = null;
		synchronized(lock) {
			for (Connection conn : connections)
			{
				if(!conn.info.equals(info) || conn.inUse()) continue;

				connect = conn;
				if(!connect.checkIfOpen()) {
					Log.log(Log.DEBUG, ConnectionManager.class, "Connection " + connect + " expired");
					connect.logoutQuietly();
					connections.remove(connect);
					connect = null;
				}
				else
					break;
			}
			if (connect != null) {
				Log.log(Log.DEBUG, ConnectionManager.class, "Connection found in cache ["+connect+"]");
				connect.lock();
				return connect;
			}
			int retries = 0;
			while (connect == null) {
				Log.log(Log.DEBUG,ConnectionManager.class, Thread.currentThread() + ": Connecting to " + info);
				try {
					connect = info.secure ? new SFtpConnection(info) : new FtpConnection(info);
					connections.add(connect);
					connect.lock();
					return connect;
				} catch (IOException e) {
					retries++;
					Log.log(Log.WARNING, ConnectionManager.class, "catch " + e.getClass().getName() + " on "+ info, e);
					info.privateKey = null;
					info.password = null; // necessary to show login dialog again instead of using saved password again
					if (retries > 3) throw e;
				}
			}
			return null;


		}


	} //}}}

	//{{{ releaseConnection() method
	public static void releaseConnection(Connection connect) {
		Log.log(Log.DEBUG, ConnectionManager.class, "releaseConnection(" + connect + ")");
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
			if(connect.inUse()) {
				Log.log(Log.DEBUG, ConnectionManager.class, "Can't close connection that still in use");
				return;
			}

			Log.log(Log.DEBUG,ConnectionManager.class, "Closing connection to "+ connect.info);
			connect.logoutQuietly();
			connections.remove(connect);
		}
	} //}}}

	//{{{ cleanup()
	/** Closes all connections, clears out every hash table and list. Recommended before unloading. */
	public static void cleanup() {
		DirectoryCache.clearAllCachedDirectories();
		for (Connection c: connections) {
			c.logoutQuietly();
		}
		try {
			client.removeAllIdentity();
		}
		catch (Exception e) {}
		connections.clear();
		logins.clear();
		passwords.clear();
		passphrases.clear();
		masterKey = null;
		restoredPasswords=false;
		client = null;
	}

	//{{{ static initializer
	static
	{
		restoredPasswords = false;
		lock = new Object();
		connections = new ArrayList<Connection>();
		logins = new HashMap<String, ConnectionInfo>();
		passwords = new HashMap<String, String>();
		passphrases = new HashMap<String, String>();

		String settingsDirectory = jEdit.getSettingsDirectory();
		if(settingsDirectory == null)
		{
			Log.log(Log.WARNING, ConnectionManager.class, "-nosettings command line switch specified;");
			Log.log(Log.WARNING, ConnectionManager.class, "passwords will not be saved.");
		}
		else
		{
			String passwordDirectory = MiscUtilities.constructPath(settingsDirectory, "cache");
			passwordFile = new File(MiscUtilities.constructPath(passwordDirectory,"password-cache"));
			passwordFile.getParentFile().mkdirs();
			try {
				passwordFile.createNewFile();
			} catch(IOException e) {
				Log.log(Log.WARNING,ConnectionManager.class, "Unable to create password file: " + passwordFile);
				passwordFile = null;
			}
		}
	} //}}}

}//}}}
