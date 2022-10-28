// See http://docs.oracle.com/javase/7/docs/technotes/guides/language/underscores-literals.html
@Target({ElementType.TYPE, ElementType.TYPE_USE})
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
    /* why */ private static final int C = 10;

    // a method
    public int getC() {
        return C;
    }

    // a class
    static class SC1 {
        static final int C = 100;

    }

    // an enum
    enum E1 {
        ONE, TWO, THREE;

        Color() {
            colorMap.put(toString(), this);
        }
    }

}