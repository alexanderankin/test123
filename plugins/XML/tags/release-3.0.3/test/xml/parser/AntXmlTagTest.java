/*
* AntXmlTagTest.java
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
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xml.XMLTestUtils.openParseAndWait;

import java.io.File;
import java.io.IOException;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
// }}}


/**
 * Rename target and project to target.name and project.name
 * $Id$
 */
public class AntXmlTagTest  {
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
    	File xml = new File(testData,"../build.xml");
    	
    	final Buffer b = openParseAndWait(xml.getPath());
    	
		Pause.pause(1000);
    	try{
    		
			action("sidekick-tree");
			
			FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
			JTreeFixture sourceTree = sidekick.tree();
			
			// inspect the tree: 
			//   - project has been renamed to 'XML',
			//     the name of the project;
			//   - first target has been renamed to 'build.prepare',
			//     the name of the target.
			TestUtils.selectPath(sourceTree,",XML,build.prepare");

			Pause.pause(1000);
    		sidekick.close();
    		
			Pause.pause(1000);
    	}finally{
    		// discard changes
    		TestUtils.close(view(), b);
    	}
    }
    
    @Test
    public void testSort(){
    	File xml = new File(testData,"../build.xml");
    	
    	final Buffer b = openParseAndWait(xml.getPath());
    	
    	try{
    		
			action("sidekick-tree");
			
			FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
			JTreeFixture sourceTree = sidekick.tree();

			Pause.pause(1000);
			// mixed target and selector
			assertEquals("build.prepare", sourceTree.valueAt(12));
			assertEquals("selector", sourceTree.valueAt(13));

			sidekick.comboBox("sort").selectItem("Type");
			sourceTree.toggleRow(1);
			// last nodes are the targets
			String[] targets = {
				"build.prepare", "ant-complete", "jar","dist", "javacc", "tags",
				"test", "test-all"
			};
			int count = sourceTree.target.getRowCount();
			for(int i=count-1;i>count - targets.length;i--){
				assertEquals(targets[targets.length - count + i], sourceTree.valueAt(i));
			}

			sidekick.comboBox("sort").selectItem("Name");
			sourceTree.toggleRow(1);
			
			Pause.pause(1000);
			// a target target first
			assertEquals("ant-complete", sourceTree.valueAt(2));
			
			sidekick.close();

    	}finally{
    		// discard changes
    		TestUtils.close(view(), b);
    	}
    }

}
