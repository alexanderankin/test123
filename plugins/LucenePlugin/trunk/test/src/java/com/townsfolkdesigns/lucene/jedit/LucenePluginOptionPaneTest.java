/*
 * Copyright (c) 2008 Eric Berry <elberry@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * 
 */
package com.townsfolkdesigns.lucene.jedit;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

import junit.framework.TestCase;

import org.swixml.SwingEngine;

import com.townsfolkdesigns.lucene.jedit.manager.OptionsManager;
import com.townsfolkdesigns.swixml.jedit.SwiXmlPlugin;

/**
 * @author elberry
 * 
 */
public class LucenePluginOptionPaneTest extends TestCase {

	public void testOptionPane() throws Exception {
		// reset the manager to defaults.
		OptionsManager manager = OptionsManager.getInstance();
		manager.clear();
		LucenePluginOptionPane optionPane = new LucenePluginOptionPane();
		optionPane.init();
		OptionsForm optionForm = optionPane.getOptionsForm();
		SwingEngine swingEngine = optionForm.getSwingEngine();
		assertNotNull(optionForm);
		assertNotNull(optionForm.getDirectories());
		// test default options.
		// test the option form.
		assertEquals(5 * 60 * 1000, optionForm.getIndexInterval()); // 5 minutes.
		assertEquals(".*", optionForm.getFileNameRegex());
		assertEquals(true, optionForm.isAutomaticallyIndexProjectFiles());
		// test the GUI components.
		assertEquals(String.valueOf(5 * 60 * 1000), ((JTextComponent) swingEngine.find("indexInterval")).getText()); // 5
		// minutes.
		assertEquals(".*", ((JTextComponent) swingEngine.find("fileNameRegex")).getText());
		assertEquals(true, ((JCheckBox) swingEngine.find("indexProjects")).isSelected());

		// visual check - good when debugging.
		SwiXmlPlugin.showContainer(optionPane);

		// test modify options.
		ArrayList<String> directories = new ArrayList<String>();
		directories.add("1");
		directories.add("2");
		optionForm.setAutomaticallyIndexProjectFiles(false);
		optionForm.setDirectories(directories);
		optionForm.setFileNameRegex("ABC");
		optionForm.setIndexInterval(10 * 60 * 1000); // 10 minutes.

		// test modified options
		assertEquals(10 * 60 * 1000, optionForm.getIndexInterval()); // 5
		// minutes.
		assertEquals("ABC", optionForm.getFileNameRegex());
		assertEquals(false, optionForm.isAutomaticallyIndexProjectFiles());
		// test the GUI components.
		assertEquals(String.valueOf(10 * 60 * 1000), ((JTextComponent) swingEngine.find("indexInterval")).getText()); // 5
		// minutes.
		assertEquals("ABC", ((JTextComponent) swingEngine.find("fileNameRegex")).getText());
		assertEquals(false, ((JCheckBox) swingEngine.find("indexProjects")).isSelected());

		// visual check - good when debugging.
		SwiXmlPlugin.showContainer(optionPane);

		// save modified options.
		optionPane._save();

		// reload from file.
		optionPane = new LucenePluginOptionPane();
		optionPane.init();
		optionForm = optionPane.getOptionsForm();
		swingEngine = optionForm.getSwingEngine();
		// test modified options
		assertEquals(10 * 60 * 1000, optionForm.getIndexInterval()); // 5
		// minutes.
		assertEquals("ABC", optionForm.getFileNameRegex());
		assertEquals(false, optionForm.isAutomaticallyIndexProjectFiles());
		// test the GUI components.
		assertEquals(String.valueOf(10 * 60 * 1000), ((JTextComponent) swingEngine.find("indexInterval")).getText()); // 5
		// minutes.
		assertEquals("ABC", ((JTextComponent) swingEngine.find("fileNameRegex")).getText());
		assertEquals(false, ((JCheckBox) swingEngine.find("indexProjects")).isSelected());

		// visual check - good when debugging.
		SwiXmlPlugin.showContainer(optionPane);
	}

}
