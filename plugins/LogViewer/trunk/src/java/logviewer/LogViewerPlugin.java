/*
 *  Copyright (C) 2003 Don Brown (mrdon@techie.com)
 *  Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)
 *  This file is part of Log Viewer, a plugin for jEdit (http://www.jedit.org).
 *  It is heavily  based off Follow (http://follow.sf.net).
 *  Log Viewer is free software; you can redistribute it and/or modify
 *  it under the terms of version 2 of the GNU General Public
 *  License as published by the Free Software Foundation.
 *  Log Viewer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with Log Viewer; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package logviewer;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.gui.*;

/**
 *  The LogViewer plugin
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 */
public class LogViewerPlugin extends EditPlugin {
    /**  The name of the plugin */
    public final static String NAME = "logviewer";
    /**  The key of the menu property */
    public final static String MENU = "logviewer.menu";
    /**  The prefix for plugin properties */
    public final static String PROPERTY_PREFIX = "logviewer.";

    /**
     *  Creates menu items
     *
     * @param  menuItems  The list of current menu items
     */
    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu(MENU));
    }

    /**
     *  Creates the option pane
     *
     * @param  od  The options dialog
     */
    public void createOptionPanes(OptionsDialog od) {
        od.addOptionPane(new LogViewerOptionPane());
    }

}

