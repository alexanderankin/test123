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

///{{{ class JumpEventListener
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
    //public ProjectJumpAction pja;
//}}}
    
//{{{ COSTRUCTOR   
    public JumpEventListener()  
    {
        super();
    }
//}}}

    
//{{{  handleMessage(EBMessage message)  method   
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
                    JumpPlugin.pja.addFile(bu.getBuffer().getPath());
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
        
//{{{  reloadTags(ProjectViewer viewer, VPTProject p)  
    public boolean reloadTags(ProjectViewer viewer, VPTProject p)
    {
        if (isAddedToBus == false) 
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
        
        JumpPlugin.pja = new ProjectJumpAction();
        
        this.PROJECT = p;
        
        ctags_bg = new CTAGS_BG(jEdit.getProperty("jump.ctags.path","options.JumpPlugin.ctags.def.path"));
        this.PROJECT_ROOT = p.getRootPath();
        this.PROJECT_NAME = p.getName();
        
        //Log.log(Log.DEBUG,this,"ProjectFiles initialized! Total files = "+ ProjectFiles.size());
        
        String s = System.getProperty("file.separator");
        
        PROJECT_TAGS = new File (System.getProperty("user.home")+s+".jedit"+s+"projectviewer"+s+"projects"+s+PROJECT_NAME+".jump");
        
                Collection v0 = Collections.synchronizedCollection(p.getFiles());
                Vector v = new Vector(v0);
                
                for (int i=0; i<v.size(); i++)
                {
                    VPTFile f = (VPTFile)v.get(i);
                    this.ProjectFiles.add(f.getCanonicalPath());
                }
        try
        {   // If no .jump file found - try to create new one
            if (PROJECT_TAGS.exists() == false)
            {
                ctags_buff = ctags_bg.getParser().parse(this.ProjectFiles);
                ctags_bg.saveBuffer(ctags_buff , PROJECT_TAGS.toString());
                viewer.setEnabled(true);
                return true;
            }
            // Read already seriailzed file 
            else
            {
                ctags_buff = ctags_bg.loadBuffer(PROJECT_TAGS.toString());
                viewer.setEnabled(true);
                //Log.log(Log.DEBUG,this,"JumpEventListener: reload done;");
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

//{{{ projectLoaded method    
    public void projectLoaded(ProjectViewerEvent evt) 
    {
        saveProjectBuffer();
        
        if (evt.getProject() != null)
        {
            ProjectFiles.clear();
            reloadTags(evt.getProjectViewer(), evt.getProject()); 
            JumpPlugin.pja.clearHistory();
        }
    }
//}}}

//{{{ projectAdded method    
    public void projectAdded(ProjectViewerEvent evt)      
    {
        saveProjectBuffer();
        try 
        {
            ProjectManager.getInstance().save();
        }
        catch (Exception e)
        {
          Log.log(Log.DEBUG,this,"JumpEventListener: projectAdded() EXCEPTION DURING ProjectManager.getInstance().save();");     
        }
        if (evt.getProject() != null)
        {
            reloadTags(evt.getProjectViewer(), evt.getProject());
        }
    }
//}}}

//{{{ projectRemoved method    
    public void projectRemoved(ProjectViewerEvent evt) 
    {
        PROJECT_TAGS.deleteOnExit();
        Log.log(Log.DEBUG,this,"JumpEventListener! "+PROJECT_TAGS+" will removed on Jedit exit.");    
    }
//}}} 

//{{{ saveProjectBuffer()
public void saveProjectBuffer()
{
    if (ctags_buff !=null && PROJECT_TAGS != null)
        {
            ctags_bg.saveBuffer(ctags_buff , PROJECT_TAGS.toString());   
        }   
}

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
}
//}}}
