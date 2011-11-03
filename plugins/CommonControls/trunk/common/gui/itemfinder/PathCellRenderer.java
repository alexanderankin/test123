/*
 * jEdit - Programmer's Text Editor
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

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.gjt.sp.jedit.MiscUtilities;

/**
 * A list cell renderer that renders path.
 * @author Matthieu Casanova
 */
public class PathCellRenderer extends DefaultListCellRenderer
{
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
						      boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected,
							  cellHasFocus);
		String path = String.valueOf(value);
		String fileName = MiscUtilities.getFileName(path);
		setText("<html><b>"+fileName+"</b> <gray>("+path+")</gray></html>");
		return this;
	}
}
