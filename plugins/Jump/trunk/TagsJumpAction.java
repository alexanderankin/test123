//{{{ IMPORTS
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.search.SearchAndReplace;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import java.awt.event.*;
import java.awt.Component;
import java.awt.Font;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.border.*;
import javax.swing.event.*;

import java.io.IOException;

import ctags.bg.*;
//}}}

class TagsJumpAction
{
//{{{ FIELDS
    private CTAGS_BG bg;
    private CTAGS_Parser parser;
    private CTAGS_Buffer buff;
    private CtagsJumpMenu jm;
    private CTAGS_Entry[] entries;
    private View view;
//}}}

//{{{ CONSTRUCTOR
    public TagsJumpAction() {
        super();
        view = jEdit.getActiveView();
    }
//}}}

//{{{ boolean parse()
    public boolean parse() {
        
        bg = new CTAGS_BG(jEdit.getProperty("jump.ctags.path","options.JumpPlugin.ctags.def.path"));
        parser = bg.getParser();
        Vector v = new Vector();
        
        v.add(jEdit.getActiveView().getBuffer().getPath());

        try
        {
            buff = parser.parse(v);
        }
        catch (IOException e)
        {
            return false;
        }
        
        if (buff == null)
        {
            return false;
        }
    
        if (buff.size() < 1)
            return false;
            
        Vector e = new Vector();
        String val = new String();
        
        for (int i = 0; i < buff.size(); i++) {
            CTAGS_Entry en = (CTAGS_Entry) buff.get(i);
            val = en.getTagName()+" ("+en.getExCmd().trim()+")";
            en.setToStringValue(val);
            e.add(en);
        }

        Object[] a = new String[e.size()];
        a = e.toArray();
        entries = new CTAGS_Entry[a.length];
        for (int i = 0; i < a.length; i++) {
            entries[i] = (CTAGS_Entry) a[i];
        }

        Arrays.sort(entries, new AlphabeticComparator());
        e.clear();
        return true;
    }
//}}}

//{{{ void showList()
    public void showList() {
        jm = new CtagsJumpMenu(view , entries,
                new TagsListModel(), true, "Tag to jump:",50);
    }
//}}}

//{{{ class CtagsJumpMenu extends JumpList
    public class CtagsJumpMenu extends JumpList {

//{{{ CONSTRUCTOR       
        public CtagsJumpMenu(View parent, Object[] list,
                ListModel model, boolean incr_search, String title,
                int list_width) {
            super(parent, list, model, incr_search, title, list_width);
        }
//}}}

//{{{  void processAction       
        public void processAction(Object o) {

            JList l = (JList) o;
            SearchAndReplace search = new SearchAndReplace();

            //String tag = (String) l.getModel().getElementAt(l.getSelectedIndex());
            //tag = tag.substring(tag.indexOf(" ",0)+2,tag.length()-1);
            
            CTAGS_Entry en = (CTAGS_Entry)l.getModel().getElementAt(l.getSelectedIndex());
            String tag = en.getExCmd();
            
            Log.log(Log.DEBUG,this,"try to find-" + tag);
            search.setSearchString(tag);
            
            search.setIgnoreCase(false);
            search.setRegexp(false);
            search.setReverseSearch(false);
            search.setBeanShellReplace(false);
            search.setAutoWrapAround(true);

            try 
            {
              search.find(view, view.getBuffer(), 0);
            } catch (Exception e) 
            {
                Log.log(Log.DEBUG,this,"failed to find - " + tag);
            }
            
        }
//}}}

//{{{ void processInsertAction
        public void processInsertAction(Object o) 
        {
            JList l = (JList) o;
            CTAGS_Entry en = (CTAGS_Entry)l.getModel().getElementAt(l.getSelectedIndex());
            String tag = en.getTagName();
            view.getTextArea().setSelectedText(tag);
        }
    }
//}}}

//}}}

//{{{ class TagsListModel
    class TagsListModel extends AbstractListModel 
    {
//{{{  int getSize()       
        public int getSize() 
        {
            return entries.length;
        }
//}}}

//{{{  Object getElementAt      
        public Object getElementAt(int index) 
        {
            return entries[index];
        }
    }
//}}}

//}}}

//{{{ class AlphabeticComparator
    class AlphabeticComparator implements Comparator {
        public int compare (Object o1, Object o2) {

            CTAGS_Entry s1 = (CTAGS_Entry) o1;
            CTAGS_Entry s2 = (CTAGS_Entry) o2;
            return s1.getTagName().toLowerCase().compareTo(s2.getTagName().toLowerCase());
        }
    }
//}}}
}
//end of TagsJumpAction.java