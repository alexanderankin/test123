/*
 * TagParserTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.data.TableCell;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.core.matcher.JButtonMatcher;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestUtils;
import static xml.XMLTestUtils.*;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

import java.io.*;
import java.util.regex.Pattern;
import javax.swing.text.*;
import javax.swing.*;
import java.util.List;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

import static xml.parser.TagParser.*;
import xml.XmlParsedData;
import sidekick.SideKickParsedData;

/**
 * unit tests for TagParser
 * $Id$
 */
public class TagParserTest{
	private static File testData;
	
    @BeforeClass
    public static void setUpjEdit() throws IOException{
        TestUtils.beforeClass();
        testData = new File(System.getProperty("test_data")).getCanonicalFile();
        assertTrue(testData.exists());
    }
    
    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }
    
    @Test
    public void testIsInsideTagNoStart(){
    	String text = "1 > 0";
    	assertFalse(TagParser.isInsideTag(text,0)); // |1 > 0
    	assertFalse(TagParser.isInsideTag(text,2)); // 1 |> 0
    	assertFalse(TagParser.isInsideTag(text,3)); // 1 >| 0
    	assertFalse(TagParser.isInsideTag(text,4)); // 1 > |0
    	assertFalse(TagParser.isInsideTag(text,5)); // 1 > 0|
    }

    @Test
    public void testIsInsideTagSimple(){
    	String text = "<a>b <c>";
    	assertFalse(TagParser.isInsideTag(text,0)); // |<a>b <c>
    	assertTrue( TagParser.isInsideTag(text,1)); // <|a>b <c>
    	assertTrue( TagParser.isInsideTag(text,2)); // <a|>b <c>
    	assertFalse(TagParser.isInsideTag(text,3)); // <a>|b <c>
    	assertFalse(TagParser.isInsideTag(text,4)); // <a>b| <c>
    	assertTrue(TagParser.isInsideTag(text,6)); // <a>b <|c>
    }

    @Test
    public void testIsInsideTagGtInAttr(){
    	String text = "<a b='>'>";
    	assertTrue( TagParser.isInsideTag(text,1)); // <|a b='>'>
    	assertTrue( TagParser.isInsideTag(text,6)); // <a b='|>'>
    	assertTrue(TagParser.isInsideTag(text,7)); // <a b='>|'>
    	assertTrue(TagParser.isInsideTag(text,8)); // <a b='>'|>
    	assertFalse(TagParser.isInsideTag(text,9)); // <a b='>'>|
    }

    /* currently, TagParser does not distinguish between end of tag and greater than,
     * so the test fails
     */
    @Test
    public void testGetTagAtOffsetGtInAttr(){
    	String text = "<a b='>'> ";
    	assertNotNull( TagParser.getTagAtOffset(text,1)); // <|a b='>'>
    	assertNotNull( TagParser.getTagAtOffset(text,6)); // <a b='|>'>
    	assertNotNull( TagParser.getTagAtOffset(text,7)); // <a b='>|'>
    	assertNotNull( TagParser.getTagAtOffset(text,8)); // <a b='>'|>
    	assertNotNull( TagParser.getTagAtOffset(text,9)); // <a b='>'>|
    	assertNull( TagParser.getTagAtOffset(text,10)); // <a b='>'> |
    }
    
    /* success */
    @Test
    public void testGetTagAtOffsetSimple(){
    	String text = "<a> </a> <b c='d'/><br/>";
    	Tag t;
    	assertNull(TagParser.getTagAtOffset(text,-1));
    	assertNull(TagParser.getTagAtOffset(text,text.length()+1));
    	assertNull(TagParser.getTagAtOffset("short <",7));

    	t = TagParser.getTagAtOffset(text,1);             // <|a> </a> <b c='d'/><br/>
    	assertNotNull(t);
    	assertEquals(0,t.start);
    	assertEquals(3,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,2);             // <a|> </a> <b c='d'/><br/>
    	assertNotNull(t);
    	assertEquals(0,t.start);
    	assertEquals(3,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,2);             // <a>| </a> <b c='d'/><br/>
    	assertNotNull(t);
    	assertEquals(0,t.start);
    	assertEquals(3,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,4);             // <a> |</a> <b c='d'/><br/>
    	assertNull(t);

    	t = TagParser.getTagAtOffset(text,5);             // <a> <|/a> <b c='d'/><br/>
    	assertNotNull(t);
    	assertEquals(4,t.start);
    	assertEquals(8,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_END_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,8);             // <a> </a>| <b c='d'/><br/>
    	assertNotNull(t);
    	assertEquals(4,t.start);
    	assertEquals(8,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_END_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,12);             // <a> </a> <b |c='d'/><br/>
    	assertNotNull(t);
    	assertEquals(9,t.start);
    	assertEquals(19,t.end);
    	assertEquals("b",t.tag);
    	assertEquals(T_STANDALONE_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,19);             // <a> </a> <b c='d'/>|<br/>
    	assertNotNull(t);
    	assertEquals(9,t.start);
    	assertEquals(19,t.end);
    	assertEquals("b",t.tag);
    	assertEquals(T_STANDALONE_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,22);             // <a> </a> <b c='d'/><br|/>
    	assertNotNull(t);
    	assertEquals(19,t.start);
    	assertEquals(24,t.end);
    	assertEquals("br",t.tag);
    	assertEquals(T_STANDALONE_TAG,t.type);
    }

    /* success */
    @Test
    public void testGetTagAtOffsetComment(){
    	String text = "<a> <!-- comment --> </a>";
    	Tag t;

    	t = TagParser.getTagAtOffset(text,1);             // <|a> <!-- comment --> </a>";
    	assertNotNull(t);
    	
    	assertNull(TagParser.getTagAtOffset(text,5));             // <a> <|!-- comment --> </a>";
    	assertNull(TagParser.getTagAtOffset(text,6));             // <a> <!|-- comment --> </a>";
    	assertNull(TagParser.getTagAtOffset(text,10));             // <a> <!-- c|omment --> </a>";
    	assertNull(TagParser.getTagAtOffset(text,19));             // <a> <!-- comment --> </a>";
    	assertNull(TagParser.getTagAtOffset(text,20));             // <a> <!-- comment --> </a>";
    }

    /* success */
    @Test
    public void testGetTagAtOffsetNotClosed(){
    	String text = "<a <b> <c d='>";
    	Tag t;

    	t = TagParser.getTagAtOffset(text,1);             // <|a <b> <c d='>
    	assertNull(t);
    	
    	t = TagParser.getTagAtOffset(text,4);             // <a <|b> <c d='>
    	assertNotNull(t);
    	assertEquals(3,t.start);
    	assertEquals(6,t.end);
    	assertEquals("b",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	t = TagParser.getTagAtOffset(text,8);             // <a <b> <|c d='>
    	assertNull(t);
    }
    
    @Test
    public void testGetMatchingTagSimple(){
    	String text = "<a> <b/> <c d='toto'> titi </c> </a>";
    	
    	Tag f;
    	Tag t;
    	
    	// find closing tag for <a>
    	f = new Tag(0,3);
    	f.tag = "a";
    	f.type = T_START_TAG;
    	
    	t = TagParser.getMatchingTag(text, f);			  // <|a> <b/> <c d='toto'> titi </c> </a>
    	assertNotNull(t);
    	assertEquals(32,t.start);
    	assertEquals(36,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_END_TAG,t.type);

    	// find opening tag for </a>
    	t = TagParser.getMatchingTag(text, t);			  // <a> <b/> <c d='toto'> titi </c> </|a>
    	assertNotNull(t);
    	assertEquals(0,t.start);
    	assertEquals(3,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_START_TAG,t.type);
    	
    	// no matching tag for standalone
    	f = new Tag(4,8);
    	f.tag = "b";
    	f.type = T_STANDALONE_TAG;
    	t = TagParser.getMatchingTag(text, f);			  // <a> <|b/> <c d='toto'> titi </c> </a>
    	assertNull(t);
    }
    
    @Test
    public void testGetMatchingTagComment(){
    	String text = "<a> <!-- </a> <a> --> </a>";
    	
    	Tag f;
    	Tag t;
    	
    	// find closing tag for <a>, ignoring the commented one
    	f = new Tag(0,3);
    	f.tag = "a";
    	f.type = T_START_TAG;
    	
    	t = TagParser.getMatchingTag(text, f);
    	assertNotNull(t);
    	assertEquals(22,t.start);
    	assertEquals(26,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_END_TAG,t.type);

    	// find opening tag for </a>, ignoring the commented one
    	t = TagParser.getMatchingTag(text, t);
    	assertNotNull(t);
    	assertEquals(0,t.start);
    	assertEquals(3,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_START_TAG,t.type);
    }
    
    @Test
    public void testGetMatchingTagRecurse(){
    	String text = "<a> <a d='toto'> </a> </a>";
    	
    	Tag f;
    	Tag t;
    	
    	// find closing tag for <a>
    	f = new Tag(0,3);
    	f.tag = "a";
    	f.type = T_START_TAG;
    	
    	t = TagParser.getMatchingTag(text, f);
    	assertNotNull(t);
    	assertEquals(22,t.start);
    	assertEquals(26,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_END_TAG,t.type);

    	t = TagParser.getMatchingTag(text, t);
    	assertNotNull(t);
    	assertEquals(0,t.start);
    	assertEquals(3,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_START_TAG,t.type);
    }
    
    @Test
    public void testGetMatchingTagNoClose(){
    	String text = "<a>  </b>";
    	
    	Tag f;
    	Tag t;
    	
    	// find closing tag for <a> (not there)
    	f = new Tag(0,3);
    	f.tag = "a";
    	f.type = T_START_TAG;
    	
    	t = TagParser.getMatchingTag(text, f);
    	assertNull(t);

    	// find opening tag for </b> (not there)
    	f = new Tag(5,9);
    	f.tag = "b";
    	f.type = T_END_TAG;
    	
    	t = TagParser.getMatchingTag(text, f);
    	assertNull(t);
    }
    
    @Test                  
    public void testGetMatchingTagGt(){
    	String text = "<a> a>b </a>";
    	
    	Tag f;
    	Tag t;
    	
    	// find closing tag for <a> (ignoring a>b)
    	f = new Tag(0,3);
    	f.tag = "a";
    	f.type = T_START_TAG;
    	
    	t = TagParser.getMatchingTag(text, f);
    	assertNotNull(t);
    	assertEquals(8,t.start);
    	assertEquals(12,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_END_TAG,t.type);

    	// find opening tag for </a> (ignoring a>b)
    	t = TagParser.getMatchingTag(text, t);
    	assertNotNull(t);
    	assertEquals(0,t.start);
    	assertEquals(3,t.end);
    	assertEquals("a",t.tag);
    	assertEquals(T_START_TAG,t.type);
    }
    
    @Test                  
    public void testFindLastOpenTagXML(){
    	final File in = new File(testData,"tagparser/test.xml");
    	
    	Buffer b = openFile(in.getPath());
    	
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick-parse");
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
		XmlParsedData data = getXmlParsedData();
		String text = b.getText(0, b.getLength());
    	Tag t;
    	
    	t = TagParser.findLastOpenTag(text, 27 , data);    //line 2:   <p:p| xmlns:p="urn:p:">
    	assertNull(t);
    	
    	t = TagParser.findLastOpenTag(text, 45 , data);    //line 2:   <p:p xmlns:p="urn:p:">|
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(45,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = TagParser.findLastOpenTag(text, 49 , data);    //line 3:   <p:|eqn>a>b</p:eqn>
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(45,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = TagParser.findLastOpenTag(text, 55 , data);    //line 3:   <p:eqn>a>|b</p:eqn>
    	assertNotNull(t);
    	assertEquals(46,t.start);
    	assertEquals(53,t.end);
    	assertEquals("p:eqn",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = TagParser.findLastOpenTag(text, 84 , null);    //line 4:   <!-- some commented| <open>tag -->
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(45,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	// inside comments, matching still works but not after the comment
		t = TagParser.findLastOpenTag(text, 94 , data);    //line 4:   <!-- some commented <open>tag| -->
    	assertNotNull(t);
    	assertEquals(85,t.start);
    	assertEquals(91,t.end);
    	assertEquals("open",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = TagParser.findLastOpenTag(text, 114 , null);    //line 6:   |<attr c="a>d"/>
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(45,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	
    	// FAILS because of the GT !
		t = TagParser.findLastOpenTag(text, 125 , data);    //line 6:   <attr c="a>|d"/>
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(45,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

 		close(view(),b);
   }

   
    @Test                  
    public void testFindLastOpenTagHTML(){
    	final File in = new File(testData,"tagparser/test.html");
    	
    	Buffer b = openFile(in.getPath());
    	
    	// wait for end of parsing
    	parseAndWait();
		
		XmlParsedData data = getXmlParsedData();
		String text = b.getText(0, b.getLength());
    	Tag t;
    	
    	// after the img (standalone)
    	t = TagParser.findLastOpenTag(text, 56 , data);    //line 5:   na|vig
    	assertNotNull(t);
    	assertEquals(22,t.start);
    	assertEquals(37,t.end);
    	assertEquals("DIV",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	// after the embedded div (recursion)
		t = TagParser.findLastOpenTag(text, 67 , data);    //line 7:   |
    	assertNotNull(t);
    	assertEquals(7,t.start);
    	assertEquals(21,t.end);
    	assertEquals("div",t.tag);
    	assertEquals(T_START_TAG,t.type);
    	
    	// in HTML mode, tags are lower-cased for comparison, but not when returned
		t = TagParser.findLastOpenTag(text, 85 , data);    //line 9:   <p>hello <br>|</br>
    	assertNotNull(t);
    	assertEquals(68,t.start);
    	assertEquals(71,t.end);
    	assertEquals("P",t.tag);
    	assertEquals(T_START_TAG,t.type);
    	
    	close(view(),b);
    }
    
    @Test                  
    public void testFindLastOpenTagMalformed(){
		String text = "<html><p> not closed</html> ";
    	Tag t;
    	
    	// after the closing html, to trigger the if(unwindIndex == -1) return tag; line 190 of TagParser
    	t = TagParser.findLastOpenTag(text, 28 , null);    //  <html><p> not closed</html>| 
    	assertNotNull(t);
    	assertEquals(6,t.start);
    	assertEquals(9,t.end);
    	assertEquals("p",t.tag);
    	assertEquals(T_START_TAG,t.type);

    }

    @Test
    public void testGetAttrsContents()
    {
		String text = "<a title= 'a \"nice\" title' a:href=\"../test\" xmlns:a\n= 'urn:a&lt;b'>";
    	Tag t;
    	t = new Tag(0,text.length());
    	t.tag="a";
    	t.type=T_START_TAG;
    	
    	List<Attr> attrs = TagParser.getAttrs(text,t);
    	assertEquals(3,attrs.size());
    	
    	Attr a;
    	
    	a = attrs.get(0);
    	assertEquals("title",a.name);
    	assertEquals("'a \"nice\" title'",a.val);

    	a = attrs.get(1);
    	assertEquals("a:href",a.name);
    	assertEquals("\"../test\"",a.val);

    	a = attrs.get(2);
    	assertEquals("xmlns:a",a.name);
    	assertEquals("'urn:a&lt;b'",a.val);
    }

    @Test
    public void testGetAttrsStandaloneAndEmpty()
    {
		String text = "<a><img src='i.png'/></a>";
    	Tag t;
    	t = new Tag(0,3);
    	t.tag="a";
    	t.type=T_START_TAG;
    	
    	List<Attr> attrs;
    	Attr a;
    	
    	attrs = TagParser.getAttrs(text,t);
    	assertEquals(0,attrs.size());
    	
    	t = new Tag(3,21);
    	t.tag="img";
    	t.type=T_STANDALONE_TAG;
    	
    	attrs = TagParser.getAttrs(text,t);
    	assertEquals(1,attrs.size());

    	a = attrs.get(0);
    	assertEquals("src",a.name);
    	assertEquals("'i.png'",a.val);

    	t = new Tag(21,25);
    	t.tag="a";
    	t.type=T_END_TAG;
    	
    	attrs = TagParser.getAttrs(text,t);
    	assertEquals(0,attrs.size());

    }

    @Test
    public void testGetAttrsMalformed()
    {
		String text = "<i a=='b'>";
    	Tag t;
    	t = new Tag(0,10);
    	t.tag="i";
    	t.type=T_START_TAG;
    	
    	List<Attr> attrs;
    	Attr a;
    	
    	attrs = TagParser.getAttrs(text,t);
    	assertEquals(1,attrs.size());

    	a = attrs.get(0);
    	assertEquals("a",a.name);
    	assertEquals("'b'",a.val);

    	text = "<j 'no name'>";
    	t = new Tag(0,13);
    	t.tag="j";
    	t.type=T_START_TAG;
    	
    	attrs = TagParser.getAttrs(text,t);
    	System.err.println(attrs);
    	assertEquals(0,attrs.size());

    	text= "<k l='1' 'not closed>";
    	t = new Tag(0,21);
    	t.tag="k";
    	t.type=T_START_TAG;
    	
    	attrs = TagParser.getAttrs(text,t);
    	assertEquals(1,attrs.size());

    	a = attrs.get(0);
    	assertEquals("l",a.name);
    	assertEquals("'1'",a.val);

    	text = "<l &attrRef;>";
    	t = new Tag(0,13);
    	t.tag="l";
    	t.type=T_START_TAG;
    	
    	attrs = TagParser.getAttrs(text,t);
    	assertEquals(0,attrs.size());
    }

   	/**
   	 * utility method to return the XmlParsedData of current view/buffer
   	 * fails if data is not instance of XmlParsedData
   	 */
    public static XmlParsedData getXmlParsedData(){
    	Pause.pause(500);
  		SideKickParsedData _data = SideKickParsedData.getParsedData(view());
  		System.err.println("XMLParsedData:"+_data.getClass()+":"+_data);
		assertTrue("no XMLParsedData in current view/buffer",
			_data instanceof XmlParsedData);
		
		return (XmlParsedData)_data;
    }
}
