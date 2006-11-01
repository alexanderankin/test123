package uk.co.antroy.latextools.parsers;

import javax.swing.table.TableModel;

/** 
 * TableModel that makes it possible to obtain the whole 
 * row as a single object. The class of the returned  
 * row entry is determined by implementations.
 *  
 * @see uk.co.antroy.latextools.parsers.BibTeXTableModel
 * @see uk.co.antroy.latextools.parsers.LabelTableModel
 * @see tableutils.TableSorter
 */
public interface IRowTableModel<T> extends TableModel {

	/**
	 * Return whatever the underlying's model
	 * @param row
	 * @return
	 */
	public abstract T getRowEntry(int row);

}