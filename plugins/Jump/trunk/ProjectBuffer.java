// * :tabSize=4:indentSize=4: 
// * :folding=explicit:collapseFolds=1:

//{{{ -------------  Imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.jedit.io.VFSManager;

import java.awt.event.*;
import java.awt.Component;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;
import java.io.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.event.*;

import ctags.bg.*;

//}}}

/**
 * Class which store all info about VTProject and it's tags
 */
public class ProjectBuffer
{
    
//{{{ -------------  Fields
    /**
    *   HistoryMode for displaing TypeTag dialog
    */
    public HistoryModel HISTORY;
    /**
    *   Strores CTAGS_Entries which already jump
    */   
    public JumpHistory JUMP_HISTORY;
    public VPTProject PROJECT;
    public String PROJECT_ROOT;
    public String PROJECT_NAME;
    public File PROJECT_TAGS;
    public Vector PROJECT_FILES = new Vector(); 
    public Vector DELETE_HELPER = new Vector();
    public CTAGS_Buffer PROJECT_CTBUFFER;
    public TypeTag TYPE_TAG_WINDOW;
    public CTAGS_BG ctags_bg;
//}}}
    
//{{{ -------------  Constructor
    protected ProjectBuffer()
    {}
//}}}

//{{{   boolean init 
/**
 * Init given ProjectBuffer. (load tags, init history etc.) 
 */   
    protected boolean init(ProjectBuffer pb , String name)
    { 
        long t1;
        t1 = System.currentTimeMillis();
        ProjectManager pm = ProjectManager.getInstance();
        pb.PROJECT = pm.getProject(name);
        if (pb.PROJECT != null)
        {
            pb.ctags_bg = new CTAGS_BG(jEdit.getProperty("jump.ctags.path","options.JumpPlugin.ctags.def.path"));
            String s = System.getProperty("file.separator");
            
            pb.PROJECT_ROOT = pb.PROJECT.getRootPath();
            pb.PROJECT_NAME = name;
            pb.PROJECT_TAGS = new File (System.getProperty("user.home")+s+".jedit"+s+"projectviewer"+s+"projects"+s+this.PROJECT_NAME+".jump");
            Collection v0 = Collections.synchronizedCollection(PROJECT.getFiles());

            Vector v = new Vector(v0);
            
            for (int i=0; i<v.size(); i++)
            {
                VPTFile f = (VPTFile)v.get(i);
                pb.PROJECT_FILES.add(f.getCanonicalPath());
            }
            
            if (!loadJumpFile(pb)) return false;
            
            // Init JumpHistory...
            pb.JUMP_HISTORY = new JumpHistory();
            pb.HISTORY = HistoryModel.getModel("jump.tag_history.project."+pb.PROJECT_NAME);
            // Init TypeTag window class
            pb.TYPE_TAG_WINDOW = new TypeTag();
            System.out.println("Buffer creating took - "+(System.currentTimeMillis()-t1)+" ms");
            return true;
        }
        else
        {
            System.out.println("ProjectBuffer: Exception at constructor.");
            return false;
        }
        
    }
//}}}
    
//{{{ getProjectBuffer(String name)
/**
 * Query point to create new ProjectBuffer object
 */ 
    public static ProjectBuffer getProjectBuffer(String name)
    {
        ProjectBuffer pb = new ProjectBuffer();
        if (pb.init(pb, name)) return pb;
        return null;
    }
//}}}
    
//{{{ -------------   .jump file stuff

//{{{ createJumpFile()
/**
* Create .jump file for ProjectBuffer.
* If you need drop .jump file - @see JumpPlugin
*/
    public boolean createJumpFile(ProjectBuffer pb) 
    {
        try
        {
            pb.PROJECT_CTBUFFER = ctags_bg.getParser().parse(pb.PROJECT_FILES);
            // if project don't contain any vaild files to parse (for ex. html, xml, etc.) we return false... To avoid creating 
            if (pb.PROJECT_CTBUFFER == null) throw new Exception();
            pb.ctags_bg.saveBuffer(pb.PROJECT_CTBUFFER , pb.PROJECT_TAGS.toString());
            //System.out.println("createJumpFile(): .jump created");
            return true;
        }
        catch(Exception e)
        {
            System.out.println("createJumpFile(): can\'t create .jump");
            return false;
        }   
    }
//}}}

//{{{ dropJumpFile()
//     public void dropJumpFile()
//     {
           
//     }
//}}}

//{{{ void saveJumpFile()
    public void saveJumpFile()
    {
        ctags_bg.saveBuffer(PROJECT_CTBUFFER , PROJECT_TAGS.toString());    
    }
//}}}

//{{{ boolean loadJumpFile()
    public boolean loadJumpFile(ProjectBuffer pb)
    {
         
        try
        {   
        // If no .jump file found - try to create new one
        ProjectViewer viewer = ProjectViewer.getViewer(jEdit.getActiveView());
            if (pb.PROJECT_TAGS.exists() == false)
            {
                if (!createJumpFile(pb))
                {
                    if (viewer != null) viewer.setEnabled(true);
                    return false;
                }
                else
                {
                    if (viewer != null) viewer.setEnabled(true);
                    System.out.println("jump creqted");
                    return true; 
                }
            }
            // Read already seriailzed file 
            else
            {   
                pb.PROJECT_CTBUFFER = pb.ctags_bg.loadBuffer(pb.PROJECT_TAGS.toString());
                if (viewer != null) viewer.setEnabled(true);
                return true;
            }
        }
        catch (Exception e)
        {
            
            System.out.println("ProjectBuffer: ctags_path_incorrect");
            e.printStackTrace();
            
            ProjectViewer viewer = ProjectViewer.getViewer(jEdit.getActiveView());
            if (viewer != null) viewer.setEnabled(true);
            return false;
        }               
    }
//}}}

//}}}

//{{{ -------------    Add, remove, reload, checkFileDeleted methods 

//{{{ checkFileDeleted()
    /**
     *  Since ProjectViewer2.0.1 have't DELETE(ADD)_FILE_FROM(TO)_PROJECT, I must manualy check it.
     */
    public void checkFileDeleted()
    {
        Collection v0 = Collections.synchronizedCollection(PROJECT.getFiles());
        Vector v = new Vector(v0);
        Vector tmp_del = new Vector();
        DELETE_HELPER = new Vector();
        
        //System.out.println("checkFileDeleted: files in project = "+v.size()+ ", files in buffer = "+PROJECT_FILES.size());
        
        // DELETE_HELPER Vector - temporary storage of currnent project filenames.    
        for (int i=0; i<v.size(); i++)
        {
            VPTFile f = (VPTFile)v.get(i);
            DELETE_HELPER.add(f.getCanonicalPath());
        }
        // Now, when DELETE_HELPER is set, I start to examine is deleted or added files...
        
        // is deleted files?
        for( int i=0; i<PROJECT_FILES.size(); i++ )
        {
              if (!DELETE_HELPER.contains(PROJECT_FILES.get(i)))
              {
                removeFile((String)PROJECT_FILES.get(i));
                tmp_del.add(PROJECT_FILES.get(i));
              }
        }
        
        // is added files?
        for(int i=0; i<DELETE_HELPER.size(); i++)
        {
            if (!PROJECT_FILES.contains(DELETE_HELPER.get(i)))
            {
                addFile((String)DELETE_HELPER.get(i));
                PROJECT_FILES.add(DELETE_HELPER.get(i));
                System.out.println(DELETE_HELPER.get(i)+" file was added");
            }
        }
        
        // Now drop deleted files from PROJECT_FILES list
        if (tmp_del.size()>0)
        {
            System.out.println("Files to delete = "+tmp_del.size());
            for(int i=0; i<tmp_del.size(); i++)
            {
                PROJECT_FILES.remove((String)tmp_del.get(i));
                System.out.println(tmp_del.get(i)+" file was deleted.");
            }
        }
        
        DELETE_HELPER.clear();
        
    }
//}}}

//{{{ void addFile
/**
* When new file open, add its tag to CTAGS_Buffer
*/ 
    public void addFile(String f)
    {
        try    
        {
            Log.log(Log.DEBUG,this,"addFile: - "+f); 
            if (f == null || f.equals("")) return;
            CTAGS_Buffer new_buff = ctags_bg.getParser().parse(f);
            if (new_buff == null)
            {
                return;
            }
            //TagsAlreadyLoaded = true;
            PROJECT_CTBUFFER.append(new_buff,f);
        } 
        catch (IOException e)
        {
            return;  
        }
    }
//}}}

//{{{ void removeFile
/**
* Remove all tags (which founded in spec. file) from CTAGS_Buffer
*/ 
    public void removeFile(String f)
    {
        PROJECT_CTBUFFER.removeFile(f);
    }
//}}}

//{{{ void reloadFile
/**
* When file modified and saved, we need to update tags
*/ 
   public void reloadFile(String f)
    {
        PROJECT_CTBUFFER.remove(f);
        addFile(f);
    }
//}}}

//}}}

}

