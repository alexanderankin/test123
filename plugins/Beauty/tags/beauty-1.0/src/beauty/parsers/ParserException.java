package beauty.parsers;


public class ParserException extends Exception {
    
    private int line = -1;
    private int column = -1;
    private int length = -1;
    
    public ParserException() {
        super();
    }
    
    public ParserException(String message) {
        super(message);
    }
    
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ParserException(Throwable cause) {
        super(cause);   
    }
    
    public ParserException(String message, int line, int column, int length) {
        super(message);
        this.line = line;
        this.column = column;
        this.length = length;
    }
    
    public int getLineNumber() {
        return line;    
    }
    
    public int getColumn() {
        return column;    
    }
    
    public int getLength() {
        return length;   
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage());
        if (line > -1) {
            sb.append('\n').append("Line: ").append(line);   
        }
        if (column > -1) {
            sb.append('\n').append("Column: ").append(column);   
        }
        if (length > -1) {
            sb.append('\n').append("Length: ").append(length);   
        }
        return sb.toString();
    }
}
