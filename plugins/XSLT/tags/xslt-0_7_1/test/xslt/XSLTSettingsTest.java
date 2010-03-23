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
import org.gjt.sp.jedit.EditPlugin;
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
 * integration tests of the Load & Save settings feature.
 * $Id$
 */
public class XSLTSettingsTest{
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
    public void testSaveAndLoad(){
    	final File xml = new File(testData,"simple/source.xml");
    	final File xsl = new File(testData,"simple/transform.xsl");
    	File settings = new File(EditPlugin.getPluginHome(XSLTPlugin.class)
    		,"source-transform-settings.xml");
    	
    	if(settings.exists()) {
    		assertTrue(settings.delete());
    	}
    	
    	action("xslt-processor");
    	final FrameFixture xsltProcessor = TestUtils.findFrameByTitle("XSLT Processor");
    	
    	xsltProcessor.radioButton("xslt.source.file").click();
    	xsltProcessor.button("xslt.source.select").click();
		final DialogFixture browseDialogXML = findDialogByTitle("File Browser");
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
    	
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.textBox("filename").requireText(settings.getName());
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
    	
    	browseDialog = findDialogByTitle("File Browser");
		//there is always a temporisation until all content gets loaded
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(settings.getName()));
		browseDialog.button("ok").click();
		
		xsltProcessor.moveToFront();
		Pause.pause(2000);
		xsltProcessor.textBox("xslt.source.prompt").requireText(xml.getPath());
		
		assertThat(xsltProcessor.list("stylesheets").contents()).containsOnly(xsl.getPath());
		
		parms.requireContents(new String[][]{ { "p", "world"}, {"q", "value"}} );
		
		xsltProcessor.radioButton("xslt.result.buffer").requireSelected();
		
		xsltProcessor.close();
    }
    
}
