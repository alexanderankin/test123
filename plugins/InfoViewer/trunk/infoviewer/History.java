/*
 * History.java - Model for an URL History
 * Copyright (C) 1999-2001 Dirk Moebius
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
import org.gjt.sp.jedit.jEdit;


/**
 * this class maintains a list of visitid URLs and remembers the current
 * entry, that is being viewed.
 */
public class History {

    private Vector entries = new Vector();
    private int currentPos = -1;

    public History() { }


    /**
     * add a new entry to the history. The new entry is made the current
     * entry of the history.
     */
    public synchronized void add(TitledURLEntry e) {
        currentPos++;
        if (currentPos >= entries.size()) {
            entries.addElement(e);
        } else {
            entries.setElementAt(e, currentPos);
            // delete the cached URLs after this entry.
            for (int i = entries.size() - 1; i > currentPos; i--) {
                entries.removeElementAt(i);
            }
        }
    }


    /** returns the current URL of the history, as String. */
    public String getCurrent() {
        return currentPos >= 0 ? getEntry(currentPos).getURL() : null;
    }


    /**
     * sets the internal state of the history to the next entry and
     * returns its URL.
     * @return the next URL as String, or null if the end of the history
     *         is reached.
     */
    public synchronized String getNext() {
        return hasNext() ? getEntry(++currentPos).getURL() : null;
    }


    /** return true, if there is a next entry in the history. */
    public boolean hasNext() {
        return currentPos < entries.size() - 1;
    }


    /**
     * sets the internal state of the history to the previous entry and
     * returns its URL.
     * @return the previous URL as String, or null if the beginning of
     *         the history is reached.
     */
    public synchronized String getPrevious() {
        return hasPrevious() ? getEntry(--currentPos).getURL() : null;
    }


    /** return true, if there is a previous entry in the history. */
    public boolean hasPrevious() {
        return currentPos > 0;
    }


    /**
     * returns the internal position number of the current entry in the
     * history. You can use this position number to recall the current
     * entry later. <p>
     * <b>Attention!</b> The position number might become invalid, if
     * you call <code>add(TitledURLEntry)</code> afterwards.
     * <code>getEntry(int)</code> will return null then.
     */
    public int getHistoryPos() {
        return currentPos;
    }


    /**
     * set the current history entry to a certain position, that was
     * retrieved by <code>getHistoryPos</code>.
     */
    public synchronized void setHistoryPos(int newpos) {
        if (newpos < 0) return;
        if (newpos >= entries.size()) return;
        currentPos = newpos;
    }


    private TitledURLEntry getEntry(int index) {
        if (index < 0 || index >= entries.size()) return null;
        return (TitledURLEntry) entries.elementAt(index);
    }


    /**
     * get the last entries from the history, but now more than
     * specified in the property 'infoviewer.max_go_menu'.
     * The entries are such that the current entry is among them.
     */
    public TitledURLEntry[] getGoMenuEntries() {
        int max = getMaxVisibleMenuEntries();
        if (currentPos + max - 1 < entries.size()) {
            // return everything from 'currentPos', but no more than 'max'
            return getEntries(currentPos, currentPos + max - 1);
        } else {
            // return the last 'max' entries
            return getEntries(entries.size() - max, entries.size() - 1);
        }
    }


    private TitledURLEntry[] getEntries(int from, int to) {
        Vector v = new Vector();
        for (int i = from; i <= to; i++) {
            TitledURLEntry e = getEntry(i);
            if (e != null) {
                e.setHistoryPos(i);
                v.addElement(e);
            }
        }
        TitledURLEntry[] entr = new TitledURLEntry[v.size()];
        v.copyInto(entr);
        return entr;
    }


    private int getMaxVisibleMenuEntries() {
        String history = jEdit.getProperty("history");
        int max;
        try {
            max = Integer.parseInt(history);
            if (max < 1) throw new NumberFormatException();
        }
        catch (NumberFormatException e) {
            max = 20;
        }
        return max;
    }

}

