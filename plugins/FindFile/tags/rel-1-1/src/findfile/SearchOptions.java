/**
 * SearchOptions.java - A set of options to define the file search to be performed.
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * @author Nicholas O'Leary
 * @version $Id: SearchOptions.java,v 1.1.1.1 2003/11/20 17:19:14 olearyni Exp $
 */

package findfile;

public class SearchOptions {
    public int sortOrder;

    /** The root path for the search. */
    public String path;

    /** The file filter to apply. */
    public String filter;

    /** Whether to recurse into subdirectories. */
    public boolean recursive;

    /** Whether to open the files automatically. */
    public boolean openResults;

    private int resultCount = 0;

    public void setResultCount(int i) {
	resultCount = i;
    }

    @Override public String toString() {
	return filter + " in " + path + " (" + resultCount + " result" + ((resultCount != 1) ? "s" : "") + ")";
    }
}