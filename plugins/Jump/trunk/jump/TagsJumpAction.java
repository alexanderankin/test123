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
import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import jump.ctags.CtagsMain;
import jump.ctags.CtagsBuffer;
import jump.ctags.CtagsEntry;
import jump.ctags.CtagsParser;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.util.Log;



class TagsJumpAction {
    private CtagsMain bg;
    private CtagsParser parser;
    private CtagsBuffer buff;
    private CtagsJumpMenu jm;
    private CtagsEntry[] entries;
    private View view;
    private ProjectBuffer currentTags;

    public TagsJumpAction() {
        super();
        view = jEdit.getActiveView();
    }

    public boolean parse() {
        currentTags = JumpPlugin.getActiveProjectBuffer();

        if (currentTags == null) {
            return false;
        }

        // TODO: if checkbox in options checked - display tags for whole project, if not checked - just for current file. For now it just for current file. 
        Vector e = new Vector();
        Vector v = currentTags.ctagsBuffer.getTagsByFile(jEdit.getActiveView()
                                                                   .getBuffer()
                                                                   .getPath());

        if (v.size() < 1) {
            return false;
        }

        String val = new String();

        for (int i = 0; i < v.size(); i++) {
            CtagsEntry en = (CtagsEntry) v.get(i);
            val = en.getTagName() + " (" + en.getExCmd().trim() + ")";
            en.setToStringValue(val);
            e.add(en);
        }

        Object[] a = new String[e.size()];
        a = e.toArray();
        entries = new CtagsEntry[a.length];

        for (int i = 0; i < a.length; i++) {
            entries[i] = (CtagsEntry) a[i];
        }

        Arrays.sort(entries, new AlphabeticComparator());
        e.clear();

        return true;
    }

    public void showList() {
        if (parse()) {
            jm = new CtagsJumpMenu(jEdit.getActiveView(), entries,
                    new TagsListModel(), true, "Tag to jump:", 35,
                    Jump.getListLocation());
        }
    }

    public class CtagsJumpMenu extends JumpList {
        public CtagsJumpMenu(View parent, Object[] list, ListModel model,
            boolean incr_search, String title, int list_width, Point location) {
            super(parent, list, model, incr_search, title, list_width, location);
        }

        // TODO: Check property SHOW_STATUSBAR_MESSAGES before proceed updateStatusBar()
        public void updateStatusBar(Object o) {
            JList l = (JList) o;
            CtagsEntry tag = (CtagsEntry) l.getModel().getElementAt(l.getSelectedIndex());
            view.getStatus().setMessageAndClear(prepareStatusMsg(tag));
        }

        private String prepareStatusMsg(CtagsEntry en) {
            StringBuffer ret = new StringBuffer();
            String ext_fields = en.getExtensionFields();

            if (ext_fields.length() > 3) {
                ext_fields = ext_fields.substring(3);

                //Ugly workaround... :((
                int f_pos = ext_fields.lastIndexOf("\t");

                if (f_pos != -1) {
                    ext_fields = ext_fields.substring(0,
                            ext_fields.lastIndexOf("\t") + 1);
                }
            } else {
                return null;
            }

            return ext_fields;
        }

        public void processAction(Object o) {
            JList l = (JList) o;

            //SearchAndReplace search = new SearchAndReplace();
            view = jEdit.getActiveView();

            CtagsEntry en = (CtagsEntry) l.getModel().getElementAt(l.getSelectedIndex());
            String tag = en.getExCmd();

            Log.log(Log.DEBUG, this, "try to find-" + tag);
            SearchAndReplace.setSearchString(tag);

            SearchAndReplace.setIgnoreCase(false);
            SearchAndReplace.setRegexp(false);
            SearchAndReplace.setReverseSearch(false);
            SearchAndReplace.setBeanShellReplace(false);
            SearchAndReplace.setAutoWrapAround(true);

            try {
                if (SearchAndReplace.find(view, view.getBuffer(), 0)) {
                    JumpPlugin.getActiveProjectBuffer().history.add(en);
                    JumpPlugin.getActiveProjectBuffer().historyModel.addItem(en.getTagName());
                }
            } catch (Exception e) {
                Log.log(Log.DEBUG, this, "failed to find - " + tag);
            }
        }

        public void processInsertAction(Object o) {
            view = jEdit.getActiveView();

            JList l = (JList) o;
            CtagsEntry en = (CtagsEntry) l.getModel().getElementAt(l.getSelectedIndex());
            String tag = en.getTagName();
            view.getTextArea().setSelectedText(tag);
        }

        public void processActionInNewView(Object o) {
            CtagsJumpMenu.this.processAction(o);
        }
    }

    class TagsListModel extends AbstractListModel {
        public int getSize() {
            return entries.length;
        }

        public Object getElementAt(int index) {
            return entries[index];
        }
    }

    class AlphabeticComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            CtagsEntry s1 = (CtagsEntry) o1;
            CtagsEntry s2 = (CtagsEntry) o2;

            return s1.getTagName().toLowerCase().compareTo(s2.getTagName()
                                                             .toLowerCase());
        }
    }
}
