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

    //{{{ imports
    import org.gjt.sp.jedit.GUIUtilities;
    import org.gjt.sp.jedit.*;
    import org.gjt.sp.jedit.textarea.*;
    import org.gjt.sp.jedit.gui.*;
    import java.io.*;
    import java.awt.*;
    import javax.swing.*;
    import projectviewer.*;
    import projectviewer.vpt.*;
    import projectviewer.event.*; //}}}

public class Jump
{
    //{{{ constructor
    public Jump()
    {
        String s = System.getProperty("file.separator");
        File jumpDir = new File(System.getProperty("user.home")+s+".jedit"+s+"jump");
        if (!jumpDir.exists())
        {
            jumpDir.mkdirs();
        }
    } //}}}

    //{{{ boolean isJumpEnabled()
    public boolean isJumpEnabled()
    {
        if (!jEdit.getBooleanProperty("jump.enable", false))
        {
            GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.enable", null);
            return false;
        }
        return true;
    } //}}}

    //{{{ boolean isProjectLoaded()
    // Is any active VTProject loaded?
    // TODO: this method must be called just once!!!
    public boolean isProjectLoaded()
    {
        System.out.println("isProjectLoaded: return"+PVActions.getCurrentProject(jEdit.getActiveView()));
        if (PVActions.getCurrentProject(jEdit.getActiveView()) == null)
        {
            return false;
        }
        return true;
    } //}}}

    //{{{ void showFilesJump()
    public void showFilesJump()
    {
        // QUESTION: May be view field must be class-field?
        View view = jEdit.getActiveView();
        if (!isJumpEnabled()) return;
        if (!isProjectLoaded()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer)
        {
            JumpPlugin.getListener().reloadProjectForced();
            if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null) JumpPlugin.fja.showList();
        }
        else
        {
            System.out.println("showProjectJump: Setting active ProjectBuffer. ");
            if (PVActions.getCurrentProject(view) != null)
            {
                JumpPlugin.getListener().reloadProjectForced();
                ProjectBuffer b = ProjectBuffer.getProjectBuffer(PVActions.getCurrentProject(view).getName());
                if (b == null) return;
                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.fja.showList();
            }
        }
    } //}}}

    //{{{ void showTagsJump()
    public void showTagsJump()
    {
        View view = jEdit.getActiveView();
        if (!isJumpEnabled()) return;
        if (!isProjectLoaded()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer)
        {
            JumpPlugin.getListener().reloadProjectForced();
            System.out.println("1.");
            if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null)JumpPlugin.tja.showList();
        }
        else
        {
            System.out.println("showProjectJump: Setting active ProjectBuffer. ");
            if (PVActions.getCurrentProject(view) != null)
            {
                System.out.println("2.");
                ProjectBuffer b = ProjectBuffer.getProjectBuffer(PVActions.getCurrentProject(view).getName());
                if (b == null) return;

                JumpPlugin.getListener().reloadProjectForced();
                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.tja.showList();
            }
        }
    } //}}}

    //{{{ void showProjectJump()
    public void showProjectJump()
    {
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) return;
        if (!isProjectLoaded())
        {
            System.out.println("Jump.showProjectJump: project not loaded!");
            return;
        }

        if (!JumpPlugin.isListenerAdded)
        { 
            System.out.println("showProjectJump: init JumpPlugin...");
            JumpPlugin.init();
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer)
        {
            JumpPlugin.getListener().reloadProjectForced();
            if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null) JumpPlugin.pja.JumpToTag();
        }
        else
        {
            System.out.println("showProjectJump: Setting active ProjectBuffer. ");
            if (PVActions.getCurrentProject(view) != null)
            {
                JumpPlugin.getListener().reloadProjectForced();
                ProjectBuffer b = ProjectBuffer.getProjectBuffer(PVActions.getCurrentProject(view).getName());
                if (b == null)
                {
                    System.out.println("showProjectJump() - Error during construct ProjectBuffer.");
                    return;
                }
                JumpPlugin.setActiveProjectBuffer(b);
                if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null) JumpPlugin.pja.JumpToTag();
            }
        }
    }//}}}

    //{{{ showFoldJump()
    public void showFoldJump()
    {
        System.out.println("Jump.showFoldJump");
          FoldJumpAction foldja = new FoldJumpAction();
          foldja.showFoldsList();
    } //}}}

    //{{{ completeTag
    public void completeTag(boolean isGlobalSearch)
    {
        View view = jEdit.getActiveView();

        if (!isJumpEnabled()) return;
        if (!isProjectLoaded())
        {
            System.out.println("Jump.completeTag: project not loaded!");
            return;
        }

        if (!JumpPlugin.isListenerAdded)
        {
            System.out.println("completeTag: init JumpPlugin...");
            JumpPlugin.init();
        }

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer)
        {
            JumpPlugin.getListener().reloadProjectForced();
            if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null) JumpPlugin.pja.completeTag(isGlobalSearch);    
        }
        else
        {
            System.out.println("completeTag: Setting active ProjectBuffer. ");
            if (PVActions.getCurrentProject(view) != null)
            {
                JumpPlugin.getListener().reloadProjectForced();
                ProjectBuffer b = ProjectBuffer.getProjectBuffer(PVActions.getCurrentProject(view).getName());
                if (b == null)
                {
                    System.out.println("completeTag() - Error during construct ProjectBuffer.");
                    return;
                }
                JumpPlugin.setActiveProjectBuffer(b);
                if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null) JumpPlugin.pja.completeTag(isGlobalSearch);
            }
        }
    }
//}}} 

    //{{{ void reloadTagsOnProject()
    public void reloadTagsOnProject()
    {
        if (!isJumpEnabled()) return;
        if (!isProjectLoaded()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
    } //}}}

    //{{{ void historyJump()
    public void historyJump()
    {
        View view = jEdit.getActiveView();
        if (!isJumpEnabled()) return;
        if (!isProjectLoaded()) return;
        if (!JumpPlugin.isListenerAdded) return;

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer)
        {
            JumpPlugin.getListener().reloadProjectForced();
            if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null) JumpPlugin.pja.JumpToPreviousTag();
        }
        else
        {
            System.out.println("showProjectJump: Setting active ProjectBuffer. ");
            if (PVActions.getCurrentProject(view) != null)
            {
                JumpPlugin.getListener().reloadProjectForced();
                ProjectBuffer b = ProjectBuffer.getProjectBuffer(PVActions.getCurrentProject(view).getName());
                if (b == null) return;
                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.pja.JumpToPreviousTag();
            }
        }
    }
//}}}

    //{{{ void jumpByInput()
    public void jumpByInput()
    {
        View view = jEdit.getActiveView();
        if (!isJumpEnabled()) return;
        if (!isProjectLoaded()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();

        if (JumpPlugin.getActiveProjectBuffer() instanceof ProjectBuffer)
        {
            JumpPlugin.getListener().reloadProjectForced();
            if (JumpPlugin.getActiveProjectBuffer().PROJECT_CTBUFFER != null) JumpPlugin.pja.JumpToTagByInput();
        }
        else
        {
            if (PVActions.getCurrentProject(view) != null)
            {
                JumpPlugin.getListener().reloadProjectForced();
                ProjectBuffer b = ProjectBuffer.getProjectBuffer(PVActions.getCurrentProject(view).getName());
                if (b == null) return;
                JumpPlugin.setActiveProjectBuffer(b);
                JumpPlugin.pja.JumpToTagByInput();
            }
        }
    } //}}}

    //{{{ getListLocation
    public static Point getListLocation()
    {
        JEditTextArea textArea = jEdit.getActiveView().getTextArea();
        textArea.scrollToCaret(false);

        int caret = textArea.getCaretPosition();
        //String sel = textArea.getSelectedText();

        Point location = textArea.offsetToXY(caret);
        location.y += textArea.getPainter().getFontMetrics().getHeight();
        SwingUtilities.convertPointToScreen(location, textArea.getPainter());
        return location;
    } //}}}
}
