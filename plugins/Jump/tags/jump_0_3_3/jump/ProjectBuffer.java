/*
 *  Jump plugin for jEdit
 *  Copyright (c) 2003-2004 Pavlikus
 *
 *  :tabSize=4:indentSize=4:
 *  :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jump;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import java.util.Iterator;

import jump.ctags.CtagsBuffer;
import jump.ctags.CtagsMain;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.util.Log;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;


public class ProjectBuffer {
    public HistoryModel historyModel;
    public JumpHistory history;
    public VPTProject project;
    public String root;
    public String name;
    public File jumpFile;
    public Vector files;
    public Vector deleteHelperVector = new Vector();
    public CtagsBuffer ctagsBuffer;
    public TypeTag typeTagWindow;
    public CtagsMain ctagsMain;
    private String fs = System.getProperty("file.separator");


    public ProjectBuffer(String name) {
        this.name = name;
    }

    public void init(VPTProject project) {
        //project = ProjectManager.getInstance().getProject(name);
    	this.project = project;

		if (ctagsMain == null) {
			ctagsMain = new CtagsMain(jEdit.getProperty(
					   JumpConstants.CTAGS_PATH_PROP,
					   JumpConstants.CTAGS_DEFAULT_PATH_PROP));
		}

        root = project.getRootPath();
        jumpFile = getJumpFileName();
        files = initFilesVector();

        loadJumpFile();

        // Init JumpHistory
        history = new JumpHistory();
        historyModel = HistoryModel.getModel("jump.tag_history.project." +
                name);
    }

    public void createJumpFile() {
		try {
			ctagsBuffer = ctagsMain.getParser().parse(files);
			CtagsMain.saveBuffer(ctagsBuffer, jumpFile.toString());
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
    }

    public void checkFileDeleted() throws Exception {
        Collection v0 = Collections.synchronizedCollection(project.getOpenableNodes());
        Vector v = new Vector(v0);
        Vector tmp_del = new Vector();
        deleteHelperVector = new Vector();

        // DELETE_HELPER Vector - temporary storage of currnent project filenames.
        for (int i = 0; i < v.size(); i++) {
			VPTNode n = (VPTNode) v.get(i);
			if (n.isFile()) {
				deleteHelperVector.add(n.getNodePath());
			}
        }

        // Now, when DELETE_HELPER is set, I start to examine is deleted or added files...
        // is deleted files?
        for (int i = 0; i < files.size(); i++) {
            if (!deleteHelperVector.contains(files.get(i))) {
                removeFile((String) files.get(i));
                tmp_del.add(files.get(i));
            }
        }

        // is added files?
        for (int i = 0; i < deleteHelperVector.size(); i++) {
            if (!files.contains(deleteHelperVector.get(i))) {
                addFile((String) deleteHelperVector.get(i));
                files.add(deleteHelperVector.get(i));
            }
        }

        // Now drop deleted files from PROJECT_FILES list
        if (tmp_del.size() > 0) {
            for (int i = 0; i < tmp_del.size(); i++) {
                files.remove((String) tmp_del.get(i));
            }
        }

        deleteHelperVector.clear();
    }

    public void addFile(String filename) throws Exception {
        CtagsBuffer tmp = ctagsMain.getParser().parse(filename);
        if (tmp != null) {
        	ctagsBuffer.append(tmp, filename);
        }
    }

    public void removeFile(String filename) {
        ctagsBuffer.removeFile(filename);
    }

    public void reloadFile(String filename) throws Exception {
        ctagsBuffer.remove(filename);
        addFile(filename);
    }

    // 	******************************************************  Private methods
    private void saveJumpFile() {
        CtagsMain.saveBuffer(ctagsBuffer, jumpFile.toString());
    }

    private void loadJumpFile() {
        if (!jumpFile.exists()) {
            createJumpFile();
        }
        else {
            ctagsBuffer = CtagsMain.loadBuffer(jumpFile.toString());
        }

        enableViewer();
    }

    private Vector initFilesVector() {
    	Collection nodes = project.getOpenableNodes();
        Vector _files = new Vector();

		for (Iterator i = nodes.iterator(); i.hasNext(); ) {
            VPTNode n = (VPTNode) i.next();
			if (n.isFile()) {
				String path = n.getNodePath();
				_files.add(path);
			}
        }

        return _files;
    }

    private File getJumpFileName() {
        return new File(jEdit.getSettingsDirectory() + fs +
            JumpConstants.JUMP_WORK_DIR + fs + name +
            JumpConstants.JUMP_FILE_EXTENSION);
    }

    private void enableViewer() {
        if (ProjectViewer.getViewer(jEdit.getActiveView()) != null) {
            ProjectViewer.getViewer(jEdit.getActiveView()).setEnabled(true);
        }
    }

    // TODO: ****************************************************  Alien method
    public TypeTag getTypeTag() {
        return new TypeTag();
    }
}
