/*
 * FtpPlugin.java - Main class of FTP plugin
 * Copyright (C) 2000, 2002 Slava Pestov
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

import java.awt.Component;
import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;

public class FtpPlugin extends EditPlugin
{
	public void start()
	{
		VFSManager.registerVFS(FtpVFS.FTP_PROTOCOL,new FtpVFS(false));
		VFSManager.registerVFS(FtpVFS.SFTP_PROTOCOL,new FtpVFS(true));
	}

	public void stop()
	{
		DirectoryCache.clearAllCachedDirectories();
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("ftp"));
	}

	public static void showOpenFTPDialog(View view, boolean secure)
	{
		String path = ((FtpVFS)VFSManager.getVFSForProtocol(
			secure ? "sftp" : "ftp"))
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
					buffer = _buffer;
			}
			if(buffer != null)
				view.setBuffer(buffer);
		}
	}

	public static void showSaveFTPDialog(View view, boolean secure)
	{
		String path = ((FtpVFS)VFSManager.getVFSForProtocol(
			secure ? "sftp" : "ftp"))
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

	public static int parsePermissions(String s)
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
