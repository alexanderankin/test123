package test;

import java.awt.Component;
import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*; 
import javax.swing.*;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.edt.*;
import org.fest.swing.timing.Pause;

import org.gjt.sp.jedit.testframework.Log;
import org.gjt.sp.jedit.testframework.TestUtils;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.options.PluginOptions;

import ise.plugin.svn.gui.SubversionOptions;

public class OptionPaneTest {
    @BeforeClass
    public static void setUpjEdit() {
        TestUtils.beforeClass();
    }

    @AfterClass
    public static void tearDownjEdit() {
        TestUtils.afterClass();
    }


    @Test
    public void testOptionPane() {
        Log.log( "testOptionPane" );
        
        // set the jEdit properties for the option pane to known values
		jEdit.setBooleanProperty( "ise.plugin.svn.useTsvnTemplate", false );
		jEdit.setIntegerProperty( "ise.plugin.svn.logRows", 500);

		// open the plugin options and select the Subverion options pane
		TestUtils.jEditFrame().menuItemWithPath("Plugins","Plugin Options...").click();
		Log.log("next find dialog");
		DialogFixture optionsDialog = WindowFinder.findDialog(PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		Log.log("next select path");
		TestUtils.selectPath(optionsDialog.tree(),new String[]{"Plugins","Subversion"});
		JPanelFixture pane = optionsDialog.panel("SubversionOptions");
		
		// test that the checkbox is unchecked and then check the checkbox
		JCheckBoxFixture useTsvnTemplate = pane.checkBox("useTsvnTemplate");
		useTsvnTemplate.requireNotSelected();
		useTsvnTemplate.click();
		
		// check that the spinner has the right value, then spin it up 10
		JSpinnerFixture maxLogs = pane.spinner("maxLogs");
		maxLogs.requireValue(500);
		maxLogs.increment(10);
		
		// click the OK button on the plugin options dialog
		optionsDialog.button(new GenericTypeMatcher<JButton>(JButton.class){
		        public boolean isMatching(JButton button) {
		             return "OK".equals(button.getText());   
		        }
		}).click();
		
		// wait a second to make sure jEdit has time to save the properties
		Pause.pause(1000);
		
		// test that the properties were set correctly
		boolean tsvn = jEdit.getBooleanProperty("ise.plugin.svn.useTsvnTemplate", false);
		int value = jEdit.getIntegerProperty("ise.plugin.svn.logRows", 500);
		
		assertTrue("useTsvnTemplate not checked and it should be", tsvn);
		assertTrue("logRows is not 510 and it should be", value == 510);
    }
}