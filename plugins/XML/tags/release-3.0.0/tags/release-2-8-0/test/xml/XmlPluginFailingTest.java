/*
 * XmlPluginFailingTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Eric Le Lay
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
import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
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
 * integration tests using test_data.
 * They all fail, I know ;-)
 * $Id$
 */
public class XmlPluginFailingTest{
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
    
	/** Completion in JSPs */
	@Test
	public void testMixedJSP(){
    	File xml = new File(testData,"mixed_jsp/myjsp.jsp");
    	
    	TestUtils.openFile(xml.getPath());
    	
    	action("xml-insert-float",1);
    	FrameFixture insert = TestUtils.findFrameByTitle("XML Insert");
    	
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		

		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
    	
    	requireEmpty(errorlist.tree());

		errorlist.close();
		
		// inside body
		gotoPositionAndWait(234);
		assertThat(insert.list("elements").contents()).contains("h1");
		
		
		// inside java code
		// the test fails, because completion is not disabled in java snippets.
		gotoPositionAndWait(296);
		assertThat(insert.list("elements").contents()).isEmpty();
		
		insert.close();
	}
	
}
