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

import errorlist.ErrorSource;

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
	private String	canPath;
	private Color	fileTypeColor;

	private Icon	fileIcon;
	private boolean	loadedIcon;
	//}}}

	//{{{ Constructors

	public VPTFile(String path) {
		this(new File(path));
	}

	public VPTFile(File file) {
		super(file.getName());
		this.file = file;
		this.canPath = null;
		this.fileTypeColor = VFS.getDefaultColorFor(file.getName());
		this.fileIcon=null;
		this.loadedIcon = false;
	}

	//}}}

	//{{{ Public methods

	//{{{ canWrite() method
	/** Returns is the underlying file is writable. */
	public boolean canWrite() {
		return file.canWrite();
	} //}}}

	//{{{ delete() method
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

	//{{{ getFile() method
	/** Return the file associated with this node. */
	public File getFile() {
		return file;
	} //}}}

	//{{{ setFile(File) method
	/** Sets the file associated with this node. */
	public void setFile(File f) {
		this.file = f;
		this.canPath = null;
		fileTypeColor = VFS.getDefaultColorFor(file.getName());
		setName(f.getName());
	} //}}}

	//{{{ isOpened() method
	/**
	 *	Returns "true" if the node is a file and is currently opened in jEdit.
	 */
	public boolean isOpened() {
		if (config.isJEdit42()) {
			return (org.gjt.sp.jedit.jEdit.getBuffer(getFile().getAbsolutePath()) != null);
		} else {
			return (org.gjt.sp.jedit.jEdit.getBuffer(getCanonicalPath()) != null);
		}
	} //}}}

	//{{{ getIcon(boolean) method
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 *  @todo add decorations to the icon
	 */
	public Icon getIcon(boolean expanded) {
		Icon baseIcon=null;
		int base_state=0;	// use different base_states for different backgrounds (for chaching)
		if (isOpened()) {
			baseIcon = fileOpenedIcon;
			base_state=1;
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
					} else {
						if (noFileIcon == null) {
							noFileIcon = GUIUtilities.loadIcon("dirty.gif");
						}
						baseIcon = noFileIcon;
					}
				}
				else {
					baseIcon = (fileIcon != null) ? fileIcon : fileClosedIcon;
				}
			} else {
				baseIcon = fileClosedIcon;
			}
		}
		// check buffer states and generate composed icon
		int file_state=IconComposer.file_state_normal;
		// get file state
		String bufferName=file.getAbsolutePath();
		Buffer buffer = jEdit.getBuffer(bufferName);
		if(buffer!=null) {
			if(buffer.isDirty()) { file_state=IconComposer.file_state_changed; }
			else if(!buffer.isEditable()) { file_state=IconComposer.file_state_readonly; }
			//Log.log(Log.DEBUG, this, "file \""+bufferName+"\" state is :["+file_state+"]");
		}
		// get msg state
		int msg_state=IconComposer.msg_state_none;
		ErrorSource[] sources = ErrorSource.getErrorSources();
		for(int i=0;i<sources.length;i++) {
			if(sources[i].getFileErrorCount(bufferName)>0) {
				msg_state=IconComposer.msg_state_messages;
				break;
			}
		}
		return IconComposer.composeIcon(baseIcon,base_state,IconComposer.vc_state_none,0,file_state,msg_state);
	} //}}}

	//{{{ getForegroundColor(boolean) method
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

	//{{{ toString() method
	/** Returns a string representation of the current node. */
	public String toString() {
		return "File [" + getFile().getAbsolutePath() + "]";
	} //}}}

	//{{{ canOpen() method
	/**	File nodes can be opened, so return true. */
	public boolean canOpen() {
		return true;
	} //}}}

	//{{{ open() method
	/**
	 *	Opens a new buffer in jEdit with the file pointed by this node. The
	 *	buffer is loaded in the currently active view.
	 */
	public void open() {
		jEdit.openFile(jEdit.getActiveView(), getNodePath());
	} //}}}

	//{{{ close() method
	/** "Closes" the jEdit buffer that contains the file. */
	public void close() {
		Buffer b;
		if (config.isJEdit42()) {
			b = jEdit.getBuffer(getFile().getAbsolutePath());
		} else {
			b = jEdit.getBuffer(getCanonicalPath());
		}
		if (b != null) {
			jEdit.closeBuffer(jEdit.getActiveView(), b);
		}
	} //}}}

	//{{{ getNodePath()
	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return getFile().getAbsolutePath();
	} //}}}

	//{{{ getCanonicalPath() method
	/** Returns the file's canonical path. */
	public String getCanonicalPath() {
		if (this.canPath == null) {
			try {
				this.canPath = file.getCanonicalPath();
			} catch (IOException ioe) {
				Log.log(Log.WARNING, this, ioe);
			}
		}
		return canPath;
	} //}}}

	//}}}

}

