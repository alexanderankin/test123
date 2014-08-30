/*
 * XSLTCompileTest.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010-2014 Eric Le Lay
 *
 * The XSLT plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 2.0, for example used by the Xalan package."
 */
package xslt;

import static org.gjt.sp.jedit.testframework.TestUtils.action;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JEditRunner.class)
public class XSLTCompileTest {
	@Rule
	public TestData testData = new TestData();

	@Test
    public void testNormal() throws IOException{
    	File xsl = new File(testData.get(),"broken/transform.xsl");
    	
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
    	options.checkBox("compile-on-save").check();
    	optionsF.OK();
    	
    	TestUtils.openFile(xsl.getPath());
    	
		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	action("save");
		Pause.pause(3000);
    	
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT)"));
		errorlist.close();
    }
	
	@Test
    public void testSymlink() throws IOException{
    	File xsl = new File(testData.get(),"broken/symlink_to_invalid_xpath.xsl");
    	
    	/* see previous test for why plugin must be activated */
    	jEdit.getPlugin("xslt.XSLTPlugin",true).getPluginJAR().activatePlugin();
    	
    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.checkBox("compile-on-save").check();
    	// Saxon is required
    	options.comboBox("factory").selectItem(Pattern.compile("XSLT 2\\.0.*"));
    	optionsF.OK();
    	
    	final Buffer buffer = TestUtils.openFile(xsl.getPath());
    	
		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	action("save");
		Pause.pause(3000);
    	
		errorlist.tree().selectRow(1);
		assertTrue(errorlist.tree().valueAt(1).startsWith("11: (XSLT)"));
		errorlist.close();
		
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				String text = buffer.getText();
				String bad = "@att-within-att";
				int offset = text.indexOf(bad);
				buffer.remove(offset, bad.length());
				buffer.insert(offset, ".");
			}
		});
		try{
			
			action("error-list-show");
	    	errorlist = TestUtils.findFrameByTitle("Error List");

	    	action("save");
			Pause.pause(3000);
	    	
			assertEquals(0, errorlist.tree().target.getRowCount());
			errorlist.close();
			
		}finally{
			TestUtils.action("undo");
			TestUtils.action("undo");
			TestUtils.action("save");
			Pause.pause(3000);
			// otherwise the error @att-within-att error stays and makes testNormal fail
			TestUtils.action("error-list-clear");
			TestUtils.close(TestUtils.view(), buffer);
		}
    }
	
	@Test
    public void testCompound() throws IOException{
    	File xsl = new File(testData.get(),"broken/error_in_imported.xsl");
    	
    	/* see previous test for why plugin must be activated */
    	jEdit.getPlugin("xslt.XSLTPlugin",true).getPluginJAR().activatePlugin();
    	
    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	options.checkBox("compile-on-save").check();
    	// Saxon is required
    	options.comboBox("factory").selectItem(Pattern.compile("XSLT 2\\.0.*"));
    	optionsF.OK();
    	
    	final Buffer buffer = TestUtils.openFile(xsl.getPath());
    	
		action("error-list-show");
    	FrameFixture errorlist = TestUtils.findFrameByTitle("Error List");

    	action("save");
		Pause.pause(3000);
    	
		assertTrue(errorlist.tree().valueAt(1).startsWith("6: (XSLT)"));
		errorlist.close();
		
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				String text = buffer.getText();
				String bad = "<xsl:import href=\"transform.xsl\"/>";
				int offset = text.indexOf(bad);
				buffer.remove(offset, bad.length());
			}
		});
		try{
			
			action("error-list-show");
	    	errorlist = TestUtils.findFrameByTitle("Error List");

	    	action("save");
			Pause.pause(3000);
	    	
			assertEquals(0, errorlist.tree().target.getRowCount());
			errorlist.close();
			
		}finally{
			TestUtils.action("undo");
			TestUtils.action("save");
			Pause.pause(3000);
			// otherwise the error stays and makes testNormal fail
			TestUtils.action("error-list-clear");
			TestUtils.close(TestUtils.view(), buffer);
		}
    }
}
