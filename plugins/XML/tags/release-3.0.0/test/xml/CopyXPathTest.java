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
import static org.gjt.sp.jedit.testframework.EBFixture.doInBetween;
import static org.gjt.sp.jedit.testframework.EBFixture.messageOfClassCondition;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static xml.XMLTestUtils.gotoPositionAndWait;
import static xml.XMLTestUtils.parseAndWait;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Registers;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.TestUtils.ClickT;
import org.gjt.sp.jedit.testframework.TestUtils.Option;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
// }}}

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
		
		Registers.getRegister('$').setTransferable(new StringSelection("NULL"));
		
		// before the root element
		gotoPositionAndWait(22);
		
		action("xml-copy-xpath");
		assertEquals("NULL",RegisterToString());

		// go into the ACTIONS element
		gotoPositionAndWait(151);
		
		action("xml-copy-xpath");
		assertEquals("/ACTIONS",RegisterToString());

		// go into the second ACTION element
		gotoPositionAndWait(325);
		
		action("xml-copy-xpath");
		assertEquals("/ACTIONS/ACTION[2]",RegisterToString());

		Registers.getRegister('$').setTransferable(new StringSelection("NULL"));

		// after the closing tag of the root element
		gotoPositionAndWait(431);

		action("xml-copy-xpath");
		assertEquals("NULL",RegisterToString());
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
		assertEquals("/a:document/b:created[1]",RegisterToString());
		
		
		// go into the first trap
		gotoPositionAndWait(511);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-copy-xpath");
				}
		});
		assertTrue(RegisterToString().startsWith("/a:document/"));
		assertNotSame("/a:document/a:trap[1]",RegisterToString());

		// go into the 2nd trap
		gotoPositionAndWait(774);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-copy-xpath");
				}
		});
		assertEquals("/a:document/a:trap[1]",RegisterToString());
		
		// go into the 3rd trap
		gotoPositionAndWait(1018);
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-copy-xpath");
				}
		});
		assertEquals("/a:document/a:trap[2]",RegisterToString());
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
		Registers.getRegister('$').setTransferable(new StringSelection("NULL"));
		
		// go into the b:created element
		gotoPositionAndWait(161);
		
		ClickT clickT = new ClickT(Option.OK);
		clickT.start();
		
		action("xml-copy-xpath");
		
		clickT.waitForClick();
		
		assertEquals("NULL",RegisterToString());
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
		
		assertEquals("NULL",RegisterToString());
		
		// go into the html
		gotoPositionAndWait(117);
		
		action("xml-copy-xpath");
		
		Pause.pause(500);
		
		assertEquals("/html", RegisterToString());
		
		// go into the css, right into @import, which is a CSS node
		gotoPositionAndWait(300);
		
		action("xml-copy-xpath");
		
		// style turns to uppercase in sidekick
		assertEquals("/html/head[1]/STYLE[1]", RegisterToString());
		
		// go into the body (second link)
		gotoPositionAndWait(659);
		
		action("xml-copy-xpath");
		
		assertEquals("/html/body[1]/div[2]/p[2]/a[1]", RegisterToString());

	}
	
	@SuppressWarnings("deprecation")
	private static String RegisterToString(){
		return Registers.getRegister('$').toString();
	}
}
