package tasklist;

import org.gjt.sp.jedit.jEdit;
import java.util.Comparator;


public class TaskComparator implements Comparator<Task> {
    public int compare( Task a, Task b ) {
        int sortColumn = jEdit.getIntegerProperty( "tasklist.table.sort-column", 1 );
        boolean sortAscending = jEdit.getBooleanProperty( "tasklist.table.sort-ascending", true );

        switch ( sortColumn ) {
            case 2:       // task type
                int value = a.getIdentifier().compareTo( b.getIdentifier() );
                if (value == 0) {
                    // sort by line number if task type is the same
                    return a.getLineNumber() < b.getLineNumber() ? -1 : 1;
                }
                return sortAscending ? value : value * -1;
            default:       // line number
                return a.getLineNumber() < b.getLineNumber() ? -1 : 1;
        }
    }
}