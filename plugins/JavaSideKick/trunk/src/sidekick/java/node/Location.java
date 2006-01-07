package sidekick.java.node;

/**
 * Class to represent a location in a source file by line and column.
 */
public class Location {
    public int line = 0;
    public int column = 0;
    
    public Location(){}
    
    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    public String toString() {
        return "Location:[line=" + line + ",column=" + column + "]";   
    }
}
