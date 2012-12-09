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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static xml.XMLTestUtils.gotoPositionAndWait;
import static xml.XMLTestUtils.parseAndWait;

import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;

import org.fest.swing.core.MouseButton;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.timing.Pause;
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
	@Test
	public void testCopyXPathNoPrefix(){
    	File xml = new File(testData,"../docs/htmlsidekick.html");
    	
    	TestUtils.openFile(xml.getPath());
		
    	// wait for end of parsing
    	doInBetween(new Runnable(){
    			public void run(){
    				action("sidekick-parse",1);
    		}}, 
    		messageOfClassCondition(sidekick.SideKickUpdate.class),
    		10000);
		
    	TestUtils.view().getTextArea().scrollTo(3281,false);
		final java.awt.Point hrefP =  TestUtils.view().getTextArea().offsetToXY(3281);
		hrefP.translate(30, 10);
		final JEditTextAreaFixture tf = new JEditTextAreaFixture(TestUtils.robot(),TestUtils.view().getTextArea()); 

		// doesn't work: the hyperlink is not triggered...
		tf.robot.moveMouse(tf.target,hrefP);
		tf.robot.pressModifiers(InputEvent.CTRL_DOWN_MASK);
		Pause.pause(2000);
		hrefP.translate(10,4);
		tf.robot.moveMouse(tf.target,hrefP);
		Pause.pause(10000);
		tf.robot.click(tf.target, MouseButton.LEFT_BUTTON);
		tf.robot.releaseModifiers(InputEvent.CTRL_DOWN_MASK);
	}


}
