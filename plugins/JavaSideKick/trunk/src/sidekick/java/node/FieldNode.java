package sidekick.java.node;


import java.util.*;


/**
 * An extension of TigerNode to represent a field.  This may be either a 
 * primitive field or a reference field. 
 * Note: in UML, this is known as an "attribute".  I avoid that usage in
 * TigerBrowser and use "field" as this is the term used throughout the
 * JVM Specification.
 */
public class FieldNode extends TigerNode {

    
    Type type = null;
    public FieldNode( String name, int modifiers, Type type ) {
        super( name, modifiers );
        this.type = type;
    }

	/**
	 * @returns true if this field represents a primitive type.    
	 */
    public boolean isPrimitive() {
        return type.isPrimitive;
    }
    
    public String getType() {
        return type == null ? "" : type.type;
    }
    
    public String getTypeParams() {
        return type == null ? "" : type.typeArgs;   
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append( ": " ).append( type.toString() );
        return sb.toString();
    }
    public int getOrdinal() {
        return FIELD;
    }
    
	/**
	 * Overridden to return false, a field may not have children.    
	 */
    public boolean canAdd(TigerNode node) {
        return false;   
    }
}


