package sidekick.markdown;

import javax.swing.text.Position;
import sidekick.util.Location;
import sidekick.util.SideKickElement;
import sidekick.Asset;
import javax.swing.Icon;

/**
 * Currently all nodes are header nodes, might need to change this if it is 
 * necessary to include items in the markdown file that are not header lines.
 */
public class Node extends Asset implements Comparable, SideKickElement {

    private String value = null;
    private Icon icon = null;
    private int level = -1;
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
    
    public Node() {
        super("");
    }
    
    public Node(String value) {
        super(value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public int getLevel() {
        return level;   
    }
    
    public void setLevel(int level) {
        this.level = level;   
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
    
    public Position getStart() {
        return startPosition;   
    }
    
    public Position getEndPosition() {
        return endPosition;
    }

    public void setEndPosition( Position p ) {
        endPosition = p;
    }

    public Position getEnd() {
        return endPosition;   
    }

    public String toString() {
        return getName();   
    }
    
    public String getShortString() {
        return toString();   
    }
    
    public String getLongString() {
        return toString();   
    }
    
    public Icon getIcon() {
        return icon;   
    }
    
    public void setIcon(Icon icon) {
        this.icon = icon;   
    }

    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
}
