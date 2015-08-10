/*
 * HtmlParserTest.java
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
package sidekick.html;

// {{{ jUnit imports 
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.selectPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xml.XMLTestUtils.openParseAndWait;

import java.io.File;
import java.io.IOException;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
// }}}

/**
 * $Id$
 */
public class HtmlParserTest{
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
    public void testErrorInCss(){
    	File xml = new File(testData,"html/error_in_css.html");
    	
    	openParseAndWait(xml.getPath());
    	
    	action("sidekick-tree");
    	
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	JTreeFixture sourceTree = sidekick.tree();
    	
		// inspect the tree
		selectPath(sourceTree,",<html>&lt;html&gt;,<head>,<STYLE type=\"text/css\">");
		
		// ensure some coherence in the assets
		JEditTextArea area = TestUtils.view().getTextArea();
		assertEquals("<style", area.getBuffer().getText(area.getCaretPosition(),6));
		
		// ensure that errors in CSS are reported at the correct location
		action("error-list-show",1);

    	action("sidekick.parser.html-switch");

		Pause.pause(2000);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
    	errorlist.resizeWidthTo(1024);
    	errorlist.tree().selectRow(1);
    	assertEquals(";",area.getSelectedText());
    	assertEquals(2,area.getCaretLine());
    }
    
}
