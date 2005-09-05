package logviewer;

import java.util.*;

public class LogType {
    
    private String name = null;
    private List columns = new ArrayList();
    private String fileNameGlob = null;
    private String firstLineGlob = null;
    private String columnRegex = null;
    private String regexGroups = null;
    private int regexFlags = 0;
    private String columnDelimiter = null;
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("LogType [");
        sb.append("name=").append(name).append(",");
        sb.append("fileNameGlob=").append(fileNameGlob).append(",");
        sb.append("firstLineGlob=").append(firstLineGlob).append(",");
        sb.append("columnRegex=").append(columnRegex).append(",");
        sb.append("columnDelimiter=").append(columnDelimiter).append(",");
        sb.append("regexGroups=").append(regexGroups).append(",");
        sb.append("regexFlags=").append(regexFlags).append(",");
        sb.append("columns=[");
        for (int i = 0; i < columns.size(); i++) {
            Column col = (Column)columns.get(i);
            sb.append(col.getName());
            if (col.getWidth() > 0)
                sb.append(",").append(col.getWidth());
            if (i + 1 < columns.size())
                sb.append(";");
        }
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }
    
    public LogType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;   
    }
    
    public void setFileNameGlob(String glob) {
        fileNameGlob = glob;   
    }
    
    public String getFileNameGlob() {
        return fileNameGlob;   
    }
    
    public void setFirstLineGlob(String glob) {
        firstLineGlob = glob;   
    }
    
    public String getFirstLineGlob() {
        return firstLineGlob;   
    }
    
    public void setColumnRegex(String regex, String groups, int flags) {
        columnRegex = regex;  
        regexGroups = groups;
        regexFlags = flags;
    }
    
    public String getColumnRegex() {
        return columnRegex;   
    }
    
    public String getGroups() {
        return regexGroups;   
    }
    
    public int getFlags() {
        return regexFlags;   
    }
    
    public void setColumnDelimiter(String delimiter) {
        columnDelimiter = delimiter;   
    }
    
    public String getColumnDelimiter() {
        return columnDelimiter;   
    }
    
    public void addColumn(String name) {
        columns.add(new Column(name));        
    }
    
    public void addColumn(String name, int width) {
        columns.add(new Column(name, width));
    }
    
    public void addColumn(String name, int offset, int width) {
        columns.add(new Column(name, offset, width));   
    }
    
    public List getColumns() {
        return columns;   
    }
    
    public class Column {
        private String columnName = null;
        private int columnWidth = -1;
        private int columnOffset = -1;
        
        public Column(String name) {
            columnName = name;    
        }
        
        public Column(String name, int width) {
            columnName = name;
            columnWidth = width;
            if (width < 0)
                throw new IllegalArgumentException("Width must not be negative.");
        }
        
        public Column(String name, int offset, int width) {
            if (width < 0)
                throw new IllegalArgumentException("Width must not be negative.");
            if (offset < 0)
                throw new IllegalArgumentException("Offset must not be negative.");
            columnName = name;
            columnWidth = width;
            columnOffset = offset;
        }
        
        public String getName() {
            return columnName;   
        }
        
        public int getWidth() {
            return columnWidth;   
        }
        
        public int getOffset() {
            return columnOffset;   
        }
    }
}
