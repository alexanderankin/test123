/*
* TaskList.java - TaskList plugin
* Copyright (C) 2001,2002 Oliver Rutherfurd
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
*
* $Id$
*/

package tasklist;

//{{{ imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import common.gui.CloseableTabbedPane;
//}}}

/**
 * A dockable component contaning a scrollable table; the table contains
 * data on task items found by parsing one or more buffers.
 *
 * @author Oliver Rutherfurd
 */
public class TaskList extends JPanel implements EBComponent {

    private View view = null;
    private CloseableTabbedPane tabs = null;
    private boolean showOpenFiles = true;
    private boolean showProjectFiles = true;

    //{{{ constructor
    /**
     * Constructor
     *
     * @param view The view in which the TaskList component will appear
     */
    public TaskList( View view ) {
        this.view = view;
        init();
        EditBus.addToBus( this );
        TaskListPlugin.registerTaskList( this );
    } //}}}

    private void init() {
        // this method can be called on property change, which can change the
        // layout of this panel, so remove all child components re-install them.
        removeAll();

        setLayout( new BorderLayout() );

        showOpenFiles = jEdit.getBooleanProperty( "tasklist.show-open-files", true );
        showProjectFiles = jEdit.getBooleanProperty( "tasklist.show-project-files", true );
        boolean projectViewerAvailable = PVHelper.isProjectViewerAvailable();

        tabs = new CloseableTabbedPane();
        Icon close_icon = GUIUtilities.loadIcon( "closebox.gif" );
        Icon hover_icon = GUIUtilities.loadIcon( "closebox.gif" );
        Icon pressed_icon = GUIUtilities.loadIcon( "closebox.gif" );
        tabs.setCloseIcons( close_icon, hover_icon, pressed_icon );

        // add a mouse listener to be able to close tabs
        tabs.addMouseListener(
            new MouseAdapter() {
                public void mousePressed( MouseEvent me ) {
                    if ( me.isPopupTrigger() ) {
                        handleIsPopup( me );
                    }
                }

                public void mouseReleased( MouseEvent me ) {
                    if ( me.isPopupTrigger() ) {
                        handleIsPopup( me );
                    }
                }

                private void handleIsPopup( MouseEvent me ) {
                    final int x = me.getX();
                    final int y = me.getY();
                    int index = tabs.indexAtLocation( x, y );
                    if ( index < 1 || (index < 2 && (showOpenFiles || showProjectFiles)) || (index < 3 && showOpenFiles && showProjectFiles )) {
                        // index 0 is the current buffer, don't close it ever,
                        // less than 0 is an invalid tab
                        return ;
                    }
                    final Component c = tabs.getComponentAt( index );
                    final JPopupMenu pm = new JPopupMenu();
                    JMenuItem close_mi = new JMenuItem( jEdit.getProperty( "tasklist.close", "Close" ) );
                    pm.add( close_mi );
                    close_mi.addActionListener(
                        new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                tabs.remove( c );
                            }
                        }
                    );
                    JMenuItem close_all_mi = new JMenuItem( jEdit.getProperty( "tasklist.close-all", "Close All" ) );
                    pm.add( close_all_mi );
                    close_all_mi.addActionListener(
                        new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                int start_index = 1;
                                if (showOpenFiles) {
                                    ++start_index;
                                }
                                if (showProjectFiles) {
                                    ++start_index;
                                }
                                for ( int i = start_index; i < tabs.getTabCount(); ) {
                                    Component comp = tabs.getComponentAt( i );
                                    tabs.remove( comp );
                                    comp = null;
                                }
                            }
                        }
                    );
                    GUIUtilities.showPopupMenu( pm, tabs, x, y );
                }
            }
        );

        addTab( jEdit.getProperty( "tasklist.current-file", "Current File" ), new CurrentBufferTaskList( view ) );
        if ( showOpenFiles ) {
            addTab( jEdit.getProperty( "tasklist.open-files", "Open Files" ), new OpenBuffersTaskList( view ) );
        }
        if ( showProjectFiles && projectViewerAvailable ) {
            addTab( jEdit.getProperty( "tasklist.project-files", "Project Files" ), new ProjectTaskList( view ) );
        }
        add( BorderLayout.CENTER, tabs );
        tabs.setSelectedIndex( 0 );
    }

    public void addTab( String name, AbstractTreeTaskList tasklist ) {
        tabs.add( name, tasklist );
        tabs.setSelectedIndex( tabs.getTabCount() - 1 );
    }

    //{{{ getName() method
    /**
     * Property accessor required by jEdit Plugin API
     * @return The plugin's name property
     */
    public String getName() {
        return "tasklist";
    } //}}}

    //{{{ getComponent() method
    /**
     * Property accessor required by jEdit Plugin API
     * @return A reference to the TaskList object
     */
    public Component getComponent() {
        return this;
    } //}}}

    public View getView() {
        return view;
    }

    // add or remove tabs based on property settings
    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof PropertiesChanged ) {
            boolean sof = jEdit.getBooleanProperty( "tasklist.show-open-files", true );
            boolean spf = jEdit.getBooleanProperty( "tasklist.show-project-files", true );
            if ( sof != showOpenFiles || spf != showProjectFiles ) {
                init();
            }
        }
    }
}