/*:folding=indent:
* BibEntry.java - BibTeX Entry
* Copyright (C) 2002 Anthony Roy
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
package uk.co.antroy.latextools.parsers;

import java.util.StringTokenizer;

import org.gjt.sp.jedit.jEdit;


public class BibEntry
    implements Comparable {

    //~ Instance/static variables .............................................

    private String ref;
    private String title;
    private String author;
    private String journal;

    //~ Constructors ..........................................................

    public BibEntry() {
        this("", "", "");
    }

    public BibEntry(String r, String t, String author) {
        setRef(r);
        setTitle(t);
        setAuthor(author);
        setJournal("");
    }

    //~ Methods ...............................................................

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {

        return author;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getJournal() {

        return journal;
    }

    public void setRef(String s) {
        this.ref = s;
    }

    public String getRef() {

        return ref;
    }

    public void setTitle(String s) {

        int bibLength = jEdit.getIntegerProperty("bibtex.bibtitle.wordlength", 
                                                 0);
        int bibCount = jEdit.getIntegerProperty("bibtex.bibtitle.wordcount", 0);
        StringTokenizer st = new StringTokenizer(s, " ");
        StringBuffer sb = new StringBuffer("");
        int count = bibCount;
        int length = st.countTokens();

        if (count == 0) {
            count = length;
        }

        while (st.hasMoreTokens() && count > 0) {
            count--;

            String ss = st.nextToken();

            if (bibLength != 0 && ss.length() > bibLength) {
                ss = ss.substring(0, bibLength) + ".";
            }

            sb.append(" " + ss);
        }

        if (bibCount != 0 && length > bibCount) {
            sb.append("...");
        }

        this.title = sb.toString();
    }

    public String getTitle() {

        return title;
    }

    public int compareTo(BibEntry be) {

        return ref.compareTo(be.getRef());
    }

    public int compareTo(Object o) {

        return compareTo((BibEntry)o);
    }

    public String toString() {

        return getRef() + "  : " + getTitle();
    }
}
