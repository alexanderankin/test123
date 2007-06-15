package ise.plugin.svn.gui;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.library.GUIUtils;
import org.tmatesoft.svn.core.SVNLogEntry;

public class UpdateResultsPanel extends JPanel {

    public UpdateResultsPanel( TreeMap < String, String > results ) {
        super( new LambdaLayout() );
        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );

        String[][] data = new String[ results.size() ][ 2 ];
        int i = 0;
        for ( String path : results.keySet() ) {
            String revision = results.get( path );
            data[ i ][ 0 ] = path;
            data[ i ][ 1 ] = revision;
        }

        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "w";
        con.p = 0;

        if (results == null || results.size() == 0) {
            add(new JLabel("Selected files are already up to date."), con);
            return;
        }

        JLabel label = new JLabel( "Updated: ");
        JTable table = new JTable( data, new String[] {"Path", "Revision"} );
        TableColumnModel column_model = table.getColumnModel();
        TableColumn column1 = column_model.getColumn( 1 );
        column1.setMaxWidth( 100 );
        column1.setPreferredWidth( 100 );
        add( label, con );
        ++con.y;
        add( GUIUtils.createTablePanel(table), con );
    }
}
