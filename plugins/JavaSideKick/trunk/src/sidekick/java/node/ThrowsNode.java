package sidekick.java.node;


// an extension of TigerNode for a "throws" clause. jbrowse shows 'throws' 
// clauses as child nodes of method and constructor nodes.
public class ThrowsNode extends TigerNode {

    public ThrowsNode(String name) {
        super(name, 0);
    }

    public int getOrdinal() {
        return THROWS;
    }

	/**
	 * Overridden to return false.    
	 */
    public boolean canAdd(TigerNode node) {
        return false;   
    }

}


