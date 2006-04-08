/*:folding=indent:
* TagPair.java - Represents a Search command for the parsers.
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

import org.gjt.sp.jedit.jEdit;

/**
 * An element of NavigationData that matches exactly one type of 
 * LaTeX elements such as chapter or command. A set of TagPairs 
 * defines a filter for the SideKick's Structure browser (we 
 * call it Navigation Data) that determines what elements to show.
 * It corresponds to one line in the text file that defines the 
 * navigation data (filters).
 * 
 * @see uk.co.antroy.latextools.parsers.NavigationList
 * See the plugin's help for details.
 */
public class TagPair {

    //~ Instance/static variables .............................................

    int level;
    int line = 0;
    int icon;
    int type = 1;
    String tag;
    String replace = " ";
    String endTag = "";

    //~ Constructors ..........................................................

    TagPair(String tag, String replace, int type, String endTag, int level, 
            int icon) {
        this(tag, replace, level, icon);
        this.endTag = endTag;
        this.type = type;
    }

    TagPair(String tag, String replace, int level, int icon) {
        this(tag, level, icon);
        this.replace = replace;
    }

    TagPair(String tag, int level, int icon) {
        this.tag = tag;
        this.level = level;
        this.icon = icon;
    }

    TagPair(String tag, int lev) {
        this(tag, 0, lev);
    }

    //~ Methods ...............................................................

    public void setEndTag(String et) {
        endTag = et;
    }

    /** How to detect the end of the element? May be empty - '}' is than used. Regular expression. */
    public String getEndTag() {

        return endTag;
    }

    /** The icon to use in the Structure browser for the matched element. */
    public int getIcon() {

        return icon;
    }

    /** Nesting level of the element - 1 for part, 2 for chapter, 3 for section, 4 for subsection... .*/
    public int getLevel() {

        return level;
    }

    /** Label to display in the Structure browser for the matched element.
     * It may refer to the groups from the regular expression for the (start) tag.
     * Example: "Capt\u003A $1" - used for the tag "\\caption\{(.*?)\}:". */
    public String getReplace() {

        return replace;
    }

    /** Regular expression matching the start of the element or the whole element. */
    public String getTag() {

        return tag;
    }

    /** 
     * Where does the element end:<ol> 
     * <li>0 - to the end of the buffer</li> 
     * <li>1 - to the end of the start search string (see getTag)</li>
     * <li>1 - to the end of the end search string</li>
     * </ol>*/
    public int getType() {

        return type;
    }

    public String toString() {

        boolean NUMBERS_OFF = jEdit.getBooleanProperty("tagpair.linenumbers");

        if (line < 0 || NUMBERS_OFF) {

            return tag;
        }

        return line + "  : " + tag;
    }
}
