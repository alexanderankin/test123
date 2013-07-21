/*
 * XSLTPluginXPathTest.java
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

import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import org.fest.swing.data.TableCell;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.TestUtils.ClickT;
import org.gjt.sp.jedit.testframework.TestUtils.Option;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * integration tests using test_data
 * $Id$
 */
@RunWith(JEditRunner.class)
public class XSLTPluginXPathTest{
	@Rule
	public TestData testData = new TestData();
	
    @BeforeClass
    public static void setUpjEdit() throws IOException{
    	/*
    	 * the plugin must be activated manually, because :
    	 * - it's not activated at startup because it is only activated if "compile on save"
    	 *   is checked, which is not the case by default
    	 * - it's not activated when one of the classes of the plugin is loaded,
    	 *   since delegateFirst=true and XSLT.jar is present in the parent ClassLoader
    	 *
    	 * when running normally, there is no problem.
    	 */
    	jEdit.getPlugin("xslt.XSLTPlugin",true).getPluginJAR().activatePlugin();
    }
    
    @Test
    public void testXPath() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xpath-tool-float",1);
    	
    	
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
		xpathTool.radioButton("xpath.source.buffer").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("/hello/text()");
				}
		});
		
		xpathTool.button("xpath.evaluate").click();
		
		Pause.pause(1000);
		xpathTool.textBox("xpath.result.data-type").requireText("node-set of size 1");
		xpathTool.textBox("xpath.result.value").requireText("world");
		
		xpathTool.close();
    }
    
    @Test
    public void testNS() throws IOException{
    	File xml = new File(testData.get(),"simple/transform.xsl");
    	
    	TestUtils.openFile(xml.getPath());


		PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.comboBox("xpath-adapter").selectItem(Pattern.compile(".*XPath 1\\.0.*"));
    	optionsF.OK();

    	action("xpath-tool-float",1);
    	
    	
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
		xpathTool.radioButton("xpath.source.buffer").click();
		
		xpathTool.button("xpath.ns.grab").click();
		Pause.pause(2000);
		
		JTableFixture table = xpathTool.table("xpath.ns");
		table.cell(TableCell.row(0).column(0)).requireValue("xsl");
		table.cell(TableCell.row(0).column(1)).requireValue("http://www.w3.org/1999/XSL/Transform");
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("//xsl:value-of");
				}
		});
		xpathTool.button("xpath.evaluate").click();
		
		Pause.pause(1000);
		xpathTool.textBox("xpath.result.data-type").requireText("node-set of size 1");
		xpathTool.textBox("xpath.result.xml-fragments").requireText("<xsl:value-of select=\".\"/>\n");
		xpathTool.close();
    }

    @Test
    public void testDocumentCache() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xpath-tool-float",1);
    	
    	
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
		xpathTool.radioButton("xpath.source.buffer").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("/hello/text()");
				}
		});
		
		xpathTool.button("xpath.evaluate").click();
		
		final String contents = "<?xml version=\"1.0\" ?>\n"
		+"<hello>you</hello>";
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					Buffer b = view().getBuffer();
					b.remove(0,b.getLength());
					b.insert(0,contents);
				}
		});

		xpathTool.button("xpath.evaluate").click();
		
		Pause.pause(1000);
		xpathTool.textBox("xpath.result.value").requireText("you");

		xpathTool.close();
		TestUtils.close(view(),view().getBuffer());
    }
    
    @Test
    public void testFile() throws IOException{
    	final File xml = new File(testData.get(),"simple/source.xml");
    	
    	action("xpath-tool-float",1);
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
    	xpathTool.radioButton("xpath.source.file").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.source.prompt").targetCastedTo(JTextComponent.class).setText(xml.getPath());
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("/hello/text()");
				}
		});
		
		xpathTool.button("xpath.evaluate").click();
		
		Pause.pause(1000);
		xpathTool.textBox("xpath.result.value").requireText("world");

		xpathTool.close();
    }
    
    @Test
    public void testHighlight()
    {
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	
    	action("xpath-tool-float",1);
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
    	xpathTool.radioButton("xpath.source.file").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.source.prompt").targetCastedTo(JTextComponent.class).setText(xsl.getPath());
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("//*");
				}
		});
		
		xpathTool.button("xpath.evaluate").click();
		
		Pause.pause(1000);
		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(0).column(0)).click();
		assertEquals(xpathTool.textBox("xpath.result.xml-fragments").targetCastedTo(JTextArea.class).getHighlighter().getHighlights().length,1);
		assertEquals(xpathTool.textBox("xpath.result.xml-fragments").targetCastedTo(JTextArea.class).getHighlighter().getHighlights()[0].getStartOffset(),0);

		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(1).column(0)).click();
		assertEquals(xpathTool.textBox("xpath.result.xml-fragments").targetCastedTo(JTextArea.class).getHighlighter().getHighlights().length,1);
		assertTrue(xpathTool.textBox("xpath.result.xml-fragments").targetCastedTo(JTextArea.class).getHighlighter().getHighlights()[0].getStartOffset()>10);

		xpathTool.close();
    }
    
    @Test
    public void testSaxon9XPathAdapter(){
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	
		PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.comboBox("xpath-adapter").selectItem(Pattern.compile(".*XPath 2\\.0.*"));
    	optionsF.OK();

    	
    	action("xpath-tool-float",1);
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
    	xpathTool.radioButton("xpath.source.file").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.source.prompt").targetCastedTo(JTextComponent.class).setText(xsl.getPath());
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("(1+1,//xsl:value-of/@select)");
				}
		});
		
		xpathTool.button("xpath.ns.grab").click();
		Pause.pause(1000);
		xpathTool.button("xpath.evaluate").click();
		
		Pause.pause(1000);
		xpathTool.table("xpath.result.node-set-summary").requireRowCount(2);
		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(0).column(0)).requireValue("xs:integer");
		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(0).column(1)).requireValue("");
		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(0).column(2)).requireValue("2");
		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(1).column(0)).requireValue("attribute(Q{}select)");
		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(1).column(1)).requireValue("select");
		xpathTool.table("xpath.result.node-set-summary").cell(TableCell.row(1).column(2)).requireValue(".");
		
		xpathTool.textBox("xpath.result.data-type").requireText("sequence of item()");
		xpathTool.textBox("xpath.result.value").requireText("2 .");
		xpathTool.textBox("xpath.result.xml-fragments").requireText("2\nselect=\".\"\n");

		
		xpathTool.close();
		
		optionsF = TestUtils.pluginOptions();
    	options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.comboBox("xpath-adapter").selectItem(Pattern.compile(".*XPath 1\\.0.*"));
    	optionsF.OK();
    }
    
    @Test
    public void testIncorrectDocument() throws IOException{
    	File xml = new File(testData.get(),"broken/source.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xpath-tool-float",1);
    	
    	
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
		xpathTool.radioButton("xpath.source.buffer").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("/hello/text()");
				}
		});
		
		new ClickT(Option.OK).start();
		xpathTool.button("xpath.evaluate").click();
	
		Pause.pause(1000);
		xpathTool.close();
    }
    
    @Test
    public void testIncorrectExpression() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	
    	TestUtils.openFile(xml.getPath());
    	action("xpath-tool-float",1);
    	
    	
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
		xpathTool.radioButton("xpath.source.buffer").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("...");
				}
		});
		
		new ClickT(Option.OK).start();
		xpathTool.button("xpath.evaluate").click();
	
		Pause.pause(1000);
		xpathTool.close();
    }

    @Test
    public void testDocumentDoesntExist() throws IOException{
    	final File xml = new File(testData.get(),"simple/not_there.xml");
    	
    	action("xpath-tool-float",1);
    	
    	
    	final FrameFixture xpathTool = TestUtils.findFrameByTitle("XPath Tool");
    	
		xpathTool.radioButton("xpath.source.file").click();
		
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xpathTool.textBox("xpath.source.prompt").targetCastedTo(JTextComponent.class).setText(xml.getPath());
					xpathTool.textBox("xpath.expression").targetCastedTo(JTextComponent.class).setText("...");
				}
		});
		
		new ClickT(Option.OK).start();
		action("xpath.evaluate");
	
		Pause.pause(1000);
		xpathTool.close();
    }
}
