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

public class CurrentBufferTaskList extends AbstractTreeTaskList {

    public CurrentBufferTaskList(View view) {
        super(view, jEdit.getProperty("tasklist.currentbuffer", "Current File:"));
        putClientProperty("isCloseable", Boolean.FALSE);
    }

    @Override
    protected boolean showProgress() {
        return false;
    }

    @Override
    protected List<String> getBuffersToScan() {
        List<String> buffers = new ArrayList<String>();
        if (!Binary.isBinary(view.getBuffer())) {
            buffers.add(view.getBuffer().getPath());
        }
        return buffers;
    }

    public void handleMessage(EBMessage message) {
        Buffer buffer = view.getBuffer();
        Buffer b = null;
        Object what = null;
        if (message instanceof ParseBufferMessage) {
            b = ((ParseBufferMessage) message).getBuffer();
            what = ((ParseBufferMessage) message).getWhat();
            if (ParseBufferMessage.APPLY_FILTER.equals(what)) {
                filterTree();
                return;
            }
        } else if (message instanceof EditPaneUpdate) {
            b = ((EditPaneUpdate) message).getEditPane().getBuffer();
        }
        if (b == null) {
            return;
        }
        if (buffer.getPath().equals(b.getPath())) {
            loadFiles();
            return;
        }
        super.handleMessage(message);
    }

}