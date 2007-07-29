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
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
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
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.SVNURL;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

public class LogResultsPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z", Locale.getDefault() );
    private View view = null;
    private LogResults logResults = null;
    private String username = null;
    private String password = null;

    /**
     * @param map with path/file name as key, a list of associated log entries as the value
     * @param showPaths whether or not path information for other files associated with each
     * revision are included in the log entries
     */
    public LogResultsPanel( LogResults logResults, boolean showPaths, View view, String username, String password ) {
        super( new LambdaLayout() );
        this.view = view;
        this.logResults = logResults;
        this.username = username;
        this.password = password;
        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        boolean top = false;
        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "w";
        con.p = 0;

        TreeMap < String, List < SVNLogEntry >> results = logResults.getEntries();
        for ( String path : results.keySet() ) {
            JLabel label = new JLabel( "Path: " + path );

            // sort the entries
            List<SVNLogEntry> entries = results.get( path );
            Collections.sort( entries, new EntryComparator() );

            // put the results data into an array to pass to a JTable
            String[][] data = new String[ entries.size() ][ showPaths ? 5 : 4 ];
            Iterator it = entries.iterator();
            for ( int i = 0; it.hasNext(); i++ ) {
                SVNLogEntry entry = ( SVNLogEntry ) it.next();
                String revision = String.valueOf( entry.getRevision() );
                String date = DATE_FORMAT.format( entry.getDate() );
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
            LogTable table = new LogTable( data, col_names );
            table.setPath( path );
            table.addMouseListener( new TableMouseListener( table ) );
            ToolTipManager.sharedInstance().registerComponent( table );

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

            add( label, con );
            ++con.y;
            add( GUIUtils.createTablePanel( table ), con );
            ++con.y;
            add( LambdaLayout.createVerticalStrut( 11, true ), con );
            ++con.y;
        }
    }

    public class LogTable extends JTable {
        private String path = null;
        public LogTable( Object[][] data, Object[] columnNames ) {
            super( data, columnNames );
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
    public class TextCellRenderer implements TableCellRenderer {
        private MeasurableTextArea textArea = new MeasurableTextArea();

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            textArea.setText( value == null ? "" : value.toString() );
            table.setRowHeight( row, Math.max( textArea.getBestHeight(), table.getRowHeight() ) );
            textArea.setBackground( isSelected ? Color.LIGHT_GRAY : Color.WHITE );
            return textArea;
        }
    }

    /**
     * Non-wrapping text area cell renderer for the paths column.
     */
    public class PathCellRenderer implements TableCellRenderer {
        private MeasurableTextArea textArea = new MeasurableTextArea();

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            textArea.setText( value == null ? "" : value.toString() );
            textArea.setToolTipText( "<html><b>Other files in this revision:</b><br><pre>" + textArea.getText() );
            textArea.setBackground( isSelected ? Color.LIGHT_GRAY : Color.WHITE );
            table.setRowHeight( row, Math.max( textArea.getBestHeight(), table.getRowHeight() ) );
            return textArea;
        }
    }

    /**
     * Wrapping text area cell renderer.
     */
    public class CommentCellRenderer implements TableCellRenderer {
        private MeasurableTextArea textArea = new MeasurableTextArea();

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            textArea.setText( value == null ? "" : value.toString() );
            textArea.setLineWrap( true );
            textArea.setWrapStyleWord( true );
            textArea.setBackground( isSelected ? Color.LIGHT_GRAY : Color.WHITE );
            table.setRowHeight( row, Math.max( textArea.getBestHeight(), table.getRowHeight() ) );
            return textArea;
        }
    }

    public class MeasurableTextArea extends JTextArea {
        public int getBestHeight() {
            int best_height = getMinimumSize().height;
            return best_height;
        }
    }

    /**
     * for sorting log entries by revision number, latest revision first
     */
    public class EntryComparator implements Comparator<SVNLogEntry> {
        public int compare( SVNLogEntry o1, SVNLogEntry o2 ) {
            Long l1 = new Long( o1.getRevision() );
            Long l2 = new Long( o2.getRevision() );
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
                JPopupMenu popup = getPopupMenu( table );
                if ( popup != null ) {
                    GUIUtilities.showPopupMenu( popup, table, me.getX(), me.getY() );
                }
            }
        }
    }

    /**
     * Create the context menu.
     */
    private JPopupMenu getPopupMenu( final LogTable table ) {
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
                                    UndeleteDialog dialog = new UndeleteDialog( view, deleted_files );
                                    GUIUtils.center( view, dialog );
                                    dialog.setVisible( true );
                                    SVNData data = dialog.getData();
                                    SVNInfo info = logResults.getInfo();
                                    String rep_url_string = info.getRepositoryRootURL().toString();
                                    String file_url_string = info.getURL().toString();
                                    String path = file_url_string.substring( rep_url_string.length() );
                                    String project_root = PVHelper.getProjectRoot(view);
                                    String revision = ( String ) table.getValueAt( rows[ 0 ], 0 );
                                    for ( String remote_filename : data.getPaths() ) {
                                        SVNURL rep_url = SVNURL.parseURIDecoded( rep_url_string + remote_filename );
                                        String local_filename = rep_url.toString().substring(file_url_string.length());
                                        CopyData copy_data = new CopyData();
                                        copy_data.setSourceURL(rep_url);
                                        copy_data.setRevision(SVNRevision.create(Long.parseLong(revision) - 1));
                                        copy_data.setDestinationFile(new File(project_root + local_filename));
                                        CopyAction action = new CopyAction(view, copy_data);
                                        action.actionPerformed(ae);
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
        if ( popup.getComponentCount() == 0 && rows.length != 2 ) {
            return null;
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
        return popup;
    }
}
