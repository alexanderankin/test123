
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
        jEdit.getPlugin( BeautyPlugin.class.getName() ).getPluginJAR().activatePluginIfNecessary();
    }

    @Test
    public void testCompilationUnit() {
        // basic test for package, import, and compilation unit
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "package test.more.whatever;\n" );
            sb.append( "\n" );
            sb.append( "import java.util.*;\n" );
            sb.append( "\n" );
            sb.append( "public class Test {\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'compilation unit' test failed, expected\n>" + sb.toString() + "<\nbut was:\n>" + after + "<", sb.toString().equals( after ) );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'if' test failed:\nexpected:\n>" + sb.toString() + "<\nbut was:\n>" + after + "<", sb.toString().equals( after ) );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'if' test 2 failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
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
            before.append( "        for (String s : people) {\n");
            before.append( "            print(s);\n");
            before.append( "            store(s);\n");
            before.append( "        }\n");
            before.append( "        for (String s : people)\n");
            before.append( "            print(s);\n");
            before.append( "    }\n" );
            before.append( "}\n" );
            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test {\n" );
            answer.append( "\n" );
            answer.append( "    public void goodFor() {\n" );
            answer.append( "        for (int i = 0; i < 10; i++) {\n" );
            answer.append( "            x = 1;\n" );
            answer.append( "        }\n" );
            answer.append( "        for (int i = 0; i < 10; i++) {\n" );
            answer.append( "            ;\n");
            answer.append( "        }\n");
            answer.append( "        for (String s : people) {\n");
            answer.append( "            print(s);\n");
            answer.append( "            store(s);\n");
            answer.append( "        }\n");
            answer.append( "        for (String s : people) {\n");
            answer.append( "            print(s);\n");
            answer.append( "        }\n");
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'for' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
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
            before.append( "        while (x != 7)\n");
            before.append( "            save(x);\n");
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
            answer.append( "            ;\n");
            answer.append( "        }\n");
            answer.append( "        while (x != 7) {\n");
            answer.append( "            save(x);\n");
            answer.append( "        }\n");
            answer.append( "    }\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'while' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'do/while' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after ) );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'switch' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
    
    @Test
    public void testComments() {
        // test the various comment constructs
        try {
            StringBuilder sb = new StringBuilder();
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
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

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
            answer.append( "    //// a comment\n" );
            answer.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'comment' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    //@Test
    public void testTryCatch() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "    public int tryCatch(int condition) {\n" );
            sb.append( "        try {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        } catch (ExceptionClass e) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "\n" );
            sb.append( "        try {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        } catch (ExceptionClass e) {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        } finally {\n" );
            sb.append( "            x = 1;\n" );
            sb.append( "        }\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'try/catch' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after ) );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'try with resources' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after ) );
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
            answer.append( "            ;\n");
            answer.append( "        }\n");
            answer.append( "        return (i > 10 ? 1 : -1);\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );

            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'keyword and method padding' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
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
            sb.append( "    @Test\n" );
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
            sb.append( "    @Test\n" );
            sb.append( "    public int markerAnnotation(int condition) {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
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
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'annotations' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    //@Test
    public void testBlankLines() {
        // should be one blank line before each method
        try {
            StringBuilder sb = new StringBuilder();
            sb.append( "public class Test {\n" );
            sb.append( "\n" );
            sb.append( "    //@Test\n" );
            sb.append( "    public int markerAnnotation(int condition) {\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    @SuppressWarnings(value = \"unchecked\")\n" );
            sb.append( "    public void methodName() {\n" );
            sb.append( "\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    @SuppressWarnings({\"unchecked\", \"deprecation\"})\n" );
            sb.append( "    public void methodName() {\n" );
            sb.append( "\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    //@Test\n" );
            sb.append( "    public int markerAnnotation(int condition) {\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );
            sb.append( "\n" );

            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( sb.toString() );
            assertTrue( "returned text was null", after != null );
            assertTrue( "'blank lines' test failed:\nexpected:\n" + sb.toString() + "\nbut was:\n" + after, sb.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    //@Test
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
            answer.append( "    int x = -1;\n" );
            answer.append( "    int y = i - j;\n" );
            answer.append( "    float z = +0.1;\n" );
            answer.append( "}\n" );

            Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            String after = beautifier.beautify( before.toString() );

            assertTrue( "'operator padding' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    //@Test
    public void setPadParens1() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test4 {\n" );
            before.append( "    public method1() {\n" );
            before.append( "        System.out.println();\n" );
            before.append( "        System.out.println( \"some text\" );\n" );
            before.append( "    }\n" );
            before.append( "}\n" );

            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test4 {\n" );
            answer.append( "    public method1() {\n" );
            answer.append( "        System.out.println();\n" );
            answer.append( "        System.out.println(\"some text\");\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );

            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            beautifier.setPadParens( false );
            String after = beautifier.beautify( before.toString() );

            assertTrue( "'padParens1' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    //@Test
    public void setPadParens2() {
        try {
            StringBuilder before = new StringBuilder();
            before.append( "public class Test4 {\n" );
            before.append( "    public method1() {\n" );
            before.append( "        System.out.println();\n" );
            before.append( "        System.out.println(\"some text\");\n" );
            before.append( "    }\n" );
            before.append( "}\n" );

            StringBuilder answer = new StringBuilder();
            answer.append( "public class Test4 {\n" );
            answer.append( "    public method1() {\n" );
            answer.append( "        System.out.println();\n" );
            answer.append( "        System.out.println( \"some text\" );\n" );
            answer.append( "    }\n" );
            answer.append( "}\n" );

            Java8Beautifier beautifier = new Java8Beautifier();
            beautifier.setEditMode( "java" );
            beautifier.setLineSeparator( "\n" );
            beautifier.setTabWidth(4 );
            beautifier.setIndentWidth(4 );
            beautifier.setUseSoftTabs( true );
            beautifier.setWrapMargin(80 );
            beautifier.setWrapMode( "none" );
            beautifier.setPadParens( true );
            String after = beautifier.beautify( before.toString() );

            assertTrue( "'padParens2' test failed:\nexpected:\n" + answer.toString() + "\nbut was:\n" + after, answer.toString().equals( after ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

}