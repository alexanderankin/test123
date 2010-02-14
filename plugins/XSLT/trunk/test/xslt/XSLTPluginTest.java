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
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.core.matcher.JButtonMatcher;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

import java.io.*;
import javax.swing.text.*;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

/**
 * integration tests using test_data
 * $Id$
 */
public class XSLTPluginTest{
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
    public void testXSLT() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"simple/transform.xsl");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xslt-processor-float",1);
    	
    	
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
		xsltProcessor.radioButton("xslt.source.buffer").click();
		xsltProcessor.button("stylesheets.add").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsl.getName()));
		browseDialog.button("ok").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xsltProcessor.textBox("xslt.result.prompt").targetCastedTo(JTextComponent.class).setText("");
				}
		});
		
		xsltProcessor.button("transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		assertEquals("hello world", b.getText(0,b.getLength()));
		xsltProcessor.close();
    }
    
    //@Test
    public void testXSLTErrorList() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"broken/transform.xsl");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xslt-processor-float",1);
    	
    	
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
		xsltProcessor.radioButton("xslt.source.buffer").click();
		
		while(xsltProcessor.list("stylesheets").contents().length>0)
		{
			xsltProcessor.list("stylesheets").selectItem(0);
			xsltProcessor.button("stylesheets.remove").click();
		}
		xsltProcessor.button("stylesheets.add").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.button("up").click();
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xsl.getParentFile().getName())).doubleClick();
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsl.getName()));
		browseDialog.button("ok").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xsltProcessor.textBox("xslt.result.prompt").targetCastedTo(JTextComponent.class).setText("");
				}
		});
		
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();

		xsltProcessor.button("transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		action("error-list-show");
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT error) xsl:ourrtput"));
		errorlist.close();
		xsltProcessor.close();
    }

    //@Test
    public void testXSLTBaseURIBug() throws IOException{
    	File xsl = new File(testData,"base_uri_bug/base-uri-bug.xsl");
    	
    	TestUtils.openFile(xsl.getPath());
    	action("xslt-processor-float",1);
    	
    	
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
		xsltProcessor.radioButton("xslt.source.buffer").click();

		while(xsltProcessor.list("stylesheets").contents().length>0)
		{
			xsltProcessor.list("stylesheets").selectItem(0);
			xsltProcessor.button("stylesheets.remove").click();
		}
		xsltProcessor.button("stylesheets.add").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsl.getName()));
		browseDialog.button("ok").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xsltProcessor.textBox("xslt.result.prompt").targetCastedTo(JTextComponent.class).setText("");
				}
		});
		
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();
		
		xsltProcessor.button("transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		
		assertEquals(0,b.getLength());

		xsltProcessor.close();

		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("15: (XSLT error)"));
		errorlist.close();
    }

    //@Test
    public void testXSLTRuntimeError() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"broken/fails_at_runtime.xsl");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xslt-processor-float",1);
    	
    	
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
		xsltProcessor.radioButton("xslt.source.buffer").click();
		
		while(xsltProcessor.list("stylesheets").contents().length>0)
		{
			xsltProcessor.list("stylesheets").selectItem(0);
			xsltProcessor.button("stylesheets.remove").click();
		}
		xsltProcessor.button("stylesheets.add").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.button("up").click();
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xsl.getParentFile().getName())).doubleClick();
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsl.getName()));
		browseDialog.button("ok").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xsltProcessor.textBox("xslt.result.prompt").targetCastedTo(JTextComponent.class).setText("");
				}
		});
		

		xsltProcessor.button("transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		action("error-list-show");
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("16: (XSLT error)"));
		errorlist.close();
		xsltProcessor.close();
    }

    @Test
    public void testXSLTSAXError() throws IOException{
    	File xml = new File(testData,"broken/source.xml");
    	File xsl = new File(testData,"simple/transform.xsl");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xslt-processor-float",1);
    	
    	
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
		xsltProcessor.radioButton("xslt.source.buffer").click();

		while(xsltProcessor.list("stylesheets").contents().length>0)
		{
			xsltProcessor.list("stylesheets").selectItem(0);
			xsltProcessor.button("stylesheets.remove").click();
		}
		xsltProcessor.button("stylesheets.add").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.button("up").click();
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xsl.getParentFile().getName())).doubleClick();
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsl.getName()));
		browseDialog.button("ok").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xsltProcessor.textBox("xslt.result.prompt").targetCastedTo(JTextComponent.class).setText("");
				}
		});
		
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();
		
		xsltProcessor.button("transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		
		assertEquals(0,b.getLength());

		xsltProcessor.close();

		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("2: (SAX error) Content is not allowed in prolog"));
		errorlist.close();
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
