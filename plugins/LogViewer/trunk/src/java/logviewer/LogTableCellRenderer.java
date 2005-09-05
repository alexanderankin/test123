
package logviewer;

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
    
    public LogTableCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }
    
    public void setWordWrap(boolean wrap) {
        setLineWrap(wrap);
        setWrapStyleWord(wrap);
    }
    
    public void toggleWordWrap() {
        setLineWrap(!getLineWrap());
        setWrapStyleWord(!getWrapStyleWord());
    }
    
    public boolean getWordWrap() {
        return getLineWrap();
    }

    public void validate() {
        // no-op
    }

    public void revalidate() {
        // no-op
    }

    public void repaint() {
        // no-op
    }

    public void repaint(int x,
            int y,
            int width,
            int height) {
        // no-op
    }

    public void repaint(long tm,
            int x,
            int y,
            int width,
            int height) {
        // no-op
    }
    
    public void firePropertyChange(String propertyName,
            byte oldValue,
            byte newValue) {
        // no-op
    }

    public void firePropertyChange(String propertyName,
            char oldValue,
            char newValue) {
        // no-op
    }

    public void firePropertyChange(String propertyName,
            short oldValue,
            short newValue) {
        // no-op
    }

    public void firePropertyChange(String propertyName,
            int oldValue,
            int newValue) {
        // no-op
        System.out.println(propertyName + ": " + oldValue + ", " + newValue);
    }

    public void firePropertyChange(String propertyName,
            long oldValue,
            long newValue) {
        // no-op
    }

    public void firePropertyChange(String propertyName,
            float oldValue,
            float newValue) {
        // no-op
    }

    public void firePropertyChange(String propertyName,
            double oldValue,
            double newValue) {
        // no-op
    }

    public void firePropertyChange(String propertyName,
            boolean oldValue,
            boolean newValue) {
        if ("lineWrap".equals(propertyName) || "wrapStyleWord".equals(propertyName))
            super.firePropertyChange(propertyName, oldValue, newValue);
    }
    

	/**
	 * Set a regular expression to identify a cell to be highlighted.  This is used
	 * at least by the "Find" functionality, cells that are found are highlighed.
	 */
    public void setHighlightRegex(String regex) {
        highlightRegex = regex;
    }

	/**
	 * Highlights the cell if the cell contains the highlight regex value.    
	 */
    private void highlight() {
        if (highlightRegex != null) {
            Pattern pattern = Pattern.compile(highlightRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(getText());
            if (matcher.find())
                setBackground(java.awt.Color.YELLOW);
            else
                setBackground(java.awt.Color.WHITE);
        }
    }
    
    public Component getTableCellRendererComponent(JTable table, Object
            value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null)
            value = "";
        setText(value.toString());
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
        if (table.getRowHeight(row) < getPreferredSize().height) {
            table.setRowHeight(row, getPreferredSize().height);
        }
        highlight();
        return this;
    }
}

