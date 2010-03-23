/*
 * XSLTOptionPaneTest.java
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
 * GUI tests of the XSLT option pane.
 * $Id$
 */
public class XSLTOptionPaneTest{
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
    public void testXSLTFactoryErrors(){
    	PluginOptionsFixture optionsF = TestUtils.pluginOptions();
    	final JPanelFixture options = optionsF.optionPane("XSLT","xslt");
    	Pause.pause(1000);
    	
    	JTextComponentFixture factoryErrors = options.textBox(JTextComponentMatcher.withName("xslt.factory-errors"));
    	factoryErrors.requireNotVisible().requireNotEditable();
    	
    	options.comboBox("factory").selectItem(Pattern.compile(".*6.5.5.*"));
    	factoryErrors.requireVisible();
    	factoryErrors.requireText(Pattern.compile(".*"+Pattern.quote(jEdit.getSettingsDirectory())+".*",Pattern.DOTALL));
    	
    	options.comboBox("factory").selectItem(Pattern.compile(".*2\\.0.*"));
    	factoryErrors.requireNotVisible();
    	
		// set the result
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					options.comboBox("factory").targetCastedTo(JComboBox.class).setSelectedItem("java.lang.String");
				}
		});
    	factoryErrors.requireVisible();
    	factoryErrors.requireText(Pattern.compile(".*SAXTransformerFactory.*",Pattern.DOTALL));

    	optionsF.Cancel();
    }
}
