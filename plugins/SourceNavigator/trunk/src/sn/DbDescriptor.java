/**
 * 
 */
package sn;

import org.gjt.sp.jedit.jEdit;

public class DbDescriptor {
	static private final String SN_SEP = "\\?";
	public String name, label, db, columns;
	public String [] columnNames;
	int nameColumn, fileColumn, lineColumn;
	public DbDescriptor(String base) {
		name = jEdit.getProperty(base + "name");
		label = jEdit.getProperty(base + "label");
		db = jEdit.getProperty(base + "db");
		columns = jEdit.getProperty(base + "columns");
		columnNames = columns.split(SN_SEP);
		fileColumn = lineColumn = nameColumn = -1;	// None by default
		for (int i = 0; i < getColumnCount(); i++) {
			String columnName = getColumn(i);
			if (columnName.equals("File"))
				fileColumn = i;
			else if (columnName.equals("Line"))
				lineColumn = i;
			else if (columnName.equals("Name"))
				nameColumn = i;
		}
	}
	public int getColumnCount() {
		return columnNames.length;
	}
	public String getColumn(int index) {
		if (index < 0 || index >= columnNames.length)
			return null;
		return columnNames[index];
	}
	public String toString() {
		return label;
	}
	public boolean isNameUsedAsKey() {
		return (nameColumn == 0);
	}
}