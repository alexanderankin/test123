/*
 * :tabSize=4:indentSize=4:noTabs=false:
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
package projectviewer.vpt;

//{{{ Imports
import java.io.IOException;
import java.lang.ref.WeakReference;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;

import projectviewer.PVActions;
import projectviewer.config.AppLauncher;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Models a file that is part of a project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTFile extends VPTNode
{

	//{{{ Constants

	private final static Icon fileClosedIcon 	= GUIUtilities.loadIcon("File.png");
	private final static Icon fileOpenedIcon 	= GUIUtilities.loadIcon("OpenFile.png");
	private static Icon noFileIcon				= null;

	private static final AppLauncher appList  = AppLauncher.getInstance();
	private static FileSystemView fsView;

	//}}}

	//{{{ Attributes

	private String	url;
	private Color	fileTypeColor;

	private Icon	fileIcon;
	private boolean	loadedIcon;

	private WeakReference<Object> vfsSession;
	//}}}

	public VPTFile(String url)
	{
		super(VFSManager.getVFSForPath(url).getFileName(url), false);
		this.url = url;
		this.fileTypeColor = VFS.getDefaultColorFor(getName());
		this.fileIcon = null;
		this.loadedIcon = false;
	}


	/** Returns is the underlying file is writable. */
	public boolean canWrite()
	{
		return getFile().isWriteable();
	}


	/**
	 *	Deletes the file from the VFS. Before deleting, try to close
	 *	the file.
	 */
	public boolean delete()
	{
		close();
		/* XXX: TODO */
		return true;
	}


	/** Return the VFS file associated with this node. */
	public VFSFile getFile()
	{
		try {
			VFS vfs = VFSManager.getVFSForPath(url);
			Object session = getVFSSession(vfs);
			return vfs._getFile(session, url, null);
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
			return null;
		}
	}


	/**
	 *	Returns "true" if the node is a file and is currently opened
	 *	in jEdit.
	 */
	public boolean isOpened()
	{
		return (jEdit.getBuffer(url) != null);
	}


	/**
	 *	Returns the VFS URL for this file.
	 */
	public String getURL()
	{
		return url;
	}


	/**
	 *	Changes this file's URL.
	 */
	public void setURL(String url)
	{
		this.url = url;
	}


	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded)
	{
		Icon baseIcon = fileClosedIcon;
		if (isOpened()) {
			baseIcon = fileOpenedIcon;
		} else {
			/* XXX: Need to fix this now that we don't have file anymore.
			if (ProjectViewerConfig.getInstance().getUseSystemIcons()) {
				if (!loadedIcon) {
					if (file.exists()) {
						if (fsView == null) {
							fsView = FileSystemView.getFileSystemView();
						}
						fileIcon = fsView.getSystemIcon(file);
						loadedIcon = true;
						baseIcon = fileIcon;
					}
				} else {
					baseIcon = (fileIcon != null) ? fileIcon : fileClosedIcon;
				}
			}
			*/
			baseIcon = fileClosedIcon;
		}
		return IconComposer.composeIcon(getFile(), url, baseIcon);
	}



	/**
	 *	Returns the node's foreground color.
	 *
	 *	@param	sel		If the node is currently selected.
	 */
	public Color getForegroundColor(boolean sel) {
		if (sel) return super.getForegroundColor(sel);
		if (fileTypeColor == null) {
			return super.getForegroundColor(sel);
		} else {
			return fileTypeColor;
		}
	}



	/** Returns a string representation of the current node. */
	public String toString()
	{
		return "File [" + url + "]";
	}


	/**	File nodes can be opened, so return true. */
	public boolean canOpen()
	{
		return true;
	}


	/**
	 *	Opens a new buffer in jEdit with the file pointed by this node. The
	 *	buffer is loaded in the currently active view.
	 */
	public void open() {
		if (ProjectViewerConfig.getInstance().getUseExternalApps()
				&& appList.getAppName(url) != null) {
			appList.launchApp(url, jEdit.getActiveView());
		} else {
			jEdit.openFile(jEdit.getActiveView(), getNodePath());
			PVActions.requestFocus(jEdit.getActiveView().getTextArea());
		}
	}


	/** "Closes" the jEdit buffer that contains the file. */
	public void close()
	{
		Buffer b = jEdit.getBuffer(url);
		if (b != null) {
			jEdit.closeBuffer(jEdit.getActiveView(), b);
		}
	}


	/**	Returns the path to the file represented by this node. */
	public String getNodePath()
	{
		return url;
	}


	/** Files are at the same level of every leaf node. */
	public int compareToNode(VPTNode node)
	{
		if (!node.getAllowsChildren()) {
			return compareName(node);
		} else {
			return 1;
		}
	}


	private Object getVFSSession(VFS vfs)
	{
		if (vfsSession == null || vfsSession.get() == null) {
			if (vfs == null) {
				vfs = VFSManager.getVFSForPath(url);
			}
			vfsSession = new WeakReference<Object>(
							vfs.createVFSSession(url, null));
		}
		return vfsSession.get();
	}

}

