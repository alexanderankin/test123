/**
 * SearchOptions.java - A set of options to define the file search to be performed.
 * :folding=explicit:collapseFolds=1:
 *
 * @author Nicholas O'Leary
 * @version $Id: SearchOptions.java 13893 Thu Oct 16 16:04:49 CDT 2008 keeleyt83 $
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
        return filter + " in " + path + " (" + resultCount + " occurrence" + ((resultCount != 1) ? "s" : "") + ")";
    }
}
