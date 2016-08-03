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

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

/**
 * A JTable that lays out the rows based on the best height for the individual
 * rows.
 */
public class BestRowTable extends JTable {

    private int bestHeight = 0;

    public BestRowTable() {
        super();
    }
    public BestRowTable( int numRows, int numColumns ) {
        super( numRows, numColumns );
    }
    public BestRowTable( Object[][] rowData, Object[] columnNames ) {
        super( rowData, columnNames );
    }
    public BestRowTable( TableModel dm ) {
        super( dm );
    }
    public BestRowTable( TableModel dm, TableColumnModel cm ) {
        super( dm, cm );
    }
    public BestRowTable( TableModel dm, TableColumnModel cm, ListSelectionModel sm ) {
        super( dm, cm, sm );
    }
    public BestRowTable( Vector rowData, Vector columnNames ) {
        super( rowData, columnNames );
    }

    @Override
    public void doLayout() {
        packRows();
        super.doLayout();
    }

    /**
     * @param row the row number in the table to calculate the best height for
     * @param margin cell margin
     * @return the preferred height of a row.  JTable doesn't provide this.
     * The returned value is equal to the tallest preferred height of all the
     * cells in the row.
     */
    public int getPreferredRowHeight( int row, int margin ) {
        int height = 1;

        // determine tallest cell in the row
        for ( int column = 0; column < getColumnCount(); column++ ) {
            TableCellRenderer renderer = getCellRenderer( row, column );
            Component comp = prepareRenderer( renderer, row, column );
            int preferred = comp.getPreferredSize().height + ( 2 * margin );
            height = Math.max( height, preferred );
        }
        return height;
    }

    /**
     * @return the best height for this table.
     */
    public int getBestHeight() {
        return bestHeight;
    }

    /**
     * Calculate and set the best height of the table using the preferred height
     * of each row and margin size of 1.
     */
    public void packRows() {
        packRows( 1 );
    }

    /**
     * Calculate and set the best height of the table using the preferred height
     * of each row and the specified margin within each cell.
     * @param margin the size of the margin to be applied to each cell.
     */
    public void packRows( int margin ) {
        packRows( 0, getRowCount(), margin );
    }

    /**
     * Adjust the heights of a range of rows.
     * @param start the starting row of the range
     * @param end the ending row of the range
     * @param margin the size of the margin to be applied to each cell.
     */
    public void packRows( int start, int end, int margin ) {
        bestHeight = 0;
        for ( int row = 0; row < getRowCount(); row++ ) {
            int height = getPreferredRowHeight( row, margin );
            setRowHeight( row, height );
            bestHeight += height;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = getBestHeight();
        return d;
    }
}