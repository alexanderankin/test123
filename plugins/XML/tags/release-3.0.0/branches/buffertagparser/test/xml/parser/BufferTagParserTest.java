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

import static xml.parser.BufferTagParser.*;
import xml.XmlParsedData;
import sidekick.SideKickParsedData;

/**
 * unit tests for BufferTagParser
 * $Id$
 */
public class BufferTagParserTest{
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
    	Buffer b = bufferWithText("xml",text);
    	try{
			assertFalse(BufferTagParser.isInsideTag(b,0)); // |1 > 0
			assertFalse(BufferTagParser.isInsideTag(b,2)); // 1 |> 0
			assertFalse(BufferTagParser.isInsideTag(b,3)); // 1 >| 0
			assertFalse(BufferTagParser.isInsideTag(b,4)); // 1 > |0
			assertFalse(BufferTagParser.isInsideTag(b,5)); // 1 > 0|
    	}finally{
    		close(view(),b);
    	}
    }

    @Test
    public void testIsInsideTagSimple(){
    	String text = "<a>b <c>";
    	Buffer b = bufferWithText("xml",text);
    	try{
			assertFalse(BufferTagParser.isInsideTag(b,0)); // |<a>b <c>
			assertTrue( BufferTagParser.isInsideTag(b,1)); // <|a>b <c>
			assertTrue( BufferTagParser.isInsideTag(b,2)); // <a|>b <c>
			assertFalse(BufferTagParser.isInsideTag(b,3)); // <a>|b <c>
			assertFalse(BufferTagParser.isInsideTag(b,4)); // <a>b| <c>
			assertTrue(BufferTagParser.isInsideTag(b,6)); // <a>b <|c>
		}finally{
			close(view(),b);
    	}
    }

    @Test
    public void testIsInsideTagGtInAttr(){
    	String text = "<a b='>'>";
    	Buffer b = bufferWithText("xml",text);
    	try{
			assertTrue( BufferTagParser.isInsideTag(b,1)); // <|a b='>'>
			assertTrue( BufferTagParser.isInsideTag(b,6)); // <a b='|>'>
			assertTrue(BufferTagParser.isInsideTag(b,7)); // <a b='>|'>
			assertTrue(BufferTagParser.isInsideTag(b,8)); // <a b='>'|>
			assertFalse(BufferTagParser.isInsideTag(b,9)); // <a b='>'>|
		}finally{
			close(view(),b);
    	}
    }

    @Test
    public void testGetTagAtOffsetGtInAttr(){
    	String text = "<a b='>'> ";
    	Buffer b = bufferWithText("xml",text);
    	try{
			assertNotNull( BufferTagParser.getTagAtOffset(b,1)); // <|a b='>'>
			assertNotNull( BufferTagParser.getTagAtOffset(b,6)); // <a b='|>'>
			assertNotNull( BufferTagParser.getTagAtOffset(b,7)); // <a b='>|'>
			assertNotNull( BufferTagParser.getTagAtOffset(b,8)); // <a b='>'|>
			assertNull( BufferTagParser.getTagAtOffset(b,9)); // <a b='>'>|
			assertNull(    BufferTagParser.getTagAtOffset(b,10)); // <a b='>'> |
		}finally{
			close(view(),b);
    	}
    }
    
    /* success */
    @Test
    public void testGetTagAtOffsetSimple(){
    	String text = "<a> </a> <b c='d'/><br/>";
    	Tag t;
    	Buffer b = bufferWithText("xml","short <");
    	try{
			assertNull(BufferTagParser.getTagAtOffset(b,-1));
			assertNull(BufferTagParser.getTagAtOffset(b,text.length()+1));
			assertNull(BufferTagParser.getTagAtOffset(b,7));
		}finally{
			close(view(),b);
		}
		
		b = bufferWithText("xml",text);
		try{
			t = BufferTagParser.getTagAtOffset(b,1);             // <|a> </a> <b c='d'/><br/>
			assertNotNull(t);
			assertEquals(0,t.start);
			/* end will be at '>', not after */
			assertEquals(2,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_START_TAG,t.type);
	
			t = BufferTagParser.getTagAtOffset(b,2);             // <a|> </a> <b c='d'/><br/>
			assertNotNull(t);
			assertEquals(0,t.start);
			assertEquals(2,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_START_TAG,t.type);
	
			t = BufferTagParser.getTagAtOffset(b,2);             // <a>| </a> <b c='d'/><br/>
			assertNotNull(t);
			assertEquals(0,t.start);
			assertEquals(2,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_START_TAG,t.type);
	
			t = BufferTagParser.getTagAtOffset(b,4);             // <a> |</a> <b c='d'/><br/>
			assertNull(t);
	
			t = BufferTagParser.getTagAtOffset(b,5);             // <a> <|/a> <b c='d'/><br/>
			assertNotNull(t);
			assertEquals(4,t.start);
			assertEquals(7,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_END_TAG,t.type);
	
			t = BufferTagParser.getTagAtOffset(b,8);             // <a> </a>| <b c='d'/><br/>
			assertNull(t);
	
			t = BufferTagParser.getTagAtOffset(b,12);             // <a> </a> <b |c='d'/><br/>
			assertNotNull(t);
			assertEquals(9,t.start);
			assertEquals(18,t.end);
			assertEquals("b",t.tag);
			assertEquals(T_STANDALONE_TAG,t.type);
	
			t = BufferTagParser.getTagAtOffset(b,19);             // <a> </a> <b c='d'/>|<br/>
			assertNull(t);
	
			t = BufferTagParser.getTagAtOffset(b,22);             // <a> </a> <b c='d'/><br|/>
			assertNotNull(t);
			assertEquals(19,t.start);
			assertEquals(23,t.end);
			assertEquals("br",t.tag);
			assertEquals(T_STANDALONE_TAG,t.type);
		}finally{
			close(view(),b);
    	}
    }

    /* success */
    @Test
    public void testGetTagAtOffsetComment(){
    	String text = "<a> <!-- comment --> </a>";
    	Tag t;
    	Buffer b = bufferWithText("xml",text);
    	try{
			t = BufferTagParser.getTagAtOffset(b,1);             // <|a> <!-- comment --> </a>";
			assertNotNull(t);
			
			assertNull(BufferTagParser.getTagAtOffset(b,5));             // <a> <|!-- comment --> </a>";
			assertNull(BufferTagParser.getTagAtOffset(b,6));             // <a> <!|-- comment --> </a>";
			assertNull(BufferTagParser.getTagAtOffset(b,10));             // <a> <!-- c|omment --> </a>";
			assertNull(BufferTagParser.getTagAtOffset(b,19));             // <a> <!-- comment --> </a>";
			assertNull(BufferTagParser.getTagAtOffset(b,20));             // <a> <!-- comment --> </a>";
		}finally{
			close(view(),b);
    	}
    }

    /* success */
    @Test
    public void testGetTagAtOffsetNotClosed(){
    	String text = "<a <b> <c d='>";
    	Tag t;
    	Buffer b = bufferWithText("xml",text);
    	try{
	
			t = BufferTagParser.getTagAtOffset(b,1);             // <|a <b> <c d='>
			assertNull(t);
			
			t = BufferTagParser.getTagAtOffset(b,4);             // <a <|b> <c d='>
			assertNotNull(t);
			assertEquals(3,t.start);
			assertEquals(5,t.end);
			assertEquals("b",t.tag);
			assertEquals(T_START_TAG,t.type);
	
			t = BufferTagParser.getTagAtOffset(b,8);             // <a <b> <|c d='>
			assertNull(t);
		}finally{
			close(view(),b);
    	}
    }
    
    @Test
    public void testGetMatchingTagSimple(){
    	String text = "<a> <b/> <c d='toto'> titi </c> </a>";
    	
    	Tag f;
    	Tag t;
    	
    	Buffer b = bufferWithText("xml",text);
    	try{
			// find closing tag for <a>
			f = new Tag(0,2);
			f.tag = "a";
			f.type = T_START_TAG;
			
			t = BufferTagParser.getMatchingTag(b, f);			  // <|a> <b/> <c d='toto'> titi </c> </a>
			assertNotNull(t);
			assertEquals(32,t.start);
			assertEquals(35,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_END_TAG,t.type);
	
			// find opening tag for </a>
			t = BufferTagParser.getMatchingTag(b, t);			  // <a> <b/> <c d='toto'> titi </c> </|a>
			assertNotNull(t);
			assertEquals(0,t.start);
			assertEquals(2,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_START_TAG,t.type);
			
			// no matching tag for standalone
			f = new Tag(4,7);
			f.tag = "b";
			f.type = T_STANDALONE_TAG;
			t = BufferTagParser.getMatchingTag(b, f);			  // <a> <|b/> <c d='toto'> titi </c> </a>
			assertNull(t);
		}finally{
			close(view(),b);
    	}
    }
    
    @Test
    public void testGetMatchingTagComment(){
    	String text = "<a> <!-- </a> <a> --> </a>";
    	
    	Tag f;
    	Tag t;
    	
    	Buffer b = bufferWithText("xml",text);
    	try{
			// find closing tag for <a>, ignoring the commented one
			f = new Tag(0,2);
			f.tag = "a";
			f.type = T_START_TAG;
			
			t = BufferTagParser.getMatchingTag(b, f);
			assertNotNull(t);
			assertEquals(22,t.start);
			assertEquals(25,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_END_TAG,t.type);
	
			// find opening tag for </a>, ignoring the commented one
			t = BufferTagParser.getMatchingTag(b, t);
			assertNotNull(t);
			assertEquals(0,t.start);
			assertEquals(2,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_START_TAG,t.type);
		}finally{
			close(view(),b);
    	}
    }
    
    @Test
    public void testGetMatchingTagRecurse(){
    	String text = "<a> <a d='toto'> </a> </a>";
    	
    	Tag f;
    	Tag t;
    	
    	Buffer b = bufferWithText("xml",text);
    	try{
    	// find closing tag for <a>
			f = new Tag(0,2);
			f.tag = "a";
			f.type = T_START_TAG;
			
			t = BufferTagParser.getMatchingTag(b, f);
			assertNotNull(t);
			assertEquals(22,t.start);
			assertEquals(25,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_END_TAG,t.type);
	
			t = BufferTagParser.getMatchingTag(b, t);
			assertNotNull(t);
			assertEquals(0,t.start);
			assertEquals(2,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_START_TAG,t.type);
		}finally{
			close(view(),b);
    	}
    }
    
    @Test
    public void testGetMatchingTagNoClose(){
    	String text = "<a>  </b>";
    	
    	Tag f;
    	Tag t;
    	
    	Buffer b = bufferWithText("xml",text);
    	try{
			// find closing tag for <a> (not there)
			f = new Tag(0,2);
			f.tag = "a";
			f.type = T_START_TAG;
			
			t = BufferTagParser.getMatchingTag(b, f);
			assertNull(t);
	
			// find opening tag for </b> (not there)
			f = new Tag(5,8);
			f.tag = "b";
			f.type = T_END_TAG;
			
			t = BufferTagParser.getMatchingTag(b, f);
			assertNull(t);
		}finally{
			close(view(),b);
    	}
    }
    
    @Test                  
    public void testGetMatchingTagGt(){
    	String text = "<a> a>b </a>";
    	
    	Tag f;
    	Tag t;
    	
    	Buffer b = bufferWithText("xml",text);
    	try{
			// find closing tag for <a> (ignoring a>b)
			f = new Tag(0,2);
			f.tag = "a";
			f.type = T_START_TAG;
			
			t = BufferTagParser.getMatchingTag(b, f);
			assertNotNull(t);
			assertEquals(8,t.start);
			assertEquals(11,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_END_TAG,t.type);
	
			// find opening tag for </a> (ignoring a>b)
			t = BufferTagParser.getMatchingTag(b, t);
			assertNotNull(t);
			assertEquals(0,t.start);
			assertEquals(2,t.end);
			assertEquals("a",t.tag);
			assertEquals(T_START_TAG,t.type);
		}finally{
			close(view(),b);
    	}
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
    	Tag t;
    	
    	t = BufferTagParser.findLastOpenTag(b, 27 , data);    //line 2:   <p:p| xmlns:p="urn:p:">
    	assertNull(t);
    	
    	t = BufferTagParser.findLastOpenTag(b, 45 , data);    //line 2:   <p:p xmlns:p="urn:p:">|
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(44,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = BufferTagParser.findLastOpenTag(b, 49 , data);    //line 3:   <p:|eqn>a>b</p:eqn>
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(44,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = BufferTagParser.findLastOpenTag(b, 55 , data);    //line 3:   <p:eqn>a>|b</p:eqn>
    	assertNotNull(t);
    	assertEquals(46,t.start);
    	assertEquals(52,t.end);
    	assertEquals("p:eqn",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = BufferTagParser.findLastOpenTag(b, 84 , null);    //line 4:   <!-- some commented| <open>tag -->
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(44,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	// inside comments, <open> is ignored
    	// contrary to TagParser
		t = BufferTagParser.findLastOpenTag(b, 94 , data);    //line 4:   <!-- some commented <open>tag| -->
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(44,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

		t = BufferTagParser.findLastOpenTag(b, 114 , null);    //line 6:   |<attr c="a>d"/>
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(44,t.end);
    	assertEquals("p:p",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	
    	// succeeds, contrary to TagParser
		t = BufferTagParser.findLastOpenTag(b, 125 , data);    //line 6:   <attr c="a>|d"/>
    	assertNotNull(t);
    	assertEquals(23,t.start);
    	assertEquals(44,t.end);
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
    	Tag t;
    	
    	// after the img (standalone)
    	t = BufferTagParser.findLastOpenTag(b, 56 , data);    //line 5:   na|vig
    	assertNotNull(t);
    	assertEquals(22,t.start);
    	assertEquals(36,t.end);
    	assertEquals("DIV",t.tag);
    	assertEquals(T_START_TAG,t.type);

    	// after the embedded div (recursion)
		t = BufferTagParser.findLastOpenTag(b, 67 , data);    //line 7:   |
    	assertNotNull(t);
    	assertEquals(7,t.start);
    	assertEquals(20,t.end);
    	assertEquals("div",t.tag);
    	assertEquals(T_START_TAG,t.type);
    	
    	// in HTML mode, tags are lower-cased for comparison, but not when returned
		t = BufferTagParser.findLastOpenTag(b, 85 , data);    //line 9:   <p>hello <br>|</br>
    	assertNotNull(t);
    	assertEquals(68,t.start);
    	assertEquals(70,t.end);
    	assertEquals("P",t.tag);
    	assertEquals(T_START_TAG,t.type);
    	
    	close(view(),b);
    }
    
    @Test                  
    public void testFindLastOpenTagMalformed(){
		String text = "<html><p> not closed</html> ";
    	Tag t;
    	
     	Buffer b = bufferWithText("xml",text);
    	try{
    		// after the closing html, to trigger the if(unwindIndex == -1) return tag; line 190 of TagParser
			t = BufferTagParser.findLastOpenTag(b, 28 , null);    //  <html><p> not closed</html>| 
			assertNotNull(t);
			assertEquals(6,t.start);
			assertEquals(8,t.end);
			assertEquals("p",t.tag);
			assertEquals(T_START_TAG,t.type);
		}finally{
			close(view(),b);
    	}
    }

    @Test
    public void testGetAttrsContents()
    {
		String text = "<a title= 'a \"nice\"\n title'\n a:href=\"../test\" xmlns:a\n= 'urn:a&lt;b'>";
    	Buffer b = bufferWithText("xml",text);
    	try{
			Tag t;
			t = new Tag(0,text.length()-1);
			t.tag="a";
			t.type=T_START_TAG;
			
			List<Attr> attrs = BufferTagParser.getAttrs(b,t);
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
		}finally{
			close(view(),b);
    	}
    }

    @Test
    public void testGetAttrsStandaloneAndEmpty()
    {
		String text = "<a><img src='i.png'/></a>";
    	Buffer b = bufferWithText("xml",text);
    	try{
			Tag t;
			t = new Tag(0,2);
			t.tag="a";
			t.type=T_START_TAG;
			
			List<Attr> attrs;
			Attr a;
			
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(0,attrs.size());
			
			t = new Tag(3,20);
			t.tag="img";
			t.type=T_STANDALONE_TAG;
			
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(1,attrs.size());
	
			a = attrs.get(0);
			assertEquals("src",a.name);
			assertEquals("'i.png'",a.val);
	
			t = new Tag(21,24);
			t.tag="a";
			t.type=T_END_TAG;
			
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(0,attrs.size());
		}finally{
			close(view(),b);
    	}
    }

    @Test
    public void testGetAttrsMalformed()
    {
		String text = "<i a=='b'>";
		Tag t;
		List<Attr> attrs;
		Attr a;

    	Buffer b = bufferWithText("xml",text);
    	try{
			t = new Tag(0,9);
			t.tag="i";
			t.type=T_START_TAG;
			
			// incorrect
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(1,attrs.size());
	
			a = attrs.get(0);
			assertEquals("a",a.name);
			assertEquals("'b'",a.val);
		}finally{
			close(view(),b);
    	}
    	
		text = "<j 'no name'>";
    	b = bufferWithText("xml",text);
    	try{
			t = new Tag(0,12);
			t.tag="j";
			t.type=T_START_TAG;
			
			attrs = BufferTagParser.getAttrs(b,t);
			System.err.println(attrs);
			assertEquals(0,attrs.size());
	
		}finally{
			close(view(),b);
    	}
		text= "<k l='1' 'not closed>";
    	b = bufferWithText("xml",text);
		try{
			t = new Tag(0,20);
			t.tag="k";
			t.type=T_START_TAG;
			
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(1,attrs.size());
	
			a = attrs.get(0);
			assertEquals("l",a.name);
			assertEquals("'1'",a.val);
		}finally{
			close(view(),b);
    	}

		text= "<k p: q='qq'>";
    	b = bufferWithText("xml",text);
		try{
			t = new Tag(0,12);
			t.tag="k";
			t.type=T_START_TAG;
			
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(1,attrs.size());
	
			a = attrs.get(0);
			assertEquals("q",a.name);
			assertEquals("'qq'",a.val);
		}finally{
			close(view(),b);
    	}

		text = "<l &attrRef;>";
    	b = bufferWithText("xml",text);
    	try{
			t = new Tag(0,11);
			t.tag="l";
			t.type=T_START_TAG;
			
			// incorrect
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(1,attrs.size());
		}finally{
			close(view(),b);
    	}
    }

    @Test
    public void testGetAttrsHTML()
    {
		String text = "<radio selected id='myid'>";
		Tag t;
		List<Attr> attrs;
		Attr a;

    	Buffer b = bufferWithText("xml",text);
    	try{
			t = new Tag(0,25);
			t.tag="radio";
			t.type=T_START_TAG;
			
			attrs = BufferTagParser.getAttrs(b,t);
			assertEquals(2,attrs.size());
	
			a = attrs.get(0);
			assertEquals("selected",a.name);
			assertEquals(null,a.val);

			a = attrs.get(1);
			assertEquals("id",a.name);
			assertEquals("'myid'",a.val);
		}finally{
			close(view(),b);
    	}
    	
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
    
    public static Buffer bufferWithText(final String mode, final String text){
    	return GuiActionRunner.execute(new GuiQuery<Buffer>(){
    			public Buffer executeInEDT(){
    				Buffer b = jEdit.newFile(view());
    				b.setMode(mode);
    				b.insert(0, text);
    				return b;
    			}
    	});
	}
}
