/*
 * XSLTThreeWayModeTest.java
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
import org.fest.swing.core.matcher.JTextComponentMatcher;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

import java.io.*;
import java.util.regex.Pattern;
import javax.swing.text.*;
import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

/**
 * integration tests of the XSLT 3-way mode.
 * $Id$
 */
public class XSLTThreeWayModeTest{
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
    public void testThreeWayModeAction(){
    	File xml = new File(testData,"simple/source.xml");
    	Buffer xmlB = openFile(xml.getPath());
    	Pause.pause(500);
    	final String xmlContents = xmlB.getText(0,xmlB.getLength());
    	close(view(),xmlB);

    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.comboBox("factory").selectItem(Pattern.compile("XSLT 1\\.0.*"));
    	optionsF.OK();

    	action("xslt.three-way-mode");
    	
    	assertEquals(3,view().getEditPanes().length);
    	
    	final Buffer xmlBuffer = (view().getEditPanes()[0]).getBuffer();
    	final Buffer xslBuffer = (view().getEditPanes()[1]).getBuffer();
    	final Buffer resBuffer = (view().getEditPanes()[2]).getBuffer();

    	assertEquals("xml",xmlBuffer.getMode().getName());
    	assertEquals("xsl",xslBuffer.getMode().getName());
    	assertEquals("text",resBuffer.getMode().getName());
    	
    	assertThat(xslBuffer.getText(0,xslBuffer.getLength())).contains("version=\"1.0\"");

    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
    	xsltProcessor.checkBox("three-way").requireSelected();
    	
    	GuiActionRunner.execute(new GuiTask(){
    			protected void executeInEDT(){
    				xmlBuffer.remove(0,xmlBuffer.getLength());
    				xmlBuffer.insert(0,xmlContents);
    			}
    	});

    	xsltProcessor.button("xslt.transform").click();
    	
    	Pause.pause(15000);
    	
    	
    	assertThat(resBuffer.getText(0,resBuffer.getLength())).contains("<hello>world</hello>");
    	
    	close(view(),xmlBuffer);
    	close(view(),xslBuffer);
    	close(view(),resBuffer);
    	
    	xsltProcessor.close();
    }
    
    @Test
    public void testToggleThreeWayMode(){
    	File xml = new File(testData,"simple/source.xml");
     	File xsl = new File(testData,"simple/transform.xsl");
     	
     	view().unsplit();
     	view().splitVertically();
     	view().splitVertically();
     	
    	assertEquals(3,view().getEditPanes().length);

     	// out of order : XSL, source, result
     	jEdit.openFile((view().getEditPanes()[0]),xsl.getPath());
     	jEdit.openFile((view().getEditPanes()[1]),xml.getPath());
     	jEdit.openFile((view().getEditPanes()[2]),xml.getPath());
     	
    	final Buffer xmlBuffer = (view().getEditPanes()[0]).getBuffer();
    	final Buffer xslBuffer = (view().getEditPanes()[1]).getBuffer();
    	final Buffer resBuffer = (view().getEditPanes()[2]).getBuffer();

    	Pause.pause(500);

    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.comboBox("factory").selectItem(Pattern.compile("XSLT 1\\.0.*"));
    	optionsF.OK();

    	action("xslt-processor");
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");

    	xsltProcessor.checkBox("three-way").check();
    	
    	xsltProcessor.radioButton("xslt.source.buffer").requireDisabled();
    	xsltProcessor.radioButton("xslt.source.file").requireDisabled();
    	xsltProcessor.button("xslt.stylesheets.add").requireDisabled();

    	xsltProcessor.button("xslt.transform").click();
    	
    	Pause.pause(10000);
    	
     	assertTrue(resBuffer.isDirty());
    	
    	
    	close(view(),xmlBuffer);
    	close(view(),xslBuffer);
    	close(view(),resBuffer);
    	
    	xsltProcessor.close();
    }
}
