package findfile;

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Comparator used for sorting results.
 * @author Nicholas O'Leary
 * @version $Id: ResultComparator.java,v 1.1.1.1 2003-11-20 17:19:14 olearyni Exp $
 */
public class ResultComparator implements Comparator
{
   public static final int SORT_BY_PATH_AZ     = 0;
   public static final int SORT_BY_PATH_ZA     = 1;
   public static final int SORT_BY_FILENAME_AZ = 2;
   public static final int SORT_BY_FILENAME_ZA = 3;
   
   private int sortBy;
   
   /**
    * Constructor.
    * @param sortBy The order of the sort.
    */
   public ResultComparator(int sortBy)
   {
      this.sortBy = sortBy;
   }
   
   /**
    * Compares two results.
    * @param o1 A ResultTreeNode
    * @param o2 A ResultTreeNode
    */
   public int compare(Object o1, Object o2)
   {
      ResultTreeNode r1 = (ResultTreeNode)((DefaultMutableTreeNode)o1).getUserObject();
      ResultTreeNode r2 = (ResultTreeNode)((DefaultMutableTreeNode)o2).getUserObject();
      switch (sortBy)
      {
         case ResultComparator.SORT_BY_FILENAME_AZ:
            return r1.filename.compareToIgnoreCase(r2.filename);
         case ResultComparator.SORT_BY_FILENAME_ZA:
            return -r1.filename.compareToIgnoreCase(r2.filename);
         case ResultComparator.SORT_BY_PATH_AZ:
            return r1.fullname.compareToIgnoreCase(r2.fullname);
         case ResultComparator.SORT_BY_PATH_ZA:
            return -r1.fullname.compareToIgnoreCase(r2.fullname);
         default:
      }
      return 0;
   }

}
