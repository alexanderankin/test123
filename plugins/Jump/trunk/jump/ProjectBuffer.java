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
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import jump.ctags.CtagsBuffer;
import jump.ctags.CtagsMain;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.HistoryModel;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;


public class ProjectBuffer {
    public HistoryModel historyModel;
    public JumpHistory history;
    public VPTProject project;
    public String root;
    public String name;
    public File jumpFile;
    public Vector files = new Vector();
    public Vector deleteHelperVector = new Vector();
    public CtagsBuffer ctagsBuffer;
    public TypeTag typeTagWindow;
    public CtagsMain ctagsMain;
    private String fs = System.getProperty("file.separator");
    

    public ProjectBuffer(String name) {
        this.name = name;
    }

    public void init(VPTProject project) throws Exception {
        //project = ProjectManager.getInstance().getProject(name);
    	this.project = project;

        //ctagsMain = new CtagsMain(jEdit.getProperty(
        //           JumpConstants.CTAGS_PATH_PROP,
        //           JumpConstants.CTAGS_DEFAULT_PATH_PROP));

        root = project.getRootPath();
        jumpFile = getJumpFileName();
        files = initFilesVector();

        loadJumpFile();

        // Init JumpHistory
        history = new JumpHistory();
        historyModel = HistoryModel.getModel("jump.tag_history.project." +
                name);
    }

    public void createJumpFile() throws Exception {
        ctagsBuffer = ctagsMain.getParser().parse(files);
        CtagsMain.saveBuffer(ctagsBuffer, jumpFile.toString());
    }

    public void checkFileDeleted() throws Exception {
        Collection v0 = Collections.synchronizedCollection(project.getFiles());
        Vector v = new Vector(v0);
        Vector tmp_del = new Vector();
        deleteHelperVector = new Vector();

        // DELETE_HELPER Vector - temporary storage of currnent project filenames.    
        for (int i = 0; i < v.size(); i++) {
            VPTFile f = (VPTFile) v.get(i);
            deleteHelperVector.add(f.getCanonicalPath());
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
                System.out.println("Jump!.ProjectBuffer.checkFileDeleted() - " +
                    deleteHelperVector.get(i) + " file was added to project");
            }
        }

        // Now drop deleted files from PROJECT_FILES list
        if (tmp_del.size() > 0) {
            //System.out.println("Files to delete = "+tmp_del.size());
            for (int i = 0; i < tmp_del.size(); i++) {
                files.remove((String) tmp_del.get(i));
                System.out.println("Jump!.ProjectBuffer.checkFileDeleted( - )" +
                    tmp_del.get(i) + " file was deleted.");
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

    private void loadJumpFile() throws Exception {
        if (!jumpFile.exists()) {
            createJumpFile();
        }
        else {
            ctagsBuffer = CtagsMain.loadBuffer(jumpFile.toString());
        }

        enableViewer();
    }
    
    private Vector initFilesVector() {
        Vector result = new Vector(Collections.synchronizedCollection(
                    project.getFiles()));

        for (int i = 0; i < result.size(); i++) {
            VPTFile f = (VPTFile) result.get(i);
            files.add(f.getCanonicalPath());
        }

        return result;
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
