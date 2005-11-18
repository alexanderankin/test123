package logviewer;

import java.util.*;

public class LogType {
    
    private String name = null;
    private ArrayList columns = new ArrayList();
    private String fileNameGlob = null;
    private String firstLineGlob = null;
    private String rowRegex = null;
    private boolean rowRegexInclude = true;
    private int rowRegexFlags = 0;
    private String rowSeparatorRegex = "\n";
    private String columnRegex = null;
    private String columnRegexGroups = null;
    private int columnRegexFlags = 0;
    private String columnDelimiter = null;
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("LogType [");
        sb.append("name=").append(name).append(",");
        sb.append("fileNameGlob=").append(fileNameGlob).append(",");
        sb.append("firstLineGlob=").append(firstLineGlob).append(",");
        sb.append("rowRegex=").append(rowRegex).append(",");
        sb.append("rowRegexInclude=").append(rowRegexInclude).append(",");
        sb.append("rowRegexFlags=").append(rowRegexFlags).append(",");
        sb.append("columnRegex=").append(columnRegex).append(",");
        sb.append("columnDelimiter=").append(columnDelimiter).append(",");
        sb.append("columnRegexGroups=").append(columnRegexGroups).append(",");
        sb.append("columnRegexFlags=").append(columnRegexFlags).append(",");
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
    
    public void setRowRegex(String regex, boolean include, int flags) {
        rowRegex = regex;
        rowRegexInclude = include;
        rowRegexFlags = flags;
    }
    
    public String getRowRegex() {
        return rowRegex;   
    }
    
    public boolean getRowInclude() {
        return rowRegexInclude;   
    }
    
    public int getRowFlags() {
        return rowRegexFlags;   
    }
    
    public void setRowSeparatorRegex(String regex) {
        rowSeparatorRegex = regex;   
    }
    
    public String getRowSeparatorRegex() {
        return rowSeparatorRegex;   
    }
    
    public void setColumnRegex(String regex, String groups, int flags) {
        columnRegex = regex;  
        columnRegexGroups = groups;
        columnRegexFlags = flags;
    }
    
    public String getColumnRegex() {
        return columnRegex;   
    }
    
    public String getColumnGroups() {
        return columnRegexGroups;   
    }
    
    public int getColumnFlags() {
        return columnRegexFlags;   
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
    
    public ArrayList getColumns() {
        return columns;   
    }
    
    public int getColumnCount() {
        return columns.size();   
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
            if (width < -1)
                throw new IllegalArgumentException("Width must not be negative.");
        }
        
        public Column(String name, int offset, int width) {
            if (width < -1)
                throw new IllegalArgumentException("Width must not be negative.");
            if (offset < -1)
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
