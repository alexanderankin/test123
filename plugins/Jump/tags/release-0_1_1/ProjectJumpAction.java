//{{{ imports
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

//{{{ class ProjectJumpAction
public class ProjectJumpAction
{
//{{{ fields
    public boolean TagsAlreadyLoaded = false;
    public JumpHistory History = new JumpHistory();
    private CTAGS_Buffer buff;
    private Vector DuplicateTags;
    private ProjectTagsJump jm;
    private CTAGS_Entry[] entries;
    private JumpEventListener listener;
    private View view;
    private TypeTag typeTagWindow;
//}}}

//{{{ CONSTRUCTOR
    public ProjectJumpAction()
    {
     typeTagWindow = new TypeTag(jEdit.getActiveView());   

    }
//}}}

//{{{ void reloadFile
/**
* When file modified and saved, we need to update tags
*/ 
   public void reloadFile(String f)
    {
        listener.ctags_buff.remove(f);
        this.addFile(f);
    }
//}}}

//{{{ void removeFile
/**
* Remove all tags (which founded in spec. file) from CTAGS_Buffer
*/ 
    public void removeFile(String f)
    {
        listener.ctags_buff.removeFile(f);
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
            if (f==null) return;
            CTAGS_Buffer new_buff = listener.ctags_bg.getParser().parse(f);
            if (new_buff == null)
            {
                return;
            }
            TagsAlreadyLoaded = true;
            listener.ctags_buff.append(new_buff,f);
        } 
        catch (IOException e)
        {
            return;  
        }
    }
//}}}

//{{{ HISTORY

    public void addToHistory(CTAGS_Entry en) 
    {
        History.add(en);
    }
    
    public void clearHistory()
    {
        History.clear();
    }
    
    public void JumpToPreviousTag()
    {
        if (jEdit.getBooleanProperty("jump.enable", false) == false)
        {
            GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.enable", new Object[0]);
            return;    
        }
        
        CTAGS_Entry en = (CTAGS_Entry)History.getPrevious();

        if (en == null)
        {
            return;
        }
        this.JumpToTag(en, false);

    }
//}}}

//{{{ JUMPINGS

//{{{ void JumpToTag()
    public void JumpToTag()
    {   
        view = jEdit.getActiveView();
        listener = JumpPlugin.getListener();
        
        if (PVActions.getCurrentProject(view) != null && listener.PROJECT==null)
        {
            JumpPlugin.listener.reloadTags(ProjectViewer.getViewer(view), PVActions.getCurrentProject(view));
        }
        getTagBySelection(this.getSelection());
    }
//}}}

//{{{ void JumpToTagByInput()
    public void JumpToTagByInput()
    {
        view = jEdit.getActiveView();
        listener = JumpPlugin.getListener();
        
        if (PVActions.getCurrentProject(view) != null)
        {
            if (listener.PROJECT==null)
            {
                if (JumpPlugin.reloadTagsOnProject()== true)
                {
                     typeTagWindow._show();
                }
                else 
                {
                    return; 
                }
            }
            else
            {
               typeTagWindow._show();
            }
        }
    }
//}}}
       
//{{{ void JumpToTag(CTAGS_Entry en, boolean AddToHistory)
    private void JumpToTag(CTAGS_Entry en, boolean AddToHistory)
    {
            if (listener.PROJECT.getFiles().size() < 1)
            {
                return;
            }
        final String HistoryModelName ="jump.tag_history.project."+JumpPlugin.listener.PROJECT_NAME;
        final String pattern = en.getExCmd();
        final CTAGS_Entry en_for_history = en;
        final boolean add_hist = AddToHistory;
        final View v = jEdit.getActiveView();  // for VFSManager inner class
        boolean AlreadyOpened = false;
        Buffer[] buffs = jEdit.getBuffers();
        for (int i = 0; i < buffs.length; i++)
        {
            if (buffs[i].getPath().equals(en.getFileName()) == true)
            {
               v.setBuffer(buffs[i]);
               AlreadyOpened = true;
               break;
            }
            
        }    
        //If file not opened yet, open it before jump.
        if (AlreadyOpened == false)
        {
            jEdit.openFile(v,en.getFileName());
            //Log.log(Log.DEBUG,this,"Open file: - "+en.getFileName());
        }    

        VFSManager.runInAWTThread(new Runnable() {
                public void run() {
                    // set the caret pos to the beginning for searching...
                    v.getTextArea().setCaretPosition(0);

        SearchAndReplace search = new SearchAndReplace();
        search.setIgnoreCase(false);
        search.setRegexp(false);
        search.setReverseSearch(true);
        search.setBeanShellReplace(false);
        search.setAutoWrapAround(true);
        search.setSearchString(pattern);

        Log.log(Log.DEBUG,this,"Try to find: - "+pattern);
        try
        {
            if (search.find(v, v.getBuffer(), 0)==false)
            {
                //Log.log(Log.DEBUG,this,"Can\'t find pattren: "+pattern);
            }
            else
            {
                if (add_hist==true)
                {
                    addToHistory(en_for_history);
                    HistoryModel.getModel(HistoryModelName).addItem(en_for_history.getTagName());
                }
            }
        }
        catch (Exception e)
        {
            Log.log(Log.DEBUG,this,"Exception during search.find() " + pattern);
        }
        
        }
            });	
    }
//}}}

//{{{ String getSelection()
    public String getSelection()
    {
        String sel = jEdit.getActiveView().getTextArea().getSelectedText();
        //Log.log(Log.DEBUG,this,"Selection-"+sel);
        
        if (sel == null)
        {
            view.getTextArea().selectWord();
            sel = view.getTextArea().getSelectedText();
        }
        
        if (sel==null) return null;

        return sel.trim();      
    }
//}}}
  
//{{{ void getTagBySelection(
  public void getTagBySelection(String sel)
    {
        listener = JumpPlugin.getListener();
        if (listener.ctags_buff == null || sel == null)
        {
            return;
        }
        
        Vector tags = listener.ctags_buff.getEntry(sel);

        if (tags.size() < 1)
        {
            Log.log(Log.DEBUG,this,"No tags found! - "+sel);
            return;
        }

        String ToStringValue;
        CTAGS_Entry entry;
        int a;
        for (int i = 0; i < tags.size(); i++)
        {
            entry = (CTAGS_Entry) tags.get(i);
            a = entry.getFileName().lastIndexOf(System.getProperty("file.separator"));
            if (a == -1)
            {
                ToStringValue = entry.getTagName();
            }
            else
            {
                ToStringValue = entry.getFileName().substring(a + 1);
                ToStringValue = ToStringValue.trim() + " (" +
                        entry.getExCmd() + ")";
            }
            
            entry.setToStringValue(ToStringValue);
        }
        
        if (tags.size() == 1)
        {
            CTAGS_Entry en = (CTAGS_Entry) tags.get(0);
            this.JumpToTag(en,true);
        }
        else
        {
            entries = new CTAGS_Entry[tags.size()];
            for (int i = 0; i < tags.size(); i++)
            {
                entries[i] = (CTAGS_Entry) tags.get(i);
            }
            Arrays.sort(entries, new AlphabeticComparator());

            jm = new ProjectTagsJump(view , entries,
                    new ProjectTagsListModel(), true, "Files where tag found:",50);
        }
    }
//}}}
//}}}

//{{{ class ProjectTagsJump
    public class ProjectTagsJump extends JumpList
    {
        View view = jEdit.getActiveView();
        
//{{{ CONSTRUCTOR
        public ProjectTagsJump(View parent, Object[] list, ListModel model, boolean incr_search, String title, int list_width)
        {
            super(parent, list, model, incr_search, title, list_width);
        }
//}}}

//{{{ void processAction
        public void processAction(Object o)
        {
            JList l = (JList) o;
            CTAGS_Entry tag = (CTAGS_Entry) l.getModel().getElementAt(l.getSelectedIndex());
            JumpToTag(tag,true);
        }
//}}}
        
//{{{ void updateStatusBar
        public void updateStatusBar(Object o)
        {
// TODO: Check property SHOW_STATUSBAR_MESSAGES before proceed updateStatusBar()
            JList l = (JList) o;
            CTAGS_Entry tag = (CTAGS_Entry) l.getModel().getElementAt(l.getSelectedIndex());
            view.getStatus().setMessageAndClear(prepareStatusMsg(tag));
        }
//}}}
        
//{{{ String prepareStatusMsg
    private String prepareStatusMsg(CTAGS_Entry en)
    {
        StringBuffer ret = new StringBuffer();
        String ext_fields = en.getExtensionFields();
           
        if (ext_fields.length()>3)
        {
           ext_fields = ext_fields.substring(3);
        }
        else
        {
           ext_fields = "";  
        }
        
        if (!ext_fields.equals(""))
        {
            //Ugly workaround... :((
           int f_pos = ext_fields.lastIndexOf("\t");
           if (f_pos != -1)
           {
                ext_fields = ext_fields.substring(0,ext_fields.lastIndexOf("\t")+1);    
           }
           ret.append(ext_fields+"   "); 
        }
        ret.append("file: "+en.getFileName());
        return ret.toString();
    }
//}}}
        
//{{{ void keyPressed
        public void keyPressed(KeyEvent evt)
        {
            switch (evt.getKeyCode())
            {
            case KeyEvent.VK_ENTER:
                processAction(itemsList);
                dispose();
                evt.consume();
                break;
            }
        }
    }
//}}}

//}}}

//{{{ class ProjectTagsListModel
    class ProjectTagsListModel extends AbstractListModel
    {
        public int getSize()
        {
            return entries.length;
        }

        public Object getElementAt(int index)
        {
            return entries[index];
        }
    }
    // End of WorkspaceTagsListModel
//}}}

//{{{ class AlphabeticComparator
    class AlphabeticComparator implements Comparator
    {
        public int compare (Object o1, Object o2)
        {
            CTAGS_Entry e1 = (CTAGS_Entry) o1;
            CTAGS_Entry e2 = (CTAGS_Entry) o2;

            return e1.getFileName().toLowerCase().compareTo(
                    e2.getFileName().toLowerCase());
        }
    }
//}}}

}
//}}}
