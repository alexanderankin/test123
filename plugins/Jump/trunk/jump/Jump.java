package jump;

import java.awt.Point;
import java.io.File;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import projectviewer.PVActions;


public class Jump {
    public Jump() {
        String s = System.getProperty("file.separator");
        File jumpDir = new File(System.getProperty("user.home") + s + ".jedit" +
                s + "jump");

        if (!jumpDir.exists()) {
            jumpDir.mkdirs();
        }
    }

    public boolean isJumpEnabled() {
        if (!jEdit.getBooleanProperty("jump.enable", false)) {
            GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.enable",
                null);

            return false;
        }

        return true;
    }

    // Is any active VTProject loaded?
    // TODO: this method must be called just once!!!
    public boolean isProjectLoaded() {
        return null != PVActions.getCurrentProject(jEdit.getActiveView());
    }

    public void showFilesJump() {
        // QUESTION: May be view field must be class-field?
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) {
            return;
        }

        if (!isProjectLoaded()) {
            return;
        }

        if (!JumpPlugin.isListenerAdded) {
            JumpPlugin.init();
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer) {
            JumpPlugin.getListener().reloadProjectForced();

            if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                JumpPlugin.fja.showList();
            }
        } else {
            System.out.println(
                "showProjectJump: Setting active ProjectBuffer. ");

            if (PVActions.getCurrentProject(view) != null) {
                JumpPlugin.getListener().reloadProjectForced();

                ProjectBuffer b = new ProjectBuffer(PVActions.getCurrentProject(
                            view).getName());

                if (b == null) {
                    return;
                }

                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.fja.showList();
            }
        }
    }

    public void showTagsJump() {
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) {
            return;
        }

        if (!isProjectLoaded()) {
            return;
        }

        if (!JumpPlugin.isListenerAdded) {
            JumpPlugin.init();
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer) {
            JumpPlugin.getListener().reloadProjectForced();
            System.out.println("1.");

            if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                JumpPlugin.tja.showList();
            }
        } else {
            System.out.println(
                "showProjectJump: Setting active ProjectBuffer. ");

            if (PVActions.getCurrentProject(view) != null) {
                System.out.println("2.");

                ProjectBuffer b = new ProjectBuffer(PVActions.getCurrentProject(
                            view).getName());

                if (b == null) {
                    return;
                }

                JumpPlugin.getListener().reloadProjectForced();
                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.tja.showList();
            }
        }
    }

    public void showProjectJump() {
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) {
            return;
        }

        if (!isProjectLoaded()) {
            System.out.println("Jump.showProjectJump: project not loaded!");

            return;
        }

        if (!JumpPlugin.isListenerAdded) {
            System.out.println("showProjectJump: init JumpPlugin...");
            JumpPlugin.init();
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer) {
            JumpPlugin.getListener().reloadProjectForced();

            if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                JumpPlugin.pja.JumpToTag();
            }
        } else {
            System.out.println(
                "showProjectJump: Setting active ProjectBuffer. ");

            if (PVActions.getCurrentProject(view) != null) {
                JumpPlugin.getListener().reloadProjectForced();

                ProjectBuffer b = new ProjectBuffer(PVActions.getCurrentProject(
                            view).getName());

                if (b == null) {
                    System.out.println(
                        "showProjectJump() - Error during construct ProjectBuffer.");

                    return;
                }

                JumpPlugin.setActiveProjectBuffer(b);

                if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                    JumpPlugin.pja.JumpToTag();
                }
            }
        }
    }

    public void showFoldJump() {
        FoldJumpAction foldja = new FoldJumpAction();
        foldja.showFoldsList();
    }

    public void completeTag(boolean isGlobalSearch) {
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) {
            return;
        }

        if (!isProjectLoaded()) {
            System.out.println("Jump.completeTag: project not loaded!");

            return;
        }

        if (!JumpPlugin.isListenerAdded) {
            System.out.println("completeTag: init JumpPlugin...");
            JumpPlugin.init();
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer) {
            JumpPlugin.getListener().reloadProjectForced();

            if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                JumpPlugin.pja.completeTag(isGlobalSearch);
            }
        } else {
            System.out.println("completeTag: Setting active ProjectBuffer. ");

            if (PVActions.getCurrentProject(view) != null) {
                JumpPlugin.getListener().reloadProjectForced();

                ProjectBuffer b = new ProjectBuffer(PVActions.getCurrentProject(
                            view).getName());

                if (b == null) {
                    System.out.println(
                        "completeTag() - Error during construct ProjectBuffer.");

                    return;
                }

                JumpPlugin.setActiveProjectBuffer(b);

                if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                    JumpPlugin.pja.completeTag(isGlobalSearch);
                }
            }
        }
    }

    public void reloadTagsOnProject() {
        if (!isJumpEnabled()) {
            return;
        }

        if (!isProjectLoaded()) {
            return;
        }

        if (!JumpPlugin.isListenerAdded) {
            JumpPlugin.init();
        }
    }

    public void historyJump() {
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) {
            return;
        }

        if (!isProjectLoaded()) {
            return;
        }

        if (!JumpPlugin.isListenerAdded) {
            return;
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer) {
            JumpPlugin.getListener().reloadProjectForced();

            if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                JumpPlugin.pja.JumpToPreviousTag();
            }
        } else {
            System.out.println(
                "showProjectJump: Setting active ProjectBuffer. ");

            if (PVActions.getCurrentProject(view) != null) {
                JumpPlugin.getListener().reloadProjectForced();

                ProjectBuffer b = new ProjectBuffer(PVActions.getCurrentProject(
                            view).getName());

                if (b == null) {
                    return;
                }

                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.pja.JumpToPreviousTag();
            }
        }
    }

    public void jumpByInput() {
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) {
            return;
        }

        if (!isProjectLoaded()) {
            return;
        }

        if (!JumpPlugin.isListenerAdded) {
            JumpPlugin.init();
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer) {
            JumpPlugin.getListener().reloadProjectForced();

            if (JumpPlugin.getActiveProjectBuffer().ctagsBuffer != null) {
                JumpPlugin.pja.JumpToTagByInput();
            }
        } else {
            if (PVActions.getCurrentProject(view) != null) {
                JumpPlugin.getListener().reloadProjectForced();

                ProjectBuffer b = new ProjectBuffer(PVActions.getCurrentProject(
                            view).getName());

                if (b == null) {
                    return;
                }

                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.pja.JumpToTagByInput();
            }
        }
    }

    public static Point getListLocation() {
        JEditTextArea textArea = jEdit.getActiveView().getTextArea();
        textArea.scrollToCaret(false);

        int caret = textArea.getCaretPosition();

        //String sel = textArea.getSelectedText();
        Point location = textArea.offsetToXY(caret);
        location.y += textArea.getPainter().getFontMetrics().getHeight();
        SwingUtilities.convertPointToScreen(location, textArea.getPainter());

        return location;
    }
}
