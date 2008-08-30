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

import javax.swing.Icon;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;

import projectviewer.VFSHelper;
//}}}

/**
 *	Models a directory that is part of a project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTDirectory extends VPTNode {

	//{{{ Constants
	private final static Icon dirClosedIcon 	= GUIUtilities.loadIcon("Folder.png");
	private final static Icon dirOpenedIcon 	= GUIUtilities.loadIcon("OpenFolder.png");
	//}}}

	protected String url;

	public VPTDirectory(String url) {
		super(VFSManager.getVFSForPath(url).getFileName(url), true);
		this.url = url;
	}


	/**
	 *	Returns the URL of this directory.
	 *
	 *	@since PV 3.0.0
	 */
	public String getURL() {
		return url;
	}


	/**
	 *	Changes this directory's URL.
	 *
	 *	@since PV 3.0.0
	 */
	public void setURL(String url)
	{
		this.url = url;
		setName(VFSManager.getVFSForPath(url).getFileName(url));
	}


	/** Returns the real name of this directory. */
	public String getFileName() {
		return VFSManager.getVFSForPath(url).getFileName(url);
	}

	/** Returns is the underlying file is writable. */
	public boolean canWrite() {
		try {
			VFSFile f = VFSHelper.getFile(url);
			return (f != null && f.isWriteable());
		} catch (IOException ioe) {
			return false;
		}
	}


	/**
	 *	Returns whether the VFS of the file allows renaming.
	 *
	 *	@since PV 3.0.0
	 */
	public boolean canRename()
	{
		int caps = VFSManager.getVFSForPath(url).getCapabilities();
		return (caps & VFS.RENAME_CAP) != 0;
	}


	/**
	 * Deleting directories is not supported.
	 *
	 * @return false
	 */
	public boolean delete() {
		return false;
	}

	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		return (expanded ? dirOpenedIcon : dirClosedIcon);
	}

	/** Returns a string representation of the current node. */
	public String toString() {
		return "Directory [" + url + "]";
	}

	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return url;
	}

	/** Directories have precedende over openable nodes... */
	public int compareTo(VPTNode node)
	{
		if (node.canOpen()) {
			return -1;
		} else if (node.isDirectory()) {
			return compareName(node);
		} else {
			return -1 * node.compareTo(this);
		}
	}

}

