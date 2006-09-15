/*
 *  :tabSize=4:indentSize=4:noTabs=true:
 *
 *
 *  $Source$
 *  Copyright (C) 2004 Jeffrey Hoyt
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
package shortcutdisplay;


import shortcutdisplay.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

/**
 *  Controlling class for the ShortcutDisplay popup
 *
 *@author     jchoyt
 *@created    April 29, 2004
 */
public class ShortcutDisplay
{

    static ArrayList shortcuts = null;
    /**
     *  a HashMap of prefix, List of shortcuts that start with that prefix. This
     *  should make processing much faster as we can pre-compute the prefixes
     *  and drop all the Shortcuts that don't have prefixes
     */
    protected static HashMap prefixes = null;
    static ShortcutDialog currentDialog = null;


    /**
     *  Constructor for the ShortcutDisplay object
     */
    public ShortcutDisplay() { }


    /**
     *  Description of the Method
     *
     *@param  bindings  Description of the Parameter
     */
    public static void displayShortcuts( Map bindings )
    {
        if ( currentDialog != null )
        {
            disposeShortcuts();
        }
        if ( !jEdit.getBooleanProperty( "options.shortcuts.displaypopup", true ) )
        {
            return;
        }
        currentDialog = new ShortcutDialog( bindings );
        currentDialog.pack();
        GUIUtilities.loadGeometry(currentDialog,"shortcutDisplay");
        currentDialog.show();
    }


    /**
     *  Description of the Methodoo
     */
    public static void disposeShortcuts()
    {
        if ( currentDialog == null )
        {
            return;
        }
        else
        {
            GUIUtilities.saveGeometry(currentDialog, "shortcutDisplay");
            jEdit.setIntegerProperty( "shortcutdisplay.xlocation", currentDialog.getX() );
            jEdit.setIntegerProperty( "shortcutdisplay.ylocation", currentDialog.getY() );
            currentDialog.hide();
            currentDialog.dispose();
            currentDialog = null;
            return;
        }
    }
}


