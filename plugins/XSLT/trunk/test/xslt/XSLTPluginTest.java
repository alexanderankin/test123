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

import static org.fest.assertions.Assertions.assertThat;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.findDialogByTitle;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import javax.swing.text.JTextComponent;

import org.fest.swing.data.TableCell;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.timing.Pause;
import org.fest.swing.timing.Timeout;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.TestUtils.ClickT;
import org.gjt.sp.jedit.testframework.TestUtils.Option;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.jEdit;

/**
 * integration tests using test_data
 * $Id$
 */
@RunWith(JEditRunner.class)
public class XSLTPluginTest{
	@Rule
	public TestData testData = new TestData();
    
    @Test
    public void testXSLT() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"simple/transform.xsl");
    	String dest = "";
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,dest,1);
    	
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		assertEquals("hello world", b.getText(0,b.getLength()));
		TestUtils.close(TestUtils.view(),b);
		xsltProcessor.close();
    }
    
    @Test
    public void testXSLTErrorList() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"broken/transform.xsl");
    	
		action("error-list-show");
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",1);
		
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();

		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT error) xsl:ourrtput"));
		errorlist.close();
		xsltProcessor.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTBaseURIBug() throws IOException{
    	File xsl = new File(testData.get(),"base_uri_bug/base-uri-bug.xsl");
    	
		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	final FrameFixture xsltProcessor = setupProcessor(xsl,xsl,"",1);
		
		// an error will be reported
		ClickT click = new ClickT(Option.OK);
		click.start();
		
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		
		assertEquals(0,b.getLength());

		xsltProcessor.close();

		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("15: (XSLT error)"));
		errorlist.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTRuntimeError() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"broken/fails_at_runtime.xsl");
    	
		action("error-list-show");
		
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",1);
		
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		assertThat(b.getName().matches("Untitled-\\d+"));
		
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("16: (XSLT error)"));
		errorlist.close();
		xsltProcessor.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTSAXError() throws IOException{
    	final File xml = new File(testData.get(),"broken/source.xml");
    	File xsl = new File(testData.get(),"simple/transform.xsl");
    	
		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

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
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		
		xsltProcessor.close();

		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("2: (SAX error) Content is not allowed in prolog"));
		errorlist.close();
		TestUtils.close(TestUtils.view(),b);
    }

    /**
     */
    @Test
    public void testXSLTCompile() throws IOException{
    	File xsl = new File(testData.get(),"broken/transform.xsl");
    	
    	TestUtils.openFile(xsl.getPath());

		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	action("error-list-clear");
    	
		Pause.pause(1000);

		action("xslt.compile");
    	

		Pause.pause(1000);
    	
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT error)"));
		errorlist.close();
    }


    @Test
    public void testXSLTResultFile() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"simple/transform.xsl");
    	File result = new File(testData.get(),"simple/output.txt");
    	
    	if(result.exists()){
    		assertTrue(result.delete());
    	}
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,result.getAbsolutePath(),1);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
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
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"simple/transform.xsl");
    	File result = new File(testData.get(),"simple/output.txt");
    	
    	if(result.exists()){
    		assertTrue(result.delete());
    	}
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,result.getAbsolutePath(),1);
				
		xsltProcessor.checkBox("open-result").requireSelected().uncheck();
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		xsltProcessor.checkBox("open-result").check();
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		assertEquals(xml.getName(),b.getName());
    	assertTrue(result.delete());
    }

    @Test
    public void testXSLTSourceURI() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"xslt2/base_document_uri.xsl");

    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",2);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
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
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"xslt2/result-document.xsl");
    	File result = new File(testData.get(),"xslt2/res.txt");
    	File realOutput = new File(testData.get(),"xslt2/output-document.txt");
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,result.getPath(),2);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		xsltProcessor.close();

		Pause.pause(1000);
		Buffer b;
		if(result.exists()){
			b = TestUtils.openFile(result.getPath());
			
			assertEquals(0,b.getLength());
			
			TestUtils.close(TestUtils.view(),b);                 
		}
		
		b = TestUtils.openFile(realOutput.getPath());
		assertEquals("Hello world !",b.getText(0,b.getLength()));		
		
		TestUtils.close(TestUtils.view(),b);
		
		if(result.exists())assertTrue(result.delete());
		assertTrue(realOutput.delete());
    }

    @Test
    public void testXSLTResolver() throws IOException{
    	File xml = new File(testData.get(),"resolver/actions.xml");
    	File xsl = new File(testData.get(),"resolver/transform.xsl");

    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,"",2);
				
		
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		String res = b.getText(0,b.getLength());
		assertThat(res).contains("Transform XML (xslt.transform)");
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testXSLTCurrentStylesheet() throws IOException{
    	File xsl = new File(testData.get(),"simple/transform.xsl");

    	final FrameFixture xsltProcessor = setupProcessor(xsl,xsl,"",1);
    	
		xsltProcessor.radioButton("xslt.stylesheets.buffer").check();
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		xsltProcessor.radioButton("xslt.stylesheets.file").check();
		xsltProcessor.close();

		Buffer b = view().getBuffer();
		
		String res = b.getText(0,b.getLength());
		assertThat(res).contains("hello ");
		TestUtils.close(TestUtils.view(),b);
    }

	@Test
    public void testXSLTParameters() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"parameters/stylesheet-with-parameters.xsl");
    	
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
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		
		assertEquals("Hello world !",b.getText(0,b.getLength()));

		xsltProcessor.close();
		TestUtils.close(TestUtils.view(),b);
    }

    @Test
    public void testURIResolverCompound() throws IOException{
    	File xml = new File(testData.get(),"simple/source.xml");
    	File xsl = new File(testData.get(),"compound_stylesheet/full.xsl");
    	String dest = "";
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,dest,1);
    	
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		
		assertEquals("this is a greeting: hello world", b.getText(0,b.getLength()));
		TestUtils.close(TestUtils.view(),b);
		xsltProcessor.close();
    }

    @Test
    public void testURIResolverIntrospection() throws IOException{
    	File xml = new File(testData.get(),"introspection/source.xml");
    	File xsl = new File(testData.get(),"introspection/transform.xsl");
    	String dest = "";
    	
    	final FrameFixture xsltProcessor = setupProcessor(xml,xsl,dest,1);
    	
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		Buffer b = view().getBuffer();
		
		assertThat(b.getText(0,b.getLength())).contains("- there are 2 templates");
		TestUtils.close(TestUtils.view(),b);
		xsltProcessor.close();
    }
    
    @Test
    public void testOutputEncoding() throws IOException{
    	File dest = new File(testData.get(),"encoding/result.xml");
    	File xsl;
    	String encoding;
    	
    	if(Charset.defaultCharset().name().equals("UTF-8")){
    		xsl = new File(testData.get(),"encoding/accentedchar-iso-8859-1.xsl");
    		encoding="ISO-8859-1";
    	}else{
    		xsl = new File(testData.get(),"encoding/accentedchar-utf8.xsl");
    		encoding="UTF-8";
    	}
    	
    	final FrameFixture xsltProcessor = setupProcessor(xsl,xsl,dest.getPath(),1);
    	
		xsltProcessor.button("xslt.transform").click();
		
		xsltProcessor.button("xslt.transform").requireEnabled(Timeout.timeout(1000));
		
		xsltProcessor.close();
		
		try{
			BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(dest), encoding));
			boolean found = false;
			for(String line=rd.readLine(); line != null ; line=rd.readLine()){
				if(line.contains("<test>")){
					assertTrue("encoding error (should contain r\u00e9ussi)? "+line, line.contains("r\u00e9ussi"));
					found = true;
				}
			}
			rd.close();
			assertTrue(found);
			
			assertTrue(dest.delete());
		}catch(IOException e){
			fail("Exception should not happen: "+e);
		}
    }

    public FrameFixture setupProcessor(File xml, File xsl, final String dest,int version){
    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	options.comboBox("factory").selectItem(Pattern.compile("XSLT "+version+"\\.0.*"));
    	optionsF.OK();


    	TestUtils.openFile(xml.getPath());
    	action("xslt-processor-float",1);
    	
    	
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	System.err.println("xsltProcessor id="+xsltProcessor.hashCode());
		xsltProcessor.radioButton("xslt.source.buffer").click();

		while(xsltProcessor.list("stylesheets").contents().length>0)
		{
			xsltProcessor.list("stylesheets").selectItem(0);
			xsltProcessor.button("xslt.stylesheets.remove").click();
		}
		xsltProcessor.button("xslt.stylesheets.add").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser - Open");
		//there is always a temporisation until all content gets loaded
		//Pause.pause(1000);
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
