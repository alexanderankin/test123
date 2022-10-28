/**
 * https://openjdk.java.net/jeps/394
 */
class PatternMatching4instanceof {
    void fn1(Number n) {
        if (n instanceof Long var) {
            var v = var;
        }
        else if (n instanceof Integer open) {
            var w = open;
        }
        else if (n instanceof Byte) {
            var x = closed();
        }
        else {
            throw new RuntimeException("");
        }

        if (!(n instanceof Long l)) {
            ;
        }

        if (n instanceof @Dummy @Dummy2 final Long l && l.byteValue() == 1 || n instanceof @Dummy @Dummy2 final Byte b && b.intValue() == 1) {
            ;
        }

        if (n instanceof Long) {
            ;
        }

        if (n instanceof Long var) {
            ;
        }

        if (n instanceof Long l) {
            ;
        }

        if (n instanceof final Long l) {
            ;
        }

        if (n instanceof @Dummy Long l) {
            ;
        }

        if (n instanceof @Dummy @Dummy2 Long l) {
            ;
        }

        if (n instanceof @Dummy final Long l) {
            ;
        }

        if (n instanceof @Dummy @Dummy2 final Long l) {
            ;
        }

        if (n instanceof @Dummy final Long l) {
            ;
        }

        if (n instanceof @Dummy @Dummy2 final Long l) {
            ;
        }
    }

}