/*
 *  CaseInsensitiveComparitor.java - Compares Import Items without regard to 
 *  character case.   
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

import jimporter.ImportItem;
import jimporter.sorting.CaseInsensitiveComparator;

public class CaseInsensitiveComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        if (!((o1 instanceof ImportItem) && (o2 instanceof ImportItem))) {
            throw new ClassCastException("CaseInsensitiveComparator is only designed to compare ImportItems.");
        }
        
        String string1 = ((ImportItem)o1).getImportStatement().toLowerCase();
        String string2 = ((ImportItem)o2).getImportStatement().toLowerCase();
        
        return string1.compareTo(string2);
    }
    
    public boolean equals(Object o) {
        return (o instanceof CaseInsensitiveComparator);
    }
}

