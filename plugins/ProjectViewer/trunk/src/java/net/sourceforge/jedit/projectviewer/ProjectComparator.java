/*
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

package net.sourceforge.jedit.projectviewer;

//required for jEdit use
import org.gjt.sp.jedit.*;
import java.util.*;


public class ProjectComparator implements MiscUtilities.Compare {
    
    public int compare(Object obj1, Object obj2) {
        
        Project project1 = (Project) obj1;
        Project project2 = (Project) obj2;
        
        return project1.get().compareTo(project2.get());
    }
}


