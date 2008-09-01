
package logviewer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.util.regex.*;


/**
 * Cell renderer that uses a JTextArea as the renderer rather than a JLabel.
 * This allows text in individual cells to wrap, which a label won't do without
 * some work.  
 *
 * The validate, revalidate, repaint, and firePropertyChange methods have been
 * overridden as no-ops for performance reasons.
 *
 * @version   $Revision$
 */
public class LogTableCellRenderer extends JTextArea implements TableCellRenderer {

    private String highlightRegex = null;
    private Pattern pattern = null;
    private Matcher matcher = null;

    public LogTableCellRenderer() {
        setLineWrap( true );
        setWrapStyleWord( true );
    }

    public void setWordWrap( boolean wrap ) {
        setLineWrap( wrap );
        setWrapStyleWord( wrap );
    }

    public void toggleWordWrap() {
        setLineWrap( !getLineWrap() );
        setWrapStyleWord( !getWrapStyleWord() );
    }

    public boolean getWordWrap() {
        return getLineWrap();
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void validate() {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void revalidate() {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void repaint() {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void repaint( int x,
            int y,
            int width,
            int height ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void repaint( long tm,
            int x,
            int y,
            int width,
            int height ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void firePropertyChange( String propertyName,
            byte oldValue,
            byte newValue ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void firePropertyChange( String propertyName,
            char oldValue,
            char newValue ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void firePropertyChange( String propertyName,
            short oldValue,
            short newValue ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void firePropertyChange( String propertyName,
            int oldValue,
            int newValue ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void firePropertyChange( String propertyName,
            long oldValue,
            long newValue ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void firePropertyChange( String propertyName,
            float oldValue,
            float newValue ) {
        // no-op
    }

    /**
     * Overridden as no-op for performance.    
     */
    public void firePropertyChange( String propertyName,
            double oldValue,
            double newValue ) {
        // no-op
    }

    /**
     * Overridden as almost a no-op for performance.  "lineWrap" and "wrapStyleWord"
        * properties are handled, all others are ignored.
     */
    public void firePropertyChange( String propertyName,
            boolean oldValue,
            boolean newValue ) {
        if ( "lineWrap".equals( propertyName ) || "wrapStyleWord".equals( propertyName ) )
            super.firePropertyChange( propertyName, oldValue, newValue );
    }


    /**
     * Set a regular expression to identify a cell to be highlighted.  This is used
     * at least by the "Find" functionality, cells that are found are highlighed.
     */
    public void setHighlightRegex( String regex ) {
        highlightRegex = regex;
        pattern = Pattern.compile( highlightRegex, Pattern.DOTALL );
    }

    /**
     * @return true if this cell has text that should be highlighted.    
     */
    private boolean shouldHighlight( ) {
        if (pattern != null)
            matcher = pattern.matcher( getText() );
        if ( matcher != null ) {
            return matcher.find();
        }
        return false;
    }

    public Component getTableCellRendererComponent( JTable table, Object
            value, boolean isSelected, boolean hasFocus, int row, int column ) {
        if ( value == null )
            value = "";
        setText( value.toString() );
        setSize( table.getColumnModel().getColumn( column ).getWidth(), getPreferredSize().height );
        if ( table.getRowHeight( row ) < getPreferredSize().height ) {
            table.setRowHeight( row, getPreferredSize().height );
        }
        boolean shouldHighlight = shouldHighlight();
        if ( isSelected ) {
            setForeground( shouldHighlight ? Color.YELLOW : table.getSelectionForeground() );
            setBackground( table.getSelectionBackground() );
        }
        else {
            setForeground( table.getForeground() );
            setBackground( shouldHighlight ? Color.YELLOW : table.getBackground() );
        }
        return this;
    }
}

