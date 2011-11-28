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

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import projectviewer.vpt.VPTNode;

import common.swingworker.*;

/**
 * A task list to display the tasks found in a tree that represents files.
 */
public class ProjectNodeTaskList extends AbstractTreeTaskList {

    private VPTNode node = null;
    
    public ProjectNodeTaskList(View view, VPTNode node) {
        super(view, jEdit.getProperty("tasklist.files.files", "Files:"));
        this.node = node;
        loadFiles();
    }

    @Override
    protected List<String> getBuffersToScan() {
        if (node == null) {
            return null;   
        }
        
        List<String> paths = new ArrayList<String>();
        
        if (node.isFile()) {
            paths.add(node.getNodePath());
        }
        else {
            Enumeration children = node.children();
            while(children.hasMoreElements()) {
                findPaths((VPTNode)children.nextElement(), paths);
            }
        }

        return paths;
    }
    
    private void findPaths(VPTNode node, List<String> paths) {
        if (node.isFile()) {
            paths.add(node.getNodePath());
        }
        else {
            Enumeration children = node.children();
            while(children.hasMoreElements()) {
                findPaths((VPTNode)children.nextElement(), paths);
            }
        }
    }
}