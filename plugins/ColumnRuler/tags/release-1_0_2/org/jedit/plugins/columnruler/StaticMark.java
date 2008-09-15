package org.jedit.plugins.columnruler;

import java.awt.Color;

import org.gjt.sp.jedit.*;

public class StaticMark extends Mark {
	protected int column;
	
	public StaticMark() {
	}
	
	public StaticMark(String name) {
		super(name);
	}

	public StaticMark(String name, Color c) {
		super(name,c);
	}
	
	public int getPositionOn(ColumnRuler ruler) {
		return getColumn();
	}

	public int getColumn() {
		return column;
	}

	public void setPositionOn(ColumnRuler ruler, int col) {
		setColumn(col);
	}
	
	//{{{ setColumn()
	/**
	 *  Moves this mark to the given column.
	 *
	 * @param  col  The new column
	 */
	public void setColumn(int col) {
		column = col;
		if (isGuideVisible())
			jEdit.getActiveView().getTextArea().repaint();
	}//}}}
	
}
