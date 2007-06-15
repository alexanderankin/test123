package ise.plugin.svn.gui;

import java.util.*;
import javax.swing.*;
import ise.plugin.svn.data.AddResults;
import ise.java.awt.LambdaLayout;

/**
 * Used for both Add and Revert.
 */
public class AddResultsPanel extends JPanel {
    public AddResultsPanel( AddResults results, boolean forAdd ) {
        super( new LambdaLayout() );
        boolean top = false;
        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "wh";
        con.p = 3;
        // show paths scheduled for add
        List<String> paths = results.getPaths();
        if ( paths != null && paths.size() > 0 ) {
            JLabel good_label = new JLabel( forAdd ? "Scheduled for add:" : "Reverted:" );
            JTable good_table = new JTable( paths.size(), 1 );
            for ( int i = 0; i < paths.size(); i++ ) {
                good_table.setValueAt( paths.get( i ), i, 0 );
            }
            add( good_label, con );
            ++con.y;
            add( good_table, con );
            top = true;
        }

        // show paths that had a problem
        Map<String, String> error_map = results.getErrorPaths();
        if ( error_map != null && error_map.size() > 0 ) {
            JLabel bad_label = new JLabel( forAdd ? "Unable to scheduled for add:" : "Unable to revert:");
            String[][] data = new String[ error_map.size() ][ 2 ];
            Iterator it = error_map.keySet().iterator();
            for ( int i = 0; it.hasNext(); i++ ) {
                String path = ( String ) it.next();
                String msg = ( String ) error_map.get( path );
                data[ i ][ 0 ] = path;
                data[ i ][ 1 ] = msg;
            }
            JTable bad_table = new JTable( data, new String[] {"Path", "Error Message"} );

            if ( top ) {
                ++con.y;
                add( LambdaLayout.createVerticalStrut( 6, true ), con );
            }
            ++con.y;
            add( bad_label, con );
            ++con.y;
            add( new JScrollPane( bad_table ), con );
        }

    }
}
