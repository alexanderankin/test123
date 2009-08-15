package tasklist;

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeStringComparator implements Comparator<DefaultMutableTreeNode> {
    public int compare( DefaultMutableTreeNode o1, DefaultMutableTreeNode o2 ) {
        if (o1 == null && o2 == null) {
            return 0;   
        }
        if (o1 == null && o2 != null) {
            return -1;   
        }
        if (o1 != null && o2 == null) {
            return 1;   
        }
        String a = ( ( DefaultMutableTreeNode ) o1 ).getUserObject().toString();
        String b = ( ( DefaultMutableTreeNode ) o2 ).getUserObject().toString();
        return a.compareTo( b );
    }
}