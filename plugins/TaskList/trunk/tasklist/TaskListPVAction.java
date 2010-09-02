/*
Copyright (c) 2009, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package tasklist;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import projectviewer.vpt.VPTNode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as menu item to search for tasks for the selected file/directory in PV.
 */
public class TaskListPVAction extends projectviewer.action.Action {

    private VPTNode node = null;

    // called by ProjectViewer to set the text on the PV context menu menuitem.    
    public String getText() {
        return jEdit.getProperty( "tasklist.parse-browser.label", "Find Tasks" );
    }

    // called by ProjectViewer to let us know the currently selected node in
    // the PV tree.  TaskList will find the tasks in this node and children of
    // this node, if any.
    public void prepareForNode( final VPTNode node ) {
        if ( node == null ) {
            return ;
        }
        this.node = node;
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (node == null) {
            return;   
        }
        
        View view = null;
        if (viewer == null) {
            view = jEdit.getActiveView();   
        }
        else {
            view = viewer.getView();   
        }
        
        TaskList taskList = TaskListPlugin.getTaskList(view);
        taskList.addTab(node.getName(), new ProjectNodeTaskList(view, node));
        view.getDockableWindowManager().showDockableWindow( "tasklist" );
    }
}