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
import javax.swing.filechooser.FileSystemView;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Models a file that is part of a project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTFile extends VPTNode {

	//{{{ Constants

	private final static Icon fileClosedIcon 	= GUIUtilities.loadIcon("File.png");
	private final static Icon fileOpenedIcon 	= GUIUtilities.loadIcon("OpenFile.png");
	private static Icon noFileIcon				= null;

	private final static ProjectViewerConfig config = ProjectViewerConfig.getInstance();
	private static FileSystemView fsView;

	//}}}

	//{{{ Attributes

	private File	file;
	private Color	fileTypeColor;

	private Icon	fileIcon;
	private boolean	loadedIcon;
	//}}}

	//{{{ +VPTFile(String) : <init>

	public VPTFile(String path) {
		this(new File(path));
	} //}}}

	//{{{ +VPTFile(File) : <init>
	public VPTFile(File file) {
		super(file.getName());
		this.file = file;
		this.fileTypeColor = VFS.getDefaultColorFor(file.getName());
		this.fileIcon = null;
		this.loadedIcon = false;
	}

	//}}}

	//{{{ Public methods

	//{{{ +canWrite() : boolean
	/** Returns is the underlying file is writable. */
	public boolean canWrite() {
		return file.canWrite();
	} //}}}

	//{{{ +delete() : boolean
	/**
	 *	Deletes the file from disk. Before deleting, try to close the file.
	 */
	public boolean delete() {
		close();
		if (!file.delete()) {
			open();
			return false;
		}
		return true;
	} //}}}

	//{{{ +getFile() : File
	/** Return the file associated with this node. */
	public File getFile() {
		return file;
	} //}}}

	//{{{ +setFile(File) : void
	/** Sets the file associated with this node. */
	public void setFile(File f) {
		this.file = f;
		fileTypeColor = VFS.getDefaultColorFor(file.getName());
		setName(f.getName());
	} //}}}

	//{{{ +isOpened() : boolean
	/**
	 *	Returns "true" if the node is a file and is currently opened in jEdit.
	 */
	public boolean isOpened() {
		return (org.gjt.sp.jedit.jEdit.getBuffer(getFile().getAbsolutePath()) != null);
	} //}}}

	//{{{ +getIcon(boolean) : Icon
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		Icon baseIcon = fileClosedIcon;
		if (isOpened()) {
			baseIcon = fileOpenedIcon;
		} else {
			if (config.getUseSystemIcons()) {
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
		}
		return IconComposer.composeIcon(file, file.getAbsolutePath(), baseIcon);
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
		return "File [" + getFile().getAbsolutePath() + "]";
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
	} //}}}

	//{{{ +close() : void
	/** "Closes" the jEdit buffer that contains the file. */
	public void close() {
		Buffer b = jEdit.getBuffer(getFile().getAbsolutePath());
		if (b != null) {
			jEdit.closeBuffer(jEdit.getActiveView(), b);
		}
	} //}}}

	//{{{ +getNodePath() : String
	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return getFile().getAbsolutePath();
	} //}}}

	//}}}

}

