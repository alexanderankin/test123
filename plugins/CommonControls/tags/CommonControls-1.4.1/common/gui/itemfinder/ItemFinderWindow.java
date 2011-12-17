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
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

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
public class ItemFinderWindow<E> extends JDialog
{
	private final ItemFinderPanel<E> itemFinderPanel;
	public final Runnable requestFocusWorker;

	//{{{ ItemFinderWindow constructor
	public ItemFinderWindow(ItemFinder<E> itemFinder)
	{
		setUndecorated(true);
		itemFinderPanel = new ItemFinderPanel<E>(this, itemFinder);
		requestFocusWorker = itemFinderPanel.requestFocusWorker;
		setContentPane(itemFinderPanel);
		pack();
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				dispose();
			}
		});
	} //}}}

	//{{{ getItemFinderPanel() method
	/**
	 * Returns the ItemFinderPanel.
	 * This is a JPanel using BorderLayout. The textfield is in the Center,
	 * the NORTH is a label, you can modify that if you like.
	 * @return the itemFinderPanel
	 */
	public ItemFinderPanel<E> getItemFinderPanel()
	{
		return itemFinderPanel;
	} //}}}

	//{{{ showWindow() methods
	/**
	 * Show the ItemFinderWindow
	 * @param view the parent view
	 * @param itemFinder the itemFinder object
	 * @param <E>
	 */
	public static <E> void showWindow(View view, ItemFinder<E> itemFinder)
	{
		showWindow(view, itemFinder, "");
	}

	/**
	 * Show the ItemFinderWindow
	 * @param view the parent view
	 * @param itemFinder the itemFinder object
	 * @param defaultText the default text
	 * @param <E>
	 * @since CommonControls 1.4.1
	 */
	public static <E> void showWindow(View view, ItemFinder<E> itemFinder, final String defaultText)
	{
		final ItemFinderWindow<E> itemFinderWindow = new ItemFinderWindow<E>(itemFinder);
		itemFinderWindow.setLocationRelativeTo(view);
		itemFinderWindow.setVisible(true);
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				itemFinderWindow.getItemFinderPanel().setText(defaultText);
			}
		});

	} //}}}
}