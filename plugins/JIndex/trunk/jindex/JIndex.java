/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * JIndex.java
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

package jindex;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.text.Collator;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.WorkThread;
import com.microstar.xml.HandlerBase;
import com.microstar.xml.XmlException;
import com.microstar.xml.XmlParser;


public class JIndex {

    public static final String VERSION = "1.0";


    /**
     * Create an empty index. The index should be loaded with
     * <code>readXML()</code> afterwards.
     */
    public JIndex() { }


    /**
     * Create a new index by parsing the libraries given by the
     * <code>LibEntry[]</code> array. The index should be saved with
     * <code>writeXML()</code> afterwards.
     */
    public JIndex(LibEntry[] libs) {
        // count the number of files in the libraries (this is an
        // estimate for the number of classes to process).
        int numFiles = 0;
        for (int i = 0; i < libs.length; i++) {
            ZipFile f = libs[i].getLibFile();
            numFiles += JIndexUtilities.zipFileSize(f);
        }

        Hashtable allNames = new Hashtable(numFiles * 10);
        setNumIterations(numFiles + 3);

        // parse all libraries:
        for (int i = 0; i < libs.length; i++)
            addLib(allNames, i, libs[i]);

        // fill and sort arrays 'keys' and 'entries'
        setStatusText("Sorting...");
        int size = allNames.size();
        keys = new String[size];
        entries = new IndexEntry[size];
        Enumeration e = allNames.keys();
        for (int i = 0; e.hasMoreElements(); ++i)
            keys[i] = (String) e.nextElement();
        advance();
        MiscUtilities.quicksort(keys, ignoreCaseComparator);
        advance();
        for (int i = 0; i < size; ++i)
            entries[i] = (IndexEntry) allNames.get(keys[i]);
        advance();

        // clear the hashtable, it is no longer needed
        allNames.clear();
        allNames = null;
    }


    /**
     * get the number of keywords in the index.
     * @return the number of keywords, or 0, if the index has not yet been fetched.
     */
    public int getNumKeywords() {
        return keys != null ? keys.length : 0;
    }


    /**
     * get the keyword at the specified position in the keyword list.
     * @return the keyword, or null, if the keyword list is empty.
     * @throws java.lang.ArrayIndexOutOfBoundsException if
     *         <code>pos &lt; 0</code> or
     *         <code>pos &gt;= getNumKeywords()</code>
     */
    public String getKeywordAt(int pos) {
        return keys != null ? keys[pos] : null;
    }


    /**
     * convert the entries in the index at a specific position to an
     * array of type IndexEntry.
     * @param the position
     * @return the IndexEntries at the specified position, or null, if i &lt; 0.
     */
    public IndexEntry[] getEntriesAt(int i) {
        if (i < 0) {
            return null;
        }

        int count = 1;
        for (IndexEntry e = entries[i]; e.next != null; e = e.next)
            count++;

        IndexEntry[] arr = new IndexEntry[count];
        IndexEntry e = entries[i];
        count = 0;

        do {
            arr[count] = e;
            e = e.next;
            count++;
        } while (e != null);

        MiscUtilities.quicksort(arr, indexEntryComparator);

        return arr;
    }


    /**
     * search for a keyword in the keys array, given a search string, and
     * return a detailled SearchResult. This search is fast, because the
     * array is sorted, and binary search can be used.<p>
     *
     * The search string may contain a simple keyword, like a method or
     * class name, and it may be specified with or without full package
     * and parameter info. For example, the following search strings are
     * valid:<p>
     *
     * "getPreferredSize"<br>
     * "getPreferredSize(int)"<br>
     * "setPreferredSize(int, int)"<br>
     * "JLabel.getPreferredSize"<br>
     * "JLabel.getPreferredSize(int)"<br>
     * "javax.swing.JLabel.getPreferredSize"<br>
     * "javax.swing.JLabel.getPreferredSize(int)"<br>
     * "javax.swing.JTable(java.lang.Object[][], java.lang.Object[])"<p>
     *
     * Note that parameters, if any, have to be separated by a comma and
     * a space! Parameters, unlike methods or classes, have to be fully
     * specified, like in the example above.
     *
     * @param searchstring the search string
     * @return a SearchResult, that contains an array if IndexEntries and
     *         the name and position of the keyword, that was searched for.
     * @see jindex.JIndex#SearchResult
     */
    public SearchResult search(String searchstring, boolean exact) {
        // extract a keyword from the search string:
        String keyword = null;
        int bracketpos = searchstring.indexOf('(');
        if (bracketpos != -1) {
            keyword = searchstring.substring(0, bracketpos);
        } else {
            keyword = searchstring;
        }
        int lastdotpos = keyword.lastIndexOf('.');
        if (lastdotpos != -1) {
            keyword = keyword.substring(lastdotpos + 1);
        }
        Log.log(Log.DEBUG, this, "keyword = " + keyword);

        // construct the SearchResult:
        SearchResult res = new SearchResult();
        res.searchstring = searchstring;
        res.keyword = keyword;
        res.keywordpos = binarySearch(keyword);

        if (res.keywordpos < 0 && exact) {
            res.entries = null;
        } else {
            res.entries = getEntriesAt(Math.abs(res.keywordpos));
        }

        return res;
    }


    void writeXML(Writer w) throws IOException {
        // write header
        w.write("<?xml version=\"1.0\"?>\n");
        w.write("<!DOCTYPE JINDEX SYSTEM \"jindex.dtd\">\n\n");
        w.write("<JINDEX version=\"");
        w.write(VERSION);
        w.write("\" size=\"");

        // write out number of keys
        w.write(Integer.toString(keys.length));
        w.write("\">\n");

        // write out keys and entries:
        for (int i = 0; i < keys.length; i++) {
            w.write("<KEY name=\"");
            w.write(keys[i]);
            w.write("\" entries=\"");
            IndexEntry[] arr = getEntriesAt(i);
            w.write(Integer.toString(arr.length));
            w.write("\">\n");
            for (int j = 0; j < arr.length; j++) {
                w.write("  ");
                arr[j].writeXML(w);
                w.write('\n');
            }
            w.write("</KEY>\n");
        }

        w.write("</JINDEX>\n");
    }


    void readXML(Reader r) throws Exception {
        XmlParser parser = new XmlParser();
        JIndexXmlHandler handler = new JIndexXmlHandler(parser);
        parser.setHandler(handler);
        parser.parse(null, null, r);
        parser = null;
        handler = null;
    }


    private void addLib(Hashtable allNames, int currentLibNum, LibEntry libEntry) {
        ZipFile f = libEntry.getLibFile();
        Enumeration e = f.entries();
        ZIPClassLoader classloader = new ZIPClassLoader(f);
        setStatusText("Processing " + libEntry.lib + " ...");

        // traverse zip file and build up index tree
        for (int i = 0; e.hasMoreElements(); i++) {
            advance();
            ZipEntry ze = (ZipEntry) e.nextElement();
            if (!ze.isDirectory()) {
                String name = ze.getName();
                if (name.endsWith(".class") && name.indexOf('$') == -1) {
                    // it's a class file, but not an inner class
                    String classname = MiscUtilities.fileToClass(name);
                    try {
                        // load class, but don't resolve (ie. link)
                        Class c = classloader.loadClass(classname, false);
                        processClass(allNames, currentLibNum, c, false, libEntry.visibility);
                    }
                    catch (Throwable t) {
                        Log.log(Log.WARNING, this, t.toString() + ": " + name + " (" + f.getName() + ")");
                    }
                }
            }
        }
    }


    private void processClass(
            Hashtable allNames,
            int currentLibNum,
            Class c,
            boolean isInnerClass,
            int visibility)
    {
        int mods = c.getModifiers();
        if (fitsProperties(visibility, mods)) {
            String cname = c.getName();
            add(allNames,
                c.isInterface() ? IndexEntry.INTERFACE : IndexEntry.CLAZZ,
                cname, null, currentLibNum);

            // Constructors
            Constructor[] constr = c.getDeclaredConstructors();
            for (int i = 0; i < constr.length; i++) {
                int cmods = constr[i].getModifiers();
                if (fitsProperties(visibility, cmods)) {
                    add(allNames,
                        IndexEntry.CONSTRUCTOR,
                        constr[i].getName(),
                        getParamList(constr[i].getParameterTypes()),
                        currentLibNum);
                }
            }

            // Methods
            Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                int mmods = methods[i].getModifiers();
                if (fitsProperties(visibility, mmods)) {
                    add(allNames,
                        IndexEntry.METHOD,
                        cname + "." + methods[i].getName(),
                        getParamList(methods[i].getParameterTypes()),
                        currentLibNum);
                }
            }

            // Fields
            Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                int fmods = fields[i].getModifiers();
                if (fitsProperties(visibility, fmods)) {
                    add(allNames,
                        IndexEntry.FIELD,
                        cname + "." + fields[i].getName(),
                        null,
                        currentLibNum);
                }
            }

            // Inner classes
            Class[] classes = c.getDeclaredClasses();
            for (int i = 0; i < classes.length; i++)
                processClass(allNames, currentLibNum, classes[i], true, visibility);
        }
    }


    private void add(
            Hashtable allNames,
            int type,
            String qualifiedName,
            String paramList,
            int currentLibNum)
    {
        int dotpos = qualifiedName.lastIndexOf('.');
        int dollarpos = qualifiedName.lastIndexOf('$');
        int shortBegin = Math.max(dotpos, dollarpos) + 1;
        String shortName = qualifiedName.substring(shortBegin).toLowerCase();
        String fullName = qualifiedName + (paramList == null ? "" : paramList);
        IndexEntry eOld = (IndexEntry) allNames.get(shortName);
        IndexEntry eNew = new IndexEntry(type, fullName, currentLibNum);

        if (eOld == null) {
            allNames.put(shortName, eNew);
        } else {
            eNew.next = eOld.next;
            eOld.next = eNew;
        }
    }


    /** test if the modifiers fits the properties set by the user */
    private static final boolean fitsProperties(int visibility, int mods) {
        if (visibility == LibEntry.PRIVATE)
            return true; // user wants all
        if (visibility == LibEntry.PACKAGE && !Modifier.isPrivate(mods))
            return true; // user wants package protected (no private)
        if (visibility == LibEntry.PROTECTED &&
            (Modifier.isProtected(mods) || Modifier.isPublic(mods)))
            return true; // user wants protected and public
        if (visibility == LibEntry.PUBLIC && Modifier.isPublic(mods))
            return true; // user wants only public
        return false;
    }


    private static final String getParamList(Class[] params) {
        StringBuffer plist = new StringBuffer("(");
        for (int i = 0; i < params.length; i++) {
            if (i != 0)
                plist.append(", ");
            plist.append(getTypeName(params[i]));
        }
        plist.append(")");
        return plist.toString();
    }


    private static final String getTypeName(Class c) {
        String cod = c.getName();
        if (c.isArray()) {
            String result;
            int arraycnt = 0;
            while (cod.charAt(arraycnt) == '[')
                arraycnt++;
            switch (cod.charAt(arraycnt)) {
                case 'B': result = "byte";    break;
                case 'C': result = "char";    break;
                case 'D': result = "double";  break;
                case 'F': result = "float";   break;
                case 'I': result = "int";     break;
                case 'J': result = "long";    break;
                case 'S': result = "short";   break;
                case 'Z': result = "boolean"; break;
                default:
                case 'L': result = cod.substring(arraycnt + 1, cod.length() - 1); break;
            }
            for (int i=0; i < arraycnt; i++)
                result += "[]";
            return result;
        } else
            return cod;
    }


    /** binary search for the keyword */
    private int binarySearch(String keyword) {
        int beg = 0;
        for (int end = keys.length - 1; beg <= end; ) {
            int mid = (beg + end) / 2;
            int res = ignoreCaseComparator.compare(keys[mid], keyword);
            if (res < 0)
                beg = mid + 1;
            else if (res > 0)
                end = mid - 1;
            else
                return mid;
        }
        // not found
        return -beg; // negative insert position
    }


    private void advance() {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread) {
            WorkThread w = (WorkThread) thread;
            int val = w.getProgressValue();
            w.setProgressValue(val + 1);
        }
   }


    private void setNumIterations(int num) {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread)
            ((WorkThread)thread).setProgressMaximum(num);
   }


   private void setStatusText(String statusText) {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread)
            ((WorkThread)thread).setStatus(statusText);
   }



    // private members
    private String[] keys = null;
    private IndexEntry[] entries = null;
    private static Collator ignoreCaseCollator;
    private static StringIgnCaseCompare ignoreCaseComparator;
    private static IndexEntryCompare indexEntryComparator;


    static {
        ignoreCaseCollator = Collator.getInstance(Locale.US);
        ignoreCaseCollator.setStrength(Collator.SECONDARY);
        ignoreCaseComparator = new StringIgnCaseCompare();
        indexEntryComparator = new IndexEntryCompare();
    }


    /**
     * this class capsules the information for the result of an invocation
     * of <code>search(String searchstring)</code>.
     */
    public static class SearchResult {

        /** the search string, that was given in <code>search</code>*/
        public String searchstring;

        /**
         * the keyword, that was used during search. This is obtained from
         * <code>searchstring</code> by stripping away package and (in case
         * of methods) class names and the parameter list, so it basically
         * contains only the bare name.
         */
        public String keyword;

        /**
         * the position of the keyword in the keys list. If the keyword
         * is not in the keys list, then it returns the negative value of
         * the index, where the keyword would be inserted.
         */
        public int keywordpos;

        /**
         * the IndexEntries matching the keyword. If nothing was found on
         * the keyword, entries is null.
         */
        public IndexEntry[] entries;

        /** create an empty search result. All members are null or zero. */
        public SearchResult() { }
    }


    private class JIndexXmlHandler extends HandlerBase {

        JIndexXmlHandler(XmlParser parser) {
            this.parser = parser;
        }


        public Object resolveEntity(String publicId, String systemId) throws IOException {
            Log.log(Log.DEBUG,this,"PublicID: "+publicId+", SystemID: "+systemId);
            if (systemId.equals("jindex.dtd"))
                return new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("jindex.dtd")));
            return null;
        }


        public void attribute(String aname, String value, boolean isSpecified) throws NumberFormatException, XmlException {
            currentAttribute = aname;

            if ("version".equals(currentAttribute)) {
                // check JIndex version
                if (!(VERSION.equals(value)))
                    throw new XmlException(
                        "wrong version, expected " + VERSION + ", got " + value,
                        "JINDEX",
                        parser.getLineNumber(),
                        parser.getColumnNumber()
                    );
            }
            else if ("size".equals(currentAttribute)) {
                // initialize arrays
                int indexSize = Integer.parseInt(value);
                keys = new String[indexSize];
                entries = new IndexEntry[indexSize];
                currentIndex = 0;
            }
            else if ("entries".equals(currentAttribute)) {
                // ignored - not needed (yet!)
            }
            else if ("name".equals(currentAttribute))
                currentKeyName = value;
            else if ("text".equals(currentAttribute))
                entryText = value;
            else if ("type".equals(currentAttribute))
                entryType = Integer.parseInt(value);
            else if ("libNum".equals(currentAttribute))
                entryLibNum = Integer.parseInt(value);
            else
                throw new XmlException(
                    "unknown attribute: " + currentAttribute,
                    "JINDEX",
                    parser.getLineNumber(),
                    parser.getColumnNumber()
                );
        }


        public void doctypeDecl(String name, String publicId, String systemId) throws Exception {
            if (name.equalsIgnoreCase("JINDEX"))
                return;
            Log.log(Log.ERROR, this, "DOCTYPE must be JINDEX");
        }


        public void endElement(String name) {
            if (name == null)
                return;
            if ("KEY".equals(name)) {
                keys[currentIndex++] = currentKeyName;
                lastEntry = null;
            }
            else if ("ENTRY".equals(name)) {
                IndexEntry newEntry = new IndexEntry(entryType, entryText, entryLibNum);
                if (lastEntry == null)
                    entries[currentIndex] = newEntry;
                else
                    lastEntry.next = newEntry;
                lastEntry = newEntry;
            }
        }


        private XmlParser parser;
        private String currentAttribute;
        private String currentKeyName;
        private String entryText;
        private int entryType;
        private int entryLibNum;
        private int currentIndex;
        private IndexEntry lastEntry;

    }


    private static class StringIgnCaseCompare implements MiscUtilities.Compare {
        public int compare(Object obj1, Object obj2) {
            return ignoreCaseCollator.compare(obj1.toString(), obj2.toString());
        }
    }


    private static class IndexEntryCompare implements MiscUtilities.Compare {
        public int compare(Object obj1, Object obj2) {
            return ignoreCaseCollator.compare(
                ((IndexEntry)obj1).name,
                ((IndexEntry)obj2).name
            );
        }
    }

}
