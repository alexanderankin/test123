package ise.plugin.svn.library;

import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;

/**
 * Grabs the string value of the contents of a table cell and shows it
 * in a popup.
 */
public class TableCellViewer extends MouseAdapter {
    private JTable table = null;
    private JTextArea ta;
    private JPopupMenu pm;
    public TableCellViewer( JTable table ) {
        this.table = table;
        ta = new JTextArea( 10, 40 );
        ta.setLineWrap( true );
        ta.setEditable( false );
        pm = new JPopupMenu();
        pm.add( new JScrollPane( ta ) );
    }
    public void mousePressed( MouseEvent me ) {
        doPopup( me );
    }
    public void mouseReleased( MouseEvent me ) {
        doPopup( me );
    }
    private void doPopup( MouseEvent me ) {
        if ( me.isPopupTrigger() ) {
            Point p = me.getPoint();
            int col = table.columnAtPoint( p );
            int row = table.rowAtPoint( p );
            Object value = table.getModel().getValueAt( row, col );
            if ( value != null ) {
                ta.setText( value.toString() );
                GUIUtils.showPopupMenu( pm, table, me.getX(), me.getY() );
            }
        }
    }
}
