/*
 * VFSFileTextFieldTest.java
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
package xml.gui;

// {{{ jUnit imports 
import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.gjt.sp.jedit.testframework.TestUtils.findDialogByTitle;
import static org.gjt.sp.jedit.testframework.TestUtils.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.text.JTextComponent;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.Containers;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
// }}}

/**
 * unit tests for VFSFileTextField
 * $Id$
 */
public class VFSFileTextFieldTest{
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
    public void testEnabledDisabled() throws IOException{
    	
    	final VFSFileTextField selector = new VFSFileTextField(view(),"xml.translate.output");
    	selector.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    	
    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		frame.textBox("xml.translate.output.prompt").requireEnabled();
		frame.button("xml.translate.output.select").requireEnabled();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setEnabled(false);
				}
		});
		frame.textBox("xml.translate.output.prompt").requireDisabled();
		frame.button("xml.translate.output.select").requireDisabled();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setEnabled(true);
				}
		});
		frame.textBox("xml.translate.output.prompt").requireEnabled();
		frame.button("xml.translate.output.select").requireEnabled();

		frame.close();
    }
    
    
    @Test
    public void testAPI() throws IOException{
    	final File f = new File(testData, "simple/actions.xml");
    	
    	final VFSFileTextField selector = new VFSFileTextField(view(),"xml.translate.output");
    	selector.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    	
    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setFile(jEdit.getProperty("xml.translate.output.browse.prompt"));
				}
		});
		
		assertFalse(selector.isFileDefined());
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setFile(f.getPath());
				}
		});
		frame.textBox("xml.translate.output.prompt").requireText(f.getPath());
		assertTrue(selector.isFileDefined());

		frame.close();
    }

    @Test
    public void testOpenFile(){
    	final File f = new File(testData, "simple/actions.xml");
    	VFSFileTextField selector = new VFSFileTextField(view(),"xml.translate.output");
    	selector.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    	
		assumeTrue(!view().getBuffer().getPath().equals(f.getPath()));

    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					frame.textBox("xml.translate.output.prompt").targetCastedTo(JTextComponent.class).setText(f.getPath());
				}
		});
		
		frame.textBox("xml.translate.output.prompt").showPopupMenu().menuItemWithPath("Open file").click();
		
		assertEquals(f.getPath(),view().getBuffer().getPath());
		
		frame.close();
		jEdit.closeBuffer(view(),view().getBuffer());
    }
    
    @Test
    public void testSelectSource(){
    	final File xml = new File(testData, "simple/actions.xml");
    	File xsd = new File(testData, "simple/actions.xsd");
    	VFSFileTextField selector = new VFSFileTextField(view(),"xml.translate.output");
    	selector.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
     	frame.show();
   	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					frame.textBox("xml.translate.output.prompt").targetCastedTo(JTextComponent.class).setText(xml.getPath());
				}
		});

		frame.textBox("xml.translate.output.prompt").showPopupMenu().menuItemWithPath("Select destination").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser - Open");
		Pause.pause(1000);
		browseDialog.button("up").click();
		Pause.pause(1000);
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xsd.getParentFile().getName())).doubleClick();
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsd.getName()));
		browseDialog.button("ok").click();

		frame.textBox("xml.translate.output.prompt").requireText(xsd.getPath());
		
		
		frame.button("xml.translate.output.select").click();
		
		browseDialog = findDialogByTitle("File Browser - Open");
		Pause.pause(1000);
		browseDialog.button("up").click();
		Pause.pause(1000);
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xml.getParentFile().getName())).doubleClick();
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xml.getName()));
		browseDialog.button("ok").click();

		frame.textBox("xml.translate.output.prompt").requireText(xml.getPath());
		
		frame.close();
		jEdit.closeBuffer(view(),view().getBuffer());
    }

    @Test
    public void testEnterNotEverywhere(){
    	final File f = new File(testData, "simple/actions.xml");
    	File xsd = new File(testData, "simple/actions.xsd");

    	VFSFileTextField selector = new VFSFileTextField(view(),"xml.translate.output");
    	selector.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    	
		assumeTrue(!view().getBuffer().getPath().equals(f.getPath()));

    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					frame.textBox("xml.translate.output.prompt").targetCastedTo(JTextComponent.class).setText(f.getPath());
				}
		});
		
		frame.textBox("xml.translate.output.prompt").pressAndReleaseKeys(KeyEvent.VK_ENTER);
		
		assertEquals(f.getPath(),view().getBuffer().getPath());
		
		TestUtils.openFile(xsd.getPath());

		
		frame.close();
		new Thread(){
			public void run(){
				action("goto-line");
		}}.start();
		Pause.pause(1000);
		final DialogFixture browseDialog = findDialogByTitle("Go To Line");
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					browseDialog.textBox().targetCastedTo(JTextComponent.class).setText("2");
				}
		});
		
		browseDialog.textBox().pressAndReleaseKeys(KeyEvent.VK_ENTER);
		
		assertEquals(xsd.getPath(),view().getBuffer().getPath());
		
		Pause.pause(500);
		jEdit.closeBuffer(view(),view().getBuffer());
		jEdit.closeBuffer(view(),view().getBuffer());
    }

}
