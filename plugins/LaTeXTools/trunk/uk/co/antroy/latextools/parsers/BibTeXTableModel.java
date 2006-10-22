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


/** 
 * Content of table of BibTeX references (columns Ref, Title, Author, Journal).
 * The data is displayed by the BibTeX navigator.
 * 
 * @see uk.co.antroy.latextools.parsers.BibEntry
 * @see uk.co.antroy.latextools.BibTeXTablePanel
 */
public class BibTeXTableModel
    extends AbstractTableModel implements IRowTableModel<BibEntry> {

    //~ Instance/static variables .............................................

    List rows;
    String[] columnNames = { "Ref", "Title", "Author", "Journal" };

    //~ Constructors ..........................................................
    /**
     * @param rows List of {@link BibEntry}.
     */
    public BibTeXTableModel(List rows) {
        this.rows = rows;
    }

    //~ Methods ...............................................................

    /* (non-Javadoc)
	 * @see uk.co.antroy.latextools.parsers.IBibTeXTableModel#getColumnCount()
	 */
    public int getColumnCount() {

        return columnNames.length;
    }

    /* (non-Javadoc)
	 * @see uk.co.antroy.latextools.parsers.IBibTeXTableModel#getColumnName(int)
	 */
    public String getColumnName(int column) {

        return column < getColumnCount() ? columnNames[column] : null;
    }

    /* (non-Javadoc)
	 * @see uk.co.antroy.latextools.parsers.IBibTeXTableModel#getRowCount()
	 */
    public int getRowCount() {

        return rows.size();
    }

    /* (non-Javadoc)
	 * @see uk.co.antroy.latextools.parsers.IBibTeXTableModel#getRowEntry(int)
	 */
    public BibEntry getRowEntry(int row) {

        return (BibEntry)rows.get(row);
    }

    /* (non-Javadoc)
	 * @see uk.co.antroy.latextools.parsers.IBibTeXTableModel#getValueAt(int, int)
	 */
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
