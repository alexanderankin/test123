/*
 * TrangGUITest.java
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
package xml.translate;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

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

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;

import java.io.*;
import java.util.*;
import javax.swing.text.JTextComponent;



/**
 * unit tests for TrangTranslator
 * $Id$
 */
public class TrangGUITest{
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
    public void testDTDToXSD() throws IOException{
    	final File in = new File(testData,"../xml/dtds/oasis-catalog.dtd");
    	final File out = new File(testData,"../xml/dtds/oasis-catalog.xsd");
    	final File out2 = new File(testData,"../xml/dtds/xml.xsd");
    	
    	Buffer b = openFile(in.getPath());
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-trang-translate");
				}
		});
    	
		DialogFixture f = findDialogByTitle("Trang GUI");
		
		
		f.textBox("xml.translate.input.prompt").requireText(in.getPath());
		f.comboBox("input-type").requireSelection("dtd");
		
		f.checkBox("inline-attlist").requireVisible().requireNotSelected();
		
		f.textBox("xml.translate.output.prompt").requireText(in.getPath());
		f.comboBox("output-type").requireSelection("dtd");
		
		f.comboBox("output-type").selectItem("xsd");
		f.checkBox("disable-abstract-elements").requireVisible().requireNotSelected();
		f.checkBox("inline-attlist").requireVisible().requireSelected();
		
		f.textBox("xml.translate.output.prompt").requireText(out.getPath());
		
    	ClickT clickT = new ClickT(Option.OK);
    	clickT.start();

		f.button(JButtonMatcher.withText("OK")).click();
		
		clickT.waitForClick();
		
    	Buffer outB = jEdit.getBuffer(out.getPath());
    	assertNotNull(outB);
    	//inline-attlist
    	assertFalse(outB.getText(0,outB.getLength())
    		.contains("<xs:attributeGroup ref=\"ns1:attlist.rewriteURI\"/>"));
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-trang-translate");
				}
		});

    	Pause.pause(1000);
    	f = new DialogFixture(robot(),robot().finder().findByType(TrangGUI.class));

		f.comboBox("output-type").selectItem("xsd");
		f.checkBox("inline-attlist").uncheck();
		
		clickT = new ClickT(Option.OK);
    	clickT.start();
		f.button(JButtonMatcher.withText("OK")).click();
		clickT.waitForClick();
    	
    	assertTrue(outB.getText(0,outB.getLength())
    		.contains("<xs:attributeGroup ref=\"ns1:attlist.rewriteURI\"/>"));

    	close(view(),outB);
    	outB = jEdit.getBuffer(out2.getPath());
    	assertNotNull(outB);
    	close(view(),outB);
    	close(view(),b);
    }
    
    @Test
    public void testInputFormatChange() throws IOException{
    	final File in1 = new File(testData,"rnc/actions.xml").getCanonicalFile();
    	final File in2 = new File(testData,"rnc/actions.rnc").getCanonicalFile();
    	
    	Buffer b = openFile(in1.getPath());
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-trang-translate");
				}
		});
    	
		final DialogFixture f = findDialogByTitle("Trang GUI");
		
		
		f.textBox("xml.translate.input.prompt").requireText(in1.getPath());
		f.comboBox("input-type").requireSelection("xml");
		
		f.button("xml.translate.input.select").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		Pause.pause(1000);
		browseDialog.button("up").click();
		Pause.pause(1000);
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(in1.getParentFile().getName())).doubleClick();
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(in2.getName()));
		browseDialog.button("ok").click();

		Pause.pause(500);
		f.comboBox("input-type").requireSelection("rnc");

    	f.close();
    	
    	close(view(),b);
    }
}
