package tasklist;

import java.awt.BorderLayout;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

public class CurrentBufferTaskList extends JPanel implements EBComponent {

    private View view = null;
    private TaskListTable table = null;

    public CurrentBufferTaskList( View view ) {
        this.view = view;
        setLayout(new BorderLayout());
        table = new TaskListTable(view);
        add( BorderLayout.CENTER, new JScrollPane( table ) );
        EditBus.addToBus(this);
    }
    
    public void handleMessage(EBMessage msg) {
        if (msg instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate)msg;
            if (view.equals(epu.getEditPane().getView()) && EditPaneUpdate.BUFFER_CHANGED.equals(epu.getWhat())) {
                table.setBuffer(epu.getEditPane().getBuffer());
            }
        }
    }
}