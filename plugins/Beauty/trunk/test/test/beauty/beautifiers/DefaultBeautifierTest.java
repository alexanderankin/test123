
package beauty.beautifiers;


import org.junit.*;
import static org.junit.Assert.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;

import beauty.BeautyPlugin;
import org.gjt.sp.jedit.testframework.TestUtils;


public class DefaultBeautifierTest {
 
    @BeforeClass public static void setUpjEdit() {
        TestUtils.beforeClass();
    }

 
    @AfterClass public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

 
    @Before public void beforeTest() {
        jEdit.getPlugin(BeautyPlugin .class.getName()).getPluginJAR().activatePluginIfNecessary();
    }

 
    @After public void afterTest() {
        jEdit.getPlugin(BeautyPlugin .class.getName()).getPluginJAR().deactivatePlugin(false);
    }
 
 
    @Test public void testPadTokens() {
        Buffer buffer = jEdit.newFile(jEdit.getActiveView());   
        String before = "a+b 1-1";
        String answer = "a + b 1 - 1 ";
        buffer.insert(0, before);
        buffer.setMode("java");
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setBuffer(buffer);
        db.setPrePadOperator(true);
        db.setPostPadOperator(true);
        db.setPrePadDigit(true);
        db.setPostPadDigit(true);
        String after = db.padTokens(new StringBuilder()).toString();
        assertTrue("Pad operators failed, expected >" + answer + "< but was >" + after + "<", answer.equals(after));
    }
 
 
    @Test public void testPrePadCharacters() {
        String before = 
            ".pageUserName{\n" +
            "	color:#5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName {\n" +
            "	color:#5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setPrePadCharacters("{");
        String after = db.prePadCharacters(new StringBuilder(before)).toString();
        assertTrue("prePadCharacters failed, expected >" + answer + "< but was >" + after + "<", answer.equals(after));
    }
    
    @Test public void testPostPadCharacters() {
        String before = 
            ".pageUserName{\n" +
            "	color:#5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName{\n" +
            "	color: #5c93c9, #5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setPostPadCharacters(":,");
        String after = db.postPadCharacters(new StringBuilder(before)).toString();
        assertTrue("postPadCharacters failed, expected >" + answer + "< but was >" + after + "<", answer.equals(after));
    }
    
    @Test public void testPreInsertLineSeparators() {
        // check that new line is inserted before {
        String before = 
            ".pageUserName{" +
            "	color:#5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName\n{" +
            "	color:#5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setLineSeparator("\n");
        db.setPreInsertLineCharacters("\\{");
        String after = db.preInsertLineSeparators(new StringBuilder(before)).toString();
        assertTrue("preInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));

        // more complex regex, check that new line is inserted before { and
        // the color definition
        before = 
            ".pageUserName{" +
            " color:#5c93c9,#5c93c9;}";
        answer = 
            ".pageUserName\n{" +
            "\n color:#5c93c9,#5c93c9;}";
 
        db = new DefaultBeautifier();
        db.setPreInsertLineCharacters("\\{,[ ].*?[:].*?[;]");
        after = db.preInsertLineSeparators(new StringBuilder(before)).toString();
        assertTrue("preInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
        
        // test that extra line is not inserted before }
        before = 
            ".pageUserName{" +
            " color:#5c93c9,#5c93c9;\n}";
        answer = 
            ".pageUserName{" +
            " color:#5c93c9,#5c93c9;\n}";
 
        db = new DefaultBeautifier();
        db.setPreInsertLineCharacters("\\}");
        after = db.preInsertLineSeparators(new StringBuilder(before)).toString();
        assertTrue("preInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
    
    @Test public void testPostInsertLineSeparators() {
        String before = 
            ".pageUserName{" +
            "	color:#5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName{\n" +
            "	color:#5c93c9,#5c93c9;\n}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setLineSeparator("\n");
        db.setPostInsertLineCharacters("\\{,;");
        String after = db.postInsertLineSeparators(new StringBuilder(before)).toString();
        assertTrue("postInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));

        before = 
            ".pageUserName{" +
            " color:#5c93c9,#5c93c9;}";
        answer = 
            ".pageUserName{\n" +
            " color:#5c93c9,#5c93c9;\n}";
 
        db = new DefaultBeautifier();
        db.setPostInsertLineCharacters("\\{,[ ].*?[:].*?[;]");
        after = db.postInsertLineSeparators(new StringBuilder(before)).toString();
        assertTrue("postInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));

        before = 
            ".pageUserName{\n" +
            " color:#5c93c9,#5c93c9;}\n";
        answer = 
            ".pageUserName{\n" +
            " color:#5c93c9,#5c93c9;}\n";
 
        db = new DefaultBeautifier();
        db.setPostInsertLineCharacters("\\}");
        after = db.postInsertLineSeparators(new StringBuilder(before)).toString();
        assertTrue("postInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
    
    @Test public void testPrePostInsertLineSeparators() {
        String before = 
            ".pageUserName{color:#5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName{color:#5c93c9,#5c93c9;\n}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setLineSeparator("\n");
        db.setPreInsertLineCharacters("\\}");
        db.setPostInsertLineCharacters(";");
        String after = db.preInsertLineSeparators(new StringBuilder(before)).toString();
        after = db.postInsertLineSeparators(new StringBuilder(after)).toString();
        assertTrue("pre/postInsertLineSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
    
    @Test public void testDontPrePadCharacters() {
        String before = 
            ".pageUserName{" +
            "	color : #5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName{" +
            "	color: #5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setLineSeparator("\n");
        db.setDontPrePadCharacters(":");
        String after = db.dontPrePadCharacters(new StringBuilder(before)).toString();
        assertTrue("dontPrePadCharacters failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
    
    @Test public void testDontPostPadCharacters() {
        String before = 
            ".pageUserName{" +
            "	color : #5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName{" +
            "	color :#5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setLineSeparator("\n");
        db.setDontPostPadCharacters(":");
        String after = db.dontPostPadCharacters(new StringBuilder(before)).toString();
        assertTrue("dontPostPadCharacters failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
    
    @Test public void testCollapseBlankLines() {
        String before = 
            ".pageUserName{\n\n\n\n\n\n\n\n" +
            "	color: #5c93c9,#5c93c9;}";
        String answer = 
            ".pageUserName{\n\n" +
            "	color: #5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier();
        db.setLineSeparator("\n");
        db.setCollapseBlankLines(true);
        String after = db.collapseBlankLines(new StringBuilder(before)).toString();
        assertTrue("collapseBlankLines failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
}