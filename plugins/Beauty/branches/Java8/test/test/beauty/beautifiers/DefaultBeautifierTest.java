
package beauty.beautifiers;


import org.junit.*;
import static org.junit.Assert.*;

import org.gjt.sp.jedit.jEdit;

import beauty.BeautyPlugin;
import org.gjt.sp.jedit.testframework.TestUtils;

//import java.io.File;
import java.util.List;

public class DefaultBeautifierTest {
 
    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
        jEdit.setProperty("mode.css.beauty.beautifier", "css.custom");
    }

 
    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }

 
    @Before
    public void beforeTest() {
        jEdit.getPlugin(BeautyPlugin .class.getName()).getPluginJAR().activatePluginIfNecessary();
    }

 
 
    @After
    public void afterTest() {
        //jEdit.getPlugin(BeautyPlugin .class.getName()).getPluginJAR().deactivatePlugin(false);
    }
 
    @Test
    public void testParseTokens() {
        String before = ".pageUserName{\n" + "color:#5c93c9,#5c93c9;}";
        DefaultBeautifier db = new DefaultBeautifier("css");
        List<DefaultBeautifier.PToken> tokens = db.parseTokens(new StringBuilder(before));
        assertTrue("Token count: " + tokens.size(), tokens.size() == 2);

        before = ".pageUserName{\n" + "	color:#5c93c9,#5c93c9; } /* a com {} ment */";
        tokens = db.parseTokens(new StringBuilder(before));
        assertTrue("Token count: " + tokens.size(), tokens.size() == 3);

        before = ".pageUserName{\n" + "	color:#5c93c9,#5c93c9; /* a com {} ment */ }";
        tokens = db.parseTokens(new StringBuilder(before));
        assertTrue("Token count: " + tokens.size(), tokens.size() == 4);
        
        StringBuilder sb = new StringBuilder();
        for (DefaultBeautifier.PToken token : tokens) {
            sb.append(token.tokenText);   
        }
        assertTrue("Reassembly failure 1", before.equals(sb.toString()));
        
        before = ".pageUserName{\n" + "	color:#5c93c9,#5c93c9;\n/* a comment\na line {} or two\nand another */\n }\n";
        tokens = db.parseTokens(new StringBuilder(before));
        sb = new StringBuilder();
        for (DefaultBeautifier.PToken token : tokens) {
            sb.append(token.tokenText);   
        }
        assertTrue("Reassembly failure 2, \n\nexpected:\n" + before + "\n\nhave:\n" + sb.toString(), before.equals(sb.toString()));
    }
 
    @Test
    public void testPadTokens() {
        String before = "a+b 1-1";
        String answer = "a + b 1 - 1 ";
 
        DefaultBeautifier db = new DefaultBeautifier("javascript");
        db.setPrePadOperator(true);
        db.setPostPadOperator(true);
        db.setPrePadDigit(true);
        db.setPostPadDigit(true);
        String after = db.padTokens(new StringBuilder(before)).toString();
        assertTrue("Pad operators failed, expected >" + answer + "< but was >" + after + "<", answer.equals(after));
    }
 
 
    @Test
    public void testPrePadCharacters() {
        String before = ".pageUserName{\n" + "	color:#5c93c9,#5c93c9;}";
        String answer = ".pageUserName {\n" + "	color:#5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setPrePadCharacters("{");
        String after = db.prePadCharacters(before);
        assertTrue("prePadCharacters failed, expected >" + answer + "< but was >" + after + "<", answer.equals(after));
    }
 
    @Test
    public void testPostPadCharacters() {
        String before = ".pageUserName{\n" + "	color:#5c93c9,#5c93c9;}";
        String answer = ".pageUserName{\n" + "	color: #5c93c9, #5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setPostPadCharacters(":,");
        String after = db.postPadCharacters(before);
        assertTrue("postPadCharacters failed, expected >" + answer + "< but was >" + after + "<", answer.equals(after));
    }
 
    @Test
    public void testPreInsertLineSeparators() {
        // check that new line is inserted before {
        String before = ".pageUserName{" + "	color:#5c93c9,#5c93c9;}";
        String answer = ".pageUserName\n{" + "	color:#5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setLineSeparator("\n");
        db.setPreInsertLineCharacters("\\{");
        String after = db.preInsertLineSeparators(before);
        assertTrue("preInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));

        // more complex regex, check that new line is inserted before { and
        // the color definition
        before = ".pageUserName{" + " color:#5c93c9,#5c93c9;}";
        answer = ".pageUserName\n{" + "\n color:#5c93c9,#5c93c9;}";
 
        db = new DefaultBeautifier("css");
        db.setPreInsertLineCharacters("\\{,[ ].*?[:].*?[;]");
        after = db.preInsertLineSeparators(before);
        assertTrue("preInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
 
        // test that extra line is not inserted before }
        before = ".pageUserName{" + " color:#5c93c9,#5c93c9;\n}";
        answer = ".pageUserName{" + " color:#5c93c9,#5c93c9;\n}";
 
        db = new DefaultBeautifier("css");
        db.setPreInsertLineCharacters("\\}");
        after = db.preInsertLineSeparators(before);
        assertTrue("preInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
 
    @Test
    public void testPostInsertLineSeparators() {
        String before = ".pageUserName{" + "	color:#5c93c9,#5c93c9;}";
        String answer = ".pageUserName{\n" + "	color:#5c93c9,#5c93c9;\n}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setLineSeparator("\n");
        db.setPostInsertLineCharacters("\\{,;");
        String after = db.postInsertLineSeparators(before);
        assertTrue("postInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));

        before = ".pageUserName{" + " color:#5c93c9,#5c93c9;}";
        answer = ".pageUserName{\n" + " color:#5c93c9,#5c93c9;\n}";
 
        db = new DefaultBeautifier("css");
        db.setPostInsertLineCharacters("\\{,[ ].*?[:].*?[;]");
        after = db.postInsertLineSeparators(before);
        assertTrue("postInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));

        before = ".pageUserName{\n" + " color:#5c93c9,#5c93c9;}\n";
        answer = ".pageUserName{\n" + " color:#5c93c9,#5c93c9;}\n";
 
        db = new DefaultBeautifier("css");
        db.setPostInsertLineCharacters("\\}");
        after = db.postInsertLineSeparators(before);
        assertTrue("postInsertLinseSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
 
    @Test
    public void testPrePostInsertLineSeparators() {
        String before = ".pageUserName{color:#5c93c9,#5c93c9;}";
        String answer = ".pageUserName{color:#5c93c9,#5c93c9;\n}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setLineSeparator("\n");
        db.setPreInsertLineCharacters("\\}");
        db.setPostInsertLineCharacters(";");
        String after = db.preInsertLineSeparators(before);
        after = db.postInsertLineSeparators(after);
        assertTrue("pre/postInsertLineSeparators failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
 
    @Test
    public void testDontPrePadCharacters() {
        String before = ".pageUserName{" + "	color : #5c93c9,#5c93c9;}";
        String answer = ".pageUserName{" + "	color: #5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setLineSeparator("\n");
        db.setDontPrePadCharacters(":");
        String after = db.dontPrePadCharacters(before);
        assertTrue("dontPrePadCharacters failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
 
    @Test
    public void testDontPostPadCharacters() {
        String before = ".pageUserName{" + "	color : #5c93c9,#5c93c9;}";
        String answer = ".pageUserName{" + "	color :#5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setLineSeparator("\n");
        db.setDontPostPadCharacters(":");
        String after = db.dontPostPadCharacters(before);
        assertTrue("dontPostPadCharacters failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
 
    @Test
    public void testCollapseBlankLines() {
        String before = ".pageUserName{\n\n\n\n\n\n\n\n" + "	color: #5c93c9,#5c93c9;}";
        String answer = ".pageUserName{\n" + "	color: #5c93c9,#5c93c9;}";
 
        DefaultBeautifier db = new DefaultBeautifier("css");
        db.setLineSeparator("\n");
        db.setCollapseBlankLines(true);
        String after = db.collapseBlankLines(before);
        assertTrue("collapseBlankLines failed, expected\n" + answer + "\nbut was\n" + after, answer.equals(after));
    }
 
}