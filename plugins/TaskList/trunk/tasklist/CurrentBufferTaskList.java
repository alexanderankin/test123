/*
* Copyright (C) 2009, Dale Anson
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
* 
*/

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
        else if (msg instanceof BufferUpdate) {
            BufferUpdate pbm = (BufferUpdate)msg;
            // check buffer saved
            if (view.equals(pbm.getView()) && 
                (ParseBufferMessage.DO_PARSE.equals(pbm.getWhat()) || BufferUpdate.SAVED.equals(pbm.getWhat()))) {
                table.setBuffer(pbm.getBuffer());
            }
        }
    }
}