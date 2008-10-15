/**
 * FindFileRequest.java - A WorkRequest to perform the file search in the appropriate thread.
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * @author Nicholas O'Leary
 * @version $Id: FindFileRequest.java,v 1.1.1.1 2003/11/20 17:19:14 olearyni Exp $
 */

package findfile;

//{{{ Imports
import javax.swing.tree.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.*;
//}}}

public class FindFileRequest extends WorkRequest {
    //{{{ Private members
    private View view;
    private FindFileResults results;
    private SearchOptions searchOptions;
    //}}}

    //{{{ Constructor
    /**
     * Constructor.
     * @param view The active view.
     * @param results A FindFileResults pane to place the results.
     * @param searchOptions The set of options for the search.
     */
    public FindFileRequest(View view, FindFileResults results,
	     SearchOptions searchOptions)
    {
	this.view = view;
	this.results = results;
	this.searchOptions = searchOptions;
    }//}}}

    //{{{ run
    /**
     * Perform the search.
     */
    public void run() {
	setStatus(jEdit.getProperty("FindFilePlugin.status-message.started"));
	DirectoryListSet dls = new DirectoryListSet(searchOptions.path, searchOptions.filter,searchOptions.recursive);

	String []dirs = dls.getFiles(view);
	final DefaultMutableTreeNode rootSearchNode = new DefaultMutableTreeNode(searchOptions);
	if (dirs != null) {
	    for (int i = 0; i != dirs.length; i++) {
		rootSearchNode.add(new DefaultMutableTreeNode(new ResultTreeNode(searchOptions.path,dirs[i].toString())));
	    }
	}
	view.getDockableWindowManager().addDockableWindow("FindFilePlugin");
	setStatus(jEdit.getProperty("FindFilePlugin.status-message.started"));
	searchOptions.setResultCount(rootSearchNode.getChildCount());
	VFSManager.runInAWTThread(new Runnable() {
	    public void run() {
		results.searchDone(rootSearchNode, searchOptions.filter, searchOptions.path);
	    }
	} );
    }//}}}
}