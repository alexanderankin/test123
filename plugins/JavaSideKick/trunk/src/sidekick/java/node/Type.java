package sidekick.java.node;

// represents a type, such as "int" or "Object", also includes generic type
// arguments
public class Type {
    
    // is the type a primitive, e.g. "int", "short", etc.
    public boolean isPrimitive = false;
    
    // name of the type, e.g. "int", "String", etc
    public String type = "";
    
    // generic type parameters, e.g. the "<String>" in "List<String>"
    public String typeArgs = "";
    
    public String toString() {
        return type + typeArgs;   
    }
}

