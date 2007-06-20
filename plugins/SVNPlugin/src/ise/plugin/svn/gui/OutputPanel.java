package ise.plugin.svn.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;
import ise.plugin.svn.*;
import ise.plugin.svn.library.GUIUtils;

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
                        JMenuItem mi = new JMenuItem( "Close" );
                        pm.add( mi );
                        mi.addActionListener( new ActionListener() {
                                    public void actionPerformed( ActionEvent ae ) {
                                        tabs.remove( c );
                                    }
                                }
                                            );
                        GUIUtils.showPopupMenu( pm, c, x, y );
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
