/*
 *  AllOtherImportsItem.java  
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.grouping;

import org.gjt.sp.jedit.jEdit;

/**
 * This class describes the import grouping that is the default.  If an import
 * statement isn't represented by other import grouping, they fall into this one
 * by default.
 *
 * @author Matthew Flower
 */
public class AllOtherImportsItem implements ImportGroupItem {
    public String toString() {
        return "* (All other Imports)";
    }

    /**
     * Store the position of this item in the properties file.
     *
     * @param itemNumber a <code>int</code> value representing this items position
     * in the list of import items to be stored.
     */
    public void store(int itemNumber) {
        jEdit.setProperty(ImportGroupOption.IMPORT_GROUP_VALUE_PREFIX + ".list." + itemNumber + ".type", "allotherimports");
    }
}


