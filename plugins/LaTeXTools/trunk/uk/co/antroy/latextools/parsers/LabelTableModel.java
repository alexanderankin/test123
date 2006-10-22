/*:folding=indent:
* LabelTableModel.java - Label Table View Model
* Copyright (C) 2002 Anthony Roy
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
*/
package uk.co.antroy.latextools.parsers;

import java.util.List;

import javax.swing.table.AbstractTableModel;


public class LabelTableModel
    extends AbstractTableModel implements IRowTableModel<LaTeXAsset> {

    //~ Instance/static variables .............................................

    private List rows;
    private String[] columnNames = { "Reference", "Section", "File" };

    //~ Constructors ..........................................................

    public LabelTableModel(List rows) {
        this.rows = rows;
    }

    //~ Methods ...............................................................

    public int getColumnCount() {

        return columnNames.length;
    }

    public String getColumnName(int column) {

        return column < getColumnCount() ? columnNames[column] : null;
    }

    public int getRowCount() {

        return rows.size();
    }

    public LaTeXAsset getRowEntry(int row) {

        return (LaTeXAsset)rows.get(row);
    }

    public Object getValueAt(int row, int column) {

        Object out = null;
        LaTeXAsset be = (LaTeXAsset)rows.get(row);

        switch (column) {

            case 0:
                out = be.getShortString();

                break;

            case 1:
                out = be.getSection();

                break;

            case 2:
                out = be.getFile().getName();

                break;

            default:
                System.err.println(
                            "LabelTableModel.getValueAt(): Column " + 
                            column + " does not exist!");
        }

        return out;
    }
}
