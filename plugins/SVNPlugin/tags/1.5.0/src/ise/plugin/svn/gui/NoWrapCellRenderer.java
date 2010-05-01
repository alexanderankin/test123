package ise.plugin.svn.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import org.gjt.sp.jedit.jEdit;

/**
 * Non-wrapping text area cell renderer.
 */
public class NoWrapCellRenderer extends JTextArea implements TableCellRenderer {

    private static Color background = jEdit.getColorProperty( "view.bgColor", Color.WHITE );
    private static Color selection = jEdit.getColorProperty( "view.selectionColor", Color.LIGHT_GRAY );
    private static Color foreground = jEdit.getColorProperty( "view.fgColor", Color.BLACK );

    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
        setText( value == null ? "" : value.toString().trim() );
        setBackground( isSelected ? selection : background );
        setForeground( foreground );
        return this;
    }
}
