/*
 * FtpPlugin.java - Main class of virtual filesystem
 * Copyright (C) 2000 Slava Pestov
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

import java.awt.Component;
import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;

public class FtpPlugin extends EBPlugin
{
	public void start()
	{
		loginHash = new Hashtable();
		VFSManager.registerVFS(FtpVFS.PROTOCOL,new FtpVFS());
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("ftp"));
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof EditorExiting)
		{
			// Clear cached directory listings
			DirectoryCache.clearAllCachedDirectories();
		}
		else if(msg instanceof VFSUpdate)
		{
			VFSUpdate vmsg = (VFSUpdate)msg;
			DirectoryCache.clearCachedDirectory(vmsg.getPath());
		}
	}

	public static void showOpenFTPDialog(View view)
	{
		String path = ((FtpVFS)VFSManager.getVFSForProtocol("ftp"))
			.showBrowseDialog(new Object[1],view);
		if(path != null)
		{
			String[] files = GUIUtilities.showVFSFileDialog(
				view,path,VFSBrowser.OPEN_DIALOG,true);
			if(files == null)
				return;

			Buffer buffer = null;
			for(int i = 0; i < files.length; i++)
			{
				Buffer _buffer = jEdit.openFile(null,files[i]);
				if(_buffer != null)
					buffer = buffer;
			}
			if(buffer != null)
				view.setBuffer(buffer);
		}
	}

	public static void showSaveFTPDialog(View view)
	{
		String path = ((FtpVFS)VFSManager.getVFSForProtocol("ftp"))
			.showBrowseDialog(new Object[1],view);
		if(path != null)
		{
			String[] files = GUIUtilities.showVFSFileDialog(
				view,path,VFSBrowser.SAVE_DIALOG,false);
			if(files == null)
				return;

			view.getBuffer().save(view,files[0],true);
		}
	}

	/**
	 * If a password has been saved for the host and user name in the
	 * session, it sets the value of the session's PASSWORD_KEY value
	 * accordingly. Otherwise, a login dialog box is displayed.
	 * @param session The VFS session
	 * @param comp The component that will parent the login dialog box
	 * @return True if everything is ok, false if the user cancelled the
	 * operation
	 */
	public static boolean showLoginDialog(Object _session, Component comp)
	{
		VFSSession session = (VFSSession)_session;
		String host = (String)session.get(VFSSession.HOSTNAME_KEY);
		String user = (String)session.get(VFSSession.USERNAME_KEY);
		String password = (String)session.get(VFSSession.PASSWORD_KEY);

		if(host != null)
		{
			LoginInfo login = (LoginInfo)loginHash.get(host);

			if(login != null && (user == null || login.user.equals(user)))
			{
				if(user == null)
				{
					user = login.user;
					session.put(VFSSession.USERNAME_KEY,user);
				}

				if(password == null)
				{
					password = login.password;
					session.put(VFSSession.PASSWORD_KEY,password);
				}
			}

			if(user != null && password != null)
				return true;
		}

		/* since this can be called at startup time,
		 * we need to hide the splash screen. */
		GUIUtilities.hideSplashScreen();

		LoginDialog dialog = new LoginDialog(comp,host,user,password);
		if(!dialog.isOK())
			return false;

		host = dialog.getHost();
		user = dialog.getUser();
		password = dialog.getPassword();

		session.put(VFSSession.HOSTNAME_KEY,host);
		session.put(VFSSession.USERNAME_KEY,user);
		session.put(VFSSession.PASSWORD_KEY,password);

		loginHash.put(host,new LoginInfo(user,password));

		return true;
	}

	/**
	 * Forgets all saved passwords.
	 * @since jEdit 2.6pre3
	 */
	public static void forgetPasswords()
	{
		loginHash.clear();
	}

	static class LoginInfo
	{
		String user, password;

		LoginInfo(String user, String password)
		{
			this.user = user;
			this.password = password;
		}
	}

	private static Hashtable loginHash;
}
