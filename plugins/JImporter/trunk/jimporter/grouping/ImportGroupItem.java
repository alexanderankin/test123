/*
 *  ImportGroupItem.java -
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

import jimporter.importer.ImportItem;

/**
 * This interface is implemented by classes that represent a single type of
 * classpath group.  A classpath group is something that a single line of text
 * in the import block belong to.  Examples include imported classes, imported
 * packages, and whitespace.
 *
 * @author Matthew Flower
 */
public interface ImportGroupItem {
	/**
	 * When this method is called, the import group item needs to persist itself
	 * permanently to long-term store, such as the jEdit properties file.
	 *
	 * @param itemNumber a <code>int</code> value which is the sequential order
	 * of the current item in the list of all import groups.
	 */
	public void store(int itemNumber);
}

