/*
 * TagUtilsTest.java
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
package xml;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static xml.XMLTestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import static org.gjt.sp.jedit.testframework.TestUtils.*;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

import java.io.*;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

/**
 * tests for Split/Join/Close/etc.
 * $Id$
 */
public class TagUtilsTest{
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
	public void testSplit(){
    	File xml = new File(testData,"split_tag/test.xml");
    	
    	Buffer b = TestUtils.openFile(xml.getPath());
    	
    	action("sidekick-parse",1);
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		// after bbbb
		gotoPositionAndWait(48);

		action("xml-split-tag",1);
		assertEquals("<a> bbbb</a>",b.getLineText(1));
		action("undo",1);
		
		// just after <a> : was causing an issue since split() believed that it was
		// inside <a>
		gotoPositionAndWait(43);
		
		action("xml-split-tag",1);
		assertEquals("<a></a>",b.getLineText(1));
		assertEquals("<a> bbbb",b.getLineText(2));
		action("undo",1);
		
		// just after </b> : was causing an issue since split() believed that it was
		// inside <a>
		gotoPositionAndWait(56);
		
		action("xml-split-tag",1);
		assertEquals("<b></b></a>",b.getLineText(2));
		assertEquals("<a> ",b.getLineText(3));
		assertEquals("<c:c xmlns:c=\"urn:hello\">you could",b.getLineText(4));//untouched
		action("undo",1);
	}
	
	@Test
	public void testSplitTag() throws IOException{
    	File xml = new File(testData,"split_tag/test.xml");
    	
    	Buffer b = TestUtils.openFile(xml.getPath());
    	
    	action("sidekick-parse");
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		// <c:c |xmlns:c="urn:hello">you could
		gotoPositionAndWait(62);

		action("xml-split-tag");
		String text = b.getText(0,b.getLength());
		action("undo",1);
		assertThat(text).contains("xmlns:c");
		
		gotoPositionAndWait(131);
		
		action("xml-split-tag");
		text = b.getText(0,b.getLength());
		action("undo",1);
		assertThat(text).contains("&lt;");
		
		// <simple |a="1" b="2"
		gotoPositionAndWait(204);
		
		action("xml-split-tag");
		assertEquals("    c = \"3\"",b.getLineText(9));
		action("undo",1);
		
		close(view(),b);
	}

}
