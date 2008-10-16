/**
 * ResultComparator.java - Comparator used for sorting results.
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * @author Nicholas O'Leary
 * @version $Id: ResultComparator.java 13891 Thu Oct 16 16:04:49 CDT 2008 keeleyt83 $
 */

package findfile;

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

public class ResultComparator implements Comparator {
    public static final int SORT_BY_PATH_AZ = 0;
    public static final int SORT_BY_PATH_ZA = 1;
    public static final int SORT_BY_FILENAME_AZ = 2;
    public static final int SORT_BY_FILENAME_ZA = 3;

    private int sortBy;

    /**
     * Constructor.
     * @param sortBy The order of the sort.
     */
    public ResultComparator(int sortBy) {
	this.sortBy = sortBy;
    }

    /**
     * Compares two results.
     * @param o1 A ResultTreeNode
     * @param o2 A ResultTreeNode
     */
    public int compare(Object o1, Object o2) {
	ResultTreeNode r1 = (ResultTreeNode) ((DefaultMutableTreeNode) o1).getUserObject();
	ResultTreeNode r2 = (ResultTreeNode) ((DefaultMutableTreeNode) o2).getUserObject();
	switch (sortBy) {
	    case ResultComparator.SORT_BY_FILENAME_AZ:
		return r1.filename.compareToIgnoreCase(r2.filename);
	    case ResultComparator.SORT_BY_FILENAME_ZA:
		return - r1.filename.compareToIgnoreCase(r2.filename);
	    case ResultComparator.SORT_BY_PATH_AZ:
		return r1.fullname.compareToIgnoreCase(r2.fullname);
	    case ResultComparator.SORT_BY_PATH_ZA:
		return - r1.fullname.compareToIgnoreCase(r2.fullname);
	    default:
	}
	return 0;
    }

}