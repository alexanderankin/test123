package sidekick.java.node;

/**
 * Accumulates the count of specific node types, that is, this keeps a count
 * of the number of classed, interfaces, methods, and fields that the parser
 * found.
 */
public class Results {
    // Parse Counters
    private int classCount = 0;
    private int interfaceCount = 0;
    private int methodCount = 0;
    private int referenceFieldCount = 0;    // count of fields that are some sort of Object, not a primitive
    private int primitiveFieldCount = 0;    // count of fields that are some sort of primitive, i.e. int, char, etc.


    // Accessor Methods
    public int getClassCount() {
        return classCount;
    }
    public int getInterfaceCount() {
        return interfaceCount;
    }
    public int getMethodCount() {
        return methodCount;
    }
    public int getReferenceFieldCount() {
        return referenceFieldCount;
    }
    public int getPrimitiveFieldCount() {
        return primitiveFieldCount;
    }


    // Increment
    public void incClassCount() {
        classCount++;
    }


    public void incInterfaceCount() {
        interfaceCount++;
    }


    /**
     * Used to count methods        
     */
    public void incMethodCount() {
        methodCount++;
    }


    /**
     * Used to count fields that are Objects, for example, <code>public static String DAY = "day";</code>        
     */
    public void incReferenceFieldCount() {
        referenceFieldCount++;
    }


    /**
     * Used to count fields that are primitives, for example, <code>private static int x = 2;</code>        
     */
    public void incPrimitiveFieldCount() {
        primitiveFieldCount++;
    }


    /**
     * This method resets all the result variables to their initial state,
     * i.e. all counts to 0, in anticipation of performing a new parse
     * which will use the result object to count what it finds.
     */
    void reset() {
        classCount = 0;
        interfaceCount = 0;
        methodCount = 0;
        referenceFieldCount = 0;
        primitiveFieldCount = 0;
    }


}
