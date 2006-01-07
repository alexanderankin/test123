package sidekick.java.node;


// an extension of TigerNode for an Interface
public class InterfaceNode extends ClassNode {
    public InterfaceNode( String name, int modifiers ) {
        super( name, modifiers );
    }
    
    public int getOrdinal() {
        return INTERFACE;   
    }
    
}


