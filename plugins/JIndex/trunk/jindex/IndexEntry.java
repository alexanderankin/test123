/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * IndexEntry.java
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

import java.io.IOException;
import java.io.Writer;
import org.gjt.sp.jedit.jEdit;


public class IndexEntry {

    public static final int CLAZZ = 0;
    public static final int INTERFACE = 1;
    public static final int CONSTRUCTOR = 2;
    public static final int METHOD = 3;
    public static final int FIELD = 4;


    public int type = CLAZZ;
    public String name = "";
    public int libEntryNum = -1;
    public IndexEntry next = null;


    public IndexEntry(int type, String name, int libEntryNum) {
        this.type = type;
        this.name = name;
        this.libEntryNum = libEntryNum;
    }


    public String toString() {
        return name;
    }


    public static final String typeToString(int t) {
        switch (t) {
            case CLAZZ: return "Class";
            case INTERFACE: return "Interface";
            case CONSTRUCTOR: return "Constructor";
            case METHOD: return "Method";
            case FIELD: return "Field";
            default:
        }
        return "Unknown type:" + t;
    }


    public String getCompleteURL() {
        String url = jEdit.getProperty("jindex.lib.doc." + libEntryNum);
        int open_bracket_pos, last_dot;
        String before, after, before_dot, after_dot;

        switch (type) {
            case METHOD:
                open_bracket_pos = name.indexOf('(');
                before = name.substring(0, open_bracket_pos);
                last_dot = before.lastIndexOf('.');
                before_dot = before.substring(0, last_dot);
                after_dot = name.substring(last_dot + 1);
                // before_dot: package, class and method name
                // after_dot: parameter list (with brackets)
                url += changeInnerClassSpec(changeToPath(before_dot))
                       + ".html#"
                       + changeInnerClassSpec(after_dot);
                break;

            case FIELD:
                last_dot = name.lastIndexOf('.');
                before = name.substring(0, last_dot);
                after = name.substring(last_dot+1);
                // before: package and class name
                // after: field name
                url += changeInnerClassSpec(changeToPath(before))
                       + ".html#"
                       + after;
                break;

            case CLAZZ: case INTERFACE: default:
                // name: package and class name
                url += changeInnerClassSpec(changeToPath(name))
                       + ".html";
                break;

            case CONSTRUCTOR:
                open_bracket_pos = name.indexOf('(');
                before = name.substring(0, open_bracket_pos);
                after = name.substring(open_bracket_pos);
                last_dot = before.lastIndexOf('.');
                before_dot = before.substring(0, last_dot + 1);
                after_dot = before.substring(last_dot + 1);
                // before_dot: package
                // after_dot: class name (= constructor name)
                // after: parameter list (with brackets)
                url += changeInnerClassSpec(changeToPath(before_dot))
                       + changeInnerClassSpec(after_dot)
                       + ".html#"
                       + changeInnerClassSpec(after_dot)
                       + changeInnerClassSpec(after);
                break;
        }

        return url;
    }


    /**
     * on JavaDoc 1.2 and higher, dots in the package specifications
     * are changed to directory separators (ie. '/' in URLs).
     * If the current libEntry is for old JavaDoc 1.1, then the string
     * is returned unchanged, because package docs are not stored in subdirs
     * on JavaDoc 1.1.
     */
    private final String changeToPath(String s) {
        String oldjdoc = jEdit.getProperty("jindex.lib.oldjdoc." + libEntryNum);
        if ("true".equals(oldjdoc))
            return s;
        // JavaDoc 1.2: change package delimiters (dots '.') to
        // directory separators ('/' in URLs).
        return s.replace('.', '/');
    }


    /**
     * the inner class separator '$' in class names must be changed to '.'
     * for JavaDoc URLs.
     */
    private final String changeInnerClassSpec(String s) {
        return s.replace('$','.');
    }


    /** for debugging purposes only */
    public String toStringDebug() {
        return typeToString(type) + ": " + name + " [" + libEntryNum + "]";
    }


    /**
     * write the entry out to a writer in XML form.
     * The XML entry has the following form: <p>
     * <pre>
     *   <ENTRY type="(type)" libNum="(libEntryNum)" name="(name)"/>
     * </pre>
     */
    void writeXML(Writer w) throws IOException {
        w.write("<ENTRY type=\"");
        w.write(Integer.toString(type));
        w.write("\" libNum=\"");
        w.write(Integer.toString(libEntryNum));
        w.write("\" text=\"");
        w.write(name);
        w.write("\"/>");
    }

}
