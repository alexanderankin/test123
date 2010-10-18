/*
* FtpVFS.java - Ftp VFS
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.io.LocalFileSaveTask;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;

/**
* FTP VFS.
* @author Slava Pestov
* @author Vadim Voituk
* @version $Id$
*/
public class FtpVFS extends VFS
{
	public static final String FTP_PROTOCOL = "ftp";
	public static final String SFTP_PROTOCOL = "sftp";
	
	public static final String EA_OWNER_USER = "user";
	public static final String EA_OWNER_GROUP = "group";
	
	// same as File VFS permissions key should be used!!!!
	public static final String PERMISSIONS_PROPERTY = "FileVFS__perms";
	public static final String MD5SUM_PROPERTY      = "FtpVFS__MD5";
	
	//{{{ FtpVFS method
	public FtpVFS(boolean secure)
	{
		super(getProtocol(secure), READ_CAP | WRITE_CAP | BROWSE_CAP
			| DELETE_CAP | RENAME_CAP | MKDIR_CAP,
			getExtendedAttributes(secure));
		
		this.secure = secure;
	} //}}}
	
	//{{{ getProtocol() method
	public static String getProtocol(boolean secure)
	{
		return (secure ? SFTP_PROTOCOL : FTP_PROTOCOL);
	} //}}}
	
	//{{{ getExtendedAttributes() method
	public static String[] getExtendedAttributes(boolean secure)
	{
		return secure 
			? new String[] { EA_TYPE, EA_SIZE }
			: new String[] { EA_TYPE, EA_SIZE, EA_STATUS };
	} //}}}
	
	//{{{ getDefaultPort() method
	public static int getDefaultPort(boolean secure)
	{
		return (secure ? 22 : 21);
	} //}}}
	
	//{{{ showBrowseDialog() method
	public String showBrowseDialog(Object[] session, Component comp)
	{
		FtpSession newSession = (FtpSession)createVFSSession(null,comp);
		if(newSession == null)
			return null;
		
		if(session != null)
			session[0] = newSession;
		
		// need trailing / for 'open from ftp server' and 'save to ftp
		// server' commands to detect that the path is in fact a
		// directory
		return getProtocol(secure) + "://" + newSession.info.getUser()
			+ "@" + newSession.info.getHost()
			+ ":" + newSession.info.getPort() + "/~/";
	} //}}}
	
	//{{{ getFileName() method
	public String getFileName(String path)
	{
		FtpAddress address = new FtpAddress(path);
		if(address.getPath().equals("/") || address.getPath().length() == 1)
		{
			address.setPath("");
			return address.toString();
		}
		else
			return super.getFileName(address.getPath());
	} //}}}
	
	//{{{ getParentOfPath() method
	public String getParentOfPath(String path)
	{
		FtpAddress address = new FtpAddress(path);
		address.setPath( super.getParentOfPath(address.getPath()) );
		return address.toString();
	} //}}}
	
	//{{{ constructPath() method
	public String constructPath(String parent, String path)
	{
		if(path.startsWith("~"))
			path = "/" + path;
		
		if(path.startsWith("/"))
		{
			FtpAddress address = new FtpAddress(parent);
			address.setPath(path);
			return address.toString();
		}
		else if(parent.endsWith("/"))
			return parent + path;
		else
			return parent + '/' + path;
	} //}}}
	
	//{{{ reloadDirectory() method
	public void reloadDirectory(String path)
	{
		DirectoryCache.clearCachedDirectory(path);
	} //}}}
	
	//{{{ FtpSession class
	static class FtpSession
	{
		ConnectionInfo info;
		Connection connection;
		
		FtpSession(ConnectionInfo info)
		{
			this.info = info;
		}
	} //}}}
	
	//{{{ createVFSSession() method
	public Object createVFSSession(String path, Component comp)
	{
		try {
			Log.log(Log.DEBUG, this, "FtpVFS.createVFSSession("+path+", "+comp+")" );
			ConnectionInfo info = ConnectionManager.getConnectionInfo(comp,
				path == null ? null : new FtpAddress(path));
			
			if(info == null)
				return null;
			else
				return new FtpSession(info);
		}
		catch(IllegalArgumentException ia) {
			// FtpAddress.<init> can throw this
			return null;
		}
	} //}}}
	
	//{{{ _endVFSSession() method
	public void _endVFSSession(Object _session, Component comp)
	{
		FtpSession session = (FtpSession)_session;
		ConnectionManager.releaseConnection(session.connection);
	} //}}}
	
	//{{{ _canonPath() method
	public String _canonPath(Object _session, String path, Component comp) throws IOException
	{
		FtpAddress address = new FtpAddress(path);
		
		if(address.getPath().startsWith("/~")) {
			Connection session = getConnection(_session);
			
			if(session.home != null) {
				if(session.home.endsWith("/"))
					address.setPath( session.home + address.getPath().substring(2) );
				else
					address.setPath( session.home + '/' + address.getPath().substring(2) );
				if(address.getPath().endsWith("/") && address.getPath().length() != 1) {
					address.setPath( address.getPath().substring(0,address.getPath().length() - 1) );
				}
			}
		}
		
		return address.toString();
	} //}}}
	
	//{{{ _listFiles() method
	public VFSFile[] _listFiles(Object _session, String url, Component comp) throws IOException
	{
		VFSFile[] directory = DirectoryCache.getCachedDirectory(url);
		if(directory != null)
			return directory;
		
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(url);
		
		directory = session.listDirectory(address.getPath());
		
		if(directory != null)
		{
			for(int i = 0; i < directory.length; i++)
			{
				FtpDirectoryEntry entry = (FtpDirectoryEntry)directory[i];
				if(entry.getType() == FtpDirectoryEntry.LINK)
					resolveSymlink(_session,url,entry);
				else
				{
					// prepend directory to create full path
					entry.setPath(constructPath(url,entry.getName()));
					entry.setDeletePath(entry.getPath());
				}
			}
			
			DirectoryCache.setCachedDirectory(url,directory);
		}
		
		return directory;
	} //}}}
	
	//{{{ FtpDirectoryEntry class
	@SuppressWarnings("serial")
	static class FtpDirectoryEntry extends VFSFile
	{
		public static final int LINK = 10;
		int permissions;
		String permissionString;
		
		private String ownerUser;
		private String ownerGroup;
		
		public FtpDirectoryEntry(String name, String path,
			String deletePath, int type, long length,
			boolean hidden, int permissions,
			String permissionString)
		{
			super(name,path,deletePath,type,length,hidden);
			this.permissions = permissions;
			this.permissionString = permissionString;
		}
		
		public String getExtendedAttribute(String name)
		{
			if (name.equals(FtpVFS.EA_OWNER_USER))
				return ownerUser;
			if (name.equals(FtpVFS.EA_OWNER_GROUP))
				return ownerGroup;
			if(name.equals(EA_TYPE) || name.equals(EA_SIZE))
				return super.getExtendedAttribute(name);
			if(name.equals(EA_STATUS))
				return permissionString;
			return null;
		}
		
		public void setOwner(String name, String group) {
			this.ownerUser = name==null || name.equals("") ? null : name;
			this.ownerGroup = group==null || group.equals("") ? null : group;
		}
		
	} //}}}
	
	//{{{ _getFile() method
	// this method is severely broken, and in many cases, most fields
	// of the returned directory entry will not be filled in.
	public VFSFile _getFile(Object _session, String path, Component comp) throws IOException
	{
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(path);
		
		FtpDirectoryEntry dirEntry = session.getDirectoryEntry(address.getPath());
		if(dirEntry != null)
		{
			if(dirEntry.getType() == FtpDirectoryEntry.LINK) {
				String parentPath = MiscUtilities.getParentOfPath(path);
				resolveSymlink(_session,parentPath,dirEntry);
			}
			
			// since _getDirectoryEntry() is always called
			// before the file is loaded (this is undocumented,
				// but true, because BufferIORequest needs to know
				// the size of the file being loaded) we can
			// check if the path name in the session is the
			// path this method was passed.
			if(address.toString().equals(path))
			{
				Buffer buffer = jEdit.getBuffer(path);
				if(buffer != null)
				{
					Log.log(Log.DEBUG,this,path + " has permissions 0" + Integer.toString(dirEntry.permissions,8));
					buffer.setIntegerProperty(PERMISSIONS_PROPERTY, dirEntry.permissions);
					//buffer.setStringProperty(MD5_PROPERTY, );
				} else {
					//Log.log(Log.ERROR,this, path + " not open?");
				}
			}
		}

		
		return dirEntry;
	} //}}}
	
	//{{{ _delete() method
	public boolean _delete(Object _session, String url, Component comp)
	throws IOException
	{
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(url);
		
		VFSFile directoryEntry = _getFile(
			_session,url,comp);
		if(directoryEntry == null)
			return false;
		
		boolean returnValue;
		
		if(directoryEntry.getType() == VFSFile.FILE)
			returnValue = session.removeFile(address.getPath());
		else //if(directoryEntry.type == VFSFile.DIRECTORY)
			returnValue = session.removeDirectory(address.getPath());
		
		DirectoryCache.clearCachedDirectory(getParentOfPath(url));
		VFSManager.sendVFSUpdate(this,url,true);
		
		return returnValue;
	} //}}}
	
	//{{{ _rename() method
	public boolean _rename(Object _session, String from, String to,
		Component comp) throws IOException
	{
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(from);
		
		String toPath = new FtpAddress(to).getPath();
		
		VFSFile directoryEntry = _getFile(
			_session,from,comp);
		if(directoryEntry == null)
			return false;
		
		directoryEntry = _getFile(_session,to,comp);
		if(directoryEntry != null && directoryEntry.getType() == VFSFile.FILE
			&& !address.getPath().equalsIgnoreCase(toPath))
		session.removeFile(toPath);
		
		boolean returnValue = session.rename(address.getPath(), toPath);
		
		DirectoryCache.clearCachedDirectory(getParentOfPath(from));
		DirectoryCache.clearCachedDirectory(getParentOfPath(to));
		VFSManager.sendVFSUpdate(this,from,true);
		VFSManager.sendVFSUpdate(this,to,true);
		
		return returnValue;
	} //}}}
	
	//{{{ _mkdir() method
	public boolean _mkdir(Object _session, String directory, Component comp) throws IOException
	{
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(directory);
		
		boolean returnValue = session.makeDirectory(address.getPath());
		
		DirectoryCache.clearCachedDirectory(getParentOfPath(directory));
		VFSManager.sendVFSUpdate(this,directory,true);
		
		return returnValue;
	} //}}}
	
	//{{{ _createInputStream() method
	public InputStream _createInputStream(Object _session, String path,
		boolean ignoreErrors, Component comp) throws IOException
	{
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(path);
		
		return session.retrieve(address.getPath());
	} //}}}
	
	//{{{ _createOutputStream() method
	public OutputStream _createOutputStream(Object _session, String path,
		Component comp) throws IOException
	{
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(path);
		
		OutputStream out = session.store(address.getPath());
		DirectoryCache.clearCachedDirectory(getParentOfPath(path));
		
		return out;
	} //}}}
	
	//{{{ _finishTwoStageSave() method
	public void _finishTwoStageSave(Object _session, Buffer buffer, String path,
		Component comp) throws IOException {
		Log.log(Log.DEBUG, "Run FtpVFS._finishTwoStageSave()", path);
		Connection session = getConnection(_session);
		
		FtpAddress address = new FtpAddress(path);
		
		// Restore permissions
		int permissions = buffer.getIntegerProperty(PERMISSIONS_PROPERTY,0);
		if(permissions != 0)
			session.chmod(address.getPath(),permissions);
	} //}}}
	
	
	/**{@inheritDoc}*/
	public void _saveComplete(java.lang.Object session, Buffer buffer,
		java.lang.String path, java.awt.Component comp)
	throws java.io.IOException
	{
		//String s = new String(StandardUtilities.md5(buffer.getText()), "UTF-8");
		//GUIUtilities.message(comp, s, null);
		//buffer.setStringProperty(FtpVFS.MD5SUM_PROPERTY, StandardUtilities.md5(buffer.getText()) );
		//Log.log(Log.DEBUG, "TEST", buffer.getStringProperty(FtpVFS.MD5SUM_PROPERTY));
	}
	
	
	/**{@inheritDoc}*/
	@Override
	public void _backup(Object session, String path, Component comp) throws IOException {
		
		Buffer buffer = jEdit.getBuffer(path);
		if (buffer == null)
			return;
		
		String backPath = getBackupFilepath(path);
		if (backPath == null)
			return;
		
		File f = new File(backPath);
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		
		if ( f.exists() && !f.canWrite()) {
			Log.log(Log.ERROR, this, "Can't write file " + backPath);
			return;
		}
		
		ThreadUtilities.runInBackground( new LocalFileSaveTask(f, buffer.getText(), buffer.getStringProperty(JEditBuffer.ENCODING)) );
	}

	/**
	 * @param path
	 * @param backupDir
	 * @return
	 */
	private String getBackupFilepath(String path) {
		String backupDir = jEdit.getProperty("backup.directory");
		if (backupDir == null || backupDir.equals(""))
			return null;
		FtpAddress uri = new FtpAddress(path);
		String backFile = "_"+uri.getScheme()+"_"+uri.getUser() + "@" + uri.getHost() + uri.getPath();
		return MiscUtilities.concatPath(backupDir, backFile);
	}
	
	//{{{ Private members
	private boolean secure;
	
	//{{{ getConnection() method
	private static Connection getConnection(Object _session) throws IOException
	{
		FtpSession session = (FtpSession)_session;
		if(session.connection == null)
			session.connection = ConnectionManager.getConnection(session.info);
		
		return session.connection;
	} //}}}
	
	//{{{ resolveSymlink() method
	private void resolveSymlink(Object _session, String url, FtpDirectoryEntry entry)
	throws IOException
	{
		Connection session = getConnection(_session);
		
		String path = constructPath(url,entry.getName());
		String[] nameArray = new String[] { entry.getName() };
		String link = session.resolveSymlink(new FtpAddress(path).getPath(), nameArray);
		entry.setName(nameArray[0]);
		
		if(link == null)
		{
			entry.setPath(path);
			entry.setType(FtpVFS.FtpDirectoryEntry.FILE);
		}
		else
		{
			link = constructPath(url,link);
			
			FtpVFS.FtpDirectoryEntry linkDirEntry;
			try
			{
				linkDirEntry = (FtpVFS.FtpDirectoryEntry)
				_getFile(_session,link,null);
			}
			catch(IOException io)
			{
				linkDirEntry = null;
			}
			
			if(linkDirEntry == null)
				entry.setType(FtpDirectoryEntry.FILE);
			else if(linkDirEntry.getType() == FtpDirectoryEntry.LINK)
			{
				Log.log(Log.WARNING,this,entry.getName() + ": Not following more than one symbolic link");
				entry.setType(FtpDirectoryEntry.FILE);
				entry.permissions = 600;
			}
			else
			{
				entry.setType(linkDirEntry.getType());
				entry.permissions = linkDirEntry.permissions;
			}
			
			entry.setPath(link);
			entry.setDeletePath(path);
		}
	} //}}}
	
	//}}}
}
