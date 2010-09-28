/*:folding=indent:
* LaTeXAsset.java - Stores a parsed element of the document.
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

import java.io.File;

import javax.swing.Icon;
import javax.swing.text.Position;

import sidekick.Asset;

import uk.co.antroy.latextools.macros.UtilityMacros;

/* (non javadoc)
 * Representation of a particular structure element of (La)TeX source
 * (chapter, section...) together with its start and end positions, a label
 * and an icon.
 * @see sidekick.Asset
 */
public class LaTeXAsset  extends Asset
    implements Comparable {

    //~ Instance/static variables .............................................

    private int level = 0;
    private String section = "";
    private int iconType = 0;
    private File file = new File("");
    public static final int DEFAULT_ICON = 0;
    public static final int SECTION_ICON = 1;
    public static final int GRAPHIC_ICON = 2;
    public static final int THEOREM_ICON = 3;
    public static final int TABLE_ICON = 4;
    public static final int LIST_ICON = 5;
    public static final int VERBATIM_ICON = 6;
    public static final int LINK_ICON = 7;

    //~ Constructors ..........................................................

    public LaTeXAsset(String name) {
        super(name);
    }

    //~ Methods ...............................................................

    public void setEnd(Position e) {
        end = e;
    }

    public Position getEnd() {

        return end;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {

        return file;
    }

    public Icon getIcon() {

        String filename = null;

        switch (iconType) {

            case DEFAULT_ICON:
                filename = "default.png";

                break;

            case SECTION_ICON:
                filename = "sections.png";

                break;

            case GRAPHIC_ICON:
                filename = "graphics.png";

                break;

            case THEOREM_ICON:
                filename = "theorem.png";

                break;

            case TABLE_ICON:
                filename = "table.png";

                break;

            case LIST_ICON:
                filename = "list.png";

                break;

            case VERBATIM_ICON:
                filename = "verbatim.png";

                break;

            case LINK_ICON:
                filename = "link.png";

                break;

            default:
                filename = null;
        }

        return UtilityMacros.getIcon(filename);
    }

    public void setIconType(int type) {
        iconType = type;
    }

    public int getIconType() {

        return iconType;
    }

    public void setLevel(int lev) {
        level = lev;
    }

    public int getLevel() {

        return level;
    }

    public String getLongString() {

        return name;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSection() {

        return section;
    }

    public String getShortString() {

        return name;
    }

    public void setStart(Position s) {
        start = s;
    }

    public Position getStart() {

        return start;
    }

    /** Create a new representation of a (La)TeX source code structure element. */
    public static LaTeXAsset createAsset(String name, Position start, 
                                         Position end, int icon_type, int lev) {

        LaTeXAsset asset = createAsset(name, start, end, icon_type);
        asset.setLevel(lev);

        return asset;
    }

    /** Create a new representation of a (La)TeX source code structure element. */
    public static LaTeXAsset createAsset(String name, Position start, 
                                         Position end, int icon_type) {

        LaTeXAsset asset = createAsset(name, start, end);
        asset.setIconType(icon_type);

        return asset;
    }

    /** Create a new representation of a (La)TeX source code structure element. */
    public static LaTeXAsset createAsset(String name, Position start, 
                                         Position end) {

        LaTeXAsset la = new LaTeXAsset(name);
        la.start = start;
        la.end = end;

        return la;
    }

    public int compareTo(LaTeXAsset asset) {

        int offset = start.getOffset();
        int assetOffset = asset.start.getOffset();

        if (offset == assetOffset) {

            String comp = offset + getLongString();

            return comp.compareTo(assetOffset + asset.getLongString());
        } else {

            return offset - assetOffset;
        }
    }

    public int compareTo(Object o) {

        return compareTo((LaTeXAsset)o);
    }

    public boolean equals(Object o) {

        if (o instanceof LaTeXAsset) {

            return equals((LaTeXAsset)o);
        } else {

            return false;
        }
    }

    public boolean equals(LaTeXAsset o) {

        boolean out = true;
        out = out && (name.equals(o.name));
        out = out && (start.getOffset() == o.start.getOffset());
        out = out && (end.getOffset() == o.end.getOffset());
        out = out && (file.equals(o.file));

        return out;
    }

    public int hashCode() {

        int out = 13;
        out = out * 37;
        out += name.hashCode();
        out = out * 37;
        out += start.getOffset();
        out = out * 37;
        out += end.getOffset();
        out = out * 37;
        out += file.hashCode();

        return out;
    }

    public String toString() {

        return getShortString();
    }
}
