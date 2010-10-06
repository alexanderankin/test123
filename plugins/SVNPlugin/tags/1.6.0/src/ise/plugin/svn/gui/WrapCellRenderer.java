package ise.plugin.svn.gui;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import org.gjt.sp.jedit.jEdit;

/**
 * Wrapping text area cell renderer.  This is useful for displaying text
 * contents that contain line separators.  This renderer will provide the
 * right preferred size so that all of the text can be visible in a table
 * cell.
 */
public class WrapCellRenderer extends JTextPane implements TableCellRenderer {

    // TODO: use view colors or just let the look and feel handle it?
    private static Color background = jEdit.getColorProperty( "view.bgColor", Color.WHITE );
    private static Color selection = jEdit.getColorProperty( "view.selectionColor", Color.LIGHT_GRAY );
    private static Color foreground = jEdit.getColorProperty( "view.fgColor", Color.BLACK );

    private WordSearcher searcher = null;

    public void setSearcher( WordSearcher searcher ) {
        this.searcher = searcher;
    }

    JTable table = null;
    int column = 0;
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
        this.table = table;
        this.column = column;
        setEditable( false );
        String content = value == null ? "" : value.toString().trim();
        setText( content );
        setBackground( isSelected ? selection : background );
        setForeground( foreground );
        if ( searcher != null ) {
            searcher.search();
        }
        return this;
    }

    /**
     * Calculates the preferred size based on the column width and the
     * amount of text to display so that the entire text will be visible.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if ( table != null ) {
            Insets insets = getInsets();
            d.width = table.getColumnModel().getColumn( column ).getWidth();
            int width = d.width - insets.left - insets.right;
            FontMetrics fm = getFontMetrics( getFont() );
            int lines = countLines( getText(), fm, width );
            d.height = ( fm.getHeight() * lines ) + 5;   // 5 pixels for padding
        }
        return d;
    }

    private int countLines( String text, FontMetrics fm, int width ) {
        StringTokenizer st = new StringTokenizer( text, " " );
        int count = 1;
        StringBuilder sb = new StringBuilder();
        while ( st.hasMoreTokens() ) {
            String word = st.nextToken();
            if ( fm.stringWidth( sb.toString() + word ) > width ) {
                ++count;
                sb = new StringBuilder();
            }
            sb.append( word ).append( ' ' );
        }
        return count;
    }
}