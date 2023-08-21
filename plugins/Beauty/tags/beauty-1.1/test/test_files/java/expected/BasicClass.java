package blah.var.foo;

import java.lang.annotation.ElementType;
import java.util.*;


public class BasicClass {
    // a field
    /* why */ private static final int C = 10;

    // a method
    public int getC() {
        switch (C) {
            case 1:
                // what
                return ++C;
            case 2:
                // do something
                y = C + z;
                break;
            case 3:

            default:
                return c;
        }
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

    void fn4() {
        // a switch expression
        fn1(
            switch (1) {
                case 1 -> 0;
                case 2 -> 2;
                default -> 1;
            }
        );
    }

}