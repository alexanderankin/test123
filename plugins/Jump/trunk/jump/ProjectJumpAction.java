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

package jump;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import jump.ctags.CTAGS_Buffer;
import jump.ctags.CTAGS_Entry;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;


public class ProjectJumpAction
{

    public boolean TagsAlreadyLoaded = false;
    private int carret_pos;

    private CTAGS_Buffer buff;
    private Vector DuplicateTags;
    private ProjectTagsJump jm;
    private CTAGS_Entry[] entries;
    private JumpEventListener listener;
    private View view;
    private TypeTag typeTagWindow;
    private ProjectBuffer currentTags;


    public ProjectJumpAction()
    {}


    public void addToHistory(CTAGS_Entry en)
    {
        JumpPlugin.getActiveProjectBuffer().JUMP_HISTORY.add(en);
        JumpPlugin.getActiveProjectBuffer().HISTORY.addItem(en.getTagName());
    }
    
    public void clearHistory()
    {
        JumpPlugin.getActiveProjectBuffer().JUMP_HISTORY.clear();
    }
    
    public void JumpToPreviousTag()
    {
        CTAGS_Entry en = (CTAGS_Entry)JumpPlugin.getActiveProjectBuffer().JUMP_HISTORY.getPrevious();
        if (en == null)
        {
            return;
        }
        this.JumpToTag(en, false, false);
    }
    
    public void JumpToTag()
    {
        getTagBySelection(this.getSelection());
    }
    
    public void JumpToTagByInput()
    {
        JumpPlugin.getActiveProjectBuffer().getTypeTag()._show();
    }
    
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
        }

        VFSManager.runInAWTThread(new Runnable()
        {
            public void run() 
            {
                // set the caret pos to the beginning for searching...
                v.getTextArea().setCaretPosition(0);

                //SearchAndReplace search = new SearchAndReplace();
                SearchAndReplace.setIgnoreCase(false);
                SearchAndReplace.setRegexp(false);
                SearchAndReplace.setReverseSearch(true);
                SearchAndReplace.setBeanShellReplace(false);
                SearchAndReplace.setAutoWrapAround(true);
                SearchAndReplace.setSearchString(pattern);

                try
                {
                    if (!SearchAndReplace.find(v, v.getBuffer(), 0))
                    {
                        //Log.log(Log.DEBUG,this,"Can\'t find pattren: "+pattern);
                    }
                    else
                    {
                        if (add_hist==true)
                        {
                            addToHistory(en_for_history);
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
    
    public String getSelection()
    {
        carret_pos = jEdit.getActiveView().getTextArea().getCaretPosition();
        String sel = jEdit.getActiveView().getTextArea().getSelectedText();
        if (sel == null)
        {
            jEdit.getActiveView().getTextArea().selectWord();
            sel = jEdit.getActiveView().getTextArea().getSelectedText();
        }
        if (sel==null) return null;
        return sel.trim();
    }
    
    public void completeTag(boolean isGlobalSearch)
    {
        JEditTextArea textArea = jEdit.getActiveView().getTextArea();
        int caret = textArea.getCaretPosition();
        textArea.goToPrevWord(true,false);
        String sel = textArea.getSelectedText();
        textArea.setCaretPosition(caret);

        if (sel.equals("") || sel==null) return;

		Vector tags = new Vector();

        currentTags = JumpPlugin.getActiveProjectBuffer();
        if (currentTags == null) return;
        tags = currentTags.PROJECT_CTBUFFER.getEntresByStartPrefix(sel);
        if (tags == null || tags.size() < 1)
        {
            Log.log(Log.DEBUG,this,"completeTag: No tags found! - "+sel);
            return;
        }

        if (tags.size() == 1)
        {
            completeWord((String)tags.get(0));
            return;
        }

        String entry;
        Vector completions = new Vector();
        CompleteWordList cw;

        for(int i=0; i<tags.size(); i++)
        {
            entry = (String) tags.get(i);
            completions.add(new CompleteWordList.Completion(entry, false));
        }

        MiscUtilities.quicksort(completions,new MiscUtilities.StringICaseCompare());
        cw = new CompleteWordList(jEdit.getActiveView(), sel, completions, Jump.getListLocation(), "", isGlobalSearch);
    }
    
    private void completeWord(String wordToPaste)
    {
		JEditTextArea ta = jEdit.getActiveView().getTextArea();
		ta.goToPrevWord(true,false);
		ta.delete();
        ta.setSelectedText(wordToPaste);
    }
    
    public void getTagBySelection(String sel)
    {
        view = jEdit.getActiveView();
        Vector tags;
        
        // Grab active ctags project
        currentTags = JumpPlugin.getActiveProjectBuffer();
        if (currentTags == null || sel == null) return;

        tags = currentTags.PROJECT_CTBUFFER.getEntry(sel);
        if (tags == null || tags.size() < 1)
        {
            Log.log(Log.DEBUG,this,"getTagBySelection: No tags found! - "+sel);
            view.getTextArea().selectNone();
            view.getTextArea().setCaretPosition(carret_pos);
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
    
    public class ProjectTagsJump extends JumpList
    {
        View view = jEdit.getActiveView();


        public ProjectTagsJump(View parent, Object[] list, ListModel model, boolean incr_search, String title, int list_width)
        {
            super(parent, list, model, incr_search, title, list_width, Jump.getListLocation());
        }

        public void processAction(Object o)
        {
            JList l = (JList) o;
            CTAGS_Entry tag = (CTAGS_Entry) l.getModel().getElementAt(l.getSelectedIndex());
            JumpToTag(tag,true, false);
        }
        
        public void processActionInNewView(Object o)
        {
            JList l = (JList) o;
            CTAGS_Entry tag = (CTAGS_Entry) l.getModel().getElementAt(l.getSelectedIndex());
            JumpToTag(tag,true, true);
        } 
        
        public void updateStatusBar(Object o)
        {
            JList l = (JList) o;
            CTAGS_Entry tag = (CTAGS_Entry) l.getModel().getElementAt(l.getSelectedIndex());
            view.getStatus().setMessageAndClear(prepareStatusMsg(tag));
        }
        
        
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
}
