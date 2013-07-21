/*
 * XSLTSettingsTest.java
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.fest.swing.data.TableCell;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * integration tests of the Load & Save settings feature.
 * $Id$
 */
@RunWith(JEditRunner.class)
public class XSLTSettingsTest{
	@Rule
	public TestData testData = new TestData();
    
    @Test
    public void testSaveAndLoad(){
    	final File xml = new File(testData.get(),"simple/source.xml");
    	final File xsl = new File(testData.get(),"simple/transform.xsl");
    	File settings = new File(EditPlugin.getPluginHome(XSLTPlugin.class)
    		,"source-transform-settings.xml");
    	
    	if(settings.exists()) {
    		assertTrue(settings.delete());
    	}
    	
    	action("xslt-processor");
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
    	xsltProcessor.radioButton("xslt.source.file").click();
    	xsltProcessor.button("xslt.source.select").click();
		final DialogFixture browseDialogXML = findDialogByTitle("File Browser - Open");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		
    	GuiActionRunner.execute(new GuiTask(){
    			protected void executeInEDT(){
    				browseDialogXML.textBox("filename").targetCastedTo(JTextComponent.class).setText(xml.getPath());
    			}
    	});
		browseDialogXML.button("ok").click();

		
    	xsltProcessor.radioButton("xslt.stylesheets.file").click();

        final XSLTProcessor processor = (XSLTProcessor)jEdit.getFirstView().getDockableWindowManager().getDockable("xslt-processor");

    	GuiActionRunner.execute(new GuiTask(){
    			protected void executeInEDT(){
    				processor.getStylesheetPanel().setStylesheets(new String[]{xsl.getPath()});
    			}
    	});
		

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
					((JTextComponent)parms.cell(TableCell.row(0).column(0)).editor()).setText("value");
				}
		});


    	xsltProcessor.radioButton("xslt.result.buffer").click();

    	xsltProcessor.button("xslt.settings.save").click();

		DialogFixture browseDialog = findDialogByTitle("File Browser - Save");
		browseDialog.textBox("filename").setText(settings.getName());
		browseDialog.button("ok").click();

		assertThat(settings.exists());
		
		xsltProcessor.moveToFront();

		// now, change things a bit...
		while(parms.rowCount()>0)
		{
			parms.selectRows(0);
			xsltProcessor.button("xslt.parameters.remove").click();
		}
		
		
    	GuiActionRunner.execute(new GuiTask(){
    			protected void executeInEDT(){
    				processor.getStylesheetPanel().setStylesheets(new String[]{""});
    			}
    	});
		
    	xsltProcessor.radioButton("xslt.result.file").click();
    	
    	// now, reload previous settings
    	xsltProcessor.button("xslt.settings.load").click();
    	
    	browseDialog = findDialogByTitle("File Browser - Open");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(settings.getName()));
		browseDialog.button("ok").click();
		
		xsltProcessor.moveToFront();
		Pause.pause(2000);
		xsltProcessor.textBox("xslt.source.prompt").requireText(xml.getPath());
		
		assertThat(xsltProcessor.list("stylesheets").contents()).containsOnly(xsl.getPath());
		
		// order of params I get back is not determinate, so have to use containsOnly
		//instead of parms.requireContents(new String[][]{ {"q", "value"}, { "p", "world"}} );
		String[][] contents = parms.contents();
		assertEquals(2, contents.length);
		List<List<String>> cc = Arrays.asList(Arrays.asList(contents[0]),Arrays.asList(contents[1]));
		assertThat(cc).containsOnly(Arrays.asList("q", "value"), Arrays.asList("p", "world"));
		
		xsltProcessor.radioButton("xslt.result.buffer").requireSelected();
		
		xsltProcessor.close();
    }
    
}
