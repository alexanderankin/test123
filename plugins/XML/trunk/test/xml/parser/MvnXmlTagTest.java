/*
* MvnXmlTagTest.java
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2012 Eric Le Lay
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
import static org.fest.assertions.Assertions.assertThat;
import static org.gjt.sp.jedit.testframework.EBFixture.simplyWaitForMessageOfClass;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.close;
import static org.gjt.sp.jedit.testframework.TestUtils.requireEmpty;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static xml.XMLTestUtils.gotoPositionAndWait;
import static xml.XMLTestUtils.openParseAndWait;
import static xml.XMLTestUtils.parseAndWait;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import org.fest.assertions.ObjectArrayAssert;
import org.fest.swing.cell.JListCellReader;
import org.fest.swing.core.MouseButton;
import org.fest.swing.driver.BasicJListCellReader;
import org.fest.swing.driver.CellRendererReader;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.TestUtils.ClickT;
import org.gjt.sp.jedit.testframework.TestUtils.Option;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.XMLTestUtils.JWindowFixture;
import xml.XMLTestUtils;
import xml.XmlPluginTest;
import xml.XmlPlugin;
// }}}


/**
* $Id$
*/
public class MvnXmlTagTest{
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
    public void testTree(){
    	File xml = new File(testData,"maven/pom.xml");
    	
    	final Buffer b = openParseAndWait(xml.getPath());
    	
    	try{
    		
			action("sidekick-tree");
			
			FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
			JTreeFixture sourceTree = sidekick.tree();
			Pause.pause(1000);
			// inspect the tree: project has been renamed to 'MyArtifact',
			// the name of the project.
			TestUtils.selectPath(sourceTree,",MyArtifact,properties");
    		sidekick.close();
    		
    	}finally{
    		// discard changes
    		TestUtils.close(view(), b);
    	}
    }
    
    @Test
    public void testComplete(){
    	File xml = new File(testData,"maven/pom.xml");
    	
    	final Buffer b = openParseAndWait(xml.getPath());
    	
    	try{
    		
	    	gotoPositionAndWait(501);
			GuiActionRunner.execute(new GuiTask(){
					protected void executeInEDT(){
						view().getTextArea().setSelectedText("<");
					}
			});
			
			action("sidekick-complete",1);
			
			JWindowFixture completion;
			
			completion = XMLTestUtils.completionPopup();
			completion.requireVisible();
			assertThat(XmlPluginTest.xmlListContents(completion.list()))
			.contains("description");

    	}finally{
    		// discard changes
    		TestUtils.close(view(), b);
    	}
    }
}
