/*
 * BufferTabs.java - Part of the BufferTabs plugin for jEdit.
 * Copyright (C) 1999, 2000 Jason Ginchereau
 * Copyright (C) 2000, 2001, 2002, 2003 Andre Kaplan
 * Copyright (C) 2001 Joe Laffey
 * Copyright (C) 2003 Kris Kopicki
 * Copyright (C) 2003 Chris Samuels
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;

import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;

import org.gjt.sp.util.Log;
import java.awt.Rectangle;
import java.awt.Cursor;
import java.awt.event.MouseMotionListener;

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
 */
public class BufferTabs extends JTabbedPane implements EBComponent
{
    private EditPane   editPane;
    private JComponent textArea;
    private Vector     buffers;

    private ChangeHandler changeHandler;
    private MouseHandler  mouseHandler;
    private MouseMotionHandler  mouseMotionHandler;

    /**
     * Creates a new set of buffer tabs that is attached to an EditPane,
     * and contains the EditPane's text area.
     */
    public BufferTabs(EditPane editPane) {
        this.editPane = editPane;
        this.textArea = editPane.getTextArea();
        this.buffers  = new Vector();

        this.changeHandler = new ChangeHandler();
        this.changeHandler.setEnabled(true);
        this.mouseHandler = new MouseHandler();
    }


    /**
     * Initializes tabs and starts listening for events.
     */
    public synchronized void start() {
        this.propertiesChanged(); //CES
        Buffer buffer = jEdit.getFirstBuffer();
        for (int i = 0; buffer != null; buffer = buffer.getNext(), i++) {
            this.bufferCreated(buffer, i);
        }

        EditBus.addToBus(this);

        int index = this.buffers.indexOf(this.editPane.getBuffer());
        if (index >= 0) {
            this.setSelectedIndex(index);
        }

        this.addChangeListener(this.changeHandler);

        // Mouse Listener for popup menu support
        this.addMouseListener(this.mouseHandler);
        
        // Mouse Motion listener for moving cursoMouseMotionHandlerr
        this.addMouseMotionListener( this.mouseMotionHandler );		
    }


    /**
     * Stops listening for events.
     */
    public synchronized void stop() {
        EditBus.removeFromBus(this);
        this.removeChangeListener(this.changeHandler);

        this.removeMouseListener(this.mouseHandler);
		
		  this.removeMouseMotionListener( this.mouseMotionHandler );	
    }


    /**
     * Gets the EditPane this tab set is attached to.
     */
    public EditPane getEditPane() {
        return this.editPane;
    }


    /**
     * EditBus message handling.
     */
    public void handleMessage(EBMessage msg) {
        Buffer buffer = null;
        if (msg instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate) msg;
            buffer = bu.getBuffer();
            if (bu.getWhat() == BufferUpdate.CREATED) {
                this.bufferCreated(buffer, buffer.getIndex());
            }
            else if (bu.getWhat() == BufferUpdate.CLOSED) {
                this.bufferClosed(buffer);
            }
            else if (bu.getWhat() == BufferUpdate.DIRTY_CHANGED) {
                this.bufferDirtyChanged(buffer);
            }
            else if (bu.getWhat() == BufferUpdate.LOADED) {
                this.bufferLoaded(buffer);
            }
            else if (bu.getWhat() == BufferUpdate.SAVED) {
                Buffer buff = bu.getBuffer();
                int index = buffers.indexOf(buff);
                setToolTipTextAt(index,buff.getPath());
            }
        } else if (msg instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) msg;
            if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED) {
                if (this.editPane == epu.getEditPane()) {
                    try {
                        buffer = this.editPane.getBuffer();
                        this.changeHandler.setEnabled(false);
                        int index = this.buffers.indexOf(buffer);
                        if (index >= 0) {
							updateColorAt( this.getSelectedIndex());
                            this.setSelectedIndex(index);
							updateHighlightAt(index);
                        }
                    } finally {
                        this.changeHandler.setEnabled(true);
                    }
                }
            }
        }
    }


    /**
     * Adds a tab when a Buffer is created.
     */
    private synchronized void bufferCreated(Buffer buffer, int index) {
        if (this.buffers.indexOf(buffer) >= 0) { return; }

        this.buffers.insertElementAt(buffer, index);

        try {
            // workaround: calls to SwingUtilities.updateComponentTreeUI
            this.getUI().uninstallUI(this);
            this.changeHandler.setEnabled(false);
			ColorTabs.instance().setEnabled( false );
			
            Component component = null;
            if (super.indexOfComponent(this.textArea) == -1) {
                component = this.textArea;
            }

            this.insertTab(buffer.getName(), null, component, buffer.getPath(), index);
            this.updateTitleAt(index);
            int selectedIndex = this.buffers.indexOf(this.editPane.getBuffer());
            this.setSelectedIndex(selectedIndex);

            if (component == this.textArea) {
                this.textArea.setVisible(true);
            }
        } finally {
            this.changeHandler.setEnabled(true);
			ColorTabs.instance().setEnabled( true );
            // workaround: calls to SwingUtilities.updateComponentTreeUI
            this.getUI().installUI(this);
        }

        //CES: Force correct color for new buffer tab
        if(index>=0) {
           this.setSelectedIndex(index);
           this.updateHighlightAt(index);
        }
    }


    /**
     * Removes the tab when a Buffer is closed.
     */
    private synchronized void bufferClosed(Buffer buffer) {
        int index = this.buffers.indexOf(buffer);
        this.buffers.removeElementAt(index);

        try {
            this.changeHandler.setEnabled(false);

            this.removeTabAt(index);
            int selectedIndex = this.buffers.indexOf(this.editPane.getBuffer());
            this.setSelectedIndex(selectedIndex);

            if (   (this.getTabCount() > 0)
                && (super.indexOfComponent(this.textArea) == -1)
            ) {
                 this.setComponentAt(0, this.textArea);
                 this.textArea.setVisible(true);
            }

            if (selectedIndex >= 0) {
                //CES: Ensure selected tab has correct color
                this.updateHighlightAt(selectedIndex);
            }

        } finally {
            this.changeHandler.setEnabled(true);
        }
    }


    public void propertiesChanged() {
        if (ColorTabs.instance().isEnabled() != jEdit.getBooleanProperty("buffertabs.color-tabs")) {
            ColorTabs.instance().setEnabled(!ColorTabs.instance().isEnabled());

            //Turn off all color features
            if (!ColorTabs.instance().isEnabled()) {
                try {
                    for (int i = this.getTabCount() - 1; i >= 0; i--) {
                        this.setBackgroundAt(i, null);
                        this.setForegroundAt(i, null);
                    }
                } catch (java.lang.NullPointerException npe) {
                    Log.log(Log.ERROR, BufferTabs.class, "propertiesChanged: 1 " + npe.toString());
                }

                try {
                    this.getUI().uninstallUI(this);
                    UIManager.getDefaults().put("TabbedPane.selected", null);
                    this.getUI().installUI(this);
                } catch (java.lang.NullPointerException npe) {
                    Log.log(Log.ERROR, BufferTabs.class, "propertiesChanged: 2 " + npe.toString());
                }
            }
        }

        if (ColorTabs.instance().isEnabled()) {
            ColorTabs.instance().setMuteColors(jEdit.getBooleanProperty("buffertabs.color-mute"));
            ColorTabs.instance().setColorVariation(jEdit.getBooleanProperty("buffertabs.color-variation"));
            ColorTabs.instance().setForegroundColorized(jEdit.getBooleanProperty("buffertabs.color-foreground"));

            if (ColorTabs.instance().isSelectedColorized() != jEdit.getBooleanProperty("buffertabs.color-selected")) {
                ColorTabs.instance().setSelectedColorized(!ColorTabs.instance().isSelectedColorized());

                //Turn off all colorhighlight
                if (!ColorTabs.instance().isSelectedColorized()) {
                    try {
                        this.getUI().uninstallUI(this);
                        UIManager.getDefaults().put("TabbedPane.selected", null);
                        this.getUI().installUI(this);
                    } catch (Exception e) {
                        Log.log(Log.ERROR, BufferTabs.class, "propertiesChanged: 3 " + e.toString());
                    }
                }


            }
			if (ColorTabs.instance().isSelectedColorized()) 
			{
				  ColorTabs.instance().setSelectedForegroundColorized(jEdit.getBooleanProperty("buffertabs.color-selected-foreground"));
			}
			
            ColorTabs.instance().propertiesChanged();
		
			if ( getSelectedIndex() >= 0)
			updateHighlightAt( getSelectedIndex() );
        }
    }



    private class ChangeHandler implements ChangeListener {
        private boolean enabled = true;


        public boolean isEnabled() {
            return this.enabled;
        }


        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }


        /**
         * Sets the EditPane's buffer when a tab is selected.
         */
        public synchronized void stateChanged(ChangeEvent evt) {
            int index = BufferTabs.this.getSelectedIndex();
            if ((index >= 0) && this.isEnabled()) {
                Buffer buffer = (Buffer) BufferTabs.this.buffers.elementAt(index);
                if (buffer != null) {
					int selectedIndex = BufferTabs.this.buffers.indexOf(BufferTabs.this.editPane.getBuffer());
					if ( selectedIndex >= 0 )
					{
						BufferTabs.this.updateColorAt(selectedIndex);
					}
                    BufferTabs.this.editPane.setBuffer(buffer);
                    BufferTabs.this.updateHighlightAt(index); //CES
                }
            }
        }
    }


    /**
     * Updates the tab title when the buffer's dirty status changes.
     */
    private synchronized void bufferDirtyChanged(Buffer buffer) {
        int index = this.buffers.indexOf(buffer);
        this.updateTitleAt(index);
    }


    /**
     * A new buffer has been loaded, or an existing buffer has been re-loaded.
     * Make sure that the title bar / icons / etc. are in synch with
     * the new state of the buffer.
     */
    private synchronized void bufferLoaded(Buffer buffer) {
        int index = this.buffers.indexOf(buffer);
        if (index > 0 && index < this.buffers.size()) {
            this.updateTitleAt(index);
			this.updateHighlightAt(index); 
        }
    }


    /**
     * Updates the color of the given tab
     */
    private void updateColorAt(int index) {
        if (!ColorTabs.instance().isEnabled()) { return; }

		
        Buffer buffer = (Buffer) this.buffers.elementAt(index);
        String name = buffer.getName();

        try {
            if (!ColorTabs.instance().isForegroundColorized()) {
                Color color = ColorTabs.instance().getDefaultColorFor(name);
                this.setBackgroundAt(index, color);
                this.setForegroundAt(index, null);
            } else {
                Color color = ColorTabs.instance().getDefaultColorFor(name);
                this.setForegroundAt(index, color);
                this.setBackgroundAt(index, null);
            }
        } catch (NullPointerException npe) {
            Log.log(Log.ERROR, BufferTabs.class, "updateColorAt: " + npe.toString());
			npe.printStackTrace();
        }

       // this.updateHighlightAt(index);
    }


    /**
     *  Force the Look and Feel to use the given color as its 'selected' color.
     *  TODO: This may cause side-effects with other tab panes.
     */
    private void updateHighlightAt(int index) {
        if (   ColorTabs.instance().isEnabled()
            && ColorTabs.instance().isSelectedColorized()
        ) {
            if (index == this.getSelectedIndex()) {
                Buffer buffer = (Buffer) this.buffers.elementAt(index);
                String name = buffer.getName();
                Color color = ColorTabs.instance().getDefaultColorFor(name);
			 Color selected = null;
				
            if (!ColorTabs.instance().isSelectedForegroundColorized()) {
				selected =  ColorTabs.instance().alterColorHighlight(color);
                this.setBackgroundAt(index, selected);
                this.setForegroundAt(index, null);
            } else {
				selected =  ColorTabs.instance().alterColorDarken(color);		
                this.setForegroundAt(index, selected);
                this.setBackgroundAt(index, null);
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


    private void updateTitleAt(int index) {
        Buffer buffer = (Buffer) this.buffers.elementAt(index);
        String title = buffer.getName();
        Icon icon = null;
        if (jEdit.getBooleanProperty("buffertabs.icons", true)) {
            icon = buffer.getIcon();
        } else {
            if (buffer.isDirty()) { title += "*"; }
            if (buffer.isNewFile()) { title += " (new)"; }
        }
        this.setTitleAt(index, title);
        this.setIconAt(index, icon);
        if (index != this.getSelectedIndex() )
			this.updateColorAt(index);
    }


    public synchronized void updateTitles() {
      this.propertiesChanged(); //CES
        for (int index = this.getTabCount() - 1; index >= 0; index--) {
            this.updateTitleAt(index);
        }
    }


    public void setTabPlacement(String location) {
        int placement = BOTTOM;
        location = location.toLowerCase();
        if (location.equals("top")) {
            placement = TOP;
        } else if (location.equals("left")) {
            placement = LEFT;
        } else if (location.equals("right")) {
            placement = RIGHT;
        }
        this.setTabPlacement(placement);
    }


    /**
     * Overridden so the JEditTextArea is at every index.
     */
    public Component getComponentAt(int index) {
        if (   this.changeHandler.isEnabled()
            && (index >= 0)
            && (index < this.getTabCount())
        ) {
            return this.textArea;
        } else {
            return super.getComponentAt(index);
        }
    }


    /**
     * Overridden so the JEditTextArea is at every index.
     */
    public int indexOfComponent(Component component) {
        return super.indexOfComponent(this.textArea);
    }


    protected String paramString() {
        int index = this.getSelectedIndex();
        if (index >= 0) {
            return "" + this.getTitleAt(index);
        } else {
            return "";
        }
    }


    public boolean isFocusTraversable() {
        return false;
    }


    public boolean isRequestFocusEnabled() {
        return false;
    }

    public int getTabAt(int x, int y ) {

        for ( int index = 0 ; index < getTabCount() ; index++ )
        {
            Rectangle rect = getBoundsAt( index );
            if ( rect.contains(x, y))
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
        public void mousePressed(MouseEvent me) {
            if (!jEdit.getBooleanProperty("buffertabs.usePopup", true)) {
                return;
            }

            if (GUIUtilities.isPopupTrigger(me)) {
                // Request focus to this buffer so we close/reload/save the
                // right buffer!
                BufferTabs.this.editPane.focusOnTextArea();

                //nab our popup
                final JPopupMenu popupMenu = BufferTabsPlugin.getRightClickPopup();
                if (popupMenu == null) {
                    return;
                }

                int x = me.getX();
                int y = me.getY();

                View view = BufferTabs.this.editPane.getView();

                // Make sure popup will fit in the view
                Dimension viewSize = view.getSize();
                Point viewLocation = view.getLocationOnScreen();
                // We don't display popup on view borders
                Insets viewInsets  = view.getInsets();

                final Point tabsLocation = BufferTabs.this.getLocationOnScreen();

                Dimension popupSize = popupMenu.getSize();
                if (popupSize.height == 0) {
                    // If popup has not yet been displayed its size is unknown
                    // and getSize() will return 0. We show the popup offscreen
                    // and then re-get the size.
                    popupMenu.show(BufferTabs.this, Integer.MIN_VALUE, Integer.MIN_VALUE);
                    popupMenu.setVisible(false);
                    popupSize = popupMenu.getSize();
                }

                // Adjusting x: if popup right bound lies outside the View,
                // display it entirely at the left of the cursor
                if (  (tabsLocation.x + x + popupSize.width) >
                      (viewLocation.x + viewSize.width - viewInsets.right)
                ) {
                    x -= popupSize.width;
                }

                // Adjusting y: if popup bottom bound lies outside the View,
                // display it as low as possible
                if ( (tabsLocation.y + y + popupSize.height) >
                     (viewLocation.y + viewSize.height - viewInsets.bottom)
                ) {
                    y =   (viewLocation.y + viewSize.height - viewInsets.bottom)
                        - (tabsLocation.y + popupSize.height);
                }

                // Display it!
                popupMenu.show(BufferTabs.this, x, y);
            }
			            else
            {
                // if middle button close the buffer
                if ( SwingUtilities.isMiddleMouseButton( me ) )
                {
                    // set the focus on the selected buffer
                    int tab = getTabAt( me.getX(), me.getY() );
                    int selection = BufferTabs.this.getSelectedIndex();
                    
                    BufferTabs.this.editPane.focusOnTextArea();
                    jEdit.closeBuffer( BufferTabs.this.editPane.getView(), (Buffer)buffers.get( tab ) );
                    //if ( tab != selection )
                   //BufferTabs.this.editPane.getBuffer() );
                }
                if ( SwingUtilities.isLeftMouseButton(me) )
                {
                      moving = getTabAt( me.getX(), me.getY() );
                }
            }
        }
     public void mouseReleased(MouseEvent me) {
            if ( SwingUtilities.isLeftMouseButton(me) ){
                if ( moving != -1 )
                {
                    int index = getTabAt(me.getX(), me.getY() );
              
                    if ( ( index != -1  ) && ( index != moving ) )
                    {
                        //System.out.println( "moving tab from " + moving + " to " + index );
                        Buffer movedBuffer = (Buffer)BufferTabs.this.buffers.elementAt(moving);
                        // moving the tab
                        bufferClosed(movedBuffer);
                        bufferCreated(movedBuffer, index);
                    }
                    else
                    {
                        //System.out.println( "not moving tab" );
                        // set the focus to the selected tab
                        BufferTabs.this.editPane.focusOnTextArea();
                    }

                    // always reset the moving indicator
                    BufferTabs.this.setCursor( Cursor.getDefaultCursor() );
                    moving  = -1;
                }
            }
        }    
        }
		
		
		   class MouseMotionHandler implements MouseMotionListener {
        
        public void mouseDragged(MouseEvent e)
        {
            if ( moving !=-1 )
            {
                if ( BufferTabs.this.getCursor() != Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) )
                BufferTabs.this.setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) ); 
            }
            else
            {
                BufferTabs.this.setCursor( Cursor.getDefaultCursor() );
            }
        }
        
        public void mouseMoved(MouseEvent e)
        {
        }
    }
}
