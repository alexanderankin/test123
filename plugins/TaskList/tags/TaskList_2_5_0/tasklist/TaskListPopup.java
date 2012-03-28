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
import java.util.Enumeration;
import javax.swing.tree.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;

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
     * @param view the view in which the popup menu will appear
     * @param comp the TaskList tree represented in the
     * window in which the popup menu will appear.
     * @param point the location of the mouse click that activated this popup
     */
    public TaskListPopup( View view, JTree comp, Point point ) {
        super( jEdit.getProperty( "tasklist.popup.heading" ) );
        setLightWeightPopupEnabled( true );
        this.view = view;
        this.comp = comp;
        this.point = point;
        listener = new ActionHandler();

        // change tag menu item and submenu
        BoundedMenu changeMenu = new BoundedMenu( jEdit.getProperty( "tasklist.popup.change-menu" ) );
        int item = 0;
        String name = jEdit.getProperty( "tasklist.tasktype." + item + ".name" );
        while ( name != null ) {
            changeMenu.add( createMenuItem( name ) );
            item++;
            name = jEdit.getProperty( "tasklist.tasktype." + item + ".name" );
        }
        add( changeMenu );

        // delete task menu item and submenu
        BoundedMenu deleteMenu = new BoundedMenu( jEdit.getProperty( "tasklist.popup.delete-task", "Delete task" ) );
        deleteMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.delete-task-tag", "Delete task tag" ), "%Dtag" ) );
        deleteMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.delete-entire-tag", "Delete entire task" ), "%Dtask" ) );
        add( deleteMenu );

        // parse buffer menu item
        JMenuItem parseBuffer = createMenuItem( jEdit.getProperty( "tasklist.popup.parse-buffer", "Parse buffer" ), "parse-buffer" );
        add( parseBuffer );

        // parse all menu item
        JMenuItem parseAll = null;
        parseAll = createMenuItem( jEdit.getProperty( "tasklist.popup.parse-all", "Parse all" ), "parse-all" );
        add( parseAll );

        if ( getTask() == null ) {
            changeMenu.setEnabled( false );
            deleteMenu.setEnabled( false );
        }

        // sort by menu
        BoundedMenu sortMenu = new BoundedMenu( jEdit.getProperty( "tasklist.popup.sortby", "Sort by" ) );
        sortMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.linenumber", "Line number" ), "sort-by-line-number" ) );
        sortMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.tasktype", "Task type" ), "sort-by-task-type" ) );
        add( sortMenu );

        // sort direction
        BoundedMenu sortDirectionMenu = new BoundedMenu( jEdit.getProperty( "tasklist.popup.sortdirection", "Sort direction" ) );
        sortDirectionMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.ascending", "Ascending" ), "sort-ascending" ) );
        sortDirectionMenu.add( createMenuItem( jEdit.getProperty( "tasklist.popup.descending", "Descending" ), "sort-descending" ) );
        add( sortDirectionMenu );
        
        JMenuItem toBuffer = null;
        addSeparator();
        toBuffer = createMenuItem( jEdit.getProperty( "tasklist.popup.to-buffer", "TaskList results to buffer" ), "to-buffer" );
        add( toBuffer );
    }

    /**
    * An extension of the JMenu class that relocates the object's child popup menu
    * as necessary so that it does not appear to right or below the bounds of
    * another component.
    * <p>
    * In the TaskList implementation, the bounding component is the TaskList panel
    * containing the table display of task items.
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

            // default location of child popup menu
            Point pPopup = new Point( dParent.width - 1 , -1 );

            SwingUtilities.convertPointToScreen( pPopup, parent );
            SwingUtilities.convertPointFromScreen( pPopup, comp );
            Dimension dList = comp.getSize();
            Dimension dPopup = getPopupMenu().getPreferredSize();
            Point pThis = this.getLocation();
            if ( pPopup.x + dPopup.width > dList.width ) {
                pPopup.x -= ( dPopup.width + dParent.width );
            }
            if ( pPopup.y + pThis.y + dPopup.height > dList.height ) {
                pPopup.y -= ( dPopup.height - dParent.height + pThis.y );
            }
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
     * Handle the various actions for the menu items in this popup menu.
     */
    class ActionHandler implements ActionListener {
        public void actionPerformed( ActionEvent evt ) {
            String cmd = evt.getActionCommand();

            if ( "to-buffer".equals( cmd ) ) {
                // create a new untitled buffer and write the contents of the
                // tree to the buffer as text
                StringBuilder sb = new StringBuilder();
                TreeModel model = ( ( JTree ) comp ).getModel();
                DefaultMutableTreeNode root = ( DefaultMutableTreeNode ) model.getRoot();
                sb.append( root ).append( '\n' );
                Enumeration en = root.children();
                while ( en.hasMoreElements() ) {
                    DefaultMutableTreeNode fileNode = ( DefaultMutableTreeNode ) en.nextElement();
                    sb.append( fileNode ).append( '\n' );
                    Enumeration en2 = fileNode.children();
                    while ( en2.hasMoreElements() ) {
                        DefaultMutableTreeNode taskNode = ( DefaultMutableTreeNode ) en2.nextElement();
                        Task task = ( Task ) taskNode.getUserObject();
                        sb.append( "\t" ).append( task.getLineNumber() + 1 ).append( '\t' ).append( task.getText() ).append( '\n' );
                    }
                }
                Buffer buffer = jEdit.newFile( view );
                buffer.insert( 0, sb.toString() );
                return ;
            }

            Task task = getTask();
            String bufferPath = task == null ? getBufferPath() : task.getBufferPath();
            if ( bufferPath == null ) {
                return ;
            }
            Buffer buffer = jEdit.getBuffer( bufferPath );
            if ( buffer == null ) {
                buffer = jEdit.openTemporary( view, null, bufferPath, false );
                buffer.setMode();
            }

            if ( "parse-buffer".equals( cmd ) ) {
                TaskListPlugin.send( new ParseBufferMessage( view, buffer, ParseBufferMessage.DO_PARSE ) );
                return ;
            }
            else if ( "parse-all".equals( cmd ) ) {
                TaskListPlugin.send( new ParseBufferMessage( view, buffer, ParseBufferMessage.DO_PARSE_ALL ) );
                return ;
            }
            else if ( "sort-by-line-number".equals( cmd ) ) {
                jEdit.setIntegerProperty( "tasklist.table.sort-column", 1 );
                EditBus.send( new PropertiesChanged( null ) );
            }
            else if ( "sort-by-task-type".equals( cmd ) ) {
                jEdit.setIntegerProperty( "tasklist.table.sort-column", 2 );
                EditBus.send( new PropertiesChanged( null ) );
            }
            else if ( "sort-ascending".equals( cmd ) ) {
                jEdit.setBooleanProperty( "tasklist.table.sort-ascending", true );
                EditBus.send( new PropertiesChanged( null ) );
            }
            else if ( "sort-descending".equals( cmd ) ) {
                jEdit.setBooleanProperty( "tasklist.table.sort-ascending", false );
                EditBus.send( new PropertiesChanged( null ) );
            }
            else if ( task != null ) {
                if ( "%Dtask".equals( cmd ) ) {
                    TaskListPlugin.removeTask( view, buffer, task );
                }
                else if ( "%Dtag".equals( cmd ) ) {
                    TaskListPlugin.removeTag( view, buffer, task );
                }
                else {
                    TaskListPlugin.replaceTag( view, buffer, task, cmd );
                }
            }
        }
    }

    private Task getTask() {
        JTree tree = ( JTree ) comp;
        TreePath path = tree.getPathForLocation( point.x, point.y );
        if ( path == null ) {
            return null;
        }
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
        Object userObject = node.getUserObject();
        if ( userObject == null || !( userObject instanceof Task ) ) {
            return null;
        }
        Task task = ( Task ) userObject;
        return task;
    }

    private String getBufferPath() {
        JTree tree = ( JTree ) comp;
        TreePath path = tree.getPathForLocation( point.x, point.y );
        if ( path == null ) {
            return null;
        }
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
        if ( node == null ) {
            return null;
        }
        Object root = tree.getModel().getRoot();
        if ( node.equals( root ) ) {
            return null;
        }
        if ( node.getParent().equals( root ) ) {
            return node.getUserObject().toString();
        }
        return null;
    }
}