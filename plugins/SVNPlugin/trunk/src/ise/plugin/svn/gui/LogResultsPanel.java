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
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;

import ise.java.awt.LambdaLayout;
import ise.plugin.svn.PVHelper;
import ise.plugin.svn.action.CopyAction;
import ise.plugin.svn.action.DiffAction;
import ise.plugin.svn.data.CopyData;
import ise.plugin.svn.data.DiffData;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.LogResults;
import ise.plugin.svn.library.GUIUtils;
//import ise.plugin.svn.library.Logger;
import ise.plugin.svn.library.TableCellViewer;
import ise.plugin.svn.data.PropertyData;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.command.Property;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.SVNURL;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

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

    private static Color background = jEdit.getColorProperty( "view.bgColor", Color.WHITE );
    private static Color foreground = jEdit.getColorProperty( "view.fgColor", Color.BLACK );
    //private static Color selection = jEdit.getColorProperty("view.selectionColor", Color.LIGHT_GRAY);

    private Properties bugtraqProperties = null;
    private String logRegex0 = null;
    private String logRegex1 = null;

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
        if ( logResults == null ) {
            return ;
        }
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
            if ( bugtraqProperties == null ) {
                loadCommitProperties( path );
            }

            JLabel label = new JLabel( jEdit.getProperty( "ips.Path>", "Path:" ) + " " + path );

            // sort the entries
            List<SVNLogEntry> entries = me.getValue();
            if ( entries == null ) {
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
                String date = entry.getDate() != null ? new SimpleDateFormat( jEdit.getProperty( "ips.yyyy-MM-dd_HH>mm>ss_Z", "yyyy-MM-dd HH:mm:ss Z" ), Locale.getDefault() ).format( entry.getDate() ) : "---";
                String author = entry.getAuthor();
                String comment = entry.getMessage();
                data[ i ][ 0 ] = revision;
                data[ i ][ 1 ] = date;
                data[ i ][ 2 ] = author;
                data[ i ][ 3 ] = comment;

                StringBuffer associated_files;      // Perforce calls this a "changelist"
                if ( showPaths && entry.getChangedPaths() != null && entry.getChangedPaths().size() > 0 ) {
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
                            associated_files.append( type ).append( ' ' ).append( cp );
                            associated_files.append( ( lep.getCopyPath() != null ) ? " (from "
                                    + lep.getCopyPath() + " revision "
                                    + lep.getCopyRevision() + ")" : "" ).append( ls );
                        }
                        else {
                            associated_files.append( cp ).append( ls );
                        }
                    }
                    data[ i ][ 4 ] = associated_files.toString();
                }
            }
            String[] col_names = showPaths ?
                    new String[] {jEdit.getProperty( "ips.Revision", "Revision" ), jEdit.getProperty( "ips.Date", "Date" ), jEdit.getProperty( "ips.Author", "Author" ), jEdit.getProperty( "ips.Comment", "Comment" ), jEdit.getProperty( "ips.Paths", "Paths" ) } :
                    new String[] {jEdit.getProperty( "ips.Revision", "Revision" ), jEdit.getProperty( "ips.Date", "Date" ), jEdit.getProperty( "ips.Author", "Author" ), jEdit.getProperty( "ips.Comment", "Comment" ) };
            final LogTable table = new LogTable( data, col_names );
            table.setPath( path );
            table.addMouseListener( new TableMouseListener( table ) );
            //ToolTipManager.sharedInstance().registerComponent( table );

            // set column widths and cell renderers
            TableColumnModel column_model = table.getColumnModel();
            TableColumn column0 = column_model.getColumn( 0 );  // revision
            column0.setMaxWidth( 60 );
            column0.setPreferredWidth( 60 );
            column0.setCellRenderer( new NoWrapCellRenderer() );
            TableColumn column1 = column_model.getColumn( 1 );  // date
            column1.setMaxWidth( 190 );
            column1.setPreferredWidth( 190 );
            column1.setCellRenderer( new NoWrapCellRenderer() );
            TableColumn column2 = column_model.getColumn( 2 );  // author
            column2.setMaxWidth( 100 );
            column2.setPreferredWidth( 100 );
            column2.setCellRenderer( new NoWrapCellRenderer() );
            TableColumn column3 = column_model.getColumn( 3 );  // comment
            WrapCellRenderer commentRenderer = new WrapCellRenderer();
            commentRenderer.setSearcher( new WordSearcher( commentRenderer, logRegex0, logRegex1 ) );
            column3.setCellRenderer( commentRenderer );
            if ( showPaths ) {
                TableColumn column4 = column_model.getColumn( 4 );    // paths
                column4.setCellRenderer( new NoWrapCellRenderer() );
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
            setBackground( LogResultsPanel.background );
            setForeground( LogResultsPanel.foreground );
        }

        public void setPath( String path ) {
            LogTable.this.path = path;
        }

        public String getPath() {
            return LogTable.this.path;
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
                    GUIUtils.showPopupMenu( popup, table, me.getX(), me.getY() );
                }
            }
        }
    }

    /**
     * Create the context menu.
     */
    private JPopupMenu getPopupMenu( final LogTable table, final int col, final int row, final int x, final int y ) {
        final int[] rows = table.getSelectedRows();
        JPopupMenu popup = new JPopupMenu();

        // Undelete
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
                JMenuItem mi = new JMenuItem( jEdit.getProperty( "ips.Undelete", "Undelete" ) );
                popup.add( mi );
                mi.addActionListener(
                    new ActionListener() {
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

        // Diff
        if ( rows.length == 2 ) {
            final String path = table.getPath();
            JMenuItem mi = new JMenuItem( jEdit.getProperty( "ips.Diff", "Diff" ) );
            popup.add( mi );
            mi.addActionListener(
                new ActionListener() {
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

        // add menu item to diff against working copy
        if ( rows.length == 1 ) {
            final String path = table.getPath();
            JMenuItem mi = new JMenuItem( jEdit.getProperty( "ips.Diff_against_working_copy", "Diff against working copy" ) );
            popup.add( mi );
            mi.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        int[] rows = table.getSelectedRows();
                        DiffData data = new DiffData();
                        List<String> paths = new ArrayList<String>();
                        paths.add( path );
                        data.setPaths( paths );
                        data.setUsername( username );
                        data.setPassword( password );
                        data.setRevision1( SVNRevision.WORKING );
                        data.setRevision2( SVNRevision.parse( ( String ) table.getValueAt( rows[ 0 ], 0 ) ) );
                        DiffAction action = new DiffAction( view, data );
                        action.actionPerformed( ae );
                    }
                }
            );
        }


        // Open browser
        if ( col == 3 ) {
            final List<String> urls = fetchUrl( ( String ) table.getValueAt( row, col ) );
            if ( urls != null ) {
                JMenuItem mi = new JMenuItem( urls.size() > 1 ? jEdit.getProperty( "ips.Open_links_in_browser", "Open links in browser" ) : jEdit.getProperty( "ips.Open_link_in_browser", "Open link in browser" ) );
                popup.add( mi );
                mi.addActionListener(
                    new ActionListener() {
                        public void actionPerformed( ActionEvent ae ) {
                            for ( String url : urls ) {
                                infoviewer.InfoViewerPlugin.openURL( view, url );
                            }
                        }
                    }
                );
            }
        }

        //popup.addSeparator();

        // Zoom
        JMenuItem mi = new JMenuItem( jEdit.getProperty( "ips.Zoom", "Zoom" ) );
        mi.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    TableCellViewer viewer = new TableCellViewer( table );
                    viewer.doPopup( col, row, x, y );
                }
            }
        );
        popup.add( mi );

        // TODO: add menu item to open file

        return popup;
    }

    private List<String> fetchUrl( String comment ) {
        if ( bugtraqProperties == null ) {
            return null;
        }
        String url = bugtraqProperties.getProperty( "bugtraq:url" );
        if ( url == null ) {
            return null;
        }
        String logregex = bugtraqProperties.getProperty( "bugtraq:logregex" );
        if ( logregex == null ) {
            // if no logregex, use bugtraq:message as the regex
            String regex = bugtraqProperties.getProperty( "bugtraq:message" );
            if ( regex == null ) {
                return null;
            }
            // example: Issue ID: %BUGID% More text
            // becomes: Issue ID: (.*?) More text$
            // The bugtraq standard says the message line will be on a single
            // line.  It will be either the first line or the last line,
            // depending on the value of bugtraq:append.
            regex = regex.replace( "%BUGID%", "(.*?)" ) + "$";
            Pattern p = Pattern.compile( regex, Pattern.DOTALL );
            Matcher m = p.matcher( comment );
            if ( m.find() ) {
                String bug_number = m.group( 1 );
                List<String> list = new ArrayList<String>();
                String[] bugs = bug_number.split( "," );
                for ( String bug : bugs ) {
                    list.add( url.replaceAll( "%BUGID%", bug ) );
                }
                return list;
            }
        }
        String regex0 = null;
        String regex1 = null;
        if ( logregex != null && logregex.length() > 0 ) {
            if ( logregex.indexOf( '\n' ) > 0 ) {
                String[] parts = logregex.split( "\n" );
                regex0 = parts[ 0 ];
                regex1 = parts[ 1 ];
            }
            else {
                regex0 = logregex;
            }
        }
        else {
            return null;
        }

        if ( regex1 == null || regex1.length() == 0 ) {
            // only have regex0 to find bug pattern
            Pattern p = Pattern.compile( regex0, Pattern.DOTALL );
            Matcher m = p.matcher( comment );
            if ( m.find() ) {
                int start = m.start();
                int end = m.end();
                List<String> list = new ArrayList<String>();
                String bug_number = comment.substring( start, end );
                String[] bugs = bug_number.split( "," );
                for ( String bug : bugs ) {
                    list.add( url.replaceAll( "%BUGID%", bug ) );
                }
                return list;
            }
        }
        else {
            // have both regex0 and regex1.  Use regex0 to find the bug id string,
            // then use regex1 to find the actual bug id within that string.
            Pattern p = Pattern.compile( regex0, Pattern.DOTALL );
            Matcher m = p.matcher( comment );
            if ( m.find() ) {
                int start = m.start();
                int end = m.end();
                String bug_string = comment.substring( start, end );
                p = Pattern.compile( regex1, Pattern.DOTALL );
                m = p.matcher( bug_string );
                if ( m.find() ) {
                    int s = m.start();
                    int e = m.end();
                    int length = e - s;
                    start += s;
                    end = start + length;
                    List<String> list = new ArrayList<String>();
                    String bug_number = comment.substring( start, start + length );
                    String[] bugs = bug_number.split( "," );
                    for ( String bug : bugs ) {
                        list.add( url.replaceAll( "%BUGID%", bug ) );
                    }
                    return list;
                }
            }
        }
        return null;
    }

    // load the tsvn and bugtraq properties associated with the given path.
    // This loads properties starting with tsvn: and bugtraq: from the given
    // path, then works up through parent paths until the project root is
    // reached.
    private void loadCommitProperties( String path ) {
        if ( path == null || path.length() == 0 ) {
            return ;
        }

        String projectRoot = PVHelper.getProjectRoot( path );
        if ( projectRoot == null || projectRoot.length() == 0 ) {
            return ;
        }
        bugtraqProperties = new Properties();
        do {

            PropertyData data = new PropertyData();
            data.setOut( new ConsolePrintStream( view ) );
            data.addPath( path );
            data.setPathsAreURLs( false );
            data.setRecursive( false );
            data.setAskRecursive( false );
            data.setRevision( SVNRevision.WORKING );
            data.setPegRevision( SVNRevision.UNDEFINED );
            String[] credentials = PVHelper.getSVNLogin( path );
            data.setUsername( credentials[ 0 ] );
            data.setPassword( credentials[ 1 ] );
            Property cmd = new Property();
            try {
                cmd.doGetProperties( data );
            }
            catch ( Exception e ) {

                // ProjectViewer allows adding paths to a project that are not
                // under the project root.  The typical use case is that all
                // files are under the root, so the 'while' loop makes sense. In
                // the case where a path is added to a project and that path is
                // not under the project root, eventually a parent path will not
                // be under version control and the Property command will throw
                // an exception, which is probably why flow ended up in this
                // catch block.  Do one more check for the properties at the
                // project root.
                path = projectRoot;
                continue;
            }
            if ( cmd.getProperties() != null ) {
                TreeMap map = cmd.getProperties();
                for ( Object key : map.keySet() ) {
                    Properties props = cmd.getProperties().get( key );
                    for ( Object name : props.keySet() ) {
                        if ( ( name.toString().startsWith( "tsvn:" ) || name.toString().startsWith( "bugtraq:" ) ) && !bugtraqProperties.containsKey( name ) ) {
                            bugtraqProperties.setProperty( name.toString(), props.getProperty( name.toString() ) );
                            //System.out.println( "+++++ " + name.toString() + " = " + props.getProperty( name.toString() ) );
                        }
                    }
                }
            }
            File f = new File( path );
            path = f.getParent();
        }
        while ( path.startsWith( projectRoot ) );

        String logregex = bugtraqProperties.getProperty( "bugtraq:logregex" );
        if ( logregex != null && logregex.length() > 0 ) {
            if ( logregex.indexOf( '\n' ) > 0 ) {
                String[] parts = logregex.split( "\n" );
                logRegex0 = parts[ 0 ];
                logRegex1 = parts[ 1 ];
            }
            else {
                logRegex0 = logregex;
            }
        }
        else {
            // if no logregex, use bugtraq:message as the regex
            String regex = bugtraqProperties.getProperty( "bugtraq:message" );
            if ( regex != null ) {
                logRegex0 = regex.replace( "%BUGID%", "(.*?)" ) + "$";
            }
        }
    }
}