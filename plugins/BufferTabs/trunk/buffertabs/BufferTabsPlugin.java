/*
 * BufferTabsPlugin.java - Main class of the BufferTabs plugin for jEdit.
 * Copyright (C) 1999, 2000 Jason Ginchereau
 * Copyright (C) 2000, 2001, 2002, 2003 Andre Kaplan
 * Copyright (C) 2003 Kris Kopicki
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


import java.awt.Component;
import java.awt.Container;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;


/**
 *
 * @author Jason Ginchereau
 * @author Andre Kaplan
 */
public class BufferTabsPlugin extends EBPlugin {
    private static Hashtable tabsMap = new Hashtable();

    private static JPopupMenu popupMenu;


    /**
     * This allows plugins to add their own option pane to the
     * <code>OptionsDialog</code>.
     */
	 /*
    public void createOptionPanes(OptionsDialog od) {
         od.addOptionPane(new BufferTabsOptionPane());
    }
*/
/*
    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(
            GUIUtilities.loadMenuItem("buffertabs.toggle-vis")
        );
    }
*/

    public void handleMessage(EBMessage msg) {
        if (msg instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) msg;

            if (epu.getWhat() == EditPaneUpdate.CREATED) {
                this.editPaneCreated(epu.getEditPane());
            }
            else if (epu.getWhat() == EditPaneUpdate.DESTROYED) {
                this.editPaneDestroyed(epu.getEditPane());
            }
        }
        else if (msg instanceof PropertiesChanged) {
            this.propertiesChanged();
        }
    }


    private void editPaneCreated(EditPane editPane) {
        if (jEdit.getBooleanProperty("buffertabs.enable", false)) {
            addBufferTabsToEditPane(editPane);
        }
    }


    private void editPaneDestroyed(EditPane editPane) {
        removeBufferTabsFromEditPane(editPane);
    }


    private void propertiesChanged() {
         String location = BufferTabsOptionPane
            .getLocationProperty("buffertabs.location", "bottom");

        View[] views = jEdit.getViews();
        for (int i = 0; i < views.length; i++) {
            EditPane[] editPanes = views[i].getEditPanes();

            for (int j = 0; j < editPanes.length; j++) {
                BufferTabs bt = (BufferTabs) tabsMap.get(editPanes[j]);
                if (bt != null) {
                    bt.setTabPlacement(location);
                    bt.updateTitles();
                }
            }
        }
    }


    public static BufferTabs getBufferTabsForEditPane(EditPane editPane) {
        return (editPane != null) ? (BufferTabs) tabsMap.get(editPane) : null;
    }


    public static void toggleBufferTabsForEditPane(EditPane editPane) {
        boolean vis = !(tabsMap.get(editPane) != null);
        if (vis) {
            addBufferTabsToEditPane(editPane);
        } else {
            removeBufferTabsFromEditPane(editPane);
        }
    }


    private static void addBufferTabsToEditPane(EditPane editPane) {
        Component ta = editPane.getTextArea();
        Container container = ta.getParent();

        BufferTabs tabs = new BufferTabs(editPane);
        tabs.setTabPlacement(BufferTabsOptionPane.getLocationProperty(
            "buffertabs.location", "bottom"));
        tabs.start();
        container.add(tabs);
        tabsMap.put(editPane, tabs);
    }


    private static void removeBufferTabsFromEditPane(EditPane editPane) {
        Component ta = editPane.getTextArea();
        BufferTabs tabs = getBufferTabsForEditPane(editPane);

        if (tabs == null) { return; }

        tabs.stop();
        Container container = tabs.getParent();
        container.remove(tabs);
        container.add(ta);
        tabsMap.remove(editPane);

        while (container != null && !(container instanceof JComponent)) {
            container = container.getParent();
        }
        if (container != null) {
            ((JComponent)container).revalidate();
        }
    }


    protected static JPopupMenu getRightClickPopup() {
    if (popupMenu == null)
        return popupMenu = GUIUtilities.loadPopupMenu("buffertabs.popup");
    else
        return popupMenu;
    }
}

