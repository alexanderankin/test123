package ise.plugin.svn.gui;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.library.GUIUtils;
import org.tmatesoft.svn.core.SVNLogEntry;

public class LogResultsPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z", Locale.getDefault() );

    /**
     * @param map with path/file name as key, a list of associated log entries as the value
     * @param showPaths whether or not path information for other files associated with each
     * revision are included in the log entries
     */
    public LogResultsPanel( TreeMap < String, List < SVNLogEntry >> results, boolean showPaths ) {
        super( new LambdaLayout() );
        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        boolean top = false;
        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "w";
        con.p = 0;

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

                StringBuffer associated_files;
                if ( showPaths && entry.getChangedPaths().size() > 0 ) {
                    associated_files = new StringBuffer();
                    String ls = System.getProperty( "line.separator" );
                    Set changedPaths = entry.getChangedPaths().keySet();
                    for ( Iterator iter = changedPaths.iterator(); iter.hasNext(); ) {
                        String cp = ( String ) iter.next();
                        associated_files.append( cp ).append( ls );
                    }
                    data[ i ][ 4 ] = associated_files.toString();
                }
            }
            String[] col_names = showPaths ?
                    new String[] {"Revision", "Date", "Author", "Comment", "Paths"} :
                    new String[] {"Revision", "Date", "Author", "Comment"};
            JTable table = new JTable( data, col_names );
            ToolTipManager.sharedInstance().registerComponent(table);

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

    /**
     * Non-wrapping text area cell renderer.
     */
    public class TextCellRenderer implements TableCellRenderer {
        private MeasurableTextArea textArea = new MeasurableTextArea();

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            textArea.setText( value == null ? "" : value.toString() );
            table.setRowHeight( row, Math.max( textArea.getBestHeight(), table.getRowHeight() ) );
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
            textArea.setToolTipText("<html><b>Other files in this revision:</b><br><pre>" + textArea.getText());
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
}
