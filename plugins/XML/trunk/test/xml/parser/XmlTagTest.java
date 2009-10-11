package xml.parser;

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
import static xml.EBFixture.*;
import xml.OptionsFixture;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import java.io.*;
import java.net.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.validation.ValidatorHandler;
import com.thaiopensource.xml.sax.DraconianErrorHandler;


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
    	File xml = new File(testData,"simple/actions.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	
    	action("sidekick.parser.xml-switch",1);
    	
    	
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	JTreeFixture sourceTree = sidekick.tree();
    	
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		// inspect the tree
		selectPath(sourceTree,"ACTIONS");
		selectPath(sourceTree,"ACTIONS/ACTION/CODE");
		
		// ensure some coherence in the assets
		JEditTextArea area = TestUtils.view().getTextArea();
		assertEquals("<CODE", area.getBuffer().getText(area.getCaretPosition(),5));
    }
    
    @Test
    public void testDisplayModes(){

		/*------      test Attributes = Id       ---------*/

    	OptionsFixture options = pluginOptions();
    	
    	options.optionPane("XML/XML","xml.general")
    	       .comboBox("showAttributes").selectItem(1);
    	
		options.OK();
    	
    	/* open a file with an id */
    	File xml = new File(testData,"xinclude/conventions.xml");
    	TestUtils.openFile(xml.getPath());
    	
    	action("sidekick.parser.xml-switch",1);
    	
    	
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	JTreeFixture sourceTree = sidekick.tree();
    	
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		// inspect the tree
		selectPath(sourceTree,"chapter id=\"conventions\"");
		
		/*------      test Attributes = None       ---------*/
		
		options = pluginOptions();
    	options.optionPane("XML/XML","xml.general")
    	       .comboBox("showAttributes").selectItem(0);
    	
		options.OK();
    	
    	action("sidekick.parser.xml-switch",1);
    	
    	
    	sidekick = TestUtils.findFrameByTitle("Sidekick");
    	sourceTree = sidekick.tree();
    	
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		// inspect the tree
		selectPath(sourceTree,"chapter");

		/*------      test Attributes = All       ---------*/
		
		options = pluginOptions();
    	options.optionPane("XML/XML","xml.general")
    	       .comboBox("showAttributes").selectItem(2);
    	
		options.OK();
    	
    	action("sidekick.parser.xml-switch",1);
    	
    	
    	sidekick = TestUtils.findFrameByTitle("Sidekick");
    	sourceTree = sidekick.tree();
    	
		// wait for end of parsing
		simplyWaitForMessageOfClass(sidekick.SideKickUpdate.class,10000);
		
		// inspect the tree
		selectPath(sourceTree,"chapter id=\"conventions\" xml:lang=\"en\"");
    }
    
}
