// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

//{{{ IMPORTS
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import java.util.*;
import java.io.*;
import ctags.bg.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.event.*;
//}}}

public class JumpEventListener extends ProjectViewerAdapter implements EBComponent
{
//{{{ FIELDS    
    public VPTProject PROJECT;
    public String  PROJECT_ROOT = new String();
    public String PROJECT_NAME = new String();
    public File PROJECT_TAGS;
    public Vector ProjectFiles = new Vector();
    
    public CTAGS_Buffer ctags_buff;
    public CTAGS_BG ctags_bg;
    public Jump jump_actions;
    
    private boolean isAddedToBus = false;
    // Workaround fields. When PV2.0.1 fires Project_Added, it immediately fires Project_Loaded, but with no files.
    // I'm set isNewProject = true at projectAdded method, then check at projectLoaded.
    private boolean isNewProject = false;
    // If isNewProject=false at Project_Loaded() - I set needReload=true, to reload project (just once)
    private boolean needReload = false;
//}}}
    
//{{{ COSTRUCTOR   
    public JumpEventListener()  
    {
        super();
        if (!isAddedToBus) 
        {
            EditBus.addToBus(this);
            isAddedToBus = true;
        }
    }
//}}}

//{{{ handleMessage(EBMessage message)  method   
    public void handleMessage(EBMessage message)    
    {
        if (message instanceof BufferUpdate)
        {
            try
            {
                BufferUpdate bu=(BufferUpdate)message;
                if(bu.getWhat()==BufferUpdate.SAVED) 
                {
                    if (jEdit.getBooleanProperty("jump.parse_on_save") == false) return;
                    JumpPlugin.getActiveProjectBuffer().addFile(bu.getBuffer().getPath());
                    JumpPlugin.getActiveProjectBuffer().checkFileDeleted();
                }
            }
            catch (Exception e)
            {
                return;   
            }
        }
    }
//}}} 

//{{{ void errorMsg
        public void errorMsg(String s)      
        {
            GUIUtilities.message(jEdit.getActiveView(), s, new Object[0]);
        }

//}}}
        
//{{{ reloadTags(ProjectViewer viewer, VPTProject p)  
    public boolean reloadTags(ProjectViewer viewer, VPTProject p)
    {
        if (!isAddedToBus) 
        {
            EditBus.addToBus(this);
            isAddedToBus = true;
        }
        
        if (new File(jEdit.getProperty("jump.ctags.path","options.JumpPlugin.ctags.def.path")).exists()==false)
        {
            errorMsg("JumpPlugin.ctags.path.incorrect");
            return false;   
        }

        if (p.getFiles().size()<1)
        {
            //No error message here to avoid it on project-import files    
            return false;
        }
        
        if (viewer==null || p == null)
        {
            errorMsg("JumpPlugin.no_project");
            return false;
        }
        if (JumpPlugin.pja == null) JumpPlugin.pja = new ProjectJumpAction();
        
        this.PROJECT = p;
        
        ctags_bg = new CTAGS_BG(jEdit.getProperty("jump.ctags.path","options.JumpPlugin.ctags.def.path"));
        this.PROJECT_ROOT = p.getRootPath();
        this.PROJECT_NAME = p.getName();
        
        //Log.log(Log.DEBUG,this,"ProjectFiles initialized! Total files = "+ ProjectFiles.size());
        
        String s = System.getProperty("file.separator");
        
        this.PROJECT_TAGS = new File (System.getProperty("user.home")+s+".jedit"+s+"projectviewer"+s+"projects"+s+this.PROJECT_NAME+".jump");
        
                Collection v0 = Collections.synchronizedCollection(p.getFiles());
                Vector v = new Vector(v0);
                
                this.ProjectFiles.clear();
                System.out.println("Files total = "+v.size());
                for (int i=0; i<v.size(); i++)
                {
                    VPTFile f = (VPTFile)v.get(i);
                    this.ProjectFiles.add(f.getCanonicalPath());
                    System.out.println("Added: "+f.getCanonicalPath());
                }
        try
        {   // If no .jump file found - try to create new one
            if (this.PROJECT_TAGS.exists() == false)
            {
                System.out.println("create tags");
                ctags_buff = ctags_bg.getParser().parse(this.ProjectFiles);
                ctags_bg.saveBuffer(ctags_buff , this.PROJECT_TAGS.toString());
                viewer.setEnabled(true);
                return true; 
            }
            // Read already seriailzed file 
            else
            {   
                // Unwanted workaround
                // If file deleted form project I must save tags before reload it.
                //System.out.println("read tags");
                //ctags_bg.saveBuffer(ctags_buff , PROJECT_TAGS.toString());
                
                ctags_buff = ctags_bg.loadBuffer(PROJECT_TAGS.toString());
                viewer.setEnabled(true);
                return true;
            }
        }
        catch (Exception e)
        {
            errorMsg("JumpPlugin.ctags.path.incorrect");
            viewer.setEnabled(true);
            return false;
        } 
        
    }
//}}}

//{{{ projectLoaded, reloadProjectForced, projectAdded, projectRemoved

//{{{ projectLoaded method    
    public void projectLoaded(ProjectViewerEvent evt) 
    {
        System.out.println("JumpEventListener: projectLoaded "+ evt.getProject());
 
        if (isNewProject == true)
        {
            System.out.println("Can\'t setup project. I'll setup it next time");
            //isNewProject = false;
        }
        
        if (evt.getProject() != null)
        {
            // If this project already loaded as ProjectBuffer, just set it active.
            if (JumpPlugin.hasProjectBuffer(evt.getProject().getName()) && needReload==false) 
            {
                JumpPlugin.setActiveProjectBuffer(JumpPlugin.getProjectBuffer(evt.getProject().getName()));
                System.out.println("JumpEventListener: switch to project - "+evt.getProject().getName());
            }
            else
            {
                // If this project loaded at first time, we create new ProjectBuffer, and then set it active.
                if (isNewProject == false)
                    {
                        ProjectBuffer bu = ProjectBuffer.getProjectBuffer(evt.getProject().getName());
                        if (bu instanceof ProjectBuffer && bu != null)
                        {
                            JumpPlugin.addProjectBuffer(bu);
                            JumpPlugin.setActiveProjectBuffer(bu);
                            System.out.println("JumpEventListener: projectLoaded!");
                        }
                    }
                else
                {
                    isNewProject = false;                
                }
            }
        }
    }
//}}}

//{{{ reloadProjectForced()
public void reloadProjectForced() 
{
    if (needReload == true)
    {
        System.out.println("JumpEventListener: reloadProjectForced()");
        View view = jEdit.getActiveView();
        ProjectViewerEvent e = new ProjectViewerEvent(ProjectViewer.getViewer(view), PVActions.getCurrentProject(view));
        projectLoaded(e);
        needReload = false;
    }
}
//}}}

//{{{ projectAdded method
// Add project to projectBuffers and set it as active    
    public void projectAdded(ProjectViewerEvent evt)      
    {
        isNewProject = true;
        needReload = true;
    }
//}}}

//{{{ projectRemoved method     
    public void projectRemoved(ProjectViewerEvent evt) 
    {
        JumpPlugin.removeProjectBuffer(evt.getProject().getName());
    }
//}}} 

//}}}

//{{{ saveProjectBuffer()
public void saveProjectBuffer()
{
    if (ctags_buff !=null && PROJECT_TAGS != null)
        {
            ctags_bg.saveBuffer(ctags_buff , PROJECT_TAGS.toString());   
        }   
}

//{{{ boolean CtagsTest()
public boolean CtagsTest()
{
    CTAGS_BG test_bg = new CTAGS_BG(jEdit.getProperty("jump.ctags.path","options.JumpPlugin.ctags.def.path")); 
    String s = System.getProperty("file.separator");
    try
    {    
    CTAGS_Buffer test_buff = test_bg.getParser().parse(System.getProperty("user.home")+s+".jedit"+s+"properties");
    return true;
    }
    catch (Exception e)
    {
        return false;
    }
}
//}}}

//}}}
}
