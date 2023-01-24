package sidekick.qdoc;

import java.util.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.Position;

import sidekick.Asset;
import sidekick.util.Location;
import sidekick.util.SideKickElement;


public class QdocNode extends Asset implements Comparable<QdocNode>, SideKickElement {
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

    /*
     * Ordinals indicate the depth this node should be in the tree:
     * 0 title
     * 1 section 1
     * 2 section 2
     * 3 section 3
     * 4 section 4
     * where larger numbers are children of the next smaller number.
     */
    private int ordinal = 0;

    private QdocNode parent = null;

    private List<QdocNode> children = null;

    // ordinal definitions
    public static int TITLE = 0;

    public static int SECTION1 = 1;

    public static int SECTION2 = 2;

    public static int SECTION3 = 3;

    public static int SECTION4 = 4;


    public QdocNode() {
        super("");
    }

    public QdocNode(String name) {
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

    public void setEndPosition(Position p) {
        endPosition = p;
        end = p;
    }

    public void setOrdinal(int i) {
        ordinal = i;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setParent(QdocNode parent) {
        this.parent = parent;
    }

    public QdocNode getParent() {
        return parent;
    }

    public void addChild(QdocNode node) {
        if (children == null) {
            children = new ArrayList<QdocNode>();
        }
        children.add(node);
    }

    public List<QdocNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return (children != null && !children.isEmpty());
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

    public int compareTo(QdocNode o) {
        if (o == null) {
            return 1;
        }
        return toString().compareToIgnoreCase(o.toString());
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ordinal; i++)  {
            sb.append('\t');
        }
        sb.append(getOrdinal()).append(name).append('\n');

        if (hasChildren()) {
            for ( QdocNode child : children) {
                sb.append(dump(child));
            }
        }
        return sb.toString();
    }

    private String dump(QdocNode node) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < node.getOrdinal(); i++)  {
            sb.append('\t');
        }
        sb.append(node.getOrdinal()).append(node.toString()).append('\n');

        if (node.hasChildren()) {
            for (QdocNode child : node.getChildren()) {
                sb.append(dump(child));
            }
        }
        return sb.toString();
    }
}