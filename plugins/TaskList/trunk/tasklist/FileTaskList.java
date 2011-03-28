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
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.msg.*;


import common.swingworker.*;

/**
 * A task list to display the tasks found in a directory or some files.
 */
public class FileTaskList extends AbstractTreeTaskList {

    private VFSFile[] files = null;
    public FileTaskList(View view, VFSFile[] vfsFiles) {
        super(view, jEdit.getProperty("tasklist.files.files", "Files:"));
        files = new VFSFile[vfsFiles.length];
        System.arraycopy(vfsFiles, 0, files, 0, vfsFiles.length);
        loadFiles();
    }

    @Override
    protected List<String> getBuffersToScan() {
        if (files == null) {
            return null;   
        }

        Set<String> paths = new HashSet<String>();
        for (VFSFile file : files) {
            if (file != null) {
                findPaths(file, paths);
            }
        }

        return new ArrayList<String>(paths);
    }
    
    private void findPaths(VFSFile file, Set<String> paths) {
        if (file.getType() == VFSFile.FILE) {
            paths.add(file.getPath());   
        }
        else if (file.getType() == VFSFile.DIRECTORY) {
            String dir = file.getPath();
            try {
                VFSFile[] children = file.getVFS()._listFiles(null, dir, view);
                for (VFSFile child : children) {
                    findPaths(child, paths);   
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}