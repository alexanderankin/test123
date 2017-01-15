/*
 * XmlTagTest.java
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
package xml.parser;

// {{{ jUnit imports 
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.pluginOptions;
import static org.gjt.sp.jedit.testframework.TestUtils.selectPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xml.XMLTestUtils.parseAndWait;

import java.io.File;
import java.io.IOException;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
// }}}

/**
 * $Id$
 */
public class XmlTagTest{
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
    public void testSimple() throws SAXException, IOException{
        PluginOptionsFixture options = pluginOptions();

        options.optionPane("XML/XML","xml.general")
               .comboBox("showAttributes").selectItem(0);

		options.OK();

		File xml = new File(testData,"simple/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
    	parseAndWait();
    	
    	action("sidekick.parser.xml-switch");
    	
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	JTreeFixture sourceTree = sidekick.tree();

		Pause.pause(1000);

		// inspect the tree
		selectPath(sourceTree,"/ACTIONS");
		selectPath(sourceTree,"/ACTIONS/ACTION/CODE");
		
		// ensure some coherence in the assets
		JEditTextArea area = TestUtils.view().getTextArea();
		assertEquals("<CODE", area.getBuffer().getText(area.getCaretPosition(),5));
    }
    
    @Test
    public void testDisplayModes(){

		/*------      test Attributes = Id       ---------*/

    	PluginOptionsFixture options = pluginOptions();
    	
    	options.optionPane("XML/XML","xml.general")
    	       .comboBox("showAttributes").selectItem(1);
    	
		options.OK();
    	
    	/* open a file with an id */
    	File xml = new File(testData,"xinclude/conventions.xml");
    	TestUtils.openFile(xml.getPath());
    	
    	parseAndWait();
    	
    	action("sidekick.parser.xml-switch");
    	
    	Pause.pause(1000);
    	
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	JTreeFixture sourceTree = sidekick.tree();
    	
		// inspect the tree
		selectPath(sourceTree,"/chapter id=\"conventions\"");
		
		/*------      test Attributes = None       ---------*/
		
		options = pluginOptions();
    	options.optionPane("XML/XML","xml.general")
    	       .comboBox("showAttributes").selectItem(0);
    	
		options.OK();
    	
		parseAndWait();
		
    	action("sidekick.parser.xml-switch");
    	
    	
    	sidekick = TestUtils.findFrameByTitle("Sidekick");
    	sourceTree = sidekick.tree();
    	
    	Pause.pause(1000);

		// inspect the tree
		selectPath(sourceTree,"/chapter");

		/*------      test Attributes = All       ---------*/
		
		options = pluginOptions();
    	options.optionPane("XML/XML","xml.general")
    	       .comboBox("showAttributes").selectItem(2);
    	
		options.OK();
    	
    	parseAndWait();
    	
    	
    	sidekick = TestUtils.findFrameByTitle("Sidekick");
    	sourceTree = sidekick.tree();
    	
    	Pause.pause(1000);

		// inspect the tree
		selectPath(sourceTree,"/chapter id=\"conventions\" xml:lang=\"en\"");
    }
    
}
