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
package xml.hyperlinks;

// {{{ jUnit imports 
import static org.gjt.sp.jedit.testframework.EBFixture.doInBetween;
import static org.gjt.sp.jedit.testframework.EBFixture.messageOfClassCondition;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import org.gjt.sp.jedit.jEdit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static xml.XMLTestUtils.gotoPositionAndWait;
import static xml.XMLTestUtils.parseAndWait;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.jedit.hyperlinks.HyperlinkSource;
import gatchan.jedit.hyperlinks.jEditOpenFileHyperlink;

import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;

import org.fest.swing.core.MouseButton;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Registers;
import org.gjt.sp.jedit.testframework.JEditTextAreaFixture;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.TestUtils.ClickT;
import org.gjt.sp.jedit.testframework.TestUtils.Option;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
// }}}

public class HyperlinksTest {
	
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
     * try to trigger an hyperlink in an html document
     */
	////@Test
	public void testGUI(){
    	File xml = new File(testData,"html/htmlsidekick.html");
    	
    	TestUtils.openFile(xml.getPath());
		
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick-parse",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
    	TestUtils.view().getTextArea().scrollTo(3319,false);
		final java.awt.Point hrefP =  TestUtils.view().getTextArea().offsetToXY(3319);
		hrefP.translate(30, 10);
		final JEditTextAreaFixture tf = new JEditTextAreaFixture(TestUtils.robot(),TestUtils.view().getTextArea()); 

		// doesn't work: the hyperlink is not triggered...
		tf.robot.moveMouse(tf.target,hrefP);
		tf.robot.pressModifiers(InputEvent.CTRL_DOWN_MASK);
		Pause.pause(2000);
		hrefP.translate(10,4);
		tf.robot.moveMouse(tf.target,hrefP);
		Pause.pause(4000);
		tf.robot.click(tf.target, MouseButton.LEFT_BUTTON);
		tf.robot.releaseModifiers(InputEvent.CTRL_DOWN_MASK);
	}

    /**
     * get an hyperlink in HTML mode, HTML sidekick
     */
	@Test
	public void testHTML(){
    	File xml = new File(testData,"html/htmlsidekick.html");
    	
    	Buffer b = TestUtils.openFile(xml.getPath());
		
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick-parse",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
    	action("sidekick-tree");
    	Pause.pause(2000);

    	HyperlinkSource src = HTMLHyperlinkSource.create();
		final Hyperlink h = src.getHyperlink(TestUtils.view().getBuffer(), 3319);
		assertNotNull(h);
		assertTrue(h instanceof jEditOpenFileHyperlink);
		assertEquals(67, h.getStartLine());
		assertEquals(67, h.getEndLine());
		GuiActionRunner.execute(new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				h.click(TestUtils.view());
			}
		});
		
		assertEquals("jeditresource:/SideKick.jar!/index.html",TestUtils.view().getBuffer().getPath());
		
		TestUtils.close(TestUtils.view(), TestUtils.view().getBuffer());
		TestUtils.close(TestUtils.view(), b);
	}

    /**
     * get an hyperlink in HTML mode, XML sidekick
     */
	@Test
	public void testHTMLAsXML(){
    	File xml = new File(testData,"html/htmlsidekick.html");
    	
    	Buffer b = TestUtils.openFile(xml.getPath());
		
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick.parser.xml-switch",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
    	action("sidekick-tree");
    	Pause.pause(2000);

    	HyperlinkSource src = HTMLHyperlinkSource.create();
		final Hyperlink h = src.getHyperlink(TestUtils.view().getBuffer(), 3319);
		assertNotNull(h);
		assertTrue(h instanceof jEditOpenFileHyperlink);
		assertEquals(67, h.getStartLine());
		assertEquals(67, h.getEndLine());
		GuiActionRunner.execute(new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				h.click(TestUtils.view());
			}
		});
		
		assertEquals("jeditresource:/SideKick.jar!/index.html",TestUtils.view().getBuffer().getPath());
		
		TestUtils.close(TestUtils.view(), TestUtils.view().getBuffer());
		TestUtils.close(TestUtils.view(), b);
	}


    /**
     * get an hash hyperlink in HTML mode, HTML sidekick
     */
	@Test
	public void testLocalLinkHTML(){
		
    	File xml = new File(testData,"html/htmlsidekick.html");
    	
    	Buffer b = TestUtils.openFile(xml.getPath());
		
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick.parser.html-switch",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
    	action("sidekick-tree");
    	TestUtils.view().getTextArea().scrollTo(893,false);
    	gotoPositionAndWait(893);
    	Pause.pause(2000);
    	HyperlinkSource src = new HTMLHyperlinkSource();
		final Hyperlink h = src.getHyperlink(b, 893);
		assertNotNull(h);
		assertTrue(h instanceof jEditOpenFileAndGotoHyperlink);
		assertEquals(29, h.getStartLine());
		assertEquals(29, h.getEndLine());
		GuiActionRunner.execute(new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				h.click(TestUtils.view());
			}
		});
		
		// moved same file, but below
		assertEquals(b.getPath(),TestUtils.view().getBuffer().getPath());
		assertEquals(114, TestUtils.view().getTextArea().getCaretLine());
		
		TestUtils.close(TestUtils.view(), b);
	}

    /**
     * get an hash hyperlink in HTML mode, XML sidekick
     */
	@Test
	public void testLocalLinkHTMLAsXML(){
    	File xml = new File(testData,"html/htmlsidekick.html");
    	
    	Buffer b = TestUtils.openFile(xml.getPath());
		
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick.parser.xml-switch",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
    	action("sidekick-tree");
    	Pause.pause(2000);

    	HyperlinkSource src = new HTMLHyperlinkSource();
		final Hyperlink h = src.getHyperlink(TestUtils.view().getBuffer(), 893);
		assertNotNull(h);
		assertTrue(h instanceof jEditOpenFileAndGotoHyperlink);
		assertEquals(29, h.getStartLine());
		assertEquals(29, h.getEndLine());
		GuiActionRunner.execute(new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				h.click(TestUtils.view());
			}
		});
		
		// moved same file, but below
		assertEquals(b.getPath(),TestUtils.view().getBuffer().getPath());
		assertEquals(114, TestUtils.view().getTextArea().getCaretLine());
		
		TestUtils.close(TestUtils.view(), b);
	}
}
