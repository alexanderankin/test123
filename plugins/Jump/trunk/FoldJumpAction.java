// * :tabSize=4:indentSize=4:
// * :folding=none:collapseFolds=1:

//{{{ IMPORTS
import org.gjt.sp.jedit.*;

import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.search.SearchAndReplace;

import java.util.*;
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

/**
 *  Shows the list of all folds founded in current buffer.
 *
 *@author     pavlikus
 *@created    1 Èþíü 2003 ã.
 */
class FoldJumpAction
{

//{{{ ---------  fields
    private View view;
    private Object[] foundedFolds;
    private Buffer buff;
    private String fold_pattern;

//}}}

//{{{ --------- constructor
    /**
     *  TODO: Detect the fold pattern for current buffer.
     *
     *@see    fold_pattern
     */
    public FoldJumpAction()
    {
        view = jEdit.getActiveView();
        buff = view.getBuffer();
    }

//}}}

//{{{ showFoldsList
    /**
     *  Construct new FoldJumpMenu and show it
     */
    public void showFoldsList()
    {
        foundedFolds = prepareFoldList();
        
        // If there no fold patterns
        if (foundedFolds == null || foundedFolds.length < 1)
        {
            return;
        }
        
        // If there just one fold founded - jump to it directly
        if (foundedFolds.length == 1)
        {
            FoldEntry e = (FoldEntry)foundedFolds[0];
            JumpToFoldDirectly(e); 
            return;
        }
        
        // If more than one fold founded - show jump list
        FoldJumpMenu jl = new FoldJumpMenu(view, foundedFolds, new FoldListModel(), true, "Fold to jump:", 30);
    }

//}}}

//{{{ Object[] prepareFoldList()
    /**
     *  Scan current buffer on availability <code>fold_pattern(s)</code>
     *
     *@return    Array of FoldEntry objects
     *@see       FoldEntry
     */
    private Object[] prepareFoldList()
    {

        Vector v = new Vector();
        int lines = buff.getLineCount();

        for (int i = 0; i < lines; i++)
        {
            if (buff.getLineText(i).indexOf("{{{") > -1)
            {
                FoldEntry en = new FoldEntry(buff.getLineText(i), i);
                v.add(en);
            }
        }
        // If there no fold patterns
        if (v.size() < 1)
        {
            return null;
        }
        
        Object[] r = v.toArray();

        if (jEdit.getBooleanProperty("jump.sort_foldlist", true))
        {
            Arrays.sort(r, new AlphabeticComparator());
        }

        return r;
    }
//}}}

//{{{ JumpToFoldDirectly
    private final void JumpToFoldDirectly(FoldEntry e)
    {
        //if (jEdit.getActiveView().getBuffer().getFoldLevel(e.getIndex()) == 0) return;
        jEdit.getActiveView().getEditPane().getTextArea().selectFold(e.getIndex());    
    }
//}}}

//}}}

//{{{ class FoldJumpMenu
    public class FoldJumpMenu extends JumpList
    {

        /**
         *@param  parent       jEdit View
         *@param  list         Array of list items to show
         *@param  model
         *@param  incr_search  not used :(
         *@param  title        title of JList
         *@param  list_width   width (characters) of JList
         *@see                 JumpList
         *@see                 FoldListModel
         */
        public FoldJumpMenu(View parent, Object[] list,
                ListModel model, boolean incr_search, String title,
                int list_width)
        {
            super(parent, list, model, incr_search, title, list_width);
        }

        public void processAction(Object o)
        {
            JList l = (JList) o;
            FoldEntry fold = (FoldEntry) l.getModel().getElementAt(l.getSelectedIndex());
            JumpToFoldDirectly(fold);
        }


        public void updateStatusBar(Object o)
        {
            JList l = (JList) o;
            FoldEntry fold = (FoldEntry) l.getModel().getElementAt(l.getSelectedIndex());
            String mess = "Line: " + (fold.getIndex()+1) + "  " + fold.toString();
            view.getStatus().setMessageAndClear(mess);
        }
    }

//}}}

//{{{ class FoldEntry
    class FoldEntry
    {
        private int index;
        private String text;

//{{{ --------- constructor
        /**
         *  Constructor for the FoldEntry object
         *
         *@param  fold_text  single textarea line which contain <code>fold_pattern</code>
         *@param  line       line number
         */
        FoldEntry(String fold_text, int line)
        {
            index = line;
            fold_text = fold_text.substring(fold_text.indexOf("{{{") + 3);
            text = fold_text.trim();
        }

//}}}
        public String toString()
        {
            return text;
        }

        public int getIndex()
        {
            return index;
        }
    }

//}}}

//{{{ class AlphabeticComparator
    //Comparator for sorting array with ignoreCase...
    class AlphabeticComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            FoldEntry s1 = (FoldEntry) o1;
            FoldEntry s2 = (FoldEntry) o2;
            return s1.toString().toLowerCase().compareTo(s2.toString().toLowerCase());
        }

    }

//}}}

//{{{ class FoldListModel
    class FoldListModel extends AbstractListModel
    {
        public int getSize()
        {
            return foundedFolds.length;
        }

        public Object getElementAt(int index)
        {
            FoldEntry i = (FoldEntry) foundedFolds[index];
            return i;
        }
    }
//}}}
}

