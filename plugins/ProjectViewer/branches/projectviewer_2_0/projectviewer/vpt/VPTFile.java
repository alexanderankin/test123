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
import java.awt.Color;
import javax.swing.Icon;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
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

	//}}}
	
	//{{{ Attributes
	
	private File		file;
	
	//}}}
	
	//{{{ Constructors 
	
	public VPTFile(String path) {
		this(new File(path));
	}
	
	public VPTFile(File file) {
		super(VPTNode.FILE, file.getName());
		this.file = file;
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
		return file.delete();
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
	} //}}}
	
	//{{{ isOpened() method 
	/**
	 *	Returns "true" if the node is a file and is currently opened in jEdit.
	 */
	public boolean isOpened() {
		return (org.gjt.sp.jedit.jEdit.getBuffer(file.getAbsolutePath()) != null);
	} //}}}
	
	//{{{ getIcon(boolean) method
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		return (isOpened() ? fileOpenedIcon : fileClosedIcon);
	} //}}}
	
	//{{{ getForegroundColor(boolean) method
	/**
	 *	Returns the node's foreground color.
	 *
	 *	@param	sel		If the node is currently selected.
	 */
	public Color getForegroundColor(boolean sel) {
		if (sel) return super.getForegroundColor(sel);
		return VFS.getDefaultColorFor(file.getName());
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
	/**
	 *	"Closes" the jEdit buffer that contains the file.
	 */
	public void close() {
		Buffer b = jEdit.getBuffer(getFile().getAbsolutePath());
		if (b != null) {
			jEdit.closeBuffer(jEdit.getActiveView(), b);
		}
	} //}}}
	
	//{{{ getNodePath()
	/**	Returns the path to the file represented by this node. */
	public String getNodePath() {
		return getFile().getAbsolutePath();
	} //}}}
	 
	//}}}
	
}
