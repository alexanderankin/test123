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

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as menu item to launch the image viewer for the selected file in PV.
 */
public class TaskListPVAction extends projectviewer.action.Action {

    private JMenuItem menuItem = new JMenuItem( jEdit.getProperty( "tasklist.parse-browser.label", "Find Tasks" ) );


    public String getText() {
        return jEdit.getProperty( "tasklist.parse-browser.label", "Find Tasks" );
    }

    public JComponent getMenuItem() {
        return menuItem;
    }

    // called by ProjectViewer to let us know the currently selected node in
    // the PV tree.
    public void prepareForNode( final VPTNode node ) {
        if ( node == null ) {
            return ;
        }
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    TaskList taskList = TaskListPlugin.getTaskList(viewer.getView());
                    taskList.addTab(node.getName(), new ProjectNodeTaskList(viewer.getView(), node));
                }
            }
        );
    }

    public void actionPerformed( ActionEvent ae ) {
        // not used
    }
}