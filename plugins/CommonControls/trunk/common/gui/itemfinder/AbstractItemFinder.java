/*
 * AbstractItemFinder.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
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
 * An abstract ItemFinder, it has no label, default cellrenderer and width.
 * @author Matthieu Casanova
 */
public abstract class AbstractItemFinder<E> implements ItemFinder<E>
{
	@Override
	public String getLabel()
	{
		return null;
	}

	@Override
	public int getWidth()
	{
		return -1;
	}

	@Override
	public ListCellRenderer getListCellRenderer()
	{
		return null;
	}
}
