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

    public String getEndTag() {

        return endTag;
    }

    public int getIcon() {

        return icon;
    }

    public int getLevel() {

        return level;
    }

    public String getReplace() {

        return replace;
    }

    public String getTag() {

        return tag;
    }

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
