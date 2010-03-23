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
    
    @Test
    public void testXSLT() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"simple/transform.xsl");
    	String dest = "";
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,dest,1);
    	
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		assertEquals("hello world", b.getText(0,b.getLength()));
		TestUtils.close(TestUtils.view(),b);
		xsltProcessor.close();
    }
    
    @Test
    public void testXSLTErrorList() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"broken/transform.xsl");
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",1);
		
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();

		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		action("error-list-show");
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT error) xsl:ourrtput"));
		errorlist.close();
		xsltProcessor.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTBaseURIBug() throws IOException{
    	File xsl = new File(testData,"base_uri_bug/base-uri-bug.xsl");
    	
    	final FrameFixture xsltProcessor = setupProcessor(xsl,xsl,"",1);
		
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		
		assertEquals(0,b.getLength());

		xsltProcessor.close();

		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("15: (XSLT error)"));
		errorlist.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTRuntimeError() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"broken/fails_at_runtime.xsl");
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",1);
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		action("error-list-show");
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("16: (XSLT error)"));
		errorlist.close();
		xsltProcessor.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTSAXError() throws IOException{
    	final File xml = new File(testData,"broken/source.xml");
    	File xsl = new File(testData,"simple/transform.xsl");
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",1);

    	TestUtils.close(view(),view().getBuffer());
		
    	xsltProcessor.radioButton("xslt.source.file").click();
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					xsltProcessor.textBox("xslt.source.prompt").targetCastedTo(JTextComponent.class).setText(xml.getPath());
				}
		});

    	
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		
		xsltProcessor.close();

		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("2: (SAX error) Content is not allowed in prolog"));
		errorlist.close();
		TestUtils.close(TestUtils.view(),b);
    }

    /**
     */
    @Test
    public void testXSLTCompile() throws IOException{
    	File xsl = new File(testData,"broken/transform.xsl");
    	
    	TestUtils.openFile(xsl.getPath());

		action("error-list-clear");
    	
    	action("xslt.compile");
    	
		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

		Pause.pause(2000);
    	
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT error)"));
		errorlist.close();
    }

    @Test
    public void testXSLTCompileOnSave() throws IOException{
    	File xsl = new File(testData,"broken/transform.xsl");
    	
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
    	
    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.checkBox("compile-on-save").requireNotSelected().check();
    	optionsF.OK();
    	
    	TestUtils.openFile(xsl.getPath());
    	
		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	action("save");
		Pause.pause(3000);
    	
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT error)"));
		errorlist.close();
    }

    @Test
    public void testXSLTResultFile() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"simple/transform.xsl");
    	File result = new File(testData,"simple/output.txt");
    	
    	if(result.exists()){
    		assertTrue(result.delete());
    	}
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,result.getAbsolutePath(),1);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(5000);
		
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		assertEquals(result.getName(),b.getName());
		assertEquals("hello world",b.getText(0,b.getLength()));
    	assertTrue(result.exists());
    	TestUtils.close(TestUtils.view(),b);
    	assertTrue(result.delete());
    }


    @Test
    public void testXSLTOpenResult() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"simple/transform.xsl");
    	File result = new File(testData,"simple/output.txt");
    	
    	if(result.exists()){
    		assertTrue(result.delete());
    	}
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,result.getAbsolutePath(),1);
				
		xsltProcessor.checkBox("open-result").requireSelected().uncheck();
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(5000);
		
		xsltProcessor.checkBox("open-result").check();
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		assertEquals(xml.getName(),b.getName());
    	assertTrue(result.delete());
    }

    @Test
    public void testXSLTSourceURI() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"xslt2/base_document_uri.xsl");

    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",2);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(5000);
		
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		String res = b.getText(0,b.getLength());
		assertThat(res).contains("document:"+xml.toURI().toString()+"\n");
		assertThat(res).contains("base-uri:"+xml.toURI().toString()+"\n");
		assertThat(res).contains("stylesheet:"+xsl.toURI().toString());
		TestUtils.close(TestUtils.view(),b);

    }

    @Test
    public void testXSLTOutputURI() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"xslt2/result-document.xsl");
    	File result = new File(testData,"xslt2/res.txt");
    	File realOutput = new File(testData,"xslt2/output-document.txt");
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,result.getPath(),2);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(5000);
		
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		assertEquals(0,b.getLength());
		
		TestUtils.close(TestUtils.view(),b);
		
		b = TestUtils.openFile(realOutput.getPath());
		assertEquals("Hello world !",b.getText(0,b.getLength()));		
		
		TestUtils.close(TestUtils.view(),b);
		
		assertTrue(result.delete());
		assertTrue(realOutput.delete());
    }

    @Test
    public void testXSLTResolver() throws IOException{
    	File xml = new File(testData,"resolver/actions.xml");
    	File xsl = new File(testData,"resolver/transform.xsl");

    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",2);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(5000);
		
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		String res = b.getText(0,b.getLength());
		assertThat(res).contains("Transform XML (xslt.transform)");
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTCurrentStylesheet() throws IOException{
    	File xsl = new File(testData,"simple/transform.xsl");

    	final FrameFixture xsltProcessor = setupProcessor(xsl,xsl,"",1);
    	
		xsltProcessor.radioButton("xslt.stylesheets.buffer").check();
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(5000);
		
		xsltProcessor.radioButton("xslt.stylesheets.file").check();
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		String res = b.getText(0,b.getLength());
		assertThat(res).contains("hello ");
		TestUtils.close(TestUtils.view(),b);
    }

	@Test
    public void testXSLTParameters() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"parameters/stylesheet-with-parameters.xsl");
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",1);
    	
		// set the parameters
		final JTableFixture parms = xsltProcessor.table("xslt.parameters"); 
		while(parms.rowCount()>0)
		{
			parms.selectRows(0);
			xsltProcessor.button("xslt.parameters.remove").click();
		}
		xsltProcessor.button("xslt.parameters.add").click();
		
		parms.cell(TableCell.row(0).column(0)).click();
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					((JTextComponent)parms.cell(TableCell.row(0).column(0)).editor()).setText("p");
				}
		});
		parms.cell(TableCell.row(0).column(1)).click();
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					((JTextComponent)parms.cell(TableCell.row(0).column(0)).editor()).setText("world");
				}
		});
		xsltProcessor.button("xslt.parameters.add").click();
		
		parms.cell(TableCell.row(1).column(0)).click();
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					((JTextComponent)parms.cell(TableCell.row(0).column(0)).editor()).setText("q");
				}
		});
		parms.cell(TableCell.row(1).column(1)).click();
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					((JTextComponent)parms.cell(TableCell.row(0).column(0)).editor()).setText("!");
				}
		});
		
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(5000);
		
		Buffer b = view().getBuffer();
		
		assertEquals("Hello world !",b.getText(0,b.getLength()));

		xsltProcessor.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testURIResolverCompound() throws IOException{
    	File xml = new File(testData,"simple/source.xml");
    	File xsl = new File(testData,"compound_stylesheet/full.xsl");
    	String dest = "";
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,dest,1);
    	
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		
		assertEquals("this is a greeting: hello world", b.getText(0,b.getLength()));
		TestUtils.close(TestUtils.view(),b);
		xsltProcessor.close();
    }

    @Test
    public void testURIResolverIntrospection() throws IOException{
    	File xml = new File(testData,"introspection/source.xml");
    	File xsl = new File(testData,"introspection/transform.xsl");
    	String dest = "";
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,dest,1);
    	
		xsltProcessor.button("xslt.transform").click();
		
		Pause.pause(10000);
		
		Buffer b = view().getBuffer();
		
		assertThat(b.getText(0,b.getLength())).contains("- there are 2 templates");
		TestUtils.close(TestUtils.view(),b);
		xsltProcessor.close();
    }

    public FrameFixture setupProcessor(File xml, File xsl, final String dest,int version){
    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.comboBox("factory").selectItem(Pattern.compile("XSLT "+version+"\\.0.*"));
    	optionsF.OK();


    	TestUtils.openFile(xml.getPath());
    	action("xslt-processor-float",1);
    	
    	
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
		xsltProcessor.radioButton("xslt.source.buffer").click();

		while(xsltProcessor.list("stylesheets").contents().length>0)
		{
			xsltProcessor.list("stylesheets").selectItem(0);
			xsltProcessor.button("xslt.stylesheets.remove").click();
		}
		xsltProcessor.button("xslt.stylesheets.add").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.button("up").click();
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xsl.getParentFile().getName())).doubleClick();
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsl.getName()));
		browseDialog.button("ok").click();

		// set the result
		if("".equals(dest))
		{
			xsltProcessor.radioButton("xslt.result.buffer").click();
		}
		else
		{
			xsltProcessor.radioButton("xslt.result.file").click();
			GuiActionRunner.execute(new GuiTask(){
					protected void executeInEDT(){
						xsltProcessor.textBox("xslt.result.prompt").targetCastedTo(JTextComponent.class).setText(dest);
					}
			});
		}

		return xsltProcessor;


    }
}
