/*
 *  Jump plugin for jEdit
 *  Copyright (c) 2003 Pavlikus
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

import jump.ctags.CTAGS_BG;
import jump.ctags.CtagsBuffer;

import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;


/**
 * Class which store all info about VTProject and it's tags
 */
public class ProjectBuffer {
    /** HistoryMode for displaing TypeTag dialog */
    public HistoryModel HISTORY;

    /** Strores CTAGS_Entries which already jump */
    public JumpHistory JUMP_HISTORY;
    public VPTProject PROJECT;
    public String PROJECT_ROOT;
    public String PROJECT_NAME;
    public File PROJECT_TAGS;
    public ArrayList PROJECT_FILES = new ArrayList();
    public Vector DELETE_HELPER = new Vector();
    public CtagsBuffer PROJECT_CTBUFFER;
    public TypeTag TYPE_TAG_WINDOW;
    public CTAGS_BG ctags_bg;

    protected ProjectBuffer() {
    }

    /**
     * Init given ProjectBuffer. (load tags, init history etc.)
     */
    protected boolean init(ProjectBuffer pb, String name) {
        long t1;
        t1 = System.currentTimeMillis();

        ProjectManager pm = ProjectManager.getInstance();
        pb.PROJECT = pm.getProject(name);

        if (pb.PROJECT != null) {
            pb.ctags_bg = new CTAGS_BG(jEdit.getProperty("jump.ctags.path",
                        "options.JumpPlugin.ctags.def.path"));

            String s = System.getProperty("file.separator");

            pb.PROJECT_ROOT = pb.PROJECT.getRootPath();
            pb.PROJECT_NAME = name;
            pb.PROJECT_TAGS = new File(System.getProperty("user.home") + s +
                    ".jedit" + s + "jump" + s + this.PROJECT_NAME + ".jump");

            Collection v0 = Collections.synchronizedCollection(PROJECT.getFiles());

            Vector v = new Vector(v0);

            for (int i = 0; i < v.size(); i++) {
                VPTFile f = (VPTFile) v.get(i);
                pb.PROJECT_FILES.add(f.getCanonicalPath());
            }

            if (!loadJumpFile(pb)) {
                return false;
            }

            // Init JumpHistory...
            pb.JUMP_HISTORY = new JumpHistory();
            pb.HISTORY = HistoryModel.getModel("jump.tag_history.project." +
                    pb.PROJECT_NAME);

            // Init TypeTag window class
            System.out.println("Buffer creating took - " +
                (System.currentTimeMillis() - t1) + " ms");

            return true;
        } else {
            System.out.println("Jump!.ProjectBuffer: Exception at init()");

            return false;
        }
    }

    public TypeTag getTypeTag() {
        return new TypeTag();
    }

    /**
    * Query point to create new ProjectBuffer object
    */
    public static ProjectBuffer getProjectBuffer(String name) {
        ProjectBuffer pb = new ProjectBuffer();

        if (pb.init(pb, name)) {
            return pb;
        }

        return null;
    }

    /**
    * Create .jump file for ProjectBuffer.
    */
    public boolean createJumpFile(final ProjectBuffer pb) {
        try {
            pb.PROJECT_CTBUFFER = ctags_bg.getParser().parse(pb.PROJECT_FILES);

            // if project don't contain any vaild files to parse (for ex. html, xml, etc.) we returns false.
            if (pb.PROJECT_CTBUFFER == null) {
                throw new Exception();
            }

            CTAGS_BG.saveBuffer(pb.PROJECT_CTBUFFER, pb.PROJECT_TAGS.toString());

            return true;
        } catch (Exception e) {
            Log.log(Log.ERROR, this,
                "Jump!.ProjectBuffer.createJumpFile() - can\'t create tags file");
            Log.log(Log.ERROR, this, e);

            return false;
        }
    }

    public void saveJumpFile() {
        CTAGS_BG.saveBuffer(PROJECT_CTBUFFER, PROJECT_TAGS.toString());
    }

    public boolean loadJumpFile(ProjectBuffer pb) {
        try {
            // If no .jump file found - try to create new one
            ProjectViewer viewer = ProjectViewer.getViewer(jEdit.getActiveView());

            if (pb.PROJECT_TAGS.exists() == false) {
                if (!createJumpFile(pb)) {
                    if (viewer != null) {
                        viewer.setEnabled(true);
                    }

                    return false;
                } else {
                    if (viewer != null) {
                        viewer.setEnabled(true);
                    }

                    System.out.println(
                        "Jump!.ProjectBuffer.loadJumpFile - Tags file created");

                    return true;
                }
            }
            // Read already seriailzed file
            else {
                pb.PROJECT_CTBUFFER = CTAGS_BG.loadBuffer(pb.PROJECT_TAGS.toString());

                if (viewer != null) {
                    viewer.setEnabled(true);
                }

                return true;
            }
        } catch (Exception e) {
            System.out.println(
                "Jump!.ProjectBuffer.loadJumpFile - Ctags path incorrect!");
            e.printStackTrace();

            ProjectViewer viewer = ProjectViewer.getViewer(jEdit.getActiveView());

            if (viewer != null) {
                viewer.setEnabled(true);
            }

            return false;
        }
    }

    /**
     *  Since ProjectViewer2.0.1 have't DELETE(ADD)_FILE_FROM(TO)_PROJECT, I must manualy check it.
     */
    public void checkFileDeleted() {
        Collection v0 = Collections.synchronizedCollection(PROJECT.getFiles());
        Vector v = new Vector(v0);
        Vector tmp_del = new Vector();
        DELETE_HELPER = new Vector();

        // DELETE_HELPER Vector - temporary storage of currnent project filenames.    
        for (int i = 0; i < v.size(); i++) {
            VPTFile f = (VPTFile) v.get(i);
            DELETE_HELPER.add(f.getCanonicalPath());
        }

        // Now, when DELETE_HELPER is set, I start to examine is deleted or added files...
        // is deleted files?
        for (int i = 0; i < PROJECT_FILES.size(); i++) {
            if (!DELETE_HELPER.contains(PROJECT_FILES.get(i))) {
                removeFile((String) PROJECT_FILES.get(i));
                tmp_del.add(PROJECT_FILES.get(i));
            }
        }

        // is added files?
        for (int i = 0; i < DELETE_HELPER.size(); i++) {
            if (!PROJECT_FILES.contains(DELETE_HELPER.get(i))) {
                addFile((String) DELETE_HELPER.get(i));
                PROJECT_FILES.add(DELETE_HELPER.get(i));
                System.out.println("Jump!.ProjectBuffer.checkFileDeleted() - " +
                    DELETE_HELPER.get(i) + " file was added to project");
            }
        }

        // Now drop deleted files from PROJECT_FILES list
        if (tmp_del.size() > 0) {
            //System.out.println("Files to delete = "+tmp_del.size());
            for (int i = 0; i < tmp_del.size(); i++) {
                PROJECT_FILES.remove((String) tmp_del.get(i));
                System.out.println("Jump!.ProjectBuffer.checkFileDeleted( - )" +
                    tmp_del.get(i) + " file was deleted.");
            }
        }

        DELETE_HELPER.clear();
    }

    /**
    * When new file open, add its tag to CTAGS_Buffer
    */
    public void addFile(String f) {
        try {
            //Log.log(Log.DEBUG,this,"addFile: - "+f); 
            if ((f == null) || f.equals("")) {
                return;
            }

            CtagsBuffer new_buff = ctags_bg.getParser().parse(f);

            if (new_buff == null) {
                return;
            }

            PROJECT_CTBUFFER.append(new_buff, f);
        } catch (IOException e) {
            return;
        }
    }

    /**
    * Remove all tags (which founded in spec. file) from CTAGS_Buffer
    */
    public void removeFile(String f) {
        PROJECT_CTBUFFER.removeFile(f);
    }

    /**
    * When file modified and saved, we need to update tags
    */
    public void reloadFile(String f) {
        PROJECT_CTBUFFER.remove(f);
        addFile(f);
    }
}
