package sidekick.property.parser.property;

import javax.swing.text.Position;
import sidekick.util.Location;
import sidekick.util.SideKickElement;

public class Property implements Comparable, SideKickElement {

    private String key = null;
    private String value = null;
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

    public Property() {
    }

    public Property(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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
        String k = key == null ? "" : key;
        String v = value == null ? "" : value;
        String p = k + " = " + v;
        return (p.equals(" = ") ? "" : p); // + " : " + getStartLocation() + ":" + getEndLocation() + ":" + getStartPosition().getOffset() + ":" + getEndPosition().getOffset();
    }

    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
}
