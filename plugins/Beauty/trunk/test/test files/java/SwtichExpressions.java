/**
 * https://openjdk.java.net/jeps/361
 */
class SwitchExpressions {

    final static private int C = 10;

    static class SC1 {
        final static int C = 100;
    }

    enum E1 {
        ONE;
    }

    int fn1(int n) {
        final int k = 4;
        var r = switch (n) {
            case 1, 2, 3 + 3, k, C, SC1.C -> 3 + SC1.C;
            case 20 -> 3 + 4 + C - k;
            case 21 -> {
                int ff = 222;
                yield ff;
            }
            case 22 -> {
                yield 33 + 3;
            }
            case 99 -> {
                throw new RuntimeException("");
            }
            default -> 0;
        };
        return r;
    }

    String fn2(String s) {
        return switch (s) {
            //case null -> "n";
            case "a" -> "";
            case "b", "c" -> "a";
            default -> "o";
        };
    }

    int fn3(final int var) {
        return switch (var) {
            case 1 -> 2;
            default -> var;
        };
    }

    void fn4() {

        fn1(switch (1) {
            case 1 -> 0;
            case 2 -> 2;
            default -> 1;
        });
    }

    int fn5() {
        E1 e = E1.ONE;
        return switch (e) {
            case ONE -> 0;
            //default -> 1;
        };
    }

    void fn6() {
        switch (1) {
            case 1 -> {

            }
        }
    }

    void fn7() {
        switch (1) {
            case 1 -> {
            }
            case 2 -> {
            }
        }
    }

    void fn8() {
        var i = 1;
        switch (1) {

        }
        var f = 2;
        switch (2) {
            case 2 -> {
                f = 3;
            }
        }
    }

    void fn9(String s) {
        switch (s) {
            case "" -> {
            }
            default -> {
            }
        }
    }

    void fn10() {
        var i = switch (1) {
            case 1 -> switch (2) {
                case 2 -> 0;
                default -> 2;
            };
            default -> 2;
        };
    }

    void fn11() {
        switch (1) {
            case 1 -> throw new RuntimeException("");
        }
    }

    int fn12() {
        var v = 1;
        int n = switch (1) {
            case 1:
                var g = 1;
                System.out.println();
                yield v;
            default:
                yield 3;
        };
        return n;
    }

    void fn13() {
        int n;
        switch (1) {
            case 1 -> n = 1;
        }
    }

    void fn14() {
        switch (1) {
            default -> {
            }
        }

        var n = 1;
        var m = switch (n) {
            case 1 -> 2;
            case 2 -> 2;
            default -> 1;
        };

        m = switch (n) {
            case 2:
                yield 2;
            default:
                yield 3;
        };


    }
}
