// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

//{{{ imports
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.event.*;
//}}}

public class Jump
{
    
//{{{ boolean isJumpEnabled()
    public boolean isJumpEnabled()
    {
        if (!jEdit.getBooleanProperty("jump.enable", false))
        {
            GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.enable", null);
            return false;    
        }
        return true;
    }
//}}}

//{{{ boolean isProjectLoaded()
// Is any active VTProject loaded?
// TODO: this method must be called just once!!!
    public boolean isProjectLoaded()
    {
        System.out.println("isProjectLoaded: return"+PVActions.getCurrentProject(jEdit.getActiveView()));
        if (PVActions.getCurrentProject(jEdit.getActiveView()) == null)
        {
            return false;
            // JumpPlugin.listener.reloadTags(ProjectViewer.getViewer(view), PVActions.getCurrentProject(view));
        } 
        return true;
    }
//}}}

//{{{ void showFilesJump()
    public void showFilesJump()
    {
        // QUESTION: May be view field must be class-field?
        View view = jEdit.getActiveView();
        if (!isJumpEnabled()) return;
        if (!isProjectLoaded()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
        //new FilesJumpAction().showList();
        if (!JumpPlugin.isListenerAdded)
        { 
            System.out.println("Jump.showFilesJump: Try to init JumpPlugin...");
            JumpPlugin.init();
        }
        
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
    }
//}}}
    
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
        // TagsJumpAction tja = new TagsJumpAction();
        // if (tja.parse())
        // {
        //     tja.showList();
        // }
    }//}}}
    
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
    }
//}}}    
    
//{{{ void reloadTagsOnProject()
    public void reloadTagsOnProject()
    {
        if (!isJumpEnabled()) return;
        if (!isProjectLoaded()) return;
        if (!JumpPlugin.isListenerAdded) JumpPlugin.init();
    }
//}}}
    
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
        //JumpPlugin.pja.JumpToPreviousTag();
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
            //System.out.println("showProjectJump: Setting active ProjectBuffer. ");
            if (PVActions.getCurrentProject(view) != null)
                {
                    JumpPlugin.getListener().reloadProjectForced();
                    ProjectBuffer b = ProjectBuffer.getProjectBuffer(PVActions.getCurrentProject(view).getName());
                    if (b == null) return;
                    JumpPlugin.setActiveProjectBuffer(b);
                    JumpPlugin.pja.JumpToTagByInput();
                }
        }
        //JumpPlugin.pja.JumpToTagByInput();
    }
//}}}

//{{{ void initJumpPlugin()
// init Jump. Used to avoid startup delay.
// private void initJumpPlugin()
// {
       
// }
//}}}

}

