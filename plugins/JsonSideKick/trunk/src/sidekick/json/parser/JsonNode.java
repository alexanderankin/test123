package sidekick.json.parser;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.Position;
import sidekick.Asset;
import sidekick.util.Location;
import sidekick.util.SideKickElement;

public class JsonNode extends Asset implements Comparable, SideKickElement {

    private boolean isArray = false;
    private boolean isObject = false;
    private boolean isPair = false;
    private boolean isNumberOrString = false;
    private Icon icon = null;
    private TreeSet<JsonNode> children = null;
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
    
    public static int arrayCount = 0;
    public static int objectCount = 0;

    public JsonNode() {
        super("");
    }

    public JsonNode(String name) {
        super(name);
    }
    
	public boolean isArray() {
		return isArray;
	}

	public void setIsArray(boolean isArray) {
		this.isArray = isArray;
		++ arrayCount;
		setName("array_" + arrayCount);
	}

	public boolean isObject() {
		return isObject;
	}

	public void setIsObject(boolean isObject) {
		this.isObject = isObject;
		++ objectCount;
		setName("object_" + objectCount);
	}

	public boolean isPair() {
		return isPair;
	}

	public void setIsPair(boolean isPair) {
		this.isPair = isPair;
	}

	public boolean isNumberOrString() {
		return isNumberOrString;
	}

	public void setIsNumberOrString(boolean b) {
		this.isNumberOrString = b;
	}
    
    public void addChild(JsonNode child) {
        if (child == null) {
            return;   
        }
        if (children == null) {
            children = new TreeSet<JsonNode>();   
        }
        children.add(child);
    }
    
    public boolean hasChildren() {
        return children != null && !children.isEmpty();   
    }
    
    // this is useful when it is known that the node can have only one child 
    public JsonNode getFirstChild() {
        if (children == null || children.isEmpty()) {
            return null;   
        }
        return children.first();
    }
    
    public Set<JsonNode> getChildren() {
        return children;   
    }
    
    public void removeChildren() {
        children = null;   
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
        if (isArray() && name != null && name.startsWith("array_")) {
            return "";
        }
        if (isObject() && name != null && name.startsWith("object_")) {
            return "";
        }
        return name;   
    }
    
    public String getLongString() {
        String type = null;
        if (isObject())
            type = "object";
        else if (isArray())
            type = "array";
        else if (isPair())
            type = "pair";
        else if (isNumberOrString())
            type = "atom";
        return name + ": type=" + type + ":" + getStartLocation() + ":" + getEndLocation() + ":" + getStartPosition().getOffset() + ":" + getEndPosition().getOffset();   
    }

    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
    
    @Override
    public String getName() {
        if (name == null || name.isEmpty()) {
            name = "unknown_" + ++objectCount;   
        }
        return name;
    }
}
