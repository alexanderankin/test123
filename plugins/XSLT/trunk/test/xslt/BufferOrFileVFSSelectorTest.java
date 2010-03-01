/*
 * BufferOrFileVFSSelectorTest.java
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
import static org.junit.Assume.*;

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
 * unit tests for BufferOrFileVFSSelector
 * $Id$
 */
public class BufferOrFileVFSSelectorTest{
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
    	
    	final BufferOrFileVFSSelector selector = new BufferOrFileVFSSelector(view(),"xslt.source");
    	
    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		frame.radioButton("xslt.source.buffer").click();
		
		frame.textBox("xslt.source.prompt").requireDisabled();
		frame.button("xslt.source.select").requireDisabled();
		
		frame.radioButton("xslt.source.file").click();
		
		frame.textBox("xslt.source.prompt").requireEnabled();
		frame.button("xslt.source.select").requireEnabled();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setEnabled(false);
				}
		});
		frame.radioButton("xslt.source.buffer").requireDisabled();
		frame.radioButton("xslt.source.file").requireDisabled();
		frame.textBox("xslt.source.prompt").requireDisabled();
		frame.button("xslt.source.select").requireDisabled();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setEnabled(true);
				}
		});
		frame.textBox("xslt.source.prompt").requireEnabled();
		frame.button("xslt.source.select").requireEnabled();

		frame.close();
    }
    
    
    @Test
    public void testAPI() throws IOException{
    	final File f = new File(testData, "simple/source.xml");
    	
    	final BufferOrFileVFSSelector selector = new BufferOrFileVFSSelector(view(),"xslt.source");
    	
    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		frame.radioButton("xslt.source.file").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setSourceFile(jEdit.getProperty("xslt.source.browse.prompt"));
				}
		});
		
		assertFalse(selector.isSourceFileDefined());
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setFileSelected(false);
				}
		});
		frame.radioButton("xslt.source.buffer").requireSelected();
		
		assertTrue(selector.isSourceFileDefined());
		

		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setFileSelected(true);
				}
		});
		frame.radioButton("xslt.source.file").requireSelected();

		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					selector.setSourceFile(f.getPath());
				}
		});
		frame.textBox("xslt.source.prompt").requireText(f.getPath());
		assertTrue(selector.isSourceFileDefined());

		frame.close();
    }

    @Test
    public void testOpenFile(){
    	final File f = new File(testData, "simple/source.xml");
    	BufferOrFileVFSSelector selector = new BufferOrFileVFSSelector(view(),"xslt.source");
    	
		assumeTrue(!view().getBuffer().getPath().equals(f.getPath()));

    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		frame.radioButton("xslt.source.file").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					frame.textBox("xslt.source.prompt").targetCastedTo(JTextComponent.class).setText(f.getPath());
				}
		});
		
		frame.textBox("xslt.source.prompt").showPopupMenu().menuItemWithPath("Open file").click();
		
		assertEquals(f.getPath(),view().getBuffer().getPath());
		
		frame.close();
		jEdit.closeBuffer(view(),view().getBuffer());
    }
    
    @Test
    public void testSelectSource(){
    	final File xml = new File(testData, "simple/source.xml");
    	File xsl = new File(testData, "simple/transform.xsl");
    	BufferOrFileVFSSelector selector = new BufferOrFileVFSSelector(view(),"xslt.source");
    	selector.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
     	frame.show();
   	
		frame.radioButton("xslt.source.file").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					frame.textBox("xslt.source.prompt").targetCastedTo(JTextComponent.class).setText(xml.getPath());
				}
		});

		frame.textBox("xslt.source.prompt").showPopupMenu().menuItemWithPath("Select source").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		Pause.pause(1000);
		browseDialog.button("up").click();
		Pause.pause(1000);
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xsl.getParentFile().getName())).doubleClick();
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xsl.getName()));
		browseDialog.button("ok").click();

		frame.textBox("xslt.source.prompt").requireText(xsl.getPath());
		
		
		frame.button("xslt.source.select").click();
		
		browseDialog = findDialogByTitle("File Browser");
		Pause.pause(1000);
		browseDialog.button("up").click();
		Pause.pause(1000);
		browseDialog.table("file").cell(
			browseDialog.table("file").cell(xml.getParentFile().getName())).doubleClick();
		Pause.pause(1000);
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(xml.getName()));
		browseDialog.button("ok").click();

		frame.textBox("xslt.source.prompt").requireText(xml.getPath());
		
		frame.close();
		jEdit.closeBuffer(view(),view().getBuffer());
    }

    @Test
    public void testEnterNotEverywhere(){
    	final File f = new File(testData, "simple/source.xml");
    	File xsl = new File(testData, "simple/transform.xsl");

    	BufferOrFileVFSSelector selector = new BufferOrFileVFSSelector(view(),"xslt.source");
    	
		assumeTrue(!view().getBuffer().getPath().equals(f.getPath()));

    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
    	frame.show();
    	
		frame.radioButton("xslt.source.file").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					frame.textBox("xslt.source.prompt").targetCastedTo(JTextComponent.class).setText(f.getPath());
				}
		});
		
		frame.textBox("xslt.source.prompt").pressAndReleaseKeys(KeyEvent.VK_ENTER);
		
		assertEquals(f.getPath(),view().getBuffer().getPath());
		
		TestUtils.openFile(xsl.getPath());

		
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
		
		assertEquals(xsl.getPath(),view().getBuffer().getPath());
		
		jEdit.closeBuffer(view(),view().getBuffer());
		jEdit.closeBuffer(view(),view().getBuffer());
    }

}
