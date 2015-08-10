/*
* SchemaMappingManagerTest.java
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2009 Eric Le Lay
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
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.core.matcher.*;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;

import static xml.XMLTestUtils.*;
import static org.gjt.sp.jedit.testframework.EBFixture.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import static org.gjt.sp.jedit.testframework.TestUtils.*;

// }}}

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.io.VFS;

import java.io.*;
import java.net.*;
import javax.swing.text.JTextComponent;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import org.gjt.sp.jedit.gui.CompletionPopup;

/**
* Various tests for the xml-prompt-schema and xml-prompt-typeid actions
* $Id$
*/
public class SchemaMappingManagerTest {
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
    
    /**
    * test the dialog box with file paths
    *
    * FIXME: the test is failing due to my French locale/keyboard layout.
    *        test it again with FEST-SWING version > Nov. 2009
    */
	@Test
	public void testChooseSchemaDialogOnFile() throws java.net.MalformedURLException
	{
		SchemaMappingManager.ChooseSchemaDialog dialog = new SchemaMappingManager.ChooseSchemaDialog(view());
		DialogFixture dialogF = new DialogFixture(robot(), dialog);
		
		File buf = new File(testData.getPath(),"relax_ng/actions.xml");
		dialog.init(buf.toString(),null,true);
		
		dialogF.show();
		dialogF.textBox("buffer_path").requireNotEditable();
		dialogF.textBox("buffer_path").requireText(buf.toString());
		
		dialogF.textBox("path").requireEditable().requireText(MiscUtilities.getParentOfPath(buf.toString()));
		dialogF.checkBox("relative").requireEnabled().requireNotSelected();
		dialogF.textBox("relative_path").requireEnabled().requireText(new File(MiscUtilities.getParentOfPath(buf.toString())).toURL().toString());
		
		File schema = new File(testData.getPath(),"relax_ng/actions.rng");
		
		/* FAILS
		// enter path by hand
		replaceText(dialogF.textBox("path"), schema);
		dialogF.textBox("path").pressAndReleaseKeys(new int[]{KeyEvent.VK_ENTER});
		dialogF.textBox("relative_path").requireEditable().requireText(schema);
		*/
		
		//choose in the VFS browser
		dialogF.button("browse").click();
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell(schema.getName()));
		browseDialog.button("ok").click();
		dialogF.textBox("path").requireText(schema.toString());
		dialogF.textBox("relative_path").requireText(schema.toURL().toString());
		assertEquals(schema.toURL().toString(),dialog.schemaURL.toString());
		
		//make the path relative
		dialogF.checkBox("relative").click();
		dialogF.textBox("relative_path").requireText(schema.getName());
		assertEquals(schema.getName(),dialog.schemaURL.toString());

		dialogF.button(JButtonMatcher.withText("Cancel")).click();

	}
	
    /**
     * test setting the schema for a file
     */
	@Test
	public void testOnFile() throws java.net.MalformedURLException, IOException
	{
		File relax_ng = new File(jEdit.getSettingsDirectory(),"relax_ng");
		copyDirectory(
				new File(testData.getPath(),"relax_ng"),
				relax_ng);
		
		new File(relax_ng,"schemas.xml").delete();
		
		File buf = new File(relax_ng,"actions.xml");
		
		Buffer buffer = openFile(buf.getPath());
		
		
		Thread t = new Thread(){
			public void run()
			{
				action("xml-prompt-schema",1);
			}
		};
		
		t.start();
		
		DialogFixture dialogF = WindowFinder.findDialog(SchemaMappingManager.ChooseSchemaDialog.class).withTimeout(5000).using(robot());
		
		//choose in the VFS browser
		dialogF.button("browse").click();
		DialogFixture browseDialog = findDialogByTitle("File Browser");
		
		browseDialog.table("file").selectCell(
			browseDialog.table("file").cell("actions.rng"));
		browseDialog.button("ok").click();
		
		MessageListener listen = new MessageListener();
		listen.registerForMessage(messageOfClassCondition(sidekick.SideKickUpdate.class));

		dialogF.button(JButtonMatcher.withText("OK")).click();

		// wait for end of parsing
		listen.waitForMessage(10000);
		
		try{t.join();}catch(InterruptedException ie){}
	
    	// XmlPlugin is not activated for some reason ??
		action("sidekick.parser.xml-switch");
		parseAndWait();
    	FrameFixture sidekick = TestUtils.findFrameByTitle("Sidekick");
    	sidekick.close();
		
		assertThat(new File(relax_ng,"schemas.xml")).exists();

		assertEquals(new File(relax_ng,"actions.rng").toURL().toString(),
			buffer.getStringProperty(SchemaMappingManager.BUFFER_AUTO_SCHEMA_PROP));
	}

	/**
     * test setting the schema type for a file
     */
	@Test
	public void testTypeIdOnFile() throws java.net.MalformedURLException, IOException
	{
		File import_schema = new File(jEdit.getSettingsDirectory(),"import_schema");
		copyDirectory(
				new File(testData.getPath(),"import_schema/relax_ng"),
				import_schema);
		
		new File(import_schema,"schemas.xml").delete();
		
		File buf = new File(import_schema,"test_multi_ns.rng");
		
		Buffer buffer = openFile(buf.getPath());
		
		
		Thread t = new Thread(){
			public void run()
			{
				action("xml-prompt-typeid",1);
			}
		};
		
		t.start();
		
		Pause.pause(1000);
		
		DialogFixture dialogF = findDialogByTitle("Choose a TypeID...");
		
		dialogF.comboBox().selectItem("RNG");

		MessageListener listen = new MessageListener();
		listen.registerForMessage(messageOfClassCondition(sidekick.SideKickUpdate.class));

		dialogF.button(JButtonMatcher.withText("OK")).click();

		// wait for end of parsing
		listen.waitForMessage(10000);
		
		try{t.join();}catch(InterruptedException ie){}
		
		assertThat(new File(import_schema,"schemas.xml")).exists();

		String prop = buffer.getStringProperty(SchemaMappingManager.BUFFER_AUTO_SCHEMA_PROP);
		assertNotNull(prop);
		assertThat(prop.endsWith("XML.jar!/xml/dtds/relaxng.rng"));
	}

	/**
     * test setting the schema type for a file with schema-mapping disabled
     */
	@Test
	public void testTypeIdOnFileSchemaMDisabled() throws java.net.MalformedURLException, IOException
	{
		File relax_ng = new File(testData,"schema_mapping_disabled/locate.rng");
		
		Buffer buffer = openFile(relax_ng.getPath());
		
		MessageListener listen = new MessageListener();
		listen.registerForMessage(messageOfClassCondition(sidekick.SideKickUpdate.class));
		action("sidekick-parse",1);
		listen.waitForMessage(10000);

		// by default, relax.rng would be used but, since schema-mapping is disabled, it is not.
		String prop = buffer.getStringProperty(SchemaMappingManager.BUFFER_AUTO_SCHEMA_PROP);
		// so the property is null
		assertEquals(null,prop);

		// will now assign a typeId of RNG
		Thread t = new Thread(){
			public void run()
			{
				action("xml-prompt-typeid",1);
			}
		};
		
		t.start();
		
		Pause.pause(1000);
		
		DialogFixture dialogF = findDialogByTitle("Choose a TypeID...");
		
		dialogF.comboBox().selectItem("RNG");

		listen.registerForMessage(messageOfClassCondition(sidekick.SideKickUpdate.class));

		dialogF.button(JButtonMatcher.withText("OK")).click();

		// wait for end of parsing
		listen.waitForMessage(10000);
		
		try{t.join();}catch(InterruptedException ie){}
		
		assertThat(new File(relax_ng.getParentFile(),"schemas.xml")).doesNotExist();

		prop = buffer.getStringProperty(SchemaMappingManager.BUFFER_SCHEMA_PROP);
		assertNotNull(prop);
		assertThat(prop.endsWith("XML.jar!/xml/dtds/relaxng.rng"));
		
		close(view(),buffer);
	}
	
    /**
     * setting the schema for a jar resource
     * It FAILS
     */
	@Test
	public void testOnResourceInJAR() throws java.net.MalformedURLException, IOException
	{
		URL buf = getClass().getClassLoader().getResource("xml/dtds/locate.rng");
		Buffer buffer = openFile(buf.toString());
		
		
		Thread t = new Thread(){
			public void run()
			{
				action("xml-prompt-schema",1);
			}
		};
		
		t.start();
		
		final DialogFixture dialogF = WindowFinder.findDialog(SchemaMappingManager.ChooseSchemaDialog.class).withTimeout(5000).using(robot());
		
		final String schemaPath = getClass().getClassLoader().getResource("xml/dtds/relaxng.rng").toString();
		
		//choose in the VFS browser
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					dialogF.textBox("path").targetCastedTo(JTextComponent.class).setText(schemaPath);
				}
		});
		
		dialogF.checkBox("relative").click();
		
		dialogF.button(JButtonMatcher.withText("OK")).click();

		MessageListener listen = new MessageListener();
		listen.registerForMessage(messageOfClassCondition(sidekick.SideKickUpdate.class));

		Pause.pause(1000);
		// can't save the schemas.xml inside the jar, so an error is reported
		jEditFrame().optionPane().okButton().click();
		listen.waitForMessage(20000);
		
		try{t.join();}catch(InterruptedException ie){}
		
		
		assertEquals(schemaPath,
			buffer.getStringProperty(SchemaMappingManager.BUFFER_AUTO_SCHEMA_PROP));
		fail("should handle the error by saving to global schemas.xml !");
	}

	public static void copyFile(File source, File dest) throws IOException
	{
		dest.createNewFile();
        VFS.copy(null,source.getPath(),dest.getPath(),view(),true);
	}
	
	public static void copyDirectory(File sourceDir, File destDir) throws IOException
	{
		
		if(!destDir.exists())
		{
			destDir.mkdir();
		}
		
		File[] children = sourceDir.listFiles();
		
		for(File sourceChild : children)
		{
			String name = sourceChild.getName();
			File destChild = new File(destDir, name);
			if(sourceChild.isDirectory()) {
				copyDirectory(sourceChild, destChild);
			}
			else {
				copyFile(sourceChild, destChild);
			}
		}	
	}
	
	public static boolean delete(File resource) throws IOException
	{ 
		if(resource.isDirectory())
		{
			File[] childFiles = resource.listFiles();
			for(File child : childFiles)
			{
				delete(child);
			}
			
		}
		return resource.delete();
	}
	
}
