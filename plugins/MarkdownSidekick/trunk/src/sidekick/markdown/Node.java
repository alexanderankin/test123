package sidekick.markdown;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.Position;
import sidekick.Asset;
import sidekick.util.Location;
import sidekick.util.SideKickElement;

/**
 * Currently all nodes are header nodes, might need to change this if it is 
 * necessary to include items in the markdown file that are not header lines.
 */
public class Node extends Asset implements Comparable, SideKickElement {

    private Icon icon = null;
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
    
    public Node(String name) {
        super(name);
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
        start = p;
    }
    
    public Position getEndPosition() {
        return endPosition;
    }

    public void setEndPosition( Position p ) {
        endPosition = p;
        end = p;
    }
    
    public void setIcon(ImageIcon icon) {
        this.icon = icon;   
    }

    public Icon getIcon() {
        return icon;   
    }
    
    public String toString() {
        return name; //getName() + '|' + getStartLocation() + '|' + getEndLocation() + '|' + getStartPosition().getOffset() + '|' + getEndPosition().getOffset();   
    }
    
    public String getShortString() {
        return name;   
    }
    
    public String getLongString() {
        return name + ": " + getStartLocation() + ":" + getEndLocation();   
    }
    
    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
}
