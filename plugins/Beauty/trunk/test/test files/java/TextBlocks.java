
/**
 * https://openjdk.java.net/jeps/378
 */
class TextBlocks {

    void f(String s) {
    }

    void fn() {
        var s = """
                a \t
                \r""";

        var s2 = """
                a""" + """
                b""";

        var s3 = """
                """;

        f("""
                a""");

        f("""
                """);
    }

}
