package ise.plugin.svn.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;
import ise.plugin.svn.*;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * Wraps a tabbed pane to show output.  There is always a 'console' tab that
 * shows raw output of the svn commands.  Each class in ise.plugin.svn.action
 * produces an output panel that is added as a tab to the tabbed pane in this
 * panel.  Tabs can be closed by right clicking on them, however, the 'console'
 * tab may not be closed.
 */
public class OutputPanel extends JPanel {

    private JTabbedPane tabs;

    // used to generate unique name for this panel
    public static int COUNT = 0;
    private int myIndex;

    // the logger for this panel, this logger should only log to this panel
    // via the handler
    private Logger logger = null;
    private SubversionGUILogHandler handler = new SubversionGUILogHandler();

    public OutputPanel() {
        super( new BorderLayout() );

        myIndex = ++COUNT;
        logger = Logger.getLogger( "ise.plugin.svn.Subversion" + myIndex );
        logger.setLevel( Level.ALL );
        logger.removeHandler( handler );
        logger.addHandler( handler );

        tabs = new JTabbedPane();
        tabs.addTab( "SVN Console", getConsolePanel() );
        add( tabs );

        // add a mouse listener to be able to close results tabs
        tabs.addMouseListener( new MouseAdapter() {
                    public void mousePressed( MouseEvent me ) {
                        if ( me.isPopupTrigger() )
                            handleIsPopup( me );
                    }

                    public void mouseReleased( MouseEvent me ) {
                        if ( me.isPopupTrigger() )
                            handleIsPopup( me );
                    }

                    private void handleIsPopup( MouseEvent me ) {
                        final int x = me.getX();
                        final int y = me.getY();
                        int index = tabs.indexAtLocation(x, y);
                        if (index < 1) {
                            // index 0 is the console, don't close it ever,
                            // less than 0 is an invalid tab
                            return;
                        }
                        final Component c = tabs.getComponentAt(index);
                        final JPopupMenu pm = new JPopupMenu();
                        JMenuItem close_mi = new JMenuItem( "Close" );
                        pm.add( close_mi );
                        close_mi.addActionListener( new ActionListener() {
                                    public void actionPerformed( ActionEvent ae ) {
                                        tabs.remove( c );
                                    }
                                }
                                            );
                        JMenuItem close_all_mi = new JMenuItem( "Close All" );
                        pm.add( close_all_mi );
                        close_all_mi.addActionListener( new ActionListener() {
                                    public void actionPerformed( ActionEvent ae ) {
                                        for (int i = 1; i < tabs.getTabCount(); ) {
                                            Component comp = tabs.getComponentAt(i);
                                            tabs.remove( comp );
                                            comp = null;
                                        }
                                    }
                                }
                                            );
                        GUIUtilities.showPopupMenu( pm, c, x, y );
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

    public void showConsole( ) {
        tabs.setSelectedIndex( 0 );
    }

    public void addTab( String name, JPanel panel ) {
        final Component c = tabs.add( name, new JScrollPane( panel ) );
        tabs.setSelectedComponent( c );
    }
}
