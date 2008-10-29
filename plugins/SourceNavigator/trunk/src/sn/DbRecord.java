/**
 * 
 */
package sn;

import org.gjt.sp.jedit.MiscUtilities;

public class DbRecord {
	private DbDescriptor desc;
	private String [] key;
	private String baseDir;
	private SourceLink link;
	public DbRecord(DbDescriptor desc, String [] key) {
		this.desc = desc;
		this.key = key;
	}
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	private int getColumnIndex(String columnName) {
		for (int i = 0; i < desc.columnNames.length; i++)
			if (desc.columnNames[i].equals(columnName))
				return i;
		return (-1);
	}
	public String getColumn(String columnName) {
		return getColumn(getColumnIndex(columnName));
	}
	public String getColumn(int columnIndex) {
		if (columnIndex < 0 || columnIndex >= key.length)
			return null;
		return key[columnIndex];
	}
	public SourceLink getSourceLink() {
		if (link != null)
			return link;
		if (desc.fileColumn < 0)
			return null;
		String file = getColumn(desc.fileColumn);
		if (file == null)
			return null;
		int line = 0;
		int offset = 0;
		if (desc.lineColumn >= 0) {
			String lineStr = (String) getColumn(desc.lineColumn);
			if (lineStr == null)
				return null;
			try {
				String [] pos = lineStr.split("\\.");
				line = Integer.valueOf(pos[0]);
				offset = (pos.length > 1) ? Integer.valueOf(pos[1]) : 0;
			} catch (Exception e) {
				return null;
			}
		}
		if (! MiscUtilities.isAbsolutePath(file))
			file = baseDir + "/" + file;
		link = new SourceLink(file, line, offset);
		return link;
	}
}