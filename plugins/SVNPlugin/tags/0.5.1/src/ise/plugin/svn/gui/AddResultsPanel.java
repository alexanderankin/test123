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

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.library.GUIUtils;
import ise.java.awt.LambdaLayout;

/**
 * Used for both Add and Revert, and now Delete and Resolved.
 */
public class AddResultsPanel extends JPanel {
    public static final int ADD = 0;
    public static final int REVERT = 1;
    public static final int DELETE = 2;
    public static final int RESOLVED = 3;

    public AddResultsPanel( AddResults results, int action ) {
        super( new LambdaLayout() );
        if ( action < 0 || action > 3 ) {
            throw new IllegalArgumentException( "invalid action: " + action );
        }
        boolean top = false;
        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.s = "wh";
        con.p = 3;

        // show paths scheduled for add
        List<String> paths = results.getPaths();
        if ( paths != null && paths.size() > 0 ) {
            JPanel top_panel = new JPanel( new BorderLayout() );
            String good_label_text = "";
            switch ( action ) {
                case ADD:
                    good_label_text = "Scheduled for add:";
                    break;
                case REVERT:
                    good_label_text = "Reverted:";
                    break;
                case DELETE:
                    good_label_text = "Scheduled for delete:";
                    break;
                case RESOLVED:
                    good_label_text = "Resolved:";
                    break;
            }
            JLabel good_label = new JLabel( good_label_text );

            String[][] data = new String[ paths.size() ][ 1 ];
            Iterator it = paths.iterator();
            for ( int i = 0; it.hasNext(); i++ ) {
                String path = ( String ) it.next();
                data[ i ][ 0 ] = path;
            }
            JTable good_table = new JTable( data, new String[] {"Path"} );

            top_panel.add( good_label, BorderLayout.NORTH );
            top_panel.add( GUIUtils.createTablePanel( good_table ), BorderLayout.CENTER );
            add( top_panel, con );
            top = true;
        }

        // show paths that had a problem
        Map<String, String> error_map = results.getErrorPaths();
        if ( error_map != null && error_map.size() > 0 ) {
            JPanel bottom_panel = new JPanel( new BorderLayout() );
            String bad_label_text = "";
            switch ( action ) {
                case ADD:
                    bad_label_text = "Unable to schedule for add:";
                    break;
                case REVERT:
                    bad_label_text = "Unable to revert:";
                    break;
                case DELETE:
                    bad_label_text = "Unable to schedule for delete:";
                    break;
                case RESOLVED:
                    bad_label_text = "Unable to resolve:";
                    break;
            }
            JLabel bad_label = new JLabel( bad_label_text );

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
            bottom_panel.add( bad_label, BorderLayout.NORTH );
            bottom_panel.add( GUIUtils.createTablePanel( bad_table ), BorderLayout.CENTER );
            add( bottom_panel, con );
        }

    }
}
