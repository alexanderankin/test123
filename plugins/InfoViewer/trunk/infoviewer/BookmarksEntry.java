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


public class BookmarksEntry implements Cloneable {
    String title;
    String url;
    
    public BookmarksEntry(String t, String u) { title = t; url = u; }
    
    public BookmarksEntry getClone() {
        return new BookmarksEntry(this.title, this.url);
    }
    
    public Object clone() { return getClone(); }
}
