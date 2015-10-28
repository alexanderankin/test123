/*
 * XMLActionsEntitiesChars.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2014 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;

// {{{ jUnit imports 
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
// }}}

/**
 * integration tests using test_data
 * $Id$
 */
@RunWith(JEditRunner.class)
public class XmlActionEntitiesChars{
	@Rule
	public TestData testData = new TestData();
	
	@Test
	public void entitiesToCharsTest() throws IOException {
    	File xml = new File(testData.get(),"entities/numeric_entities.xml");
    	
    	final Buffer b = TestUtils.openFile(xml.getPath());

    	final View v = TestUtils.view();
    	
    	XMLTestUtils.parseAndWait();
    	
    	//Pause.pause(300, TimeUnit.SECONDS);
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					v.getTextArea().goToBufferStart(false);
					v.getTextArea().goToBufferEnd(true);
				}
		});
		
		action("xml-entities-to-chars");
		
		try{
			String text;
			
			text = GuiActionRunner.execute(new GuiQuery<String>() {
				@Override
				protected String executeInEDT() throws Throwable {
					return b.getText(0, b.getLength());
				}
			});
			
			assertTrue("3 newlines",
					text.contains("Here are 4 newlines: \n\n\n\nHere")); 
			assertTrue("eacute",
					text.contains("Here's 'é': é\n"));
			assertTrue("cat",
					text.contains("Obligatory cat: 🐱\n"));

			assertTrue("XII",
					text.contains("Why write Ⅻ when you can write ⅩⅠⅠ?\n"));
			assertTrue("Delete",
					text.contains("Don't delete: \u007F\n"));
			assertTrue("Private use",
					text.contains("Two private use characters: \udbff\udffe \udbff\udfff\n"));
			
			assertTrue("invalid should not be replaced",
					text.contains("Not unicode (parse error): &#x2;\n"));
			assertTrue("surrogate should not be replaced",
					text.contains("Surrogate: &#D8FF;\n"));
			

		}finally {
			TestUtils.close(v, b);
		}
	}
}