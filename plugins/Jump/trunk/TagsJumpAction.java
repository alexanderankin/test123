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

//{{{ imports
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
import java.awt.*;
import java.awt.event.*;
import java.awt.Component;
import java.awt.Font;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.border.*;
import javax.swing.event.*;

import java.io.IOException;

import ctags.bg.*; //}}}

class TagsJumpAction
{
    //{{{ fields
    private CTAGS_BG bg;
    private CTAGS_Parser parser;
    private CTAGS_Buffer buff;
    private CtagsJumpMenu jm;
    private CTAGS_Entry[] entries;
    private View view;
    private ProjectBuffer currentTags; //}}}

    //{{{ constructor
    public TagsJumpAction()
    {
        super();
        view = jEdit.getActiveView();
    } //}}}

    //{{{ boolean parse()
    public boolean parse()
    {
        currentTags = JumpPlugin.getActiveProjectBuffer();

        if (currentTags == null)
        {
            return false;
        }

        // TODO: if checkbox in options checked - display tags for whole project, if not checked - just for current file. For now it just for current file. 
        Vector e = new Vector();
        Vector v = currentTags.PROJECT_CTBUFFER.getTagsByFile(jEdit.getActiveView().getBuffer().getPath());
        if (v.size() <1) return false;

        String val = new String();

        for (int i = 0; i < v.size(); i++) 
        {
            CTAGS_Entry en = (CTAGS_Entry)v.get(i);
            val = en.getTagName()+" ("+en.getExCmd().trim()+")";
            en.setToStringValue(val);
            e.add(en);
        }

        Object[] a = new String[e.size()];
        a = e.toArray();
        entries = new CTAGS_Entry[a.length];
        for (int i = 0; i < a.length; i++) 
        {
            entries[i] = (CTAGS_Entry) a[i];
        }

        Arrays.sort(entries, new AlphabeticComparator());
        e.clear();
        return true;
    } //}}}

    //{{{ void showList()
    public void showList()
    {
        if (parse())
        {
            jm = new CtagsJumpMenu(jEdit.getActiveView() , entries, new TagsListModel(), true, "Tag to jump:",35, Jump.getListLocation());
        }
    } //}}}

    //{{{ class CtagsJumpMenu extends JumpList
    public class CtagsJumpMenu extends JumpList
    {

    //{{{ constructor
    public CtagsJumpMenu(View parent, Object[] list,
            ListModel model, boolean incr_search, String title,
            int list_width, Point location) 
    {
        super(parent, list, model, incr_search, title, list_width, location);
    } //}}}

    //{{{ updateStatusBar
    // TODO: Check property SHOW_STATUSBAR_MESSAGES before proceed updateStatusBar()
    public void updateStatusBar(Object o)
    {
        JList l = (JList) o;
        CTAGS_Entry tag = (CTAGS_Entry) l.getModel().getElementAt(l.getSelectedIndex());
        view.getStatus().setMessageAndClear(prepareStatusMsg(tag));
    } //}}}

    //{{{ prepareStatusMsg
    private String prepareStatusMsg(CTAGS_Entry en)
    {
        StringBuffer ret = new StringBuffer();
        String ext_fields = en.getExtensionFields();

        if (ext_fields.length()>3)
        {
           ext_fields = ext_fields.substring(3);
           //Ugly workaround... :((
           int f_pos = ext_fields.lastIndexOf("\t");
           if (f_pos != -1)
           {
                ext_fields = ext_fields.substring(0,ext_fields.lastIndexOf("\t")+1);
           }
        }
        else
        {
           return null;
        }
        return ext_fields;
    } //}}}

    //{{{ void processAction
    public void processAction(Object o)
    {
        JList l = (JList) o;
        SearchAndReplace search = new SearchAndReplace();
        view = jEdit.getActiveView();

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
          if (search.find(view, view.getBuffer(), 0))
          {
            JumpPlugin.getActiveProjectBuffer().JUMP_HISTORY.add(en); 
            JumpPlugin.getActiveProjectBuffer().HISTORY.addItem(en.getTagName());
          }
        }
        catch (Exception e)
        {
            Log.log(Log.DEBUG,this,"failed to find - " + tag);
        }
    } //}}}

    //{{{ void processInsertAction
    public void processInsertAction(Object o)
    {
        view = jEdit.getActiveView();
        JList l = (JList) o;
        CTAGS_Entry en = (CTAGS_Entry)l.getModel().getElementAt(l.getSelectedIndex());
        String tag = en.getTagName();
        view.getTextArea().setSelectedText(tag);
    } //}}}

    //{{{ void processActionInNewView
    public void processActionInNewView(Object o)
    {
        CtagsJumpMenu.this.processAction(o);
    } //}}}

    } //}}}

    //{{{ class TagsListModel
    class TagsListModel extends AbstractListModel
    {
        //{{{ int getSize()
        public int getSize()
        {
            return entries.length;
        } //}}}

        //{{{ Object getElementAt
        public Object getElementAt(int index)
        {
            return entries[index];
        } //}}}
    }
    //}}}

    //{{{ class AlphabeticComparator
    class AlphabeticComparator implements Comparator {
        public int compare (Object o1, Object o2) {

            CTAGS_Entry s1 = (CTAGS_Entry) o1;
            CTAGS_Entry s2 = (CTAGS_Entry) o2;
            return s1.getTagName().toLowerCase().compareTo(s2.getTagName().toLowerCase());
        }
    } //}}}
}