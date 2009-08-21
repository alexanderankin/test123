/*
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
* 
*/

/* danson, abstracted from TaskList */

package tasklist;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.table.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

public class TaskListTable extends JPanel implements EBComponent {
    private boolean init = false;
    private View view = null;
    private Buffer buffer = null;
    private JLabel bufferName = null;
    private JTable table = null;

    //{{{ constructor
    public TaskListTable( View view ) {
        this( view, view.getBuffer(), true );
    }

    public TaskListTable( View view, Buffer buffer, boolean showTableHeader ) {
        this.view = view;
        this.buffer = buffer;

        setLayout( new BorderLayout() );
        bufferName = new JLabel( buffer.toString(), SwingConstants.LEFT );
        add( bufferName, BorderLayout.NORTH );
        table = new JTable();
        table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        table.setCellSelectionEnabled( false );
        table.setRowSelectionAllowed( true );

        // a cell renderer that does not indicate cell focus
        table.setDefaultRenderer( Object.class, new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent( JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column ) {
                        Component c = super.getTableCellRendererComponent( table, value,
                                isSelected, false, row, column );
                        int horizAlignment = SwingConstants.LEFT;
                        if ( column == 0 )
                            horizAlignment = SwingConstants.CENTER;
                        else if ( column == 1 )
                            horizAlignment = SwingConstants.RIGHT;
                        ( ( JLabel ) c ).setHorizontalAlignment( horizAlignment );
                        return c;
                    }
                }
                                );

        table.setDefaultRenderer( Image.class, null );
        table.setDefaultRenderer( Number.class, null );
        table.setDefaultRenderer( String.class, new PaddedCellRenderer() );

        TaskListModel taskListModel = new TaskListModel( buffer );
        table.setModel( taskListModel );
        table.setShowVerticalLines( jEdit.getBooleanProperty( "tasklist.table.vertical-lines" ) );
        table.setShowHorizontalLines( jEdit.getBooleanProperty( "tasklist.table.horizontal-lines" ) );
        MouseHandler handler = new MouseHandler();
        table.addMouseListener( handler );

        // TODO: Fix height of header using Windows L&F
        if ( table.getTableHeader() != null ) {
            table.getTableHeader().setReorderingAllowed( false );
            table.getTableHeader().setResizingAllowed( true );
            table.getTableHeader().addMouseListener( handler );
            Dimension dim = table.getTableHeader().getPreferredSize();
            dim.height = table.getRowHeight();
            table.getTableHeader().setPreferredSize( dim );
            table.getTableHeader().setVisible( true );
        }
        resizeTable();
        sort();
        JPanel tablePanel = new JPanel( new BorderLayout() );
        if ( showTableHeader ) {
            tablePanel.add( table.getTableHeader(), BorderLayout.NORTH );
        }
        tablePanel.add( table, BorderLayout.CENTER );
        add( tablePanel, BorderLayout.CENTER );
        add( Box.createVerticalStrut( 11 ), BorderLayout.SOUTH );
        init = true;
    } //}}}

    public void setBuffer( Buffer buffer ) {
        bufferName.setText( buffer.toString() );
        TaskListModel taskListModel = new TaskListModel( buffer );
        table.setModel( taskListModel );
        resizeTable();
        sort();
        invalidate();
        repaint();
    }

    public Buffer getBuffer() {
        return getTaskListModel().getBuffer();
    }

    //{{{ getTaskListModel() method
    public TaskListModel getTaskListModel() {
        return ( TaskListModel ) table.getModel();
    } //}}}

    /**
     * @return the number of rows in this table.  
     */
    public int getRowCount() {
        return getTaskListModel().getRowCount();
    }

    //{{{ tableChanged() method
    /**
     * Calls resizeTable() when the number of table columns change
     *
     * @param e The TableModelEvent represeting the change in the table's state
     */
    public void tableChanged( TableModelEvent e ) {
        table.tableChanged( e );

        if ( e.getFirstRow() == e.HEADER_ROW && init ) {
            try {
                resizeTable();
            }
            catch ( Exception ex ) {
                Log.log( Log.ERROR, TaskList.class, ex );
            }
        }
    } //}}}

    //{{{ resizeTable() method
    /**
     * Re-sizes the columns in the table - called when cols are
     * added or removed.
     */
    public void resizeTable() {
        TableColumnModel columnModel = table.getColumnModel();

        // symbol
        columnModel.getColumn( 0 ).setMinWidth( 20 );
        columnModel.getColumn( 0 ).sizeWidthToFit();
        columnModel.getColumn( 0 ).setMaxWidth( 20 );
        columnModel.getColumn( 0 ).setResizable( false );

        // line number
        columnModel.getColumn( 1 ).setMinWidth( 50 );
        columnModel.getColumn( 1 ).setPreferredWidth( 50 );
        columnModel.getColumn( 1 ).setMaxWidth( 50 );
        columnModel.getColumn( 1 ).setResizable( false );

        // text
        columnModel.getColumn( 2 ).setMinWidth( 200 );
        columnModel.getColumn( 2 ).setPreferredWidth( 800 );
        table.getTableHeader().resizeAndRepaint();
    } //}}}

    //{{{ sort() method
    private void sort() {
        getTaskListModel().sort();
    } //}}}

    //{{{ MouseHandler class
    /**
     * Responds to mouse clicks in the table or its header row
     */
    class MouseHandler extends MouseAdapter {
        //{{{ mouseClicked() method
        /**
         * Calls handling routine based on number, type and location
         * of mouse clicks
         * @param e The MouseEvent being handled
         */
        public void mouseClicked( MouseEvent e ) {
            Point p = e.getPoint();
            final int rowNum = table.rowAtPoint( p );
            /* single click with right mouse button on table header */
            if ( e.getClickCount() == 1 &&
                    ( e.getModifiers() & InputEvent.BUTTON3_MASK ) != 0 &&
                    e.getComponent() != table.getTableHeader() ) {
                e.consume();
                showPopup( view, p );
            }
            /* multiple clicks, not with right mouse button */
            else if ( e.getClickCount() > 1 &&
                    ( e.getModifiers() & InputEvent.BUTTON3_MASK ) == 0 ) {
                if ( e.getComponent() == table.getTableHeader() ) {
                    TaskListModel model = getTaskListModel();
                    int sortCol = table.columnAtPoint( p );
                    // if icon clicked, sort by task type & description
                    if ( sortCol == 0 ) {
                        sortCol = 2;
                    }
                    if ( model.getSortCol() == sortCol ) {
                        model.setSortAscending( !model.getSortAscending() );
                    }
                    else {
                        model.setSortCol( sortCol );
                    }
                    model.sort();
                }
                else if ( rowNum > -1 ) {
                    table.setRowSelectionInterval( rowNum, rowNum );
                    showTaskText( rowNum );
                }
            }
            /* single non-right click */
            else if ( e.getClickCount() == 1 ) {
                if ( e.getComponent() == table.getTableHeader() )
                    return ;

                if ( TaskListPlugin.getAllowSingleClickSelection() ) {
                    table.setRowSelectionInterval( rowNum, rowNum );
                    showTaskText( rowNum );
                }
            }
        } //}}}

        //{{{ showPopup(View view, int row, Point p) method
        /**
         * Causes a popup context menu to be shown
         * @param view The View in which the TaskList component appears
         * @param row The table row clicked by the mouse
         * @param p The Point within the TaskList's table object clicked by the mouse
         */
        private void showPopup( View view, Point p ) {
            TaskListPopup popup = new TaskListPopup( view, table, p );

            // keep within screen limits; use task list panel, not table
            SwingUtilities.convertPointToScreen( p, table );
            SwingUtilities.convertPointFromScreen( p, table );

            Dimension dt = table.getSize();
            Dimension dp = popup.getPreferredSize();
            if ( p.x + dp.width > dt.width )
                p.x = dt.width - dp.width;
            if ( p.y + dp.height > dt.height )
                p.y = dt.height - dp.height;
            popup.show( table, p.x + 1, p.y + 1 );
        } //}}}

        //{{{ showTaskText(int row) method
        /**
         * Locates and displays buffer text corresponding to the selected row of the TaskList's table component
         *
         * @param row The selected row of the TaskList table
         */
        private void showTaskText( final int row ) {
            // get EditPane of buffer clicked, goto selection
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        Task task = ( Task ) getTaskListModel().elementAt( row );
                        EditPane[] editPanes = view.getEditPanes();
                        Buffer buffer = jEdit.getBuffer( task.getBufferPath() );
                        if ( buffer == null ) {
                            return ;     // buffer not open
                        }
                        for ( EditPane editPane : editPanes ) {
                            Buffer[] buffers = editPane.getBufferSet().getAllBuffers();
                            for ( Buffer ep_buffer : buffers ) {
                                if ( ep_buffer.equals( buffer ) ) {
                                    editPane.setBuffer( ep_buffer );
                                    JEditTextArea textArea = editPane.getTextArea();
                                    textArea.setCaretPosition( textArea.getLineStartOffset( task.getLineNumber() ) + task.getStartOffset() );
                                    textArea.scrollToCaret( true );
                                    textArea.grabFocus();
                                    return ;
                                }
                            }
                        }
                    }
                }
            );
        } //}}}

    } //}}}

    //{{{ addNotify() method
    /**
     * Adds the TaskList and its table's data model to the EditBus
     * to listen for messages; registers the data model to be notified when
     * tasks are added or removed.
     */
    public void addNotify() {
        super.addNotify();
        EditBus.addToBus( this );
        EditBus.addToBus( getTaskListModel() );
    } //}}}

    //{{{ removeNotify() method
    /**
     * Removes the TaskList and its table's data model from the EditBus;
     * removes the data model form the list of components that listen for
     * the addition or removal of tasks items
     */
    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBus( this );
        EditBus.removeFromBus( getTaskListModel() );
    } //}}}


    //{{{ handleMessage(EBMessage msg) method
    /**
     * Message handling routine required by the jEdit Plugin API
     *
     * @param message The EBMessage received from the EditBus
     */
    public void handleMessage( EBMessage message ) {
        if ( message instanceof PropertiesChanged ) {
            table.setShowVerticalLines(
                jEdit.getBooleanProperty( "tasklist.table.vertical-lines" ) );
            table.setShowHorizontalLines(
                jEdit.getBooleanProperty( "tasklist.table.horizontal-lines" ) );
            int col = jEdit.getIntegerProperty( "tasklist.table.sort-column", 1 );
            boolean ascending = jEdit.getBooleanProperty( "tasklist.table.sort-ascending", true );
            getTaskListModel().setSortCol( col );
            getTaskListModel().setSortAscending( ascending );
            getTaskListModel().sort();

            String displayType = jEdit.getProperty( "tasklist.buffer.display" );
            String bufferDisplay;
            if ( displayType.equals( jEdit.getProperty( "options.tasklist.general.buffer.display.fullpath" ) ) ) {
                bufferDisplay = buffer.getPath();
            }
            else if ( displayType.equals( jEdit.getProperty( "options.tasklist.general.buffer.display.nameonly" ) ) ) {
                bufferDisplay = buffer.getName();
            }
            else {
                // filename (directory)
                bufferDisplay = buffer.toString();
            }
            bufferName.setText( bufferDisplay );
        }
    } //}}}

    // A cell renderer that puts 6 pixels of padding on the left side of the cell.
    class PaddedCellRenderer extends DefaultTableCellRenderer {
        Border paddedBorder = BorderFactory.createEmptyBorder( 0, 6, 0, 0 );

        public Component getTableCellRendererComponent( JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column ) {
            JLabel label = ( JLabel ) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
            label.setBorder( paddedBorder );
            return label;
        }
    }
}