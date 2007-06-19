package ise.plugin.svn.gui;

import java.text.SimpleDateFormat;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.ListOps;
import ise.plugin.svn.data.CommitData;
import org.tmatesoft.svn.core.SVNCommitInfo;

public class CommitResultsPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z", Locale.getDefault() );


    public CommitResultsPanel( CommitData results ) {
        super( new LambdaLayout() );
        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "w";
        con.p = 0;

        JLabel label = new JLabel( "Committed:" );
        SVNCommitInfo info = results.getInfo();
        String revision = String.valueOf( info.getNewRevision() );
        String author = info.getAuthor();
        String date = DATE_FORMAT.format( info.getDate() );

        List<String> paths = ListOps.toList(ListOps.toSet(results.getPaths()));
        Collections.sort( paths );
        String[][] data = new String[ paths.size() ][ 4 ];
        int i = 0;
        for ( String path : paths ) {
            data[ i ][ 0 ] = path;
            data[ i ][ 1 ] = revision;
            data[ i ][ 2 ] = date;
            data[ i ][ 3 ] = author;
            ++i;
        }
        JTable table = new JTable( data, new String[] {"Path", "Revision", "Date", "Author"} );
        TableColumnModel column_model = table.getColumnModel();
        TableColumn column0 = column_model.getColumn( 1 );
        column0.setMaxWidth( 60 );
        column0.setPreferredWidth( 60 );
        TableColumn column1 = column_model.getColumn( 2 );
        column1.setMaxWidth( 200 );
        column1.setPreferredWidth( 200 );
        TableColumn column2 = column_model.getColumn( 3 );
        column2.setMaxWidth( 180 );
        column2.setPreferredWidth( 180 );


        add( label, con );
        ++con.y;
        add( GUIUtils.createTablePanel(table), con );
    }

}
