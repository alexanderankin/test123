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

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

//{{{ Imports
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//}}}

/**
 * The ItemFinderWindow is a Window that contains a SearchField and a list of items.
 * When typing in the search field, the item list is updated, and when selecting an item of that list, an action is
 * triggered.
 * To use it, you have to implement {@link ItemFinder}.
 *
 * Then you have two choice : instantiate {@link ItemFinderWindow} and make it visible.
 * or use {@link ItemFinderWindow#showWindow()} that will create the window and show it.
 * @author Matthieu Casanova
 */
public class ItemFinderWindow<E> extends JFrame
{
	/**
	 * This window will contains the scroll with the items.
	 */
	private final JWindow window;
	private final JTextField searchField;
	private final JList itemList;

	public final RequestFocusWorker requestFocusWorker;
	private final ItemFinder<E> itemFinder;

	//{{{ ItemFinderWindow constructor
	public ItemFinderWindow(ItemFinder<E> itemFinder)
	{
		this.itemFinder = itemFinder;
		setUndecorated(true);
		window = new JWindow(this);
		searchField = new JTextField(50);

		itemList = new JList(itemFinder.getModel());
		itemList.setBorder(BorderFactory.createEtchedBorder());
		if (itemFinder.getListCellRenderer() != null)
			itemList.setCellRenderer(itemFinder.getListCellRenderer());
		itemList.addKeyListener(new ItemListKeyAdapter(searchField));
		itemList.addMouseListener(new MyMouseAdapter());

		searchField.addKeyListener(new SearchFieldKeyAdapter());
		searchField.getDocument().addDocumentListener(new MyDocumentListener());
		JScrollPane scroll = new JScrollPane(itemList);
		window.setContentPane(scroll);
		JPanel panel = new JPanel(new BorderLayout());

		String label = itemFinder.getLabel();
		if (label != null)
			panel.add(new JLabel(label), BorderLayout.NORTH);

		panel.add(searchField, BorderLayout.CENTER);
		setContentPane(panel);
		window.pack();
		requestFocusWorker = new RequestFocusWorker(searchField);
		pack();
		if (itemFinder.getWidth() != -1)
			setSize(itemFinder.getWidth(), getHeight());
	} //}}}

	//{{{ showWindow() method
	public static void showWindow(View view, ItemFinder itemFinder)
	{
		ItemFinderWindow itemFinderWindow = new ItemFinderWindow(itemFinder);
		itemFinderWindow.setLocationRelativeTo(view);
		itemFinderWindow.setVisible(true);
	} //}}}

	//{{{ setVisible() method
	@Override
	public void setVisible(boolean b)
	{
		Rectangle bounds = getBounds();
		window.setLocation(bounds.x, bounds.y + bounds.height);
		GUIUtilities.requestFocus(this, searchField);
		window.setVisible(false);
		super.setVisible(b);
	} //}}}

	//{{{ dispose()
	@Override
	public void dispose()
	{
		window.dispose();
		super.dispose();
	} //}}}

	//{{{ handledByList() method
	private static boolean handledByList(KeyEvent e)
	{
		return e.getKeyCode() == KeyEvent.VK_DOWN ||
			e.getKeyCode() == KeyEvent.VK_UP ||
			e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
			e.getKeyCode() == KeyEvent.VK_PAGE_UP;
	} //}}}

	//{{{ select() method
	private void select()
	{
		E value = (E) itemList.getSelectedValue();
		if (value != null)
		{
			itemFinder.selectionMade(value);
			dispose();
		}
	} //}}}

	//{{{ SearchFieldKeyAdapter class
	private class SearchFieldKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			if (handledByList(e))
			{
				itemList.dispatchEvent(e);
			}
			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				dispose();
			}
			else if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				select();
			}
		}
	} //}}}

	//{{{ ItemListKeyAdapter class
	private static class ItemListKeyAdapter extends KeyAdapter
	{
		private final JTextField searchField;

		private ItemListKeyAdapter(JTextField searchField)
		{
			this.searchField = searchField;
		}

		@Override
		public void keyTyped(KeyEvent e)
		{
			searchField.dispatchEvent(e);
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			if (!handledByList(e))
			{
				searchField.dispatchEvent(e);
			}
		}
	} //}}}

	//{{{ MyMouseAdapter class
	private class MyMouseAdapter extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2)
			{
				select();
			}
		}
	} //}}}

	//{{{ RequestFocusWorker class
	protected static class RequestFocusWorker implements Runnable
	{
		private final JTextField searchField;

		private RequestFocusWorker(JTextField searchField)
		{
			this.searchField = searchField;
		}

		public void run()
		{
			searchField.requestFocus();
		}
	} //}}}

	//{{{ MyDocumentListener class
	private class MyDocumentListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent e)
		{
			updateList(searchField.getText());
		}

		public void removeUpdate(DocumentEvent e)
		{
			updateList(searchField.getText());
		}

		public void changedUpdate(DocumentEvent e)
		{
			updateList(searchField.getText());
		}

		private void updateList(String s)
		{
			itemFinder.updateList(s);
			int size = itemList.getModel().getSize();
			if (size == 0)
			{
				itemList.clearSelection();
			}
			else
			{
				window.pack();
				window.setVisible(true);
				if (itemList.getSelectedIndex() == -1)
				{
					itemList.setSelectedIndex(0);
				}
			}
			EventQueue.invokeLater(requestFocusWorker);
		}
	} //}}}

}