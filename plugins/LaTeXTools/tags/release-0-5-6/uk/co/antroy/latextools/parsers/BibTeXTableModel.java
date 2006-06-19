/*:folding=indent:
* BibTeXTableModel.java - BibTeX Table View Model
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


public class BibTeXTableModel
    extends AbstractTableModel {

    //~ Instance/static variables .............................................

    List rows;
    String[] columnNames = { "Ref", "Title", "Author", "Journal" };

    //~ Constructors ..........................................................

    public BibTeXTableModel(List rows) {
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

    public BibEntry getRowEntry(int row) {

        return (BibEntry)rows.get(row);
    }

    public Object getValueAt(int row, int column) {

        Object out = null;
        BibEntry be = (BibEntry)rows.get(row);

        switch (column) {

            case 0:
                out = be.getRef();

                break;

            case 1:
                out = be.getTitle();

                break;

            case 2:
                out = be.getAuthor();

                break;

            case 3:
                out = be.getJournal();

                break;

            default:
                System.err.println(
                            "BibTeXTableModel.getValueAt(): Column " + 
                            column + " does not exist!");
        }

        return out;
    }
}
