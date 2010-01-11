/*
Copyright (c) 2005, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
  * Neither the name of the <ORGANIZATION> nor the names of its contributors
  may be used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sidekick.java.node;


import javax.swing.*;
import javax.swing.text.Position;
import java.util.*;

import sidekick.Asset;
import sidekick.util.SideKickElement;
import sidekick.util.Location;

import org.gjt.sp.util.Log;

/**
 * Base class for all "nodes" in a file.  A single file is represented by a
 * CompilationUnit, which may contain one or more interfaces or classes, which
 * in turn may contain one or more fields and/or one or more methods and/or
 * one or more enum types, or any other kind of member element.  This base class
 * provides common functionality for all subtypes, including the name, modifiers,
 * and line number for the node, as well as parent/child relationships.
 * <p>
 * These are used as the UserObject in each TreeNode in the tree in the main
 * panel for JBrowse.
 * <p>
 * Locations and Positions: Asset uses Position, which is the offset from the
 * beginning of a Buffer.  TigerParser can provide line number and column, so
 * those are represented by Locations.  Locations can be converted to Positions
 * by calling Buffer.getLineOffset(Location.line) + Location.column.
 */
public class TigerNode extends Asset implements SideKickElement {

    // these values are used for sorting nodes and for easy type identification.
    public static final int ERROR = -1;
    public static final int COMPILATION_UNIT = 0;
    public static final int EXTENDS = 1;
    public static final int IMPLEMENTS = 2;
    public static final int CONSTRUCTOR = 4;
    public static final int METHOD = 8;
    public static final int THROWS = 16;
    public static final int CLASS = 32;     // default sort after methods for inner classes
    public static final int INTERFACE = 64;
    public static final int INITIALIZER = 96;
    public static final int FIELD = 128;
    public static final int ENUM = 256;
    public static final int IMPORT = 512;
    public static final int BLOCK = 1024;
    public static final int VARIABLE = 2048;
    public static final int PRIMARY_EXPRESSION = 2049;
    public static final int PARAMETER = 4096;
    public static final int TYPE = 8192;

    // these are used to sort javacc nodes
    public static final int OPTIONS = COMPILATION_UNIT;
    public static final int PARSER = 3;
    public static final int BNF_PRODUCTION = METHOD + 1;
    public static final int REGEX_PRODUCTION = BNF_PRODUCTION + 1;
    public static final int JAVA_PRODUCTION = BNF_PRODUCTION + 2;
    public static final int TOKEN_MGR_PRODUCTION = BNF_PRODUCTION + 3;


    // name for this node
    private String name;


    // modifiers, see ModifierSet
    private int modifiers = 0;

    // start end end locations for this node in the source file
    private Location startLocation = new Location();
    private Location endLocation = startLocation;

    // child nodes, may be null.
    private ArrayList<TigerNode> children;

    private Type type = null;

    // if filled in, this should contain the full package and class name of the
    // type of this node, e.g. java.lang.String or sidekick.java.node.TigerNode
    private String fullyQualifiedTypeName = null;

    // parent node, will only be null for CUNode, all other nodes will have a
    // parent node.
    private TigerNode parent;

    // should this node be visible in the tree display?
    private boolean isVisible = true;

    /**
     * Default constructor, no name, all modifiers, bad line number... assumes
     * name, modifiers, and line number will be set to valid values later.
     */
    public TigerNode() {
        this( "", 0 );
    }

    /**
     * @param name name for this node, e.g. for a method, this would be the
     * method name
     * @param modifiers see ModifierSet, this value indicates item modifiers
     * such as "public static final"
     */
    public TigerNode( String name, int modifiers ) {
        super( name );
        this.name = name;
        this.modifiers = modifiers;
    }

    public void setName( String name ) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setStartLocation( Location loc ) {
        startLocation = loc;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setEndLocation( Location loc ) {
        endLocation = loc;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Position getStart() {
        Position start = super.getStart();
        if ( start == null ) {
            return new Position() {
                       public int getOffset() {
                           return 0;
                       }
                   };
        }
        else {
            return start;
        }
    }

    public Position getStartPosition() {
        return getStart();
    }

    public void setStartPosition(Position p) {
        super.setStart(p);
    }


    public void setEndPosition(Position p) {
        setEnd(p);
    }

    public Position getEndPosition() {
        return super.getEnd();
    }

    public void setModifiers( int m ) {
        modifiers = m;
    }
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Ordinal is used for sorting nodes, can also be used as a type identifier.
     * Subclasses should override to set appropriate ordinal.
     */
    public int getOrdinal() {
        return Integer.MAX_VALUE;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getType() {
        return type == null ? "" : type.getType();
    }

    public String getTypeParams() {
        return type == null ? "" : type.getTypeParams();
    }

    public void setFullyQualifiedTypeName(String name) {
        fullyQualifiedTypeName = name;
    }

    public String getFullyQualifiedTypeName() {
        return fullyQualifiedTypeName;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * @return line: modifiers name, e.g. "83: public int getLineNumber"
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "<html>" ).append( startLocation.line ).append( ": " ).append( ModifierSet.toString( modifiers ) ).append( " " ).append( name );
        return sb.toString();
    }

    /**
     * Subclasses should override, default implementation returns true.  This
     * method is to check whether the given node can be added to this node as
     * a child node.
     * @param node the node to add
     * @return true if it is allowed to add the given node to this node
     */
    public boolean canAdd( TigerNode node ) {
        return true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean b) {
        isVisible = b;
    }

    /**
     * Add a child node to this node.  The parent of the child will be set to
     * this node.
     * @param child a TigerNode to add as a child, for example, a MethodNode may
     * be added to a ClassNode.  The child will be added only if <code>canAdd</code>
     * returns true.
     */
    public void addChild( TigerNode child ) {
        if ( child == null )
            return ;
        if ( canAdd( child ) ) {
            if ( children == null )
                children = new ArrayList<TigerNode>();
            child.setParent( this );
            children.add( child );
        }
        else {
            Log.log(Log.DEBUG, this, "Not allowed to add child: " + child.getClass().getName() + ", " + child.toString() + " to " + getClass().getName());
        }
    }

    /**
     * Add a bunch of child nodes at once.  Any elements of <code>kids</code> that
     * is not a TigerNode will be skipped.  The parent of each child will be set to
     * this node.
     * @param kids some TigerNodes to add as children of this node.
     */
    public void addChildren( List kids ) {
        if ( kids == null )
            return ;
        if ( children == null )
            children = new ArrayList<TigerNode>();
        for ( Iterator it = kids.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if ( ! ( o instanceof TigerNode ) ) {
                Log.log(Log.DEBUG, this, "Child not added, not a TigerNode: " + o.getClass().getName() + ", " + o.toString());
                continue;
            }
            TigerNode child = ( TigerNode ) o;
            addChild( child );
        }
    }

    /**
     * @return the child nodes of this node, may be null.  Nodes will be sorted
     * per the current sorting scheme.
     */
    public ArrayList<TigerNode> getChildren() {
        return children;
    }

    public int getChildCount() {
        return children == null ? 0 : children.size();
    }

    public TigerNode getChildAt( int index ) {
        if ( children == null )
            return null;
        return ( TigerNode ) children.get( index );
    }

    public void setParent( TigerNode p ) {
        parent = p;
    }

    public TigerNode getParent() {
        return parent;
    }

    /**
     * @return the CUNode that is the top-level parent of this node.
     */
    public CUNode getCompilationUnit() {
        TigerNode parent = getParent();
        while ( parent != null && parent.getOrdinal() != COMPILATION_UNIT ) {
            parent = parent.getParent();
        }
        return ( CUNode ) parent;
    }


    // for Asset
    public String getLongString() {
        return toString();
    }

    public String getShortString() {
        return TigerLabeler.getText( this );
    }

    public Icon getIcon() {
        return TigerLabeler.getIcon( this );
    }

    /**
     * Dumps the entire tree starting with this node.
     */
    public String dump() {
        return dump(0);
    }

    protected String dump(int level) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < level; i++) {
            tabs.append("\t");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tabs).append( this.getClass().getName()).append(":").append(this.toString() ).append( '\n' );
        List children = getChildren();
        if ( children != null ) {
            for ( Iterator it = children.iterator(); it.hasNext(); ) {
                TigerNode child = ( TigerNode ) it.next();
                sb.append(tabs).append(child.dump(level + 1));
            }
        }
        return sb.toString();
    }

}
