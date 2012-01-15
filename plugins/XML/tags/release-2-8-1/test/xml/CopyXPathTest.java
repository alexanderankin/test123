/*
 * CopyXPathTest.java
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
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import static org.gjt.sp.jedit.testframework.TestUtils.*;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Registers;

import java.io.*;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

/**
 * Various tests for the XMLActions actions
 * $Id$
 */
public class CopyXPathTest{
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
    
    /**
     * same namespace, no prefix : easy !
     */
	@Test
	public void testCopyXPathNoPrefix(){
    	File xml = new File(testData,"dtd/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
		
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick-parse",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
		Registers.getRegister('$').setValue("NULL");
		
		// before the root element
		gotoPositionAndWait(22);
		
		action("xml-copy-xpath");
		assertEquals("NULL",Registers.getRegister('$').toString());

		// go into the ACTIONS element
		gotoPositionAndWait(151);
		
		action("xml-copy-xpath");
		assertEquals("/ACTIONS",Registers.getRegister('$').toString());

		// go into the second ACTION element
		gotoPositionAndWait(325);
		
		action("xml-copy-xpath");
		assertEquals("/ACTIONS/ACTION[2]",Registers.getRegister('$').toString());

		Registers.getRegister('$').setValue("NULL");

		// after the closing tag of the root element
		gotoPositionAndWait(431);

		action("xml-copy-xpath");
		assertEquals("NULL",Registers.getRegister('$').toString());
	}

	/**
	 * some tests are failing for now...
	 */
	@Test
	public void testCopyXPathWithPrefix(){
    	File xml = new File(testData,"with_prefix/test.xml");
    	
    	TestUtils.openFile(xml.getPath());

		// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick-parse",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
		// go into the b:created element
		gotoPositionAndWait(161);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-copy-xpath");
				}
		});
		assertEquals("/a:document/b:created[1]",Registers.getRegister('$').toString());
		
		
		// go into the first trap
		gotoPositionAndWait(511);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-copy-xpath");
				}
		});
		assertTrue(Registers.getRegister('$').toString().startsWith("/a:document/"));
		assertNotSame("/a:document/a:trap[1]",Registers.getRegister('$').toString());

		// go into the 2nd trap
		gotoPositionAndWait(774);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-copy-xpath");
				}
		});
		assertEquals("/a:document/a:trap[1]",Registers.getRegister('$').toString());
		
		// go into the 3rd trap
		gotoPositionAndWait(1018);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-copy-xpath");
				}
		});
		assertEquals("/a:document/a:trap[2]",Registers.getRegister('$').toString());
	}
	
	/**
	 * no exception : only a beep, please !
	 */
	@Test
	public void testTextFile(){
    	File xml = new File(testData,"rnc/actions.rnc");
    	
    	TestUtils.openFile(xml.getPath());

		// wait for end of parsing
    	parseAndWait();

		// set the register to a known value
		Registers.getRegister('$').setValue("NULL");
		
		// go into the b:created element
		gotoPositionAndWait(161);
		
		action("xml-copy-xpath");
		
		assertEquals("NULL",Registers.getRegister('$').toString());
	}
	
	/**
	 */
	@Test
	public void testHtmlFile(){
    	File xml = new File(testData,"html/well_formed.html");
    	
    	TestUtils.openFile(xml.getPath());

		// wait for end of parsing
    	parseAndWait();
		
		// go into the doctype
		gotoPositionAndWait(62);
		
		action("xml-copy-xpath");
		
		assertEquals("NULL",Registers.getRegister('$').toString());
		
		// go into the html
		gotoPositionAndWait(117);
		
		action("xml-copy-xpath");
		
		Pause.pause(500);
		
		assertEquals("/html",Registers.getRegister('$').toString());
		
		// go into the css, right into @import, which is a CSS node
		gotoPositionAndWait(300);
		
		action("xml-copy-xpath");
		
		// style turns to uppercase in sidekick
		assertEquals("/html/head[1]/STYLE[1]",Registers.getRegister('$').toString());
		
		// go into the body (second link)
		gotoPositionAndWait(659);
		
		action("xml-copy-xpath");
		
		assertEquals("/html/body[1]/div[2]/p[2]/a[1]",Registers.getRegister('$').toString());

	}
}
