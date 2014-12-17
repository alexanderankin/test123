package sidekick.antlr4.parser;

public class ParseException extends Exception {
    private String message;
    private int lineNumber;
    private int column;
    private int length;
    
    public ParseException(String message, int lineNumber, int column, int length) {
        this.message = message;
        this.lineNumber = lineNumber;
        this.column = column;
        this.length = length;
    }
    
    public String getMessage() {
        return message;   
    }
    
    public int getLineNumber() {
        return lineNumber;   
    }
    
    public int getColumn() {
        return column;   
    }
    
    public int getLength() {
        return length;   
    }
}