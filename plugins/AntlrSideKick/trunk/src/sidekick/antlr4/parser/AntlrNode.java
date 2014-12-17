package sidekick.antlr4.parser;

import javax.swing.text.Position;
import sidekick.util.Location;
import sidekick.util.SideKickElement;

public class AntlrNode implements Comparable, SideKickElement {

    private String name = null;
    
    private Location startLocation = new Location();
    private Location endLocation = new Location();
    private Position startPosition = new Position() {
        public int getOffset() {
            return 0;
        }
    };
    private Position endPosition = new Position() {
        public int getOffset() {
            return 0;
        }
    };

    public AntlrNode() {
    }

    public AntlrNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;   
    }

    public void setStartLocation(Location start) {
        startLocation = start;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setEndLocation(Location end) {
        endLocation = end;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position p) {
        startPosition = p;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public void setEndPosition( Position p ) {
        endPosition = p;
    }

    public String toString() {
        return name;
    }

    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
}
