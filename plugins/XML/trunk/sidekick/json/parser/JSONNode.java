package sidekick.json.parser;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.Position;
import sidekick.Asset;
import sidekick.util.Location;
import sidekick.util.SideKickElement;

public class JSONNode extends Asset implements Comparable, SideKickElement {

    private boolean isArray = false;
    private Icon icon = null;
    private Set<JSONNode> children = null;
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

    public JSONNode() {
        super("");
    }

    public JSONNode(String name) {
        super(name);
    }
    
    public void setIsArray(boolean b) {
        isArray = b;   
    }
    
    public boolean isArray() {
        return isArray;   
    }
    
    public void addChild(JSONNode child) {
        if (child == null) {
            return;   
        }
        if (children == null) {
            children = new TreeSet<JSONNode>();   
        }
        children.add(child);
    }
    
    public Set<JSONNode> getChildren() {
        return children;   
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
        return name;
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
