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

/**
* This code is based on:
* A macro to show all of the tasks that the TaskList plugin would show
* if the TaskList plugin had any concept of ProjectViewer.  This macro
* gets the list of files from ProjectViewer for the current project,
* passes each of them to TaskList to find the tasks for each file, and
* combines them all into a single tree display.  This puts all the tasks
* for the entire project in a single display.
*
* @author Dale Anson, 3 Nov 2008
*/
package tasklist;

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;


import common.swingworker.*;

public class OpenBuffersTaskList extends AbstractTreeTaskList {

    public OpenBuffersTaskList(View view) {
        super(view, jEdit.getProperty("tasklist.openfiles.open-files", "Open Files:"));
        putClientProperty("isCloseable", Boolean.FALSE);
    }

    @Override
    protected boolean canRun() {
        return jEdit.getBooleanProperty( "tasklist.show-open-files", true );
    }

    @Override
    protected List<String> getBuffersToScan() {
        // fetch all open buffers
        List<String> openBuffers = new ArrayList<String>();
        EditPane[] editPanes = view.getEditPanes();
        for ( EditPane editPane : editPanes ) {
            Buffer[] buffers = editPane.getBufferSet().getAllBuffers();
            for ( Buffer buffer : buffers ) {
                openBuffers.add( buffer.getPath() );
            }
        }
        return openBuffers;
    }
}