package ghm.follow;

import java.awt.Point;
import java.awt.Rectangle;

import java.util.*;
import java.util.regex.*;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.*;
import logviewer.LogTableCellRenderer;
import logviewer.LogTableModel;
import logviewer.LogType;
import logviewer.TableSorter;

/**
 * danson: added this class to allow log files to be shown in a JTable.
 *
 * @version   $Revision$
 */
public class JTableDestination extends OutputDestinationComponent {

    private JTable table = null;
    private JViewport viewport = null;
    private LogType logType = null;
    private LogTableModel model = null;
    private BitSet showColumns = null;
    private List columnNames = null;
    private LogTableCellRenderer cellRenderer = null;

    private boolean autoPositionCaret_ = true;

    private Point foundCell = new Point(0, 0);
    private boolean wrapFind = false;
    private String toFind = null;
    private Pattern findPattern = null;


    /**
     * Constructor for JTableDestination
     *
     * @param table The actual JTable
     * @param type the LogType containing the definitions for the table columns
     */
    public JTableDestination(JTable table, LogType type) {
        if (table == null)
            throw new IllegalArgumentException("table cannot be null");
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        this.table = table;
        this.logType = type;

        // set up the table columns.  Columns with a defined width of 0 are not
        // shown.
        List columns = logType.getColumns();
        showColumns = new BitSet(columns.size());
        columnNames = new ArrayList();
        for (int i = 0; i < columns.size(); i++) {
            LogType.Column column = (LogType.Column) columns.get(i);
            if (column.getWidth() != 0) {
                columnNames.add(column.getName());
                showColumns.flip(i);
            }
        }

        // install a special renderer so the find looks better and word wrap works
        cellRenderer = new LogTableCellRenderer();
        try {
            table.setDefaultRenderer(Class.forName("java.lang.Object"), cellRenderer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // use a LogTableModel underneath as it is very fast, then wrap it in
        // a table sorter
        model = new LogTableModel(columnNames);
        TableSorter sorter = new TableSorter(model);
        table.setModel(sorter);
        sorter.setTableHeader(table.getTableHeader());

        // maybe jump right to the bottom
        scrollToBottom();

    }

    /**
     * Gets the JTable
     *
     * @return   The JTable value
     */
    public JTable getJTable() {
        return table;
    }

    /** Toggle word wrap */
    public void toggleWordWrap() {
        cellRenderer.toggleWordWrap();
    }

    /**
     * Gets the wordWrap value
     *
     * @return   The wordWrap value
     */
    public boolean getWordWrap() {
        return cellRenderer.getWordWrap();
    }

    /**
     * Sets the word wrap value
     *
     * @param wrap  The new wordWrap value
     */
    public void setWordWrap(boolean wrap) {
        cellRenderer.setWordWrap(wrap);
    }

    /**
     * @return   whether caret will be automatically moved to the bottom of the
     *      text area when text is appended
     */
    public boolean autoPositionCaret() {
        return autoPositionCaret_;
    }

    /**
     * @param autoPositionCaret  if true, caret will be automatically moved to
     *      the bottom of the text area when text is appended
     */
    public void setAutoPositionCaret(boolean autoPositionCaret) {
        autoPositionCaret_ = autoPositionCaret;
        scrollToBottom();
    }

    /** Toggle auto position caret */
    public void toggleAutoPositionCaret() {
        autoPositionCaret_ = !autoPositionCaret_;
        scrollToBottom();
    }


    /**
     * Find something in the log file
     *
     * @param toFind what to find
     */
    public void find(String toFind) {
        find(toFind, 0, 0);
    }

    private Point point00 = new Point(0, 0);

    /**
     * The actual find method, used by both <code>find</code> and <code>findNext</code>.
     *
     * @param toFind what to find
     * @param start_row what row to start looking in
     * @param start_col what column in the row to start looking in.
     */
    private void find(String toFind, int start_row, int start_col) {
        if (toFind == null || toFind.length() == 0)
            return;
        if (findPattern == null)
            findPattern = Pattern.compile(toFind, Pattern.DOTALL);
        if (!toFind.equals(this.toFind)) {
            this.toFind = toFind;
            findPattern = Pattern.compile(toFind, Pattern.DOTALL);
        }

        if (viewport == null)
            viewport = (JViewport) table.getParent();

        cellRenderer.setHighlightRegex(toFind);

        int wrap_count = 0;
        for (int row = start_row; row < table.getRowCount(); row++) {
            for (int col = start_col; col < table.getColumnCount(); col++) {
                String value = (String) table.getValueAt(row, col);
                if (value == null || value.length() == 0)
                    continue;
                Matcher matcher = findPattern.matcher(value);
                if (matcher.find()) {
                    foundCell = new Point(row, col);
                    viewport.setViewPosition(point00);
                    Rectangle foundRectangle = table.getCellRect(row, col, true);
                    viewport.scrollRectToVisible(foundRectangle);
                    Point position = viewport.getViewPosition();
                    viewport.setViewPosition(new Point(position.x, position.y + foundRectangle.height));
                    return;
                }
            }
            start_col = 0;
            if (wrapFind && row == table.getRowCount() - 1 && wrap_count < 2) {
                start_row = 0;
                row = 0;
                ++wrap_count;
            }
        }
    }

    /**
     * Find the next occurance of the given text
     *
     * @param toFind what to find
     */
    public void findNext(String toFind) {
        if (foundCell == null)
            foundCell = new Point(0, 0);
        int start_row = foundCell.x;
        int start_col = foundCell.y + 1;
        if (start_col >= table.getColumnCount()) {
            start_col = 0;
            start_row += 1;
            if (start_row >= table.getRowCount())
                start_row = 0;
        }
        find(toFind, start_row, start_col);
    }

    /**
     * Sets the wrapFind value
     *
     * @param wrap  If true, wrap find back to the top of the file
     */
    public void setWrapFind(boolean wrap) {
        wrapFind = wrap;
    }

    /** Clear the table.  Does not delete any data. */
    public void clear() {
        model = new LogTableModel(columnNames);
        TableSorter sorter = new TableSorter(model);
        table.setModel(sorter);
        sorter.setTableHeader(table.getTableHeader());
    }

    /**
     * Add an entry to the table.  The String represents a line from the log file.
     * Depending on the definitions in the LogType, this will be split into cells
     * for the table either by regex, delimiter, or width.
     *
     * @param s
     */
    public void print(String s) {
        
        if (s == null)
            return;
        
        s = s.trim();
        
        // maybe eliminate rows by a regex
        s = removeEntryByRegex(s);

        // maybe split up the row data by a regex
        String regex = logType.getColumnRegex();
        if (regex != null) {
            addEntryByRegex(s);
            scrollToBottom();
            return;
        }

        // maybe split up the row data by a delimiter
        String delimiter = logType.getColumnDelimiter();
        if (delimiter != null) {
            addEntryByDelimiter(s);
            scrollToBottom();
            return;
        }

        // maybe the columns are fixed width
        addEntryByColumnWidth(s);
        scrollToBottom();
    }

    /** Scroll the view to the bottom of the log */
    private void scrollToBottom() {
        if (autoPositionCaret_) {
            if (viewport == null)
                viewport = (JViewport) table.getParent();
            if (viewport != null) {
                int y = (int) (viewport.getViewSize().getHeight() - viewport.getExtentSize().getHeight());
                Point bottomPosition = new Point(0, y);
                viewport.setViewPosition(bottomPosition);
                viewport.revalidate();
            }
        }
    }
    
    private String removeEntryByRegex(String entry) {
        String regex = logType.getRowRegex();
        if (regex == null)
            return entry;
        boolean include = logType.getRowInclude();
        int flags = logType.getRowFlags();

        Pattern p = Pattern.compile(regex, flags);
        Matcher m = p.matcher(entry);
        if (m.matches() && !include) {
            return "";
        }
        else if (!m.matches() && include) {
            return "";   
        }
        return entry;
    }

    /**
     * Adds one or more rows to the table.
     *
     * @param rows  the rows
     */
    private void addEntryByColumnWidth(String entry) {
        List columns = logType.getColumns();
        List rowsToAdd = new ArrayList();
        List toShow = new ArrayList();
        boolean hasContent = false;
        for (int j = 0; j < columns.size(); j++) {
            if (showColumns.get(j)) {
                LogType.Column column = (LogType.Column) columns.get(j);
                int offset = column.getOffset();
                if (offset < 0)
                    continue;
                int width = column.getWidth();
                if (width == -1 && j == columns.size() - 1) {
                    // -1 means add all remaining column data to this column
                    width = entry.length();
                }
                if (width <= 0)
                    continue;
                if (offset > entry.length())
                    offset = 0;
                int endOffset = offset + width;
                if (endOffset > entry.length())
                    endOffset = entry.length();
                String columnData = entry.substring(offset, endOffset);
                if (columnData.length() > 0)
                    hasContent = true;
                toShow.add(columnData);
            }
        }
        if (hasContent)
            rowsToAdd.add(toShow);
        if (rowsToAdd.size() > 0)
            model.addRows(rowsToAdd);
    }

    /**
     * Adds one or more rows to the table.
     *
     * @param rows  the rows
     */
    private void addEntryByDelimiter(String entry) {
        String delimiter = logType.getColumnDelimiter();
        List rowsToAdd = new ArrayList();
        String[] rowdata = entry.split(delimiter);
        List toShow = new ArrayList();
        boolean hasContent = false;
        for (int j = 0; j < rowdata.length; j++) {
            if (showColumns.get(j)) {
                if (rowdata[j] != null && rowdata[j].length() > 0)
                    hasContent = true;
                toShow.add(rowdata[j]);
            }
        }
        if (hasContent)
            rowsToAdd.add(toShow);
        if (rowsToAdd.size() > 0)
            model.addRows(rowsToAdd);
    }

    /**
     * Adds one or more rows to the table.
     *
     * @param rows  the rows
     */
    private void addEntryByRegex(String entry) {
        String regex = logType.getColumnRegex();
        String grps = logType.getColumnGroups();
        int[] groups = new int[]{0};
        if (grps != null) {
            String[] split = grps.split("[,]");
            groups = new int[split.length];
            for (int i = 0; i < split.length; i++)
                groups[i] = Integer.parseInt(split[i].trim());
        }

        int flags = logType.getColumnFlags();

        List rowsToAdd = new ArrayList();

        String rowdata = entry;
        Pattern p = Pattern.compile(regex, flags);
        Matcher m = p.matcher(entry);
        List row = new ArrayList();
        int j = 0;
        boolean hasContent = false;
        while (m.find()) {
            for (int k = 0; k < groups.length; k++) {
                String match = m.group(groups[k]);
                if (match != null && showColumns.get(j)) {
                    if (match.length() > 0)
                        hasContent = true;
                    row.add(match);
                }
            }
            ++j;
        }
        if (hasContent)
            rowsToAdd.add(row);
        if (rowsToAdd.size() > 0)
            model.addRows(rowsToAdd);
    }

}

