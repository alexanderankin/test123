/*
* Outlineable.java
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2003, 2010 Matthieu Casanova
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
package net.sourceforge.phpdt.internal.compiler.parser;

import gatchan.phpparser.project.itemfinder.PHPItem;

/**
 * Here is an interface that object that can be in the outline view must implement.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public interface Outlineable
{
	/**
	 * Returns the parent of the item.
	 *
	 * @return the parent
	 */
	Outlineable getParent();

	/**
	 * Give the name of the item.
	 *
	 * @return the name of the item
	 */
	String getName();

	/**
	 * Returns the item type.
	 * in {@link PHPItem#CLASS},{@link PHPItem#FIELD}, {@link PHPItem#INTERFACE}, {@link PHPItem#METHOD}
	 *
	 * @return the item type
	 */
	int getItemType();

	/**
	 * Add a children.
	 *
	 * @param o children the outlineable
	 * @return true if it was added
	 */
	boolean add(Outlineable o);

	/**
	 * Returns the children at index.
	 *
	 * @param index the index
	 * @return the children at index
	 */
	Outlineable get(int index);

	/**
	 * Returns how many children this item has.
	 *
	 * @return the children count
	 */
	int size();
}
