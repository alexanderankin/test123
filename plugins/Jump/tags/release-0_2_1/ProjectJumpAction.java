// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

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

public class ProjectJumpAction
{
//{{{ fields
    public boolean TagsAlreadyLoaded = false;
    //public JumpHistory History = new JumpHistory();
    private CTAGS_Buffer buff;
    private Vector DuplicateTags;
    private ProjectTagsJump jm;
    private CTAGS_Entry[] entries;
    private JumpEventListener listener;
    private View view;
    private TypeTag typeTagWindow;
    
    private ProjectBuffer currentTags;
    
    private int carret_pos;
    //private int historyIndex;
//}}}

//{{{ CONSTRUCTOR
    public ProjectJumpAction()
    { }
//}}}

//{{{ HISTORY STUFF

//{{{ addToHistory(CTAGS_Entry en)
    public void addToHistory(CTAGS_Entry en) 
    {
        JumpPlugin.getActiveProjectBuffer().JUMP_HISTORY.add(en);
        JumpPlugin.getActiveProjectBuffer().HISTORY.addItem(en.getTagName());
    }
//}}}
    
//{{{ clearHistory()
// QUESTION: Did we use this method at all?
    public void clearHistory()
    {
        JumpPlugin.getActiveProjectBuffer().JUMP_HISTORY.clear();
    }
//}}}
    
//{{{ JumpToPreviousTag()
    public void JumpToPreviousTag()
    {
        CTAGS_Entry en = (CTAGS_Entry)JumpPlugin.getActiveProjectBuffer().JUMP_HISTORY.getPrevious();
        
        if (en == null)
        {
            return;
        }
        this.JumpToTag(en, false, false);
    }
//}}}
    
//}}}

//{{{ JUMPINGS

//{{{ void JumpToTag()

// This method proceed in actions.xml
    public void JumpToTag()
    {   
        getTagBySelection(this.getSelection());
    }
//}}}

//{{{ void JumpToTagByInput()
// This method proceed in actions.xml
    public void JumpToTagByInput()
    {
        JumpPlugin.getActiveProjectBuffer().getTypeTag()._show();
    }
//}}}
       
//{{{ void JumpToTag(CTAGS_Entry en, boolean AddToHistory, boolean newView)
    private void JumpToTag(CTAGS_Entry en, boolean AddToHistory, boolean newView)
    {
        final String HistoryModelName ="jump.tag_history.project."+JumpPlugin.listener.PROJECT_NAME;
        final String pattern = en.getExCmd();
        final CTAGS_Entry en_for_history = en;
        final boolean add_hist = AddToHistory;
        // for VFSManager inner class
        final View v;
        
        if (newView)
        {
            v = jEdit.newView(jEdit.getActiveView());
               
        }
        else
        {
            v = jEdit.getActiveView();    
        }
        
        
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
        if (newView)
        {
            jEdit.openFile(v, en.getFileName());
            GUIUtilities.requestFocus(jEdit.getActiveView(), v.getTextArea()); 
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
                if (!search.find(v, v.getBuffer(), 0))
                {
                    //Log.log(Log.DEBUG,this,"Can\'t find pattren: "+pattern);
                }
                else
                {
                    if (add_hist==true)
                    {
                        addToHistory(en_for_history);
                        //HistoryModel.getModel(HistoryModelName).addItem(en_for_history.getTagName());
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
        
        carret_pos = jEdit.getActiveView().getTextArea().getCaretPosition();
        String sel = jEdit.getActiveView().getTextArea().getSelectedText();
        //Log.log(Log.DEBUG,this,"Selection-"+sel);
        
        if (sel == null)
        {
            jEdit.getActiveView().getTextArea().selectWord();
            sel = jEdit.getActiveView().getTextArea().getSelectedText();
        }
        
        if (sel==null) return null;

        return sel.trim();      
    }
//}}}
  
//{{{ void getTagBySelection(
  public void getTagBySelection(String sel)
    {
        view = jEdit.getActiveView();
        
        // Grab active ctags project
        currentTags = JumpPlugin.getActiveProjectBuffer();
        if (currentTags == null || sel == null) return;
        
        Vector tags = currentTags.PROJECT_CTBUFFER.getEntry(sel);
        if (tags == null || tags.size() < 1)
        {
            Log.log(Log.DEBUG,this,"getTagBySelection: No tags found! - "+sel);
            view.getTextArea().selectNone();
            view.getTextArea().setCaretPosition(carret_pos);
            return;
        }
        
        // ToStringValue - how to display CTAGS_Entry at JumpList. See CTAGS_Entry setToStringValue()
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
            this.JumpToTag(en,true, false);
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
                    new ProjectTagsListModel(), true, "Files where tag found:",35);
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
            JumpToTag(tag,true, false);
        }
//}}}

//{{{ processInsertAction 
        public void processActionInNewView(Object o)
        {
            // TODO: Pass focus to newly opened view
            JList l = (JList) o;
            CTAGS_Entry tag = (CTAGS_Entry) l.getModel().getElementAt(l.getSelectedIndex());
            JumpToTag(tag,true, true);
        } //}}}
    //}
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
