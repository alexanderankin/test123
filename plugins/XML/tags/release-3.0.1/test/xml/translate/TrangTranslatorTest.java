/*
 * TrangTranslatorTest.java
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
import static org.fest.assertions.Assertions.assertThat;
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.close;
import static org.gjt.sp.jedit.testframework.TestUtils.findDialogByTitle;
import static org.gjt.sp.jedit.testframework.TestUtils.jEditFrame;
import static org.gjt.sp.jedit.testframework.TestUtils.newFile;
import static org.gjt.sp.jedit.testframework.TestUtils.openFile;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.timing.Pause;
import org.fest.swing.timing.Timeout;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.TestUtils.ClickT;
import org.gjt.sp.jedit.testframework.TestUtils.Option;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
// }}}


/**
 * unit tests for TrangTranslator
 * $Id$
 */
public class TrangTranslatorTest{
	private static File testData;
	private final List<String> empty = Collections.<String>emptyList();
	
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
    	final File out = new File(testData,"catalog.xsd");
    	
    	ClickT clickT = new ClickT(Option.OK);
    	clickT.start();
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					TrangTranslator.translate(view()
						,null,Collections.singletonList(in.getPath()),empty
						,null,out.getPath(),empty);
				}
		});
    	
		clickT.waitForClick();
    	
    	Buffer outB = jEdit.getBuffer(out.getPath());
    	assertNotNull(outB);
    	assertTrue(outB.getText(0,outB.getLength())
    		.contains("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""));
    	close(view(),outB);

    	outB = jEdit.getBuffer(new File(out.getParentFile(),"xml.xsd").getPath());
    	assertNotNull(outB);
    	assertTrue(outB.getText(0,outB.getLength())
    		.contains("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""));
    	close(view(),outB);
    }
    
    @Test
    public void testGuessInputType(){
    	Buffer b = newFile();
    	b.setMode(jEdit.getMode("xml"));
    	assertEquals("xml",TrangTranslator.guessInputType(b));
    	
    	b.setMode(jEdit.getMode("text"));
    	assertNull(TrangTranslator.guessInputType(b));
    	
    	b = openFile(new File(testData,"bug1082903/try.dtd").getPath());
    	assertEquals("dtd",TrangTranslator.guessInputType(b));
    
    	close(view(),b);

    	// Trang doesn't have XSD input so it sees the XSD as an example XML file
    	b = openFile(new File(testData,"multiple_name/schema.xsd").getPath());
    	assertEquals("xml",TrangTranslator.guessInputType(b));

    	close(view(),b);
    }

    @Test
    public void testDTDToXSDWAction() throws IOException{
    	final File in = new File(testData,"../xml/dtds/oasis-catalog.dtd");
    	final File out = new File(testData,"../xml/dtds/oasis-catalog.xsd");
    	final File out2 = new File(testData,"../xml/dtds/xml.xsd");
    	
    	openFile(in.getPath());
    	
    	ClickT clickT = new ClickT(Option.OK);
    	clickT.start();
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					action("xml-translate-to-xsd");
				}
		});
    	
		clickT.waitForClick();
    	
    	Buffer outB = jEdit.getBuffer(out.getPath());
    	assertNotNull(outB);
    	assertTrue(outB.getText(0,outB.getLength())
    		.contains("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""));
    	close(view(),outB);
    	
    	outB = jEdit.getBuffer(out2.getPath());
    	assertNotNull(out2);
    	close(view(),outB);
    }
   
    @Test
    public void testFailInput() throws IOException{
    	final File in = new File(testData,"../XML.props");
    	
    	Buffer b = openFile(in.getPath());
    	
    	
    	new Thread(){
    		public void run(){
				GuiActionRunner.execute(new GuiTask(){
						protected void executeInEDT(){
							action("xml-translate-to-xsd");
						}
				});
			}
		}.start();
		Pause.pause(1000);
    	
		DialogFixture d = findDialogByTitle("Couldn't guess input type");
		
		assertThat(d.list().contents()).containsOnly("xml","dtd","rng","rnc");
		d.optionPane().yesButton().click();
		
    	ClickT clickT = new ClickT(Option.OK);
    	clickT.start();

		clickT.waitForClick();
    	
    	close(view(),b);

    }
    
    @Test
    public void testFailParams() throws IOException{
    	final File in = new File(testData,"simple/actions.xml");
    	final File out = new File(testData,"simple/actions.dtd");
    	
    	new Thread(){
    		public void run(){
				GuiActionRunner.execute(new GuiTask(){
						protected void executeInEDT(){
							TrangTranslator.translate(view()
								,null,Collections.singletonList(in.getPath()),Collections.singletonList("weirdy param")
								,null,out.getPath(),Collections.<String>emptyList());
						}
				});
			}
		}.start();
		Pause.pause(1000);
    	
		DialogFixture d = findDialogByTitle("Translation failed");
		
		d.optionPane().okButton().click();
		
    }
    

    @Test
    public void testXMLToRNG() throws IOException{
    	final String[] in = {
    			new File(testData,"../actions.xml").getPath(),
    			new File(testData,"relax_ng/actions.xml").getPath()
    		};
    	final File out = new File(testData,"../actions.rnc");
    	
    	ClickT clickT = new ClickT(Option.OK);
    	clickT.start();
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					TrangTranslator.translate(view()
						,null,Arrays.asList(in),empty
						,null,out.getPath(),empty);
				}
		});
    	
		clickT.waitForClick();
    	
    	Buffer outB = jEdit.getBuffer(out.getPath());
    	assertNotNull(outB);
    	assertTrue(outB.getText(0,outB.getLength())
    		.contains("element CODDE")); 
    	assertTrue(outB.getText(0,outB.getLength())
    		.contains("element CODE")); 
    	close(view(),outB);
    }
   
    @Test
    public void testLiveBufferContents() throws IOException{
    	final File in = new File(testData,"rnc/actions.rnc");
     	final File out = new File(testData,"rnc/actions.dtd");
     	
     	Buffer b = openFile(in.getPath());
    	
    	b.insert(96," | \"MAYBE\"");
    	
     	ClickT clickT = new ClickT(Option.OK);
    	clickT.start();
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					TrangTranslator.translate(view()
						,null,Collections.singletonList(in.getPath()),empty
						,null,out.getPath(),empty);
				}
		});
    	
		clickT.waitForClick();
    	
    	Buffer outB = jEdit.getBuffer(out.getPath());
    	assertNotNull(outB);
    	assertTrue(outB.getText(0,outB.getLength())
    		.contains("<!ENTITY % att-bool \"TRUE|FALSE|MAYBE\">"));
    	close(view(),outB);
   }

    @Test
    public void testNonExistantBuffer() throws IOException{
    	final File in = new File(testData,"rnc/NOT_THERE.rnc");
     	final File out = new File(testData,"rnc/actions.dtd");
     	
		SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					TrangTranslator.translate(view()
						,null,Collections.singletonList(in.getPath()),empty
						,null,out.getPath(),empty);
				}
		});
    	
		// triggers an IOException
		final JOptionPaneFixture options = jEditFrame().optionPane(Timeout.timeout(5000));
		options.requireErrorMessage();
		options.requireMessage(Pattern.compile(".*FileNotFoundException.*NOT_THERE.*",Pattern.DOTALL));
		options.okButton().click();
   }
}
