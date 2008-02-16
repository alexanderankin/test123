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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.PVHelper;
import ise.plugin.svn.action.CopyAction;
import ise.plugin.svn.action.DiffAction;
import ise.plugin.svn.data.CopyData;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.LogResults;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.TableCellViewer;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.SVNURL;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

/**
 * Shows the results of an svn log command.  The display shows 4 or optionally 5
 * columns, with the 5th column being the paths of other files committed in the
 * same revision.
 */
public class LogResultsPanel extends JPanel {

    private View view = null;
    private LogResults logResults = null;
    private String username = null;
    private String password = null;

    /**
     * @param map with path/file name as key, a list of associated log entries as the value
     * @param showPaths whether or not path information for other files associated with each
     * revision are included in the log entries
     * @param view the parent frame
     * @param username not used here, but passed on to commands available in the context menu
     * @param password password for username
     */
    public LogResultsPanel( LogResults logResults, boolean showPaths, View view, String username, String password ) {
        super( new LambdaLayout() );
        this.view = view;
        this.logResults = logResults;
        this.username = username;
        this.password = password;
        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "w";
        con.p = 0;

        TreeMap < String, List < SVNLogEntry >> results = logResults.getEntries();
        Set < Map.Entry < String, List < SVNLogEntry >>> set = results.entrySet();
        for ( Map.Entry < String, List < SVNLogEntry >> me : set ) {
            String path = ( String ) me.getKey();
            JLabel label = new JLabel( "Path: " + path );

            // sort the entries
            List<SVNLogEntry> entries = me.getValue();
            if (entries == null) {
                continue;
            }
            Collections.sort( entries, new EntryComparator() );

            // put the results data into an array to pass to a JTable. Columns
            // are revision, data, author, comment, and associated files.
            String[][] data = new String[ entries.size() ][ showPaths ? 5 : 4 ];
            Iterator it = entries.iterator();
            for ( int i = 0; it.hasNext(); i++ ) {
                SVNLogEntry entry = ( SVNLogEntry ) it.next();
                String revision = String.valueOf( entry.getRevision() );
                String date = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z", Locale.getDefault() ).format( entry.getDate() );
                String author = entry.getAuthor();
                String comment = entry.getMessage();
                data[ i ][ 0 ] = revision;
                data[ i ][ 1 ] = date;
                data[ i ][ 2 ] = author;
                data[ i ][ 3 ] = comment;

                StringBuffer associated_files;      // Perforce calls this a "changelist"
                if ( showPaths && entry.getChangedPaths().size() > 0 ) {
                    associated_files = new StringBuffer();
                    String ls = System.getProperty( "line.separator" );
                    // entry.changedPaths has the path as a string as the key,
                    // and an SVNLogEntryPath as the value
                    Set changedPaths = entry.getChangedPaths().keySet();
                    for ( Iterator iter = changedPaths.iterator(); iter.hasNext(); ) {
                        String cp = ( String ) iter.next();
                        SVNLogEntryPath lep = ( SVNLogEntryPath ) entry.getChangedPaths().get( cp );
                        if ( lep != null ) {
                            // type is one of A (added), M (modified), D (deleted), or R (replaced)
                            // show this along with the path
                            char type = lep.getType();
                            associated_files.append( type ).append( " " );
                        }
                        associated_files.append( cp ).append( ls );
                    }
                    data[ i ][ 4 ] = associated_files.toString();
                }
            }
            String[] col_names = showPaths ?
                    new String[] {"Revision", "Date", "Author", "Comment", "Paths"} :
                    new String[] {"Revision", "Date", "Author", "Comment"};
            final LogTable table = new LogTable( data, col_names );
            table.setPath( path );
            table.addMouseListener( new TableMouseListener( table ) );
            //ToolTipManager.sharedInstance().registerComponent( table );

            // set column widths and cell renderers
            TableColumnModel column_model = table.getColumnModel();
            TableColumn column0 = column_model.getColumn( 0 );  // revision
            column0.setMaxWidth( 60 );
            column0.setPreferredWidth( 60 );
            column0.setCellRenderer( new TextCellRenderer() );
            TableColumn column1 = column_model.getColumn( 1 );  // date
            column1.setMaxWidth( 190 );
            column1.setPreferredWidth( 190 );
            column1.setCellRenderer( new TextCellRenderer() );
            TableColumn column2 = column_model.getColumn( 2 );  // author
            column2.setMaxWidth( 100 );
            column2.setPreferredWidth( 100 );
            column2.setCellRenderer( new TextCellRenderer() );
            TableColumn column3 = column_model.getColumn( 3 );  // comment
            column3.setCellRenderer( new CommentCellRenderer() );
            if ( showPaths ) {
                TableColumn column4 = column_model.getColumn( 4 );    // paths
                column4.setCellRenderer( new PathCellRenderer() );
            }

            table.packRows();
            table.getColumnModel().addColumnModelListener(
                new TableColumnModelListener() {
                    public void columnAdded( TableColumnModelEvent e ) {}
                    public void columnMarginChanged( ChangeEvent e ) {
                        table.packRows();
                    }
                    public void columnMoved( TableColumnModelEvent e ) {
                        table.packRows();
                    }
                    public void columnRemoved( TableColumnModelEvent e ) {}
                    public void columnSelectionChanged( ListSelectionEvent e ) {}
                }
            );

            add( label, con );
            ++con.y;
            add( GUIUtils.createTablePanel( table ), con );
            ++con.y;
            add( LambdaLayout.createVerticalStrut( 11, true ), con );
            ++con.y;
        }
    }

    /**
     * Extends JTable to add a path name associated with the table.
     */
    public static class LogTable extends BestRowTable {

        private String path = null;

        public LogTable( String[][] rowData, String[] columnNames ) {
            super( rowData, columnNames );
        }

        public void setPath( String path ) {
            LogTable.this.path = path;
        }

        public String getPath() {
            return LogTable.this.path;
        }
    }

    /**
     * Non-wrapping text area cell renderer.
     */
    public static class TextCellRenderer extends JTextArea implements TableCellRenderer {
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            setText( value == null ? "" : value.toString() );
            setBackground( isSelected ? Color.LIGHT_GRAY : Color.WHITE );
            return this;
        }
    }

    /**
     * Non-wrapping text area cell renderer for the paths column.
     */
    public static class PathCellRenderer extends JTextArea implements TableCellRenderer {
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            setText( value == null ? "" : value.toString() );
            //setToolTipText( "<html><b>Other files in this revision:</b><br><pre>" + getText() );
            setBackground( isSelected ? Color.LIGHT_GRAY : Color.WHITE );
            return this;
        }
    }

    /**
     * Wrapping text area cell renderer.
     */
    public static class CommentCellRenderer extends JTextPane implements TableCellRenderer {
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            setText( value == null ? "" : value.toString() );
            setBackground( isSelected ? Color.LIGHT_GRAY : Color.WHITE );
            return this;
        }
    }

    /**
     * for sorting log entries by revision number, latest revision first
     */
    public static class EntryComparator implements Comparator<SVNLogEntry>, Serializable {
        public int compare( SVNLogEntry o1, SVNLogEntry o2 ) {
            Long l1 = Long.valueOf( o1.getRevision() );
            Long l2 = Long.valueOf( o2.getRevision() );
            return l2.compareTo( l1 );
        }
    }

    /**
     * MouseListener to popup context menu on the table.
     */
    class TableMouseListener extends MouseAdapter {
        private LogTable table = null;
        public TableMouseListener( LogTable table ) {
            TableMouseListener.this.table = table;
        }

        public void mouseReleased( MouseEvent me ) {
            handleClick( me );
        }

        public void mousePressed( MouseEvent me ) {
            handleClick( me );
        }

        private void handleClick( MouseEvent me ) {
            if ( me.isPopupTrigger() ) {
                Point p = me.getPoint();
                int col = table.columnAtPoint( p );
                int row = table.rowAtPoint( p );
                JPopupMenu popup = getPopupMenu( table, col, row, me.getX(), me.getY() );
                if ( popup != null ) {
                    GUIUtilities.showPopupMenu( popup, table, me.getX(), me.getY() );
                }
            }
        }
    }

    /**
     * Create the context menu.
     */
    private JPopupMenu getPopupMenu( final LogTable table, final int col, final int row, final int x, final int y) {
        final int[] rows = table.getSelectedRows();
        JPopupMenu popup = new JPopupMenu();

        // if displaying paths, check the individual paths for those starting
        // with "D", which indicates a deleted file.  Show "Undelete" menu item
        // for such paths.
        if ( rows.length == 1 && table.getColumnCount() == 5 ) {
            String path_value = ( String ) table.getValueAt( rows[ 0 ], 4 );
            String[] paths = path_value.split( System.getProperty( "line.separator" ) );
            final List<String> deleted_files = new ArrayList<String>();
            for ( String p : paths ) {
                if ( p != null && p.startsWith( "D " ) ) {
                    // found a deleted file, add it to the list
                    deleted_files.add( p.substring( 2 ) );
                }
            }
            if ( !deleted_files.isEmpty() ) {
                JMenuItem mi = new JMenuItem( "Undelete" );
                popup.add( mi );
                mi.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                try {
                                    // have the user select which files to undelete
                                    UndeleteDialog dialog = new UndeleteDialog( view, deleted_files );
                                    GUIUtils.center( view, dialog );
                                    dialog.setVisible( true );
                                    SVNData data = dialog.getData();
                                    if ( data == null ) {
                                        return ;     // null means user canceled
                                    }

                                    // get the repository url and filename of the file to recover
                                    SVNInfo info = logResults.getInfo();
                                    String rep_url_string = info.getRepositoryRootURL().toString();
                                    String file_url_string = info.getURL().toString();

                                    // get the revision to undelete
                                    String revision = ( String ) table.getValueAt( rows[ 0 ], 0 );

                                    // get project root to use as base directory for local destination
                                    String project_root = PVHelper.getProjectRoot( view );

                                    // do the undelete
                                    for ( String remote_filename : data.getPaths() ) {
                                        // remote file
                                        SVNURL rep_url = SVNURL.parseURIDecoded( rep_url_string + remote_filename );

                                        // local filename, extract from remote name
                                        String local_filename = rep_url.toString().substring( file_url_string.length() );

                                        // prep for copy
                                        CopyData copy_data = new CopyData();
                                        copy_data.setSourceURL( rep_url );      // what to copy
                                        copy_data.setRevision( SVNRevision.create( Long.parseLong( revision ) - 1 ) );  // at what revision
                                        copy_data.setDestinationFile( new File( project_root + local_filename ) );  // where to put it

                                        // do the copy
                                        CopyAction action = new CopyAction( view, copy_data );
                                        action.actionPerformed( ae );
                                    }
                                }
                                catch ( Exception e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                                    );

            }
        }
        if ( rows.length == 2 ) {
            final String path = table.getPath();
            JMenuItem mi = new JMenuItem( "Diff" );
            popup.add( mi );
            mi.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent ae ) {
                            int[] rows = table.getSelectedRows();
                            String revision1 = ( String ) table.getValueAt( rows[ 0 ], 0 );
                            String revision2 = ( String ) table.getValueAt( rows[ 1 ], 0 );
                            DiffAction action = new DiffAction( view, path, revision1, revision2, username, password );
                            action.actionPerformed( ae );
                        }
                    }
                                );
        }
        //popup.addSeparator();
        JMenuItem mi = new JMenuItem("Zoom");
        mi.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    TableCellViewer viewer = new TableCellViewer(table);
                    viewer.doPopup(col, row, x, y);
                }
        });
        popup.add(mi);

        return popup;
    }
}
