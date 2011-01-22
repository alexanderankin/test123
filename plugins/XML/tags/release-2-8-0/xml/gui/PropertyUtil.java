/*
 * PropertyUtil.java - Utility methods for accessing properties
 *
 * Copyright 2002 Greg Merrill
 * 			 2004 Robert McKinnon
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

package xml.gui;

import org.gjt.sp.jedit.jEdit;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for accessing properties.
 * copied from XSLTPlugin: xslt/PropertyUtil.java
 * @author Greg Merrill
 * @author Robert McKinnon
 */
public class PropertyUtil {

	/**
	 * Returns a List of property values whose keys are identical excepct for
	 * a trailing "." and index.
	 *
	 * @param key key of the enumerated property, excluding the trailing "." and
	 *            index.  I.e., if you have properties "file.0=file0.txt" and
	 *            "file.1=file1.txt", calling getEnumeratedProperty("file", properties)
	 *            would return a list containing "file0.txt" and "file1.txt".
	 */
	public static List<String> getEnumeratedProperty(String key) {
		List<String> values = new ArrayList<String>();
		int i = 0;
		String value;
		while ((value = jEdit.getProperty(calculateKey(key, i++))) != null) {
			values.add(value);
		}
		return values;
	}

	/**
	 * Sets a series of property values whose keys are identical excepct for
	 * a trailing "." and index.
	 *
	 * @param key    key of the enumerated property (see
	 *               {@link #getEnumeratedProperty})
	 * @param values values to be assigned to the enumerated property, in order.
	 *               All members of this List must be Strings.
	 */
	public static void setEnumeratedProperty(String key, List<String> values) {
		List currentValues = getEnumeratedProperty(key);
		for (int i = 0; i < currentValues.size(); i++) {
			jEdit.setProperty(calculateKey(key, i), null);
		}
		for (int i = 0; i < values.size(); i++) {
			jEdit.setProperty(calculateKey(key, i), values.get(i));
		}
	}

	/**
	 * @return indexed property key
	 */
	private static String calculateKey(String key, int index) {
		return key + "." + index;
	}

}

