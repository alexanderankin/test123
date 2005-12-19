/*
 *  ShortcutDisplayOptionPane.java - Shortcuts options panel
 *  Copyright (C) 2005 Jeffrey Hoyt
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package shortcutdisplay;

import javax.swing.JLabel;
import javax.swing.Box;
import java.awt.BorderLayout;
import javax.swing.*;
import org.gjt.sp.jedit.*;


/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 20, 2005
 */
public class ShortcutDisplayOptionPane extends AbstractOptionPane
{

    private JCheckBox showPopup;
    private JCheckBox sortByAction;


    /**
     *  Constructor for the ShortcutDisplayOptionPane object
     */
    public ShortcutDisplayOptionPane()
    {
        super( "shortcut-display" );
    }

    // protected members
    /**
     *  Description of the Method
     */
    protected void _init()
    {
        setLayout( new BorderLayout( 12, 12 ) );

        showPopup = new JCheckBox( jEdit.getProperty( "options.shortcuts.popup.label" ),
            jEdit.getBooleanProperty( "options.shortcuts.displaypopup", true ) );
        sortByAction = new JCheckBox( jEdit.getProperty( "options.shortcuts.popup.sort.label" ),
            jEdit.getBooleanProperty( "options.shortcuts.sortbyaction", false ) );
        Box center = Box.createVerticalBox();
        center.add( showPopup );
        center.add( sortByAction );

        add( BorderLayout.CENTER, center );
    }


    /**
     *  Description of the Method
     */
    protected void _save()
    {
        jEdit.setBooleanProperty( "options.shortcuts.displaypopup", showPopup.isSelected() );
        jEdit.setBooleanProperty( "options.shortcuts.sortbyaction", sortByAction.isSelected() );
    }
}

