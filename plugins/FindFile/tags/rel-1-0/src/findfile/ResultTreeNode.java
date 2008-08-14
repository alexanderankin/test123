/**
 * ResultTreeNode.java - A single file found in a search.
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * @author Nicholas O'Leary
 * @version $Id: ResultTreeNode.java,v 1.1.1.1 2003/11/20 17:19:14 olearyni Exp $
 */

package findfile;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;

public class ResultTreeNode {
    /** The file's path with the original search path removed from the beginning. */
    public String shortname;

    /** The file's full path name. */
    public String fullname;

    /** The file's filename */
    public String filename;

    private char fileSep;
    /**
     * Constructor.
     * @param searchPath The path that was specfied in the original search
     * @param file The full path of the file
     */
    public ResultTreeNode(String searchPath, String file) {
	fullname = file;
	shortname = file.substring(searchPath.length());
	fileSep = VFSManager.getVFSForPath(fullname).getFileSeparator();
	if (shortname.indexOf(fileSep) == 0) {
	    shortname = shortname.substring(1);
	}

	filename = VFSManager.getVFSForPath(fullname).getFileName(fullname);
    }

    /**
     * Checks whether the file specified in this node is currently open.
     * QUESTION: Should this only look in the active view?
     * @return whether the file is open.
     */
    public boolean isOpen() {
	return (jEdit.getBuffer(fullname) != null);
    }

    /**
     * @return the shortname
     */

    @Override public String toString() {
	if (jEdit.getProperty(FindFileOptionPane.OPTIONS + "hidePath",
		"false").equals("false"))
	{
	    return shortname;
	} else {
	    return filename;
	}


    }
}