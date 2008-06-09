/*
 * BufferTabs.java - Part of the BufferTabs plugin for jEdit.
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 * Copyright (C) 1999, 2000 Jason Ginchereau
 * Copyright (C) 2000, 2001, 2002, 2003 Andre Kaplan
 * Copyright (C) 2001 Joe Laffey
 * Copyright (C) 2003 Kris Kopicki
 * Copyright (C) 2003 Chris Samuels
 * Copyright (C) 2008 Matthieu Casanova
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


package buffertabs;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.bufferset.BufferSetListener;
import org.gjt.sp.jedit.bufferset.BufferSet;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.util.Log;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * A tabbed pane that contains a text area.  The text area's buffer is
 * changed when a tab is selected, and when the buffer is changed in
 * another way, the tabs get updated.
 *
 * The tabs provide a popup menu that may be accessed by right-clicking
 * on a tab. This popup menu provides a number of functions relating to
 * the buffer.
 *
 * @author Jason Ginchereau
 * @author Andre Kaplan
 * @author Joe Laffey
 * @author Chris Samuels
 * @author Matthieu Casanova
 */
public class BufferTabs extends JTabbedPane implements EBComponent, BufferSetListener
{
	private final EditPane editPane;
	private final JComponent textArea;
	private BufferSet bufferSet;

	private final ChangeHandler changeHandler;
	private final MouseHandler mouseHandler;
	private final MouseMotionHandler mouseMotionHandler;

	private final Set<Buffer> knownBuffers;

	/**
	 * Creates a new set of buffer tabs that is attached to an EditPane,
	 * and contains the EditPane's text area.
	 * @param editPane the editpane where the bufferTabs will be attached
	 */
	public BufferTabs(EditPane editPane)
	{
		this.editPane = editPane;
		textArea = editPane.getTextArea();

		changeHandler = new ChangeHandler();
		changeHandler.setEnabled(true);
		mouseHandler = new MouseHandler();
		mouseMotionHandler = new MouseMotionHandler();
		knownBuffers = Collections.synchronizedSet(new HashSet<Buffer>());
	}


	/**
	 * Initializes tabs and starts listening for events.
	 */
	public synchronized void start()
	{
		propertiesChanged(); //CES
		BufferSet bufferSet = editPane.getBufferSet();
		setBufferSet(bufferSet);

		EditBus.addToBus(this);

		int index = bufferSet.indexOf(editPane.getBuffer());

		updateColorAt(getSelectedIndex());

		if (index >= 0)
		{
			setSelectedIndex(index);
			updateHighlightAt(index);
		}

		addChangeListener(changeHandler);

		// Mouse Listener for popup menu support
		addMouseListener(mouseHandler);

		// Mouse Motion listener for moving cursoMouseMotionHandlerr
		addMouseMotionListener(mouseMotionHandler);
	}


	/**
	 * Stops listening for events.
	 */
	public synchronized void stop()
	{
		EditBus.removeFromBus(this);
		removeChangeListener(changeHandler);

		removeMouseListener(mouseHandler);

		removeMouseMotionListener(mouseMotionHandler);
	}

	/**
	 * Gets the EditPane this tab set is attached to.
	 */
	public EditPane getEditPane()
	{
		return editPane;
	}

	/**
	 * EditBus message handling.
	 */
	public void handleMessage(EBMessage message)
	{
		if (message instanceof BufferUpdate)
		{

			BufferUpdate bu = (BufferUpdate) message;
			Buffer buffer = bu.getBuffer();
			if (bu.getWhat() == BufferUpdate.DIRTY_CHANGED)
			{
				int index = bufferSet.indexOf(buffer);
				updateTitleAt(index);
			}
			else if (bu.getWhat() == BufferUpdate.LOADED)
			{
				int index = bufferSet.indexOf(buffer);
				if (index >= 0  && index < getTabCount())
				{
					updateTitleAt(index);
					updateHighlightAt(index);
				}
			}
			else if (bu.getWhat() == BufferUpdate.SAVED)
			{
				Buffer buff = bu.getBuffer();
				int index = bufferSet.indexOf(buff);
				setToolTipTextAt(index, buff.getPath());
			}
		}
		else if (message instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate) message;
			EditPane editPane = epu.getEditPane();
			if (editPane == this.editPane)
			{
				if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
				{
					try
					{
						Buffer buffer = editPane.getBuffer();
						int index = bufferSet.indexOf(buffer);
						if (!knownBuffers.contains(buffer))
						{
							// we don't know this buffer yet, let's add it now by simulation
							// of a bufferAdded event
							if (index != -1)
							{
								bufferAdded(buffer, index);
							}
						}
						changeHandler.setEnabled(false);
						updateColorAt(getSelectedIndex());
						setSelectedIndex(index);
						updateHighlightAt(index);
					}
					finally
					{
						changeHandler.setEnabled(true);
					}
				}
				else if (epu.getWhat() == EditPaneUpdate.BUFFERSET_CHANGED)
				{
					setBufferSet(editPane.getBufferSet());
				}
			}
		}
	}

	public void bufferRemoved(Buffer buffer, int index)
	{
		try
		{
			changeHandler.setEnabled(false);
			knownBuffers.remove(buffer);
			removeTabAt(index);


			if (getTabCount() > 0 && super.indexOfComponent(textArea) == -1)
			{
				setComponentAt(0, textArea);
				textArea.setVisible(true);
			}

		}
		finally
		{
			changeHandler.setEnabled(true);
		}
	}

	public void bufferMoved(Buffer buffer, int oldIndex, int newIndex)
	{
		try
		{
			changeHandler.setEnabled(false);
			bufferRemoved(buffer, oldIndex);
			bufferAdded(buffer, newIndex);
		}
		finally
		{
			changeHandler.setEnabled(true);
		}
	}

	public void bufferAdded(Buffer buffer, int index)
	{
		if (knownBuffers.contains(buffer))
		{
			// the buffer is already known, it was maybe added by a setBuffer() in the EditPane
			return;
		}
		try
		{
			// workaround: calls to SwingUtilities.updateComponentTreeUI
			getUI().uninstallUI(this);
			changeHandler.setEnabled(false);
			knownBuffers.add(buffer);
			//ColorTabs.instance().setEnabled( false );

			Component component = null;
			if (super.indexOfComponent(textArea) == -1)
			{
				component = textArea;
			}

			insertTab(buffer.getName(), null, component, buffer.getPath(), index);
			updateTitleAt(index);
			//	 int selectedIndex = this.buffers.indexOf(this.editPane.getBuffer());
			//      this.setSelectedIndex(selectedIndex);
			//Log.log(Log.MESSAGE, BufferTabs.class, "selected : 1 " + selectedIndex +" index "+ index  );
			if (component == textArea)
			{
				textArea.setVisible(true);
			}
		}
		finally
		{
			changeHandler.setEnabled(true);
			//ColorTabs.instance().setEnabled( true );
			// workaround: calls to SwingUtilities.updateComponentTreeUI
			getUI().installUI(this);
		}

		if (editPane.getBuffer() == buffer)
		{
			setSelectedIndex(index);
			updateHighlightAt(index);
		}
		else
			updateColorAt(index);
		
		//CES: Force correct color for new buffer tab
		//this.updateColorAt(this.getSelectedIndex());

		/*if (index >= 0)
		{
			//this.updateColorAt( index );
			int prevSelected = bufferSet.indexOf(previousBuffer);

			if (buffer == getEditPane().getBuffer())
			{
				if (prevSelected < getTabCount())
					updateColorAt(prevSelected);
				setSelectedIndex(index);
				updateHighlightAt(index);
			}
			else
			{
				if (prevSelected < getTabCount())
				{
					setSelectedIndex(prevSelected);
					updateHighlightAt(prevSelected);
				}
				updateColorAt(index);
			}
		}     */
	}

	public void bufferCleared()
	{
		changeHandler.setEnabled(false);
		knownBuffers.clear();
		for (int i = getTabCount() - 1; i >= 0; i--)
		{
			removeTabAt(i);
		}
		changeHandler.setEnabled(true);
	}

	public void propertiesChanged()
	{
		if (ColorTabs.instance().isEnabled() != jEdit.getBooleanProperty("buffertabs.color-tabs"))
		{
			ColorTabs.instance().setEnabled(!ColorTabs.instance().isEnabled());

			//Turn off all color features
			if (!ColorTabs.instance().isEnabled())
			{
				for (int i = getTabCount() - 1; i >= 0; i--)
				{
					setBackgroundAt(i, null);
					setForegroundAt(i, null);
				}

				getUI().uninstallUI(this);
				UIManager.getDefaults().put("TabbedPane.selected", null);
				getUI().installUI(this);
			}
		}

		if (ColorTabs.instance().isEnabled())
		{
			ColorTabs.instance().setMuteColors(jEdit.getBooleanProperty("buffertabs.color-mute"));
			ColorTabs.instance().setColorVariation(jEdit.getBooleanProperty("buffertabs.color-variation"));
			ColorTabs.instance().setForegroundColorized(jEdit.getBooleanProperty("buffertabs.color-foreground"));

			if (ColorTabs.instance().isSelectedColorized() != jEdit.getBooleanProperty("buffertabs.color-selected"))
			{
				ColorTabs.instance().setSelectedColorized(!ColorTabs.instance().isSelectedColorized());

				//Turn off all colorhighlight
				if (!ColorTabs.instance().isSelectedColorized())
				{
					try
					{
						getUI().uninstallUI(this);
						UIManager.getDefaults().put("TabbedPane.selected", null);
						getUI().installUI(this);
					}
					catch (Exception e)
					{
						Log.log(Log.ERROR, BufferTabs.class, "propertiesChanged: 3 " + e.toString());
					}
				}


			}
			if (ColorTabs.instance().isSelectedColorized())
			{
				ColorTabs.instance().setSelectedForegroundColorized(jEdit.getBooleanProperty("buffertabs.color-selected-foreground"));
			}

			ColorTabs.instance().propertiesChanged();

			int selectedIndex = getSelectedIndex();
			for (int i = getTabCount() - 1; i >= 0; i--)
			{
				if (selectedIndex == i)
				{
					updateHighlightAt(i);
				}
				else
					updateColorAt(i);
			}
		}
	}

	private void setBufferSet(BufferSet bufferSet)
	{
		if (bufferSet != this.bufferSet)
		{
			changeHandler.setEnabled(false);
			if (this.bufferSet != null)
			{
				knownBuffers.clear();
				this.bufferSet.removeBufferSetListener(this);
				for (int i = getTabCount();i>0;i--)
				{
					removeTabAt(0);
				}
			}
			this.bufferSet = bufferSet;
			changeHandler.setEnabled(true);
			bufferSet.getAllBuffers(this);
			bufferSet.addBufferSetListener(this);
		}
	}


	private class ChangeHandler implements ChangeListener
	{
		private boolean enabled = true;


		public boolean isEnabled()
		{
			return enabled;
		}


		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}


		/**
		 * Sets the EditPane's buffer when a tab is selected.
		 */
		public synchronized void stateChanged(ChangeEvent e)
		{
			int index = getSelectedIndex();
			if (index >= 0 && isEnabled())
			{
				Buffer buffer = bufferSet.getBuffer(index);
				if (buffer != null)
				{
					int selectedIndex = bufferSet.indexOf(editPane.getBuffer());
					if (selectedIndex >= 0)
					{
						updateColorAt(selectedIndex);
					}
					editPane.setBuffer(buffer);
					updateHighlightAt(index); //CES
				}
			}
		}
	}

	/**
	 * Updates the color of the given tab
	 */
	private void updateColorAt(int index)
	{
		if (!ColorTabs.instance().isEnabled())
		{
			return;
		}
		if (index < 0)
		{
			return;
		}

		Buffer buffer = bufferSet.getBuffer(index);
		String name = buffer.getName();

		if (!ColorTabs.instance().isForegroundColorized())
		{
			Color color = ColorTabs.instance().getDefaultColorFor(name);
			setBackgroundAt(index, color);
			setForegroundAt(index, null);
		}
		else
		{
			Color color = ColorTabs.instance().getDefaultColorFor(name);
			setForegroundAt(index, color);
			setBackgroundAt(index, null);
		}

		// this.updateHighlightAt(index);
	}


	/**
	 * Force the Look and Feel to use the given color as its 'selected' color.
	 * TODO: This may cause side-effects with other tab panes.
	 */
	private void updateHighlightAt(int index)
	{
		if (index < 0)
		{
			return;
		}
		if (ColorTabs.instance().isEnabled()
		    && ColorTabs.instance().isSelectedColorized())
		{
			if (index == getSelectedIndex())
			{
				Buffer buffer = bufferSet.getBuffer(index);
				String name = buffer.getName();
				Color color = ColorTabs.instance().getDefaultColorFor(name);
				Color selected;

				if (!ColorTabs.instance().isSelectedForegroundColorized())
				{
					selected = ColorTabs.instance().alterColorHighlight(color);
					setBackgroundAt(index, selected);
					setForegroundAt(index, null);
				}
				else
				{
					selected = ColorTabs.instance().alterColorDarken(color);
					setForegroundAt(index, selected);
					setBackgroundAt(index, null);
				}
/*
		try {
		    this.getUI().uninstallUI(this);
		    UIManager.getDefaults().put(
			"TabbedPane.selected",
			new ColorUIResource(
			    ColorTabs.instance().alterColorHighlight(color)
			)
		    );
		    this.getUI().installUI(this);
		} catch (Exception e) {
		    Log.log(Log.ERROR, BufferTabs.class, "updateHighlightAt: " + e.toString());
					e.printStackTrace();
		}*/
			}
		}
	}


	private void updateTitleAt(int index)
	{
		if (index < 0 || index >= getTabCount())
		{
			return;
		}
		Buffer buffer = bufferSet.getBuffer(index);
		String title = buffer.getName();
		Icon icon = null;
		if (jEdit.getBooleanProperty("buffertabs.icons", true))
		{
			icon = buffer.getIcon();
		}
		else
		{
			if (buffer.isDirty())
			{
				title += "*";
			}
			if (buffer.isNewFile())
			{
				title += " (new)";
			}
		}
		setTitleAt(index, title);
		setIconAt(index, icon);
		//if (index != this.getSelectedIndex() )
		//	this.updateColorAt(this.getSelectedIndex());
	}


	public synchronized void updateTitles()
	{
		propertiesChanged(); //CES
		for (int index = getTabCount() - 1; index >= 0; index--)
		{
			updateTitleAt(index);
		}
	}

	public void setTabPlacement(String location)
	{
		location = location.toLowerCase();
		int placement = BOTTOM;
		if ("top".equals(location))
		{
			placement = TOP;
		}
		else if ("left".equals(location))
		{
			placement = LEFT;
		}
		else if ("right".equals(location))
		{
			placement = RIGHT;
		}
		setTabPlacement(placement);
	}


	/**
	 * Overridden so the JEditTextArea is at every index.
	 */
	@Override
	public Component getComponentAt(int index)
	{
		if (changeHandler.isEnabled() && index >= 0 && index < getTabCount())
		{
			return textArea;
		}
		else
		{
			return super.getComponentAt(index);
		}
	}


	/**
	 * Overridden so the JEditTextArea is at every index.
	 */
	@Override
	public int indexOfComponent(Component component)
	{
		return super.indexOfComponent(textArea);
	}


	@Override
	protected String paramString()
	{
		int index = getSelectedIndex();
		if (index >= 0)
		{
			return getTitleAt(index);
		}
		else
		{
			return "";
		}
	}

	@Override
	public boolean isFocusable()
	{
		return false;
	}

	@Override
	public boolean isRequestFocusEnabled()
	{
		return false;
	}

	public int getTabAt(int x, int y)
	{

		for (int index = 0; index < getTabCount(); index++)
		{
			Rectangle rect = getBoundsAt(index);
			if (rect.contains(x, y))
			{
				return index;
			}
		}
		return -1;
	}


	private static int moving = -1;

	/**
	 * An inner class used to handle a popup menu when right-clicking on the
	 * tab pane. The actions currently apply to the frontmost buffer, which is
	 * automatically the tab that is clicked because the buffer comes to the
	 * front before the popup menu is displayed.
	 */
	class MouseHandler extends MouseAdapter
	{


		/**
		 * Handles the right-click, displaying the popup
		 */
		@Override
		public void mousePressed(MouseEvent e)
		{
			if (!jEdit.getBooleanProperty("buffertabs.usePopup", true))
			{
				return;
			}

			if (GUIUtilities.isPopupTrigger(e))
			{
				// Request focus to this buffer so we close/reload/save the
				// right buffer!
				editPane.focusOnTextArea();

				//nab our popup
				JPopupMenu popupMenu = BufferTabsPlugin.getRightClickPopup();
				if (popupMenu == null)
				{
					return;
				}

				int x = e.getX();
				int y = e.getY();

				View view = editPane.getView();

				// Display it!
				popupMenu.show(e.getComponent(), x, y);
			}
			else
			{
				// if middle button close the buffer
				if (SwingUtilities.isMiddleMouseButton(e)
				    && jEdit.getBooleanProperty("buffertabs.close-tab-on.single-middle-click"))
				{
					// set the focus on the selected buffer
					int tabIndex = getTabAt(e.getX(), e.getY());

					editPane.focusOnTextArea();
					jEdit.closeBuffer(editPane.getView(), bufferSet.getBuffer(tabIndex));
					//if ( tab != selection )
					//BufferTabs.this.editPane.getBuffer() );
				}
				if (SwingUtilities.isLeftMouseButton(e))
				{
					moving = getTabAt(e.getX(), e.getY());
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e))
			{
				if (moving != -1)
				{
					int mv = moving;
					moving = -1;
					int index = getTabAt(e.getX(), e.getY());
					if (index != -1 && index != mv)
					{
						//System.out.println( "moving tab from " + moving + " to " + index );
						Buffer movedBuffer = bufferSet.getBuffer(mv);
						bufferSet.addBufferAt(movedBuffer, index);
						// moving the tab
					}
					else
					{
						//System.out.println( "not moving tab" );
						// set the focus to the selected tab
						editPane.focusOnTextArea();
					}

					// always reset the moving indicator
					setCursor(Cursor.getDefaultCursor());
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e))
			{
				if (e.getClickCount() == 2 &&
				    jEdit.getBooleanProperty("buffertabs.close-tab-on.double-left-click"))
				{
					//set the focus on the selected buffer
					int tabIndex = getTabAt(e.getX(), e.getY());
					editPane.focusOnTextArea();
					jEdit.closeBuffer(editPane.getView(), bufferSet.getBuffer(tabIndex));

				}
			}
		}

	}

	class MouseMotionHandler implements MouseMotionListener
	{

		public void mouseDragged(MouseEvent e)
		{
			if (moving != -1)
			{
				if (getCursor() != Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
			else
			{
				setCursor(Cursor.getDefaultCursor());
			}
		}

		public void mouseMoved(MouseEvent e)
		{
		}
	}
}
