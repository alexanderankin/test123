/*
 *  SortedImportList.java - Import list which sorts the import items.  
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
package jimporter.sorting;

import java.util.ArrayList;
import java.util.Collections;
import jimporter.importer.JavaImportList;
import jimporter.sorting.CaseInsensitiveComparator;

/**
 * An <code>JavaImportList</code> that is sorted according the options that the user
 * has set in the options dialog for JImporter.
 */
public class SortedImportList extends JavaImportList {
    /**
     * Gets a list of sorted imports.
     */
    public ArrayList getImportList() {
        Collections.sort(super.getImportList(), new ImportGroupingComparator());        
        return super.getImportList();
    }
}
