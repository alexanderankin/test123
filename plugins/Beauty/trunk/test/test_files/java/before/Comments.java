// See http://docs.oracle.com/javase/7/docs/technotes/guides/language/underscores-literals.html
@Target({ElementType.TYPE, ElementType.TYPE_USE})
public class LexerTest {
    /**
     * whatever is not a nice thing to say
     * but it is what it is
     */
    void /* WHATever */ whatever() {
        if (x == 6) {
            int y = 4;    // because 4 is nice

            // but 14 is not nice
            if (w == 14) {
                /* everyone likes 69 */
                z = 69;
            }
        }
        else {
            /* a comment */
            int w = 14;    // and another
        }

        if (x == 9) {
            /*
             * call doSim to do something
             */
            doSim();
        }

        for ( String part : parts) {
            doSomeStuff();    /* end of line regular comment */
            andMore();
            break;
        }

        for (int i = 0; i < 100; i++) {
            i++;
        }
        while (true) {
            System.out.println("+++++ it's true forever, never leave me!");
        }

        do {
            System.out.println("do it");
        }
        while (true);
    }

    /**
     Another doc comment,
     spanning several lines.
     @whatever just for fun
     @whynot because, just because
     */
    class C0 {
    /*
     * This is an empty class body, nothing to do here. This comment, however,
     * should be indented, and it's not.
     */
    // yet, it is full of inane comments
    // like this one
    /* yep */
    }

    final class FC1 extends SC1 /* because SC1 is extendable */ {
    }

}
