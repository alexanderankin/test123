package sidekick.java.node;


/**
 * An extension of TigerNode to represent a static initializer block.
 */
public class InitializerNode extends TigerNode {

    public InitializerNode( int line ) {
        super( "static_" + line, 0 );
    }

    public int getOrdinal() {
        return INITIALIZER;
    }
    
	/**
	 * Overridden to return false, a static initializer may not have children.    
	 */
    public boolean canAdd(TigerNode node) {
        return false;   
    }
    
}


