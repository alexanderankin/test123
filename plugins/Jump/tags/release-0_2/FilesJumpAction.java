// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

//{{{ imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import java.util.*;

import java.awt.event.*;
import java.awt.Component;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.event.*;

import ctags.bg.*;
//}}}

class FilesJumpAction
{

//{{{ fields
    private Object[] files;
    public HashMap tabs_files = new HashMap();
    private View view;
    private ProjectBuffer currentTags;
//}}}

//{{{ constructor
    public FilesJumpAction() 
    {
        super();
        tabs_files = new HashMap();
    }
//}}}

//{{{ void showList
    public void showList() 
    {
        // if (PVActions.getCurrentProject(view) != null && JumpPlugin.listener.PROJECT==null)
        // {
        //     JumpPlugin.listener.reloadTags(ProjectViewer.getViewer(view), PVActions.getCurrentProject(view));
        // }
        files = getFileList();
        if (files==null) return;
        FilesJumpMenu jl = new FilesJumpMenu(view , files,
                new FilesListModel(), true, "File to jump:",30);
    }
//}}}

//{{{ Object[] getTabsList()
private Object[] getFileList() 
    {
        view = jEdit.getActiveView();
        // if (JumpPlugin.getListener().ProjectFiles == null)
        // {
        //     GUIUtilities.message(view, "JumpPlugin.no_project", new Object[0]);
        //     return null;  
        // }
        currentTags = JumpPlugin.getActiveProjectBuffer();
        if (currentTags == null) return null;
        if (currentTags.PROJECT_FILES.size() <2) return null;  
        
        Vector files = currentTags.PROJECT_FILES;
        
        String[] res = new String[files.size()];
        for (int i=0; i<files.size(); i++)
        {
            String path = new String();
            path = (String)files.get(i);
            res[i] = path.substring(path.lastIndexOf(System.getProperty("file.separator"))+1);

            tabs_files.put(res[i],path);
        }
      
        Arrays.sort(res, new AlphabeticComparator());
        return res;
    }
    // end of getTabsList
//}}}
    
//{{{ class FilesJumpMenu
    public class FilesJumpMenu extends JumpList {

        public FilesJumpMenu(View parent, Object[] list,
                ListModel model, boolean incr_search, String title,
                int list_width) {
            super(parent, list, model, incr_search, title, list_width);
        }
        
        public void updateStatusBar(Object o)
        {
// TODO: Check property SHOW_STATUSBAR_MESSAGES before proceed updateStatusBar()
            JList l = (JList) o;
            String tab_name = (String) l.getModel().getElementAt(l.getSelectedIndex());
            String file_name = (String)tabs_files.get(tab_name);
            view.getStatus().setMessageAndClear("file: "+file_name);
        }
        
    // private String prepareStatusMsg(CTAGS_Entry en)
    // {
        
    //     return ("file: "+en.getFileName());
    // }
        
        public void processAction(Object o) 
        {
            JList l = (JList) o;

            String tab_name = (String) l.getModel().getElementAt(l.getSelectedIndex());
            String file_name = (String)tabs_files.get(tab_name);
            jEdit.openFile(parent,file_name);

        }
    }
//}}}

//{{{ class FilesListModel
    //ListModel for files JumpList
    class FilesListModel extends AbstractListModel {
        public int getSize() {
            return files.length;
        }

        public Object getElementAt(int index) {
            String i = (String) files[index];
            return i;
        }
    }
//}}}

//{{{ class AlphabeticComparator
    //Comparator for sorting array with ignoreCase...
    class AlphabeticComparator implements Comparator {
        public int compare (Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }

    }
//}}}
}