package beauty.beautifiers;

import org.junit.*;
import static org.junit.Assert.*;
import org.gjt.sp.jedit.jEdit;
import beauty.BeautyPlugin;
import org.gjt.sp.jedit.testframework.TestUtils;

public class JavaBeautifierTest {

    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
    }

    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

    @Before
    public void beforeTest() {
        jEdit.getPlugin( BeautyPlugin.class.getName()  ).getPluginJAR().activatePluginIfNecessary();
    }

    @Test
    public void testCompilationUnit() {
        // basic test for package, import, and compilation unit
        try {
            StringBuilder before = new StringBuilder();
            before.append( "package test.more.whatever;\n" );
            before.append( "\n" );
            before.append( "import java.util.*;\n" );
            before.append( "\n" );
            before.append( "public class Test {\n" );
            before.append( "\n" );
            before.append( "\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "package test.more.whatever;\n" );
            answer.append( "\n" );
            answer.append( "import java.util.*;\n" );
            answer.append( "\n" );       // default is 2 lines after import statements
            answer.append( "\n" );
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'compilation unit' test failed, expected\n>" + answer.toString() + "<\nbut was:\n>" + after + "<", answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testConstructor() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test4 {\n" );
            before.append( "    public Test4() {\n" );
            before.append( "        System.out.println();\n" );
            before.append( "        System.out.println(\"some text\");\n" );
            before.append( "    }\n" );
            before.append( "}\n" );                                                       
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test4 {\n" );
            answer.append( "\n" );
            answer.append( "    public Test4() {\n" );
            answer.append( "        System.out.println();\n" );
            answer.append( "        System.out.println( \"some text\" );\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            beautifier.setPadParens( true );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'constructor' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testIf() {
        // test the various if constructs
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    public void goodIf() {\n" );
            sb.append( "        if (condition) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "\n" );
            sb.append( "        if (condition) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        } else {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "\n" );
            sb.append( "        if (condition) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        } else if (condition) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        } else {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'if' test failed:\nexpected:\n>" + sb.toString() + "<\nbut was:\n>" + after + "<", sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testIf2() {
        // test that brackets are inserted in 'if' and 'else'
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test {\n" );
            before.append( "    public void goodIf() {\n" );
            before.append( "        if (condition) \n" );
            before.append( "            x = 1;\n" );
            before.append( "\n" );
            before.append( "        if (condition) \n" );
            before.append( "            x = 1;\n" );
            before.append( "        else \n" );
            before.append( "            x = 1;\n" );
            before.append( "\n" );
            before.append( "        if (condition) \n" );
            before.append( "            x = 1;\n" );
            before.append( "        else if (condition) \n" );
            before.append( "            x = 1;\n" );
            before.append( "        else\n" );
            before.append( "            x = 1;\n" );
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "    public void goodIf() {\n" );
            answer.append( "        if (condition) {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        }\n" );
            answer.append( "\n" );
            answer.append( "        if (condition) {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        } else {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        }\n" );
            answer.append( "\n" );
            answer.append( "        if (condition) {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        } else if (condition) {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        } else {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        }\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'if' test 2 failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testFor() {
        // test the 'for' constructs
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test {\n" );
            before.append( "    public void goodFor() {\n" );
            before.append( "        for (int i = 0; i < 10; i++) {\n" );
            before.append( "            x = 1;\n" );
            before.append( "        }\n" );
            before.append( "        for (int i = 0; i < 10; i++);\n" );
            before.append( "        for (String s : people) {\n" );
            before.append( "            print(s);\n" );
            before.append( "            store(s);\n" );
            before.append( "        }\n" );
            before.append( "        for (String s : people)\n" );
            before.append( "            print(s);\n" );
            before.append( "    }\n" );
            before.append( "}\n\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "    public void goodFor() {\n" );
            answer.append( "        for (int i = 0; i < 10; i++) {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        }\n" );
            answer.append( "        for (int i = 0; i < 10; i++) {\n" );
            answer.append( "            ;\n" );
            answer.append( "        }\n" );
            answer.append( "        for (String s : people) {\n" );
            answer.append( "            print(s);\n" );
            answer.append( "            store(s);\n" );
            answer.append( "        }\n" );
            answer.append( "        for (String s : people) {\n" );
            answer.append( "            print(s);\n" );
            answer.append( "        }\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'for' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testWhile() {
        // test the 'while' constructs
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test {\n" );
            before.append( "    public void goodWhile() {\n" );
            before.append( "        while (i < 10) {\n" );
            before.append( "            x = 1;\n" );
            before.append( "        }\n" );
            before.append( "        while (i < 10);\n" );
            before.append( "        while (x != 7)\n" );
            before.append( "            save(x);\n" );
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "    public void goodWhile() {\n" );
            answer.append( "        while (i < 10) {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        }\n" );
            answer.append( "        while (i < 10) {\n" );
            answer.append( "            ;\n" );
            answer.append( "        }\n" );
            answer.append( "        while (x != 7) {\n" );
            answer.append( "            save(x);\n" );
            answer.append( "        }\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'while' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testDoWhile() {
        // test the 'do/while' constructs
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    public void goodDoWhile() {\n" );
            sb.append( "        do {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        } while (i < 10);\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'do/while' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testSwitch() {
        // test the 'switch' construct
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    public void goodSwitch() {\n" );
            sb.append( "        switch (condition) {\n" );
            sb.append( "            case ABC:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "            /* falls through */\n" );
            sb.append( "            case DEF:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "                break;\n" );
            sb.append( "            case XYZ:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "                break;\n" );
            sb.append( "            default:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "                break;\n" );
            sb.append( "        }\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'switch' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testComments1() {
        // test the various comment constructs
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "/**\n" );
            sb.append( " * a javadoc comment\n" );
            sb.append( " * @param condition The switch condition\n" );
            sb.append( " * @return the condition\n" );
            sb.append( " */\n" );
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    /**\n" );
            sb.append( "     * a javadoc comment\n" );
            sb.append( "     * @param condition The switch condition\n" );
            sb.append( "     * @return the condition\n" );
            sb.append( "     */\n" );
            sb.append( "    public int goodSwitch(int condition) {\n" );
            sb.append( "        // a single line comment\n" );
            sb.append( "        switch (condition) {\n" );
            sb.append( "            case ABC:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "            /* falls through, a single line block comment */\n" );
            sb.append( "            case DEF:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "                break;\n" );
            sb.append( "            case XYZ:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "                break;\n" );
            sb.append( "            default:\n" );
            sb.append( "                x = 1;\n" );
            sb.append( "                break;\n" );
            sb.append( "        }\n\n" );
            sb.append( "        /*\n" );
            sb.append( "         * a multi-line\n" );
            sb.append( "         * block comment\n" );
            sb.append( "         * another line\n" );
            sb.append( "         */\n" );
            sb.append( "        return condition;\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            sb.append( "\n" );
            sb.append( "// end of file comment\n");
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test 1 failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
    
    @Test
    public void testComments2() {
        // test to allow single line comments with multiple slashes. It is a somewhat
        // common practice to use 3 or 4 slashes to indicate a temporary removal of
        // code or to mark a TODO.
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class CommentTest {\n" );
            before.append( "    ////a comment\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class CommentTest {\n" );
            answer.append( "\n" );
            answer.append( "    //// a comment\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test 2 failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testComments3() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class CommentTest {\n" );
            before.append( "    int a = 6; // a comment\n" );
            before.append( "    int b = 6;\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class CommentTest {\n" );
            answer.append( "\n" );
            answer.append( "    int a = 6;    // a comment\n" );
            answer.append( "    int b = 6;\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test 3 failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSingleLineComment() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test {\n" );
            before.append( "    public int goodSwitch(int condition) {\n" );
            before.append( "        //a single line comment\n" );
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "    public int goodSwitch(int condition) {\n" );
            answer.append( "        // a single line comment\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testSingleLineComment2() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test {\n" );
            before.append( "    public int goodSwitch(int condition) {\n" );
            before.append( "        //       a single line comment\n" );
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "    public int goodSwitch(int condition) {\n" );
            answer.append( "        // a single line comment\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testTryCatch() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    public int tryCatch(int condition) {\n" );
            sb.append( "        try {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "        catch (ExceptionClass e) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "\n" );
            sb.append( "        try {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "        catch (ExceptionClass e) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "        finally {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            beautifier.setBreakElse( true );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'try/catch' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testTryWithResources() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    public static void writeToFileZipFileContents(String zipFileName, String outputFileName) throws java.io.IOException {\n" );
            sb.append( "        java.nio.charset.Charset charset = java.nio.charset.StandardCharsets.US_ASCII;\n" );
            sb.append( "        java.nio.file.Path outputFilePath = java.nio.file.Paths.get(outputFileName);\n" );
            sb.append( "        // Open zip file and create output file with\n" );
            sb.append( "        // try-with-resources statement\n" );
            sb.append( "        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(zipFileName); java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(outputFilePath, charset)) {\n" );
            sb.append( "            // Enumerate each entry\n" );
            sb.append( "            for (java.util.Enumeration entries = zf.entries(); entries.hasMoreElements(); ) {\n" );
            sb.append( "                // Get the entry name and write it to the output file\n" );
            sb.append( "                String newLine = System.getProperty(\"line.separator\");\n" );
            sb.append( "                String zipEntryName = ((java.util.zip.ZipEntry)entries.nextElement()).getName() + newLine;\n" );
            sb.append( "                writer.write(zipEntryName, 0, zipEntryName.length());\n" );
            sb.append( "            }\n" );
            sb.append( "        }\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'try with resources' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testKeywordAndMethodPadding() {
        // keywords followed by a ( should have a space separating the keyword
        // and (.  Method names should not have a space between the name and
        // the (.
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test {\n" );
            before.append( "    public int noSpaceAfter (int condition) {\n" );
            before.append( "        // space after for, while, and return\n" );
            before.append( "        for(int i = 0; i < j; i++) {\n" );
            before.append( "            \n" );
            before.append( "        }\n" );
            before.append( "        while(true);\n" );
            before.append( "        return(i > 10 ? 1 : -1);\n" );
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "    public int noSpaceAfter(int condition) {\n" );
            answer.append( "        // space after for, while, and return\n" );
            answer.append( "        for (int i = 0; i < j; i++) {\n" );
            answer.append( "        }\n" );
            answer.append( "        while (true) {\n" );
            answer.append( "            ;\n" );
            answer.append( "        }\n" );
            answer.append( "        return (i > 10 ? 1 : -1);\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'keyword and method padding' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testAnnotations() {
        // test the various forms of annotations, including annotation type declarations
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    // @Test\n" );
            sb.append( "    public int markerAnnotation(int condition) {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    @SuppressWarnings(value = \"unchecked\")\n" );
            sb.append( "    public void methodName() {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    @SuppressWarnings({\"unchecked\", \"deprecation\"})\n" );
            sb.append( "    public void methodName() {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    // @Test\n" );
            sb.append( "    public int markerAnnotation(int condition) {\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            sb.append( "\n" );
            sb.append( "public @interface RequestForEnhancement {\n" );
            sb.append( "    int id();\n" );
            sb.append( "    String synopsis();\n" );
            sb.append( "    String engineer() default \"[unassigned]\";\n" );
            sb.append( "    String date() default \"[unimplemented]\";\n" );
            sb.append( "}\n" );
            sb.append( "\n" );
            sb.append( "@interface RequestForEnhancement {\n" );
            sb.append( "    int id();\n" );
            sb.append( "    String synopsis();\n" );
            sb.append( "    String engineer() default \"[unassigned]\";\n" );
            sb.append( "    String date() default \"[unimplemented]\";\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'annotations' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testBlankLines() {
        // should be one blank line before each method
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    // @Test\n" );
            sb.append( "    public int markerAnnotation(int condition) {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    @SuppressWarnings(value = \"unchecked\")\n" );
            sb.append( "    public void methodName() {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    @SuppressWarnings({\"unchecked\", \"deprecation\"})\n" );
            sb.append( "    public void methodName() {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    // // @Test\n" );
            sb.append( "    public int markerAnnotation(int condition) {\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'blank lines' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testOperatorPadding() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test4 {\n" );
            before.append( "    int x = - 1;\n" );
            before.append( "    int y = i-j;\n" );
            before.append( "    float z = +  0.1;\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test4 {\n" );
            answer.append( "\n" );
            answer.append( "    int x = -1;\n" );
            answer.append( "    int y = i - j;\n" );
            answer.append( "    float z = +0.1;\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'operator padding' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testPadParens1() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test4 {\n" );
            before.append( "    public void method1() {\n" );
            before.append( "        System.out.println();\n" );
            before.append( "        System.out.println( \"some text\" );\n" );
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test4 {\n" );
            answer.append( "\n" );
            answer.append( "    public void method1() {\n" );
            answer.append( "        System.out.println();\n" );
            answer.append( "        System.out.println(\"some text\");\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            beautifier.setPadParens( false );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'padParens1' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testPadParens2() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test4 {\n" );
            before.append( "    public void method1() {\n" );
            before.append( "        System.out.println();\n" );
            before.append( "        System.out.println(\"some text\");\n" );
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test4 {\n" );
            answer.append( "\n" );
            answer.append( "    public void method1() {\n" );
            answer.append( "        System.out.println();\n" );
            answer.append( "        System.out.println( \"some text\" );\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            beautifier.setPadParens( true );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'padParens2' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
    
    @Test
    public void testSortModifiers() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test4 {\n" );
            before.append( "    final static public void method1() {\n" );
            before.append( "        System.out.println();\n" );
            before.append( "        System.out.println(\"some text\");\n" );
            before.append( "    }\n" );
            before.append( "}\n" );                                            
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test4 {\n" );
            answer.append( "\n" );
            answer.append( "    public static final void method1() {\n" );
            answer.append( "        System.out.println();\n" );
            answer.append( "        System.out.println( \"some text\" );\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            beautifier.setPadParens( true );
            // default setting is to sort imports
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'sortModifiers' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testSortAndGroupImports() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "import          c.b.a;\n" );
            before.append( "import          b.a.c;\n" );
            before.append( "import static   a.c.b;\n" );
            before.append( "import javax.swing.table.*;\n" );
            before.append( "import javax.swing.*;\n" );
            before.append( "import javax.swing.event.*;\n" );
            before.append( "import java.awt.*;\n" );
            before.append( "import java.awt.event.*;\n" );
            before.append( "import java.io.*;\n" );
            before.append( "import java.util.*;\n" );
            before.append( "import org.gjt.sp.jedit.*;\n" );
            before.append( "import org.gjt.sp.jedit.msg.*;\n" );
            before.append( "import beauty.parsers.java.JavaParser;\n" );
            before.append( "import ise.java.awt.*;\n" );
            before.append( "\n" );
            before.append( "public class Test4 {\n" );
            before.append( "    public final static void method1() {\n" );
            before.append( "        System.out.println();\n" );
            before.append( "        System.out.println(\"some text\");\n" );
            before.append( "    }\n" );
            before.append( "}\n" );                                                        
            StringBuilder answer = new StringBuilder();
            answer.append( "import static a.c.b;\n" );
            answer.append( "\n" );
            answer.append( "import b.a.c;\n" );
            answer.append( "\n" );
            answer.append( "import beauty.parsers.java.JavaParser;\n" );
            answer.append( "\n" );
            answer.append( "import c.b.a;\n" );
            answer.append( "\n" );
            answer.append( "import ise.java.awt.*;\n" );
            answer.append( "\n" );
            answer.append( "import java.awt.*;\n" );
            answer.append( "import java.awt.event.*;\n" );
            answer.append( "import java.io.*;\n" );
            answer.append( "import java.util.*;\n" );
            answer.append( "\n" );
            answer.append( "import javax.swing.*;\n" );
            answer.append( "import javax.swing.event.*;\n" );
            answer.append( "import javax.swing.table.*;\n" );
            answer.append( "\n" );
            answer.append( "import org.gjt.sp.jedit.*;\n" );
            answer.append( "import org.gjt.sp.jedit.msg.*;\n" );
            answer.append( "\n\n" );        // default is 2 blank lines after imports
            answer.append( "public class Test4 {\n" );
            answer.append( "\n" );
            answer.append( "    public static final void method1() {\n" );
            answer.append( "        System.out.println();\n" );
            answer.append( "        System.out.println( \"some text\" );\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            beautifier.setPadParens( true );
            // default setting is to sort and group imports
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'sortAndGroupImports' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
    
    @Test
    public void testGenericsAndInterfaces() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public interface List <E> {\n" );
            before.append( "    void add(E x);\n" );
            before.append( "    Iterator<E> iterator();\n" );
            before.append( "}\n" );
            before.append( "public interface Iterator<E> {\n" );
            before.append( "    E next();\n" );
            before.append( "    boolean hasNext();\n" );
            before.append( "}\n" );
            before.append( "interface Collection<E> {\n" );
            before.append( "    public <T> boolean containsAll(Collection<T> c);\n" );
            before.append( "    public <T extends E> boolean addAll(Collection<T> c);\n" );
            before.append( "    // Hey, type variables can have bounds too!\n" );
            before.append( "}\n" );
            before.append( "public class Test {\n" );
            before.append( "    public void drawAll(List<? extends Shape> shapes) {\n" );
            before.append( "        for (Shape s: shapes) {\n" );
            before.append( "            s.draw(this);\n" );
            before.append( "       }\n" );
            before.append( "    }\n" );
            before.append( "    int a = 6;\n" );
            before.append( "    static <T> void fromArrayToCollection(T[] a, Collection<T> c) {\n" );
            before.append( "        for (T o : a) {\n" );
            before.append( "            c.add(o); // Correct\n" );
            before.append( "        }\n" );
            before.append( "    }\n" );
            before.append( "    int b = 7;\n" );
            before.append( "    Collection<EmpInfo>  emps = sqlUtility.select(EmpInfo.class, \"select * from emps\");\n" );
            before.append( "    public static <T> Collection<T> select(Class<T> c, String sqlStatement) {\n" ); 
            before.append( "    Collection<T> result = new ArrayList<T>();\n" );
            before.append( "    // Run sql query using jdbc.\n" );
            before.append( "    for (T r : result ) {\n" ); 
            before.append( "    T item = c.newInstance();\n" ); 
            before.append( "    result.add(item);\n" );
            before.append( "    }\n" ); 
            before.append( "    return result;\n" ); 
            before.append( "    }\n" ); 
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public interface List<E> {\n" ); 
            answer.append( "\n" ); 
            answer.append( "    void add(E x);\n" ); 
            answer.append( "    Iterator<E> iterator();\n" ); 
            answer.append( "}\n" ); 
            answer.append( "\n" ); 
            answer.append( "\n" ); 
            answer.append( "public interface Iterator<E> {\n" ); 
            answer.append( "\n" ); 
            answer.append( "    E next();\n" ); 
            answer.append( "    boolean hasNext();\n" ); 
            answer.append( "}\n" ); 
            answer.append( "\n" ); 
            answer.append( "\n" ); 
            answer.append( "interface Collection<E> {\n" ); 
            answer.append( "\n" ); 
            answer.append( "    public <T> boolean containsAll(Collection<T> c);\n" ); 
            answer.append( "    public <T extends E> boolean addAll(Collection<T> c);\n" ); 
            answer.append( "    // Hey, type variables can have bounds too!\n" ); 
            answer.append( "}\n" ); 
            answer.append( "\n" ); 
            answer.append( "\n" ); 
            answer.append( "public class Test {\n" ); 
            answer.append( "\n" ); 
            answer.append( "    public void drawAll(List<? extends Shape> shapes) {\n" ); 
            answer.append( "        for (Shape s : shapes) {\n" ); 
            answer.append( "            s.draw(this);\n" ); 
            answer.append( "        }\n" ); 
            answer.append( "    }\n" ); 
            answer.append( "\n" ); 
            answer.append( "    int a = 6;\n" ); 
            answer.append( "\n" ); 
            answer.append( "    static <T> void fromArrayToCollection(T[] a, Collection<T> c) {\n" ); 
            answer.append( "        for (T o : a) {\n" ); 
            answer.append( "            c.add(o);    // Correct\n" ); 
            answer.append( "        }\n" ); 
            answer.append( "    }\n" ); 
            answer.append( "\n" ); 
            answer.append( "    int b = 7;\n" ); 
            answer.append( "    Collection<EmpInfo> emps = sqlUtility.select(EmpInfo.class, \"select * from emps\");\n" ); 
            answer.append( "\n" ); 
            answer.append( "    public static <T> Collection<T> select(Class<T> c, String sqlStatement) {\n" ); 
            answer.append( "        Collection<T> result = new ArrayList<T>();\n" ); 
            answer.append( "        // Run sql query using jdbc.\n" ); 
            answer.append( "        for (T r : result) {\n" ); 
            answer.append( "            T item = c.newInstance();\n" ); 
            answer.append( "            result.add(item);\n" ); 
            answer.append( "        }\n" ); 
            answer.append( "        return result;\n" ); 
            answer.append( "    }\n" ); 
            answer.append( "}\n" ); 
            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'genericsAndInterfaces' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
    
    @Test
    public void testLambdas() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "package a.b.c;\n" ); 
            before.append( "\n" ); 
            before.append( "import java.util.*;\n" ); 
            before.append( "import java.util.function.Consumer;\n" ); 
            before.append( "import static java.lang.Math;\n" ); 
            before.append( "import static javax.swing.*;\n" ); 
            before.append( "\n" ); 
            before.append( "public abstract  class LambdaScopeTest {\n" ); 
            before.append( "    int a;\n" ); 
            before.append( "    public final static  int x = 0;\n" ); 
            before.append( "    public  String s = \"hello\";\n" ); 
            before.append( "    public  boolean b = true;\n" ); 
            before.append( "    public  String n = null;\n" ); 
            before.append( "    public  char c = 'c';\n" ); 
            before.append( "    public  float f = 1.234;\n" ); 
            before.append( "    public  String[] longArray = {\"apple\",\"banana\",\"carrot\",\"dill\",\"eggplant\"};\n" ); 
            before.append( "\n" ); 
            before.append( "    public LambdaScopeTest() {\n" ); 
            before.append( "        System.out.println(\"x = \" + x);    // Statement A\n" ); 
            before.append( "        System.out.println(\"+++++\");\n" ); 
            before.append( "        System.out.println(\"y = \" + y);\n" ); 
            before.append( "        System.out.println(\"this.x = \" + this.x);\n" ); 
            before.append( "        System.out.println(\"LambdaScopeTest.this.x = \" + LambdaScopeTest.this.x);\n" ); 
            before.append( "    }\n" ); 
            before.append( "\n" ); 
            before.append( "    class FirstLevel {\n" ); 
            before.append( "        // this comment is after the curly bracked and before public int\n" ); 
            before.append( "        public  int x = 1;\n" ); 
            before.append( "        private void methodA() {\n" ); 
            before.append( "            myConsumer.accept(x);\n" ); 
            before.append( "            Object o = (Object)myConsumer.whatever();\n" ); 
            before.append( "            return true;\n" ); 
            before.append( "        }\n" ); 
            before.append( "\n" ); 
            before.append( "        void methodInFirstLevel(int x) {\n" ); 
            before.append( "            // The following statement causes the compiler to generate\n" ); 
            before.append( "            // the error \"local variables referenced from a lambda expression\n" ); 
            before.append( "            // must be final or effectively final\" in statement A:\n" ); 
            before.append( "            //\n" ); 
            before.append( "            // x = 99;\n" ); 
            before.append( "            Consumer <Integer> myConsumer = (y) -> {\n" ); 
            before.append( "                System.out.println(\"x = \" + x);    // Statement B\n" ); 
            before.append( "                System.out.println(\"y = \" + y);\n" ); 
            before.append( "                System.out.println(\"this.x = \" + this.x);\n" ); 
            before.append( "                System.out.println(\"LambdaScopeTest.this.x = \" + LambdaScopeTest.this.x);\n" ); 
            before.append( "            };\n" ); 
            before.append( "            myConsumer.accept(x);\n" ); 
            before.append( "            Object o = (Object)myConsumer.whatever();\n" ); 
            before.append( "        }\n" ); 
            before.append( "    }\n" ); 
            before.append( "\n" ); 
            before.append( "    // main follows\n" ); 
            before.append( "    public static void main(String... args) {\n" ); 
            before.append( "        // this comment is after the curly bracked and before LambdaScopeTest\n" ); 
            before.append( "        LambdaScopeTest st = new LambdaScopeTest();\n" ); 
            before.append( "        // line 61\n" ); 
            before.append( "        LambdaScopeTest .FirstLevel fl = st.newFirstLevel();\n" ); 
            before.append( "        fl.methodInFirstLevel(23);\n" ); 
            before.append( "    }\n" ); 
            before.append( "}\n" ); 

            StringBuilder answer = new StringBuilder();
            answer.append( "package a.b.c;\n" ); 
            answer.append( "\n" ); 
            answer.append( "import static java.lang.Math;\n" ); 
            answer.append( "import java.util.*;\n" ); 
            answer.append( "import java.util.function.Consumer;\n" ); 
            answer.append( "\n" ); 
            answer.append( "import static javax.swing.*;\n" ); 
            answer.append( "\n" ); 
            answer.append( "\n" ); 
            answer.append( "public abstract class LambdaScopeTest {\n" ); 
            answer.append( "\n" ); 
            answer.append( "    int a;\n" ); 
            answer.append( "    public static final int x = 0;\n" ); 
            answer.append( "    public String s = \"hello\";\n" ); 
            answer.append( "    public boolean b = true;\n" ); 
            answer.append( "    public String n = null;\n" ); 
            answer.append( "    public char c = 'c';\n" ); 
            answer.append( "    public float f = 1.234;\n" ); 
            answer.append( "    public String[] longArray = {\"apple\", \"banana\", \"carrot\", \"dill\", \"eggplant\"};\n" ); 
            answer.append( "\n" ); 
            answer.append( "    public LambdaScopeTest() {\n" ); 
            answer.append( "        System.out.println(\"x = \" + x);    // Statement A\n" ); 
            answer.append( "        System.out.println(\"+++++\");\n" ); 
            answer.append( "        System.out.println(\"y = \" + y);\n" ); 
            answer.append( "        System.out.println(\"this.x = \" + this.x);\n" ); 
            answer.append( "        System.out.println(\"LambdaScopeTest.this.x = \" + LambdaScopeTest.this.x);\n" ); 
            answer.append( "    }\n" ); 
            answer.append( "\n" ); 
            answer.append( "\n" ); 
            answer.append( "\n" ); 
            answer.append( "    class FirstLevel {\n" ); 
            answer.append( "\n" ); 
            answer.append( "        // this comment is after the curly bracked and before public int\n" ); 
            answer.append( "        public int x = 1;\n" ); 
            answer.append( "\n" ); 
            answer.append( "        private void methodA() {\n" ); 
            answer.append( "            myConsumer.accept(x);\n" ); 
            answer.append( "            Object o = (Object)myConsumer.whatever();\n" ); 
            answer.append( "            return true;\n" ); 
            answer.append( "        }\n" ); 
            answer.append( "\n" ); 
            answer.append( "\n" );          
            answer.append( "        void methodInFirstLevel(int x) {\n" ); 
            answer.append( "            // The following statement causes the compiler to generate\n" ); 
            answer.append( "            // the error \"local variables referenced from a lambda expression\n" ); 
            answer.append( "            // must be final or effectively final\" in statement A:\n" ); 
            answer.append( "            //\n" ); 
            answer.append( "            // x = 99;\n" ); 
            answer.append( "            Consumer<Integer> myConsumer = (y) -> {\n" ); 
            answer.append( "                System.out.println(\"x = \" + x);    // Statement B\n" ); 
            answer.append( "                System.out.println(\"y = \" + y);\n" ); 
            answer.append( "                System.out.println(\"this.x = \" + this.x);\n" ); 
            answer.append( "                System.out.println(\"LambdaScopeTest.this.x = \" + LambdaScopeTest.this.x);\n" ); 
            answer.append( "            };\n" ); 
            answer.append( "            myConsumer.accept(x);\n" ); 
            answer.append( "            Object o = (Object)myConsumer.whatever();\n" ); 
            answer.append( "        }\n" ); 
            answer.append( "    }\n" ); 
            answer.append( "\n" ); 
            answer.append( "    // main follows\n" ); 
            answer.append( "    public static void main(String... args) {\n" ); 
            answer.append( "        // this comment is after the curly bracked and before LambdaScopeTest\n" ); 
            answer.append( "        LambdaScopeTest st = new LambdaScopeTest();\n" ); 
            answer.append( "        // line 61\n" ); 
            answer.append( "        LambdaScopeTest.FirstLevel fl = st.newFirstLevel();\n" ); 
            answer.append( "        fl.methodInFirstLevel(23);\n" ); 
            answer.append( "    }\n" ); 
            answer.append( "}\n" ); 


            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'lambdas' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
  
    @Test
    public void testArraysAndDims() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "class Test {\n" ); 
            before.append( "    void method1() {\n" ); 
            before.append( "        int @NotNull[] a;\n" ); 
            before.append( "        grid[l][t].textWidthPix = accTabstop;\n" ); 
            before.append( "        grid[l][t] = new ETTabstop();\n" );
            before.append( "        String sa[] = new String[2];\n" ); 
            before.append( "        sa[0] = \"he\";\n" );  
            before.append( "        sa[1] = \"llo\";\n" ); 
            before.append( "        String[] sd = {\"apple\", \"banana\", \"carrot\", \"dill\", \"eggplant\"};\n" );   
            before.append( "        String[] sb = {\n" ); 
            before.append( "            \"e\", \"f\",\n" );  
            before.append( "            \"g\", \"h\"\n" ); 
            before.append( "        };\n" ); 
            before.append( "        String[] sc = {\"a\", \"b\",\n" );  
            before.append( "            \"c\", \"d\"};\n" ); 
            before.append( "        System.out.println(sa[0] + sa[1]);\n" ); 	
            before.append( "        Point a[] = { new Point(0,0), new Point(1,1) };\n" ); 
            before.append( "        String[][] names = {{\"Mr. \", \"Mrs. \", \"Ms. \"},\n" );             
            before.append( "        {\"Smith\", \"Jones\"}};\n" ); 
            before.append( "    }\n" );                                                               
            before.append( "}\n" ); 

            StringBuilder answer = new StringBuilder();
            answer.append( "class Test {\n" );
            answer.append( "\n" );
            answer.append( "\n" );
            answer.append( "    void method1() {\n" );
            answer.append( "        int @NotNull[] a;\n" );
            answer.append( "        grid[l][t].textWidthPix = accTabstop;\n" );
            answer.append( "        grid[l][t] = new ETTabstop();\n");
            answer.append( "        String sa[] = new String [2];\n" );
            answer.append( "        sa[0] = \"he\";\n" );
            answer.append( "        sa[1] = \"llo\";\n" );
            answer.append( "        String[] sd = {\"apple\", \"banana\", \"carrot\", \"dill\", \"eggplant\"};\n" );
            answer.append( "        String[] sb = {\n" );
            answer.append( "            \"e\", \"f\",\n" );
            answer.append( "            \"g\", \"h\"\n" );
            answer.append( "        };\n" );
            answer.append( "        String[] sc = {\n" );
            answer.append( "            \"a\", \"b\",\n" );
            answer.append( "            \"c\", \"d\"\n" );
            answer.append( "        };\n" );
            answer.append( "        System.out.println(sa[0] + sa[1]);\n" );
            answer.append( "        Point a[] = {new Point(0, 0), new Point(1, 1)};\n" );
            answer.append( "        String[][] names = {\n" );
            answer.append( "            {\"Mr. \", \"Mrs. \", \"Ms. \"},\n" );
            answer.append( "            {\"Smith\", \"Jones\"}\n" );
            answer.append( "        };\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );

            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "'arrays and dims' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
 
    @Test
    public void testPrimary() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    public static void main(String[] args) {\n" );
            sb.append( "        caretPositions[i] = editPanes[i].getTextArea().getCaretPosition().getSize().getWidth();\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth( 4 );
            beautifier.setIndentWidth( 4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin( 80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'testPrimary' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after )  );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
    
}
