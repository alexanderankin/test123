/*
 *  PopupList.java
 *  Copyright (C) 2002 Calvin Yu
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

package common.gui;

// {{{ Imports
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultInputHandler;
import org.gjt.sp.util.Log;
// }}}
/**
 * A popup control for displaying a arbitrary list of items.
 */
public class PopupList
   implements FocusListener, WindowListener
{

   static final private int DEFAULT_VISIBLE_ROW_COUNT = 5;

   private boolean requestTextAreaFocusOnCancel;

   private JPanel panel;
   private JList list;
   private ListModel model;
   private List listeners;
   private JWindow window;

   /**
    * Create a new <code>PopupList</code>.
    */
   public PopupList()
   {
      this(DEFAULT_VISIBLE_ROW_COUNT);
      requestTextAreaFocusOnCancel = true;
   }

   /**
    * Create a new <code>PopupList</code>.
    */
   public PopupList(int visibleRowCount)
   {
      panel = new JPanel(new BorderLayout(0, 0));
      model = new ListModel();
      list = new JList(model);
      list.addFocusListener(this);
      list.setVisibleRowCount(visibleRowCount);
      list.setCellRenderer(new ListItemListCellRenderer());
      panel.add(new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
      listeners = new LinkedList();

      list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectItem");
      list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "selectItem");
      list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
      list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "nextItem");
      list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "previousItem");
      list.getActionMap().put("selectItem", new SelectItemAction());
      list.getActionMap().put("cancel", new CancelAction());
      list.getActionMap().put("nextItem", new SelectNextItemAction());
      list.getActionMap().put("previousItem", new SelectPreviousItemAction());
   }

   /**
    * Enable key stroke cycling.
	*
	* @deprecated As of CC 0.9.0, this method does nothing.
    */
   public void enableKeyStrokeCycling(String anActionName)
   {
   }

   /**
    * Disable key stroke cycling.
	*
	* @deprecated As of CC 0.9.0, this method does nothing.
    */
   public void disableKeyStrokeCycling()
   {
   }

   /**
    * Set the items to show.
    */
   public void setItems(List theItems)
   {
      model.setItems(theItems);
   }

   /**
    * Returns the selected item.
    */
   public ListItem getSelectedItem()
   {
      return (ListItem) list.getSelectedValue();
   }

   /**
    * Returns the selected actual item.  This is equivalent of calling
    * <code>getSelectedItem().getActualItem()</code>.
    */
   public Object getSelectedActualItem()
   {
      return getSelectedItem().getActualItem();
   }

   /**
    * Sets the selected actual item.
    */
   public void setSelectedActualItem(Object actualItem)
   {
      int itemIndex = model.indexOfActualItem(actualItem);
      list.setSelectedIndex(itemIndex);
   }

   /**
    * Set whether focus should go to the main text area if this popup is
    * cancelled.  The default is <code>false</code>.
    */
   public void setRequestTextAreaFocusOnCancel(boolean b)
   {
      requestTextAreaFocusOnCancel = b;
   }

   /**
    * Show this popup in a window.
    */
   public void show(View view)
   {
      if (window != null) {
         Log.log(Log.WARNING, this, "Popup already shown");
         return;
      }
      window = new JWindow(view);
      window.setContentPane(panel);
      window.pack();
      Point viewLoc = view.getLocation();
      Dimension viewSize = view.getSize();
      Dimension popupSize = window.getSize();
      Point popLoc = new Point(viewLoc.x + ((viewSize.width - popupSize.width) / 2),
                               viewLoc.y + ((viewSize.height - popupSize.height) / 2));
      window.addWindowListener(this);
      window.setLocation(popLoc);
      window.setVisible(true);
      list.requestFocus();
   }

   /**
    * Cancel the popup.
    */
   public void cancel()
   {
      if (window != null) {
         window.dispose();
         if (requestTextAreaFocusOnCancel) {
            View view = (View) window.getOwner();
            view.getEditPane().getTextArea().requestFocus();
         }
         window = null;
      }
   }

   /**
    * Add an <code>java.awt.event.ActionListener</code>.
    */
   public void addActionListener(ActionListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Show this popup.
    *
    * @param view The view requesting the popup.
    * @param items The list of {@link ListItem}s to show.
    * @param selectedActualItem The {@link ListItem#getActualItem()} that is selected.
    * @param listener The <code>java.awt.event.ActionListener</code> to invoke
    * when a selection is made.
    */
   static public PopupList show(View view, List items,
                                Object selectedActualItem,
                                ActionListener listener)
   {
      PopupList popupList = new PopupList();
      popupList.addActionListener(listener);
      popupList.setItems(items);
      popupList.setSelectedActualItem(selectedActualItem);
      popupList.show(view);
      return popupList;
   }

   /**
    * Show this popup.
    *
    * @param view The view requesting the popup.
    * @param items The list of {@link ListItem}s to show.
    * @param listener The <code>java.awt.event.ActionListener</code> to invoke
    * when a selection is made.
    */
   static public PopupList show(View view, List items, ActionListener listener)
   {
      return show(view, items, null, listener);
   }

   // {{{ FocusListener Methods
   public final void focusGained(FocusEvent evt) {}

   public final void focusLost(FocusEvent evt) {
      cancel();
   }
   // }}}

   // {{{ WindowListener Methods
   /**
    * Handle a window closing event.
    */
   public final void windowClosing(WindowEvent evt) {}

   /**
    * Handle a window closed event.
    */
   public final void windowClosed(WindowEvent evt) {}

   /**
    * Handle a window opened event.
    */
   public final void windowOpened(WindowEvent evt) {}

   /**
    * Handle a window activated event.
    */
   public final void windowActivated(WindowEvent evt) {}

   /**
    * Handle a window deactivated event.
    */
   public final void windowDeactivated(WindowEvent evt) {
      cancel();
   }

   /**
    * Handle a window iconified event.
    */
   public final void windowIconified(WindowEvent evt) {}

   /**
    * Handle a window deiconified event.
    */
   public final void windowDeiconified(WindowEvent evt)
   {
      cancel();
   }
   // }}}

   /**
    * Fire an action event.
    */
   private void fireActionPerformed()
   {
      ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
      for (Iterator i = listeners.iterator(); i.hasNext();) {
         ((ActionListener) i.next()).actionPerformed(evt);
      }
   }

   /**
    * A list model implementation.
    */
   private class ListModel extends AbstractListModel
   {
      private List items;

      /**
       * Create a new <code>ListModel</code>.
       */
      public ListModel()
      {
         items = new ArrayList();
      }

      /**
       * Set the items.
       */
      public void setItems(List theItems)
      {
         if (!items.isEmpty()) {
            int lastIdx = items.size() - 1;
            items.clear();
            fireIntervalRemoved(this, 0, lastIdx);
         }
         items = theItems;
         if (!items.isEmpty()) {
            fireIntervalAdded(this, 0, items.size() - 1);
         }
      }

      /**
       * Returns the index of the first {@link ListItem} that contains the given
       * actual item.
       */
      public int indexOfActualItem(Object actualItem)
      {
         int i = 0;
         for (Iterator iter = items.iterator(); iter.hasNext();) {
            ListItem each = (ListItem) iter.next();
            if (actualItem == null && each.getActualItem() == null) {
               return i;
            } else if (actualItem != null && actualItem.equals(each.getActualItem())) {
               return i;
            }
            i++;
         }
         return -1;
      }

      /**
       * Returns the element at the given index.
       */
      public Object getElementAt(int index)
      {
         return items.get(index);
      }

      /**
       * Returns the number of items.
       */
      public int getSize()
      {
         return items.size();
      }
   }

   /**
    * A list cell renderer for {@link ListItem}s.
    */
   private class ListItemListCellRenderer extends DefaultListCellRenderer
   {
      /**
       * Returns the renderer component.
       */
      public Component getListCellRendererComponent(JList list, Object value,
                                                    int index,
                                                    boolean isSelected,
                                                    boolean cellHasFocus)
      {
         ListItem item = (ListItem) value;
         value = item.getLabel();
         Component c = super.getListCellRendererComponent(list, value, index,
                                                          isSelected,
                                                          cellHasFocus);
         setIcon(item.getIcon());
         return c;
      }
   }

   /**
    * An action to cancel the popup.
    */
   private class CancelAction extends AbstractAction
   {
      /**
       * Cancel the popup.
       */
      public void actionPerformed(ActionEvent evt)
      {
         cancel();
      }
   }

   /**
    * An action selecting the current item.
    */
   private class SelectItemAction extends AbstractAction
   {
      /**
       * Select the current item.
       */
      public void actionPerformed(ActionEvent evt)
      {
         window.dispose();
         window = null;
         fireActionPerformed();
      }
   }

   /**
    * Select the previous item
    */
   private class SelectPreviousItemAction extends AbstractAction
   {
      /**
       * Select the previous item.
       */
      public void actionPerformed(ActionEvent evt)
      {
         int idx = list.getSelectedIndex();
         if (idx < 0) {
            idx = 0;
         } else if (idx == 0) {
            idx = model.getSize() - 1;
         } else {
            idx--;
         }
         list.setSelectedIndex(idx);
         list.ensureIndexIsVisible(idx);
      }
   }

   /**
    * Select the next item
    */
   private class SelectNextItemAction extends AbstractAction
   {
      /**
       * Select the next item.
       */
      public void actionPerformed(ActionEvent evt)
      {
         int idx = list.getSelectedIndex();
         if (idx < 0) {
            idx = 0;
         } else if (idx == (model.getSize() - 1)) {
            idx = 0;
         } else {
            idx++;
         }
         list.setSelectedIndex(idx);
         list.ensureIndexIsVisible(idx);
      }
   }

}
