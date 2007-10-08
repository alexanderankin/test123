package sidekick.css.parser;

import java.util.*;
import javax.swing.text.Position;
import sidekick.util.*;

public class CSSNode implements SideKickElement {

    private String name = "";
    private List<CSSNode> children = new ArrayList<CSSNode>();
    private Location start = null;
    private Location end = null;
    private Position startPosition = null;
    private Position endPosition = null;

    public CSSNode() {
    }

    public CSSNode( String name ) {
        this.name = name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public void setStartLocation( Location loc ) {
        start = loc;
    }

    public Location getStartLocation() {
        if ( start == null )
            start = new Location();
        return start;
    }

    public void setEndLocation( Location loc ) {
        end = loc;
    }

    public Location getEndLocation() {
        if ( end == null )
            end = new Location();
        return end;
    }

    public void setStartPosition( Position s ) {
        startPosition = s;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setEndPosition( Position s ) {
        endPosition = s;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public void addChild( CSSNode child ) {
        children.add( child );
    }

    public void addChildren( List<CSSNode> kids ) {
        children.addAll( kids );
    }

    public List<CSSNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

}
