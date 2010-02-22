/*
 * XSLTPluginTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Eric Le Lay
 *
 * The XSLT plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 2.0, for example used by the Xalan package."
 */
package xslt;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;

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

// }}}

import java.io.*;
import javax.swing.text.*;
import javax.swing.*;

/**
 * integration tests using test_data
 * $Id$
 */
public class XSLTPluginXPathTest{
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
    
    //@Test
    public void testXPath() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xpath-tool-float",1);
    	
    	
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
		xpathTool.radioButton("xpath.select.buffer").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("/hello/text()");
				}
		});
		
		xpathTool.button("xpath.evaluate").click();
		
		Pause.pause(1000);
		xpathTool.textBox("xpath.result.data-type").requireText("node-set of size 1");
		xpathTool.textBox("xpath.result.value").requireText("world");
    }
    
}
