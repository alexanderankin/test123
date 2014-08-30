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
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.Containers;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * unit tests for BufferOrFileVFSSelector
 * $Id$
 */
@RunWith(JEditRunner.class)
public class BufferOrFileVFSSelectorTest{
	
	@Rule
	public TestData testData = new TestData();
	
    @Test
    public void testEnabledDisabled() throws IOException{
    	
    	final BufferOrFileVFSSelector selector = createSelector();
    	
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
    	final File f = new File(testData.get(), "simple/source.xml");
    	
    	final BufferOrFileVFSSelector selector = createSelector();
    	
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


	private BufferOrFileVFSSelector createSelector() {
		return GuiActionRunner.execute(new GuiQuery<BufferOrFileVFSSelector>() {
			@Override
			protected BufferOrFileVFSSelector executeInEDT() throws Throwable {
				return new BufferOrFileVFSSelector(view(),"xslt.source");
			}
		});
	}

    @Test
    public void testOpenFile(){
    	final File f = new File(testData.get(), "simple/source.xml");
    	BufferOrFileVFSSelector selector = createSelector();
    	
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
		TestUtils.close(view(), view().getBuffer());
    }
    
    @Test
    public void testSelectSource(){
    	final File xml = new File(testData.get(), "simple/source.xml");
    	File xsl = new File(testData.get(), "simple/transform.xsl");
    	final BufferOrFileVFSSelector selector = createSelector();
    	GuiActionRunner.execute(new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				selector.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
			}
		});
    	
    	final FrameFixture frame = new FrameFixture(TestUtils.robot(),Containers.frameFor(selector));
     	frame.show();
   	
		frame.radioButton("xslt.source.file").click();
		
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					frame.textBox("xslt.source.prompt").targetCastedTo(JTextComponent.class).setText(xml.getPath());
				}
		});

		frame.textBox("xslt.source.prompt").showPopupMenu().menuItemWithPath("Select source").click();
		
		DialogFixture browseDialog = findDialogByTitle("File Browser - Open");
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

		frame.textBox("xslt.source.prompt").requireText(xml.getPath());
		
		frame.close();
		TestUtils.close(view(), view().getBuffer());
    }

    @Test
    public void testEnterNotEverywhere(){
    	final File f = new File(testData.get(), "simple/source.xml");
    	File xsl = new File(testData.get(), "simple/transform.xsl");

    	BufferOrFileVFSSelector selector = createSelector();
    	
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
		
		TestUtils.close(view(), view().getBuffer());
		TestUtils.close(view(), view().getBuffer());
    }

}
