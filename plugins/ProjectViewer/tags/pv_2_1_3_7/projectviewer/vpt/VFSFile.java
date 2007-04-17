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
import java.io.File;
import java.io.IOException;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import projectviewer.ProjectViewer;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Models a file accessed using jEdit's VFS.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VFSFile extends VPTNode {

	//{{{ Constants

	private final static Icon fileClosedIcon =
		new ImageIcon(VFSFile.class.getResource("/projectviewer/images/remote_file.png"));;
	private final static Icon fileOpenedIcon 	=  (ImageIcon) GUIUtilities.loadIcon("OpenFile.png");

	private final static ProjectViewerConfig config = ProjectViewerConfig.getInstance();
	private static FileSystemView fsView;

	//}}}

	//{{{ Attributes
	private String	path;
	private Color	fileTypeColor;
	private Icon	fileIcon;
	private boolean	loadedIcon;
	//}}}

	//{{{ +VFSFile(String) : <init>
	public VFSFile(String path) {
		super(VFSManager.getVFSForPath(path).getFileName(path), false);
		this.path = path;
		this.fileTypeColor = VFS.getDefaultColorFor(getName());
		this.loadedIcon = false;
	} //}}}

	//{{{ +canWrite() : boolean
	/**
	 *	Returns true. Since we can't check (easily) if we can or cannot write
	 *	to a file using the VFS api, let the VFS manager decide it, and show
	 *	an error in case it can't write...
	 */
	public boolean canWrite() {
		return true;
	} //}}}

	//{{{ +delete() : boolean
	/**
	 *	Deletes the file from disk. Before deleting, try to close the file.
	 */
	public boolean delete() {
		VFS vfs = VFSManager.getVFSForPath(path);
		if ((vfs.getCapabilities() & VFS.DELETE_CAP) != 0) {
			close();
			ProjectViewer v = ProjectViewer.getViewer(jEdit.getActiveView());
			Object s = vfs.createVFSSession(path, v);
			try {
				vfs._delete(s, path, v);
			} catch (IOException ioe) {
				open();
				Log.log(Log.DEBUG, this, ioe);
				return false;
			}
		}
		return true;
	} //}}}

	//{{{ +isOpened() : boolean
	/** Returns "true" if the node is a file and is currently opened in jEdit. */
	public boolean isOpened() {
		return (org.gjt.sp.jedit.jEdit.getBuffer(path) != null);
	} //}}}

	//{{{ +getIcon(boolean) : Icon
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		Icon baseIcon;
		if (isOpened()) {
			baseIcon = fileOpenedIcon;
		} else {
			if (config.getUseSystemIcons()) {
				if (!loadedIcon) {
					if (fsView == null) fsView = FileSystemView.getFileSystemView();
					File f = new File(getName());
					fileIcon = fsView.getSystemIcon(f);
					loadedIcon = true;
				}
				baseIcon = (fileIcon != null) ? fileIcon : fileClosedIcon;
			} else {
				baseIcon = fileClosedIcon;
			}
		}
		return IconComposer.composeIcon(null, path, baseIcon);
	} //}}}

	//{{{ +getForegroundColor(boolean) : Color
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
	} //}}}

	//{{{ +toString() : String
	/** Returns a string representation of the current node. */
	public String toString() {
		return "VFSFile [" + path + "]";
	} //}}}

	//{{{ +canOpen() : boolean
	/**	File nodes can be opened, so return true. */
	public boolean canOpen() {
		return true;
	} //}}}

	//{{{ +open() : void
	/**
	 *	Opens a new buffer in jEdit with the file pointed by this node. The
	 *	buffer is loaded in the currently active view.
	 */
	public void open() {
		jEdit.openFile(jEdit.getActiveView(), getNodePath());
		jEdit.getActiveView().getTextArea().requestFocus();
	} //}}}

	//{{{ +close() : void
	/** "Closes" the jEdit buffer that contains the file. */
	public void close() {
		Buffer b = jEdit.getBuffer(path);
		if (b != null) {
			jEdit.closeBuffer(jEdit.getActiveView(), b);
		}
	} //}}}

	//{{{ +getNodePath() : String
	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return path;
	} //}}}

	//{{{ +compareToNode(VPTNode) : int
	/** Compares a VFSFile node to another VPTNode. */
	public int compareToNode(VPTNode node) {
		if (node.getAllowsChildren()) {
			return 1;
		} else {
			return compareName(node);
		}
	} //}}}

}

