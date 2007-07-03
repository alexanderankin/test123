package ise.plugin.svn.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.EmptyBorder;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.action.*;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.data.UpdateData;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * Shows the results of an update.  Conflicted files, updated files,
 * added files, and deleted files are shown in separate tables.
 */
public class UpdateResultsPanel extends JPanel {

    private View view = null;
    private UpdateData data = null;
    private JPopupMenu popupMenu = null;

    public UpdateResultsPanel( View view, UpdateData results ) {
        super( new LambdaLayout() );
        this.view = view;
        this.data = results;

        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );

        JLabel label = new JLabel( "Updated to revision: " + results.getRevision() );

        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.y = 0;
        con.s = "wh";
        con.p = 3;


        add( label, con );

        boolean added = false;

        List<String> list = results.getConflictedFiles();
        if ( list != null ) {
            ++con.y;
            add( createPanel( "Files with conflicts:", list ), con );
            added = true;
        }

        list = results.getUpdatedFiles();
        if ( list != null ) {
            ++con.y;
            add( createPanel( "Updated files:", list ), con );
            added = true;
        }

        list = results.getAddedFiles();
        if ( list != null ) {
            ++con.y;
            add( createPanel( "Added files:", list ), con );
            added = true;
        }

        list = results.getDeletedFiles();
        if ( list != null ) {
            ++con.y;
            add( createPanel( "Deleted files:", list ), con );
            added = true;
        }

        if ( !added ) {
            label.setText( label.getText() + " (Already up to date.)" );
        }
    }

    private JPanel createPanel( String title, List<String> values ) {
        JLabel label = new JLabel( title );
        String[][] data = new String[ values.size() ][ 1 ];
        for ( int i = 0; i < values.size(); i++ ) {
            data[ i ][ 0 ] = values.get( i );
        }
        JTable table = new JTable( data, new String[] {"Path:"} );
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( new EtchedBorder() );
        panel.add( label, BorderLayout.NORTH );
        panel.add( GUIUtils.createTablePanel( table ), BorderLayout.CENTER );
        return panel;
    }

    /**
     * MouseListener to popup context menu on the tree.
     */
    class TableMouseListener extends MouseAdapter {

        private JTable table = null;

        public TableMouseListener( JTable table ) {
            this.table = table;
        }

        public void mouseReleased( MouseEvent me ) {
            handleClick( me );
        }

        public void mousePressed( MouseEvent me ) {
            handleClick( me );
        }

        private void handleClick( MouseEvent me ) {
            if ( me.isPopupTrigger() ) {
                if ( table.getSelectedRows().length == 0 ) {
                    int row = table.rowAtPoint( me.getPoint() );
                    int col = table.columnAtPoint( me.getPoint() );
                    table.setRowSelectionInterval( row, row );
                    table.setColumnSelectionInterval( col, col );
                }
                GUIUtilities.showPopupMenu( popupMenu, UpdateResultsPanel.this, me.getX(), me.getY() );
            }
        }
    }

    /**
     * Create the context menu.
     */
    private JPopupMenu createPopupMenu( final JTable table ) {
        // update, commit, revert, add, log, need to add others as appropriate
        final JPopupMenu pm = new JPopupMenu();

        JMenuItem mi = new JMenuItem( "Info" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        int[] rows = table.getSelectedRows();
                        List<String> paths = new ArrayList<String>();
                        for ( int row : rows ) {
                            String path = ( String ) table.getValueAt( row, 0 );
                            if ( path == null || path.length() == 0 ) {
                                continue ;
                            }
                            paths.add( path );
                        }
                        InfoAction action = new InfoAction( view, paths, data.getUsername(), data.getPassword() );
                        action.actionPerformed( ae );
                    }
                }
                            );

        mi = new JMenuItem( "Log" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        int[] rows = table.getSelectedRows();
                        List<String> paths = new ArrayList<String>();
                        for ( int row : rows ) {
                            String path = ( String ) table.getValueAt( row, 0 );
                            if ( path == null || path.length() == 0 ) {
                                continue ;
                            }
                            paths.add( path );
                        }
                        LogAction action = new LogAction( view, paths, data.getUsername(), data.getPassword() );
                        action.actionPerformed( ae );
                    }
                }
                            );
        return pm;
    }
}
