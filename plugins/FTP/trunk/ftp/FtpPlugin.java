/*
 * FtpPlugin.java - Main class of FTP plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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
import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

// SSHTools uses log4j
import org.apache.log4j.*;
//}}}

public class FtpPlugin extends EditPlugin
{
	//{{{ stop() method
	public void stop()
	{
		DirectoryCache.clearAllCachedDirectories();
	} //}}}

	//{{{ showOpenFTPDialog() method
	public static void showOpenFTPDialog(View view, boolean secure)
	{
		if(secure && !OperatingSystem.hasJava14())
		{
			GUIUtilities.error(view,"vfs.sftp.no-java14",null);
			return;
		}

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
	} //}}}

	//{{{ showSaveFTPDialog() method
	public static void showSaveFTPDialog(View view, boolean secure)
	{
		if(secure && !OperatingSystem.hasJava14())
		{
			GUIUtilities.error(view,"vfs.sftp.no-java14",null);
			return;
		}

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
	} //}}}

	//{{{ initSshtoolsHome() method
	public static void initSshtoolsHome()
	{
		String path = MiscUtilities.constructPath(
			jEdit.getSettingsDirectory(),"sshtools");

		System.getProperties().put("sshtools.home",path);

		String[] files = new String[] {
			"authorization.xml",
			"automation.xml",
			"sshtools.xml"
		};

		File dir = new File(path,"conf");
		dir.mkdirs();

		try
		{
			for(int i = 0; i < files.length; i++)
			{
				File file = new File(dir,files[i]);
				if(!file.exists())
				{
					copy(FtpPlugin.class.getResourceAsStream(
						"/conf/" + files[i]),
						new FileOutputStream(
						file));
				}
			}

			RollingFileAppender log = new RollingFileAppender(
				new PatternLayout(),
				MiscUtilities.constructPath(path,"ssh.log"),
				true);
			log.setMaxFileSize("100KB");
			BasicConfigurator.configure(log);
		}
		catch(IOException io)
		{
			Log.log(Log.ERROR,FtpPlugin.class,io);
		}
	} //}}}

	//{{{ Private members

	//{{{ copy() method
	private static void copy(InputStream in, OutputStream out) throws IOException
	{
		try
		{
			byte[] buf = new byte[4096];
			int count;
			while((count = in.read(buf,0,buf.length)) != -1)
			{
				out.write(buf,0,count);
			}
		}
		finally
		{
			in.close();
			out.close();
		}
	} //}}}

	//}}}
}
