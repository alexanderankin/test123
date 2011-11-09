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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gjt.sp.jedit.GUIUtilities;

/**
 * The ItemFinderWindow is a Window that contains a SearchField and a list of items.
 * When typing in the search field, the item list is updated, and when selecting an item of that list, an action is
 * triggered.
 * To use it, you have to implement {@link ItemFinder}, then instantiate {@link ItemFinderPanel} and make it visible.
 * @author Matthieu Casanova
 */
public class ItemFinderPanel<E> extends JPanel
{
	/**
	 * This window will contains the scroll with the items.
	 */
	private final JWindow window;
	private final JTextField searchField;
	private final JList itemList;

	public final RequestFocusWorker requestFocusWorker;
	private final ItemFinder<E> itemFinder;

	public ItemFinderPanel(Window owner, ItemFinder<E> itemFinder)
	{
		super(new BorderLayout());
		this.itemFinder = itemFinder;
		window = new JWindow(owner);
		searchField = new JTextField(50);

		itemList = new JList(itemFinder.getModel());
		itemList.setBorder(BorderFactory.createEtchedBorder());
		ListCellRenderer listCellRenderer = itemFinder.getListCellRenderer();
		if (listCellRenderer != null)
			itemList.setCellRenderer(listCellRenderer);
		itemList.addKeyListener(new ItemListKeyAdapter(searchField));
		itemList.addMouseListener(new MyMouseAdapter());

		searchField.addKeyListener(new SearchFieldKeyAdapter());
		searchField.getDocument().addDocumentListener(new MyDocumentListener());
		JScrollPane scroll = new JScrollPane(itemList);
		window.setContentPane(scroll);

		String label = itemFinder.getLabel();
		if (label != null)
			add(new JLabel(label), BorderLayout.NORTH);

		add(searchField, BorderLayout.CENTER);
		window.pack();
		requestFocusWorker = new RequestFocusWorker(searchField);
	}

	public void dispose()
	{
		Window owner = window.getOwner();
		window.dispose();
		if (owner instanceof ItemFinderWindow)
			owner.dispose();
	}

	private static boolean handledByList(KeyEvent e)
	{
		return e.getKeyCode() == KeyEvent.VK_DOWN ||
			e.getKeyCode() == KeyEvent.VK_UP ||
			e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
			e.getKeyCode() == KeyEvent.VK_PAGE_UP;
	}

	private void select()
	{
		E value = (E) itemList.getSelectedValue();
		if (value != null)
		{
			searchField.setText("");
			itemFinder.selectionMade(value);
			dispose();
		}
	}

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
				searchField.setText("");
				dispose();
			}
			else if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				select();
			}
		}
	}

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
	}

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
	}

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
	}

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
				window.setVisible(false);
			}
			else
			{
				if (!window.isVisible())
				{
					Rectangle bounds = getBounds();
					window.pack();
					Point locationOnScreen = getLocationOnScreen();
					window.setLocation(locationOnScreen.x, locationOnScreen.y + bounds.height);
					Rectangle screenBounds = GUIUtilities.getScreenBounds();
					int maxWidth = screenBounds.width - locationOnScreen.x;
					int width = Math.min(window.getWidth(), maxWidth);
					window.setSize(width, window.getHeight());
				}
				window.setVisible(true);
				if (itemList.getSelectedIndex() == -1)
				{
					itemList.setSelectedIndex(0);
				}
			}
			EventQueue.invokeLater(requestFocusWorker);
		}
	}
}