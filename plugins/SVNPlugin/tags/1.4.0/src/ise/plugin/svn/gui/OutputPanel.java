/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import ise.plugin.svn.*;
import ise.plugin.svn.library.GUIUtils;
import common.swingworker.SwingWorker;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import common.gui.CloseableTabbedPane;

/**
 * Wraps a tabbed pane to show output.  There is always a 'console' tab that
 * shows raw output of the svn commands.  Each class in ise.plugin.svn.action
 * produces an output panel that is added as a tab to the tabbed pane in this
 * panel.  Tabs can be closed by right clicking on them, however, the 'console'
 * tab may not be closed.
 */
public class OutputPanel extends JPanel {

    private CloseableTabbedPane tabs;

    // used to generate unique name for this panel
    static int COUNT = 0;
    private int myIndex;

    // the logger for this panel, this logger should only log to this panel
    // via the handler
    private transient Logger logger = null;
    private SubversionGUILogHandler handler = new SubversionGUILogHandler();


    public OutputPanel() {
        super( new BorderLayout() );

        myIndex = ++COUNT;
        logger = Logger.getLogger( "ise.plugin.svn.Subversion" + myIndex );
        logger.setLevel( Level.ALL );
        logger.removeHandler( handler );
        logger.addHandler( handler );

        tabs = new CloseableTabbedPane();
        Icon close_icon = GUIUtilities.loadIcon( "closebox.gif" );
        tabs.setCloseIcons(close_icon, close_icon, close_icon);
        tabs.addTab( "SVN Console", getConsolePanel() );
        add( tabs );

        // add a mouse listener to be able to close results tabs
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
                    int index = tabs.indexAtLocation(x, y);
                    if (index < 1) {
                        // index 0 is the console, don't close it ever,
                        // less than 0 is an invalid tab
                        return ;
                    }
                    final Component c = tabs.getComponentAt(index);
                    final JPopupMenu pm = new JPopupMenu();
                    JMenuItem close_mi = new JMenuItem( jEdit.getProperty("ips.Close", "Close") );
                    pm.add( close_mi );
                    close_mi.addActionListener(
                        new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                tabs.remove( c );
                            }
                        }
                    );
                    JMenuItem close_all_mi = new JMenuItem( jEdit.getProperty("ips.Close_All", "Close All") );
                    pm.add( close_all_mi );
                    close_all_mi.addActionListener(
                        new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                for (int i = 1; i < tabs.getTabCount(); ) {
                                    Component comp = tabs.getComponentAt(i);
                                    tabs.remove( comp );
                                    comp = null;
                                }
                            }
                        }
                    );
                    GUIUtils.showPopupMenu( pm, tabs, x, y );
                }
            }
        );
    }

    private JPanel getConsolePanel() {
        return handler.getPanel();
    }

    public Logger getLogger() {
        return logger;
    }

    public void showConsole() {
        tabs.setSelectedIndex( 0 );
    }

    /**
     * Automatically adds scroll bars to the given panel.
     * @param name the name for the tab
     * @param panel the panel to display in the tab.  Scrollbars will
     * automatically be added to the panel.
     */
    public void addTab( String name, JPanel panel ) {
        JScrollPane scroller = new JScrollPane( panel );
        JScrollBar bar = scroller.getVerticalScrollBar();
        bar.setUnitIncrement( 15 );
        final Component c = tabs.add( name, scroller );
        tabs.setSelectedComponent( c );
    }

    public void addWorker(String name, SwingWorker worker) {
        if (worker == null) {
            return ;
        }
        handler.getStopPanel().addWorker(name, worker);
    }
}