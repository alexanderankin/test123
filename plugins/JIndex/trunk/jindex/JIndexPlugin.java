/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * JIndexPlugin.java - JIndex plugin
 * Copyright (C) 1999 Dirk Moebius
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


package jindex;

import java.util.Vector;
import org.gjt.sp.jedit.*;
// deprecated -- commented bty maeste -- import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.OptionsDialog;
// deprecated -- commented bty maeste -- import org.gjt.sp.jedit.msg.CreateDockableWindow;
import org.gjt.sp.util.Log;


/**
 * A plugin for accessing JavaDoc generated API documentation via
 * keywords in the jEdit textarea.
 *
 * @author <A HREF="mailto:dmoebius@gmx.net">Dirk Moebius</A>
 */
public class JIndexPlugin extends EditPlugin {

    public final static String NAME = JIndexDockable.DOCKABLE_NAME;


    public void start() {
        // deprecated -- commented bty maeste -- EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, NAME);
        if (JIndexHolder.getInstance().getStatus() == JIndexHolder.STATUS_NOT_EXISTS) {
            String s = java.io.File.separator;
            String home = System.getProperty("java.home");
            jEdit.setProperty("jindex.lib.name.0", home+s+"lib"+s+"rt.jar");
            jEdit.setProperty("jindex.lib.doc.0", home+s+"docs"+s+"api");
            jEdit.setProperty("jindex.lib.oldjdoc.0", "false");
        }
    }


    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu("jindex-menu"));
    }


    public void createOptionPanes(OptionsDialog optionsDialog) {
        optionsDialog.addOptionPane(new JIndexOptionPane());
    }


    public void handleMessage(EBMessage message) {
        /* deprecated -- commented bty maeste
        if (message instanceof CreateDockableWindow) {
            // jEdit requests an instance of a dockable JIndex window:
            CreateDockableWindow cmsg = (CreateDockableWindow) message;
            if (NAME.equals(cmsg.getDockableWindowName()))
                cmsg.setDockableWindow(new JIndexDockable(cmsg.getView()));
        } */
    }


    /**
     * Show either the JIndexDockable, or create a MultipleEntriesDialog,
     * and lookup the specified token in the index.
     *
     * @param view  where to create the JIndexDockable or the MultipleEntriesDialog.
     * @param token  the token.
     */
    public static void lookup(View view, String token) {
        DockableWindowManager mgr = view.getDockableWindowManager();
        if (mgr != null && mgr.isDockableWindowVisible(NAME)) {
            // a docked JIndexDockable is already visible in the given View, use it:
            mgr.showDockableWindow(NAME);
            // deprecated -- commented bty maeste -- DockableWindow win = mgr.getDockableWindow(NAME);
            javax.swing.JComponent win = mgr.getDockable(NAME);
            JIndexDockable d = (JIndexDockable) win.getComponent(0);
            d.search(token);
        } else {
            // create a new MultipleEntriesDialog and search:
            MultipleEntriesDialog dlg = new MultipleEntriesDialog(view, token);
            dlg.search(token);
        }
    }

}
