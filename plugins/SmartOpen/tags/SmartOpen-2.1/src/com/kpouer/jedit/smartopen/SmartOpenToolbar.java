/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011-2013 Matthieu Casanova
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

package com.kpouer.jedit.smartopen;

//{{{ Imports
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import common.gui.itemfinder.ItemFinder;
import common.gui.itemfinder.ItemFinderPanel;
import org.gjt.sp.jedit.View;
//}}}

/**
 * @author Matthieu Casanova
 */
public class SmartOpenToolbar extends JToolBar
{
	private final ItemFinderPanel<String> itemFinderPanel;
	private final JTextField extensionTextField;
	private final FileItemFinder itemFinder;

	//{{{ SmartOpenToolbar constructor
	public SmartOpenToolbar(View view, FileIndex itemIndex)
	{
		extensionTextField = new JTextField(6);
		itemFinder = new FileItemFinder(itemIndex, extensionTextField);
		itemFinderPanel = new ItemFinderPanel<>(view, itemFinder);
		Dimension maximumSize = itemFinderPanel.getMaximumSize();
		itemFinderPanel.setMaximumSize(new Dimension(500, maximumSize.height));
		Dimension minimumSize = itemFinderPanel.getMinimumSize();
		itemFinderPanel.setMinimumSize(new Dimension(500, minimumSize.height));
		add(itemFinderPanel);
		add(new JLabel("extension:"));
		add(extensionTextField);
	} //}}}

	//{{{ getItemFinderPanel() method
	public ItemFinderPanel<String> getItemFinderPanel()
	{
		return itemFinderPanel;
	} //}}}

	public void setFileIndex(FileIndex itemFinder)
	{
		this.itemFinder.setFileIndex(itemFinder);
	}
}
