package sidekick.java.node;

// represents a parameter to a method or constructor, is not a TigerNode.
public class Parameter {
    
    // is the parameter final? e.g. methodA(final int x)
    public boolean isFinal = false;
    
    // the type
    public Type type = null;
    
    // is this parameter a vararg? e.g. methodA( Object... a)
    public boolean isVarArg = false;
    public String name = "";
    
}
