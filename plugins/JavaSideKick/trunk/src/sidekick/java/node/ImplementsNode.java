package sidekick.java.node;


/**
 * an extension of TigerNode for an "implements" clause. jbrowse 
 * shows "implements" clauses as child nodes of class nodes. 
 */
public class ImplementsNode extends TigerNode {
    
    private Type type;
    
    public ImplementsNode( Type type ) {
        super( type.type, 0 );
        this.type = type;
    }

    public int getOrdinal() {
        return IMPLEMENTS;
    }

    public String getType() {
        return type == null ? "" : type.type;
    }
    
    public String getTypeParams() {
        return type == null ? "" : type.typeArgs;   
    }

    /**
     * Overridden to return false.    
     */
    public boolean canAdd( TigerNode node ) {
        return false;
    }
    

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( getStartLocation().line ).append( ": " ).append( ModifierSet.toString( getModifiers() ) ).append( " " ).append( type.toString() );
        return sb.toString();
    }
}


