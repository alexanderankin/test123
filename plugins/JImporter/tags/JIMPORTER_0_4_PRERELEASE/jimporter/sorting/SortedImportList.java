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
import jimporter.ImportList;
import jimporter.sorting.CaseInsensitiveComparator;

public class SortedImportList extends ImportList {
    public ArrayList getImportList() {
        SortCaseInsensitiveOption scio = new SortCaseInsensitiveOption();
        if (scio.state()) {
            Collections.sort(super.getImportList(), new CaseInsensitiveComparator());
        } else {
            Collections.sort(super.getImportList());
        }
        return super.getImportList();
    }
}
