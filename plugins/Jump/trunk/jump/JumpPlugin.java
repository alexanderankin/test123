package jump;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import jump.ctags.*;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.util.Log;

import projectviewer.PVActions;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;


public class JumpPlugin extends EditPlugin {
    public final static String NAME = "JumpPlugin";
    public final static String MENU = "JumpPlugin.menu";
    public final static String PROPERTY_PREFIX = "plugin.JumpPlugin.";
    public final static String OPTION_PREFIX = "options.JumpPlugin.";
    public static Jump jump_actions;
    public static ProjectJumpAction pja;
    public static TagsJumpAction tja;
    public static FilesJumpAction fja;
    public static JumpEventListener listener;
    public static boolean isListenerAdded = false;
    private static HashMap projectBuffers;
    private static ProjectBuffer activeProjectBuffer;
    private static Vector buffersForDelete;

    public void start() {
        jump_actions = new Jump();
    }

    public void stop() {
        projectRenamedWorkaround();

        String s = System.getProperty("file.separator");

        try {
            // Delete unneeded .jump files
            File f;

            for (int i = 0; i < buffersForDelete.size(); i++) {
                f = new File(System.getProperty("user.home") + s + ".jedit" +
                        s + "jump" + s + (String) buffersForDelete.get(i));
                f.delete();
            }
        } catch (Exception ex) {
            Log.log(Log.DEBUG, this, "JumpPlugin: failed drop unused tags");
            ex.printStackTrace();
        }

        if (isListenerAdded == false) {
            return;
        }

        try {
            if (projectBuffers.size() > 0) {
                ProjectBuffer pb;
                Vector v = new Vector(projectBuffers.values());

                for (int i = 0; i < v.size(); i++) {
                    pb = (ProjectBuffer) v.get(i);
                    CtagsMain.saveBuffer(pb.PROJECT_CTBUFFER,
                        pb.PROJECT_TAGS.toString());
                }
            }
        } catch (Exception e) {
            Log.log(Log.DEBUG, this, "failed to save tags on exit");
            e.printStackTrace();
        }

        dispose();
    }

    public static ProjectBuffer getActiveProjectBuffer() {
        return activeProjectBuffer;
    }

    // TODO: UGLY setProject/ addProject conditions
    public static boolean setActiveProjectBuffer(ProjectBuffer buff) {
        try {
            //System.out.println("JumpPlugin: setActiveProjectBuffer...");
            if (projectBuffers.containsKey(buff.PROJECT_NAME)) {
                activeProjectBuffer = (ProjectBuffer) projectBuffers.get(buff.PROJECT_NAME);
                projectBuffers.put(buff.PROJECT_NAME, buff);

                //System.out.println("JumpPlugin: setActiveProjectBuffer!");
                return true;
            } else {
                addProjectBuffer(buff);
                setActiveProjectBuffer(buff);

                return true;
            }
        } catch (Exception e) {
            System.out.println("JumpPlugin: setActiveProjectBuffer failed");

            return false;
        }
    }

    /**
     *  Just add this ProjectBuffer into hash
     */
    public static void addProjectBuffer(ProjectBuffer buff) {
        System.out.println("JumpPlugin: addProjectBuffer - " + buff);

        if (buff == null) {
            return;
        }

        projectBuffers.put(buff.PROJECT_NAME, buff);
    }

    /**
     *  Removes ProjectBuffer from hash, setting setActiveProjectBuffer(null)
     */
    public static void removeProjectBuffer(ProjectBuffer buff) {
        projectBuffers.remove(buff);
        setActiveProjectBuffer(null);
    }

    /**
     *  Removes ProjectBuffer from hash, setting setActiveProjectBuffer(null)
     *  @param name - @see ProjectBuffer.PROJECT_NAME
     */
    public static void removeProjectBuffer(String name) {
        // if (activeProjectBuffer == buff) {}
        ProjectBuffer b = (ProjectBuffer) projectBuffers.get(name);

        if (b != null) {
            projectBuffers.remove(b);
            setActiveProjectBuffer(null);

            HistoryModel mo = HistoryModel.getModel("jump.tag_history.project." +
                    name);
            mo.clear();
        }
    }

    /**
     *  Return ProjectBuffer from hash
     */
    public static ProjectBuffer getProjectBuffer(String name) {
        return (ProjectBuffer) projectBuffers.get(name);
    }

    /**
     *  Check is ProjectBuffer already exists in hash
     *  @param name = @see ProjectBuffer.PROJECT_NAME
     */
    public static boolean hasProjectBuffer(String name) {
        return projectBuffers.containsKey(name);
    }

    /**
    *   Check is ProjectBuffer already exists in hash
    */
    public static boolean hasProjectBuffer(ProjectBuffer buff) {
        return projectBuffers.containsValue(buff);
    }

    public static JumpEventListener getListener() {
        return listener;
    }

    public static boolean reloadTagsOnProject() {
        if (!jump_actions.isJumpEnabled()) {
            return false;
        }

        if (ProjectViewer.getViewer(jEdit.getActiveView()) != null) {
            ProjectViewer pv = ProjectViewer.getViewer(jEdit.getActiveView());
            VPTProject pr = PVActions.getCurrentProject(jEdit.getActiveView());

            return (listener.reloadTags(pv, pr));
        }

        return false;
    }

    /**
     *  Init all classes here, instead of in start() to avoid long starup time
     */
    public static void init() {
        System.out.println("JumpPlugin: init...");

        View v = jEdit.getActiveView();
        pja = new ProjectJumpAction();
        tja = new TagsJumpAction();
        fja = new FilesJumpAction();
        listener = new JumpEventListener();
        projectBuffers = new HashMap();

        if (PVActions.getCurrentProject(v) != null) {
            ProjectViewer.addProjectViewerListener(listener, v);
        }

        isListenerAdded = true;
    }

    /**
     *  Since PViewer have't PROJECT_RENAMED event, here I try to determine coincidence of project files and .jump files...
     */
    private void projectRenamedWorkaround() {
        //System.getProperty("user.home")+s+".jedit"+s+"projectviewer"+s+"projects"+s+this.PROJECT_NAME+".jump"
        buffersForDelete = new Vector();

        String s = System.getProperty("file.separator");
        File pDir = new File(System.getProperty("user.home") + s + ".jedit" +
                s + "projectviewer" + s + "projects" + s);

        try {
            List files = new Vector();
            String[] _files = pDir.list();
            files = Arrays.asList(_files);

            String tmp;

            for (int i = 0; i < _files.length; i++) {
                tmp = _files[i];

                if (tmp.endsWith(".jump")) {
                    if (!files.contains(tmp.replaceAll(".jump", ".xml"))) {
                        buffersForDelete.add(tmp);

                        //System.out.println("Add to buffersForDelete - " + tmp.replaceAll(".xml", ".jump"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception projectRenamedWorkaround - " + e);
        }
    }

    public void dispose() {
        activeProjectBuffer = null;
        projectBuffers = null;

        View v = jEdit.getActiveView();

        if (PVActions.getCurrentProject(v) != null) {
            ProjectViewer.removeProjectViewerListener(listener, v);
            System.out.println("JumpPlugin - ProjectViewerListener removed");
        }

        listener.dispose();

        jump_actions = null;
        pja = null;
        tja = null;
        fja = null;
        buffersForDelete = null;
        listener = null;
    }
}
