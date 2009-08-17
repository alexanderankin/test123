/*
* TaskListPopup.java - provides popup actions for TaskList plugin
* Copyright (c) 2001 John Gellene
* jgellene@nyc.rr.com
* http://community.jedit.org
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
* $Id$
*/

package tasklist;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;


/**
 * A popup menu for the TaskList plugin
 *
 * @author John Gellene (jgellene@nyc.rr.com)
 */
public class TaskListPopup extends JPopupMenu {
    private View view;
    private JComponent comp;
    private Point point;
    private final ActionListener listener;

    /**
     * Constructor
     *
     * @param View view the view in which the popup menu will appear
     * @param TaskList list the TaskList object represented in the
     * window in which the popup menu will appear
     * @param int TaskNum the zero-based index of the selected table row
     * that will be the subject of the popup
     */
    public TaskListPopup( View view, JComponent comp, Point point ) {
        super( jEdit.getProperty( "tasklist.popup.heading" ) );
        setLightWeightPopupEnabled( true );
        this.view = view;
        this.comp = comp;
        this.point = point;
        listener = new ActionHandler();

        BoundedMenu changeMenu = new BoundedMenu( jEdit.getProperty( "tasklist.popup.change-menu" ) );

        int item = 0;
        String name = jEdit.getProperty( "tasklist.tasktype." + item + ".name" );
        while ( name != null ) {
            changeMenu.add( createMenuItem( name ) );
            item++;
            name = jEdit.getProperty( "tasklist.tasktype." + item + ".name" );
        }
        add( changeMenu );

        BoundedMenu deleteMenu = new BoundedMenu( jEdit.getProperty( "tasklist.popup.delete-task", "Delete task" ) );
        deleteMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.delete-task-tag", "Delete task tag" ), "%Dtag" ) );
        deleteMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.delete-entire-tag", "Delete entire task" ), "%Dtask" ) );
        add( deleteMenu );

        JMenuItem parseBuffer = createMenuItem( jEdit.getProperty( "tasklist.popup.parse-buffer", "Parse buffer" ), "parse-buffer" );
        add( parseBuffer );
        JMenuItem parseAll = null;
        if ( comp instanceof JTree ) {
            parseAll = createMenuItem( jEdit.getProperty( "tasklist.popup.parse-all", "Parse all" ), "parse-all" );
            add( parseAll );
        }

        if ( getTask() == null ) {
            changeMenu.setEnabled( false );
            deleteMenu.setEnabled( false );
            parseBuffer.setEnabled( false );
            if ( parseAll != null ) {
                parseAll.setEnabled( false );
            }
        }

    }

    /**
    * An extension of the JMenu class that relocates the object's child popup menu
    * as necessary so that it does not appear to right or below the bounds of
    * another component.
    * <p>
    * In the TaskList implementation, the bounding component is the TaskList panel
    * containing the table display of taks items.
    *
    * @author John Gellene ( jgellene@nyc.rr.com )
    */
    public class BoundedMenu extends JMenu {
        /**
         * Constructs a BoundedMenu object.
         *
         * @param bounds the Component forming the bounds for the object's
         * child popup menu.
         * @param title the text to be displayed on the parent menu item
         */
        public BoundedMenu( String title ) {
            super( title );
        }

        /**
         * Overrides the implementation in JMenu to relocate the child
         * popup menu so it does not appear outside the right-hand
         * or lower borders of the menu's bounding component.
         *
         * @param visible determines whether the child popup menu is to
         * made visible.
         */
        public void setPopupMenuVisible( boolean visible ) {
            boolean oldValue = isPopupMenuVisible();
            if ( visible != oldValue ) {
                if ( ( visible == true ) && isShowing() ) {
                    Point p = setLocation();
                    getPopupMenu().show( this, p.x, p.y );
                }
                else {
                    getPopupMenu().setVisible( false );
                }
            }
        }

        /**
         * Determines the location of the child popup menu.
         *
         * @return a Point representing the upper left-hand corner
         * of the child popup menu, expressed relative to the parent
         * menu of this BoundedMenu object.
         */
        private Point setLocation() {
            Component parent = getParent();
            Dimension dParent = parent.getPreferredSize();
            /* NOTE: default location of child popup menu */
            Point pPopup = new Point( dParent.width - 1 , -1 );
            SwingUtilities.convertPointToScreen( pPopup, parent );
            SwingUtilities.convertPointFromScreen( pPopup, comp );
            Dimension dList = comp.getSize();
            Dimension dPopup = getPopupMenu().getPreferredSize();
            Point pThis = this.getLocation();
            if ( pPopup.x + dPopup.width > dList.width )
                pPopup.x -= ( dPopup.width + dParent.width );
            if ( pPopup.y + pThis.y + dPopup.height > dList.height )
                pPopup.y -= ( dPopup.height - dParent.height + pThis.y );
            SwingUtilities.convertPointToScreen( pPopup, comp );
            SwingUtilities.convertPointFromScreen( pPopup, parent );
            return pPopup;
        }
    }

    /**
     * Creates a menu item for the popup menu
     *
     * @param name Represents the menu item entry's text
     * @param cmd Represents the action command associated
     * with the menu item
     *
     * @return a JMenuItem representing the new menu item
     */
    private JMenuItem createMenuItem( String name, String cmd ) {
        JMenuItem mi = new JMenuItem( name );
        mi.setActionCommand( cmd != null ? cmd : name );
        mi.addActionListener( listener );
        return mi;
    }

    /**
     * Creates a menu item for the popup menu containing an
     * action command with the same name as the menu item
     *
     * @param name Represents the menu item entry's text
     *
     * @return a JMenuItem representing the new menu item
     */
    private JMenuItem createMenuItem( String name ) {
        return createMenuItem( name, null );
    }

    /**
     * Causes substitution of the comment tag for the selected task item;
     * displays a message if a parsing error occurs;
     * reparses buffer regardless of success.
     */
    class ActionHandler implements ActionListener {

        public void actionPerformed( ActionEvent evt ) {
            Task task = getTask();
            if ( task == null ) {
                return ;
            }
            String cmd = evt.getActionCommand();
            if ( cmd.equals( "parse-buffer" ) ) {
                EditBus.send( new ParseBufferMessage( view, task.getBuffer(), ParseBufferMessage.DO_PARSE ) );
                return ;
            }
            else if ( cmd.equals( "parse-all" ) ) {
                EditBus.send( new ParseBufferMessage( view, task.getBuffer(), ParseBufferMessage.DO_PARSE_ALL ) );
                return ;
            }
            else if ( cmd.equals( "%Dtask" ) ) {
                task.removeTask( view );
            }
            else {
                if ( cmd.equals( "%Dtag" ) ) {
                    task.removeTag( view );
                }
                else {
                    task.replaceTag( view, cmd );
                }
            }
            view = null;
        }
    }

    private Task getTask() {
        if ( comp instanceof JTable ) {
            JTable table = ( JTable ) comp;
            int row = table.rowAtPoint( point );
            TaskListModel model = ( TaskListModel ) table.getModel();
            Task task = ( Task ) model.elementAt( row );
            return task;
        }
        else {
            JTree tree = ( JTree ) comp;
            TreePath path = tree.getPathForLocation( point.x, point.y );
            DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
            Object userObject = node.getUserObject();
            if ( userObject == null || !( userObject instanceof Task ) ) {
                return null;
            }
            Task task = ( Task ) userObject;
            return task;
        }
    }
}