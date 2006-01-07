package sidekick.java.node;



/**
 * an extension of TigerNode for an "extends" clause. jbrowse 
 * shows "extends" clauses as child nodes of class nodes. 
 */
public class ExtendsNode extends TigerNode {
    
    private Type type;
    
    public ExtendsNode( Type type ) {
        super( type.type, 0 );
        this.type = type;
    }

    public int getOrdinal() {
        return EXTENDS;
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


