package sidekick.java.node;



// an extension of TigerNode for an enum
public class EnumNode extends TigerNode {
    public EnumNode( String name, int modifiers ) {
        super( name, modifiers );
    }

    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append( ": enum" );
        return sb.toString();
    }
    public int getOrdinal() {
        return ENUM;
    }
    
	/**
	 * Overridden to return false.    
	 */
    public boolean canAdd(TigerNode node) {
        return false;   
    }
}


