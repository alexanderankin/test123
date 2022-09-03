
@Target({ElementType.TYPE, ElementType.TYPE_USE})

// See http://docs.oracle.com/javase/7/docs/technotes/guides/language/underscores-literals.html
public class LexerTest {
    
    /**
     * whatever is not a nice thing to say
     * but it is what it is
     */
    void whatever() {
        if (y == 6) {
            int z = 12;   
        }
    }
    // a field
    final static /* why */ private int C = 10;

    // a method
    public int getC() {
        return C;   
    }
    
    // a class
    static class SC1 {
        final static int C = 100;
    }

    // an enum
    enum E1 {
        ONE, TWO, THREE;
        Color() { colorMap.put(toString(), this); }
    }
}