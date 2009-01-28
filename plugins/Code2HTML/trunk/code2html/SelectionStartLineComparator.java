/*
 * SelectionStartLineComparator.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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

package code2html ;

import java.util.Comparator ;
import org.gjt.sp.jedit.textarea.Selection ;

public class SelectionStartLineComparator implements Comparator    {
    public int compare(Object obj1, Object obj2) {
        Selection s1 = (Selection) obj1;
        Selection s2 = (Selection) obj2;

        int diff = s1.getStartLine() - s2.getStartLine();

        if (diff == 0) {
            return 0;
        } else if (diff > 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
