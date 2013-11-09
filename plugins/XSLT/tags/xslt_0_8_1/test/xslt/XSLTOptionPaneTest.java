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

import java.util.regex.Pattern;

import javax.swing.JComboBox;

import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.timing.Pause;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.testframework.JEditRunner;
import org.gjt.sp.jedit.testframework.PluginOptionsFixture;
import org.gjt.sp.jedit.testframework.TestData;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * GUI tests of the XSLT option pane.
 * $Id$
 */
@RunWith(JEditRunner.class)
public class XSLTOptionPaneTest{
	@Rule
	public TestData testData = new TestData();
	
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
