package sidekick.css.parser;

import java.util.*;
import javax.swing.text.Position;
import sidekick.util.*;
import org.gjt.sp.jedit.jEdit;

public class CSSNode implements SideKickElement, Comparable<CSSNode> {

    private String name = "";
    private List<CSSNode> children = new ArrayList<CSSNode>();
    private Location start = null;
    private Location end = null;
    private Position startPosition = null;
    private Position endPosition = null;

    public CSSNode() {}

    public CSSNode( String name ) {
        this.name = name;
    }

    public void setName( String name ) {
        if ( name != null ) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String name = getName();
        if (jEdit.getBooleanProperty( "sidekick.css.showLineNums", false )) {
            StringBuilder sb = new StringBuilder();
            sb.append(getStartLocation().line).append(": ").append(name);
            name = sb.toString();
        }
        return name;
    }

    public void setStartLocation( Location loc ) {
        start = loc;
    }

    public Location getStartLocation() {
        if ( start == null ) {
            start = new Location();
        }
        return start;
    }

    public void setEndLocation( Location loc ) {
        end = loc;
    }

    public Location getEndLocation() {
        if ( end == null ) {
            end = new Location();
        }
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
        Collections.sort(children);
        return children;
    }

    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    public boolean equals( Object o ) {
        return getName().equals( o ) && getChildren().equals(((CSSNode)o).getChildren());
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public int compareTo( CSSNode node ) {
        String my_name = getName().toLowerCase();
        if ( my_name.startsWith( "." ) || my_name.startsWith( "#" ) ) {
            my_name = my_name.substring( 1 );
        }
        String your_name = node.getName().toLowerCase();
        if ( your_name.startsWith( "." ) || your_name.startsWith( "#" ) ) {
            your_name = your_name.substring( 1 );
        }
        return my_name.compareTo( your_name );
    }
}