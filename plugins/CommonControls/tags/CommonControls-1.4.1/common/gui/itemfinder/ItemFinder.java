/*
 * ItemFinder.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Matthieu Casanova
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
package common.gui.itemfinder;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public interface ItemFinder<E>
{
	/**
	 * Returns the label for that ItemFinder.
	 * If it is null, there is no label.
	 * Otherwise the label will be displayed on top of the textfield,
	 * if the panel is in an ItemFinderWindow, and on the left otherwise.
	 * @return the label or null
	 */
	String getLabel();

	/**
	 * The list model that is used by the ItemFinderWindow
	 * @return a listmodel
	 */
	ListModel getModel();

	/**
	 * Returns a ListCellRenderer that will render the celles of the dropdown list.
	 * If null, the default one is used
	 * @return the ListCellRenderer
	 */
	ListCellRenderer getListCellRenderer();

	/**
	 * Some chars were typed, update the list
	 * @param s the searched string
	 */
	void updateList(String s);

	/**
	 * A selection has been made.
	 * Do an action
	 * @param item the selected item
	 */
	void selectionMade(E item);
}
