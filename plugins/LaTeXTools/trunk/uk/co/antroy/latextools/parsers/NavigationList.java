/*:folding=indent:
* NavigationList.java - Creates a list of items parsed from a navigation list.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import uk.co.antroy.latextools.options.NavigationOptionPane;

import console.Console;

/**
 * Represents a list of 'tags' (see {@link uk.co.antroy.latextools.parsers.TagPair}) 
 * that match structure elements of (La)TeX source code (chapters, sections...).
 * The list is used to find the elements for display in the SideKick's 
 * Structure browser.
 * 
 * Users may define their own navigation data that will be likely a subset of the 
 * default navigation data/list that match all structure elements to limit 
 * the display to only some elements (e.g. only chapters). 
 * 
 * See the plugin's help.
 * @see uk.co.antroy.latextools.parsers.TagPair
 * @see uk.co.antroy.latextools.parsers.LaTeXParser
 * @see uk.co.antroy.latextools.LaTeXDockable
 */
public class NavigationList
    implements Comparable {

    //~ Instance/static variables .............................................

    private int importance = 0;
    private int lowestLevel;
    private String title;
    private List<TagPair> list = new ArrayList<TagPair>();
    private static SortedSet<NavigationList> navData = new TreeSet<NavigationList>();

    //~ Constructors ..........................................................

    public NavigationList(String s) {
        this.title = s;
    }

    public NavigationList(String s, int imp) {
        this.title = s;
        this.importance = imp;
    }

    //~ Methods ...............................................................

    /**
     * Return the navigation data (filters for the structure browser)
     * in a set with items of the type {@link TagPair}.
     */
    public static SortedSet<NavigationList> getNavigationData() {

        try {

            InputStream istr = NavigationList.class.getResourceAsStream(
                                           "/default.nav");

            if (istr != null) {

                Reader reader = new InputStreamReader(istr);
                loadNavigationFile(reader);
            } else {
                Log.log(Log.DEBUG, NavigationList.class, 
                        "NavigationList not read");
            }
        } catch (Exception e) {
            Log.log(Log.ERROR, NavigationList.class, 
                    "NavigationList: " + e.getMessage());
        }
        
        File navDir = new File(NavigationOptionPane.getUserDir());

        if (navDir.exists()) {

            try {

                File[] files = navDir.listFiles(new FileFilter() {
                    public boolean accept(File f) {

                        return f.toString().endsWith(".nav");
                    }
                });

                for (int i = 0; i < files.length; i++) {
                    loadNavigationFile(new FileReader(files[i]));
                }
            } catch (Exception e) {
                Log.log(Log.ERROR, NavigationList.class, e.toString());
                e.printStackTrace();
            }
        }

        return new TreeSet<NavigationList>(navData);
    }

    public boolean add(TagPair tp) {

        return list.add(tp);
    }

    /** Compare with respect to their importance (user defined override the default). */
    public int compareTo(NavigationList be) {

        return this.importance - be.importance;
    }

    public int compareTo(Object o) {

        return compareTo((NavigationList)o);
    }

    static public void setDefaultGroup(NavigationList nl) {
	    String title = nl.title;
	    setDefaultGroup(title);
    }
    
    static public void setDefaultGroup(String defaultGroup) {
	    jEdit.setProperty("latextools-navigation-group", defaultGroup);
    }
    
    static public NavigationList getDefaultGroup() {
	    String defaultGroup = jEdit.getProperty("latextools-navigation-group");
	    NavigationList retval = null;
	    for (NavigationList nl: navData) {
		    if (retval == null) retval = nl;
		    if (nl.title.equals(defaultGroup)) return nl; 
	    }
	    return retval;
    }
    
    public boolean equals(Object o) {

        return equals((NavigationList)o);
    }

    public boolean equals(NavigationList n) {

        return this.toString().equals(n.toString());
    }

    public int hashCode() {

        return title.hashCode();
    }

    public Iterator iterator() {

        return list.iterator();
    }

    public int size() {

        return list.size();
    }

    public String toString() {

        return title;
    }

    public void setLowestLevel(int lev) {
        lowestLevel = lev;
    }

    public int getLowestLevel() {

        return lowestLevel;
    }

    /** The title of the filter (such as All, Sections) to display 
     * in the drop-down select list of LaTeX Tools dockable. */
    public String getTitle() {

        return title;
    }

    /**
     * Parse the file defining the navigation (filters) and 
     * construct its representation.
     * @param reader Reader of the navigation definition file.
     */
    private static void loadNavigationFile(Reader reader) {

        try {

            BufferedReader in = new BufferedReader(reader);
            String nextLine = in.readLine().trim();

            while (nextLine != null) {

                int lowestLevel = 0;

                if (nextLine.length() > 1 && nextLine.startsWith("@")) {

                    if (nextLine.endsWith("0")) {

                        NavigationList nl = new NavigationList(nextLine.substring(
                                                                           1, 
                                                                           nextLine.length() - 1));
                        navData.remove(nl);
                        nextLine = in.readLine();
                    } else {

                        int importance = Integer.parseInt(nextLine.substring(
                                                                      nextLine.length() - 1));
                        NavigationList nl = new NavigationList(nextLine.substring(
                                                                           1, 
                                                                           nextLine.length() - 1), 
                                                               importance);
                        navData.remove(nl);
                        navData.add(nl);
                        nextLine = in.readLine().trim();

                        while (nextLine.length() > 3 && 
                               nextLine.indexOf(":") > 0) {

                            StringTokenizer st = new StringTokenizer(nextLine, 
                                                                     ":");

                            if (st.countTokens() != 6) {
                                nextLine = in.readLine().trim();

                                continue;
                            }

                            int lev = Integer.parseInt(st.nextToken());
                            lowestLevel = Math.max(lowestLevel, lev);
                            nl.add(new TagPair(st.nextToken(), st.nextToken(), 
                                               Integer.parseInt(st.nextToken()), 
                                               st.nextToken(), lev, 
                                               Integer.parseInt(st.nextToken())));
                            nextLine = in.readLine().trim();
                        }

                        nl.setLowestLevel(lowestLevel);
                        navData.add(nl);
                    }
                } else {
                    nextLine = in.readLine();
                }
            }
        } catch (Exception e) {
            Log.log(Log.ERROR, NavigationList.class, 
                    "NavigationList error" + e);
            e.printStackTrace();
        }
    }
}
