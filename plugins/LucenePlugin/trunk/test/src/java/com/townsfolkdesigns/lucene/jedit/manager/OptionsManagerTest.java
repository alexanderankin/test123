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
package com.townsfolkdesigns.lucene.jedit.manager;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author elberry
 *
 */
public class OptionsManagerTest extends TestCase {
	
	public void testOptionsManager() {
		// reset the manager to defaults.
		OptionsManager manager = OptionsManager.getInstance();
		manager.clear();
		// check default values.
		File settingsDir = manager.getSettingsFile();
		assertNotNull(settingsDir);
		assertTrue(settingsDir.exists());
		assertNotNull(manager.getDirectories());
		assertEquals(5 * 60 * 1000, manager.getIndexInterval()); // 5 minutes.
		assertEquals(".*", manager.getFileNameRegex());
		assertEquals(true, manager.isAutomaticallyIndexProjects());
		// Modify values.
		ArrayList<String> directories = new ArrayList<String>();
		directories.add("1");
		directories.add("2");
		manager.setAutomaticallyIndexProjects(false);
		manager.setDirectories(directories);
		manager.setFileNameRegex("ABC");
		manager.setIndexInterval(10 * 60 * 1000); // 10 minutes.
		manager.save();
		// check modified values.
		settingsDir = manager.getSettingsFile();
		assertNotNull(settingsDir);
		assertTrue(settingsDir.exists());
		assertNotNull(manager.getDirectories());
		assertEquals(2, manager.getDirectories().size());
		assertEquals("1", manager.getDirectories().get(0));
		assertEquals("2", manager.getDirectories().get(1));
		assertEquals(10 * 60 * 1000, manager.getIndexInterval()); // 5 minutes.
		assertEquals("ABC", manager.getFileNameRegex());
		assertEquals(false, manager.isAutomaticallyIndexProjects());
	}
}
