/*
 * InfoViewerAction.java - jEdit action listener
 * Copyright (C) 2000 Dirk Moebius
 * Contains portions of EditAction.java Copyright (C) 1998, 1999 by 
 * Slava Pestov
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

package infoviewer.actions;

import infoviewer.InfoViewer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.EventObject;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * The class all InfoViewer actions must extend. It is an
 * <code>AbstractAction</code> with support for finding out
 * the InfoViewer that invoked the action.<p>
 *
 * @author Dirk Moebius
 * @author Slava Pestov
 */
public abstract class InfoViewerAction extends AbstractAction {

    public static final String MNEMONIC = "MenuMnemonic";
    public static final String ACCELERATOR = "MenuAccelerator";

    
    /**
     * Creates a new <code>InfoViewerAction</code>. This constructor
     * should be used by InfoViewer's own actions only.
     *
     * @param name_key a jEdit property with the name for the action.
     *                 Other resources are determined by looking up the 
     *                 following keys in the jEdit properties:
     *                 <ul>
     *                 <li><code>name.icon</code> the icon filename</li>
     *                 <li><code>name.description</code> a short description</li>
     *                 <li><code>name.mnemonic</code> a menu mnemonic</li>
     *                 <li><code>name.shortcut</code> an keybord shortcut</li>
     *                 </ul>
     * @see java.awt.KeyStroke#getKeyStroke
     */
    public InfoViewerAction(String name_key) {         
        super(jEdit.getProperty(name_key));
        
        String icon = jEdit.getProperty(name_key + ".icon");
        String desc = jEdit.getProperty(name_key + ".description");
        String mnem = jEdit.getProperty(name_key + ".mnemonic");
        String shrt = jEdit.getProperty(name_key + ".shortcut");

        if (icon != null) {
            URL u = getClass().getResource(icon);
            if (u != null) {
                putValue(SMALL_ICON, new ImageIcon(u));
            }
        }
        if (desc != null) {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(LONG_DESCRIPTION, desc);
        }
        if (mnem != null)
            putValue(MNEMONIC, mnem);
        if (shrt != null)
            putValue(ACCELERATOR, 
                 org.gjt.sp.jedit.textarea.DefaultInputHandler.parseKeyStroke(
                     shrt));
    }
    

    /**
     * Determines the InfoViewer to use for the action.
     */
    public static InfoViewer getViewer(EventObject evt) {
        if (evt != null) {
            Object o = evt.getSource();
            if (o instanceof Component)
                return getViewer((Component)o);
        }
        // this shouldn't happen
        return null;
    }

    
    /**
     * Finds the InfoViewer parent of the specified component.
     */
    public static InfoViewer getViewer(Component comp) {
        for (;;) {
            if (comp instanceof InfoViewer)
                return (InfoViewer)comp;
            else if (comp instanceof JPopupMenu)
                comp = ((JPopupMenu)comp).getInvoker();
            else if (comp != null)
                comp = comp.getParent();
            else
                break;
        }
        return null;
    }

}

