/* $Id$
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

import java.awt.datatransfer.*;
import java.io.File;
import java.util.*;
import org.gjt.sp.jedit.*;

/** A project file.
 */
public final class ProjectFile implements Transferable {

	private final static int KEY_UNSET = -1;

	private static Comparator comparator;

	private String fullPath;
	private String name;
	private boolean opened;

	/** Create a new <code>ProjectFile</code>.
	 *
	 *@param  fullFilePath  Description of Parameter
	 */
	public ProjectFile(String fullFilePath) {
		fullPath = fullFilePath;
	}

	/** Returns a comparator for project files.
	 *
	 *@return    The comparator value
	 */
	public static Comparator getComparator() {
		if (comparator == null)
			comparator = new FileComparator();
		return comparator;
	}

	/** Performs a simple cast.
	 *
	 *@param  obj  Description of Parameter
	 *@return      Description of the Returned Value
	 */
	private static ProjectFile toProjectFile(Object obj) {
		return (ProjectFile) obj;
	}

	/** Returns <code>true</code> if this file is opened.
	 *
	 *@return    The opened value
	 */
	public boolean isOpened() {
		return isInBuffer();
	}

	/** Returns the path identifying this file.
	 *
	 *@return    The path value
	 */
	public String getPath() {
		return fullPath;
	}
    
    public void setPath(String aPath) {
       this.fullPath = aPath;
    }

	/** Returns the name of the file, with the path structure.
	 *
	 *@return    The name value
	 */
	public String getName() {
		if (name == null)
			name = getPath().substring(getPath().lastIndexOf(File.separatorChar) + 1);
		return name;
	}

	/** Returns the buffer for the given file, or <code>null</code> if
	 * this file isn't currently in a buffer.
	 *
	 *@return    The buffer value
	 */
	public Buffer getBuffer() {
		return jEdit.getBuffer(getPath());
	}

	/** Returns <code>true</code> if this file is currently in a JEdit buffer.
	 *
	 *@return    The inBuffer value
	 */
	public boolean isInBuffer() {
		return getBuffer() != null;
	}

	/** Returns an array of DataFlavor objects indicating the flavors the data
	 * can be provided in.
	 *
	 *@return    The transferDataFlavors value
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.javaFileListFlavor};
	}

	/** Returns whether or not the specified data flavor is supported for
	 * this object.
	 *
	 *@param  flavor  Description of Parameter
	 *@return         The dataFlavorSupported value
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(DataFlavor.javaFileListFlavor);
	}

	/** Returns an object which represents the data to be transferred.
	 *
	 *@param  flavor                          Description of Parameter
	 *@return                                 The transferData value
	 *@exception  UnsupportedFlavorException  Description of Exception
	 */
	public Object getTransferData(DataFlavor flavor)
			 throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor))
			throw new UnsupportedFlavorException(flavor);

		List fileList = new ArrayList(1);
		fileList.add(toFile());
		return fileList;
	}

	/** Returns <code>true</code> if project file exists.
	 *
	 *@return    Description of the Returned Value
	 */
	public boolean exists() {
		return toFile().exists();
	}

	/** Returns underlying <code>java.io.File</code>.
	 *
	 *@return    Description of the Returned Value
	 */
	public File toFile() {
		return new File(fullPath);
	}

	/** Returns <code>true</code> if the given path equals the path of
	 * this file.
	 *
	 *@param  aPath  Description of Parameter
	 *@return        Description of the Returned Value
	 */
	public boolean pathEquals(String aPath) {
		return getPath().equals(aPath);
	}

	/** Returns the name of the file.
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString() {
		return getName();
	}

	/** Returns <code>true</code> if the specified this object
	 * equals <code>obj</code>.
	 *
	 *@param  obj  Description of Parameter
	 *@return      Description of the Returned Value
	 */
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (!(obj instanceof ProjectFile))
			return false;
		return pathEquals(((ProjectFile) obj).getPath());
	}

	/** Returns the hash code.
	 *
	 *@return    Description of the Returned Value
	 */
	public int hashCode() {
		return getPath().hashCode();
	}

	/** A compare class.
	 *
	 *@author     ensonic
	 *@created    1. Juli 2002
	 */
	private final static class FileComparator implements Comparator {
		/**
		 * Compare the two objects.
		 *
		 *@param  obj1  Description of Parameter
		 *@param  obj2  Description of Parameter
		 *@return       Description of the Returned Value
		 */
		public int compare(Object obj1, Object obj2) {
			return toProjectFile(obj1).getName().compareTo(toProjectFile(obj2).getName());
		}

	}

}

