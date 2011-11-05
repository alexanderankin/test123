/*
 * ItemFinderWindow.java
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

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
//}}}

/**
 * The ItemFinderWindow is a Window that contains a SearchField and a list of items.
 * When typing in the search field, the item list is updated, and when selecting an item of that list, an action is
 * triggered.
 * To use it, you have to implement {@link ItemFinder}.
 *
 * Then you have two choice : instantiate {@link ItemFinderWindow} and make it visible.
 * or use {@link ItemFinderWindow#showWindow(View, ItemFinder)} that will create the window and show it.
 * @author Matthieu Casanova
 */
public class ItemFinderWindow<E> extends JFrame
{
	private ItemFinderPanel<E> itemFinderPanel;

	//{{{ ItemFinderWindow constructor
	public ItemFinderWindow(ItemFinder<E> itemFinder)
	{
		setUndecorated(true);
		itemFinderPanel = new ItemFinderPanel<E>(this, itemFinder);
		JPanel panel = new JPanel(new BorderLayout());

		String label = itemFinder.getLabel();
		if (label != null)
			panel.add(new JLabel(label), BorderLayout.NORTH);

		panel.add(itemFinderPanel, BorderLayout.CENTER);
		setContentPane(panel);
		pack();
	} //}}}

	//{{{ showWindow() method
	public static void showWindow(View view, ItemFinder itemFinder)
	{
		ItemFinderWindow itemFinderWindow = new ItemFinderWindow(itemFinder);
		itemFinderWindow.setLocationRelativeTo(view);
		itemFinderWindow.setVisible(true);
	} //}}}

	//{{{ handledByList() method
	private static boolean handledByList(KeyEvent e)
	{
		return e.getKeyCode() == KeyEvent.VK_DOWN ||
			e.getKeyCode() == KeyEvent.VK_UP ||
			e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
			e.getKeyCode() == KeyEvent.VK_PAGE_UP;
	} //}}}
}