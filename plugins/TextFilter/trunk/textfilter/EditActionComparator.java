/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package textfilter;

//{{{ Imports
import java.util.Comparator;

import org.gjt.sp.jedit.EditAction;
//}}}

/**
 *	Compares two EditActions based on their labels.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class EditActionComparator implements Comparator {

	//{{{ +compare(Object, Object) : int
	public int compare(Object o1, Object o2) {
		return ((EditAction)o1).getLabel().compareTo(
				((EditAction)o2).getLabel());
	} //}}}

}

