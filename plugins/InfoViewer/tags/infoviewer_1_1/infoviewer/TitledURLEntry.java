/*
 * BookmarksEntry.java - a bookmark, consisting of title and url
 * Copyright (C) 1999 Slava Pestov
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


/**
 * a TitledURLEntry stores an URL together with a title string. If the URL
 * represents an HTML document, the title (from the &lt;TITLE&gt; tag)
 * will be stored there. The URL is stored as string. No checking for
 * MalformedURLExceptions is done on the URL.
 */
public class TitledURLEntry implements Cloneable {
    
    private String title = null;
    private String url = null;
    private int historyPos = -1;

    /** new TitledURLEntry with title and url */    
    public TitledURLEntry(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }
    
    public String getURL() {
        return url;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
     
    /** 
     * if this TitledURLEntry is part of a history, store its internal
     * position number.
     */
    public void setHistoryPos(int historyPos) {
        this.historyPos = historyPos;
    }

    /** 
     * if this TitledURLEntry is part of a history, return its internal
     * position number.
     * @return the internal position number of the history, or -1 if this
     *         TitledURLEntry is not part of a history.
     */
    public int getHistoryPos() {
        return historyPos;
    }
    
    public TitledURLEntry getClone() {
        TitledURLEntry e = new TitledURLEntry(title, url);
        e.historyPos = this.historyPos;
        return e;
    }

    public Object clone() {
        return getClone();
    }
    
    public String toString() {
        return url;
    }
}
