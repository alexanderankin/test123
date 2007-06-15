package ise.plugin.svn.gui;

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


    public LogResultsPanel( TreeMap < String, List < SVNLogEntry >> results ) {
        super( new LambdaLayout() );
        setBorder(new EmptyBorder(3, 3, 3, 3));
        boolean top = false;
        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "w";
        con.p = 0;

        for ( String path : results.keySet() ) {
            JLabel label = new JLabel( "Path: " + path );
            List<SVNLogEntry> entries = results.get( path );
            Collections.sort(entries, new EntryComparator());
            String[][] data = new String[ entries.size() ][ 4 ];
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
            }
            JTable table = new JTable( data, new String[] {"Revision", "Date", "Author", "Comment"} );
            TableColumnModel column_model = table.getColumnModel();
            TableColumn column0 = column_model.getColumn(0);
            column0.setMaxWidth(60);
            column0.setPreferredWidth(60);
            TableColumn column1 = column_model.getColumn(1);
            column1.setMaxWidth(200);
            column1.setPreferredWidth(200);
            TableColumn column2 = column_model.getColumn(2);
            column2.setMaxWidth(180);
            column2.setPreferredWidth(180);
            add(label, con);
            ++con.y;
            add( GUIUtils.createTablePanel(table), con );
            ++con.y;
            add( LambdaLayout.createVerticalStrut( 11, true ), con );
            ++con.y;
        }
    }

    public class EntryComparator implements Comparator<SVNLogEntry> {
        public int compare(SVNLogEntry o1, SVNLogEntry o2) {
            Long l1 = new Long(o1.getRevision());
            Long l2 = new Long(o2.getRevision());
            return l2.compareTo(l1);
        }
    }
}
