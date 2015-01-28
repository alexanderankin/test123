package bigdoc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.*;
import ise.java.awt.*;

/**
 * Simple status bar for BigDoc. Provides an area for showing status messages
 * and the line offset and total line count of the current file.
 *
 * @author Dale Anson
 */
public class StatusBar extends JPanel {

    private JTextField field;
    private JTextField line;
    private LambdaLayout layout;

    public StatusBar() {
        layout = new LambdaLayout();
        setLayout( layout );
        field = new JTextField( 80 );
        field.setEditable( false );
        line = new JTextField();
        line.setEditable( false );
        add( field, "0, 0, 1, 1, 0, wh, 1" );
        add( line, "1, 0, R, 1, 0, wh, 1" );
    }

    public void setStatus( String status ) {
        if ( status == null ) {
            status = "";
        }
        field.setText( status );
        layout.layoutContainer( this );
    }

    public String getStatus() {
        return field.getText();
    }

    public void setLine( int line, int total ) {
        StringBuilder sb = new StringBuilder().append(line).append(':').append(total);
        this.line.setText( sb.toString() );
        layout.layoutContainer( this );
    }

    public int getLine() {
        return Integer.parseInt( line.getText() );
    }
}
