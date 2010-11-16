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
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import java.io.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;

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
    	
    	TestUtils.openFile(xml.getPath());
    	
    	parseAndWait();
    	
    	action("sidekick-tree");
    	
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	JTreeFixture sourceTree = sidekick.tree();
    	
		// inspect the tree
		selectPath(sourceTree,"<html>&lt;html&gt;/<head>/<STYLE>");
		
		// ensure some coherence in the assets
		JEditTextArea area = TestUtils.view().getTextArea();
		assertEquals("<style", area.getBuffer().getText(area.getCaretPosition(),6));
		
		// ensure that errors in CSS are reported at the correct location
		action("error-list-show",1);
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
    	errorlist.tree().selectRow(1);
    	assertEquals(";",area.getSelectedText());
    	assertEquals(2,area.getCaretLine());
    }
    
}
