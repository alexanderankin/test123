/*
 * InfoViewerAction.java - jEdit action listener
 * Copyright (C) 2000-2002 Dirk Moebius
 * Contains portions of EditAction.java Copyright (C) 1998, 1999 by
 * Slava Pestov
 *
 * :tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DefaultInputHandler;
import org.gjt.sp.util.Log;

/**
 * The class all InfoViewer actions must extend. It is an
 * <code>AbstractAction</code> with support for finding out
 * the InfoViewer that invoked the action.<p>
 *
 * @author Dirk Moebius
 */
public abstract class InfoViewerAction extends AbstractAction
{

	public static KeyStroke parseKeyStroke(String keyStroke)
    {
            if(keyStroke == null)
                    return null;
            int modifiers = 0;
            int index = keyStroke.indexOf('+');
            if(index != -1)
            {
                    for(int i = 0; i < index; i++)
                    {
                            switch(Character.toUpperCase(keyStroke
                                    .charAt(i)))
                            {
                            case 'A':
                                    modifiers |= InputEvent.ALT_MASK;
                                    break;
                            case 'C':
                                    modifiers |= InputEvent.CTRL_MASK;
                                    break;
                            case 'M':
                                    modifiers |= InputEvent.META_MASK;
                                    break;
                            case 'S':
                                    modifiers |= InputEvent.SHIFT_MASK;
                                    break;
                            }
                    }
            }
            String key = keyStroke.substring(index + 1);
            if(key.length() == 1)
            {
                    char ch = key.charAt(0);
                    if(modifiers == 0)
                            return KeyStroke.getKeyStroke(ch);
                    else
                    {
                            return KeyStroke.getKeyStroke(Character.toUpperCase(ch),
                                    modifiers);
                    }
            }
            else if(key.length() == 0)
            {
                    Log.log(Log.ERROR,DefaultInputHandler.class,
                            "Invalid key stroke: " + keyStroke);
                    return null;
            }
            else
            {
                    int ch;

                    try
                    {
                            ch = KeyEvent.class.getField("VK_".concat(key))
                                    .getInt(null);
                    }
                    catch(Exception e)
                    {
                            Log.log(Log.ERROR,DefaultInputHandler.class,
                                    "Invalid key stroke: "
                                    + keyStroke);
                            return null;
                    }

                    return KeyStroke.getKeyStroke(ch,modifiers);
            }
    }


	
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
    InfoViewerAction(String name_key)
    {
        super(jEdit.getProperty(name_key));

        String icon = jEdit.getProperty(name_key + ".icon");
        String desc = jEdit.getProperty(name_key + ".description");
        String mnem = jEdit.getProperty(name_key + ".mnemonic");
        String shrt = jEdit.getProperty(name_key + ".shortcut");

        if (icon != null)
        {
            Icon i = GUIUtilities.loadIcon(icon);
            if (i != null)
                putValue(SMALL_ICON, i);
        }

        if (desc != null)
        {
            putValue(SHORT_DESCRIPTION, desc);
            putValue(LONG_DESCRIPTION, desc);
        }

        if (mnem != null)
            putValue(MNEMONIC_KEY, new Integer(mnem.charAt(0)));

        if (shrt != null) {
//            putValue(ACCELERATOR_KEY, DefaultInputHandler.parseKeyStroke(shrt));
        	
        }
    }


    /**
     * Determines the InfoViewer to use for the action.
     */
    public static InfoViewer getViewer(EventObject evt)
    {
        if (evt == null)
            return null; // this shouldn't happen

        Object o = evt.getSource();
        if (o instanceof Component)
            return getViewer((Component)o);
        else
            return null;
    }


    /**
     * Finds the InfoViewer parent of the specified component.
     */
    public static InfoViewer getViewer(Component comp)
    {
        for (;;)
        {
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


    /**
     * Finds the Frame parent of the source component of
     * the given EventObject.
     */
    public static Frame getFrame(EventObject evt)
    {
        if (evt == null)
            return null; // this shouldn't happen

        Object source = evt.getSource();

        if (source instanceof Component)
        {
            Component comp = (Component)source;
            for (;;)
            {
                if (comp instanceof Frame)
                    return (Frame)comp;
                else if (comp instanceof JPopupMenu)
                    comp = ((JPopupMenu)comp).getInvoker();
                else if (comp != null)
                    comp = comp.getParent();
                else
                    break;
            }
        }

        return null;
    }

}

