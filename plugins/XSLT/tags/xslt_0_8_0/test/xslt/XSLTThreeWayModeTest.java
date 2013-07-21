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

import static org.fest.assertions.Assertions.assertThat;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.close;
import static org.gjt.sp.jedit.testframework.TestUtils.openFile;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.regex.Pattern;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * integration tests of the XSLT 3-way mode.
 * $Id$
 */
@RunWith(JEditRunner.class)
public class XSLTThreeWayModeTest{
	@Rule
	public TestData testData = new TestData();
    
    @Test
    public void testThreeWayModeAction(){
    	File xml = new File(testData.get(),"simple/source.xml");
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
    	assertEquals(3,GuiActionRunner.execute(new GuiQuery<Integer>() {
    		@Override
    		protected Integer executeInEDT() throws Throwable {
    			return view().getEditPanes().length;
    		}
		}).intValue());
    	
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
    	final File xml = new File(testData.get(),"simple/source.xml");
     	final File xsl = new File(testData.get(),"simple/transform.xsl");
     	
     	GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
		     	view().unsplit();
			}
		});
     	
     	GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
		     	view().splitVertically();
			}
		});

     	GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
		     	view().splitVertically();
			}
		});

     	assertEquals(3,view().getEditPanes().length);

     	// out of order : XSL, source, result
    	for(int i=0;i < 3; i++){
    		final int j = i;
	     	GuiActionRunner.execute(new GuiTask() {
				
				@Override
				protected void executeInEDT() throws Throwable {
			     	jEdit.openFile((view().getEditPanes()[j]),xsl.getPath());
				}
	     	});
    	}

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
    	
    	GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				jEdit.getActiveView().unsplit();
			}
		});
    	
    	xsltProcessor.close();
    }
}
