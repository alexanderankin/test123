/*
 * Bookmarks.java - Model for Bookmarks
 * Copyright (C) 1999 2000 Dirk Moebius
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package infoviewer;

import java.util.Vector;
import javax.swing.table.*;
import org.gjt.sp.jedit.jEdit;


public class Bookmarks extends AbstractTableModel {


	private static final long serialVersionUID = 6142063286592461932L;
	private Vector<TitledURLEntry> entries = new Vector<TitledURLEntry>();
    
    public Bookmarks() {
        for (int i=0; ; i++) {
            String title = jEdit.getProperty("infoviewer.bookmarks.title."+i);
            if (title == null) break;
            String url = jEdit.getProperty("infoviewer.bookmarks.url."+i);
            add(new TitledURLEntry(title, url));
        }
    }


    public void add(TitledURLEntry e) {
        entries.addElement(e);
        fireTableRowsInserted(entries.size()-1, entries.size()-1);
    }
        
    
    public int getSize() {
        return entries.size();
    }
    
    
    public String getTitle(int index) {
        TitledURLEntry e = getEntry(index);
        return e == null ? null : e.getTitle();
    }
    
    
    public String getURL(int index) {
        TitledURLEntry e = getEntry(index);
        return e == null ? null : e.getURL();
    }

    
    public TitledURLEntry getEntry(int index) {
        if (index < 0 || index > entries.size()) return null;
        TitledURLEntry e = (TitledURLEntry) entries.elementAt(index);
        return e;
    }

    
    public void delete(int row) {
        entries.removeElementAt(row);
        fireTableRowsDeleted(row, row);
    }

    
    public void moveup(int row) {
        if (row == 0) return;
        TitledURLEntry b = getEntry(row);
        entries.removeElementAt(row);
        entries.insertElementAt(b, row-1);
        fireTableRowsUpdated(row-1, row);
    }
    
    
    public void movedown(int row) {
        if (row == entries.size()-1) return;
        TitledURLEntry b = getEntry(row);
        entries.removeElementAt(row);
        entries.insertElementAt(b, row+1);
        fireTableRowsUpdated(row, row+1);
    }


    // begin AbstractTableModel implementation
    
    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return entries.size();
    }

    public Object getValueAt(int row, int col) {
        Object obj = null;
        if (row < entries.size()) {
            switch (col) {
                case 0: obj = getEntry(row).getTitle(); break;
                case 1: obj = getEntry(row).getURL(); break;
            }
        }
        return obj;
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }

    public void setValueAt(Object value, int row, int col) {
        TitledURLEntry e = getEntry(row);
        switch (col) {
            case 0: e.setTitle(value.toString()); break;
            case 1: e.setURL(value.toString()); break;
            default: break;
        }
        fireTableRowsUpdated(row, row);
    }

    public String getColumnName(int index) {
        String ret = "";
        switch (index) {
            case 0: ret = jEdit.getProperty("infoviewer.bdialog.col0"); break;
            case 1: ret = jEdit.getProperty("infoviewer.bdialog.col1"); break;
            default: break;
        }
        return ret;
    }

    // end AbstractTableModel implementation
    
    
    public void save() {
        int i = 0;
        int count = 0;
        while (i < entries.size()) {
            TitledURLEntry b = getEntry(i);
            if (b == null) continue;
            if (b.getTitle() == null || b.getTitle().length() == 0) {
                delete(i);
            } else {
                jEdit.setProperty("infoviewer.bookmarks.title." + count, 
                                  b.getTitle());
                jEdit.setProperty("infoviewer.bookmarks.url." + count,
                                  b.getURL());
                i++;
                count++;
            }
        }
        jEdit.unsetProperty("infoviewer.bookmarks.title." + count);
        jEdit.unsetProperty("infoviewer.bookmarks.url." + count);
    }

}

